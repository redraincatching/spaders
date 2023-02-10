import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InvadersApplication extends JFrame implements Runnable, KeyListener {
    private static final Dimension windowSize = new Dimension(750, 750);
    private static final int NOALIENS = 50;
    private final Sprite2D[] aliens = new Sprite2D[NOALIENS];
    private final Sprite2D player;
    private static String workingDirectory;



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

        // load images and initialise sprites
        workingDirectory = System.getProperty("user.dir");

        ImageIcon alienIcon = new ImageIcon(workingDirectory + "\\src\\alien_ship_1.png");
        ImageIcon shipIcon = new ImageIcon(workingDirectory + "\\src\\player_ship.png");
        player = new Sprite2D(325, 700,  shipIcon.getImage());

        for (int i = 0; i < NOALIENS; i++) {
            aliens[i] = new Sprite2D(alienIcon.getImage());
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
        do {
            // constantly attempt to move the player, whether the change to x will be 0 or not
            player.movePlayer();

            // move each alien randomly
            for (Sprite2D s : aliens) {
                s.moveEnemy();
            }
            this.repaint();

            // sleep for 20 ms
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (true);
    }

    // paint method
    public void paint (Graphics g) {
        // draw a black rectangle on the whole canvas
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, windowSize.width, windowSize.height);

        // iterating through each sprite, moving it, and calling its paint method
        for (int i = 0; i < NOALIENS; i ++) {
            aliens[i].paint(g);
        }

        player.paint(g);
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

}
