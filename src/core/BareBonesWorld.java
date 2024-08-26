package core;

import tileengine.*;
import utils.*;

import java.io.Serializable;
import java.util.*;

public class BareBonesWorld implements Serializable {
    private final int WIDTH;
    private final int HEIGHT;
    private final Random RANDOM;
    private final TETile[][] WORLD;
    private static final int MIN_ROOMS = 20;
    private static final int HEADER_SIZE = 2;
    private static final int MAX_FAILED_ATTEMPTS = 40;
    /** Keeps track of all tiles that comprise the floors of rooms */
    private final List<Point> ALL_ROOM_FLOORS = new ArrayList<>();
    /** Keeps track of all the tiles that are the starts of hallways */
    private final Set<Point> HALLWAY_STARTS = new HashSet<>();
    /** Keeps track of all the tiles that are hallway floors so walls can be put up around them */
    private final Set<Point> HALLWAY_FLOORS = new HashSet<>();
    /** Connects singular rooms together to ensure no hallways just connect the room together */
    private final HallwayUnion ALL_ROOMS = new HallwayUnion();
    /** Keeps track of all hallwayStarts that end up being dead ends */
    private final Set<Point> DEAD_ENDS = new HashSet<>();
    private final ArrayList<TETile> RESOURCE_PACK;

    public BareBonesWorld(long seed, int width, int height, ArrayList<TETile> resource) {
        this.RANDOM = new Random(seed);
        this.WIDTH = width;
        this.HEIGHT = height;
        this.RESOURCE_PACK = resource;
        this.WORLD = new TETile[WIDTH][HEIGHT];
    }

    /** Create the world with its randomly generated rooms and hallways */
    public TETile[][] initializeWorld() {
        fillWorldWithNothing();
        generateRooms();
        generateHallways();
        return WORLD;
    }

    /** Create the world with all tiles to have nothing on them */
    private void fillWorldWithNothing() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                WORLD[x][y] = RESOURCE_PACK.getLast();
            }
        }
    }

    /** Creates the room's floor, accounting for the border of both the walls and world */
    private void createFloor(int xCorner, int yCorner, int floorWidth, int floorHeight) {
        for (int x = xCorner; x <= xCorner + floorWidth; x++) {
            for (int y = yCorner; y <= yCorner + floorHeight; y++) {
                WORLD[x][y] = RESOURCE_PACK.getFirst();
                ALL_ROOM_FLOORS.add(new Point(x, y));
            }
        }
    }

    /** Returns a list of all the tiles that comprise the floors of the rooms */
    public List<Point> getAllRoomFloors() {
        return ALL_ROOM_FLOORS;
    }

    /** Creates the room's walls, accounting for the border of the world */
    private void createWall(int xCorner, int yCorner, int floorWidth, int floorHeight) {
        Set<Point> roomBorder = new HashSet<>(); // all tiles that make up the room's wall
        ArrayList<Point> nonBorderTiles = new ArrayList<>(); // all valid tiles that can be a hallway
        int newX = xCorner + floorWidth;
        int newY = yCorner + floorHeight;
        for (int x = xCorner - 1; x <= newX + 1; x++) {
            for (int y = yCorner - 1; y <= newY + 1; y++) {
                if (WORLD[x][y] == RESOURCE_PACK.getLast()) {
                    // creates the walls of the room
                    WORLD[x][y] = RESOURCE_PACK.get(1);

                    // add all tiles that aren't 3 tiles from the world's border or in a room's corner into a list
                    if (x >= 3 && y >= 3 && x < WORLD.length - 3 && y < WORLD[0].length - 3) {
                        if (!((x == xCorner - 1 || x == newX + 1) && (y == yCorner - 1 || y == newY + 1))) {
                            nonBorderTiles.add(new Point(x, y));
                        }
                    }

                    // connects all the room's walls into a singular disjoint set
                    roomBorder.add(new Point(x, y));
                    if (x < WORLD.length - 1 && roomBorder.contains(new Point(x + 1, y))) {
                        ALL_ROOMS.union(new Point(x, y), new Point(x + 1, y));
                    }
                    if (y < WORLD[0].length - 1 && roomBorder.contains(new Point(x, y + 1))) {
                        ALL_ROOMS.union(new Point(x, y), new Point(x, y + 1));
                    }
                    if (x >= 1 && roomBorder.contains(new Point(x - 1, y))) {
                        ALL_ROOMS.union(new Point(x, y), new Point(x - 1, y));
                    }
                    if (y >= 1 && roomBorder.contains(new Point(x, y - 1))) {
                        ALL_ROOMS.union(new Point(x, y), new Point(x, y - 1));
                    }
                }
            }
        }
        createHallwayStart(nonBorderTiles);
    }

    /** Designates 1-3 random tiles from the wall to be the starting point of a hallway */
    private void createHallwayStart(ArrayList<Point> nonBorderTiles) {
        List<Point> allRoomStarts = new ArrayList<>();
        // create the first opening to a hallway in a room
        Point entryOne = nonBorderTiles.get(RandomUtils.uniform(RANDOM, 0, nonBorderTiles.size()));
        HALLWAY_STARTS.add(entryOne);
        allRoomStarts.add(entryOne);
        WORLD[entryOne.getX()][entryOne.getY()] = RESOURCE_PACK.get(2);

        // create a second opening if the hallway is not opened from the same spot or the spot next to it
        for (int i = 0; i < MAX_FAILED_ATTEMPTS; i++) {
            Point entryTwo = nonBorderTiles.get(RandomUtils.uniform(RANDOM, 0, nonBorderTiles.size()));
            if (!entryTwo.equals(entryOne) && isGoodDist(entryOne, entryTwo)) {
                HALLWAY_STARTS.add(entryTwo);
                allRoomStarts.add(entryTwo);
                ALL_ROOMS.union(entryOne, entryTwo);
                WORLD[entryTwo.getX()][entryTwo.getY()] = RESOURCE_PACK.get(2);
                break;
            }
        }

        for (int i = 0; i < MAX_FAILED_ATTEMPTS; i++) {
            Point entryThree = nonBorderTiles.get(RandomUtils.uniform(RANDOM, 0, nonBorderTiles.size()));
            if (!entryThree.equals(entryOne) && isGoodDist(entryOne, entryThree)) {
                if (allRoomStarts.size() > 1) {
                    Point entryTwo = allRoomStarts.get(1);
                    if (!entryThree.equals(entryTwo) && isGoodDist(entryTwo, entryThree)) {
                        HALLWAY_STARTS.add(entryThree);
                        ALL_ROOMS.union(entryOne, entryThree);
                        ALL_ROOMS.union(entryTwo, entryThree);
                        WORLD[entryThree.getX()][entryThree.getY()] = RESOURCE_PACK.get(2);
                        break;
                    }
                } else {
                    HALLWAY_STARTS.add(entryThree);
                    ALL_ROOMS.union(entryOne, entryThree);
                    WORLD[entryThree.getX()][entryThree.getY()] = RESOURCE_PACK.get(2);
                    break;
                }
            }
        }
    }

    /** Ensures all hallwayStarts have at least a 1 tile gap between them */
    private boolean isGoodDist(Point entryOne, Point entryTwo) {
        return (Math.abs(entryTwo.getX() - entryOne.getX()) > 1
                && (Math.abs(entryTwo.getY() - entryOne.getY()) > 1));
    }

    /** Finds and designates the closest tile in getHallwayStarts that is not from the same room to be a hallway */
    private Point leastDistance(Point tile, Set<Point> hallwayStarts) {
        Map<Double, Point> distance = new TreeMap<>();
        // sort all the tiles from least to greatest distance
        for (Point allTiles : hallwayStarts) {
            if (!ALL_ROOMS.connected(tile, allTiles)) {
                double xCoord = Math.pow(Math.abs(tile.getX() - allTiles.getX()), 2);
                double yCoord = Math.pow(Math.abs(tile.getY() - allTiles.getY()), 2);
                double distances = Math.pow((xCoord + yCoord), 0.5);
                distance.put(distances, allTiles);
            }
        }

        // return a valid tile that is the closest to the given tile
        for (Map.Entry<Double, Point> least : distance.entrySet()) {
            return least.getValue();
        }
        return null;
    }

    /** Ensures that two rooms do not overlap and have at least a 4 tile gap between them for hallways */
    private boolean isValidRoom(int xCorner, int yCorner, int floorWidth, int floorHeight) {
        for (int x = xCorner - 5; x <= xCorner + floorWidth + 5; x++) {
            for (int y = yCorner - 5; y <= yCorner + floorHeight + 5; y++) {
                if (x >= 0 && y >= 0 && x < WORLD.length && y < HEIGHT - HEADER_SIZE) { // if within boundaries
                    if (WORLD[x][y] != RESOURCE_PACK.getLast()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** Creates the room with floors and walls */
    private void createRoom(int xCorner, int yCorner, int floorWidth, int floorHeight) {
        if (isValidRoom(xCorner, yCorner, floorWidth, floorHeight)) {
            createFloor(xCorner, yCorner, floorWidth, floorHeight);
            createWall(xCorner, yCorner, floorWidth, floorHeight);
        }
    }

    /** Randomly generates rooms throughout the world */
    private void generateRooms() {
        int numberOfRooms = MIN_ROOMS + RANDOM.nextInt(9);
        for (int i = 0; i < numberOfRooms; i++) {
            int attempts = 0;
            while (attempts < MAX_FAILED_ATTEMPTS) {
                int floorWidth = RandomUtils.uniform(RANDOM, 4, 8); //width of a room's floor
                int floorHeight = RandomUtils.uniform(RANDOM, 5, 9); //height of a room's floor
                int xCorner = RandomUtils.uniform(RANDOM, 1, WIDTH - floorWidth - 1); //coord of floor's left x corner
                int yCorner = RandomUtils.uniform(RANDOM, 1, HEIGHT - HEADER_SIZE - floorHeight - 1); //left y corner
                if (isValidRoom(xCorner, yCorner, floorWidth, floorHeight)) {
                    createRoom(xCorner, yCorner, floorWidth, floorHeight);
                    break;
                }
                attempts++;
            }
        }
    }

    /** Ensures that two hallways do not go through rooms and leave enough space for its walls  */
    private boolean isValidHall(int currX, int currY) {
        TETile wall = RESOURCE_PACK.get(1);
        TETile roomFloor = RESOURCE_PACK.get(0);
        if (currX < 1 || currX >= WORLD.length - 1 || currY < 1 || currY >= HEIGHT - HEADER_SIZE - 1) {
            return false;
        } else if (WORLD[currX + 1][currY] == wall || WORLD[currX + 1][currY] == roomFloor
                || WORLD[currX - 1][currY] == wall || WORLD[currX - 1][currY] == roomFloor
                || WORLD[currX][currY + 1] == wall || WORLD[currX][currY + 1] == roomFloor
                || WORLD[currX][currY - 1] == wall || WORLD[currX][currY - 1] == roomFloor) {
            return false;
        } else {
            return WORLD[currX][currY] == RESOURCE_PACK.getLast();
        }
    }

    /** Plans out the hallways that would connect two hallway starts together */
    private Set<Point> planHallwayFloors(int currX, int currY, int targetX, int targetY) {
        Set<Point> plannedHalls = new HashSet<>();
        while (currX != targetX || currY != targetY) {
            if (currX < targetX && isValidHall(currX + 1, currY)
                    && !plannedHalls.contains(new Point(currX + 1, currY))) {
                currX++;
            } else if (currX > targetX && isValidHall(currX - 1, currY)
                    && !plannedHalls.contains(new Point(currX - 1, currY))) {
                currX--;
            } else if (currY < targetY && isValidHall(currX, currY + 1)
                    && !plannedHalls.contains(new Point(currX, currY + 1))) {
                currY++;
            } else if (currY > targetY && isValidHall(currX, currY - 1)
                    && !plannedHalls.contains(new Point(currX, currY - 1))) {
                currY--;
            } else {
                Point other = makeTurnOrAdvance(currX, currY, targetX, targetY, plannedHalls);
                if (other == null || plannedHalls.contains(other)) {
                    break;
                }
                currX = other.getX();
                currY = other.getY();
            }
            plannedHalls.add(new Point(currX, currY));
        }
        addTarget(plannedHalls, targetX, targetY);
        return plannedHalls;
    }

    /** Makes a turn or advance forward as needed when creating a hallway */
    private Point makeTurnOrAdvance(int currX, int currY, int targetX, int targetY, Set<Point> plan) {
        if (currX != targetX) {
            if (isValidHall(currX, currY + 1) && !plan.contains(new Point(currX, currY + 1))) {
                return new Point(currX, currY + 1);
            } else if (isValidHall(currX, currY - 1) && !plan.contains(new Point(currX, currY - 1))) {
                return new Point(currX, currY - 1);
            }
        } else if (currY != targetY) {
            if (isValidHall(currX + 1, currY) && !plan.contains(new Point(currX + 1, currY))) {
                return new Point(currX + 1, currY);
            } else if (isValidHall(currX - 1, currY) && !plan.contains(new Point(currX - 1, currY))) {
                return new Point(currX - 1, currY);
            }
        }
        return null;
    }

    /** Adds the target tile into plannedHalls if the hallway is fully connected */
    private void addTarget(Set<Point> plannedHalls, int targetX, int targetY) {
        if (!plannedHalls.contains(new Point(targetX, targetY))) {
            Set<Point> surroundingTargetTile = new HashSet<>();
            if (isValidHall(targetX + 1, targetY)) {
                surroundingTargetTile.add(new Point(targetX + 1, targetY));
            }
            if (isValidHall(targetX - 1, targetY)) {
                surroundingTargetTile.add(new Point(targetX - 1, targetY));
            }
            if (isValidHall(targetX, targetY + 1)) {
                surroundingTargetTile.add(new Point(targetX, targetY + 1));
            }
            if (isValidHall(targetX, targetY - 1)) {
                surroundingTargetTile.add(new Point(targetX, targetY - 1));
            }

            for (Point tile : surroundingTargetTile) {
                if (plannedHalls.contains(tile)) {
                    plannedHalls.add(new Point(targetX, targetY));
                    break;
                }
            }
        }
    }

    /** Ensures that only the hallways that are not dead ends will be created */
    private void weedOutPlannedDeadEnds(Set<Point> hallwayStarts, Set<Point> deadEnds) {
        for (Point tile : hallwayStarts) {
            int currX = tile.getX();
            int currY = tile.getY();
            Point target = leastDistance(tile, hallwayStarts);
            if (target == null) {
                deadEnds.add(tile);
                continue;
            }
            int targetX = target.getX();
            int targetY = target.getY();
            Set<Point> plannedHallway = planHallwayFloors(currX, currY, targetX, targetY);
            if (plannedHallway.contains(target)) {
                HALLWAY_FLOORS.addAll(plannedHallway);
                ALL_ROOMS.union(tile, target);
                deadEnds.remove(tile);
                deadEnds.remove(target);
            } else {
                if (!HALLWAY_FLOORS.contains(tile)) {
                    deadEnds.add(tile);
                }
            }
        }
    }

    /** If the hallway was a dead end, reconnect them so that all rooms connect together */
    private void reconnectDeadEnds() {
        Set<Point> currentDeadEnds = new HashSet<>(DEAD_ENDS);
        Set<Point> newDeadEnds = new HashSet<>();
        while (!areAllRoomsConnected()) {
            newDeadEnds.clear();
            weedOutPlannedDeadEnds(currentDeadEnds, newDeadEnds);
            currentDeadEnds = new HashSet<>(newDeadEnds);
        }

        for (Point tile : currentDeadEnds) {
            WORLD[tile.getX()][tile.getY()] = RESOURCE_PACK.get(1);
        }
    }

    /** Checks to see if all rooms are connected together by hallways */
    private boolean areAllRoomsConnected() {
        List<Point> hallwayStarts = new ArrayList<>(HALLWAY_STARTS);
        for (int i = 0; i < hallwayStarts.size(); i++) {
            for (int j = i + 1; j < hallwayStarts.size(); j++) {
                if (!ALL_ROOMS.connected(hallwayStarts.get(i), hallwayStarts.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Create the hallway floors that were planned out  */
    private void createHallwayFloors() {
        for (Point tile : HALLWAY_FLOORS) {
            WORLD[tile.getX()][tile.getY()] = RESOURCE_PACK.get(2);
        }
    }

    /** Creates the walls for the hallways */
    private void createHallwayWalls() {
        for (Point tile : HALLWAY_FLOORS) { // for everything in the hash set, draw a border around it
            for (int x = tile.getX() - 1; x <= tile.getX() + 1; x++) {
                for (int y = tile.getY() - 1; y <= tile.getY() + 1; y++) {
                    if (WORLD[x][y] == RESOURCE_PACK.getLast()) {
                        WORLD[x][y] = RESOURCE_PACK.get(1);
                    }
                }
            }
        }
    }

    /** Generates the hallway with its border */
    private void generateHallways() {
        weedOutPlannedDeadEnds(HALLWAY_STARTS, DEAD_ENDS);
        reconnectDeadEnds();
        createHallwayFloors();
        createHallwayWalls();
    }
}
