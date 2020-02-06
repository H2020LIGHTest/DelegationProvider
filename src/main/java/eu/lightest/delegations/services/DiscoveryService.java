package eu.lightest.delegations.services;

import com.google.gson.Gson;
import eu.lightest.delegations.DelegationProviderProperties;
import eu.lightest.delegations.impl.AuthenticationImpl;
import eu.lightest.delegations.impl.exceptions.AuthenticationTokenInvalidException;
import eu.lightest.delegations.model.database.DelegationDataSet;
import eu.lightest.delegations.model.json.IJsonDelegationResult;
import eu.lightest.delegations.impl.DiscoveryImpl;
import eu.lightest.delegations.impl.exceptions.DelegationHashInvalidException;
import eu.lightest.delegations.impl.exceptions.DelegationIdInvalidException;
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
public class DiscoveryService {

    @PathParam ("api_version")
    private String mApiVersion;

    private Log mLog = null;

    @PostConstruct
    private void initialise() throws IOException {
        mLog = LogFactory.getLog(DiscoveryService.class);

        DelegationProviderProperties properties = new DelegationProviderProperties();
        mLog.info("----- Discovery -----");
        mLog.info("Version: " + properties.getGitPropertyRevId());
        StorageFactory.getInstance().configure(properties);
    }

    @PreDestroy
    private void close() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        StorageFactory.getInstance().close(properties);
        mLog.info("----- Discovery End -----");
    }

    @GET
    @Path("/search")
    public Response searchDelegations(@QueryParam("hash") String hash,
                                      @QueryParam("token") String token) {

        mLog.debug("Received request to search for delegation\n"
                    + "\tHash: " + hash + "\n"
                    + "\tToken: " + token);

        DiscoveryImpl impl = new DiscoveryImpl();
        AuthenticationImpl auth = new AuthenticationImpl();

        Response rsp = null;

        try {
            auth.verifyToken(token);
            IJsonDelegationResult result = impl.getAllDelegationsForHash(hash);

            String rspMsg = "";
            if (result.get().isEmpty()) {
                rspMsg = "The delegation does not exist!";
            } else {
                Gson gson = new Gson();
                rspMsg = gson.toJson(result);
            }

            rsp = Response.status(Response.Status.OK).entity(rspMsg).build();
        } catch (IOException | SQLException | DelegationHashInvalidException e) {
            mLog.error(e.getMessage(), e);
            rsp = Response.status(Response.Status.BAD_REQUEST).build();
        } catch (AuthenticationTokenInvalidException e) {
            mLog.error(e.getMessage(), e);
            rsp = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return rsp;
    }

    @GET
    @Path("/search/{id}")
    public Response searchSpecificDelegation(@PathParam("id") String id,
                                             @QueryParam("token") String token ) {
        mLog.debug("Received request to search for delegation\n"
                   + "\tId: " + id + "\n"
                   + "\tToken: " + token);

        DiscoveryImpl impl = new DiscoveryImpl();
        AuthenticationImpl auth = new AuthenticationImpl();

        Response rsp = null;

        try {
            auth.verifyToken(token);

            DelegationDataSet data = impl.getSpecificDelegation(id);

            if ( data == null ) {
                rsp = Response.status(Response.Status.BAD_REQUEST).entity("No data for the given id exists").build();
            } else {
                Gson gson = new Gson();
                String serializedData = gson.toJson(data);
                rsp = Response.status(Response.Status.OK).entity(serializedData).build();
            }
        } catch (IOException | SQLException | DelegationIdInvalidException e) {
            mLog.error(e.getMessage(), e);
            rsp = Response.status(Response.Status.BAD_REQUEST).build();
        } catch (AuthenticationTokenInvalidException e) {
            mLog.error(e.getMessage(), e);
            rsp = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return rsp;
    }

}
