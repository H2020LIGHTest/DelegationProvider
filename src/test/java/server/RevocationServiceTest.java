package server;

import com.google.gson.Gson;
import eu.lightest.delegations.DelegationProviderProperties;
import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.model.json.JsonRevokedDelegationResponse;
import eu.lightest.delegations.services.RevocationService;
import eu.lightest.delegations.storage.database.DatabaseController;
import lib.TestDataLib;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.*;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class RevocationServiceTest extends JerseyTest {

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
    public void setupTest() throws NoSuchAlgorithmException, SQLException, DelegationWriteException, DelegationHashMissingException, IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationIdMissingException,  ClassNotFoundException {
        tl.setup();
    }

    @After
    public void cleanupDatabase() throws SQLException, IOException, ClassNotFoundException {
        tl.cleanupDatabase();
    }

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(RevocationService.class);
    }

    @Test
    public void revokeDelegation() throws SQLException, IOException, ClassNotFoundException {

        tl.injectToken("12345");

        Response rsp = target("/v1.0/revoke/1").queryParam("token", "12345").queryParam("reason", "test revocation").request().post(null);
        assertEquals(200, rsp.getStatus());
    }

    @Test
    public void revokeNonExistingDelegation() throws SQLException, IOException, ClassNotFoundException {

        tl.injectToken("12345");

        Response rsp = target("/v1.0/revoke/1234").queryParam("token", "12345").queryParam("reason", "this should not work").request().post(null);
        assertEquals(400, rsp.getStatus());
    }

    @Test
    @Ignore
    public void revokeDelegationTwice() throws IOException, SQLException, ClassNotFoundException {

        tl.injectToken("12345");

        //DelegationProviderProperties properties = new DelegationProviderProperties();
        //DatabaseController.getInstance().connect(properties.getPropertyDatabaseAddress());
        Response rsp = target("/v1.0/revoke/2").queryParam("token", "12345").queryParam("reason", "test revocation").request().post(null);
        assertEquals(200, rsp.getStatus());

        rsp = target("/v1.0/revoke/2").queryParam("token", "12345").queryParam("reason", "test revocation").request().post(null);
        assertEquals(400, rsp.getStatus());

    }

    @Test
    @Ignore
    public void readNotRevokedExistingDelegation() throws SQLException, IOException, ClassNotFoundException {
        tl.injectToken("12345");

        Response rsp = target("/v1.0/revoke/" + tl.getDelegationHash()[1]).queryParam("token", "12345").request().get();
        assertEquals(200, rsp.getStatus());

        String response = rsp.readEntity(String.class);

        Gson gson = new Gson();
        JsonRevokedDelegationResponse revokedDelegation = gson.fromJson(response, JsonRevokedDelegationResponse.class);

        assertEquals("ACTIVE", revokedDelegation.getStatus());

    }

    @Test
    public void readNotRevokedNotExistingDelegation() throws SQLException, IOException, ClassNotFoundException {
        tl.injectToken("12345");

        String invalidDelegationHash = "asdflkjasdflkjasdlfkj";
        Response rsp = target("/v1.0/revoke/" + invalidDelegationHash).queryParam("token", "12345").request().get();
        assertEquals(200, rsp.getStatus());

        String response = rsp.readEntity(String.class);

        Gson gson = new Gson();
        JsonRevokedDelegationResponse revokedDelegation = gson.fromJson(response, JsonRevokedDelegationResponse.class);

        assertEquals("UNKNOWN", revokedDelegation.getStatus());
    }

    @Test
    public void readRevokedExistingDelegation() throws SQLException, IOException, ClassNotFoundException {
        tl.injectToken("12345");

        String invalidDelegationHash = "asdflkjasdflkjasdlfkj";

        Response rsp = target("/v1.0/revoke/1").queryParam("token", "12345").queryParam("reason", "test revocation").request().post(null);
        assertEquals(200, rsp.getStatus());

        rsp = target("/v1.0/revoke/" + tl.getDelegationHash()[0]).queryParam("token", "12345").request().get();
        assertEquals(200, rsp.getStatus());

        String response = rsp.readEntity(String.class);

        Gson gson = new Gson();
        JsonRevokedDelegationResponse revokedDelegation = gson.fromJson(response, JsonRevokedDelegationResponse.class);

        assertEquals("REVOKED", revokedDelegation.getStatus());
    }
}
