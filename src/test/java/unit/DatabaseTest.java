package unit;

import eu.lightest.delegations.impl.exceptions.DelegationAlreadyRevokedException;
import eu.lightest.delegations.model.database.DelegationDataSet;
import eu.lightest.delegations.model.database.DelegationKeyDataSet;
import eu.lightest.delegations.model.database.RevocationListDataSet;
import eu.lightest.delegations.impl.exceptions.DelegationIdMissingException;
import eu.lightest.delegations.impl.exceptions.DelegationRevocationFailedException;
import eu.lightest.delegations.storage.database.DatabaseController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static org.junit.Assert.assertNotNull;

public class DatabaseTest {

    private Log mLog = LogFactory.getLog(DatabaseTest.class);

    private Random n = new Random();

    @After
    public void cleanup() throws IOException, SQLException {

        DatabaseController.getInstance().close();

        File database = new File("test.db");
        if (database.delete()) {
            mLog.info("Successfully removed test.db database");
        } else {
            mLog.error("Could not remove database test.db!");
        }
    }

    @Test
    public void initialisationTest() throws SQLException, ClassNotFoundException {
        DatabaseController dbc = DatabaseController.getInstance();
        dbc.connect("jdbc:sqlite:test.db");
        dbc.setupDatabase();
        assertNotNull(dbc);
    }


    @Test
    public void insertArbitraryDataIntoDelegationsTest() throws SQLException, ClassNotFoundException {
        DatabaseController dbc = DatabaseController.getInstance();
        dbc.connect("jdbc:sqlite:test.db");
        dbc.setupDatabase();

        int id = dbc.addDelegation("this is a test", "this is a test", "this is a test");
        Assert.assertEquals(1, id);
    }

    @Test
    public void insertMultipleArbitraryDataIntoDelegationTest() throws SQLException, ClassNotFoundException {
        DatabaseController dbc = DatabaseController.getInstance();
        dbc.connect("jdbc:sqlite:test.db");
        dbc.setupDatabase();

        int id = dbc.addDelegation("this is a test", "this is a test", "this is a test");
        Assert.assertEquals(1, id);

        id = dbc.addDelegation("this is another test", "this is another test", "this is another test");
        Assert.assertEquals(2, id);
    }

    @Test
    public void insertMultipleArbitraryDataIntoDelegationAndRemoveTest() throws SQLException, ClassNotFoundException {
        DatabaseController dbc = DatabaseController.getInstance();
        dbc.connect("jdbc:sqlite:test.db");
        dbc.setupDatabase();

        int id = dbc.addDelegation("this is a test", "this is a test", "this is a test");
        Assert.assertEquals(1, id);

        dbc.removeDelegation(id);

        id = dbc.addDelegation("this is another test", "this is another test", "this is another test");
        Assert.assertEquals(1, id);
    }

    @Test
    public void insertArbitraryKeyForArbitraryDelegationTest() throws SQLException, ClassNotFoundException {
        DatabaseController dbc = DatabaseController.getInstance();
        dbc.connect("jdbc:sqlite:test.db");
        dbc.setupDatabase();

        int id = dbc.addDelegation("this is a test", "this is a test", "this is a test");
        Assert.assertEquals(1, id);

        dbc.addDelegationKey(id, "this is a key for delegation id 0");

    }

    @Test
    public void readArbitraryDelegationDataTest() throws SQLException, ClassNotFoundException {
        DatabaseController dbc = DatabaseController.getInstance();
        dbc.connect("jdbc:sqlite:test.db");
        dbc.setupDatabase();

        int id = dbc.addDelegation("this is a test", "this is a test", "this is a test");
        Assert.assertEquals(1, id);

        DelegationDataSet dds = dbc.readDelegation(id);
        Assert.assertEquals(1, dds.getId());
        Assert.assertEquals("this is a test", dds.getHash());
        Assert.assertEquals("this is a test", dds.getKey());
        Assert.assertEquals("this is a test", dds.getData());
    }

    @Test
    public void readArbitraryDelegationKeyDataTest() throws SQLException, ClassNotFoundException {
        DatabaseController dbc = DatabaseController.getInstance();
        dbc.connect("jdbc:sqlite:test.db");
        dbc.setupDatabase();

        int id = dbc.addDelegation("this is a test", "this is a test", "this is a test");
        Assert.assertEquals(1, id);

        DelegationDataSet dds = dbc.readDelegation(id);
        Assert.assertEquals(1, dds.getId());
        Assert.assertEquals("this is a test", dds.getHash());
        Assert.assertEquals("this is a test", dds.getKey());
        Assert.assertEquals("this is a test", dds.getData());

        dbc.addDelegationKey(id, "this is a key for delegation id 0");

        DelegationKeyDataSet dkds = dbc.readDelegationKey(id);
        Assert.assertEquals(id, dkds.getId());
        Assert.assertEquals("this is a key for delegation id 0", dkds.getKey());
    }

    @Test
    public void insertMultipleArbitraryDataIntoDelegationRandomlyTest() throws SQLException, ClassNotFoundException {
        DatabaseController dbc = DatabaseController.getInstance();
        dbc.connect("jdbc:sqlite:test.db");
        dbc.setupDatabase();

        int id = 0;
        int cnt = 0;

        int rnd = n.nextInt(512);
        if ( rnd < 0 ) {
            rnd *= -1;
        }

        mLog.info("Testing with " + rnd + " elements" );

        while(cnt < rnd) {
            id = dbc.addDelegation("this is a test", "this is a test", "this is a test");
            ++cnt;
            Assert.assertEquals(cnt, id);
        }

    }

    @Test
    public void autoIdGenerationTest() throws SQLException, ClassNotFoundException {
        DatabaseController dbc = DatabaseController.getInstance();
        dbc.connect("jdbc:sqlite:test.db");
        dbc.setupDatabase();

        int id = 0;
        int cnt = 0;
        int dcnt = 0;

        int rnd = n.nextInt(512);
        if ( rnd < 0 ) {
            rnd *= -1;
        }

        mLog.info("Testing with " + rnd + " elements" );

        while(cnt < rnd) {
            id = dbc.addDelegation("this is a test", "this is a test", "this is a test");
            ++cnt;
            ++dcnt;
            Assert.assertEquals(dcnt, id);

            if (cnt % 3 == 0) {
                dbc.removeDelegation(id);
                --dcnt;
            }
        }

    }

    @Test
    public void testRecovationTable() throws SQLException, ClassNotFoundException, DelegationIdMissingException, DelegationRevocationFailedException, DelegationAlreadyRevokedException {
        DatabaseController dbc = DatabaseController.getInstance();
        dbc.connect("jdbc:sqlite:test.db");
        dbc.setupDatabase();

        int id = 0;
        String reason = "test revocation";
        long now_ms = System.currentTimeMillis();
        Date now = new Date(now_ms);

        id = dbc.addDelegation("this is a test", "this is a test key", "this is test data");
        dbc.revokeDelegationById(id, reason, now_ms);

        RevocationListDataSet rlds = dbc.readRevokedDelegation(id);
        Assert.assertEquals(id, rlds.getId());
        Assert.assertEquals(reason, rlds.getReason());
        Assert.assertEquals(now, rlds.getRevocationTime());
    }


    @Test
    public void testAuthenticationTable() throws SQLException, ClassNotFoundException {
        DatabaseController dbc = DatabaseController.getInstance();
        dbc.connect("jdbc:sqlite:test.db");
        dbc.setupDatabase();

        Connection c = dbc.getConnection();

        try(PreparedStatement insert_test = c.prepareStatement("INSERT INTO authentication (uid, challenge, response, exp_date, token) VALUES (?,?,?,?,?)")) {
            insert_test.setInt(1, 12345);
            insert_test.setString(2, "This is a test");
            insert_test.setString(3, "This is a test response");
            insert_test.setDate(4, new Date(System.currentTimeMillis()));
            insert_test.setString(5, "This is a token");

            insert_test.executeUpdate();
            assertNotNull(insert_test);
        }
    }


}
