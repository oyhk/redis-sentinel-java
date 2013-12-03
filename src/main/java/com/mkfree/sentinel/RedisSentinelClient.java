package com.mkfree.sentinel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.pool.impl.GenericObjectPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.mkfree.sentinel.CheckRedisSentinelServer;
import com.mkfree.sentinel.RedisInfo;

/**
 * redis 集群自动切换客户端
 * 
 * @author oyhk
 * 
 *         2013-12-3 上午11:12:08
 */
public class RedisSentinelClient {

	// jedis共享连接池(目前使用静态变量解决)
	private static ShardedJedisPool shardedJedisPool = null;
	// 连接池的配置 (如果不设置，使用默认值)
	private GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
	// jedisSentinel 监控实例
	private Jedis jedisSentinel = null;

	// 常用的静态变量
	private static final String IP = "ip";
	private static final String PORT = "port";
	private static final String NAME = "name";

	/**
	 * 创建redis集群客户端
	 * 
	 * @param host ip地址
	 * @param port 端口
	 */
	public RedisSentinelClient(String host, int port) {
		this.jedisSentinel = new Jedis(host, port);
		this.createShardedJedisPool();
		new Thread(new CheckRedisSentinelServer(this)).start();
	}

	/**
	 * 创建redis集群客户端
	 * 
	 * @param host ip地址
	 * @param port 端口
	 * @param timeout 客户端连接超时时间(单位 秒）
	 */
	public RedisSentinelClient(String host, int port, int timeout) {
		this.jedisSentinel = new Jedis(host, port, timeout);
	}

	/**
	 * 创建多个集群共享的连接池
	 */
	private void createShardedJedisPool() {
		List<RedisInfo> redisInfos = new ArrayList<RedisInfo>();
		List<Map<String, String>> lists = jedisSentinel.sentinelMasters();
		for (int i = 0; i < lists.size(); i++) {
			Map<String, String> map = lists.get(i);
			System.out.println(map);
			RedisInfo redisInfo = new RedisInfo();
			redisInfo.setHost(map.get(IP));
			redisInfo.setPort(Integer.parseInt(map.get(PORT)));
			redisInfo.setName(map.get(NAME));
			redisInfos.add(redisInfo);
		}
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		for (int i = 0; i < redisInfos.size(); i++) {
			RedisInfo redisInfo = redisInfos.get(i);
			shards.add(new JedisShardInfo(redisInfo.getHost(), redisInfo.getPort(), redisInfo.getName()));
		}
		shardedJedisPool = new ShardedJedisPool(poolConfig, shards);
	}

	/**
	 * 返回redis 客户端资源
	 * 
	 * @return
	 */
	public ShardedJedis getResource() {
		return shardedJedisPool.getResource();
	}

	/**
	 * 把资源返回连接池
	 * 
	 * @param shardedJedis
	 */
	public void returnBrokenResource(ShardedJedis shardedJedis) {
		shardedJedisPool.returnBrokenResource(shardedJedis);
	}

	public ShardedJedisPool getShardedJedisPool() {
		return shardedJedisPool;
	}

	public static void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
		RedisSentinelClient.shardedJedisPool = shardedJedisPool;
	}

	public Jedis getJedisSentinel() {
		return jedisSentinel;
	}

	public void setJedisSentinel(Jedis jedisSentinel) {
		this.jedisSentinel = jedisSentinel;
	}

	public GenericObjectPool.Config getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(GenericObjectPool.Config poolConfig) {
		this.poolConfig = poolConfig;
	}

}
