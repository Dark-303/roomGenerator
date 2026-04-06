import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class DungeonGenerator {
    public record Coord(int x, int y) {
    }

    private static Random rng;
    private static double roomProbability = 0.6;

    public static Map<Coord, Room> generate(int size, int seed) {
        rng = new Random(seed);
        Map<Coord, Room> grid = new HashMap<>();
        HashSet<Coord> generatedSet = new HashSet<>();
        ArrayList<Coord> generated = new ArrayList<>();

        grid.put(new Coord(0, 0), new Room(true, true, true, true));
        generated.add(new Coord(0, 0));
        generatedSet.add(new Coord(0, 0));

        while (!generated.isEmpty()) {
            int selected = rng.nextInt(generated.size());
            Map<Coord, Room> newSquares = makeAdjacent(grid, generated.get(selected), size);
            for (Map.Entry<Coord, Room> e : newSquares.entrySet()) {
                if (!generatedSet.contains(e.getKey()))
                    generated.add(e.getKey());
                generatedSet.add(e.getKey());
            }
            generated.remove(selected);
        }

        postProcess(grid, size);

        return grid;
    }

    private static Map<Coord, Room> makeAdjacent(Map<Coord, Room> grid, Coord square, int size) {
        Map<Coord, Room> adjacentMap = new HashMap<>();

        ArrayList<Coord> adjacent = new ArrayList<Coord>();
        adjacent.add(new Coord(square.x, square.y + 1));
        adjacent.add(new Coord(square.x, square.y - 1));
        adjacent.add(new Coord(square.x - 1, square.y));
        adjacent.add(new Coord(square.x + 1, square.y));

        Collections.shuffle(adjacent, rng);

        for (Coord a : adjacent) {
            Room room = makeRoom(grid, a, size);
            if (room != null) {
                grid.put(a, room);
                adjacentMap.put(a, room);
            }
        }

        return adjacentMap;
    }

    private static Room makeRoom(Map<Coord, Room> grid, Coord square, int size) {
        if (grid.containsKey(square))
            return null;
        if (Math.abs(square.x) > size || Math.abs(square.y) > size)
            return null;

        boolean n = false;
        boolean s = false;
        boolean e = false;
        boolean w = false;

        Coord u = new Coord(square.x, square.y + 1);
        Coord d = new Coord(square.x, square.y - 1);
        Coord l = new Coord(square.x - 1, square.y);
        Coord r = new Coord(square.x + 1, square.y);

        if (grid.containsKey(u)) {
            if (grid.get(u).isSouth()) {
                n = true;
            }
        } else {
            if (rng.nextDouble() >= roomProbability) {
                n = true;
            }
        }

        if (grid.containsKey(d)) {
            if (grid.get(d).isNorth()) {
                s = true;
            }
        } else {
            if (rng.nextDouble() >= roomProbability) {
                s = true;
            }
        }

        if (grid.containsKey(l)) {
            if (grid.get(l).isEast()) {
                w = true;
            }
        } else {
            if (rng.nextDouble() >= roomProbability) {
                w = true;
            }
        }

        if (grid.containsKey(r)) {
            if (grid.get(r).isWest()) {
                e = true;
            }
        } else {
            if (rng.nextDouble() >= roomProbability) {
                e = true;
            }
        }

        if ((n && s) || (e && w) || (n && e) || (n && w) || (s && e) || (s && w)) {
            return new Room(n, s, e, w);
        } else {
            return null;
        }
    }

    public static void postProcess(Map<Coord, Room> grid, int size) {
        Map<Coord, Room> deadEnds = new HashMap<>();

        for (Map.Entry<Coord, Room> entry : grid.entrySet()) {
            Coord pos = entry.getKey();
            Room existingRoom = entry.getValue();
            Coord[] neighbors = {new Coord(pos.x, pos.y + 1), // North
                    new Coord(pos.x, pos.y - 1), // South
                    new Coord(pos.x + 1, pos.y), // East
                    new Coord(pos.x - 1, pos.y) // West
            };

            for (int i = 0; i < 4; i++) {
                Coord target = neighbors[i];
                if (Math.abs(target.x) <= size && Math.abs(target.y) <= size
                        && !grid.containsKey(target)) {
                    Room newDeadEnd = new Room(false, false, false, false);
                    if (i == 0) {
                        newDeadEnd.setSouth(true);
                        existingRoom.setNorth(true);
                    }
                    if (i == 1) {
                        newDeadEnd.setNorth(true);
                        existingRoom.setSouth(true);
                    }
                    if (i == 2) {
                        newDeadEnd.setWest(true);
                        existingRoom.setEast(true);
                    }
                    if (i == 3) {
                        newDeadEnd.setEast(true);
                        existingRoom.setWest(true);
                    }

                    deadEnds.put(target, newDeadEnd);
                }
            }
        }
        grid.putAll(deadEnds);

        for (Map.Entry<Coord, Room> entry : grid.entrySet()) {
            Coord pos = entry.getKey();
            Room r = entry.getValue();

            if (r.isNorth() && !grid.containsKey(new Coord(pos.x, pos.y + 1)))
                r.setNorth(false);
            if (r.isSouth() && !grid.containsKey(new Coord(pos.x, pos.y - 1)))
                r.setSouth(false);
            if (r.isEast() && !grid.containsKey(new Coord(pos.x + 1, pos.y)))
                r.setEast(false);
            if (r.isWest() && !grid.containsKey(new Coord(pos.x - 1, pos.y)))
                r.setWest(false);
        }

        for (Map.Entry<Coord, Room> entry : grid.entrySet()) {
            Coord pos = entry.getKey();
            Room r = entry.getValue();
            sync(grid, r, pos, pos.x, pos.y + 1, "N");
            sync(grid, r, pos, pos.x, pos.y - 1, "S");
            sync(grid, r, pos, pos.x + 1, pos.y, "E");
            sync(grid, r, pos, pos.x - 1, pos.y, "W");
        }
    }

    private static void sync(Map<Coord, Room> grid, Room r, Coord pos, int nx, int ny, String dir) {
        Room neighbor = grid.get(new Coord(nx, ny));
        if (neighbor != null) {
            // If I have a door, you must have one. If you have a door, I must have one.
            if (dir.equals("N") && (r.isNorth() || neighbor.isSouth())) {
                r.setNorth(true);
                neighbor.setSouth(true);
            }
            if (dir.equals("S") && (r.isSouth() || neighbor.isNorth())) {
                r.setSouth(true);
                neighbor.setNorth(true);
            }
            if (dir.equals("E") && (r.isEast() || neighbor.isWest())) {
                r.setEast(true);
                neighbor.setWest(true);
            }
            if (dir.equals("W") && (r.isWest() || neighbor.isEast())) {
                r.setWest(true);
                neighbor.setEast(true);
            }
        }
    }
}
