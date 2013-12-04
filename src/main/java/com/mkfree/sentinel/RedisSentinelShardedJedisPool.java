package com.mkfree.sentinel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.pool.impl.GenericObjectPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 创建redis集群客户端 (这是一个集群并分片的连接池)
 * 
 * @author oyhk
 * 
 *         2013-12-3 下午6:00:25
 */
public class RedisSentinelShardedJedisPool extends RedisSentinel {

	/**
	 * 创建redis集群客户端
	 * 
	 * @param host ip地址
	 * @param port 端口
	 * @param clusterName 群集名
	 */
	public RedisSentinelShardedJedisPool(String host, int port, String clusterName) {
		this.jedisSentinel = new Jedis(host, port);
		this.createShardedJedisPool(clusterName);
		this.checkRedisSentinelServer(this, clusterName);
	}

	/**
	 * 创建多个集群共享的连接池
	 */
	private void createShardedJedisPool(String clusterName) {
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
	@Override
	public void returnBrokenResource(ShardedJedis shardedJedis) {
		shardedJedisPool.returnBrokenResource(shardedJedis);
	}

	@Override
	public void returnBrokenResource(Jedis resource) {
		// TODO Auto-generated method stub
	}

	public ShardedJedisPool getShardedJedisPool() {
		return shardedJedisPool;
	}

	public Jedis getJedisSentinel() {
		return jedisSentinel;
	}

	public void setJedisSentinel(Jedis jedisSentinel) {
		this.jedisSentinel = jedisSentinel;
	}

}
