package Tank;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class Player2 implements Observer {

    private Image img, pb, // tank image, bullet (shell) image
            bpwr, rpwr, life, shld; // powerup, life, shield indicators
    private int x, y, w, h, // position, width, height
            oldX, oldY, // previous x and y position before crashing
            spawnTime = 0, // for explosion, respawn animation
            fireTime = 0, // fire rate
            angle = 0, // angle of turret (angle * 6 = true angle in degrees)
            health = 16,
            armor = 0, // durability of shield (does not affect health)
            score = 0, // number of wins
            lives = 3, powerUp = 0,
            ammo = 0; // ammo of bounce shell or rocket
    private double rad; // angle in radians
    private boolean boom = false, // destroyed?
            ready = true; // can be moved again? (after explosion)
    static boolean up = false, down = false, left = false, right = false, fire = false;
    private ImageObserver obs;
    private GameEvents ge;
    private String msg;
    private ArrayList<Image> expimg = new ArrayList(); // explosion images
    private ArrayList<Image> hpimg = new ArrayList(); // health indicator images
    private ArrayList<Image> simg = new ArrayList(); // shell images at different angles
    private ArrayList<Image> bimg = new ArrayList(); // bounce shell images at different angles
    private ArrayList<Image> rimg = new ArrayList(); // rocket images ...
    private ArrayList<Image> timg = new ArrayList(); // tank images ...
    private Font label;

    Player2(Image img, int x, int y) {
        this.img = img;
        this.x = oldX = x;
        this.y = oldY = y;
        w = img.getWidth(obs);
        h = img.getHeight(obs);

        bpwr = Tank.r.getSprite("Resources/Element/BIcon.png");
        rpwr = Tank.r.getSprite("Resources/Element/RIcon.png");
        shld = Tank.r.getSprite("Resources/Element/Shield2.png");
        life = Tank.r.getSprite("Resources/Element/T2Life.png");

        for (int i = 1; i <= 6; i++) {
            expimg.add(Tank.r.getSprite("Resources/BE/BE" + i + ".png"));
        }

        for (int i = 0; i < 10; i++) {
            hpimg.add(Tank.r.getSprite("Resources/Health/health0" + i + ".png"));

        }

        for (int i = 10; i <= 16; i++) {
            hpimg.add(Tank.r.getSprite("Resources/Health/health" + i + ".png"));
        }

        for (int i = 0; i < 10; i++) {
            simg.add(Tank.r.getSprite("Resources/Shell/S0" + i + ".png"));
            bimg.add(Tank.r.getSprite("Resources/Bounce/B0" + i + ".png"));
            rimg.add(Tank.r.getSprite("Resources/Rocket/R0" + i + ".png"));
            timg.add(Tank.r.getSprite("Resources/T2/T2-0" + i + ".png"));
        }

        for (int i = 10; i < 60; i++) {
            simg.add(Tank.r.getSprite("Resources/Shell/S" + i + ".png"));
            bimg.add(Tank.r.getSprite("Resources/Bounce/B" + i + ".png"));
            rimg.add(Tank.r.getSprite("Resources/Rocket/R" + i + ".png"));
            timg.add(Tank.r.getSprite("Resources/T2/T2-" + i + ".png"));
        }

        label = new Font("SansSerif", Font.BOLD, 16);
    }

    public void draw(Graphics g, ImageObserver obs) {
        this.obs = obs;
        g.setFont(label);
        
        // display health, player name at top of tank
        g.drawImage(hpimg.get(health), x + 2, y - 14, 60, 16, obs);
        g.drawString("Player 2", x + 2, y);
        
        // indicate number of lives at bottom of tank
        if (lives > 1) {
            g.drawImage(life, x + 0, y + 60, 16, 16, obs);
            if (lives > 2) {
                g.drawImage(life, x + 16, y + 60, 16, 16, obs);
            }
        }

        if (ammo > 0) { // ammo > 0 when powerup acquired
            switch (powerUp) {
                case 1: // bounce
                    g.drawImage(bpwr, x + 32, y + 60, obs);
                    break;
                case 2: // rocket
                    g.drawImage(rpwr, x + 32, y + 60, obs);
                    break;
            }
            g.drawString(Integer.toString(ammo), x + 48, y + 74); // show ammo count
        }

        if (!boom && lives > 0) { // draw tank if not destroyed and has lives
            g.drawImage(img, x, y, obs);
            if (armor > 0) { // if shield get
                g.drawImage(shld, x - 8, y - 8, obs);
            }
        } else if (lives == 0) {
            health = 0;
            Tank.r.setGameOver(false); // p1 wins
        } else if (spawnTime == 29) { // respawn sequence start
            lives--;
            if (lives > 0) {
                health = 16;
                ready = true; // ready to move
            }
            spawnTime++;
        } else if (spawnTime > 29 && spawnTime < 329) { // respawn sequence
            if (spawnTime % 30 <= 15) { // flickering to indicate recent respawn
                g.drawImage(img, x, y, obs);
            }
            spawnTime++;
        } else if (spawnTime == 329) { // end respawn sequence
            spawnTime = 0;
            boom = false;
        } else { // explosion sequence
            g.drawImage(expimg.get(spawnTime / 5), x, y, obs);
            spawnTime++;
        }
    }

    public void update() {
        if (ready) {
            rad = Math.toRadians(angle * 6);
            oldX = x;
            oldY = y;

            if (left) {
                angle++;
                if (angle > 59) {
                    angle = 0;
                }
            }
            if (right) {
                angle--;
                if (angle < 0) {
                    angle = 59;
                }
            }
            if (up) {
                x += 5 * Math.cos(rad);
                y -= 5 * Math.sin(rad);
            }
            if (down) {
                x -= 5 * Math.cos(rad);
                y += 5 * Math.sin(rad);
            }

            if (fire) {
                if (fireTime == 0) {
                    if (ammo == 0) {
                        powerUp = 0;
                        pb = simg.get(angle);
                    } else {
                        switch (powerUp) {
                            case 1:
                                pb = bimg.get(angle);
                                break;
                            case 2:
                                pb = rimg.get(angle);
                                break;
                        }
                        ammo--;
                    }
                    Tank.pBullet1.add(new PlayerBullet(pb, x + 22, y + 20, 2));
                    fireTime++;
                } else if (fireTime == 14) {
                    fireTime = 0;
                } else {
                    fireTime++;
                }
            } else {
                fireTime = 0;
            }
            img = timg.get(angle);

            if (Tank.p1.collide(x, y, w, h)) {
                Tank.gameEvents.setValue("P1Crashed");
            }
        }
    }

    public void update(Observable obj, Object arg) {
        ge = (GameEvents) arg;
        msg = (String) ge.event;
        if (ready) {
            if (!boom) {
                switch (msg) {
                    case "P2HitShell":
                        if (armor > 0) {
                            armor--;
                        } else {
                            health--;
                        }
                        Tank.gs.playSound("Resources/Sound/ExpS.wav");
                        break;
                    case "P2HitBounce":
                        if (armor > 0) {
                            armor--;
                        } else {
                            health--;
                        }
                        Tank.gs.playSound("Resources/Sound/ExpS.wav");
                        break;
                    case "P2HitRocket":
                        if (armor > 0) {
                            armor -= 4;
                        } else {
                            health -= 4;
                        }
                        Tank.gs.playSound("Resources/Sound/ExpS.wav");
                        break;
                }
            }
            switch (msg) {
                case "P2Crashed":
                    x = oldX;
                    y = oldY;
                    break;
                case "P2GotBounce":
                    powerUp = 1;
                    ammo = 16;
                    break;
                case "P2GotRocket":
                    powerUp = 2;
                    ammo = 8;
                    break;
                case "P2GotShield":
                    armor = 8;
                    break;
                case "P2GotHealth":
                    health += 4;
                    if (health > 16) {
                        health = 16;
                    }
                    break;
            }

            if (health <= 0) {
                health = fireTime = armor = ammo = powerUp = 0;
                boom = true;
                Tank.gs.playSound("Resources/Sound/ExpL.wav");
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

    public boolean isDestroyed() {
        return boom;
    }

    public boolean isDead() {
        if (lives == 0) {
            return true;
        }
        return false;
    }

    public void setScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getRad() {
        return rad;
    }

    public int getPowerUp() {
        return powerUp;
    }

    public void reset() {
        x = 684;
        y = 688;
        spawnTime = fireTime = armor = ammo = powerUp = 0;
        health = 16;
        lives = 3;
        angle = 0;
        boom = false;
        ready = true;
    }
}