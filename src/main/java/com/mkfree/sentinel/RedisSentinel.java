package com.mkfree.sentinel;

import org.apache.commons.pool.impl.GenericObjectPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
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
	public static ShardedJedisPool shardedJedisPool = null;
	// jedis 不分片 共享连接池(目前使用静态变量解决)
	public static JedisPool jedisPool = null;
	// 连接池的配置 (如果不设置，使用默认值)
	protected GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
	// jedisSentinel 监控实例
	protected Jedis jedisSentinel = null;

	// 常用的静态变量
	public static final String IP = "ip";
	public static final String PORT = "port";
	public static final String NAME = "name";

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

	public Jedis getJedisSentinel() {
		return jedisSentinel;
	}

	public void setJedisSentinel(Jedis jedisSentinel) {
		this.jedisSentinel = jedisSentinel;
	}

}
