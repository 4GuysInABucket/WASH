/*
 * @author Juan Lorenzo Gonzalez
 * @author Hilce Estefanía Larsen Ruiz
 * @author Martha Iliana García Hinojosa
 * @author Carlos Enrique Alavez García
 * @version beta
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

import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Sound bgSound;
    private SoundClip bgSoundClip;
    private SoundClip watergunSound;
    private InputManager inputManager;
    private TileMapRenderer renderer;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction exit;
    private GameAction fire;
    private GameAction pause;
    private GameAction restart;
    private GameAction instructions;
    private GameAction credits;
    private GameAction sound;
    public static ArrayList<Bullet> bullets;
    private int angle;
    private int bulletOffset;
    private Animation bulletAnim;
    
    public static int lives;
    public static int score;
    public static int municiones;
    private LinkedList<Integer> scorelist; 
    private String fileName;
    private String[] arr;
    private boolean bscores;
    private int introCounter;
    
    public static Image iPause;
    public static Image iGameOver;
    public static Image iLives;
    public static Image iSound;
    
    public static Image iIntro;
    public static Image iIntro2;
    public static Image iIntro3;
    public static Image iIntro4;
    public static Image iMenu;
    public static Image iInstr;
    public static Image iCredits;
    public static Image iBoy;
    public static Image iGirl;
    public static Image iLevel;
    public static Image iLoose;
    public static Image iWin;
    
    private boolean bIntro;
    private boolean bMenu;
    private boolean bInstr;
    private boolean bCredits;
    public static boolean bBoy;
    private boolean bGirl;
    private boolean bLevel;
    private boolean bLoose;
    private boolean bWin;
    private boolean bPlayer;
    
    private boolean bPause;
    private boolean bSound;

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
        bgSound = soundManager.getSound("/sounds/megaman8bit.wav");
        watergunSound = new SoundClip("/sounds/watergun.wav");
        URL urlSound = WashStart.class.getResource("/sounds/megaman8bit.wav");
        bgSoundClip= new SoundClip("/sounds/megaman8bit.wav",true);//agrega el loop al soundclip

        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
            midiPlayer.getSequence("/sounds/music.midi");
        bgSoundClip.play();
        toggleDrumPlayback();
        
        bullets = new ArrayList<Bullet>();
        angle=0;
        
        lives = 3;
        score = 0;
        municiones = 3;
        introCounter = 2700;
        
        
        
        iGameOver = ResourceManager.loadImage("gameover.png");
        iLives = ResourceManager.loadImage("toothbrush.png");
        
        
        iIntro = ResourceManager.loadImage("logo.jpg");
        iIntro2 = ResourceManager.loadImage("intro.gif");
        iIntro3 = ResourceManager.loadImage("intro2.png");
        iIntro4 = ResourceManager.loadImage("intro2.png");
        iMenu = ResourceManager.loadImage("menu.jpg");
        iPause = ResourceManager.loadImage("pause.png");
        iInstr = ResourceManager.loadImage("instr.png");
        iCredits = ResourceManager.loadImage("credits.jpg");
        iBoy = ResourceManager.loadImage("chooseboy.jpg");
        iGirl = ResourceManager.loadImage("choosegirl.jpg");
        iLevel = ResourceManager.loadImage("levelcomplete.png");
        //iLoose = ResourceManager.loadImage("youloose.png");
        iWin = ResourceManager.loadImage("youwin.png");
                
        bIntro = true;
        bMenu = false;
        bInstr = false;
        bCredits = false;
        bBoy = true;
        bGirl = false;
        bLevel = false;
        bLoose = false;
        bWin = false;
        bPause = false;
        bSound = true;
        bPlayer = false;
        
        fileName = "scores.txt";
        scorelist = new LinkedList<Integer>();
        bscores = false;
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
        instructions = new GameAction("instructions", GameAction.DETECT_INITAL_PRESS_ONLY);
        credits = new GameAction("credits", GameAction.DETECT_INITAL_PRESS_ONLY);
        sound = new GameAction("sound", GameAction.DETECT_INITAL_PRESS_ONLY);
        
        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump, KeyEvent.VK_UP);
        inputManager.mapToKey(fire, KeyEvent.VK_SPACE);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(pause, KeyEvent.VK_P);
        inputManager.mapToKey(restart, KeyEvent.VK_ENTER);
        inputManager.mapToKey(instructions, KeyEvent.VK_I);
        inputManager.mapToKey(credits, KeyEvent.VK_C);
        inputManager.mapToKey(sound, KeyEvent.VK_S);
    }
    
    /**
     * Reinitializes variables, in order to restart the game.
     */
    private void restartGame() {
        
        resourceManager.setCurrentMap(1);
        map = resourceManager.reloadMap();
        
        lives = 3;
        score = 0;
        municiones = 3;
        
        bPause = false;
        bgSoundClip.play();
        bSound = true;
        
        scorelist = new LinkedList<Integer>();
        bscores = false;
        
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
            if (jump.isPressed() && !bPause) {
                player.jump(false);
            }
            player.setVelocityX(velocityX);
            
            if(player.getStanding()==0){
                bulletAnim = ResourceManager.bulletAnimationLeft();
            }else{
                bulletAnim = ResourceManager.bulletAnimationRight();
            }
                                   
            if(fire.isPressed() && !bPause){
                if(player.isFiring()){
                    long elapsed = (System.nanoTime() - player.getBulletTimer())/1000000;
                    if(elapsed > player.getBulletDelay()&&municiones>0){
                        municiones--;
                        bullets.add(new Bullet(bulletAnim, angle,
                                player.getX()+bulletOffset,
                                player.getY()+player.getHeight()/2-16));
                        map.addSprite(bullets.get(bullets.size()-1));
                        watergunSound.play();
                        player.setBulletTimer(System.nanoTime());
                    }
                }
                player.fire(true);
            }
            
            if(pause.isPressed()){
                bPause = !bPause;
                if(bPause){
                    bgSoundClip.stop();
                }else{
                    bgSoundClip.play();
                }
            }
            
            if (bBoy && moveRight.isPressed() && bPlayer) {
                bBoy = false;
                bGirl = true;
            }
            if (bGirl && moveLeft.isPressed() && bPlayer) {
                bGirl = false;
                bBoy = true;
            }
            
            if (restart.isPressed()) {
                if (bIntro) {
                    bMenu = true;
                    bIntro = false;
                }
                else if (bMenu) {
                    bMenu = false;
                    //restartGame();
                    bBoy = true;
                    bPlayer = true;
                }
                else if (bBoy && bPlayer) {
                    restartGame();
                    //map = resourceManager.reloadMap();
                    bPlayer=false;
                }
                else if (bGirl && bPlayer) {
                    restartGame();
                    //map = resourceManager.reloadMap();
                    bPlayer=false;
                }
                else if (lives<=0) {
                    bPlayer=true;
                    restartGame();
                }
            }
            
            if (instructions.isPressed()) {
                bInstr = !bInstr;
            }
            
            if (credits.isPressed()) {
                bCredits = !bCredits;
            }
            
            if (sound.isPressed()) {
                bSound = !bSound;
                if(!bSound){
                    bgSoundClip.stop();
                }else{
                    bgSoundClip.play();
                }
            }
            
        }

    }
    
    private void printSimpleString(Graphics2D g2d, String s, int width, int XPos, int YPos){  
            int stringLen = (int)  
                g2d.getFontMetrics().getStringBounds(s, g2d).getWidth();  
            int start = width/2 - stringLen/2;  
            g2d.drawString(s, start + XPos, YPos);  
     }  

    /**
     * Draw Method
     * @param g  Graphics2D
     */
    public void draw(Graphics2D g) {
        
        Window window = ScreenManager.device.getFullScreenWindow();
        Font font;
        try {
            font = ResourceManager.getFont();
            font = font.deriveFont(24f);
            g.setFont(font);
        } catch (FontFormatException ex) {
            Logger.getLogger(WashStart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WashStart.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (bIntro && introCounter > 2200) {
            g.drawImage(iIntro, 0, 0,
                    window.getWidth(), window.getHeight(), null);
            introCounter--;
        }
        else if(bIntro && introCounter > 200){
            g.drawImage(iIntro2, 0, 0,
                    window.getWidth(), window.getHeight(), null);
            introCounter--;
        }
        else if(bIntro && introCounter > 0){
            g.drawImage(iIntro4, 0, 0,
                    window.getWidth(), window.getHeight(), null);
            introCounter--;
        }
        else if(bIntro){
            bIntro=false;
            bMenu=true;
            //printSimpleString(g,"Presiona ENTER Para Continuar",window.getWidth(),0,400);
        }
        else if (bMenu) {
            g.drawImage(iMenu, 0, 0,
                    window.getWidth(), window.getHeight(), null);
        }
        else if (bBoy && bPlayer) {
            g.drawImage(iBoy, 0, 0,
                    window.getWidth(), window.getHeight(), null);
        }
        else if (bGirl && bPlayer) {
            g.drawImage(iGirl, 0, 0,
                    window.getWidth(), window.getHeight(), null);
        }
        else if (lives>0) {
            if (!bPause){
                renderer.draw(g, map,
                screen.getWidth(), screen.getHeight());
                /*
                for(int j = 0; j < bullets.size(); j++){
                    bullets.get(j).draw(g);
                }
                */

                for (int i = 0; i < lives; i++) {
                    g.drawImage(iLives, i*60+5, 10, null);
                }
                
                g.drawString("Puntaje: " + score, 5, 60);
                g.drawString("Municiones: " + municiones, 5, 90);
            }
            else {
                g.drawImage(iPause, 0, 0,
                    window.getWidth(), window.getHeight(), null);
            }
            
        }
        else {
            g.drawImage(iGameOver, 0, 0,
                    window.getWidth(), window.getHeight(), null);
            g.drawString("Puntajes más altos", 50, 50);
            for (int i = 0; i<5 && i<scorelist.size(); i++) {
                g.drawString("#" + (i+1) + ": " + scorelist.get(i), 100, 100+50*i);
            }
        }
        
        if (bInstr) {
            g.drawImage(iInstr, 0, 0,
                    window.getWidth(), window.getHeight(), null);
        }
        else if (bCredits) {
            g.drawImage(iCredits, 0, 0,
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
                    map.removeSprite(bullets.get(j));
                    bullets.remove(j);
                    j--;
                } 
            }
            checkBulletCollision();
        }
        
        if (lives<=0 && !bscores) {
            scorelist.add(score);
            
            try {
                readFile();
                saveFile();
                bscores = true;
            } catch (IOException ex) {
                System.out.println("Error in " + ex.toString());
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
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
                //player.setState(Creature.STATE_DYING);
            }
        }
    }
     
    /**
     * Checks for Grub collision with bullets. Bullets kill grub.
     * 
     * @param grub Grub
     * @param bullet Bullet
     */
    public void checkBulletCollision()
    {
        for(int i = 0; i<bullets.size(); i++){
            Sprite collisionSprite = getSpriteCollision(bullets.get(i));
            if(collisionSprite instanceof Grub || collisionSprite instanceof Fly){
                Creature badguy = (Creature)collisionSprite;
                badguy.setVidas(badguy.getVidas()-1);
                map.removeSprite(bullets.get(i));
                bullets.remove(i);
                if(badguy.getVidas()==0){
                    badguy.setState(Creature.STATE_DYING);
                    score+=100;
                }
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
            municiones += 1;
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
            municiones = 3;
            map = resourceManager.loadNextMap();
        }
    }
    
    /**
     * Reads the scores' file, to get the current saved score list.
     * @throws IOException 
     */
    public void readFile() throws IOException {
        BufferedReader fileIn;
        try {
            fileIn = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e){
            File points = new File(fileName);
            PrintWriter fileOut = new PrintWriter(points);
            fileOut.println("0");
            fileOut.close();
            fileIn = new BufferedReader(new FileReader(fileName));
        }
        String dato = fileIn.readLine();
        while(dato != null) {
            int number = (Integer.parseInt(dato));
            scorelist.add(number);
            dato = fileIn.readLine();
        }
        fileIn.close();
        }
    
    /**
     * Saves the updated score list into the file.
     * @throws IOException 
     */
    public void saveFile() throws IOException {
        
        Collections.sort(scorelist);
        Collections.reverse(scorelist);
        
        PrintWriter fileOut = new PrintWriter(new FileWriter(fileName));
        for (int i = 0; i<5 && i<scorelist.size(); i++) {
            fileOut.println(scorelist.get(i));
        }
        fileOut.close();
    }
}
