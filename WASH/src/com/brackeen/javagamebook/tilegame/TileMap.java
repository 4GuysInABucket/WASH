package com.brackeen.javagamebook.tilegame;

import java.awt.Image;
import java.util.LinkedList;
import java.util.Iterator;

import com.brackeen.javagamebook.graphics.Sprite;

/**
 * The TileMap class contains the data for a tile-based
 * map, including Sprites. Each tile is a reference to an
 * Image. Of course, Images are used multiple times in the tile
 * map.
*/
public class TileMap {

    private Image[][] tiles;
    private LinkedList sprites;
    private Sprite player;

    /**
     * Creates a new TileMap with the specified width and
     * height (in number of tiles) of the map.
     * @param width
     * @param height 
     */
    
    public TileMap(int width, int height) {
        tiles = new Image[width][height];
        sprites = new LinkedList();
    }


    /**
     * Gets the width of this TileMap (number of tiles across).
     * @return int
     */
    
    public int getWidth() {
        return tiles.length;
    }


    /**
     * Gets the height of this TileMap (number of tiles down).
     * @return int
     */
    
    public int getHeight() {
        return tiles[0].length;
    }


    /**
     * Gets the tile at the specified location. Returns null if
     * no tile is at the location or if the location is out of
     * bounds.
     * @param x
     * @param y
     * @return Image
     */
    
    
    public Image getTile(int x, int y) {
        if (x < 0 || x >= getWidth() ||
            y < 0 || y >= getHeight())
        {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }


    /**
     * Sets the tile at the specified location.
     * @param x
     * @param y
     * @param tile 
     */
    
    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }


    /**
     * Gets the player Sprite.
     * @return 
     */
    
    public Sprite getPlayer() {
        return player;
    }


    /**
     * Sets the player Sprite.
     * @param player 
     */
    public void setPlayer(Sprite player) {
        this.player = player;
    }


    /**
     * Adds a Sprite object to this map.
     * @param sprite 
     */
    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }


    /**
     * Removes a Sprite object from this map.
     * @param sprite 
     */
    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }


    /**
     * Gets an Iterator of all the Sprites in this map,
     * excluding the player Sprite.
     * @return 
     */
    public Iterator getSprites() {
        return sprites.iterator();
    }

}
