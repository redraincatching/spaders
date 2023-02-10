import java.awt.*;

public class Bullet extends Sprite2D{
    public Bullet(double startingX, double startingY, Image img) {
        super(startingX, startingY, img);
    }

    public void move() {
        y += ySpeed;
    }
}
