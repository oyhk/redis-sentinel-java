package com.mkfree.sentinel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.pool.impl.GenericObjectPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * redis 集群自动切换客户端
 * 
 * @author oyhk
 * 
 *         2013-12-3 上午11:12:08
 */
public abstract class RedisSentinel {

	// jedis 分片 共享连接池(目前使用静态变量解决)
	public ShardedJedisPool shardedJedisPool = null;
	// jedis 不分片 共享连接池(目前使用静态变量解决)
	public JedisPool jedisPool = null;
	// 连接池的配置 (如果不设置，使用默认值)
	protected GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
	// jedisSentinel 监控实例
	protected Jedis jedisSentinel = null;

	// 常用的静态变量
	public static final String IP = "ip";
	public static final String PORT = "port";
	public static final String NAME = "name";

	private String nextMaster = null;
	private String upMaster = null;

	/**
	 * 创建多个集群共享的连接池
	 */
	protected void createJedisPool(String... clusterName) {
		List<String> redisInfo = jedisSentinel.sentinelGetMasterAddrByName(clusterName[0]);
		String host = redisInfo.get(0);
		int port = Integer.parseInt(redisInfo.get(1));
		jedisPool = new JedisPool(poolConfig, host, port);
	}

	/**
	 * 检查redis sentinel 主/从redis服务是否正常, 当发生故障时，当redis sentinel监控自动切换 从redis 升级为主redis，需要重新初始化jedispool
	 * 
	 * @param redisSentinel 那种监控的连接池（是否分片)
	 * @param againCheckTime 单位（毫秒/millisecond）
	 * @param clusterName 集群名
	 */
	protected void checkRedisSentinelServer(final RedisSentinel redisSentinel, final int againCheckTime, final String... clusterName) {
		System.out.println("检查redis sentinel 主/从redis服务是否正常,任务开始...");
		Runnable checkRedisSentinelRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						jedisSentinel.ping();
						List<String> masters = jedisSentinel.sentinelGetMasterAddrByName(clusterName[0]);
						String master = masters.toString();
						if (upMaster == null || upMaster.equals("")) {
							upMaster = master;
						}
						if (nextMaster == null || nextMaster.equals("") || !nextMaster.equals(master)) {
							nextMaster = master;
						}
						if (nextMaster.equals(upMaster)) {
							continue;
						}
						System.out.println("主redis发生故障，自动切换...");
						if (redisSentinel instanceof RedisSentinelJedisPool) {
							createJedisPool(clusterName);// 重新初始化jedispool
							System.out.println("重新初始化jedispool...");
						} else if (redisSentinel instanceof RedisSentinelShardedJedisPool) {
							// 暂时不考虑分片
						}
						upMaster = nextMaster;
						Thread.sleep(againCheckTime);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("redis sentinel 监控异常,请检查...");
				}
			}
		};
		new Thread(checkRedisSentinelRunnable).start();
	}

	/**
	 * 获取客户端连接池
	 * 
	 * @return
	 */
	public abstract Object getResource();

	/**
	 * 返回一个客户端连接到连接池
	 * 
	 * @param resource
	 */
	public abstract void returnBrokenResource(ShardedJedis resource);

	/**
	 * 返回一个客户端连接到连接池
	 * 
	 * @param resource
	 */
	public abstract void returnBrokenResource(Jedis resource);

	public GenericObjectPool.Config getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(GenericObjectPool.Config poolConfig) {
		this.poolConfig = poolConfig;
	}

}
