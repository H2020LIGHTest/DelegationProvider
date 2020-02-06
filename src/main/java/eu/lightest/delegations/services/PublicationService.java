package eu.lightest.delegations.services;

import com.google.gson.Gson;
import eu.lightest.delegations.DelegationProviderProperties;
import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.model.json.IJsonDelegationCreated;
import eu.lightest.delegations.model.json.JsonPublishDelegation;
import eu.lightest.delegations.model.json.JsonPublishDelegationKey;
import eu.lightest.delegations.impl.PublicationImpl;
import eu.lightest.delegations.storage.StorageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("/{api_version}")
public class PublicationService {

    @PathParam("api_version")
    protected String mApiVersion;

    protected Log mLog = LogFactory.getLog(PublicationService.class);

    protected PublicationImpl mPub = new PublicationImpl();

    @PostConstruct
    private void initialise() throws IOException {
        mLog.info("----- Publication -----");
        DelegationProviderProperties properties = new DelegationProviderProperties();
        mLog.info("Version: " + properties.getGitPropertyRevId());
        StorageFactory.getInstance().configure(properties);
    }

    @PreDestroy
    private void close() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        StorageFactory.getInstance().close(properties);
        mLog.info("----- Publication End -----");
    }

    /**
     * Publish a delegation on the server. This endpoint receives the data for a delegation.
     * The data is currently a string and must contain the JSON format for @see{JsonPublishDelegation}.
     *
     * Possible error cases:
     *  - Delegation already exists (usually the provider will try to create a new unique id, but may fail due to the same payload)
     *  - Database or Filesystem not properly configured
     *
     * @param data JsonPublishDeleagation formated JSON structure containing the information
     * @return CREATED - Delegation was successfully published. Returned JSON contains the id of the delegation
     * @return BAD_REQUEST - One or more problems during the creation have occurred. See returned error message for information.
     */
    @POST
    @Path("/publish")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publishDelegationJson(String data) {
        mLog.debug("Received request to publish a delegation");
        mLog.debug("Data: ");
        mLog.debug(data);

        Response rsp = null;
        try {
            JsonPublishDelegation jsonDelegation = JsonPublishDelegation.fromString(data);

            if ( jsonDelegation == null ) {
                mLog.error("No, or empty delegation data passed!");
                throw new DelegationDataMissingException();
            }

            IJsonDelegationCreated result = mPub.publishDelegationJson(jsonDelegation);
            if ( result == null ) {
                throw new DelegationAlreadyExistsException();
            }
            Gson json = new Gson();

            String serialized = json.toJson(result);

            rsp = Response.status(Response.Status.CREATED).entity(serialized).build();
            mLog.debug("Response build, ready to send");
            mLog.info("Successfully published");

        } catch (IOException
                | DelegationWriteException
                | DelegationHashMissingException
                | DelegationDataMissingException
                | DelegationKeyMissingException
                | SQLException
                | DelegationIdInvalidException
                | DelegationHashInvalidException
                | DelegationAlreadyExistsException e) {
            mLog.error(e.getMessage(),e);
            rsp = Response.status(Response.Status.BAD_REQUEST).build();
        }

        return rsp;
    }

    /**
     * Publish a delegation key
     *
     * @return CREATED - Key was successfully added to the delegation
     * @return BAD_REQUEST - One or more problems during the createion have ocured. Se returned error message for information.
     */
    @POST
    @Path("/publish_key")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publishDelegationKeyJson(String data) throws IOException {
        mLog.debug("Received request to publish a delegation");
        mLog.debug("Data: ");
        mLog.debug(data);

        JsonPublishDelegationKey jsonKey = JsonPublishDelegationKey.fromString(data);

        Response rsp = null;
        try {
            if (!mPub.publishDelegationKeyJson(jsonKey)) {
                rsp = Response.status(Response.Status.BAD_REQUEST).build();
                mLog.error("Publication failed due to wrong jsonKey data");
            }
            else {
                rsp = Response.status(Response.Status.CREATED).build();
                mLog.info("Successfully published key");
            }
        } catch (DelegationKeyMissingException
                | DelegationIdMissingException
                | DelegationHashMissingException
                | DelegationWriteException
                | SQLException
                | DelegationIdInvalidException e) {
            mLog.error(e.getMessage(),e);
            rsp = Response.status(Response.Status.BAD_REQUEST).build();
        }

        return rsp;
    }
}
