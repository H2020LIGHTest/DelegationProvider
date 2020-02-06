package eu.lightest.delegations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DelegationProviderProperties {

    private Properties mProperties = null;
    private Log mLog = LogFactory.getLog(DelegationProviderProperties.class);

    private static final String PROPERTY_FILE_NAME = "config.properties";
    private static final String GIT_PROPERTY_FILE_NAME = "git.properties";

    private Properties mGitProperties = null;

    public DelegationProviderProperties() throws IOException {
        mProperties = initProperties(PROPERTY_FILE_NAME);
        mGitProperties = initProperties(GIT_PROPERTY_FILE_NAME);
        mLog.debug("Successfully loaded property file");
    }

    private Properties initProperties(String file) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = null;

        inputStream = getClass().getClassLoader().getResourceAsStream(file);
        if ( inputStream == null ) {
            throw new FileNotFoundException("property file '" + file + "' not found!");
        }

        properties.load(inputStream);

        inputStream.close();

        return properties;

    }

    public String getPropertyStorageType() {
        return getPropertyValueByName("data.store.type");
    }

    public String getPropertyDatabaseAddress() {
        return getPropertyValueByName("data.store.database.address");
    }

    public String getPropertyDatabaseUser() {
        return getPropertyValueByName("data.store.database.user");
    }

    public String getPropertyDatabasePassword() {
        return getPropertyValueByName("data.store.database.password");
    }

    public String getPropertyDatabaseName() {
        return getPropertyValueByName("data.store.database.name");
    }

    public String getPropertyValueByName(String name) {
        mLog.debug("Searching property '" + name + "'");

        String value = mProperties.getProperty(name);

        mLog.debug("Property '"+name+"' has value '" +value+"'");
        return value;
    }

    public String getGitPropertyRevId() {
        return getGitPropertyByName("git.commit.id.describe");
    }

    public String getGitPropertyByName(String name) {
        return mGitProperties.getProperty(name);
    }
}
