import java.util.Map;

public class Main {
    public static void main(String[] args) {
        int dungeonSize = 5; // Distance from center to edge

        int seed = (int) (Math.random() * 100 * Math.random()
                * (int) (System.currentTimeMillis() / 1000));

        System.out.println("WELCOME TO THE SEEDED DUNGEON");
        System.out.println("SEED: " + seed);

        // 1. Generate the dungeon (Update generate to return the Map!)
        Map<DungeonGenerator.Coord, Room> dungeon = DungeonGenerator.generate(dungeonSize, seed);

        // 2. Visualizer Loop
        // We go from size down to -size for Y so North is "Up" on your screen
        for (int y = dungeonSize; y >= -dungeonSize; y--) {
            StringBuilder line1 = new StringBuilder();
            StringBuilder line2 = new StringBuilder();
            StringBuilder line3 = new StringBuilder();

            boolean rowHasData = false;

            for (int x = -dungeonSize; x <= dungeonSize; x++) {
                DungeonGenerator.Coord currentCoord = new DungeonGenerator.Coord(x, y);
                Room r = dungeon.get(currentCoord);

                if (r == null) {
                    line1.append("     ");
                    line2.append("     ");
                    line3.append("     ");
                } else {
                    rowHasData = true;
                    // Using your methods: isNorth(), isSouth(), etc.
                    line1.append(r.isNorth() ? "  |  " : "  #  ");
                    line2.append(r.isWest() ? "-" : "#").append(" R ")
                            .append(r.isEast() ? "-" : "#");
                    line3.append(r.isSouth() ? "  |  " : "  #  ");
                }
            }

            // 3. Only print rows that actually contain rooms
            if (rowHasData) {
                System.out.println(line1);
                System.out.println(line2);
                System.out.println(line3);
            }
        }
    }
}
