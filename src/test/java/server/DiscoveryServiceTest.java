package server;

import com.google.gson.Gson;
import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.model.json.JsonDelegationResult;
import eu.lightest.delegations.model.json.JsonDelegationResultEntry;
import eu.lightest.delegations.services.DiscoveryService;
import lib.TestDataLib;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.*;

import javax.ws.rs.core.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DiscoveryServiceTest extends JerseyTest {
    private static Log mLog = LogFactory.getLog(DiscoveryServiceTest.class);

    private static TestDataLib tl = new TestDataLib();

    @BeforeClass
    public static void setup() throws IOException, SQLException, ClassNotFoundException, DelegationWriteException, DelegationKeyMissingException, NoSuchAlgorithmException, DelegationDataMissingException, DelegationHashMissingException, DelegationIdMissingException {
        tl.beforeSetup();
    }

    @AfterClass
    public static void cleanup() throws SQLException {
        tl.cleanup();
    }

    @Before
    public void setupTest() throws NoSuchAlgorithmException, SQLException, DelegationWriteException, DelegationHashMissingException, IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationIdMissingException, ClassNotFoundException {
        tl.setup();
    }

    @After
    public void cleanupDatabase() throws SQLException, ClassNotFoundException, IOException {
        tl.cleanupDatabase();
    }

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(DiscoveryService.class);
    }


    @Test
    public void discoverAllDelegationsForHash() throws SQLException, IOException, ClassNotFoundException {

        tl.injectToken("12345");
        Response rsp = target("/v1.0/search").queryParam("hash", tl.getDelegationHash()[0]).queryParam("token", "12345").request().get();
        String response = rsp.readEntity(String.class);

        mLog.debug("Received response: " + response );
        assertEquals(200, rsp.getStatus());

        Gson gson = new Gson();
        JsonDelegationResult jsonResult = gson.fromJson(response, JsonDelegationResult.class);

        List<JsonDelegationResultEntry> results = jsonResult.get();

        for ( JsonDelegationResultEntry entry : results ) {
            assertEquals("1", entry.getId());
            assertEquals(tl.getDelegationData()[0], entry.getData());
            assertEquals("ACTIVE", entry.getStatus());
        }

    }

    @Test
    @Ignore
    public void discoverDelegationForId() throws SQLException, IOException, ClassNotFoundException {

        tl.injectToken("12345");

        Response rsp = target("/v1.0/search/2").queryParam("token", "12345").request().get();
        String response = rsp.readEntity(String.class);

        mLog.debug("Received response: " + response);
        assertEquals(200, rsp.getStatus());
    }

    @Test
    public void discoverNonExistingDelegation() throws SQLException, IOException, ClassNotFoundException {

        tl.injectToken("12345");

        Response rsp = target("/v1.0/search").queryParam("hash", "12345").queryParam("token", "12345").request().get();
        String response = rsp.readEntity(String.class);

        mLog.debug("Received response: " + response);
        assertEquals(200, rsp.getStatus());
    }

    @Test
    public void discoverNonExistingDelegationForId() throws SQLException, IOException, ClassNotFoundException {

        tl.injectToken("12345");

        Response rsp = target("/v1.0/search/1111").queryParam("hash", "12345").queryParam("token", "12345").request().get();
        String response = rsp.readEntity(String.class);
        mLog.debug("Received response: " + response);
        assertEquals(400, rsp.getStatus());
    }

}
