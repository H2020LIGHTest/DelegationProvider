package unit;

import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.model.json.JsonDelegationCreated;
import eu.lightest.delegations.model.json.JsonPublishDelegation;
import eu.lightest.delegations.model.json.JsonPublishDelegationKey;
import eu.lightest.delegations.impl.PublicationImpl;
import eu.lightest.delegations.storage.database.DatabaseController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class PublicationTest {

    private static String DELEGATION_DATA = "Some delegation data test";
    private static String DELEGATION_HASH = "Some delegation data hash";
    private static String DELEGATION_KEY  = "Some delegation  key";

    private Log mLog = LogFactory.getLog(PublicationTest.class);

    @Before
    public void setupDatabase() throws SQLException, ClassNotFoundException {
        DatabaseController.getInstance().connect("jdbc:sqlite:test.db");
        DatabaseController.getInstance().setupDatabase();
    }

    @After
    public void cleanup() throws SQLException {
        DatabaseController.getInstance().close();

        File database = new File("delegations.db");
        if (database.delete()) {
            mLog.info("Successfully removed delegations.db database");
        } else {
            mLog.error("Could not remove database delegations.db!");
        }

        database = new File("test.db");
        if (database.delete()) {
            mLog.info("Successfully removed test.db database");
        } else {
            mLog.error("Could not remove database test.db!");
        }
    }

    @Test
    public void publishAsimpleDelegation() throws IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationHashMissingException, DelegationWriteException, SQLException, DelegationIdInvalidException {
        PublicationImpl impl = new PublicationImpl();

        JsonPublishDelegation delegation = new JsonPublishDelegation();
        delegation.setData(DELEGATION_DATA);
        delegation.setPublicKeyHash(DELEGATION_HASH);
        delegation.setPublicKey(DELEGATION_KEY);

        JsonDelegationCreated result = impl.publishDelegationJson(delegation);
        mLog.debug(result);

        Assert.assertEquals(new Integer(1), result.getId());
    }

    @Test(expected = DelegationKeyMissingException.class)
    @Ignore
    public void publishDelegationWithoutKey() throws IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationHashMissingException, DelegationWriteException, SQLException, DelegationIdInvalidException {
        PublicationImpl impl = new PublicationImpl();

        JsonPublishDelegation delegation = new JsonPublishDelegation();
        delegation.setData(DELEGATION_DATA);
        delegation.setPublicKeyHash(DELEGATION_HASH);

        JsonDelegationCreated result = impl.publishDelegationJson(delegation);
        mLog.debug(result);
    }

    @Test(expected = DelegationDataMissingException.class)
    public void publishDelegationWithoutData() throws IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationHashMissingException, DelegationWriteException, SQLException, DelegationIdInvalidException {
        PublicationImpl impl = new PublicationImpl();

        JsonPublishDelegation delegation = new JsonPublishDelegation();
        delegation.setPublicKeyHash(DELEGATION_HASH);
        delegation.setPublicKey(DELEGATION_KEY);


        JsonDelegationCreated result = impl.publishDelegationJson(delegation);
        mLog.debug(result);
    }

    @Test(expected = DelegationHashMissingException.class)
    public void publishDelegationWithoutHash() throws IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationHashMissingException, DelegationWriteException, SQLException, DelegationIdInvalidException {
        PublicationImpl impl = new PublicationImpl();

        JsonPublishDelegation delegation = new JsonPublishDelegation();
        delegation.setData(DELEGATION_DATA);
        delegation.setPublicKey(DELEGATION_KEY);


        JsonDelegationCreated result = impl.publishDelegationJson(delegation);
        mLog.debug(result);
    }

    @Test
    public void publishAsimpleDelegationKey() throws IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationHashMissingException, DelegationWriteException, DelegationIdMissingException, SQLException, DelegationIdInvalidException {
        PublicationImpl impl = new PublicationImpl();

        JsonPublishDelegation delegation = new JsonPublishDelegation();
        delegation.setData(DELEGATION_DATA);
        delegation.setPublicKeyHash(DELEGATION_HASH);
        delegation.setPublicKey(DELEGATION_KEY);

        JsonDelegationCreated result = impl.publishDelegationJson(delegation);
        mLog.debug(result);

        Assert.assertEquals(new Integer(1), result.getId());

        JsonPublishDelegationKey key = new JsonPublishDelegationKey();
        key.setId(result.getId());
        key.setHash(delegation.getHash());
        key.setKey(delegation.getPublicKey());

        impl.publishDelegationKeyJson(key);
    }

    @Test(expected = DelegationIdMissingException.class)
    public void publishAsimpleDelegationKeyMissingId() throws IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationHashMissingException, DelegationWriteException, DelegationIdMissingException, SQLException, DelegationIdInvalidException {
        PublicationImpl impl = new PublicationImpl();

        JsonPublishDelegation delegation = new JsonPublishDelegation();
        delegation.setData(DELEGATION_DATA);
        delegation.setPublicKeyHash(DELEGATION_HASH);
        delegation.setPublicKey(DELEGATION_KEY);

        JsonDelegationCreated result = impl.publishDelegationJson(delegation);
        mLog.debug(result);

        Assert.assertEquals(new Integer(1), result.getId());

        JsonPublishDelegationKey key = new JsonPublishDelegationKey();
        key.setHash(delegation.getHash());
        key.setKey(delegation.getPublicKey());

        impl.publishDelegationKeyJson(key);
    }

    @Test(expected = DelegationHashMissingException.class)
    public void publishAsimpleDelegationHashMissingId() throws IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationHashMissingException, DelegationWriteException, DelegationIdMissingException, SQLException, DelegationIdInvalidException {
        PublicationImpl impl = new PublicationImpl();

        JsonPublishDelegation delegation = new JsonPublishDelegation();
        delegation.setData(DELEGATION_DATA);
        delegation.setPublicKeyHash(DELEGATION_HASH);
        delegation.setPublicKey(DELEGATION_KEY);

        JsonDelegationCreated result = impl.publishDelegationJson(delegation);
        mLog.debug(result);

        Assert.assertEquals(new Integer(1), result.getId());

        JsonPublishDelegationKey key = new JsonPublishDelegationKey();
        key.setId(result.getId());
        key.setKey(delegation.getPublicKey());

        impl.publishDelegationKeyJson(key);
    }

    @Test(expected = DelegationKeyMissingException.class)
    public void publishAsimpleDelegationKeyKeyMissingId() throws IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationHashMissingException, DelegationWriteException, DelegationIdMissingException, SQLException, DelegationIdInvalidException {
        PublicationImpl impl = new PublicationImpl();

        JsonPublishDelegation delegation = new JsonPublishDelegation();
        delegation.setData(DELEGATION_DATA);
        delegation.setPublicKeyHash(DELEGATION_HASH);
        delegation.setPublicKey(DELEGATION_KEY);

        JsonDelegationCreated result = impl.publishDelegationJson(delegation);
        mLog.debug(result);

        Assert.assertEquals(new Integer(1), result.getId());

        JsonPublishDelegationKey key = new JsonPublishDelegationKey();
        key.setId(result.getId());
        key.setHash(delegation.getHash());

        impl.publishDelegationKeyJson(key);
    }


}
