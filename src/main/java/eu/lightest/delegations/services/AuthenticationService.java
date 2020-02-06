package eu.lightest.delegations.services;

import eu.lightest.delegations.DelegationProviderProperties;
import eu.lightest.delegations.impl.AuthenticationImpl;
import eu.lightest.delegations.impl.exceptions.ChallengeMissingException;
import eu.lightest.delegations.impl.exceptions.ResponseMissingException;
import eu.lightest.delegations.impl.exceptions.TokenMissingException;
import eu.lightest.delegations.storage.StorageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@Path("/{api_version}")
public class AuthenticationService {

    private Log mLog = LogFactory.getLog(AuthenticationService.class);

    @PathParam("api_version")
    private String mApiVersion;

    @PostConstruct
    private void initialise() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();


        mLog.info("----- Authentication -----");
        mLog.info("Version: " + properties.getGitPropertyRevId());
        StorageFactory.getInstance().configure(properties);
    }

    @PreDestroy
    private void close() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        StorageFactory.getInstance().close(properties);
        mLog.info("----- Authentication End -----");
    }


    @POST
    @Path("/auth")
    public Response startAuthentication(String key) {
        Response rsp;

        AuthenticationImpl impl = new AuthenticationImpl();

        try {
            if (key ==  null || key.isEmpty())
            {
                mLog.debug("key is null!");
                rsp = Response.status(Response.Status.BAD_REQUEST).entity("Wrong data provided, could not create a challenge").build();
                return rsp;
            }

            String challenge = impl.generateChallenge(key);

            mLog.debug("Challenge: " + challenge);
            rsp = Response.ok(challenge).build();

        } catch (NullPointerException
                | InvalidKeyException
                | ChallengeMissingException
                | IOException
                | SQLException
                | ResponseMissingException
                | TokenMissingException
                | NoSuchAlgorithmException
                | BadPaddingException
                | NoSuchPaddingException
                | IllegalBlockSizeException e) {
            mLog.error(e.getMessage(),e);
            rsp = Response.status(Response.Status.BAD_REQUEST).build();
        }

        return rsp;
    }

    @POST
    @Path("/auth/result")
    @Consumes("application/x-www-form-urlencoded")
    public Response endAuthentication( @FormParam("key") String key,
                                       @FormParam("result") String result) {
        Response rsp = null;

        AuthenticationImpl impl = new AuthenticationImpl();
        try {

            key = key.replace(" ", "+");
            mLog.debug("Key: " + key);
            mLog.debug("Result: " + result);

            String token = impl.answerChallengeForToken(key, result);
            if ( token == null ) {
                rsp = Response.serverError().build();
            } else {
                rsp = Response.ok(token).build();
            }
        } catch (IOException | SQLException e) {
            mLog.error(e.getMessage(),e);
            rsp = Response.serverError().build();
        }

        return rsp;
    }
}
