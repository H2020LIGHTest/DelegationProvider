package eu.lightest.delegations.services;

import eu.lightest.delegations.DelegationProviderProperties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/{api_version}")
public class TestService {

    @PathParam("api_version")
    private String mApiVersion;

    @GET
    @Path("/test")
    public Response getTest() throws IOException {
        DelegationProviderProperties p = new DelegationProviderProperties();
        return Response.status(Response.Status.OK).entity("Api: " + mApiVersion  + "\nRev: " + p.getGitPropertyRevId()).build();
    }
}
