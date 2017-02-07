package Tank;

import java.awt.*;
import java.net.URL;

public class Renderer extends Component {

    private boolean gameOver = false, // is game over?
            reset = false, // if reset button pressed
            result; // true = p2 wins; false = p1 wins
    private Image img, bg = getSprite("Resources/Element/Background.png");
    private URL url;
    private MediaTracker tracker;

    Renderer() {
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean getResult() {
        return result;
    }

    public void setGameOver(boolean result) {
        gameOver = true;
        this.result = result;

        if (result) {
            Tank.p2.setScore();
        } else {
            Tank.p1.setScore();
        }
    }

    // for resetting game's parameters for new game
    public void restart() {
        reset = true;
    }

    // reset parameters
    private void reset() {
        Tank.p1.reset();
        Tank.p2.reset();
        Tank.pBullet1.clear();
        Tank.pBullet2.clear();

        for (int i = 0; i < Tank.pow.size(); i++) {
            Tank.pow.get(i).reset();
        }
        
        for (int i = 0; i < Tank.bwalls.size(); i++) {
            Tank.bwalls.get(i).reset();
        }

        gameOver = reset = false;
    }

    public Image getSprite(String name) {
        url = Tank.class.getResource(name);
        img = getToolkit().getImage(url);

        try {
            tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {
        }
        return img;
    }

    // draw all objects, images, etc.
    // continuously update all images, objects
    public void drawFull(int w, int h, Graphics2D g2) {
        g2.drawImage(bg, 0, 0, w, h, this); // bg image of ground

        for (int i = 0; i < Tank.pow.size(); i++) {
            Tank.pow.get(i).update();
            Tank.pow.get(i).draw(g2, this);
        }

        for (int i = 0; i < Tank.walls.size(); i++) {
            Tank.walls.get(i).update();
            Tank.walls.get(i).draw(g2, this);
        }
        
        for (int i = 0; i < Tank.bwalls.size(); i++) {
            Tank.bwalls.get(i).update();
            Tank.bwalls.get(i).draw(g2, this);
        }

        if (!Tank.pBullet1.isEmpty()) {
            for (int i = 0; i < Tank.pBullet1.size(); i++) {
                Tank.pBullet1.get(i).update();
                Tank.pBullet1.get(i).draw(g2, this);
            }

            if (Tank.pBullet1.getFirst().isDestroyed()) {
                Tank.pBullet1.removeFirst();
            }
        }

        if (!Tank.pBullet2.isEmpty()) {
            for (int i = 0; i < Tank.pBullet2.size(); i++) {
                Tank.pBullet2.get(i).update();
                Tank.pBullet2.get(i).draw(g2, this);
            }

            if (Tank.pBullet2.getFirst().isDestroyed()) {
                Tank.pBullet2.removeFirst();
            }
        }

        // players are not drawn at game over
        if (gameOver) {
            if (reset) { // reset if reset button pressed at game over
                reset();
            }
        } else {
            if (!Tank.p1.isDead()) {
                Tank.p1.update();
            }

            if (!Tank.p2.isDead()) {
                Tank.p2.update();
            }

            Tank.p1.draw(g2, this);
            Tank.p2.draw(g2, this);
        }
    }
}