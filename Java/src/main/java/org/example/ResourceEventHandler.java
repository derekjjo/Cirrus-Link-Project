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

    @Override
    public void handleEvent(Map<String, Object> resource) {
        if (filterEvent(resource)) {
            logEvent(encodeEvent(resource));
        }
    }

    private boolean filterEvent(Map<String, Object> resource) {
        // Example: Filter by name and datatype
        return "TestResource".equals(resource.get("name")) && "int".equals(resource.get("datatype"));
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