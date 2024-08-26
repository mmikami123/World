package core;

import java.io.Serializable;
import java.util.*;
import tileengine.*;

public class ResourcePack implements Serializable {
    ArrayList<TETile> forest = new ArrayList<>();
    ArrayList<TETile> beach = new ArrayList<>();
    ArrayList<TETile> cottonCandy = new ArrayList<>();
    private final String RESOURCE_PACK;

    public ResourcePack(String resourcePack) {
        this.RESOURCE_PACK = resourcePack;
    }

    public ArrayList<TETile> chooseResourcePack() {
        return switch (RESOURCE_PACK) {
            case "A Forest" -> createForest();
            case "Beach" -> createBeach();
            case "Cotton Candy" -> createCottonCandy();
            default -> new ArrayList<>();
        };
    }

    private ArrayList<TETile> createForest() {
        forest.add(Tileset.TREE);
        forest.add(Tileset.MOUNTAIN);
        forest.add(Tileset.GRASS);
        forest.add(Tileset.NOTHING);
        return forest;
    }

    private ArrayList<TETile> createBeach() {
        beach.add(Tileset.GRASS);
        beach.add(Tileset.WATER);
        beach.add(Tileset.SAND);
        beach.add(Tileset.NOTHING);
        return beach;
    }

    private ArrayList<TETile> createCottonCandy() {
        cottonCandy.add(Tileset.FLOWER);
        cottonCandy.add(Tileset.WALL);
        cottonCandy.add(Tileset.FLOOR);
        cottonCandy.add(Tileset.NOTHING);
        return cottonCandy;
    }
}
