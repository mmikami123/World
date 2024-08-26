package core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HallwayUnion implements Serializable {
    private final Map<Point, Point> parent = new HashMap<>();

    public Point find(Point p) {
        if (!parent.containsKey(p)) {
            parent.put(p, p);
        }
        if (!p.equals(parent.get(p))) {
            parent.put(p, find(parent.get(p)));
        }
        return parent.get(p);
    }

    public void union(Point p1, Point p2) {
        Point root1 = find(p1);
        Point root2 = find(p2);
        if (!root1.equals(root2)) {
            parent.put(root1, root2);
        }
    }

    public boolean connected(Point p1, Point p2) {
        return find(p1).equals(find(p2));
    }
}
