package davidiserovich.TMXLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class TileMapData {
	/*
	 * A data structure holding the information from a TMX tilemap.
	 * This can be used to reconstruct a graphical tilemap of theoretically
	 * arbitrarily large size onscreen from repeated images of a tileset.
	 */
	
	/* Constants */
	long GID_MASK = 0x3fffffff;
	
	static class TileSet{
		public String name;
		
		public int firstGID;
		public int tileWidth, tileHeight;
		
		public String ImageFilename;
		public int imageWidth, imageHeight;
		
		// Design decision note:
		// Since there can be a theoretically unlimited
		// number of tile properties, it is best to describe
		// them with a hashmap, but this will not be very efficient
		// for massive lookups. Some buffer should be implemented by the user
		// (Example: tile 39 is a water tile, and swimming matters. Check against
		// 39.)
		// Of course, if the usage is sufficiently complex that we'll be looping through
		// large arrays to find the property even if we buffer it, just getting it from 
		// the hash map may be better.
		// So I'm doing it this way until I think of a better one:
		
		// HashMap<GID, Properties<String, String>>
		public HashMap<String, HashMap<String, String>> properties;
		//
		
	}
	
	static class Layer{
		/*
		 * Holds the tile placement data for a layer of the tilemap
		 */
		
		public String name;
		
		// Design decision note:
		// By tmx design, the actual tilemap is just a set of gids.
		// The array can be initialized to new int[width][height]
		// I won't store the tiles as objects for the sake of memory
		// efficiency.
		
		public long[][] tiles;
		
		public int width, height;
		public double opacity;
		
		HashMap<String, String> properties;
		
		
	}
	
	public class TMXObject{
		/*
		 * Holds an "object" from the tilemap.
		 * These are for pixel-precision placed
		 * game objects with unique data, such as spawnpoints
		 * and items, etc.
		 */
		
		
		String name;
		int x, y;
		int width, height;
		int gid;
	}
	
	public long getGIDAt(int x, int y){
		/*
		 * Gets the GID of the tile at the position on
		 * the lowest layer. Overloaded for shorthand:
		 * many maps have only one layer.
		 */
		return ((layers.get(0).tiles[y][x]) & GID_MASK);
	}
	
	public long getGIDAt(int x, int y, int layerIndex){
		/*
		 * Gets the GID from a specified position and layer
		 */
		
		return ((layers.get(layerIndex).tiles[y][x]) & GID_MASK);
	}
	
	public Long getLocalID(long GID){
		/* 
		 * Gets the offset from this GID's tileset's 
		 * first GID, so we can chop it up from its tileset image properly or
		 * get its properties
		 */
		long currentFirstGID;
		for (int i = tilesets.size() - 1; i >= 0; i--){
			 currentFirstGID = tilesets.get(i).firstGID;
			 if (currentFirstGID <= GID) return new Long(GID - currentFirstGID);
		}
		// The GID is not valid, or there was an ordering problem 
		// in the TMX file and the tilesets GIDs are not ordered from
		// lowest to highest, or that space simply has not been defined (0)
		return null;
	}
	
	public Integer getTileSetIndex(long GID){
		/* 
		 * Same as above, instead return the tileset's index
		 * in the tilesets ArrayList.
		 */
		long currentFirstGID;
		for (int i = tilesets.size() - 1; i >= 0; i--){
			 currentFirstGID = tilesets.get(i).firstGID;
			 if (currentFirstGID <= GID) return new Integer(i);
		}
		return null;
		
	}
	
	public Integer getTileSetIndex(String name){
		/* 
		 * Get the index of a tileset by its name.
		 */
		for (int i = tilesets.size() - 1; i >= 0; i--){
			 if (name.equals(tilesets.get(i).name)) return new Integer(i);
		}
		return null;
		
	}
	
	public Integer getLayerIndex(String name){
		/*
		 * Get's the index of a layer by its name.
		 */
		
		for (int i = 0; i < layers.size(); i++){
			if (layers.get(i).name.equals(name)) return new Integer(i);
		}
		return null;
	}
	
	
	
	/* Map data fields */
	
	public String name;
	public int	height, width;
	public int	tilewidth, tileheight;
	public String orientation; // Must be "orthogonal", for now.
	
	public ArrayList<TileSet> tilesets; // <tileset.name, tileset>
	public HashMap<String, TMXObject> objects; // <object.name, object>
	public ArrayList<Layer> layers;
	
	public TileMapData(){
		tilesets = new ArrayList<TileSet>();
		objects = new HashMap<String, TMXObject>();
		layers = new ArrayList<Layer>();
	}
}

