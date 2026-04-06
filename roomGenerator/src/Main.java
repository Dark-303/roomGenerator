import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        int dungeonSize = 20; // Increased size to test the file export
        String fileName = "dungeon_output.txt";

        int seed =  -19416371; //Long.hashCode(Long.hashCode(System.currentTimeMillis()) + new Random().nextLong());

        System.out.println("GENERATING SEEDED DUNGEON...");
        System.out.println("SEED: " + seed);
        System.out.println("EXPORTING TO: " + fileName);

        Map<DungeonGenerator.Coord, Room> dungeon = DungeonGenerator.generate(dungeonSize, seed);

        // try-with-resources ensures the file is saved and closed even if an error occurs
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {

            writer.println("WELCOME TO THE SEEDED DUNGEON");
            writer.println("SEED: " + seed);
            writer.println(); // Extra space

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
                        // Line 1: North Connection
                        line1.append(r.isNorth() ? "  |  " : "  #  ");

                        // Line 2: West, Room, East Connections
                        String west = r.isWest() ? "-" : "#";
                        String east = r.isEast() ? "-" : "#";
                        line2.append(west).append(" R ").append(east);

                        // Line 3: South Connection
                        line3.append(r.isSouth() ? "  |  " : "  #  ");
                    }
                }

                if (rowHasData) {
                    writer.println(line1);
                    writer.println(line2);
                    writer.println(line3);
                }
            }

            System.out.println("SUCCESS! Check " + fileName + " for your map.");

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
