import java.util.ArrayList;
import java.util.List;

public class ApplicationService {

    // 1. 获取某个 TA 的所有申请
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

    // 2. 更新申请状态
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

    // 3. 创建新申请
    public boolean createApplication(Application app) {
        String line = app.getId() + "," + app.getTaId() + "," + app.getPositionId() + ",PENDING,None";
        List<String> lines = FileUtil.read("data/applications.txt");
        lines.add(line);
        FileUtil.write("data/applications.txt", lines);
        return true;
    }

    // 4. 新增：获取所有申请记录（供 MO 和 Admin 审批使用）
    public List<Application> getAllApplications() {
        List<String> lines = FileUtil.read("data/applications.txt");
        List<Application> list = new ArrayList<>();
        for (String line : lines) {
            String[] s = line.split(",");
            if (s.length >= 5) {
                list.add(new Application(s[0].trim(), s[1].trim(), s[2].trim(), s[3].trim(), s[4].trim()));
            }
        }
        return list;
    }
}