package shadrin.dev.reading_properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shadrin.dev.config.SimulationConfig;

import java.io.*;
import java.util.Properties;

public class Property {
    private static final String DEFAULT_CONFIG_PATH = "src/main/resources/parameter.yaml";

    public String parametersReader()  {
        Properties props = new Properties();
        try (InputStream input = Property.class.getResourceAsStream("application.properties")) {
            if (input == null){
                return DEFAULT_CONFIG_PATH;
            }
            props.load(input);
            String filePath = props.getProperty("config.path");
            if (filePath != null && new File(filePath).exists()){
                return filePath;
            } else {
                return DEFAULT_CONFIG_PATH;
            }
        } catch (IOException e) {
            return DEFAULT_CONFIG_PATH;
        }
    }
}
