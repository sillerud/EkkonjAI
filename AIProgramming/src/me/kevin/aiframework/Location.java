package me.kevin.aiframework;

import java.util.ArrayList;

import me.kevin.ai.PathFinder;

public class Location {
	int j;
	int k;
	Map map;
	public Location(int j, int k, Map map) {
		this.j = j;
		this.k = k;
		this.map = map;
	}
	public int getJ(){
		return j;
	}
	public int getK(){
		return k;
	}
	public Tile getTile(){
		return map.getTileAt(j,k);
	}
	public Map getMap(){
		return map;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Location){
			Location loc = (Location)obj;
			return loc.getJ() == getJ() && loc.getK() == getK();
		}

		return super.equals(obj);
	}
	public Location getNeighbour(Direction dir) {
		int j = 0;
		int k = 0;
		if(dir == Direction.UP){
			j = -1;
			k = -1;
		}else if(dir == Direction.RIGHT_UP){
			j = -1;
			k = 0;
		}else if(dir == Direction.LEFT_UP){
			j = 0;
			k = -1;
		}else if(dir == Direction.LEFT_DOWN){
			j = 1;
			k = 0;
		}else if(dir == Direction.RIGHT_DOWN){
			j = 0;
			k = 1;
		}else if(dir == Direction.DOWN){
			j = 1;
			k = 1;
		}
		return new Location(this.j + j, this.k + k, map);
	}
	public boolean isInRangeFor(Weapon weapon, int lvl, Location loc){
		if(weapon == Weapon.Laser){
			for(Direction dir : Direction.values()){
				Location current = this;
				for(int i = 0; i < 2 + lvl; i++){
					current = current.getNeighbour(dir);
					if(loc.getJ() == current.getJ() && loc.getK() == current.getK()) return true;
				}
			}
		}
		if(weapon == Weapon.Mortar){
			for(Location l : getPossibleTargets(Weapon.Mortar, lvl, null)){
				if(loc.getJ() == l.getJ() && loc.getK() == l.getK()) return true;
			}
		}
		if(weapon == Weapon.Droid){
			for(Location l : getPossibleTargets(Weapon.Droid, lvl, new TileType[]{TileType.ROCK, TileType.VOID, TileType.SPAWN})){
				if(loc.getJ() == l.getJ() && loc.getK() == l.getK()) return true;
			}
		}

		return false;
	}
	public Direction getLaserDirection(int lvl, Location target){
		for(Direction dir : Direction.values()){
			Location current = this;
			for(int i = 0; i < 2 + lvl; i++){
				current = current.getNeighbour(dir);
				if(target.getJ() == current.getJ() && target.getK() == current.getK()) return dir;
			}
		}
		return Direction.UP;
	}
	public ArrayList<Location> getPossibleTargets(Weapon w, int lvl, TileType[] notPossible){
		ArrayList<Location> possible = new ArrayList<Location>();
		ArrayList<Location> open = new ArrayList<Location>();
		ArrayList<Location> closed = new ArrayList<Location>();
		open.add(this);
		int wmax = 0;
		if(w == Weapon.Mortar){
			wmax = 1;
		}
		if(w == Weapon.Droid){
			wmax = 2;
		}
		int len = wmax + lvl;
		for(int i = 0; i < len; i ++){
			for(Location tmp : new ArrayList<Location>(open)){
				for(Direction dir : Direction.values()){
					Location loc = tmp.getNeighbour(dir);
					if(notPossible != null){
						for(TileType tt : notPossible){
							if(loc.getTile().getTileType() == tt){
								continue;
							}
						}
					}
					if(!PathFinder.listContains(loc, closed)){
						if(!PathFinder.listContains(loc, open)){
							open.add(loc);
							possible.add(loc);
						}else{
							closed.add(loc);
							PathFinder.removeFromList(loc, open);
						}
					}
				}
			}
		}

		return possible;
	}

	public boolean containsPlayer(ArrayList<Player> pls){
		for(Player pl : pls){
			if(pl.getLocation().getJ() == getJ() && pl.getLocation().getK() == getK())return true;
		}
		return false;
	}
	public Direction getDirection(Location loc){
		for(Direction dir : Direction.values()){
			Location test = this.getNeighbour(dir);
			if(test.equals(loc)){
				return dir;
			}
		}
		return Direction.UP;
	}
	public ArrayList<WeaponData> getPossibleWeapons(Location target, WeaponData[] possible) {
		ArrayList<WeaponData> possibleWeapon = new ArrayList<WeaponData>();
		for(WeaponData w : possible){
			if(this.isInRangeFor(w.getWeapon(), w.getLvl(), target)){
				possibleWeapon.add(w);
			}
		}
		return possibleWeapon;
	}
	@Override
	public String toString() {
		return super.toString() + " J:" + getJ() + " K:" + getK();
	}
	public int getWeaponDamage(WeaponData weapon, ArrayList<Player> ais, int unusedturns, MePlayer mePlayer) {
		int AoE_damage = 0;
		int weapon_damage = 0;
		Weapon w = weapon.getWeapon();
		if(containsPlayer(ais)){
			if(w == Weapon.Droid){
				weapon_damage = 20 + (weapon.getLvl() * 2);
			}else if(w == Weapon.Mortar){
				if(weapon.getLvl() == 3){
					weapon_damage = 25;
				}else{
					weapon_damage = 20;
				}
			}
		}else if(getJ() == mePlayer.getLocation().getJ() && getK() == mePlayer.getLocation().getK()){
			if(w == Weapon.Droid){
				weapon_damage = -(20 + (weapon.getLvl() * 2));
			}else if(w == Weapon.Mortar){
				if(weapon.getLvl() == 3){
					weapon_damage = -25;
				}else{
					weapon_damage = -20;
				}
			}
		}
		for(Direction dir : Direction.values()){
			Location loc = getNeighbour(dir);
			if(loc.containsPlayer(ais)){
				if(w == Weapon.Droid){
					AoE_damage += 10;
				}else if(w == Weapon.Mortar){
					AoE_damage += 18;
				}
			}else if(loc.getJ() == mePlayer.getLocation().getJ() && loc.getK() == mePlayer.getLocation().getK()){
				if(w == Weapon.Droid){
					AoE_damage -= 10;
				}else if(w == Weapon.Mortar){
					AoE_damage -= 18;
				}
			}
		}
		return (int)(weapon_damage + AoE_damage + unusedturns * (0.2 * weapon_damage) + unusedturns * (0.2 * AoE_damage));

	}
}