package com.mkfree.sentinel;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;

public class RedisSentinelJedisPool extends RedisSentinel {

	/**
	 * 创建redis集群客户端
	 * 
	 * @param host ip地址
	 * @param port 端口
	 * @param clusterName 群集名
	 */
	public RedisSentinelJedisPool(String host, int port, String... clusterName) {
		this.jedisSentinel = new Jedis(host, port);
		this.createJedisPool(clusterName);
		new Thread(new CheckRedisSentinelServer(this)).start();
	}

	/**
	 * 创建多个集群共享的连接池
	 */
	private void createJedisPool(String... clusterName) {
		List<String> redisInfo = jedisSentinel.sentinelGetMasterAddrByName(clusterName[0]);
		System.out.println(redisInfo);
		String host = redisInfo.get(0);
		int port = Integer.parseInt(redisInfo.get(1));
		jedisPool = new JedisPool(poolConfig, host, port);
	}

	@Override
	public Object getResource() {
		return jedisPool.getResource();
	}

	@Override
	public void returnBrokenResource(ShardedJedis resource) {

	}

	@Override
	public void returnBrokenResource(Jedis resource) {
		jedisPool.returnBrokenResource(resource);
	}

}
