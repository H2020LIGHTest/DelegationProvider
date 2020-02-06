package server;

import com.google.gson.Gson;
import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.model.database.DelegationKeyDataSet;
import eu.lightest.delegations.model.json.JsonDownloadDelegation;
import eu.lightest.delegations.services.DownloadService;
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

public class DownloadServiceTest extends JerseyTest {

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
        return new ResourceConfig(DownloadService.class);
    }

    @Test
    @Ignore
    public void downloadDelegation() throws SQLException, IOException, ClassNotFoundException {

        tl.injectToken("12345");

        Response rsp = target("/v1.0/download/1").queryParam("token", "12345").request().get();
        assertEquals(200, rsp.getStatus());

        String response = rsp.readEntity(String.class);

        Gson gson = new Gson();
        JsonDownloadDelegation data = gson.fromJson(response, JsonDownloadDelegation.class);

        assertEquals("1", data.getId());
    }

    @Test
    public void downloadDelegationKey() throws SQLException, IOException, ClassNotFoundException {

        tl.injectToken("12345");

        Response rsp = target("/v1.0/download/1/key").queryParam("token", "12345").request().get();
        assertEquals(200, rsp.getStatus());

        String response = rsp.readEntity(String.class);

        Gson gson = new Gson();

        DelegationKeyDataSet key = gson.fromJson(response, DelegationKeyDataSet.class);
        assertEquals(1, key.getId());

    }

}
