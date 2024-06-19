package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceEventHandler implements EventHandlerInterface {
    private static final String LOG_FILE_PATH = "events.txt";
    private static final Logger errorLOGGER = Logger.getLogger(ResourceEventHandler.class.getName());
    private String filterName;

    public ResourceEventHandler(String filterName) {
        this.filterName = filterName;
    }

    @Override
    public void handleEvent(Map<String, Object> resource) {
        if (filterName(resource)) {
            logEvent(encodeEvent(resource));
        }
    }

    private boolean filterName(Map<String, Object> resource) {
        // Filter by the supplied name
        return filterName.equals(resource.get("name"));
    }

    private String encodeEvent(Map<String, Object> resource) {
        return resource.toString(); // Simple encoding, could be converted to JSON or other formats
    }

    private void logEvent(String encodedResource) {
        System.out.println(encodedResource); //
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write(encodedResource + "\n");
        } catch (IOException e) {
            errorLOGGER.log(Level.SEVERE, "Failed to write to log file: " + LOG_FILE_PATH, e);
        }
    }

}