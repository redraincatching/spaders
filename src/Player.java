import java.awt.*;

public class Player extends Sprite2D{
    public Player(Image img) {
        super(img);
    }

    public void move() {
        x += xSpeed;
    }
}
