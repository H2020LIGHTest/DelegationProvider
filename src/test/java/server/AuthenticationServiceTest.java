package server;

import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.services.AuthenticationService;
import lib.TestDataLib;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.*;

import javax.crypto.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.*;
import java.security.InvalidKeyException;
import java.sql.SQLException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class AuthenticationServiceTest extends JerseyTest {

    private static Log mLog = LogFactory.getLog(AuthenticationServiceTest.class);

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
        return new ResourceConfig(AuthenticationService.class);
    }


    @Test
    public void getChallengeFromServer() {
        Entity data = Entity.entity(Base64.encodeBase64String(tl.getDelegationKey()[0].getPublic().getEncoded()), MediaType.TEXT_PLAIN);
        Response rsp = target("/v1.0/auth").request().post(data);
        assertEquals(200, rsp.getStatus());

        String challenge = rsp.readEntity(String.class);
        mLog.debug("Challenge: " + challenge);
        assertNotNull(challenge);
    }

    @Test
    @Ignore
    public void answerChallengeFromServer() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, SQLException {
        Entity data = Entity.entity(Base64.encodeBase64String(tl.getDelegationKey()[0].getPublic().getEncoded()), MediaType.TEXT_PLAIN);
        Response rsp = target("/v1.0/auth").request().post(data);
        assertEquals(200, rsp.getStatus());

        String challenge = rsp.readEntity(String.class);
        mLog.debug("Challenge: " + challenge);
        assertNotNull(challenge);

        String answer = tl.decode(challenge, tl.getDelegationKey()[0].getPrivate());

        mLog.debug("Answer: '" + answer + "'");
        Entity result = Entity.entity("key=" + challenge + "&result=" + answer, MediaType.APPLICATION_FORM_URLENCODED);
        rsp = target("/v1.0/auth/result").request().post(result);

        assertEquals(200, rsp.getStatus());
        String token = rsp.readEntity(String.class);
        mLog.debug("Token: " + token);

        assertNotNull(token);
    }

    @Test
    public void sendEmptyKey() {
        Response rsp = target("/v1.0/auth").request().post(null);
        assertEquals(400, rsp.getStatus());

    }


    @Test
    public void sendAuthRequestIssue22() {
        // Invalid keyspec
        String key="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+QcVFiQHnmzx+gHJJu2y2lrr5nCyCRKCr4OZTuwUErnF+EzdNj50k9t5CsEYZzwpfB/Zsly4TYMl1sucCuG5nGmRr1OfMFHvDhIOZVltFw/togXjvFMVXtGXozWW";
        Entity entity = Entity.entity(key, MediaType.TEXT_PLAIN);
        Response rsp = target("/v1.0/auth").request().post(entity);
        assertEquals(400, rsp.getStatus());
    }


}
