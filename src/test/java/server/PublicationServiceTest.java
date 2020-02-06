package server;

import com.google.gson.Gson;
import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.model.json.JsonDelegationCreated;
import eu.lightest.delegations.model.json.JsonPublishDelegation;
import eu.lightest.delegations.model.json.JsonPublishDelegationKey;
import eu.lightest.delegations.services.PublicationService;
import lib.TestDataLib;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;


public class PublicationServiceTest extends JerseyTest {

    private static Log mLog = LogFactory.getLog(PublicationServiceTest.class);
    private static TestDataLib tl = new TestDataLib();

    @BeforeClass
    public static void setup() throws IOException, SQLException, ClassNotFoundException, DelegationWriteException, DelegationKeyMissingException, NoSuchAlgorithmException, DelegationDataMissingException, DelegationHashMissingException, DelegationIdMissingException {
        tl.beforeSetup();
    }

    @AfterClass
    public static void cleanup() throws SQLException {
        tl.cleanup();
    }

    @After
    public void cleanupDatabase() throws SQLException, IOException, ClassNotFoundException {
        tl.cleanupDatabase();
    }

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(PublicationService.class);
    }

    @Test
    public void publishEmptyDelegationWithNoJson() {

        Entity test = Entity.entity("", MediaType.APPLICATION_JSON);
        Response rsp = target("/v1.0/publish").request().post(test);
        Assert.assertEquals(400, rsp.getStatus());

    }

    @Test
    public void publishDelegationWithCorrectEmptyJson() {
        JsonPublishDelegation data = new JsonPublishDelegation();

        Gson gson = new Gson();

        Entity test = Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON);
        Response rsp = target("/v1.0/publish").request().post(test);
        Assert.assertEquals(400, rsp.getStatus());

    }

    @Test
    public void publishSimpleDelegation() {
        JsonPublishDelegation data = new JsonPublishDelegation();
        data.setData("This is a test");
        data.setPublicKey("This is a public key");
        data.setPublicKeyHash("This is a public key hash");

        Gson gson = new Gson();

        Entity test = Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON);
        Response rsp = target("/v1.0/publish").request().post(test);
        Assert.assertEquals(201, rsp.getStatus());
        Assert.assertTrue(rsp.readEntity(String.class).matches("..id..*"));
    }

    @Test
    public void publishSameDelegationMultipleTimes() {
        JsonPublishDelegation data = new JsonPublishDelegation();
        data.setData("This is a test");
        data.setPublicKey("This is a public key");
        data.setPublicKeyHash("This is a public key hash");

        Gson gson = new Gson();

        Entity test = Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON);
        Response rsp = target("/v1.0/publish").request().post(test);
        Assert.assertEquals(201, rsp.getStatus());
        Assert.assertTrue(rsp.readEntity(String.class).matches("..id..*"));

        rsp = target("/v1.0/publish").request().post(test);
        Assert.assertEquals(201, rsp.getStatus());
    }

    @Test
    public void publishDelegationKeyForExistingDelegation() {
        // Publish Delegation
        JsonPublishDelegation data = new JsonPublishDelegation();
        data.setData("This is a test");
        data.setPublicKey("This is a public key");
        data.setPublicKeyHash("This is a public key hash");

        Gson gson = new Gson();

        Entity test = Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON);
        Response rsp = target("/v1.0/publish").request().post(test);
        Assert.assertEquals(201, rsp.getStatus());
        String result = rsp.readEntity(String.class);
        Assert.assertTrue(result.matches("..id..*"));

        // Convert returned id to a json object, so that we can get the id and pass it for the next request
        JsonDelegationCreated delegation = gson.fromJson(result, JsonDelegationCreated.class);

        mLog.info("DelegationId: " + delegation);
        mLog.info("Extracted Id: " + delegation.getId());

        JsonPublishDelegationKey delegationKey = new JsonPublishDelegationKey();
        delegationKey.setId( delegation.getId() );
        delegationKey.setKey("This is the key for the delegation!");
        delegationKey.setHash(data.getHash());

        Entity delegationEntity = Entity.entity(gson.toJson(delegationKey), MediaType.APPLICATION_JSON);
        rsp = target("/v1.0/publish_key").request().post(delegationEntity);
        Assert.assertEquals(201, rsp.getStatus());

    }

    @Test
    public void publishDelegationKeyForNonExistingDelegation() {
        // Publish Delegation

        Gson gson = new Gson();

        JsonPublishDelegationKey delegationKey = new JsonPublishDelegationKey();
        delegationKey.setId( 1 );
        delegationKey.setKey("This is the key for the delegation!");
        delegationKey.setHash("This is a fake delegation hash");

        Entity delegationEntity = Entity.entity(gson.toJson(delegationKey), MediaType.APPLICATION_JSON);
        Response rsp = target("/v1.0/publish_key").request().post(delegationEntity);
        Assert.assertEquals(400, rsp.getStatus());


    }

    @Test
    public void publishMultipleDelegationKeyForExistingDelegation() {
        // Publish Delegation
        JsonPublishDelegation data = new JsonPublishDelegation();
        data.setData("This is a test");
        data.setPublicKey("This is a public key");
        data.setPublicKeyHash("This is a public key hash");

        Gson gson = new Gson();

        Entity test = Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON);
        Response rsp = target("/v1.0/publish").request().post(test);
        Assert.assertEquals(201, rsp.getStatus());
        String result = rsp.readEntity(String.class);
        Assert.assertTrue(result.matches("..id..*"));

        // Convert returned id to a json object, so that we can get the id and pass it for the next request
        JsonDelegationCreated delegation = gson.fromJson(result, JsonDelegationCreated.class);

        mLog.info("DelegationId: " + delegation);
        mLog.info("Extracted Id: " + delegation.getId());

        JsonPublishDelegationKey delegationKey = new JsonPublishDelegationKey();
        delegationKey.setId( delegation.getId() );
        delegationKey.setKey("This is the key for the delegation!");
        delegationKey.setHash(data.getHash());

        Entity delegationEntity = Entity.entity(gson.toJson(delegationKey), MediaType.APPLICATION_JSON);
        rsp = target("/v1.0/publish_key").request().post(delegationEntity);
        Assert.assertEquals(201, rsp.getStatus());

        delegationEntity = Entity.entity(gson.toJson(delegationKey), MediaType.APPLICATION_JSON);
        rsp = target("/v1.0/publish_key").request().post(delegationEntity);
        Assert.assertEquals(201, rsp.getStatus());

    }

    @Test
    public void publishMultipleDelegationsForSameUser() {
        JsonPublishDelegation data = new JsonPublishDelegation();
        data.setData("This is a test");
        data.setPublicKey("This is a public key");
        data.setPublicKeyHash("This is a public key hash");

        Gson gson = new Gson();

        Entity test = Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON);
        Response rsp = target("/v1.0/publish").request().post(test);
        Assert.assertEquals(201, rsp.getStatus());
        Assert.assertTrue(rsp.readEntity(String.class).matches("..id..*"));

        data.setData("This is another dataset");
        data.setPublicKeyHash("1");
        test = Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON);
        rsp = target("/v1.0/publish").request().post(test);
        Assert.assertEquals(201, rsp.getStatus());
    }
}
