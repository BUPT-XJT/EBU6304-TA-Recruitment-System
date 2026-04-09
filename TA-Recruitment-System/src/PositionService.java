import java.util.ArrayList;
import java.util.List;

public class PositionService {
    public List<Position> getAllPositions() {
        List<String> lines = FileUtil.read("data/positions.txt");
        List<Position> list = new ArrayList<>();
        for (String line : lines) {
            String[] s = line.split(",");
            if (s.length >= 4) {
                list.add(new Position(s[0], s[1], s[2], s[3]));
            }
        }
        return list;
    }

    public boolean updatePositionStatus(String posId, String status) {
        List<String> lines = FileUtil.read("data/positions.txt");
        List<String> newLines = new ArrayList<>();
        for (String line : lines) {
            String[] s = line.split(",");
            if (s[0].equals(posId)) {
                newLines.add(s[0] + "," + s[1] + "," + s[2] + "," + status);
            } else {
                newLines.add(line);
            }
        }
        FileUtil.write("data/positions.txt", newLines);
        return true;
    }
}