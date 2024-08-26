package core;

import tileengine.TETile;
import tileengine.Tileset;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        char option = input.charAt(0);
        StringBuilder seedString = new StringBuilder();
        StringBuilder remainingInput = new StringBuilder();
        long seed = 0;

        if (option == 'N') {
            for (int i = 1; i < input.length(); i++) {
                if (input.charAt(i) == 'S') {
                    remainingInput.append(input.substring(i + 1));
                    break;
                }
                seedString.append(input.charAt(i));
            }
            seed = Long.parseLong(seedString.toString());
        } else if (option == 'L') {
            remainingInput.append(input.substring(1));
            CreateWorldFeatures loadedWorld = CreateWorldFeatures.loadWorld();
            if (loadedWorld != null) {
                return processMovement(loadedWorld, remainingInput.toString());
            } else {
                throw new IllegalArgumentException("No saved world exists.");
            }
        }
        TETile avatar = Tileset.HEART;
        String resource = "Cotton Candy";
        CreateWorldFeatures world = new CreateWorldFeatures(seed, avatar, resource);
        return processMovement(world, remainingInput.toString());
    }

    /**
     * Helper method to process remaining input for the generated world.
     *
     * @param world generated from string or load
     * @param movement remaining actions
     * @return the 2D TETile[][] representing the state of the world
     */
    private static TETile[][] processMovement(CreateWorldFeatures world, String movement) {
        TETile[][] worldTiles = world.getWorld();
        Avatar avatar = world.getAvatar();

        for (int i = 0; i < movement.length(); i++) {
            char key = movement.charAt(i);
            switch (key) {
                case 'W':
                    avatar.moveUp(worldTiles);
                    break;
                case 'A':
                    avatar.moveLeft(worldTiles);
                    break;
                case 'S':
                    avatar.moveDown(worldTiles);
                    break;
                case 'D':
                    avatar.moveRight(worldTiles);
                    break;
                case ':':
                    if (i + 1 < movement.length() && movement.charAt(i + 1) == 'Q') {
                        world.saveWorld();
                        return worldTiles;
                    }
                    break;
                default:
                    break;
            }
        }
        return worldTiles;
    }

    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.HEART.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
