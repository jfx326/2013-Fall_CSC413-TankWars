package Tank;

import java.awt.*;
import java.awt.image.*;
import java.util.Random;

public class PowerUp {

    private Image img; // image of powerup
    private int x, y, w, h, // position, width, height
            type; // what kind?
    private ImageObserver obs;
    private Random gen; // for generating powerup at random position

    PowerUp(Image img, int x, int y, int type, Random gen) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.type = type;
        this.gen = gen;
        w = img.getHeight(obs);
        h = img.getWidth(obs);
    }

    public void draw(Graphics g, ImageObserver obs) {
        this.obs = obs;
        g.drawImage(img, x, y, obs);
    }

    public void update() {
        // update tank if it picks up powerup
        // respawn powerup at new position
        if (Tank.p1.collide(x, y, w, h)) {
            reset(); // respawn at new position
            try {
                switch (type) {
                    case 0:
                        Tank.gameEvents.setValue("P1GotHealth");
                        break;
                    case 1:
                        Tank.gameEvents.setValue("P1GotShield");
                        break;
                    case 2:
                        Tank.gameEvents.setValue("P1GotBounce");
                        break;
                    case 3:
                        Tank.gameEvents.setValue("P1GotRocket");
                        break;
                    default: // in case of programming accidents...
                        throw new Exception("Nonexistent Power-Up");
                }
            } catch (Exception e) {
            }
        } else if (Tank.p2.collide(x, y, w, h)) {
            reset(); // respawn at new position
            try {
                switch (type) {
                    case 0:
                        Tank.gameEvents.setValue("P2GotHealth");
                        break;
                    case 1:
                        Tank.gameEvents.setValue("P2GotShield");
                        break;
                    case 2:
                        Tank.gameEvents.setValue("P2GotBounce");
                        break;
                    case 3:
                        Tank.gameEvents.setValue("P2GotRocket");
                        break;
                    default: // in case of programming accidents...
                        throw new Exception("Nonexistent Power-Up");
                }
            } catch (Exception e) {
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
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }

    public void reset() {
        x = gen.nextInt(Tank.mapSize - 40);
        y = gen.nextInt(Tank.mapSize - 40);
    }
}