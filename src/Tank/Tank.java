package Tank;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

public class Tank extends JApplet implements Runnable {

    private int x1, y1, x2, y2, // pos. of p1 and p2
            rLim, bLim, tlLim = 5; // viewing boundary limits
    static int mapSize; // size of map
    private Thread thread, me;
    private BufferedImage bimg, // image displayed on screen
            map, view1, view2; // map, split screen views (drawn offscreen)
    private Random generator = new Random(); // random power up pos.
    static Player1 p1;
    static Player2 p2;
    static LinkedList<PlayerBullet> pBullet1 = new LinkedList();
    static LinkedList<PlayerBullet> pBullet2 = new LinkedList();
    static LinkedList<Wall> walls = new LinkedList();
    static LinkedList<BreakableWall> bwalls = new LinkedList();
    static LinkedList<PowerUp> pow = new LinkedList();
    static GameEvents gameEvents;
    private Image t1, t2, // tank images
            bpwr, rpwr, spwr, hp, // powerup images
            wall, bwall; // wall images
    private KeyControl key;
    private Graphics2D g2;
    private Dimension d;
    static Renderer r = new Renderer();
    static GameSounds gs = new GameSounds();
    private Font score = new Font("SanSerif", Font.BOLD, 128);
    private Font bText = new Font("SanSerif", Font.BOLD, 48);
    private Font sText = new Font("SanSerif", Font.BOLD, 36);

    public void init() {
        setBackground(Color.black);

        mapSize = 928;
        rLim = mapSize - 320;
        bLim = mapSize - 485;

        t1 = r.getSprite("Resources/T1/T1-30.png"); // p1 image
        t2 = r.getSprite("Resources/T2/T2-00.png"); // p2 image

        bpwr = r.getSprite("Resources/Element/BPickUp.png"); // bounce
        rpwr = r.getSprite("Resources/Element/RPickUp.png"); // rocket
        spwr = r.getSprite("Resources/Element/SPickUp.png"); // shield
        hp = r.getSprite("Resources/Element/HPickUp.png"); // health

        wall = r.getSprite("Resources/Element/Wall1.png");
        bwall = r.getSprite("Resources/Element/Wall2.png");

        gs.playSound("Resources/Sound/Music.wav"); // bgm

        p1 = new Player1(t1, 172, 176);
        p2 = new Player2(t2, 684, 688);

        pow.add(new PowerUp(hp, generator.nextInt(mapSize - 40), generator.nextInt(mapSize - 40), 0, generator));
        pow.add(new PowerUp(spwr, generator.nextInt(mapSize - 40), generator.nextInt(mapSize - 40), 1, generator));
        pow.add(new PowerUp(bpwr, generator.nextInt(mapSize - 40), generator.nextInt(mapSize - 40), 2, generator));
        pow.add(new PowerUp(rpwr, generator.nextInt(mapSize - 40), generator.nextInt(mapSize - 40), 3, generator));

        buildWalls();
        buildBWalls();

        gameEvents = new GameEvents();
        gameEvents.addObserver(p1);
        gameEvents.addObserver(p2);

        key = new KeyControl();
        addKeyListener(key);

        setFocusable(true);
    }

    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public void run() {
        me = Thread.currentThread();
        while (thread == me) {
            repaint();

            try {
                thread.sleep(17); // approximately 60 FPS
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    // for better double buffering - paint twice after repaint
    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        d = getSize();

        // double buffering - draw map, split screen images offscreen
        // if no double buffering and draw directly = flickering
        g2 = createMap(mapSize, mapSize);
        r.drawFull(mapSize, mapSize, g2);

        // redetermine viewing positions, limits
        x1 = p1.getX() - 144;
        y1 = p1.getY() - 144;
        x2 = p2.getX() - 144;
        y2 = p2.getY() - 144;

        if (x1 <= tlLim && (y1 <= tlLim)) {
            x1 = y1 = tlLim;
        } else if (x1 >= rLim && y1 >= bLim) {
            x1 = rLim;
            y1 = bLim;
        } else if ((x1 <= tlLim) && (y1 >= bLim)) {
            x1 = tlLim;
            y1 = bLim;
        } else if ((x1 >= rLim) && (y1 <= tlLim)) {
            x1 = rLim;
            y1 = tlLim;
        } else if (x1 <= tlLim) {
            x1 = tlLim;
        } else if (x1 >= rLim) {
            x1 = rLim;
        } else if (y1 <= tlLim) {
            y1 = tlLim;
        } else if (y1 >= bLim) {
            y1 = bLim;
        }

        if (x2 <= tlLim && (y2 <= tlLim)) {
            x2 = y2 = tlLim;
        } else if (x2 >= rLim && y2 >= bLim) {
            x2 = rLim;
            y2 = bLim;
        } else if ((x2 <= tlLim) && (y2 >= bLim)) {
            x2 = tlLim;
            y2 = bLim;
        } else if ((x2 >= rLim) && (y2 <= tlLim)) {
            x2 = rLim;
            y2 = tlLim;
        } else if (x2 <= tlLim) {
            x2 = tlLim;
        } else if (x2 >= rLim) {
            x2 = rLim;
        } else if (y2 <= tlLim) {
            y2 = tlLim;
        } else if (y2 >= bLim) {
            y2 = bLim;
        }

        // draw split screen views offscreen
        view1 = map.getSubimage(x1, y1, (d.width / 2) - 5, d.height);
        view2 = map.getSubimage(x2, y2, (d.width / 2) - 5, d.height);
        g2.dispose();

        // draw eventual onscreen image offscreen
        g2 = createGraphics2D(d.width, d.height);
        g2.drawImage(view1, 0, 0, this); // p1's view
        g2.drawImage(view2, (d.width / 2) + 5, 0, this); // p2's view
        g2.drawImage(map, 240, 280, 160, 160, this); // minimap

        g2.setColor(Color.WHITE);
        g2.setFont(sText);

        // showing scores; who won and who lost at game over
        if (!r.isGameOver()) {
            g2.drawString("Wins: " + Integer.toString(p1.getScore()), 5, 40);
            g2.drawString("Wins: " + Integer.toString(p2.getScore()), 330, 40);
        } else {
            g2.drawString("Current Wins", 45, 180);
            g2.drawString("Current Wins", 360, 180);
            g2.drawString("Press ENTER to play again.", 90, 390);

            g2.setFont(bText);
            if (r.getResult()) {
                g2.drawString("YOU LOSE!", 30, 120);
                g2.drawString("YOU WIN!", 360, 120);
            } else {
                g2.drawString("YOU WIN!", 45, 120);
                g2.drawString("YOU LOSE!", 345, 120);
            }

            g2.setFont(score);
            g2.drawString(Integer.toString(p1.getScore()), 45, 315);
            g2.drawString(Integer.toString(p2.getScore()), 345, 315);
        }

        g2.dispose();

        // draw complete offscreen image onscreen (all images are now on bimg)
        g.drawImage(bimg, 0, 0, this);
    }

    // for drawing onscreen image (bimg)
    private Graphics2D createGraphics2D(int w, int h) {
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
            bimg = (BufferedImage) createImage(w, h);
        }
        g2 = bimg.createGraphics();
        g2.setColor(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.clearRect(0, 0, w, h);
        return g2;
    }

    // for drawing offscreen image (map, view1, view2) for double buffering
    private Graphics2D createMap(int w, int h) {
        if (map == null || map.getWidth() != w || map.getHeight() != h) {
            map = (BufferedImage) createImage(w, h);
        }
        g2 = map.createGraphics();
        g2.setColor(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.clearRect(0, 0, w, h);
        return g2;
    }

    private void buildWalls() {
        for (int i = -4; i <= mapSize - 36; i += 32) {
            walls.add(new Wall(wall, i, 0));
            walls.add(new Wall(wall, i, mapSize - 32));
        }

        for (int i = 32; i < mapSize - 32; i += 32) {
            walls.add(new Wall(wall, -4, i));
            walls.add(new Wall(wall, mapSize - 36, i));
        }

        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 6; j++) {
                walls.add(new Wall(wall, (128 * j) - 4, 128 * i));
            }
        }

        for (int i = 1; i <= 2; i++) {
            for (int j = 416; j <= 480; j += 32) {
                walls.add(new Wall(wall, j - 4, 128 * i));
                walls.add(new Wall(wall, j - 4, (128 * i) + 512));
                walls.add(new Wall(wall, (128 * i) - 4, j));
                walls.add(new Wall(wall, (128 * i) + 508, j));
            }
        }
    }

    private void buildBWalls() {
        for (int i = 1; i <= 2; i++) {
            for (int j = 160; j <= 224; j += 32) {
                bwalls.add(new BreakableWall(bwall, j - 4, 128 * i));
                bwalls.add(new BreakableWall(bwall, j - 4, (128 * i) + 512));
                bwalls.add(new BreakableWall(bwall, (128 * i) - 4, j));
                bwalls.add(new BreakableWall(bwall, (128 * i) + 508, j));
            }

            for (int j = 672; j <= 736; j += 32) {
                bwalls.add(new BreakableWall(bwall, j - 4, 128 * i));
                bwalls.add(new BreakableWall(bwall, j - 4, (128 * i) + 512));
                bwalls.add(new BreakableWall(bwall, (128 * i) - 4, j));
                bwalls.add(new BreakableWall(bwall, (128 * i) + 508, j));
            }

            for (int j = 0; j <= 6; j += 2) {
                for (int k = 32; k <= 96; k += 32) {
                    bwalls.add(new BreakableWall(bwall, (128 * j) + k - 4, (128 * i) + 256));
                    bwalls.add(new BreakableWall(bwall, (128 * i) + 252, (128 * j) + k));
                }
            }
        }
    }

    public static void main(String argv[]) {
        final Tank tank = new Tank();
        tank.init();
        JFrame f = new JFrame("TankWars");
        f.addWindowListener(new WindowAdapter() {
        });
        f.getContentPane().add("Center", tank);
        f.pack();
        f.setSize(new Dimension(640, 480));
        f.setVisible(true);
        f.setResizable(false);
        f.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        tank.start();
    }
}