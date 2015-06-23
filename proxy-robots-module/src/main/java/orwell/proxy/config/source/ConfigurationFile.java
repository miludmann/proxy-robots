package orwell.proxy.config.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.proxy.config.ConfigModel;
import orwell.proxy.config.Configuration;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Michaël Ludmann on 6/9/15.
 */
public class ConfigurationFile extends Configuration {
    private final static Logger logback = LoggerFactory.getLogger(ConfigurationFile.class);
    private final String filePath;

    public ConfigurationFile(final String filePath) throws FileNotFoundException, NotFileException {
        this.filePath = filePath;

        final File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }
        if (file.isDirectory()) {
            throw new NotFileException(filePath);
        }
        logback.info("Using configuration file given as parameter: " + filePath);

        populate();
    }

    @Override
    protected void populateFromSource() throws JAXBException {
        final File file = new File(filePath);
        setConfigModel((ConfigModel) unmarshaller.unmarshal(file));
        logback.info("Configuration loaded from external file: " + filePath);
    }
}
