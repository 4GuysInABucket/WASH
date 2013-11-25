/*
 * The MIT License
 *
 * Copyright 2013 JLo.
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
package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.tilegame.WashStart;
import com.brackeen.javagamebook.graphics.Sprite;
import com.brackeen.javagamebook.graphics.Animation;
import com.brackeen.javagamebook.tilegame.TileMapRenderer;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author JLo
 */
public class Bullet extends Sprite{
    
    private int r;
    
    private double ddx;
    private double ddy;
    private double rad;
    private double speed;
    private boolean live;
    
    private Color color1;
    
    //Constructor
    public Bullet(Animation anim, double angle, float x, float y){
        
        super(anim, x, y);
        
        r=6;
        
        rad = Math.toRadians(angle);
        speed = 4;
        ddx = Math.cos(rad) * speed;
        ddy = Math.sin(rad) * speed;
        
        color1 = Color.BLUE;
        live=false;
        
    }
    
    public boolean updateBullet(long elapsedTime){
        this.setX(this.getX()+(float)ddx);
        this.setY(this.getY()+(float)ddy);
        
        //anim.update(elapsedTime);
        
        if(this.getX() < -r || this.getX() > WashStart.screen.getWidth() - TileMapRenderer.offsetX + r ||
                this.getY() < -r || this.getY() > WashStart.screen.getHeight() + r){
            return true;
        }
        
        
        return false;
        
    }
    
    public double getSpeed(){
        return this.speed;
    }
    public void setLive(boolean l){
        this.live = l;
    }
    
    public void draw(Graphics2D g){
        //g.setColor(color1);
        //g.fillOval((int)(x - r), (int)(y - r), 2 * r, 2 * r);
        g.drawImage(this.getImage(), Math.round(this.getX()), Math.round(this.getY()), null);
    }
    
}
