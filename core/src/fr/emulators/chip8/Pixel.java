package fr.emulators.chip8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.awt.Point;

public class Pixel {

    public final Point location;
    public boolean color = false;
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;

    public Pixel(int x, int y){
        this.location = new Point(x, y);
    }

    public void draw(ShapeRenderer shapeRenderer){
        Color color = this.color ? Color.WHITE : Color.BLACK;
        shapeRenderer.rect(location.x*WIDTH, location.y*HEIGHT, WIDTH, HEIGHT, color, color, color, color);
    }

}
