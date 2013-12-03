package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.tilegame.sprites.*;
import java.net.URL;


/**
 * The ResourceManager class loads and manages tile Images and
 * "host" Sprites used in the game. Game Sprites are cloned from
 * "host" Sprites.
 */
public class ResourceManager {

    private ArrayList tiles;
    private int currentMap;
    private static GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite playerNSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite connySprite;
    private Sprite tonnySprite;
    private Sprite flySprite;
    private Font font;

    /**
     * Creates a new ResourceManager with the specified
     * GraphicsConfiguration.
     */
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
    }
    
    
    public static Font getFont() throws FontFormatException, IOException{
        URL urlFont = ResourceManager.class.getResource("/fonts/8bit.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, urlFont.openStream());
        return font;
        
    }


    /**
     * Gets an image from the images/ directory.
     * 
     * @param name Image Name
     * @return Image
     */
    public static Image loadImage(String name) {
        String filename = "/images/" + name;
        URL urlImg = ResourceManager.class.getResource(filename);
        return new ImageIcon(urlImg).getImage();
    }
    
    /**
     * Obtain mirror image
     * 
     * @param image image to be mirror
     * @return Image
     */

    public static Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }

    /**
     * Obtain Flipped image
     * 
     * @param image
     * @return 
     */

    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }

    /**
     * Transform the Image
     * 
     * @param image
     * @param x
     * @param y
     * @return Image
     */

    private static Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }

    /**
     * Load Next Map
     * 
     * @return  TileMap
     */
    
    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap++;
            try {
                if(currentMap==4){
                    currentMap=1;
                }
                map = loadMap(
                    "/maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
    }
    
    public void setCurrentMap(int map){
        this.currentMap = map;
    }
    
    public int getCurrentMap(){
        return this.currentMap;
    }

    /**
     * Reload Map
     * 
     * @return  Tile Map
     */

    public TileMap reloadMap() {
        try {
            return loadMap(
                "/maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Load Map
     * 
     * @param filename
     * @return TileMap
     * @throws IOException 
     */

    private TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;
        InputStream ipStrm = ResourceManager.class.getResourceAsStream(filename);
        
    InputStreamReader ipStrmRdr = new InputStreamReader(ipStrm);
    BufferedReader reader = new BufferedReader(ipStrmRdr);
        // read every line in the text file into the list
        //BufferedReader reader = new BufferedReader(
        //    new FileReader(urlMap.toString()));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, coinSprite, x, y);
                }
                else if (ch == '!') {
                    addSprite(newMap, musicSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, connySprite, x, y);
                }
                else if (ch == '3') {
                    addSprite(newMap, tonnySprite, x, y);
                }
            }
        }

        Sprite player;
        
        if(WashStart.bBoy){
        // add the player to the map
          player = (Sprite)playerSprite.clone();
        }else{
          player = (Sprite)playerNSprite.clone();  
        }
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);

        return newMap;
    }

    /**
     * Add Sprite
     * 
     * @param map
     * @param hostSprite
     * @param tileX
     * @param tileY 
     */

    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------

    /**
     * Load Tile Images
     */

    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            //URL urlImg = ResourceManager.class.getResource("/images/" + name);
            if (ch == 'M'){
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
    }

    /**
     * Load Creature Sprites
     */

    public void loadCreatureSprites() {

        Image[][] images = new Image[4][];
        
        // load left-facing images
        images[0] = new Image[] {
            loadImage("player1.png"),
            loadImage("player2.png"),
            loadImage("player3.png"),
            loadImage("player4.png"),
            loadImage("player5.png"),
            loadImage("player6.png"),
            loadImage("player7.png"),
            loadImage("player8.png"),
            loadImage("player9.png"),
            loadImage("player10.png"),//10            
            loadImage("fly1.png"),
            loadImage("fly2.png"),
            loadImage("fly3.png"),
            loadImage("grub1.png"),
            loadImage("grub2.png"),
            loadImage("player.png"),
            loadImage("salto1.png"),
            loadImage("salto2.png"),
            loadImage("salto3.png"),
            loadImage("salto4.png"),//20
            loadImage("salto5.png"),
            loadImage("salto6.png"),
            loadImage("standing1.png"),
            loadImage("standing2.png"),
            loadImage("sucio1.png"),//25
            loadImage("sucio2.png"),
            loadImage("sucio3.png"),
            loadImage("sucio4.png"),
            loadImage("sucio5.png"),
            loadImage("sucio6.png"),//30
            loadImage("sucio7.png"),
            loadImage("sucio8.png"),//32
            loadImage("salvado1.png"),//33
            loadImage("salvado2.png"),
            loadImage("salvado3.png"),
            loadImage("salvado4.png"),
            loadImage("salvado5.png"),
            loadImage("salvado6.png"),
            loadImage("salvado7.png"),
            loadImage("salvado8.png"),//40
            loadImage("salvado9.png"),
            loadImage("salvado10.png"),
            loadImage("salvado11.png"),
            loadImage("salvado12.png"),
            loadImage("salvado13.png"),
            loadImage("salvado14.png"),
            loadImage("salvado15.png"),
            loadImage("salvado16.png"),
            loadImage("salvado17.png"),//49
            loadImage("menossucio1.png"),//50
            loadImage("menossucio2.png"),
            loadImage("menossucio3.png"),
            loadImage("menossucio4.png"),
            loadImage("menossucio5.png"),
            loadImage("menossucio6.png"),
            loadImage("menossucio7.png"),
            loadImage("menossucio8.png"),//57
            loadImage("mmenossucio1.png"),//58
            loadImage("mmenossucio2.png"),
            loadImage("mmenossucio3.png"),
            loadImage("mmenossucio4.png"),
            loadImage("mmenossucio5.png"),
            loadImage("mmenossucio6.png"),
            loadImage("mmenossucio7.png"),
            loadImage("mmenossucio8.png"),//64
            loadImage("nplayer1.png"),//65
            loadImage("nplayer2.png"),
            loadImage("nplayer3.png"),
            loadImage("nplayer4.png"),
            loadImage("nplayer5.png"),
            loadImage("nplayer6.png"),
            loadImage("nplayer7.png"),
            loadImage("nplayer8.png"),
            loadImage("nplayer9.png"),
            loadImage("nplayer10.png"),//74
            loadImage("nsalto1.png"),
            loadImage("nsalto2.png"),
            loadImage("nsalto3.png"),
            loadImage("nsalto4.png"),
            loadImage("nsalto5.png"),
            loadImage("nsalto6.png"),//80
            loadImage("nstanding1.png"),//81
            loadImage("nstanding2.png"),//82
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[8];
        Animation[] playerAnimN = new Animation[8];
        Animation[] flyAnim = new Animation[8];
        Animation[] grubAnim = new Animation[8];
        Animation[] connyAnim = new Animation[8];
        Animation[] tonnyAnim = new Animation[8];
        for (int i=0; i<4; i++) {
           playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2], 
                    images[i][3], images[i][4], images[i][5],
                    images[i][6], images[i][7], images[i][8],
                    images[i][9]);
           playerAnimN[i] = createPlayerAnim(
                images[i][65], images[i][66], images[i][67], 
                    images[i][68], images[i][69], images[i][70],
                    images[i][71], images[i][72], images[i][73],
                    images[i][74]);
            flyAnim[i] = createFlyAnim(
                images[i][10], images[i][11], images[i][12]);
        }
        playerAnim[4] = createPlayerStanding(images[0][22], images[0][23]);//standing left
        playerAnim[5] = createPlayerStanding(images[1][22], images[1][23]);//standing right
        playerAnim[6] = createPlayerJumping(images[0][16], images[0][17], //jumping left
                images[0][18], images[0][19], images[0][20], images[0][21]);
        playerAnim[7] = createPlayerJumping(images[1][16], images[1][17], //jumping right
                images[1][18], images[1][19], images[1][20], images[1][21]);
        
        playerAnimN[4] = createPlayerStanding(images[0][81], images[0][82]);//standing left
        playerAnimN[5] = createPlayerStanding(images[1][81], images[1][82]);//standing right
        playerAnimN[6] = createPlayerJumping(images[0][75], images[0][76], //jumping left
                images[0][77], images[0][78], images[0][79], images[0][80]);
        playerAnimN[7] = createPlayerJumping(images[1][75], images[1][76], //jumping right
                images[1][77], images[1][78], images[1][79], images[1][80]);
        
        flyAnim[4] = createPlayerStanding(images[0][10], images[0][10]);
        flyAnim[5] = createPlayerStanding(images[1][10], images[1][10]);
        grubAnim[0] = createEnemyAnim("sucio");
        grubAnim[1] = createMirrorEnemyAnim("sucio");
        grubAnim[2]= createEnemyAnim("salvado");
        grubAnim[3]= createMirrorEnemyAnim("salvado");
        grubAnim[4] = createEnemyAnim("menossucio");
        grubAnim[5] = createMirrorEnemyAnim("menossucio");
        grubAnim[6] = createEnemyAnim("mmenossucio");
        grubAnim[7] = createMirrorEnemyAnim("mmenossucio");
        connyAnim[0] = createEnemyAnim("conny/sucio");
        connyAnim[1] = createMirrorEnemyAnim("conny/sucio");
        connyAnim[2]= createEnemyAnim("conny/salvado");
        connyAnim[3]= createMirrorEnemyAnim("conny/salvado");
        connyAnim[4] = createEnemyAnim("conny/menossucio");
        connyAnim[5] = createMirrorEnemyAnim("conny/menossucio");
        connyAnim[6] = createEnemyAnim("conny/mmenossucio");
        connyAnim[7] = createMirrorEnemyAnim("conny/mmenossucio");
        tonnyAnim[0] = createEnemyAnim("tonny/sucio");
        tonnyAnim[1] = createMirrorEnemyAnim("tonny/sucio");
        tonnyAnim[2]= createEnemyAnim("tonny/salvado");
        tonnyAnim[3]= createMirrorEnemyAnim("tonny/salvado");
        tonnyAnim[4] = createEnemyAnim("tonny/menossucio");
        tonnyAnim[5] = createMirrorEnemyAnim("tonny/menossucio");
        tonnyAnim[6] = createEnemyAnim("tonny/mmenossucio");
        tonnyAnim[7] = createMirrorEnemyAnim("tonny/mmenossucio");

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            playerAnim[2], playerAnim[3], playerAnim[4], playerAnim[5], playerAnim[6], playerAnim[7]);
        playerNSprite = new Player(playerAnimN[0], playerAnimN[1],
            playerAnimN[2], playerAnimN[3], playerAnimN[4], playerAnimN[5], playerAnimN[6], playerAnimN[7]);
        flySprite = new Fly(flyAnim[0], flyAnim[1],
            flyAnim[2], flyAnim[3], flyAnim[4], flyAnim[5], flyAnim[4], flyAnim[5]);
        grubSprite = new Grub(grubAnim[0], grubAnim[1],
            grubAnim[2], grubAnim[3], grubAnim[4], grubAnim[5], grubAnim[6], grubAnim[7]);
        connySprite = new Grub(connyAnim[0], connyAnim[1],
            connyAnim[2], connyAnim[3], connyAnim[4], connyAnim[5], connyAnim[6], connyAnim[7]);
        tonnySprite = new Grub(tonnyAnim[0], tonnyAnim[1],
            tonnyAnim[2], tonnyAnim[3], tonnyAnim[4], tonnyAnim[5], tonnyAnim[6], tonnyAnim[7]);
    }

    /**
     * Create Player Animations
     * 
     * @param player1
     * @param player2
     * @param player3
     * @return  Animation
     */

    private Animation createPlayerAnim(Image player1,
        Image player2, Image player3, Image player4,
        Image player5, Image player6, Image player7,
        Image player8, Image player9, Image player10)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 80);
        anim.addFrame(player2, 80);
        anim.addFrame(player3, 80);
        anim.addFrame(player4, 80);
        anim.addFrame(player5, 80);
        anim.addFrame(player6, 80);
        anim.addFrame(player7, 80);
        anim.addFrame(player8, 80);
        anim.addFrame(player9, 80);
        anim.addFrame(player10, 80);
        
        return anim;
    }
    
    /**
     * Create Player Animations
     * 
     * @param player1
     * @param player2
     * @param player3
     * @return  Animation
     */

    private Animation createPlayerJumping(Image player1,
        Image player2, Image player3, Image player4,
        Image player5, Image player6)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 200);
        anim.addFrame(player2, 200);
        anim.addFrame(player3, 200);
        anim.addFrame(player4, 200);
        anim.addFrame(player5, 200);
        anim.addFrame(player6, 200);
        
        return anim;
    }
    
    /**
     * Create Player Animations
     * 
     * @param player1
     * @param player2
     * @param player3
     * @return  Animation
     */

    private Animation createPlayerStanding(Image player1, Image player2)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 250);
        anim.addFrame(player2, 250);
        
        return anim;
    }
    
    /**
     * Create Fly Animation
     * 
     * @param img1
     * @param img2
     * @param img3
     * @return  Animation
     */

    private Animation createFlyAnim(Image img1, Image img2,
        Image img3)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);
        return anim;
    }

    /**
     * Create Grub Animation
     * 
     * @param img1
     * @param img2
     * @return  Animation
     */

    private Animation createGrubAnim(Image img1, Image img2, Image img3, Image img4,
            Image img5, Image img6,Image img7, Image img8) {
        Animation anim = new Animation();
        anim.addFrame(img1, 150);
        anim.addFrame(img2, 150);
        anim.addFrame(img3, 150);
        anim.addFrame(img4, 150);
        anim.addFrame(img5, 150);
        anim.addFrame(img6, 150);
        anim.addFrame(img7, 150);
        anim.addFrame(img8, 150);
        return anim;
    }
    
    /**
     * 
     * @param str
     * @return anim
     */
    
    private Animation createEnemyAnim(String str) {
        Animation anim = new Animation();
        for(int i=1;i<=26;i++){
            anim.addFrame(loadImage(str+i+".png"), 80);
        }
        return anim;
    }
    
    /**
     * 
     * @param str
     * @return anim
     */
    
    private Animation createMirrorEnemyAnim(String str) {
        Animation anim = new Animation();
        for(int i=1;i<=26;i++){
            anim.addFrame(getMirrorImage(loadImage(str+i+".png")), 80);
        }
        return anim;
    }
    

    /**
     * Load PowerUp Sprites
     */

    private void loadPowerUpSprites() {
        // create "goal" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("heart1.png"), 150);
        anim.addFrame(loadImage("heart2.png"), 150);
        anim.addFrame(loadImage("heart3.png"), 150);
        anim.addFrame(loadImage("heart2.png"), 150);
        goalSprite = new PowerUp.Goal(anim);

        // create "star" sprite
        anim = new Animation();
        anim.addFrame(loadImage("bottle.png"), 100);
        anim.addFrame(loadImage("bottle.png"), 100);
        coinSprite = new PowerUp.Star(anim);

        // create "music" sprite
        anim = new Animation();
        anim.addFrame(loadImage("music1.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        anim.addFrame(loadImage("music3.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        musicSprite = new PowerUp.Music(anim);
    }
    
    public static Animation bulletAnimationLeft(){
        Animation anim = new Animation();
        
        anim.addFrame(loadImage("bullet1.png"), 50);
        anim.addFrame(loadImage("bullet2.png"), 50);
        anim.addFrame(loadImage("bullet3.png"), 50);
        
        return anim;
    }
    public static Animation bulletAnimationRight(){
        Animation anim = new Animation();
        
        anim.addFrame(getMirrorImage(loadImage("bullet1.png")), 50);
        anim.addFrame(getMirrorImage(loadImage("bullet2.png")), 50);
        anim.addFrame(getMirrorImage(loadImage("bullet3.png")), 50);
        
        return anim;
    }
    
    /**
     * Create Grub Animation
     * 
     * @param img1
     * @param img2
     * @return  Animation
     */

    private Animation createLimpioAnim(Image img1, Image img2, Image img3, Image img4,
            Image img5, Image img6,Image img7, Image img8, Image img9, Image img10, Image img11, Image img12,
            Image img13, Image img14,Image img15, Image img16, Image img17) {
        Animation anim = new Animation();
        anim.addFrame(img1, 100);
        anim.addFrame(img2, 100);
        anim.addFrame(img3, 100);
        anim.addFrame(img4, 100);
        anim.addFrame(img5, 100);
        anim.addFrame(img6, 100);
        anim.addFrame(img7, 100);
        anim.addFrame(img8, 100);
        anim.addFrame(img9, 100);
        anim.addFrame(img10, 100);
        anim.addFrame(img11, 100);
        anim.addFrame(img12, 100);
        anim.addFrame(img13, 100);
        anim.addFrame(img14, 100);
        anim.addFrame(img15, 100);
        anim.addFrame(img16, 100);
        anim.addFrame(img17, 600);

        return anim;
    }

}
