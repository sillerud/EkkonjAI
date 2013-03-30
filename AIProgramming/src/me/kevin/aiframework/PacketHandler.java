package me.kevin.aiframework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;


import org.json.*;

@SuppressWarnings("unused")
public class PacketHandler implements Runnable {
	int rev = 1;
	String name;
	Socket client;
	boolean isRunning = true;
	ArrayList<PacketListener> packetListeners = new ArrayList<PacketListener>();

	public void addListener(PacketListener listener, String name){
		packetListeners.add(listener);
		this.name = name;
	}

	public void shutDown(){
		isRunning = false;
	}

	@Override
	public void run() {
		try{
			while(client == null && isRunning){
				try{
					//client = new Socket("151.216.26.38", 54321);
					client = new Socket("127.0.0.1", 54321);
				}catch(IOException e){
					System.err.println("Unable to connect:");
					e.printStackTrace();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						System.err.println("Failed to sleep");
						e1.printStackTrace();
					}
					System.err.println("Retrying!");
				}
			}
			InputStream in = client.getInputStream();
			System.out.println("Connection successful!");
			//JSONTokener jsonTokener = new JSONTokener(in);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringWriter sw = new StringWriter();
			new JSONWriter(sw).object()
			.key("message").value("connect")
			.key("revision").value(rev)
			.key("name").value(name)
			.endObject();
			sendPacket(new Packet(sw));
			while(isRunning){
				String line;
				while((line = reader.readLine()) != null){
					if(line.toLowerCase().contains("error"))System.err.println(line);
					JSONObject jobj = new JSONObject(line);
					//System.out.println(line);
					if(!jobj.isNull("message")){
						for(PacketListener listener : packetListeners){
							String message = jobj.getString("message");
							if(message.equalsIgnoreCase("gamestate")){
								listener.onPacketRecieve(new Packet(PacketType.GameState, jobj));
							}
							if(message.equalsIgnoreCase("connect")){
								listener.onPacketRecieve(new Packet(PacketType.Connect, jobj));
							}
							if(message.equalsIgnoreCase("action")){
								listener.onPacketRecieve(new Packet(PacketType.Action, jobj));
							}
						}
					}
				}
			}
		}catch(IOException e){

		}
	}
	PrintStream ps = null;
	public void sendPacket(Packet packet){
		try {
			if(ps == null){
				OutputStream out;
				out = client.getOutputStream();
				ps = new PrintStream(out);
			}
			System.err.println("sending packet: " + packet.getStringWriter().toString());
			ps.println(packet.getStringWriter().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
