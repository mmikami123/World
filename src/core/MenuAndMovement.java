package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.*;
import java.awt.*;

public class MenuAndMovement {
    private static final int ONE_SEC = 1000;
    private static final int SMALL_FONT = 20;
    private static final int MED_FONT = 30;
    private static final int BIG_FONT = 40;
    private static final int HUGE_FONT = 80;
    private static final int HEIGHT1 = 250;
    private static final int HEIGHT2 = 180;
    private static final int HEIGHT3 = 80;
    private static final int SMALL_ADJUSTMENT = 35;
    private static final int MED_ADJUSTMENT = 60;
    private static final int BIG_ADJUSTMENT = 100;
    private static final int BIGGEST_ADJUSTMENT = 140;
    private String selectedResource = "Cotton Candy";
    private TETile selectedAvatar = Tileset.HEART;
    private CreateWorldFeatures world;
    private final int WIDTH;
    private final int HEIGHT;

    public MenuAndMovement(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    /** Displays the game menu and properly handles key inputs */
    public void createMenu() {
        drawMenu();
        processMenu();
    }

    /** Creates and displays the game menu */
    private void drawMenu() {
        // create the UI for the title
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, BIG_FONT);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2.0, 2.0 * HEIGHT / 3, "CS61B: THE GAME");

        // create the UI for the input options
        Font fontSmall = new Font("Monaco", Font.PLAIN, SMALL_FONT);
        StdDraw.setFont(fontSmall);
        String[] options = {"New Game (N)", "Load Game (L)", "Quit (Q)", "Choose New Avatar (A)",
            "Choose new resource pack (R)"};
        for (int i = 0; i < options.length; i++) {
            StdDraw.text(WIDTH / 2.0, (HEIGHT / 3.0 - i * MED_FONT) + SMALL_ADJUSTMENT, options[i]);
        }

        // display the game menu
        StdDraw.show();
    }

    /** Draws a frame with the specified string in the middle */
    private void drawFrame(String text) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, MED_FONT);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, text);
        StdDraw.show();
    }

    /** Returns the next key typed, no matter how much time has passed */
    private char getInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return StdDraw.nextKeyTyped();
            }
        }
    }

    /** Processes user input into the main menu options */
    private void processMenu() {
        while (true) {
            char option = Character.toUpperCase(getInput());
            switch (option) {
                case 'A':
                    chooseYourAvatar();
                    break;
                case 'R':
                    chooseYourResource();
                    break;
                case 'N':
                    createNewWorld();
                    return;
                case 'L':
                    loadPrevWorld();
                    break;
                case 'Q':
                    drawFrame("Quitting game.");
                    StdDraw.pause(ONE_SEC);
                    System.exit(0);
                    return;
                default:
                    drawFrame("Must select a valid option.");
                    StdDraw.pause(ONE_SEC);
                    drawMenu();
                    break;
            }
        }
    }

    /** Creates a generic frame for a selection of various choices */
    private void chooseFrame(String text2, String text3, String text4, String text5,
                             String text6, String text7, String text8, int adjust) {
        // title
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, BIG_FONT);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2.0, 2.0 * HEIGHT / 3 + SMALL_ADJUSTMENT, "Customize Your World");

        // player's choice
        Font fontMed = new Font("Monaco", Font.BOLD, MED_FONT);
        StdDraw.setFont(fontMed);
        StdDraw.text(WIDTH / 2.0, 2.0 * HEIGHT / 3 - SMALL_ADJUSTMENT, text2);

        // visual options for choices
        Font fontHuge = new Font("Monaco", Font.BOLD, HUGE_FONT);
        StdDraw.setFont(fontHuge);
        StdDraw.textLeft(adjust, HEIGHT1, text3);
        StdDraw.text(WIDTH / 2.0, HEIGHT1, text4);
        StdDraw.textRight(WIDTH - adjust, HEIGHT1, text5);

        // explained options for choices
        Font fontSmall = new Font("Monaco", Font.BOLD, SMALL_FONT);
        StdDraw.setFont(fontSmall);
        StdDraw.textLeft(MED_ADJUSTMENT, HEIGHT2, text6);
        StdDraw.text(WIDTH / 2.0, HEIGHT2, text7);
        StdDraw.textRight(WIDTH - MED_ADJUSTMENT, HEIGHT2, text8);

        // option to exit the selection screen
        StdDraw.text(WIDTH / 2.0, HEIGHT3, "Save changes made (S)");

        // display the avatar options
        StdDraw.show();
    }

    private void chooseResourceFrame(String resource) {
        chooseFrame("Chosen resource pack: " + resource, "♠▲\"",
                "\"≈▒", "❀#·", "A Forest (A)", "Beach (B)", "Cotton Candy (C)",
                BIG_ADJUSTMENT);
    }

    private void chooseYourResource() {
        chooseResourceFrame(selectedResource);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char resourceKey = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (resourceKey == 'A') {
                    selectedResource = "A Forest";
                    chooseResourceFrame(selectedResource);
                }
                if (resourceKey == 'B') {
                    selectedResource = "Beach";
                    chooseResourceFrame(selectedResource);
                }
                if (resourceKey == 'C') {
                    selectedResource = "Cotton Candy";
                    chooseResourceFrame(selectedResource);
                }
                if (resourceKey == 'S') {
                    drawMenu();
                    break;
                }
                chooseResourceFrame(selectedResource);
            }
        }
    }

    /** Creates the frame shown during the avatar selection screen */
    private void chooseAvatarFrame(TETile avatar) {
        chooseFrame("Chosen avatar: " + avatar.character(), "♥", "¶",
                "⌂", "Annabeth (A)", "Bartholomew (B)", "Cyclopentane (C)", BIGGEST_ADJUSTMENT);
    }

    /** Allows you to choose a new avatar in the game */
    private void chooseYourAvatar() {
        chooseAvatarFrame(selectedAvatar);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char avatarKey = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (avatarKey == 'A') {
                    selectedAvatar = Tileset.HEART;
                    chooseAvatarFrame(selectedAvatar);
                }
                if (avatarKey == 'B') {
                    selectedAvatar = Tileset.INDENT;
                    chooseAvatarFrame(selectedAvatar);
                }
                if (avatarKey == 'C') {
                    selectedAvatar = Tileset.HOUSE;
                    chooseAvatarFrame(selectedAvatar);
                }
                if (avatarKey == 'S') {
                    drawMenu();
                    break;
                }
                chooseAvatarFrame(selectedAvatar);
            }
        }
    }


    /** Creates a new world for the player */
    private void createNewWorld() {
        drawFrame("Enter seed & press S to start: ");
        StringBuilder seed = new StringBuilder();
        boolean digitEntered = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char seedling = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (seedling == 'S' && digitEntered) {
                    break;
                } else if (!Character.isDigit(seedling)) {
                    drawFrame("Seeds can only contain digits.");
                    StdDraw.pause(ONE_SEC);
                } else {
                    seed.append(seedling);
                    digitEntered = true;
                }
                drawFrame("Enter seed & press S to start: " + seed);
            }
        }
        world = new CreateWorldFeatures(Long.parseLong(seed.toString()), selectedAvatar, selectedResource);
        world.renderWorld();
        processMovement();
    }

    /** Handles HUD updates */
    private void processMovement() {
        boolean colonPressed = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char keyPressed = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (colonPressed) {
                    if (keyPressed == 'Q') {
                        drawFrame("Saving and quitting game.");
                        StdDraw.pause(ONE_SEC);
                        world.saveWorld();
                        System.exit(0);
                    }
                    colonPressed = false;
                } else if (keyPressed == ':') {
                    colonPressed = true;
                } else if (keyPressed == 'V') {
                    world.toggleLineOfSight();
                    world.renderWorld();
                } else {
                    processWASD(keyPressed);
                }
                // world.renderWorld();
            }
            world.drawHeader();
        }
    }

    /** Responds to avatar movement based on the key pressed */
    private void processWASD(char option) {
        switch (option) {
            case 'W':
                world.getAvatar().moveUp(world.getWorld());
                break;
            case 'A':
                world.getAvatar().moveLeft(world.getWorld());
                break;
            case 'S':
                world.getAvatar().moveDown(world.getWorld());
                break;
            case 'D':
                world.getAvatar().moveRight(world.getWorld());
                break;
            default:
                break;
        }
        world.renderWorld();
    }

    /** Loads a previously saved world for the player */
    private void loadPrevWorld() {
        drawFrame("Loading world...");
        world = CreateWorldFeatures.loadWorld();
        if (world != null) {
            world.renderWorld();
            processMovement();
        } else {
            drawFrame("No saved game found.");
            StdDraw.pause(ONE_SEC);
            drawMenu();
        }
    }
}
