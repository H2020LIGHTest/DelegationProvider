package unit;

import eu.lightest.delegations.storage.IStorageRead;
import eu.lightest.delegations.storage.IStorageWrite;
import eu.lightest.delegations.storage.StorageFactory;
import eu.lightest.delegations.storage.StorageSystem;
import eu.lightest.delegations.storage.database.DatabaseRead;
import eu.lightest.delegations.storage.database.DatabaseWrite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class StorageFactoryTests {

    private Log mLog = LogFactory.getLog(StorageFactoryTests.class);
    @Test
    public void getStorageSystemTest() throws IOException {
        StorageFactory instance = StorageFactory.getInstance();
        Assert.assertEquals(StorageSystem.database, instance.getStorageSystem());
    }

    @Test
    public void getStorageWriteImplementation() throws IOException {
        IStorageWrite impl = StorageFactory.getInstance().getStorageWrite();
        Assert.assertTrue(impl instanceof DatabaseWrite);
    }

    @Test
    public void getStorageReadImplementation() throws IOException {
        IStorageRead impl = StorageFactory.getInstance().getStorageRead();
        Assert.assertTrue(impl instanceof DatabaseRead);
    }
}
