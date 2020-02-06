package eu.lightest.delegations.services;

import com.google.gson.Gson;
import eu.lightest.delegations.DelegationProviderProperties;
import eu.lightest.delegations.impl.AuthenticationImpl;
import eu.lightest.delegations.impl.exceptions.AuthenticationTokenInvalidException;
import eu.lightest.delegations.model.database.DelegationDataSet;
import eu.lightest.delegations.model.database.DelegationKeyDataSet;
import eu.lightest.delegations.model.json.JsonDownloadDelegation;
import eu.lightest.delegations.impl.DiscoveryImpl;
import eu.lightest.delegations.impl.DownloadImpl;
import eu.lightest.delegations.impl.exceptions.DelegationIdInvalidException;
import eu.lightest.delegations.storage.StorageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("/{api_version}")
public class DownloadService {

    @PathParam("api_version")
    private String  mApiVersion;

    Log mLog = LogFactory.getLog(DownloadService.class);

    @PostConstruct
    private void initialise() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        mLog.info("----- Download -----");
        mLog.info("Version: " + properties.getGitPropertyRevId());
        StorageFactory.getInstance().configure(properties);
    }

    @PreDestroy
    private void close() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        StorageFactory.getInstance().close(properties);
        mLog.info("----- Download End -----");
    }

    @GET
    @Path("/download/{id}")
    public Response downloadDelegationWithId(@PathParam("id") String id,
                                             @QueryParam("token") String token) {

        mLog.debug("Received download request for\n"
                    + "\tId: " + id + "\n"
                    + "\tToken: " + token);

        Response rsp = null;
        DiscoveryImpl discovery = new DiscoveryImpl();
        AuthenticationImpl auth = new AuthenticationImpl();

        try {
            auth.verifyToken(token);

            DelegationDataSet data = discovery.getSpecificDelegation(id);
            JsonDownloadDelegation delegation = new JsonDownloadDelegation(data.getId(), data.getData(), data.getKey());

            Gson gson = new Gson();
            String serializedDelegation = gson.toJson(delegation);

            rsp = Response.status(Response.Status.OK).entity(serializedDelegation).build();

        } catch (IOException | SQLException | DelegationIdInvalidException e) {
            mLog.error(e.getMessage(), e);
            rsp = Response.status(Response.Status.BAD_REQUEST).build();
        } catch (AuthenticationTokenInvalidException e) {
            mLog.error(e.getMessage(), e);
            rsp = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return rsp;
    }

    @GET
    @Path("/download/{id}/key")
    public Response downloadKeyForDelegationId(@PathParam("id") String id,
                                               @QueryParam("token") String token) {
        Response rsp = null;

        DownloadImpl download = new DownloadImpl();
        AuthenticationImpl auth = new AuthenticationImpl();

        try {
            auth.verifyToken(token);
            DelegationKeyDataSet data = download.getEncryptionKey(id);

            Gson gson = new Gson();
            String serializedDelegationKey = gson.toJson(data);

            rsp = Response.status(Response.Status.OK).entity(serializedDelegationKey).build();
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
