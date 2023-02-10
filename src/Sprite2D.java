import java.awt.*;
import java.util.Random;

public abstract class Sprite2D {
    protected double x,y;
    protected double xSpeed = 0;
    protected double ySpeed = 0;
    protected int width, height;
    protected static final double DEFAULT_X = 325, DEFAULT_Y = 700;   // player start position
    protected final Random rand = new Random();
    protected final Image spriteImage;

    public Sprite2D(Image img) {
        this(DEFAULT_X, DEFAULT_Y, img);
    }

    public Sprite2D(double startingX, double startingY, Image img) {
        spriteImage = img;
        x = startingX;
        y = startingY;
        getImageDimensions();
    }

    // public methods
    public void setPosition(double x, double y) {
        // not useful yet, will be later
        this.x = x;
        this.y = y;
    }

    public void getImageDimensions() {
        width = spriteImage.getWidth(null);
        height = spriteImage.getHeight(null);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setxSpeed(double deltaX) {
        xSpeed = deltaX;
    }

    public double getxSpeed() { return xSpeed; }

    public void setySpeed(double deltaY) {ySpeed = deltaY; }

    public void paint(Graphics g) {
        g.drawImage(spriteImage, (int) x, (int) y, null);
    }
}
