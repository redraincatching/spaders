import java.awt.*;

public class Alien extends Sprite2D {

    public Alien(double startingX, double startingY, Image img, Image altImg) {
        super(startingX, startingY, img, altImg);
        xSpeed = 5;
        ySpeed = 5;
    }

    // methods
    public void move() {
        // real basic but i don't care
        x += xSpeed;
        y += ySpeed;
        if (ySpeed != 0) { setySpeed(0); };
    }

    // collision detection
    // these are overloaded
    public boolean checkCollision() {
        return x + width >= 750 || x <= 0;
    }


    public boolean checkCollision(Bullet b) {
        // if bullet x to x + width within bounds of x + width and bullet y to y + height within bounds of y + height
        return (
                ( (x <= b.getX() + b.getWidth()) && (x + width >= b.getX()) )
                    &&
                ( (y <= b.getY() + b.getHeight()) && (y + height >= b.getY()) )
        );
    }

    public boolean checkCollision(Player p) {
        // if bullet x to x + width within bounds of x + width and bullet y to y + height within bounds of y + height
        return (
                ( (x <= p.getX() + p.getWidth()) && (x + width >= p.getX()) )
                    &&
                ( (y <= p.getY() + p.getHeight()) && (y + height >= p.getY()) )
        );
    }
}
