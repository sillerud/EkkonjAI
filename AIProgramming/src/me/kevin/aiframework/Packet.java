package me.kevin.aiframework;

import java.io.StringWriter;


import org.json.JSONObject;

public class Packet {
	private PacketType packet;
	StringWriter writer;
	JSONObject jobj;
	
	public Packet(PacketType serverpacket, JSONObject jobj) {
		packet = serverpacket;
		this.jobj = jobj;
	}
	
	public Packet(StringWriter wr){
		this.writer = wr;
	}
	
	public Object getStringWriter() {
		return writer;
	}

	public PacketType getPacketType() {
		return packet;
	}

	public JSONObject getJSONObject() {
		return jobj;
	}
}
