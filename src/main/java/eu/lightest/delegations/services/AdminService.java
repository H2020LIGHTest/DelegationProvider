package eu.lightest.delegations.services;

import eu.lightest.delegations.DelegationProviderProperties;
import eu.lightest.delegations.storage.StorageFactory;
import eu.lightest.delegations.storage.database.DatabaseController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Path("/{api_version}")
public class AdminService {
    private Log mLog = LogFactory.getLog(AdminService.class);

    @PathParam("api_version")
    private String mApiVersion;

    @PostConstruct
    private void initialise() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();


        mLog.info("----- Admin -----");
        mLog.info("Version: " + properties.getGitPropertyRevId());
        StorageFactory.getInstance().configure(properties);
    }

    @PreDestroy
    private void close() throws IOException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        StorageFactory.getInstance().close(properties);
        mLog.info("----- Admin End -----");
    }

    @GET
    @Path("close")
    public Response closeDb() throws SQLException {
        DatabaseController.getInstance().close();
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("settimeout")
    public Response setTimeout() throws SQLException {
       Connection c = DatabaseController.getInstance().getConnection();
       try(PreparedStatement stmt = c.prepareStatement("pragma busy_timeout=30000;")){
            stmt.execute();
       }
       return Response.status(Response.Status.OK).build();
    }
}
