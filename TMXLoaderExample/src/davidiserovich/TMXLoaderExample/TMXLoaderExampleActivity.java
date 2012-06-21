package davidiserovich.TMXLoaderExample;

import android.app.Activity;
import android.os.Bundle;

import android.graphics.Bitmap;

import android.widget.ImageView;
import android.widget.Toast;

import davidiserovich.TMXLoader.*;

public class TMXLoaderExampleActivity extends Activity {


	ImageView mapView;
	String FILENAME = "ExampleMap.tmx";


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Start the parser, get back TMX data object
        TileMapData t = TMXLoader.readTMX(FILENAME, this);
        
        mapView = (ImageView)findViewById(R.id.MapImage);

        // Create a Bitmap from the tilemap data
        Bitmap mapImage = TMXLoader.createBitmap(t, this, 0, t.layers.size());
        
        // Set the imageview to show the map, if we have one
        if (mapImage != null){
        	mapView.setImageBitmap(mapImage);
        }
        // Map loading problem, inform the user.
        else{
	         Toast errorMessage = Toast.makeText(getApplicationContext(), "Map could not be loaded", Toast.LENGTH_LONG);
	         errorMessage.show();
        }

    }
}