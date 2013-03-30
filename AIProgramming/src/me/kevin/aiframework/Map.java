package me.kevin.aiframework;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Map {
	Tile[][] map;
	public Map(JSONObject jObject) {
		JSONObject jobj = jObject.getJSONObject("map");
		map = new Tile[jobj.getInt("j-length")][jobj.getInt("k-length")];
		System.out.println(jobj.getInt("j-length"));
		JSONArray J = jobj.getJSONArray("data");
		for(int j = 0; j < J.length(); j++){
			JSONArray K = J.getJSONArray(j);
			for(int k = 0; k < K.length(); k++){
				map[j][k] = new Tile(K.getString(k).toCharArray()[0]);
			}
		}
	}
	
	public Tile[][] getMap(){
		return map;
	}

	public Tile getTileAt(int j, int k) {
		if(j < 0 || k < 0)return new Tile('0');
		if(j >= map.length || k >= map[0].length){
			System.out.println("K OR J IS LESS IS HIGHER THEN MAX");
			return new Tile('0');
		}
		return map[j][k];
	}
	
	public int getMaxJ(){
		return map.length;
	}
	
	public int getMaxK(){
		return map[0].length;
	}

	public ArrayList<Location> getAllLocations() {
		ArrayList<Location> all = new ArrayList<Location>();
		for(int j = 0; j < getMaxJ(); j++){
			for(int k = 0; k < getMaxK(); k++){
				all.add(new Location(j, k, this));
			}
		}
		return all;
	}
}
