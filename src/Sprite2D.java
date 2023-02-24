import java.awt.*;

public abstract class Sprite2D {
    protected double x,y;
    protected double xSpeed = 0;
    protected double ySpeed = 0;
    protected int width, height;
    protected static final double DEFAULT_X = 375, DEFAULT_Y = 550;   // player start position
    protected final Image spriteImage;
    protected final Image spriteImageAlternate;
    protected int frameCycle = 0; // used to alternate between sprites
    protected boolean exists = true;   // used to "kill" aliens and bullets

    public Sprite2D(Image img) {
        // default x and y, and single image
        this(DEFAULT_X, DEFAULT_Y, img);
    }

    public Sprite2D(double startingX, double startingY, Image img) {
        // specified x and y, and single image
        this(startingX, startingY, img, img);
    }

    public Sprite2D(double startingX, double startingY, Image img, Image alt) {
        // specified x and y, and double image
        spriteImage = img;
        spriteImageAlternate = alt;
        x = startingX;
        y = startingY;
        getImageDimensions(0);  // start with default image
    }

    // public methods
    public void setPosition(double x, double y) {
        // not useful yet, will be later
        this.x = x;
        this.y = y;
    }

    public void getImageDimensions(int img) {
        switch (img) {
            case 0 -> {
                width = spriteImage.getWidth(null);
                height = spriteImage.getHeight(null);
            }
            case 1 -> {
                width = spriteImageAlternate.getWidth(null);
                height = spriteImageAlternate.getHeight(null);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getX() { return x; }

    public double getY() { return y; }

    public void setxSpeed(double deltaX) {
        xSpeed = deltaX;
    }

    public double getxSpeed() { return xSpeed; }

    public void setySpeed(double deltaY) {ySpeed = deltaY; }

    public boolean getExists() { return exists; }

    public void setExists(boolean b) { exists = b; }

    public void paint(Graphics g) {
        frameCycle++;
        if (frameCycle % 50 < 25) {
            g.drawImage(spriteImage, (int) x, (int) y, null);
        }
        else {
            g.drawImage(spriteImageAlternate, (int) x, (int) y, null);
        }

        if (frameCycle == 200) {
            frameCycle = 0; // so it doesn't continue counting forever
        }
    }
}
