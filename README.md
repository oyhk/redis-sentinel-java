redis-sentinel-java 
========

本项目主要是做一个redis-sentinel java客户端集群的自动切换方案，主要为了学习而玩。我相信有非常多的bug，请原谅。

- 简单使用
- 环境要求
	- 一定要搭好一个redis sentinel的集群redis环境才行
	
```
git clone git@github.com:oyhk/redis-sentinel-java.git
```

 ```
	public static void main(String[] args) throws InterruptedException {
		String host = "192.168.9.17";
		int port = 26379;
		String clusterName = "master1";
		RedisSentinel redisSentinelJedisPool = new RedisSentinelJedisPool(host, port, clusterName);
		
		Jedis jedis = null;
		try {
			jedis = (Jedis) client.getResource();
			jedis.set("key", "value");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.returnBrokenResource(jedis);
		}
	}
	
 ```



自动切换日志信息

```
{info-refresh=9917, port=6379, quorum=1, num-slaves=1, flags=master, last-ok-ping-reply=392, pending-commands=0, num-other-sentinels=0, name=master1, last-ping-reply=392, runid=0dec551778d0ce9857478e82521a3b7ed2984070, ip=192.168.9.19}
[192.168.9.19, 6379]
set1
set2
set3
set4
[192.168.9.19, 6379]
set5
redis.clients.jedis.exceptions.JedisConnectionException: java.net.ConnectException: 拒绝连接
	at redis.clients.jedis.Connection.connect(Connection.java:137)
	at redis.clients.jedis.BinaryClient.connect(BinaryClient.java:65)
	at redis.clients.jedis.Connection.sendCommand(Connection.java:82)
	at redis.clients.jedis.BinaryClient.set(BinaryClient.java:82)
	at redis.clients.jedis.Client.set(Client.java:23)
	at redis.clients.jedis.Jedis.set(Jedis.java:43)
	at redis.clients.jedis.ShardedJedis.set(ShardedJedis.java:31)
	at com.mkfree.sentinel.RedisSentinelClientTest$1.run(RedisSentinelClientTest.java:26)
	at java.lang.Thread.run(Thread.java:722)
Caused by: java.net.ConnectException: 拒绝连接
	at java.net.PlainSocketImpl.socketConnect(Native Method)
	at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:339)
	at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:200)
	at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:182)
	at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:391)
	at java.net.Socket.connect(Socket.java:579)
	at redis.clients.jedis.Connection.connect(Connection.java:132)
	... 8 more
[192.168.9.19, 6379]
[192.168.9.19, 6379]
[192.168.9.19, 6379]
[192.168.9.18, 6379]
主redis发生故障，自动切换...
[192.168.9.18, 6379]
[192.168.9.18, 6379]
set1
set2
set3
[192.168.9.18, 6379]
set4
```