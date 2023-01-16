import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class JTarga {
    private final BufferedImage image;
    private final int width;
    private final int height;

    public JTarga(Path path) throws IOException {
        this.image = ImageIO.read(new FileInputStream(String.valueOf(path)));
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    private void putHeader(WritableByteChannel out) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(18);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.put((byte) 0); // identifier length
        buffer.put((byte) 0); // color map type
        buffer.put((byte) 2); // image type

        // color map specification
        buffer.putShort((byte) 0);
        buffer.putShort((byte) 0);
        buffer.put((byte) 0);

        // image specification
        buffer.putShort((short) image.getMinX()); // x origin
        buffer.putShort((short) image.getMinY()); // y origin
        buffer.putShort((short) width); // width
        buffer.putShort((short) height); //height
        buffer.put((byte) 32);
        buffer.put((byte) (8 | (1 << 5))); // image descriptor

        // buffer.put((byte) 0); // image id

        buffer.flip();
        out.write(buffer);
    }

    private void putImageData(WritableByteChannel out) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer.clear();

                int pixel = image.getRGB(image.getMinX() + x, image.getMinY() + y);
                Color color = new Color(pixel, true);

                // Instead of RGBA, Targa uses the BGRA format for some reason
                buffer.put((byte) color.getBlue());
                buffer.put((byte) color.getGreen());
                buffer.put((byte) color.getRed());
                buffer.put((byte) color.getAlpha());

                buffer.flip();
                out.write(buffer);
            }
        }
    }

    public void saveImage(Path destination) throws IOException {
        WritableByteChannel out = Files.newByteChannel(destination,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        putHeader(out);
        putImageData(out);
    }
}
