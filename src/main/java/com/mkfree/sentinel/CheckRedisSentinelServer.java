package com.mkfree.sentinel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 检查redis sentinel 服务是否正常
 * 
 * @author oyhk
 * 
 *         2013-12-2 上午10:37:27
 */
public class CheckRedisSentinelServer implements Runnable {

	private RedisSentinelClient redisSentinelClient = null;
	private String nextMaster = null;
	private String upMaster = null;

	public CheckRedisSentinelServer(RedisSentinelClient redisSentinelClient) {
		this.redisSentinelClient = redisSentinelClient;
	}

	@Override
	public void run() {
		try {
			while (true) {
				this.redisSentinelClient.getJedisSentinel().ping();
				List<String> masters = this.redisSentinelClient.getJedisSentinel().sentinelGetMasterAddrByName("master1");
				String master = masters.toString();
				System.out.println(master);
				if (upMaster == null || upMaster.equals("")) {
					upMaster = master;
				}
				if (nextMaster == null || nextMaster.equals("") || !nextMaster.equals(master)) {
					nextMaster = master;
				}

				if (!nextMaster.equals(upMaster)) {
					System.out.println("主redis发生故障，自动切换...");
					List<RedisInfo> redisInfos = new ArrayList<RedisInfo>();
					List<Map<String, String>> lists = this.redisSentinelClient.getJedisSentinel().sentinelMasters();
					for (int i = 0; i < lists.size(); i++) {
						Map<String, String> map = lists.get(i);
						RedisInfo redisInfo = new RedisInfo();
						redisInfo.setHost(map.get("ip"));
						redisInfo.setPort(Integer.parseInt(map.get("port")));
						redisInfo.setName(map.get("name"));
						redisInfos.add(redisInfo);
					}
					List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
					for (int i = 0; i < redisInfos.size(); i++) {
						RedisInfo redisInfo = redisInfos.get(i);
						shards.add(new JedisShardInfo(redisInfo.getHost(), redisInfo.getPort(), redisInfo.getName()));
					}
					RedisSentinelClient.setShardedJedisPool(new ShardedJedisPool(this.redisSentinelClient.getPoolConfig(), shards));
					upMaster = nextMaster;
				}
				Thread.sleep(5000);
			}
		} catch (Exception e) {

		}
	}
}