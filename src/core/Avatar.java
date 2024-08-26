package core;

import tileengine.*;
import java.io.Serializable;

public class Avatar implements Serializable {
    private final TETile character;
    private TETile previousTile;
    private int xPosition;
    private int yPosition;

    public Avatar(int initialX, int initialY, TETile initialTile, TETile character) {
        this.xPosition = initialX;
        this.yPosition = initialY;
        this.previousTile = initialTile;
        this.character = character;
    }

    /** Ensures the avatar does not move out of the world's bounds or onto a wall */
    public boolean isValidMove(int x, int y, TETile[][] world) {
        if (x >= 0 && x < world.length && y >= 0 && y < world[0].length) {
            TETile targetTile = world[x][y];
            return targetTile.equals(Tileset.TREE) || targetTile.equals(Tileset.GRASS)
                    || targetTile.equals(Tileset.SAND)
                    || targetTile.equals(Tileset.FLOWER) || targetTile.equals(Tileset.FLOOR);
        }
        return false;
    }

    /** Move the avatar up one tile */
    public void moveUp(TETile[][] world) {
        int newY = yPosition + 1;
        if (isValidMove(xPosition, newY, world)) {
            world[xPosition][yPosition] = previousTile;
            previousTile = world[xPosition][newY];
            yPosition = newY;
            world[xPosition][yPosition] = character;
        }
    }

    /** Move the avatar down one tile */
    public void moveDown(TETile[][] world) {
        int newY = yPosition - 1;
        if (isValidMove(xPosition, newY, world)) {
            world[xPosition][yPosition] = previousTile;
            previousTile = world[xPosition][newY];
            yPosition = newY;
            world[xPosition][yPosition] = character;
        }
    }

    /** Move the avatar left one tile */
    public void moveLeft(TETile[][] world) {
        int newX = xPosition - 1;
        if (isValidMove(newX, yPosition, world)) {
            world[xPosition][yPosition] = previousTile;
            previousTile = world[newX][yPosition];
            xPosition = newX;
            world[xPosition][yPosition] = character;
        }
    }

    /** Move the avatar right one tile */
    public void moveRight(TETile[][] world) {
        int newX = xPosition + 1;
        if (isValidMove(newX, yPosition, world)) {
            world[xPosition][yPosition] = previousTile;
            previousTile = world[newX][yPosition];
            xPosition = newX;
            world[xPosition][yPosition] = character;
        }
    }

    public int getX() {
        return xPosition;
    }

    public int getY() {
        return yPosition;
    }

    public TETile getCharacter() {
        return character;
    }
}
