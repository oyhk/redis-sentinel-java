package com.mkfree.sentinel;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

public class RedisSentinelClientTest {

	public static void main(String[] args) throws InterruptedException {
		String host = "192.168.9.17";
		int port = 26379;
		String clusterName = "master1";
		// RedisSentinel redisSentinelShardedJedisPool = new RedisSentinelShardedJedisPool(host, port, clusterName);
		// testSet(redisSentinelShardedJedisPool);
		RedisSentinel redisSentinelJedisPool = new RedisSentinelJedisPool(host, port, clusterName);
		setRedisSentinelJedisPool(redisSentinelJedisPool);
	}

	/**
	 * 模拟多线程操作
	 */
	public static void setRedisSentinelShardedJedisPool(final RedisSentinel client) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int count = 0;
					while (true) {
						ShardedJedis shardedJedis = (ShardedJedis) client.getResource();
						Thread.sleep(1000);
						shardedJedis.set("b" + count, "b" + count);
						client.returnBrokenResource(shardedJedis);
						count++;
						System.out.println("set" + count);
					}
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * 模拟多线程操作
	 */
	public static void setRedisSentinelJedisPool(final RedisSentinel client) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				while (true) {
					Jedis jedis = null;
					try {
						jedis = (Jedis) client.getResource();
						Thread.sleep(1000);
						jedis.set("b" + count, "b" + count);
						count++;
						System.out.println("set" + count);
					} catch (Exception e) {
						e.printStackTrace();
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					} finally {
						client.returnBrokenResource(jedis);
					}
				}
			}
		}).start();
	}
}
