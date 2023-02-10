import java.awt.*;
import java.util.Random;

public class Sprite2D {
    private double x,y;
    private double xSpeed = 0;
    private static final double DEFAULT_X = 150, DEFAULT_Y = 150;
    private final Random rand = new Random();
    private final Image spriteImage;

    public Sprite2D(Image img) {
        this(DEFAULT_X, DEFAULT_Y, img);
    }

    public Sprite2D(double startingX, double startingY, Image img) {
        spriteImage = img;
        x = startingX;
        y = startingY;
    }

    // public methods
    public void moveEnemy() {
        // real basic but i don't care
        x = (rand.nextInt(0, 750));
        y = (rand.nextInt(0, 650));
    }

    public void movePlayer() {
        x += xSpeed;
    }

    public void setPosition(double x, double y) {
        // not useful yet, will be later
        this.x = x;
        this.y = y;
    }

    public void setxSpeed(double deltaX) {
        xSpeed = deltaX;
    }

    public void paint(Graphics g) {
        g.drawImage(spriteImage, (int) x, (int) y, null);
    }
}
