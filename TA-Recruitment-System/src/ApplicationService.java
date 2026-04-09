import java.util.ArrayList;
import java.util.List;

public class ApplicationService {
    public List<Application> getApplicationsByUser(String taId) {
        List<String> lines = FileUtil.read("data/applications.txt");
        List<Application> list = new ArrayList<>();
        for (String line : lines) {
            String[] s = line.split(",");
            if (s.length >= 5 && s[1].equals(taId)) {
                list.add(new Application(s[0], s[1], s[2], s[3], s[4]));
            }
        }
        return list;
    }

    public boolean updateApplicationStatus(String appId, String status) {
        List<String> lines = FileUtil.read("data/applications.txt");
        List<String> newLines = new ArrayList<>();
        for (String line : lines) {
            String[] s = line.split(",");
            if (s[0].equals(appId)) {
                newLines.add(s[0] + "," + s[1] + "," + s[2] + "," + status + "," + s[4]);
            } else {
                newLines.add(line);
            }
        }
        FileUtil.write("data/applications.txt", newLines);
        return true;
    }

    public boolean createApplication(Application app) {
        String line = app.getId() + "," + app.getTaId() + "," + app.getPositionId() + ",PENDING,None";
        List<String> lines = FileUtil.read("data/applications.txt");
        lines.add(line);
        FileUtil.write("data/applications.txt", lines);
        return true;
    }
}