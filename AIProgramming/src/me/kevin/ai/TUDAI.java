package me.kevin.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.kevin.aiframework.Bot;
import me.kevin.aiframework.Direction;
import me.kevin.aiframework.Location;
import me.kevin.aiframework.Player;
import me.kevin.aiframework.TileType;
import me.kevin.aiframework.Weapon;
import me.kevin.aiframework.WeaponData;

public class TUDAI extends Bot {

	public static final String name = "TUDAI" + new Random().nextInt(200);

	int EXPLODIUMPERCENT = 0;
	int RUBIDIUMPERCENT = 0;
	int SCRAPPERCENT = 0;

	public static void main(String[] args){
		new TUDAI().initialize(name);
	}

	@Override
	public Weapon[] getWeapons() {
		int laserChanse = 100;
		int droidChanse = 100;
		int mortarChanse = 100;

		Weapon[] weapons = new Weapon[2];
		System.out.println("Starting weapon calculation");
		ArrayList<Location> holeMap = getMap().getAllLocations();

		HashMap<TileType, Integer> tileCount = new HashMap<TileType, Integer>();
		for(TileType tt : TileType.values()){
			tileCount.put(tt, 0);
		}
		for(Location loc : holeMap){
			Integer value = tileCount.get(loc.getTile().getTileType());
			if(value == null){
				value = 0;
			}
			tileCount.put(loc.getTile().getTileType(), value + 1);
		}

		int tiles = holeMap.size();
		System.out.println("Number of tiles " + tiles);

		for(TileType tt : tileCount.keySet()){
			System.out.println("Number of " + tt.name() + " " + tileCount.get(tt));
			System.out.println("Percentage of " + tt.name() + " " + (int)(((double)tileCount.get(tt) / (double)tiles) * 100.0));
		}

		int voidPercentage = (int)(((double)tileCount.get(TileType.VOID) / (double)tiles) * 100.0);
		int scrapPercentage = (int)(((double)tileCount.get(TileType.SCRAP) / (double)tiles) * 100.0);
		int rockPercentage = (int)(((double)tileCount.get(TileType.ROCK) / (double)tiles) * 100.0);
		int rubidiumPercentage = (int)(((double)tileCount.get(TileType.RUBIDIUM) / (double)tiles) * 100.0);
		int explodiumPercentage = (int)(((double)tileCount.get(TileType.EXPLODIUM) / (double)tiles) * 100.0);

		droidChanse -= (double)voidPercentage / 1.3;
		droidChanse -= (double)rockPercentage / 1.3;
		droidChanse += ((int)((double)scrapPercentage / 2.0));

		mortarChanse += ((int)((double)explodiumPercentage / 2.0));
		mortarChanse -= (double)voidPercentage / 4.0;

		laserChanse += ((int)((double)rubidiumPercentage / 2.0));

		Weapon worstChoise = Weapon.Laser;
		int worstChoiseChanse = Integer.MAX_VALUE;

		for(Weapon w : Weapon.values()){
			if(w == Weapon.Droid){
				if(droidChanse < worstChoiseChanse){
					worstChoiseChanse = droidChanse;
					worstChoise = w;
				}
			}else if(w == Weapon.Laser){
				if(laserChanse < worstChoiseChanse){
					worstChoiseChanse = laserChanse;
					worstChoise = w;
				}
			}else{
				if(mortarChanse < worstChoiseChanse){
					worstChoiseChanse = mortarChanse;
					worstChoise = w;
				} 
			}
		}
		int index = 0;

		for(Weapon w : Weapon.values()){
			if(w != worstChoise){
				weapons[index] = w;
				index ++;
			}
		}
		weapons[0] = Weapon.Laser;
		return weapons;
	}

	@Override
	public void doPreGame() {


	}

	int globalTurns = 0;
	//TODO Fix laser
	@Override
	public void doAction() {
		int turnsDone = 0;
		ArrayList<WeaponData> possibleWeaponData = new ArrayList<WeaponData>();
		if(getOtherAis().size() > 0){
			PathFinder pf = new PathFinder();
			pf.initialize(getMe().getLocation());
			pf.findPath(getTarget().getLocation());
			List<Location> path = pf.getPath(false);
			if(path == null) path = new ArrayList<Location>();
			Location currentLocation = getMe().getLocation();
			for(int i = 0; i < ((path.size() > 3) ? 3 : path.size()); i++){
				Location nextLocation = path.get(i);
				Direction dir = currentLocation.getDirection(nextLocation);
				if(dir != null){
					possibleWeaponData = currentLocation.getPossibleWeapons(getOtherAis().get(0).getLocation(),
							new WeaponData[]{getMe().getPrimaryWeapon(), getMe().getSecondaryWeapon()});
					if(!possibleWeaponData.isEmpty())break;

					getMe().move(dir);
					currentLocation = nextLocation;
					turnsDone += 1;
				}else{
					System.out.println("Err nauw! Cant find the next direction!!");
				}
			}
			int failsafe = 0;
			if(turnsDone < 3){
				while(turnsDone < 3){

					if(doUpgrade(getMe().getPrimaryWeapon()) || doUpgrade(getMe().getSecondaryWeapon())){
						turnsDone ++;
						continue;
					}

					Object[] w = getBestTarget(possibleWeaponData, 3 - turnsDone, currentLocation);
					WeaponData weapon = (WeaponData)w[0];

					if(failsafe > 40){
						break;
					}
					if(weapon == null){
						System.err.println("Well, obviusly i cant find a weapon to use, going after resources!");
						ResourceFinder rf = new ResourceFinder(getMe().getLocation());
						Location prim = rf.findResources(getMe().getPrimaryWeapon().getWeapon().getResource()).get(0);
						Location sec = rf.findResources(getMe().getSecondaryWeapon().getWeapon().getResource()).get(0);
						PathFinder pfinder = new PathFinder();
						pfinder.initialize(getMe().getLocation());
						if(prim.getTile().cost > sec.getTile().cost){
							pfinder.findPath(prim);
						}else{
							pfinder.findPath(sec);
						}
						ArrayList<Location> rpath = new ArrayList<Location>();
						rpath = pfinder.getPath(true);
						if(rpath != null && !rpath.isEmpty()){
							getMe().move(getMe().getLocation().getDirection(rpath.get(0)));
						}
						if(rpath != null && rpath.isEmpty() ){
							getMe().mine();
						}
						if(rpath != null){
							turnsDone ++;
							continue;
						}
					}
					failsafe ++;
					boolean gatherResource = false;
					/*for(Player pl : getOtherAis()){
						if(pl.getScore() != 0 || globalTurns > 15){
							gatherResource = false;
							break;
						}
					}*/
					if(gatherResource){
						ResourceFinder rf = new ResourceFinder(getMe().getLocation());
						Location prim = rf.findResources(getMe().getPrimaryWeapon().getWeapon().getResource()).get(0);
						Location sec = rf.findResources(getMe().getSecondaryWeapon().getWeapon().getResource()).get(0);
						PathFinder pfinder = new PathFinder();
						pfinder.initialize(getMe().getLocation());
						if(prim.getTile().cost > sec.getTile().cost){
							pfinder.findPath(prim);
						}else{
							pfinder.findPath(sec);
						}
						ArrayList<Location> rpath = new ArrayList<Location>();
						if(rpath != null && !rpath.isEmpty()){
							getMe().move(getMe().getLocation().getDirection(rpath.get(0)));
						}
						if(rpath.isEmpty()){
							getMe().mine();
						}
						turnsDone ++;
						continue;
					}else if(weapon == null || weapon.getWeapon() == null){
						System.err.println("FAILSAFE!");
					}else if(weapon.getWeapon() == Weapon.Laser){
						if(w[1] instanceof Location){
							Location loc = (Location)w[1];
							Direction dir = getMe().getLocation().getDirection(loc);
							getMe().shootLaser(dir);
							turnsDone ++;
							continue;
						}
					}else if(weapon.getWeapon() == Weapon.Mortar){
						if(w[1] instanceof Location){
							Location loc = (Location)w[1];
							getMe().shootMortar((loc.getJ() - currentLocation.getJ()) + ","
									+ (loc.getK() - currentLocation.getK()));
							turnsDone ++;
							continue;
						}
					}else if(weapon.getWeapon() == Weapon.Droid){
						if(w[1] instanceof Location){
							Location loc = (Location)w[1];
							PathFinder pathf = new PathFinder();
							pathf.initialize(currentLocation);
							pathf.findPath(loc);
							Location[] locpath = new Location[3 + weapon.getLvl()];
							ArrayList<Location> p = pathf.getPath(true);
							for(int i = 0; i < 2 + weapon.getLvl(); i++){
								if(i >= p.size()){
									break;
								}
								locpath[i] = p.get(i);
							}
							getMe().shotDroid(locpath, currentLocation);
							turnsDone ++;
							continue;
						}
					}else{
						turnsDone ++;
					}
				}

			}
		}
		globalTurns ++;
	}


	private boolean doUpgrade(WeaponData w) {
		if(getMe().getResourceCount(w.getWeapon().getResource()) > w.getLvl() + 3){
			getMe().upgradeWeapon(w.getWeapon());
			return true;
		}

		return false;
	}

	//TODO Add points for killing
	/**
	 * I really hate to use objects, but i did not care to make a new class just for this ;)
	 * @param possibleWeaponData 
	 * @return an object array where 0 is the weapon and 1  is the target
	 */
	public Object[] getBestTarget(ArrayList<WeaponData> possibleWeaponData, int unusedturns, Location curr) {
		Object[] info = new Object[2];
		Object[] damage = new Object[2];
		damage[0] = null;
		damage[1] = 0;
		for(WeaponData weapon : possibleWeaponData){
			TileType[] notPossible = null;
			if(weapon.getWeapon() == Weapon.Droid){
				notPossible = new TileType[]{TileType.VOID, TileType.ROCK, TileType.SPAWN};
			}
			if(weapon.getWeapon() == Weapon.Laser){
				int currdamage = 0;
				for(Direction dir : Direction.values()){
					Location current = curr;
					for(int i = 0; i <= 2 + weapon.getLvl(); i++){
						current = current.getNeighbour(dir);
						if(current.containsPlayer(getOtherAis())){
							if(weapon.getLvl() ==  1){
								currdamage += 16;
							}else if(weapon.getLvl() == 2){
								currdamage += 18;
							}else{
								currdamage += 22;
							}
						}
						if(currdamage > objtoint(damage[1])){
							info[0] = weapon;
							info[1] = getMe().getLocation().getNeighbour(dir);
							damage[0] = weapon;
							damage[1] = currdamage;
						}
					}
				}
			}else{
				for(Location loc : curr.getPossibleTargets(weapon.getWeapon(), weapon.getLvl(), notPossible)){

					Location current = loc;
					int currdamage = current.getWeaponDamage(weapon, getOtherAis(), unusedturns, getMe());
					if(currdamage > objtoint(damage[1])){
						info[0] = weapon;
						info[1] = current;
						damage[0] = weapon;
						damage[1] = currdamage;
					}
					for(Direction dir : Direction.values()){
						current = loc.getNeighbour(dir);
						currdamage = current.getWeaponDamage(weapon, getOtherAis(), unusedturns, getMe());
						if(currdamage > objtoint(damage[1])){
							info[0] = weapon;
							info[1] = current;
							damage[0] = weapon;
							damage[1] = currdamage;
						}
					}
				}
			}
		}

		return info;
	}
	public int objtoint(Object obj){
		if(obj instanceof Integer){
			return (Integer)obj;
		}
		return 0;
	}
	public Player getTarget() {
		//TODO Kalkulere "beste" fiende med tanke på points, deaths, path kostnad og 
		



		return getOtherAis().get(0);
	}
}
