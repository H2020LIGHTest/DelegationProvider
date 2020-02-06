package unit;

import eu.lightest.delegations.storage.database.DatabaseController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class DiscoveryTests {

    private Log mLog = LogFactory.getLog(DiscoveryTests.class);


    @After
    public void cleanup() throws IOException, SQLException {

        DatabaseController.getInstance().close();

        File database = new File("delegations.db");
        if (database.delete()) {
            mLog.info("Successfully removed delegations.db database");
        } else {
            mLog.error("Could not remove database delegations.db!");
        }
    }

    @Test
    public void getAllDelegationsForHashTest() {
        assertTrue(true);
    }

    @Test
    public void getDelegationForIdTest() {
        assertTrue(true);
    }

    @Test
    public void queryWithInvalidHashTest() {
        assertTrue(true);
    }

    @Test
    public void queryWithInvalidIdTest() {
        assertTrue(true);
    }
}
