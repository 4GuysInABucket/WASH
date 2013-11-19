/*
 * @author Juan Lorenzo Gonzalez
 * @author Hilce Estefanía Larsen Ruiz
 * @author Martha Iliana García Hinojosa
 * @author Carlos Enrique Alavez García
 * @version alpha
 */

/*
 * The MIT License
 *
 * Copyright 2013 4 Guys in a Bucket.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.util.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.sound.*;
import com.brackeen.javagamebook.input.*;
import com.brackeen.javagamebook.test.GameCore;
import static com.brackeen.javagamebook.test.GameCore.screen;
import com.brackeen.javagamebook.tilegame.sprites.*;

/**
 * WashStart manages all parts of the game.
 */
public class WashStart extends GameCore {

    public static void main(String[] args) {
        new WashStart().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    private InputManager inputManager;
    private TileMapRenderer renderer;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction exit;
    private GameAction fire;
    private GameAction pause;
    private boolean bPause;
    private GameAction restart;
    public static ArrayList<Bullet> bullets;
    private int angle;
    private int bulletOffset;
    private Animation bulletAnim;
    
    public static int lives;
    public static int score;
    
    public static Image iPause;
    public static Image iGameOver;
    public static Image iLives;

    /**
     * Initializes Game and variables.
     */
    public void init() {
        super.init();

        // set up input manager
        initInput();

        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
            resourceManager.loadImage("background.png"));

        // load first map
        map = resourceManager.loadNextMap();

        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        prizeSound = soundManager.getSound("sounds/prize.wav");
        boopSound = soundManager.getSound("sounds/boop2.wav");

        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
            midiPlayer.getSequence("sounds/music.midi");
        midiPlayer.play(sequence, true);
        toggleDrumPlayback();
        
        bullets = new ArrayList<Bullet>();
        angle=0;
        
        lives = 3;
        score = 0;
        
        bPause = false;
        
        iPause = ResourceManager.loadImage("pause.png");
        iGameOver = ResourceManager.loadImage("gameover.jpg");
        iLives = ResourceManager.loadImage("toothbrush.png");
    }


    /**
     * Closes any resources used by the WashStart.
     */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }

    /**
     * Declares input keys
     */
    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        jump = new GameAction("jump",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        fire = new GameAction("fire");
        pause = new GameAction("pause", GameAction.DETECT_INITAL_PRESS_ONLY);
        restart = new GameAction("restart", GameAction.DETECT_INITAL_PRESS_ONLY);

        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump, KeyEvent.VK_UP);
        inputManager.mapToKey(fire, KeyEvent.VK_SPACE);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(pause, KeyEvent.VK_P);
        inputManager.mapToKey(restart, KeyEvent.VK_R);
    }
    
    /**
     * Reinitializes variables, in order to restart the game.
     */
    private void restartGame() {
        
        map = resourceManager.reloadMap();
        
        lives = 3;
        score = 0;
        
        bPause = false;
    }
    
    /**
     * Checks input if player is alive
     * @param elapsedTime  Time elapsed
     */
    private void checkInput(long elapsedTime) {

        if (exit.isPressed()) {
            stop();
        }

        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
            float velocityX = 0;
            if (moveLeft.isPressed()) {
                velocityX-=player.getMaxSpeed();
                angle=180;
                bulletOffset=0;
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getMaxSpeed();
                angle=0;
                bulletOffset=player.getWidth()/2;
            }
            if (jump.isPressed()) {
                player.jump(false);
            }
            player.setVelocityX(velocityX);
            
            if(player.getStanding()==0){
                bulletAnim = ResourceManager.bulletAnimationLeft();
            }else{
                bulletAnim = ResourceManager.bulletAnimationRight();
            }
                                   
            if(fire.isPressed()){
                if(player.isFiring()){
                    long elapsed = (System.nanoTime() - player.getBulletTimer())/1000000;
                    if(elapsed > player.getBulletDelay()){
                        bullets.add(new Bullet(bulletAnim, angle,
                                player.getX()+bulletOffset+TileMapRenderer.offsetX,
                                player.getY()-player.getHeight()/2-16));
                        player.setBulletTimer(System.nanoTime());
                    }
                }
                player.fire(true);
            }
            
            if(pause.isPressed()){
                bPause = !bPause;
            }
            
            if (restart.isPressed()) {
                restartGame();
            }
        }

    }

    /**
     * Draw Method
     * @param g  Graphics2D
     */
    public void draw(Graphics2D g) {
        
        Window window = ScreenManager.device.getFullScreenWindow();
        
        if (lives>0) {
            if (!bPause){
                renderer.draw(g, map,
                screen.getWidth(), screen.getHeight());
                for(int j = 0; j < bullets.size(); j++){
                    bullets.get(j).draw(g);
                }

                for (int i = 0; i < lives; i++) {
                    g.drawImage(iLives, i*60+5, 10, null);
                }
                
                g.drawString("Score: " + score, 5, 60);
            }
            else {
                g.drawImage(iPause, 0, 0,
                    window.getWidth(), window.getHeight(), null);
            }
            
        }
        else {
            g.drawImage(iGameOver, 0, 0,
                    window.getWidth(), window.getHeight(), null);
        }
    }
    
    /**
     * Gets the current map.
     */
    public TileMap getMap() {
        return map;
    }
    
    /**
     * Turns on/off drum playback in the midi music (track 1).
     */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer != null) {
            sequencer.setTrackMute(DRUM_TRACK,
                !sequencer.getTrackMute(DRUM_TRACK));
        }
    }
    
    /**
     * Gets the tile that a Sprites collides with. Only the
     * Sprite's X or Y should be changed, not both. Returns null
     * if no collision is detected.
     * 
     * @param sprite
     * @param newX
     * @param newY
     * @return Point
     */ 
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }

    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }
    
    /**
     * Gets the Sprite that collides with the specified Sprite,
     * or null if no Sprite collides with the specified Sprite.
     * 
     * @param sprite
     * @return Sprite
     */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }
    
    /**
     * Updates Animation, position, and velocity of all Sprites
     * in the current map.
     * 
     * @param elapsedTime  Time Elapsed
     */
    public void update(long elapsedTime) {
        Creature player = (Creature)map.getPlayer();


        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {
            map = resourceManager.reloadMap();
            return;
        }

        // get keyboard/mouse input
        checkInput(elapsedTime);
        
        if (!bPause){
            // update player
            updateCreature(player, elapsedTime);
            player.update(elapsedTime);

            // update other sprites
            Iterator i = map.getSprites();
            while (i.hasNext()) {
                Sprite sprite = (Sprite)i.next();
                if (sprite instanceof Creature) {
                    Creature creature = (Creature)sprite;
                    if (creature.getState() == Creature.STATE_DEAD) {
                        i.remove();
                    }
                    else {
                        updateCreature(creature, elapsedTime);
                    }
                }
                // normal update
                sprite.update(elapsedTime);
            }
            for(int j = 0; j < bullets.size(); j++){
                boolean remove = bullets.get(j).updateBullet(elapsedTime);
                if(remove){
                    bullets.remove(j);
                    j--;
                } 
            }
        }
    }
    
    /**
     * Updates the creature, applying gravity for creatures that
     * aren't flying, and checks collisions.
     * 
     * @param creature
     * @param elapsedTime
     */
    private void updateCreature(Creature creature,
        long elapsedTime)
    {

        // apply gravity
        if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                GRAVITY * elapsedTime);
        }

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player)creature, false);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player)creature, canKill);
        }

    }
    
    /**
     * Checks for Player collision with other Sprites. If
     * canKill is true, collisions with Creatures will kill
     * them.
     * 
     * @param player  Player
     * @param canKill  If sprite can kill player
     */
    public void checkPlayerCollision(Player player,
        boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)collisionSprite);
        }
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill) {
                // kill the badguy and make player bounce
                soundManager.play(boopSound);
                badguy.setState(Creature.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
                score+=100;
            }
            else {
                // player dies!
                lives--;
                player.setState(Creature.STATE_DYING);
            }
        }
    }
    
    /**
     * Gives the player the speicifed power up and removes it
     * from the map.
     * 
     * @param powerUp Power up
     */
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);

        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            score += 50;
            soundManager.play(prizeSound);
        }
        else if (powerUp instanceof PowerUp.Music) {
            // change the music
            soundManager.play(prizeSound);
            toggleDrumPlayback();
        }
        else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            soundManager.play(prizeSound,
                new EchoFilter(2000, .7f), false);
            map = resourceManager.loadNextMap();
        }
    }

}
