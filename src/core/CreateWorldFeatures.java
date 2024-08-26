package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.*;
import utils.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class CreateWorldFeatures implements Serializable {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 45;
    private static final int SMALL_FONT = 20;
    private static final int HEADER_SIZE = 2;
    private static final int VISIBILITY_RADIUS = 3;
    private final BareBonesWorld BARE_BONES_WORLD;
    private boolean LINE_OF_SIGHT = false;
    private final TETile[][] WORLD;
    private TERenderer TER;
    private Avatar AVATAR;

    public CreateWorldFeatures(long seed, TETile avatar, String resource) { // , TETile avatar
        ResourcePack resourcePack = new ResourcePack(resource);
        ArrayList<TETile> selectedResource = resourcePack.chooseResourcePack();
        this.BARE_BONES_WORLD = new BareBonesWorld(seed, WIDTH, HEIGHT, selectedResource);
        this.WORLD = BARE_BONES_WORLD.initializeWorld();
        initializeRenderer();
        initializeAvatar(avatar);
    }

    private void initializeRenderer() {
        this.TER = new TERenderer();
        TER.initialize(WIDTH, HEIGHT);
    }

    /** Creates the avatar and randomly places it in one of the rooms */
    private void initializeAvatar(TETile avatar) {
        List<Point> allRoomFloors = BARE_BONES_WORLD.getAllRoomFloors();
        Point randomRoom = allRoomFloors.get(RandomUtils.uniform(new Random(), 0, allRoomFloors.size()));
        AVATAR = new Avatar(randomRoom.getX(), randomRoom.getY(), WORLD[randomRoom.getX()][randomRoom.getY()], avatar);
    }

    private void updateAvatar() {
        WORLD[AVATAR.getX()][AVATAR.getY()] = AVATAR.getCharacter();
    }

    public void renderWorld() {
        updateAvatar();
        if (LINE_OF_SIGHT) {
            TER.renderFrame(lineOfSightWorld());
        } else {
            TER.renderFrame(WORLD);
        }
        drawHeader();
    }

    /** Creates a header at the top of the world to display the tile being hovered over by the user's mouse */
    public void drawHeader() {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(WIDTH / 2.0, HEIGHT - 1, WIDTH / 2.0, HEADER_SIZE - 1); // Clear the header area

        StdDraw.setPenColor(Color.WHITE);
        Font fontSmall = new Font("Monaco", Font.BOLD, SMALL_FONT);
        StdDraw.setFont(fontSmall);

        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        String tileDescription = "not in the world";
        if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT - HEADER_SIZE) {
            TETile i = WORLD[mouseX][mouseY];
            if (i.description().equals("mountain") || i.description().equals("water")
                    || i.description().equals("wall")) {
                tileDescription = "a wall tile";
            } else if (i.description().equals("nothing")) {
                tileDescription = "empty";
            } else {
                tileDescription = "a floor tile";
            }
        }

        StdDraw.textLeft(2, HEIGHT - 1, "Hovered tile is " + tileDescription);
        StdDraw.textRight(WIDTH - 2, HEIGHT - 1, "Toggle visibility (V), Save + Quit (:Q)");
        // StdDraw.line(0, HEIGHT - HEADER_SIZE, WIDTH, HEIGHT - HEADER_SIZE);
        StdDraw.show();
    }

    private TETile[][] lineOfSightWorld() {
        TETile[][] limitedView = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (Math.abs(x - AVATAR.getX()) <= VISIBILITY_RADIUS
                        && Math.abs(y - AVATAR.getY()) <= VISIBILITY_RADIUS) {
                    limitedView[x][y] = WORLD[x][y];
                } else {
                    limitedView[x][y] = Tileset.NOTHING;
                }
            }
        }
        return limitedView;
    }

    public void saveWorld() {
        File worldFile = new File("./byow.txt");
        try {
            if (!worldFile.exists()) {
                worldFile.createNewFile();
            }
            try (FileOutputStream fs = new FileOutputStream(worldFile);
                 ObjectOutputStream os = new ObjectOutputStream(fs)) {
                os.writeObject(this);
            }
        } catch (IOException e) {
            System.out.println("Error saving the world: " + e.getMessage());
        }
    }

    // @source: ChatGPT function to handle deserialization
    public static CreateWorldFeatures loadWorld() {
        File worldFile = new File("./byow.txt");
        if (!worldFile.exists()) {
            System.out.println("Save file not found.");
            return null;
        }
        try (FileInputStream fs = new FileInputStream(worldFile);
             ObjectInputStream is = new ObjectInputStream(fs)) {
            CreateWorldFeatures loadedWorld = (CreateWorldFeatures) is.readObject();
            loadedWorld.initializeRenderer();
            loadedWorld.updateAvatar();
            return loadedWorld;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading the world: " + e.getMessage());
            return null;
        }
    }

    public TETile[][] getWorld() {
        return WORLD;
    }

    public Avatar getAvatar() {
        return AVATAR;
    }

    public void toggleLineOfSight() {
        LINE_OF_SIGHT = !LINE_OF_SIGHT;
    }
}
