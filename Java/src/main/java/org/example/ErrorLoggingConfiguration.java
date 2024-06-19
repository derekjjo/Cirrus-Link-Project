package org.example;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ErrorLoggingConfiguration {
    public static void configure() {
        Logger errorlogger = Logger.getLogger("org.example");
        try {
            // Create a file handler that writes log messages to a file called "errorLog.txt"
            FileHandler fileHandler = new FileHandler("errorLog.txt", true);
            fileHandler.setFormatter(new SimpleFormatter()); // Use a simple text formatter
            errorlogger.addHandler(fileHandler);

            errorlogger.setLevel(Level.ALL);

        } catch (IOException e) {
            errorlogger.log(Level.SEVERE, "Failed to configure logger", e);
        }
    }
}
