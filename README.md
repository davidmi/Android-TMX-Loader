TMX LOADER FOR ANDROID
======================
Second beta release - 0.8.1
Written by David Iserovich

Decision of distribution license pending,
meanwhile you are free to use the code if you
attribute credit.


The map editor [Tiled (link)](http://www.mapeditor.org/) produce files in an XML-based format called `.tmx`.

This library and its classes allow the loading of TMX files into a `TileMapData` object, and allow the creating of an Android Bitmap based on said `TileMapData` object.

What's in the Box
-----

`TMXLoader/src/davidiserovich/TMXLoader/` contains the main source files.
`TMXLoader/assets/` contains an example TMX file and its tileset image.

Both directories are regular Eclipse Android projects, and can be imported.

How it Works
---

The main activity in TMXLoaderExample is called TMXLoaderActivity, and uses the `TileMapData` object and a `Canvas` to create a `Bitmap` containing the arrangment of tiles that the TMX specifies.

### Setting it Up

- To import in Eclipse, choose `File` -> `Import` -> `General` -> `Existing Projects Into Workspace` and select the folder `TMXLoader`.
- To try the example app, do the same for the `TMXLoaderExample` directory
- Right click the `TMXLoader` project you now have, choose Properties, select Android, and make sure "Is Library" is checked
- To use TMXLoader in a project, go to the project's Android properties as above, and add a reference to TMXLoader
- Import `davidiserovich.TMXLoader.*` and you should be ready to go!


Known Restrictions
---

- The only tilemap layout supported is 0 border, 0 margins, 0 spacing.
- Map objects are not yet supported.
- Tile rotations are not yet supported.
- The only supported data format is CSV
(This can be set in Tiled in `Edit` -> `Preferences` -> `Store Tile Data As: CSV`)


CHANGES
-------

###6/20/2012 - 0.8.1
* Separated Bitmap rendering into TMXLoader.java
* Added support for layer selection
* Changed project type to Android Library
* Moved sample code into new, related project


TODO
----
- [ ] Javadoc
- [ ] Compressed file support
- [ ] Support for different tile border, margin, and spacing widths
- [ ] Support for map objects
- [ ] Tile rotation
- [ ] XML based TMX files
				






