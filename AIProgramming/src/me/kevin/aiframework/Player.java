package me.kevin.aiframework;


import org.json.JSONObject;

public class Player {
	String name;
	int health = 100;
	int score = 0;
	Location location;
	WeaponData PRIMARY_WEAPON;

	WeaponData SECONDARY_WEAPON;
	public Player(JSONObject jsonObject, Map map) {
		updatePlayer(jsonObject, map);
		name = jsonObject.getString("name");
	}
	public void updatePlayer(JSONObject jsonObject, Map map){
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
		if(!jsonObject.isNull("primary-weapon")){
			JSONObject weapon = jsonObject.getJSONObject("primary-weapon");
			String type = weapon.getString("name");
			if(type.equalsIgnoreCase("laser")){
				PRIMARY_WEAPON = new WeaponData(Weapon.Laser, weapon.getInt("level"));
			}else if(type.equalsIgnoreCase("mortar")){
				PRIMARY_WEAPON = new WeaponData(Weapon.Mortar, weapon.getInt("level"));
			}else if(type.equalsIgnoreCase("droid")){
				PRIMARY_WEAPON = new WeaponData(Weapon.Droid, weapon.getInt("level"));
			}
		}
		if(!jsonObject.isNull("secondary-weapon")){
			JSONObject weapon = jsonObject.getJSONObject("secondary-weapon");
			String type = weapon.getString("name");
			if(type.equalsIgnoreCase("laser")){
				SECONDARY_WEAPON = new WeaponData(Weapon.Laser, weapon.getInt("level"));
			}else if(type.equalsIgnoreCase("mortar")){
				SECONDARY_WEAPON = new WeaponData(Weapon.Mortar, weapon.getInt("level"));
			}else if(type.equalsIgnoreCase("droid")){
				SECONDARY_WEAPON = new WeaponData(Weapon.Droid, weapon.getInt("level"));
			}
		}
	}
	public final String getName(){
		return name;
	}
	public final int getHealth(){
		return health;
	}
	public final int getScore(){
		return score;
	}
	public final Location getLocation(){
		return location;
	}
	public final WeaponData getPrimaryWeapon(){
		return PRIMARY_WEAPON;
	}
	public final WeaponData getSecondaryWeapon(){
		return SECONDARY_WEAPON;
	}
}
