package Tank;

import java.awt.*;
import java.awt.image.*;

public class Wall {

    private Image img;
    private int x, y, w, h;
    private ImageObserver obs;

    Wall(Image img, int x, int y) {
        this.img = img;
        this.x = x;
        this.y = y;
        w = img.getHeight(obs);
        h = img.getWidth(obs);
    }

    public void draw(Graphics g, ImageObserver obs) {
        this.obs = obs;
        g.drawImage(img, x, y, obs);
    }
    
    public void update() { // checks for collisions w/ tanks, powerups
        if (Tank.p1.collide(x, y, w, h)) {
            Tank.gameEvents.setValue("P1Crashed");
        } else if (Tank.p2.collide(x, y, w, h)) {
            Tank.gameEvents.setValue("P2Crashed");
        } else { // respawn powerup at clear location if collides w/ wall
            // breakable walls are okay since they can be broken
            for (int i = 0; i < Tank.pow.size(); i++) {
                while (Tank.pow.get(i).collide(x, y, w, h)) {
                    Tank.pow.get(i).reset();
                }
            }
        }
    }

    public boolean collide(int x, int y, int w, int h) {
        if ((y + h > this.y) && (y < this.y + this.h)) {
            if ((x + w > this.x) && (x < this.x + this.w)) {
                return true;
            }
        }
        return false;
    }
}