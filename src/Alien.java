import java.awt.*;

public class Alien extends Sprite2D {

    public Alien(double startingX, double startingY, Image img) {
        super(startingX, startingY, img);
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

    public boolean checkCollision() {
        return x + width >= 750 || x <= 0;
    }
}
