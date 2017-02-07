package Tank;


import java.awt.event.*;

public class KeyControl extends KeyAdapter {

    private int key;

    public void keyPressed(KeyEvent e) {
        key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            Tank.p1.left = true;
        }
        if (key == KeyEvent.VK_RIGHT) {
            Tank.p1.right = true;
        }
        if (key == KeyEvent.VK_UP) {
            Tank.p1.up = true;
        }
        if (key == KeyEvent.VK_DOWN) {
            Tank.p1.down = true;
        }
        if (key == KeyEvent.VK_CONTROL) {
            Tank.p1.fire = true;
        }
        if (key == KeyEvent.VK_A) {
            Tank.p2.left = true;
        }
        if (key == KeyEvent.VK_D) {
            Tank.p2.right = true;
        }
        if (key == KeyEvent.VK_W) {
            Tank.p2.up = true;
        }
        if (key == KeyEvent.VK_S) {
            Tank.p2.down = true;
        }
        if (key == KeyEvent.VK_SHIFT) {
            Tank.p2.fire = true;
        }
        
        if (key == KeyEvent.VK_ENTER && Tank.r.isGameOver()) {
            Tank.r.restart();
        }
    }

    public void keyReleased(KeyEvent e) {
        key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            Tank.p1.left = false;
        }
        if (key == KeyEvent.VK_RIGHT) {
            Tank.p1.right = false;
        }
        if (key == KeyEvent.VK_UP) {
            Tank.p1.up = false;
        }
        if (key == KeyEvent.VK_DOWN) {
            Tank.p1.down = false;
        }
        if (key == KeyEvent.VK_CONTROL) {
            Tank.p1.fire = false;
        }
        if (key == KeyEvent.VK_A) {
            Tank.p2.left = false;
        }
        if (key == KeyEvent.VK_D) {
            Tank.p2.right = false;
        }
        if (key == KeyEvent.VK_W) {
            Tank.p2.up = false;
        }
        if (key == KeyEvent.VK_S) {
            Tank.p2.down = false;
        }
        if (key == KeyEvent.VK_SHIFT) {
            Tank.p2.fire = false;
        }
    }
}