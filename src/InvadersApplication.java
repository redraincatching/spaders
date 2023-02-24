import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.util.ArrayList;

public class InvadersApplication extends JFrame implements Runnable, KeyListener {
    private static final Dimension windowSize = new Dimension(800, 600);
    private BufferStrategy strategy;
    private boolean inProgress = false, gameWon = false, gameLost = false;
    // 4 states: start, running, win, lose
    private static final int NOALIENS = 50; // CAN'T BE A PRIME NUMBER
    private final Alien[] aliens = new Alien[NOALIENS];
    private final Bullet[] bullets = new Bullet[3];   // max 3 bullets at once, think that's how classic space invaders worked
    private final Player player;
    private int timer = 0, score = 0, remainingEnemies = NOALIENS;


    // constructor
    public InvadersApplication() {
        // adding a keyListener
        addKeyListener(this);

        // create and set up the window
        this.setTitle("spaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // display the window, centred on the screen
        Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screensize.width/2 - windowSize.width/2;
        int y = screensize.height/2 - windowSize.height/2;
        setBounds(x, y, windowSize.width, windowSize.height);
        setVisible(true);

        // init buffer
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        // load images and initialise sprites
        String workingDirectory = System.getProperty("user.dir");

        ImageIcon alienIcon = new ImageIcon(workingDirectory + "\\src\\images\\ct255-images\\alien_ship_1.png");
        ImageIcon alienAltIcon = new ImageIcon (workingDirectory + "\\src\\images\\ct255-images\\alien_ship_2.png");
        ImageIcon shipIcon = new ImageIcon(workingDirectory + "\\src\\images\\ct255-images\\player_ship.png");
        ImageIcon bulletIcon = new ImageIcon(workingDirectory + "\\src\\images\\ct255-images\\bullet.png");
        player = new Player(shipIcon.getImage());

        for (int i = 0; i < bullets.length; i++) {
            bullets[i] = new Bullet(bulletIcon.getImage());
        }


        // find factors of NOALIENS as close to midpoint as possible, then arrange them into grid
        int cols = getFactors();
        // more columns than rows, ideally

        // x position is BASE + STEP*(column_no)
        // y position is BASE + STEP*(row_no)
        for (int i = 0; i < NOALIENS; i++) {
            aliens[i] = new Alien(
                    100 + 65*(i%cols),
                    100 + 50*(Math.floor((float) i/cols)),
                    alienIcon.getImage(), alienAltIcon.getImage());
        }

        // start animation thread
        Thread t = new Thread(this);
        t.start();
    }

    // main
    public static void main(String[] args) {
        InvadersApplication app = new InvadersApplication();
    }


    // thread's entry point
    @Override
    public void run() {
        boolean collisionWall = false;
        do {
            if (!inProgress) {
                // just repaint and sleep, easy
                this.repaint();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {

                // if none of the aliens are left, you win
                if (remainingEnemies == 0) {
                    gameWon = true;
                    inProgress = false;
                }

                // if any aliens hit the player, you lose
                if (playerCollision()) {
                    gameLost = true;
                    inProgress = false;
                }

                // constantly attempt to move the player, whether the change to x will be 0 or not
                player.move();

                // move bullets or delete them if they hit the top of the screen
                moveBullets();


                // bullet collision checking
                // this is early as if aliens don't exist, they skip some later checks
                bulletCollision();

                // move each alien
                collisionWall = alienCollisionWall();

                moveAliens(collisionWall);

                timer++;

                // repaint
                this.repaint();

                // sleep for 100 ms
                try {
                    // busy waiting but i don't really care
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                collisionWall = false;
            }

        } while (true);
    }

    // paint method
    public void paint (Graphics g) {
        // redirect drawing calls to offscreen buffer
        g = strategy.getDrawGraphics();

        // draw a black rectangle on the whole canvas
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, windowSize.width, windowSize.height);
        g.setColor(Color.WHITE);

        if (inProgress) {
            // timer and score
            g.drawString("score: " + score, windowSize.width/4, 50);
            g.drawString("timer: " + timer, (windowSize.width/4) * 3, 50);

            paintAliens(g);
            paintBullets(g);

            player.paint(g);
        }
        else if (gameWon) {
            // win screen
            g.drawString("conglaturation", windowSize.width/3, windowSize.height/2);
            g.drawString("score: " + score, windowSize.width/4, 50);
            g.drawString("timer: " + timer, (windowSize.width/4) * 3, 50);
        }
        else if (gameLost) {
            // lose screen
            g.drawString("you died", windowSize.width/3, windowSize.height/2);
        }
        else {
            g.drawString("legally distinct space alien game", windowSize.width/3, windowSize.height/2);
            g.drawString("press any key to start", windowSize.width/3, windowSize.height/2 + 70);
        }
        // flip buffers
        strategy.show();
    }


    // key-based event handlers
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (inProgress && !gameWon) {
            switch (key) {
                case KeyEvent.VK_RIGHT -> player.setxSpeed(10);
                case KeyEvent.VK_LEFT -> player.setxSpeed(-10);
                case KeyEvent.VK_SPACE -> {
                    // first bullet in array that doesn't exist gets added
                    for (Bullet b : bullets) {
                        if (!b.getExists()) {
                            // has to be at middle and slightly above player
                            b.fire(player.getX() + ((float) player.getWidth() / 2), player.getY() - b.getHeight());
                            break;
                        }
                    }
                }
                default -> System.out.println("unknown key pressed: " + e.getKeyChar());
            }
        }
        else if (!gameLost && !gameWon) {
            inProgress = true;  // press any key to start
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // have to do it like this so firing the bullet doesn't stop the ship
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT -> player.setxSpeed(0);
        }
    }

    // running functions
    public void moveBullets() {
        for (Bullet b : bullets) {
            if (b.getExists()) {
                b.move();
                // wall collision, enemy is handled by aliens
                if (b.checkCollision()) {
                    b.setExists(false);
                }
            }
        }
    }

    public void moveAliens(boolean collisionWall) {
        // move aliens unless one hits a wall, at which point change direction and drop down a row
        for (Alien a : aliens) {
            if (a.getExists()) {
                if (collisionWall) {
                    a.setxSpeed(-a.getxSpeed());
                    a.setySpeed(10);
                }
                a.move();
            }
        }
    }

    public boolean alienCollisionWall() {
        // check if any alien has hit a wall
        for (Alien a : aliens) {
            if (a.getExists()) {
                if (a.checkCollision()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void bulletCollision() {
        // for every alien, check if a bullet is currently hitting it
        // if yes, they both disappear
        for (Alien a : aliens) {
            if (a.getExists()) {
                for (Bullet b : bullets) {
                    if (b.getExists()) {
                        if (a.checkCollision(b)) {
                            a.setExists(false);
                            b.setExists(false);
                            remainingEnemies --;
                            score += 100;
                        }
                    }
                }
            }
        }
    }

    public boolean playerCollision() {
        for (Alien a : aliens) {
            if (a.checkCollision(player)) {
                return true;
            }
        }
        return false;
    }

    // painting functions
    public void paintAliens(Graphics g) {
        // iterating through each sprite, moving it, and calling its paint method
        // only paint if the alien exists
        for (int i = 0; i < NOALIENS; i ++) {
            if (aliens[i].getExists()) {
                aliens[i].paint(g);
            }
        }
    }

    public void paintBullets(Graphics g) {
        // same as paintAliens for bullets
        for (Bullet b : bullets) {
            if (b.getExists()) {
                b.paint(g);
            }
        }
    }

    // other helper functions
    static int getFactors () {
        // this is a little wonky but anyway
        ArrayList<Integer> factors = new ArrayList<>();

        // just make sure there's space
        for(int i = 1; i <= Math.sqrt(InvadersApplication.NOALIENS); ++i)
        {
            if (InvadersApplication.NOALIENS % i == 0){
                factors.add(i);
                factors.add(InvadersApplication.NOALIENS /i);
            }
        }

        // return highest of the two middle factors
        return Math.max(factors.get(factors.size()-1), factors.get(factors.size()-2));
    }
}


