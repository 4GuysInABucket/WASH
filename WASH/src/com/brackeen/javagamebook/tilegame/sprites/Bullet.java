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

import java.awt.*;
import com.brackeen.javagamebook.tilegame.WashStart;
import com.brackeen.javagamebook.test.GameCore;

/**
 *
 * @author JLo
 */
public class Bullet {
    
    //Variables
    private double x;
    private double y;
    private int r;
    
    private double dx;
    private double dy;
    private double rad;
    private double speed;
    
    private Color color1;
    
    //Constructor
    public Bullet(double angle, double x, double y){
        
        this.x = x;
        this.y = y;
        r=4;
        
        rad = Math.toRadians(angle);
        speed = 2;
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;
        
        color1 = Color.YELLOW;
        
    }
    
    public boolean update(){
        x += dx;
        y += dy;
        
        if(x < -r || x > WashStart.screen.getWidth() + r ||
                y < -r || y > WashStart.screen.getHeight() + r){
            return true;
        }
        
        return false;
    }
    
    public void draw(Graphics2D g){
        g.setColor(color1);
        g.fillOval((int)(x - r), (int)(y - r), 2 * r, 2 * r);
    }
    
}
