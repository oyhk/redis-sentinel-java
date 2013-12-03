package com.mkfree.sentinel;

import redis.clients.jedis.ShardedJedis;

public class RedisSentinelClientTest {

	public static void main(String[] args) throws InterruptedException {
		String host = "192.168.9.17";
		int port = 26379;
		RedisSentinelClient client = new RedisSentinelClient(host, port);
		testSet(client);
	}

	/**
	 * 模拟多线程操作
	 */
	public static void testSet(final RedisSentinelClient client) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int count = 0;
					while (true) {
						ShardedJedis shardedJedis = client.getResource();
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
						testSet(client);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}).start();
	}
}
