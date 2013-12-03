package com.mkfree.sentinel;

/**
 * redis集群主从信息
 * 
 * @author oyhk
 * 
 *         2013-11-27 下午4:06:14
 */
public class RedisInfo {
	private String host;
	private int port;
	private String name;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}