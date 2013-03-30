package me.kevin.ai;

import java.util.ArrayList;
import java.util.HashMap;

import me.kevin.aiframework.Direction;
import me.kevin.aiframework.Location;
import me.kevin.aiframework.TileType;

public class ResourceFinder {
	ArrayList<Location> closed = new ArrayList<Location>();
	ArrayList<Location> open = new ArrayList<Location>();
	HashMap<TileType, ArrayList<Location>> resources = new HashMap<TileType, ArrayList<Location>>();
	
	public ResourceFinder(Location start) {
		open.add(start);
		ArrayList<Location> locs = resources.get(start.getTile().getTileType());
		if(locs == null){
			locs = new ArrayList<Location>();
		}
		locs.add(start);
		resources.put(start.getTile().getTileType(), locs);
		while(!open.isEmpty()){
			for(Location loc : new ArrayList<Location>(open)){
				for(Location child : getChilds(loc)){
					open.add(child);
					locs = resources.get(child.getTile().getTileType());
					if(locs == null){
						locs = new ArrayList<Location>();
					}
					locs.add(child);
					resources.put(child.getTile().getTileType(), locs);
				}
				PathFinder.removeFromList(loc, open);
				closed.add(loc);
			}
		}
	}
	
	public ArrayList<Location> findResources(TileType resource){
		return resources.get(resource);
	}
	public ArrayList<Location> getChilds(Location current){
		ArrayList<Location> temp = new ArrayList<Location>();
		for(Direction dir : Direction.values()){
			Location t = current.getNeighbour(dir);
			if(t.getJ() <= t.getMap().getMaxJ() && t.getK() <= t.getMap().getMaxJ() && t.getJ() >= 0 && t.getK() >= 0){
				if(!PathFinder.listContains(t, closed)){
					if(!PathFinder.listContains(t, open)){
						temp.add(t);
					}
				}
			}
		}
		return temp;
	}
}
