import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtil {
    private static final Logger logger = Logger.getLogger(FileUtil.class.getName());
    private static final String CHARSET = "UTF-8";

    public static List<String> read(String path) {
        List<String> lines = new ArrayList<>();

        if (path == null || path.trim().isEmpty()) {
            logger.warning("File path is empty, return empty lines");
            return lines;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), CHARSET))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "File not found: " + path, e);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Read file failed: " + path, e);
        }
        return lines;
    }


    public static void write(String path, List<String> lines) {

        if (path == null || path.trim().isEmpty()) {
            logger.warning("File path is empty, skip write");
            return;
        }
        if (lines == null) {
            logger.warning("Lines is null, skip write");
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), CHARSET))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Write file failed: " + path, e);
        }
    }
}