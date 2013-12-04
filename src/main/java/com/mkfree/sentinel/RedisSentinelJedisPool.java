package com.mkfree.sentinel;

import redis.clients.jedis.Jedis;
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
		this.checkRedisSentinelServer(this, 5000, clusterName);
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
