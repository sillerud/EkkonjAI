package me.kevin.aiframework;


public class Tile {
	char ch;
	Map map;
	
	//Only for my path finding
	public int cost = 0;
	
	public Tile(char c) {
		ch = c;
	}
	
	public TileType getTileType(){
		if(ch == 'v' || ch == 'V'){
			return TileType.VOID;
		}else if(ch == 'g' || ch == 'G'){
			return TileType.GRASS;
		}else if(ch == 's' || ch == 'S'){
			return TileType.SPAWN;
		}else if(ch == 'e' || ch == 'E'){
			return TileType.EXPLODIUM;
		}else if(ch == 'r' || ch == 'R'){
			return TileType.RUBIDIUM;
		}else if(ch == 'c' || ch == 'R'){
			return TileType.SCRAP;
		}else if(ch == 'o' || ch == 'O'){
			return TileType.ROCK;
		}else{
			return TileType.VOID;
		}
	}
	public boolean isWalkable(){
		return getTileType() != TileType.VOID && getTileType() != TileType.ROCK;// && getTileType() != TileType.SPAWN;
	}
}
