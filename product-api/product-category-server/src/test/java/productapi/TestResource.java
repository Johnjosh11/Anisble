package productapi;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;

public class TestResource {
    public static String readResource(Resource resource) throws IOException {
        return new String(Files.readAllBytes(resource.getFile().toPath()));
    }
}
