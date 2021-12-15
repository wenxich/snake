package org.cis120.snake;

import java.awt.*;
import java.util.LinkedList;

public class Snake extends GameObj {
    public static final int SIZE = 10;
    public static final int INIT_POS_X = 20;
    public static final int INIT_POS_Y = 20;
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 0;

    private int snakeVX = 3;
    private int snakeVY = 3;

    private int px = getPx();
    private int py = getPy();

    public Snake(int boardWidth, int boardHeight) {
        super(INIT_VEL_X, INIT_VEL_Y, INIT_POS_X, INIT_POS_Y, SIZE, SIZE, boardWidth, boardHeight);
    }

    public Snake(int positionX, int positionY, int boardWidth,
                 int boardHeight, LinkedList<Point> objs) {
        super(INIT_VEL_X, INIT_VEL_Y, positionX, positionY,
                SIZE, SIZE, boardWidth, boardHeight, objs);
    }

    public int getSnakeVX() {
        return snakeVX;
    }
    public int getSnakeVY() {
        return snakeVY;
    }

    public void setSnakeVX(int velocity) {
        snakeVX = velocity;
    }

    public void setSnakeVY(int velocity) {
        snakeVY = velocity;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.black);
        for (Point p : getGameObjects()) {
            g.fillRect(p.x, p.y, 10, 10);
        }
    }

    @Override
    public void move() {
        for (int i = getGameObjects().size() - 1; i >= 1; i--) {
            getGameObjects().get(i).setLocation(getGameObjects().get(i - 1));
        }
        setPx(px + getVx());
        setPy(py + getVy());
        px = getPx();
        py = getPy();
        getGameObjects().set(0, new Point(px, py));

        clip();
    }

    public void grow(int length) {
        for (int i = 0; i < length; i++) {
            LinkedList<Point> newGameObjects;
            newGameObjects = getGameObjects();
            newGameObjects.add(new Point(getGameObjects().getLast()));
            setGameObjects(newGameObjects);
        }
    }

    public boolean hasHitItself() {
        if (getGameObjects().size() > 1) {
            for (int i = 1; i < getGameObjects().size(); i++) {
                if ((getGameObjects().getFirst().x + getVx() == getGameObjects().get(i).x)
                        && (getGameObjects().getFirst().y + getVy() == getGameObjects().get(i).y)) {
                    return true;
                }
            }
        }
        return false;
    }
}
