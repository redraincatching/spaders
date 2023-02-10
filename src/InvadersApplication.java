import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Arrays;

public class InvadersApplication extends JFrame implements Runnable, KeyListener {
    private static final Dimension windowSize = new Dimension(750, 750);
    private BufferStrategy strategy;
    private static final int NOALIENS = 50; // CAN'T BE A PRIME NUMBER
    private final Alien[] aliens = new Alien[NOALIENS];
    private final Player player;


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
        ImageIcon shipIcon = new ImageIcon(workingDirectory + "\\src\\images\\ct255-images\\player_ship.png");
        player = new Player(shipIcon.getImage());

        // find factors of NOALIENS as close to midpoint as possible, then arrange them into grid
        int cols = getFactors(NOALIENS);
        // more columns than rows, ideally

        // x position is BASE + STEP*(column_no)
        // y position is BASE + STEP*(row_no)
        for (int i = 0; i < NOALIENS; i++) {
            aliens[i] = new Alien(
                    100 + 65*(i%cols),
                    100 + 50*(Math.floor((float) i/cols)),
                    alienIcon.getImage());
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
        boolean collision = false;
        do {
            // constantly attempt to move the player, whether the change to x will be 0 or not
            player.move();

            // move each alien
            for (Alien a : aliens) {
                if (a.checkCollision()) {
                    collision = true;
                    break;
                }
            }
            for (Alien a : aliens) {
                if (collision) {
                    a.setxSpeed(-a.getxSpeed());
                    a.setySpeed(10);
                }
                a.move();
            }

            // repaint
            this.repaint();

            // sleep for 20 ms
            try {
                // busy waiting but i don't really care
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            collision = false;
        } while (true);
    }

    // paint method
    public void paint (Graphics g) {
        // redirect drawing calls to offscreen buffer
        g = strategy.getDrawGraphics();

        // draw a black rectangle on the whole canvas
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, windowSize.width, windowSize.height);

        // iterating through each sprite, moving it, and calling its paint method
        for (int i = 0; i < NOALIENS; i ++) {
            aliens[i].paint(g);
        }

        player.paint(g);

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

        switch (key) {
            case KeyEvent.VK_RIGHT -> player.setxSpeed(10);
            case KeyEvent.VK_LEFT -> player.setxSpeed(-10);
            default -> System.out.println("unknown key pressed: " + e.getKeyChar());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.setxSpeed(0);
    }


    // other helper functions
    static int getFactors (int n)
    {
        // this is a little wonky but anyway
        ArrayList<Integer> factors = new ArrayList<>();

        // just make sure there's space
        for(int i = 1; i <= Math.sqrt(n); ++i)
        {
            if (n % i == 0){
                factors.add(i);
                factors.add(n/i);
            }
        }

        // return highest of the two middle factors
        return Math.max(factors.get(factors.size()-1), factors.get(factors.size()-2));
    }
}


