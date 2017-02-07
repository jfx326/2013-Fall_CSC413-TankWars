package Tank;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class BreakableWall {

    private Image img; // image of breakable wall
    private int x, y, w, h, // position, height, width of wall
            spawnTime = 0; // time until wall appears again
    private boolean boom = false; // destroyed?
    private ImageObserver obs;
    private ArrayList<Image> expimg = new ArrayList(); // explosion images

    BreakableWall(Image img, int x, int y) {
        this.img = img;
        this.x = x;
        this.y = y;
        w = img.getHeight(obs);
        h = img.getWidth(obs);

        for (int i = 1; i <= 6; i++) {
            expimg.add(Tank.r.getSprite("Resources/SE/SE" + i + ".png"));
        }
    }

    public void draw(Graphics g, ImageObserver obs) {
        this.obs = obs;

        if (!boom) { // draw image if not destroyed
            g.drawImage(img, x, y, obs);
        } else if (spawnTime < 30) { // explosion animation
            g.drawImage(expimg.get(spawnTime / 5), x, y, obs);
            spawnTime++;
        } // flicker effect that shows that wall is being rebuilt
        else if (spawnTime >= 1800 && spawnTime < 2100) {
            if (spawnTime % 30 <= 15) {
                g.drawImage(img, x, y, obs);
            }
            // delay rebuilding wall if tank is on position
            // flicker effect continues
            if (Tank.p1.collide(x, y, w, h) || Tank.p2.collide(x, y, w, h)) {
                if (spawnTime % 30 == 29) {
                    spawnTime = 1799;
                }
            }
            spawnTime++;
        } // wall rebuilt (takes 30 seconds after explosion)
        else if (spawnTime == 2100) {
            spawnTime = 0;
            boom = false;
        } else {
            spawnTime++;
        }
    }

    public void update() {
        if (!boom) { // detect collisions w/ players if not destroyed
            if (Tank.p1.collide(x, y, w, h)) {
                Tank.gameEvents.setValue("P1Crashed");
            } else if (Tank.p2.collide(x, y, w, h)) {
                Tank.gameEvents.setValue("P2Crashed");
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

    public void destroy() {
        boom = true;
        Tank.gs.playSound("Resources/Sound/ExpS.wav");
    }

    public boolean isDestroyed() {
        return boom;
    }

    public void reset() {
        spawnTime = 0;
        boom = false;
    }
}