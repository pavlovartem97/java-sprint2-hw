import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class FileParser {
    static String readFileContentsOrNull(String path)
    {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл. Возможно, файл не находится в нужной директории.");
            return null;
        }
    }
}
