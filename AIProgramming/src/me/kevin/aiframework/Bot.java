package me.kevin.aiframework;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Bot implements PacketListener {
	private ArrayList<Player> otherais = new ArrayList<Player>();
	private PacketHandler pHandler;
	private Map map;
	private MePlayer mePlayer;
	private boolean hasConnectionMessage = false;
	private String mename;
	
	/**
	 * This framework is written by TheUnnaemdDude/Kevin, it comes with NO warenty or documentation, but i think it is should be easy to use.
	 * It is not finnished and bugs you find you must fix youreself.
	 * The integer in the class Tile is just for my pathfinding.
	 * This is the final and only release of this API.
	 * @param name The name of the bot.
	 */
	public void initialize(String name) {
		pHandler = new PacketHandler();
		pHandler.addListener(this, name);
		Thread pHandlerThread = new Thread(pHandler);
		pHandlerThread.start();
		mePlayer = new MePlayer(pHandler, name);
		mename = name;
	}

	@Override
	public void onPacketRecieve(Packet packet) {
		if(packet.getPacketType() == PacketType.GameState){
			JSONObject jobj = packet.getJSONObject();
			JSONArray jarr = jobj.getJSONArray("players");
			boolean isMyTurn = false;
			this.map = new Map(jobj);
			for(int i = 0; i < jarr.length(); i++){
				if(!jarr.getJSONObject(i).isNull("name")){
					String addingName = jarr.getJSONObject(i).getString("name");
					if(!mename.equalsIgnoreCase(addingName)){
						boolean isAdded = false;
						for(Player pl : otherais){
							if(pl.getName().equalsIgnoreCase(addingName)){
								pl.updatePlayer(jarr.getJSONObject(i), map);
								System.out.println("Updated info about " + pl.getName() + " he has " + pl.getHealth() + " health and " + pl.getScore() + " score");
								isAdded = true;
								break;
							}
						}
						if(!isAdded){
							Player player = new Player(jarr.getJSONObject(i), map);
							System.out.println("Got info about " + player.getName() + " he has " + player.getHealth() + " health and " + player.getScore() + " score");
							otherais.add(player);
						}
					}else{
						mePlayer.update(jarr.getJSONObject(i), map);
						if(i == 0){
							if(jobj.getInt("turn") > 0){
								isMyTurn = true;
							}
						}
					}
				}
			}
			if(hasConnectionMessage){
				doPreGame();
				Weapon[] weapons = getWeapons();
				getMe().setPrimaryWeapon(weapons[0]);
				getMe().setSecondaryWeapon(weapons[1]);
				getMe().setReady();
				hasConnectionMessage = false;
			}
			if(isMyTurn){
				doAction();
			}
			isMyTurn = false;
		}
		if(packet.getPacketType() == PacketType.Connect){
			hasConnectionMessage = true;
		}
	}
	
	public ArrayList<Player> getOtherAis(){
		return otherais;
	}
	
	public MePlayer getMe(){
		return mePlayer;
	}
	
	public Map getMap(){
		return map;
	}
	
	
	public abstract Weapon[] getWeapons();
	public abstract void doAction();
	public abstract void doPreGame();
}
