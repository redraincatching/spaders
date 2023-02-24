import java.awt.*;

public class Bullet extends Sprite2D{
    public Bullet(Image img) {
        super(img);
        exists = false;
        ySpeed = -25;
    }

    public void move() {
        y += ySpeed;
    }

    public void fire(double x, double y) {
        // take player current x and y, then start moving
        this.x = x;
        this.y = y;
        exists = true;
    }

    public boolean checkCollision() {
        return y <= 0;
    }
}
