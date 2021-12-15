package org.cis120.snake;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class GoldenApple extends GameObj implements Food {
    public static final int SIZE = 20;
    public static final int INIT_POS_X = (int)(Math.random() * 560);
    public static final int INIT_POS_Y = (int)(Math.random() * 360);
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 0;

    private BufferedImage goldenAppleImg;

    public GoldenApple(int boardWidth, int boardHeight) {
        super(INIT_VEL_X, INIT_VEL_Y, INIT_POS_X, INIT_POS_Y, SIZE, SIZE, boardWidth, boardHeight);
        try {
            goldenAppleImg = ImageIO.read(new File("files/goldenapple.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GoldenApple(int positionX, int positionY, int boardWidth,
                       int boardHeight, LinkedList<Point> objs) {
        super(INIT_VEL_X, INIT_VEL_Y, positionX, positionY, SIZE,
                SIZE, boardWidth, boardHeight, objs);
        try {
            goldenAppleImg = ImageIO.read(new File("files/goldenapple.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void powerUp(Snake snake) {
        snake.setSnakeVX(snake.getSnakeVX() + 1);
        snake.setSnakeVY(snake.getSnakeVY() + 1);
    }

    @Override
    public void draw(Graphics g) {
        for (Point p : getGameObjects()) {
            g.drawImage(goldenAppleImg, p.x, p.y, 20, 20, null);
        }
    }

    @Override
    public boolean intersects(GameObj that) {
        for (int i = 0; i < getGameObjects().size(); i++) {
            Point p = getGameObjects().get(i);
            if (p.x + getWidth() >= that.getPx()
                    && p.y + getHeight() >= that.getPy()
                    && that.getPx() + that.getWidth() >= p.x
                    && that.getPy() + that.getHeight() >= p.y) {
                remove(i);
                return true;
            }
        }
        return false;
    }
}
