package unit;

import eu.lightest.delegations.DelegationProviderProperties;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class PropertiesTest {

    private static final String EXISTING_PROPERTY = "data.store.type";
    private static final String INVALID_PROPERTY = "invalid.property";

    @Test
    public void readExistingProperty() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        String value = properties.getPropertyValueByName(EXISTING_PROPERTY);
        Assert.assertEquals("database", value);
    }

    @Test
    public void readNonExistingProperty() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        String value = properties.getPropertyValueByName(INVALID_PROPERTY);
        Assert.assertSame(null, value);
    }

    @Test
    public void readStorageProperty() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        String value = properties.getPropertyStorageType();
        Assert.assertEquals("database", value);
    }

    @Test
    public void readDatabaseAddressProperty() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        String value = properties.getPropertyDatabaseAddress();
        Assert.assertEquals("jdbc:sqlite:delegations.db", value);
    }

    @Test
    public void readDatabaseUserProperty() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        String value = properties.getPropertyDatabaseUser();
        Assert.assertEquals("delegation", value);
    }

    @Test
    public void readDatabasePasswordProperty() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        String value = properties.getPropertyDatabasePassword();
        Assert.assertEquals("delegation", value);
    }

    @Test
    public void readDatabaseNameProperty() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        String value = properties.getPropertyDatabaseName();
        Assert.assertEquals("delegations", value);
    }
}
