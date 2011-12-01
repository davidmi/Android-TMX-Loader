package davidiserovich.TMXLoader;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.sax.StartElementListener;
import android.util.Log;

public class TMXHandler extends DefaultHandler {
	/*
	 * This is an SAX2 XML parser that interprets the input TMX
	 * file and creates a TileMapData object.
	 */
	
	// NOTE: Map Object loading is not yet implemented 
	
	// Markers for which tag we're in
	private boolean inMap, inTileSet, inTile, inLayer, inData, inObjectGroup, inProperties;
	
	// ID of the current tile that we're adding properties to.
	// This is actually an OFFSET from firstGID of the tile in
	// the tileset. Beware.
	private String currentTileID;
	
	TileMapData.TileSet currentTileSet;
	TileMapData.Layer currentLayer;
	
	HashMap<String, HashMap<String, String>> currentTileSetProperties;
	HashMap<String, String> currentLayerProperties;
	
	private TileMapData data;
	
	/*
	 * These fields hold the buffer and data to
	 * help decode the long stream of gids in the
	 * data field
	 */
	private char buffer[];
	private int bufferIndex;
	private int currentX;
	private int currentY;
	public int MAX_INT_DECIMAL_LENGTH = 10;
	
	public TMXHandler(){
		super();
		buffer = new char[MAX_INT_DECIMAL_LENGTH];
		bufferIndex = 0;
		currentX = 0;
		currentY = 0;
	}
	

	public TileMapData getData(){ return data; }
	
	@Override
	public void startDocument() throws SAXException{
		data = new TileMapData();
	}
	
	@Override
	public void endDocument() throws SAXException{
		
	}
	
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if(localName.equals("map")) { 
		      inMap = true; 
		      // Check that the orientation is orthogonal
		      if (!(atts.getValue("orientation").equals("orthogonal"))){
		    	  throw new SAXException("Unsupported orientation. Parse Terminated.");
		      }
		      data.orientation = atts.getValue("orientation");
		      Log.d("Checking", data.orientation);
		      data.height = Integer.parseInt(atts.getValue("height"));
		      data.width = Integer.parseInt(atts.getValue("width"));
		      data.tilewidth = Integer.parseInt(atts.getValue("tilewidth"));
		      data.tileheight = Integer.parseInt(atts.getValue("tileheight"));
		      
		 
		      //data.sectionId = atts.getValue("id"); 
		    } else if(localName.equals("tileset")) { 
				inTileSet = true;
				currentTileSet = new  TileMapData.TileSet();
				currentTileSet.firstGID = Integer.parseInt(atts.getValue("firstgid"));
				currentTileSet.tileWidth = Integer.parseInt(atts.getValue("tilewidth"));
				currentTileSet.tileHeight = Integer.parseInt(atts.getValue("tileheight"));
				currentTileSet.name = atts.getValue("name");
				currentTileSetProperties = new HashMap<String, HashMap<String, String>>();
		      
		    } else if (inTileSet && localName.equals("image")){
		    	currentTileSet.ImageFilename = atts.getValue("source");
		    	currentTileSet.imageWidth = Integer.parseInt(atts.getValue("width"));
		    	currentTileSet.imageHeight = Integer.parseInt(atts.getValue("height"));
		    	
		    } else if (inTileSet && localName.equals("tile")){
		    	inTile = true;
		    	currentTileID = atts.getValue("id");
		    	
		    } else if (inTile && localName.equals("properties")){
		    	inProperties = true;
		    	Log.d("Tile ID", currentTileID);
		    	currentTileSetProperties.put(currentTileID, new HashMap<String, String>());
		    	
		    } else if (inLayer && localName.equals("properties")){
		    	inProperties = true;
		    	
		    } else if (inTile && inProperties && localName.equals("property") ){
		    	(currentTileSetProperties.get(currentTileID)).put(atts.getValue("name"), atts.getValue("value"));
		    
		    } else if (inLayer && inProperties && localName.equals("property")){
		    	currentLayerProperties.put(atts.getValue("name"), atts.getValue("value"));
		    } else if (localName.equals("layer")){
		    	inLayer = true;
		    	
		    	currentLayer = new TileMapData.Layer();
		    	currentLayer.name = atts.getValue("name");
		    	currentLayer.width = Integer.parseInt(atts.getValue("width"));
		    	currentLayer.height = Integer.parseInt(atts.getValue("height"));
		    	if (atts.getValue("opacity") != null) currentLayer.opacity = Double.parseDouble(atts.getValue("opacity"));
		    	currentLayer.tiles = new long[currentLayer.height][currentLayer.width];	
		    	
		    	currentLayerProperties = new HashMap<String, String>();
		    	
		    } else if(localName.equals("data")){
		    	/* Data is loaded directly into the int array in characters()
		    	 * We just check if the encoding is supported here. */
		    	inData = true;
		    	String encoding = atts.getValue("encoding");
		    	if (!encoding.equals("csv")){
		    		throw new SAXException("Unsupported encoding. Parse Terminated.");
		    	}
		    }
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException { 
	    
	    if(localName.equals("map")) { 
	      inMap = false;
	      
	    } else if(localName.equals("tileset")) { 
		    inTileSet = false;
		    currentTileSet.properties = currentTileSetProperties;
	    	currentTileSetProperties = null;
		    data.tilesets.add(currentTileSet);
		    currentTileSet = null; // Clear it just in case
	      
	    } else if (localName.equals("tile")){
	    	inTile = false;
	    	currentTileID = "-1"; // -1 won't be produced when searching for properties. Just a safeguard for improperly formatted files.
	      
	    } else if (localName.equals("properties")){
	    	inProperties = false;
	      
	    } else if(localName.equals("layer")){
	    	inLayer = false;
	    	currentLayer.properties = currentLayerProperties;
	    	data.layers.add(currentLayer);
	    	currentLayer = null; // Clear it just in case
	    } else if(localName.equals("data")){
	    	inData = false;
	    	// In case we  missed the last entry (no non-numeral chars before tag end)
	    	if (bufferIndex > 0){
	    		currentLayer.tiles[currentY][currentX] = Long.parseLong(new String(buffer, 0, bufferIndex));
	    	}
	    	// Clear buffer 
    		bufferIndex = 0;
    		currentX = 0;
    		currentY = 0;
	    }
	}
	
	@Override 
	public void characters(char ch[], int start, int length) { 
		/* Java has no unsigned types, so we have to use a long
		 * instead of an int so we can "simulate" an unsigned int.
		 * Disgusting.
		 * Anyway, we're going to add the numbers from the character
		 * stream to a buffer until we hit a comma, at which point we
		 * empty the buffer and convert it to a long, and dump it
		 * into the array. These are raw, so the horizontal and vertical
		 * flip bits may be set - to get the actual GID number, we'll use
		 * TileMapData's getGIDAt(x, y), which will mask it properly.
		 */
		if (inData){	
		    for(int i = 0; i < length; i++){
		    	if (ch[start+i] <= '9' && ch[start+i] >= '0'){
		    		buffer[bufferIndex] = ch[start+i];
		    		//Log.d("Wrote to index", String.valueOf(bufferIndex));
		    		bufferIndex++;
		    		
		    	}else{
		    		// When we hit a comma or any non-number character, empty the buffer and enter the relevant
		    		// GID into the data field
		    		//int what = Integer.parseInt(new String(buffer, 0, bufferIndex));
		    		//Log.d("Number", new String(buffer, 0, bufferIndex));
		    		String nextNumber = new String(buffer, 0, bufferIndex);
		    		if ((nextNumber != null) && ((nextNumber.trim()) != "") && (bufferIndex != 0)){
		    			//Log.d("Checking", nextNumber + " yes");
			    		currentLayer.tiles[currentY][currentX] = Long.parseLong(nextNumber);
			    		bufferIndex = 0;
			    		
			    		// Move to the next tile
			    		if (currentX < (currentLayer.width - 1)){
			    			currentX++;
			    		
			    		}else if (currentY < (currentLayer.width - 1)){
			    			currentX = 0;
			    			currentY++;
			    		}
		    		}
		    		
		    	}
		    	
		    }
		}
	    
	}
	
	
	
}
