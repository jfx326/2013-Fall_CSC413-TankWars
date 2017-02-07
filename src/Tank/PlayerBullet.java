package Tank;

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;

public class PlayerBullet {

    private Image img; // image of bullet (shell)
    private int x, y, w, h, // position, height, width
            player, // who fired?
            oldX, oldY, // previous position prior to collision
            type, // what kind of shell?
            angle, // shares from firing tank, changes if bounces
            bounce = 8, // for bounce shells; they bounce off walls at most 8 times
            postBoomTime = 0; // for explosion sequence
    private double rad; // angle in radians
    private boolean boom = false, // explosion?
            gone = false; // is it out of the game?
    private ImageObserver obs;
    private ArrayList<Image> expimg = new ArrayList(); // explosion
    private ArrayList<Image> bimg = new ArrayList(); // bounce shell images at all angles

    PlayerBullet(Image img, int x, int y, int player) {
        this.img = img;
        this.x = oldX = x;
        this.y = oldY = y;
        this.player = player;
        w = img.getHeight(obs);
        h = img.getWidth(obs);

        // get powerup type, angle from firing tank
        if (player == 1) {
            rad = Tank.p1.getRad();
            type = Tank.p1.getPowerUp();
        } else if (player == 2) {
            rad = Tank.p2.getRad();
            type = Tank.p2.getPowerUp();
        }

        angle = ((int) Math.toDegrees(rad)) / 6;

        for (int i = 1; i <= 6; i++) {
            expimg.add(Tank.r.getSprite("Resources/SE/SE" + i + ".png"));
        }

        for (int i = 0; i < 10; i++) {
            bimg.add(Tank.r.getSprite("Resources/Bounce/B0" + i + ".png"));
        }

        for (int i = 10; i < 60; i++) {
            bimg.add(Tank.r.getSprite("Resources/Bounce/B" + i + ".png"));
        }
    }

    public void draw(Graphics g, ImageObserver obs) {
        this.obs = obs;
        if (!boom) { // draw bullet if no collision
            g.drawImage(img, x, y, obs);
        } else if (postBoomTime == 29) { // explosion sequence done
            gone = true;
        } else if (boom) { // explosion
            postBoomTime++;
            g.drawImage(expimg.get(postBoomTime / 5), oldX, oldY, obs);
        }
    }

    public void update() {
        if (!boom && !gone) {
            oldX = x;
            oldY = y;
            x += 10 * Math.cos(rad);
            y -= 10 * Math.sin(rad);

             // check for friendly fire, collisions; destroy bullet afterwards
            if (player == 1) {
                if (type == 1 && bounce < 8 && Tank.p1.collide(x, y, w, h) && !Tank.p1.isDestroyed()) {
                    Tank.gameEvents.setValue("P1HitBounce");
                    destroy();
                } else if (Tank.p2.collide(x, y, w, h) && !Tank.p2.isDestroyed()) {
                    switch (type) {
                        case 1:
                            Tank.gameEvents.setValue("P2HitBounce");
                            break;
                        case 2:
                            Tank.gameEvents.setValue("P2HitRocket");
                            break;
                        default:
                            Tank.gameEvents.setValue("P2HitShell");
                    }
                    destroy();
                }
            } else if (player == 2) {
                if (type == 1 && bounce < 8 && Tank.p2.collide(x, y, w, h) && !Tank.p2.isDestroyed()) {
                    Tank.gameEvents.setValue("P2HitBounce");
                    destroy();
                } else if (Tank.p1.collide(x, y, w, h) && !Tank.p1.isDestroyed()) {
                    switch (type) {
                        case 1:
                            Tank.gameEvents.setValue("P1HitBounce");
                            break;
                        case 2:
                            Tank.gameEvents.setValue("P1HitRocket");
                            break;
                        default:
                            Tank.gameEvents.setValue("P1HitShell");
                    }
                    destroy();
                }
            }

            // check collisions w/ walls
            for (int i = 0; i < Tank.walls.size(); i++) {
                if (Tank.walls.get(i).collide(x, y, w, h)) {
                    if (type == 1 && bounce > 1) {
                        x = oldX;
                        y = oldY;
                        
                        // change angle if bounce
                        if (angle == 0) { // going left bounces right
                            angle = 30;
                        } else if (angle == 30) { // opposite of above
                            angle = 0;
                        } else {
                            angle = 60 - angle;
                        }
                        
                        // update direction, image
                        rad = Math.toRadians(angle * 6);
                        img = bimg.get(angle);
                        bounce--; // one fewer bounce
                    } else {
                        destroy(); // no more shell
                    }
                    break;
                }
            }

            // collisions w/ breakable walls
            for (int i = 0; i < Tank.bwalls.size(); i++) {
                if (Tank.bwalls.get(i).collide(x, y, w, h) && !Tank.bwalls.get(i).isDestroyed()) {
                    if (type == 1 && bounce > 1) {
                        x = oldX;
                        y = oldY;
                        if (angle == 0) {
                            angle = 30;
                        } else if (angle == 30) {
                            angle = 0;
                        } else {
                            angle = 60 - angle;
                        }
                        rad = Math.toRadians(angle * 6);
                        img = bimg.get(angle);
                        bounce--;
                        
                    } // rockets pierce through breakable walls
                    else if (type != 2) {
                        destroy(); // destroy bullet
                    }
                    Tank.bwalls.get(i).destroy(); // destroy wall
                    break;
                }
            }
        }
    }

    private void destroy() {
        x = oldX;
        y = oldY;
        x = y = 2048;
        boom = true;
        Tank.gs.playSound("Resources/Sound/ExpS.wav");
    }

    // for removing shell from linked list and memory once destroyed
    public boolean isDestroyed() {
        return gone;
    }
}