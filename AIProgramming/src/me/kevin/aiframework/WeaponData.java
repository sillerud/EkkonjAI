package me.kevin.aiframework;

public class WeaponData {

	Weapon weapon;
	int lvl;
	public WeaponData(Weapon weapon, int lvl) {
		this.weapon = weapon;
		this.lvl = lvl;
	}
	public Weapon getWeapon() {
		return weapon;
	}
	
	public int getLvl(){
		return lvl;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof WeaponData){
			WeaponData wd = (WeaponData)obj;
			if(getLvl() == wd.getLvl() && getWeapon() == wd.getWeapon()){
				return true;
			}else{
				return false;
			}
		}
		return super.equals(obj);
	}
}
