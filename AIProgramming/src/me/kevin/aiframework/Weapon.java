package me.kevin.aiframework;

public enum Weapon implements WeaponInfo {
	Laser{
		@Override
		public TileType getResource() {
			return TileType.RUBIDIUM;
		}
	}
	,
	Mortar{
		@Override
		public TileType getResource() {
			return TileType.EXPLODIUM;
		}
	}
	,
	Droid{
		@Override
		public TileType getResource() {
			return TileType.SCRAP;
		}
	}
}
