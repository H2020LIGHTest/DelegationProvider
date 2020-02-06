package eu.lightest.delegations.services;

import com.google.gson.Gson;
import eu.lightest.delegations.DelegationProviderProperties;
import eu.lightest.delegations.impl.AuthenticationImpl;
import eu.lightest.delegations.impl.RevocationImpl;
import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.model.json.JsonRevokedDelegationResponse;
import eu.lightest.delegations.storage.StorageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("/{api_version}")
public class RevocationService {

    @PathParam("api_version")
    private String mApiVersion;

    private Log mLog = LogFactory.getLog(RevocationService.class);

    @PostConstruct
    private void initialise() throws IOException {
        mLog.info("----- Revocation -----");
        DelegationProviderProperties properties = new DelegationProviderProperties();
        mLog.info("Version: " + properties.getGitPropertyRevId());
        StorageFactory.getInstance().configure(properties);
    }

    @PreDestroy
    private void close() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        StorageFactory.getInstance().close(properties);
        mLog.info("----- Revocation End -----");
    }

    @GET
    @Path("/revoke/{hash}")
    public Response checkDelegation(@PathParam("hash") String hash) {
        Response rsp = null;

        RevocationImpl impl = new RevocationImpl();

        try {
            JsonRevokedDelegationResponse rspData = impl.delegationRevoked(hash);

            Gson gson = new Gson();

            String serialized = gson.toJson(rspData);

            rsp = Response.status(Response.Status.OK).entity(serialized).build();

        } catch (DelegationIdInvalidException | IOException | SQLException e) {
            mLog.error(e.getMessage(), e);
            rsp = Response.status(Response.Status.BAD_REQUEST).build();
        }

        return rsp;
    }

    @POST
    @Path("/revoke/{id}")
    public Response revokeDelegation(@PathParam("id") String id,
                                     @QueryParam("token") String token,
                                     @QueryParam("reason") String reason) {
        Response rsp = null;

        RevocationImpl impl = new RevocationImpl();
        AuthenticationImpl auth = new AuthenticationImpl();

        try {
            auth.verifyToken(token);
            JsonRevokedDelegationResponse rspData = impl.revokeDelegation(id, reason);

            Gson gson = new Gson();

            String serialized = gson.toJson(rspData);

            rsp = Response.status(Response.Status.OK).entity(serialized).build();

        } catch (IOException
                | SQLException
                | DelegationIdInvalidException
                | DelegationIdMissingException
                | DelegationRevocationFailedException
                | DelegationIdAlreadyRevokedException e) {
            mLog.error(e.getMessage(), e);
            rsp = Response.status(Response.Status.BAD_REQUEST).build();
        } catch (DelegationAlreadyRevokedException e) {
            mLog.error(e.getMessage(), e);
            rsp = Response.status(Response.Status.BAD_REQUEST).encoding("The delegation is already revoked!").build();
        } catch (AuthenticationTokenInvalidException e) {
            mLog.error(e.getMessage(), e);
            rsp = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return rsp;
    }
}
