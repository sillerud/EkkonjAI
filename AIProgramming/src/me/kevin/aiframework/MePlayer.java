package me.kevin.aiframework;

import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

public class MePlayer {
	PacketHandler pHandler;
	WeaponData PRIMARY_WEAPON;
	WeaponData SECONDARY_WEAPON;

	String name;
	
	int rubCount = 0;
	int scrCount = 0;
	int expCount = 0;

	public MePlayer(PacketHandler pHandler, String name) {
		this.pHandler = pHandler;
		this.name = name;
	}

	public void setPrimaryWeapon(Weapon w){
		PRIMARY_WEAPON = new WeaponData(w, 1);
	}
	public void setSecondaryWeapon(Weapon w){
		SECONDARY_WEAPON = new WeaponData(w, 1);
	}

	public void setReady(){
		if(PRIMARY_WEAPON == null)throw new NullPointerException("PRIMARY_WEAPON is null");
		if(SECONDARY_WEAPON == null)throw new NullPointerException("SECONDARY_WEAPON is null");
		StringWriter sw = new StringWriter();
		new JSONWriter(sw).object()
		.key("message").value("loadout")
		.key("primary-weapon").value(PRIMARY_WEAPON.getWeapon().name().toLowerCase())
		.key("secondary-weapon").value(SECONDARY_WEAPON.getWeapon().name().toLowerCase())
		.endObject();
		pHandler.sendPacket(new Packet(sw));
	}

	public void shootMortar(String coord){
		StringWriter sw = new StringWriter();
		JSONWriter jw = new JSONWriter(sw);
		jw.object()
		.key("message").value("action")
		.key("type").value("mortar")
		.key("coordinates").value(coord);
		jw.endObject();
		System.out.println(sw.toString());
		pHandler.sendPacket(new Packet(sw));
	}
	public void shootLaser(Direction dir){
		StringWriter sw = new StringWriter();
		JSONWriter jw = new JSONWriter(sw);
		jw.object()
		.key("message").value("action")
		.key("type").value("laser")
		.key("direction").value(dir.toString().toLowerCase().replaceAll("_", "-"));
		jw.endObject();
		System.out.println(sw.toString());
		pHandler.sendPacket(new Packet(sw));
	}

	public void shotDroid(Location[] path, Location currentLocation){
		for(Location loc : path)System.err.println(loc);
		StringWriter sw = new StringWriter();
		Location last = currentLocation;
		JSONArray jarr = new JSONArray();
		for(int i = 0; i < path.length; i++){
			if(last != null){
				Direction dir = last.getDirection(path[i]);
				if(dir != null){
					jarr.put(i, dir.toString().replaceAll("_", "-").toLowerCase());
				}
			}
			last = path[i];
		}
		JSONWriter jw = new JSONWriter(sw);
		jw.object()
			.key("message").value("action")
			.key("type").value("droid")
			.key("sequence").value(jarr)
		.endObject();
		System.out.println(sw.toString());
		pHandler.sendPacket(new Packet(sw));
	}

	public void move(Direction dir) {
		this.location = getLocation().getNeighbour(dir);
		StringWriter sw = new StringWriter();
		JSONWriter jw = new JSONWriter(sw);
		jw.object()
		.key("message").value("action")
		.key("type").value("move")
		.key("direction").value(dir.toString().toLowerCase().replaceAll("_", "-"));
		jw.endObject();
		pHandler.sendPacket(new Packet(sw));
	}
	int health = 100;
	int score = 0;
	Location location;
	public void update(JSONObject jsonObject, Map map) {
		name = jsonObject.getString("name");
		if(!jsonObject.isNull("health"))
			health = jsonObject.getInt("health");
		if(!jsonObject.isNull("score"))
			score = jsonObject.getInt("score");
		if(!jsonObject.isNull("position")){
			String pos = jsonObject.getString("position");
			String[] temp = pos.replaceAll(" ", "").split(",");
			int j = Integer.parseInt(temp[0]);
			int k = Integer.parseInt(temp[1]);
			location = new Location(j, k, map);
		}
	}
	public int getHealth(){
		return health;
	}
	public int getScore(){
		return score;
	}
	public Location getLocation(){
		return location;
	}

	public boolean canMove(Direction dir) {
		if(getLocation() == null)return false;
		if(getLocation().getMap() == null)return false;
		TileType tt = getLocation().getNeighbour(dir).getTile().getTileType();
		return  tt != TileType.VOID && tt != TileType.ROCK;
	}
	public void upgradeWeapon(Weapon w){
		StringWriter sw = new StringWriter();
		JSONWriter jw = new JSONWriter(sw);
		jw.object()
		.key("message").value("action")
		.key("type").value("upgrade")
		.key("weapon").value(w.name().toLowerCase())
		.endObject();
		pHandler.sendPacket(new Packet(sw));
	}
	public void mine(){
		StringWriter sw = new StringWriter();
		JSONWriter jw = new JSONWriter(sw);
		jw.object()
		.key("message").value("action")
		.key("type").value("mine")
		.endObject();
	}

	public WeaponData getPrimaryWeapon() {
		return PRIMARY_WEAPON;
	}

	public WeaponData getSecondaryWeapon() {
		return SECONDARY_WEAPON;
	}

	public int getResourceCount(TileType resource) {
		if(resource == TileType.EXPLODIUM) return expCount;
		else if(resource == TileType.SCRAP) return scrCount;
		else if(resource == TileType.RUBIDIUM) return rubCount;
		else return 0;
	}
}
