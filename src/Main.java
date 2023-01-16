import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {

        JTarga jTarga = new JTarga(Paths.get("YOUR_IMPORT_IMAGE_PATH"));
        jTarga.saveImage(Paths.get("YOUR_EXPORT_PATH"));

        System.out.println("Targa exported, yay!");
    }
}
