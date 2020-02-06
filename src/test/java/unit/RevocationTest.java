package unit;

import eu.lightest.delegations.storage.database.DatabaseController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class RevocationTest {

    private Log mLog = LogFactory.getLog(RevocationTest.class);

    @After
    public void cleanup() throws SQLException {

        DatabaseController.getInstance().close();

        File database = new File("delegations.db");
        if (database.delete()) {
            mLog.info("Successfully removed delegations.db database");
        } else {
            mLog.error("Could not remove database delegations.db!");
        }
    }

    @Test
    public void revokeExistingDelegation() {
        assertTrue(true);
    }

    @Test
    public void revokeInvalidDelegation() {
        assertTrue(true);
    }

    @Test
    public void revokeDelegationWithInvalidAutzhenticationToken() {
        assertTrue(true);
    }

    @Test
    public void revokeRevokedDelegation() {
        assertTrue(true);
    }

    @Test
    public void downloadRevokedDelegation() {
        assertTrue(true);
    }
}
