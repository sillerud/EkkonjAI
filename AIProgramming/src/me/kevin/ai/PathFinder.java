package me.kevin.ai;

import java.util.ArrayList;
import java.util.List;

import me.kevin.aiframework.Direction;
import me.kevin.aiframework.Location;
import me.kevin.aiframework.Map;

public class PathFinder {
	Map map;
	ArrayList<Location> open = new ArrayList<Location>();
	ArrayList<Location> closed = new ArrayList<Location>();
	Location startTile;
	Location endTile;
	Location currentLoc;
	Location start;
	
	public void initialize(Location start){
		this.start = start;
		this.map = start.getMap();
		resetMap(map);
		open.add(start);
		startTile = start;
		while(!open.isEmpty()){
			currentLoc = getLocWithLowestCostTotal(open);


			removeFromList(currentLoc, open);
			closed.add(currentLoc);

			ArrayList<Location> childTiles = getChildTiles(currentLoc);

			for(Location child : childTiles){
				if(!listContains(child, open)){
					if(!listContains(child, closed)){
						open.add(child);

						child.getTile().cost = currentLoc.getTile().cost + 1;
					}
				}
			}
		}
	}

	public void findPath(Location target){
		endTile = target; 
	}

	private ArrayList<Location> getChildTiles(Location currentLoc) {
		ArrayList<Location> childTiles = new ArrayList<Location>();
		Location childTile;

		for(Direction dir : Direction.values()){
			childTile = currentLoc.getNeighbour(dir);
			if(childTile.getJ() >= 0 && childTile.getK() >= 0 && childTile.getJ() <= map.getMaxJ()
					&& childTile.getK() < map.getMaxK() && childTile.getTile().isWalkable()){
				childTiles.add(childTile);
			}
		}
		return childTiles;
	}

	private Location getLocWithLowestCostTotal(ArrayList<Location> op) {
		Location tlt = new Location(-1, -1, map);

		int lowestTotal = Integer.MAX_VALUE;

		for(Location loc : op){
			if(loc.getTile().cost <= lowestTotal){
				lowestTotal = loc.getTile().cost;
				tlt = loc;
			}
		}
		return tlt;
	}
	public static void removeFromList(Location loc, List<Location> list){
		ArrayList<Location> looptrough = new ArrayList<Location>(list);
		for(Location location : looptrough){
			if(loc.getJ() == location.getJ() && loc.getK() == location.getK()){
				list.remove(location);
			}
		}
	}
	public static boolean listContains(Location loc, List<Location> list){
		ArrayList<Location> looptrough = new ArrayList<Location>(list);
		for(Location location : looptrough){
			if(loc.getJ() == location.getJ() && loc.getK() == location.getK()){
				return true;
			}
		}
		return false;
	}
	public static void resetMap(Map map){
		for(int j = 0; j < map.getMaxJ(); j++){
			for(int k = 0; k < map.getMaxK(); k++){
				map.getTileAt(j, k).cost = 0;
			}
		}
	}

	public ArrayList<Location> getPath(boolean addEndtile){
		ArrayList<Location> path = new ArrayList<Location>();
		ArrayList<Location> inverted = getInvertedPath(addEndtile);
		if(inverted == null)return null;
		if(inverted.isEmpty())return null;
		for(int i = inverted.size() - 1; i >= 0; i--){
			path.add(inverted.get(i));
		}
		return path;
	}
	final static int maxLoop = 10000;
	public ArrayList<Location> getInvertedPath(boolean addEndtile){
		int currentLoop = 0;
		boolean startFound = false;
		Location currentLoc = endTile;
		ArrayList<Location> path = new ArrayList<Location>();
		if(addEndtile){
			path.add(currentLoc);
		}
		while(!startFound){
			ArrayList<Location> childs = getChildTiles(currentLoc);
			Location bestPath = currentLoc;
			for(Location loc : childs){
				if(loc.equals(startTile)){
					startFound = true;
					System.out.println(path);
					return path;
				}
				if(listContains(loc, closed) || listContains(loc, open)){
					if(loc.getTile().cost < currentLoc.getTile().cost && loc.getTile().cost > 0){
						currentLoc = loc;
						bestPath = loc;
					}
				}
			}
			if(!path.contains(bestPath))
				path.add(bestPath);
			if(currentLoop > maxLoop) {
				System.err.println("Looped to long, exiting");
				break;
			}

		}
		return null;
	}
}
