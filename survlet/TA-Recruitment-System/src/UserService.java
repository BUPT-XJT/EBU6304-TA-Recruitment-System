import java.util.List;

public class UserService {
    public User login(String userId, String password) {
        //
        List<String> lines = FileUtil.read("TA-Recruitment-System/data/users.txt");

        System.out.println("--- 正在开始登录调试 ---");
        System.out.println("用户输入 ID: [" + userId + "]");
        System.out.println("用户输入 PWD: [" + password + "]");

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;

            String[] s = line.split(",");
            if (s.length >= 5) {
                //
                String idInFile = s[0].trim();
                String nameInFile = s[1].trim();
                String pwdInFile = s[2].trim();

                // 关键调试信息：打印出文件里每一行读出来的到底是什么
                System.out.println("检查文件中数据 -> ID: [" + idInFile + "], PWD: [" + pwdInFile + "]");

                if (idInFile.equals(userId) && pwdInFile.equals(password)) {
                    System.out.println("✅ 匹配成功！欢迎 " + nameInFile);
                    return new User(idInFile, nameInFile, pwdInFile, s[3].trim(), s[4].trim());
                }
            } else {
                System.out.println("⚠️ 这一行数据格式不对(字段少于5个): " + line);
            }
        }

        System.out.println("❌ 遍历结束，没有找到匹配的用户。");
        return null;
    }
}