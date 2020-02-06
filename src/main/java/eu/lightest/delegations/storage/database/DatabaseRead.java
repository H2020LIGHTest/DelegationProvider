package eu.lightest.delegations.storage.database;

import eu.lightest.delegations.impl.exceptions.DelegationHashMissingException;
import eu.lightest.delegations.model.database.DelegationDataSet;
import eu.lightest.delegations.model.database.DelegationKeyDataSet;
import eu.lightest.delegations.model.json.IJsonDelegationResult;
import eu.lightest.delegations.impl.exceptions.DelegationHashInvalidException;
import eu.lightest.delegations.impl.exceptions.DelegationIdInvalidException;
import eu.lightest.delegations.storage.IStorageRead;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.sql.SQLException;
import java.util.List;

public class DatabaseRead implements IStorageRead {

    private Log mLog = LogFactory.getLog(DatabaseRead.class);

    @Override
    public IJsonDelegationResult readAll(String hash) throws DelegationHashInvalidException {
        if ( hash == null ) {
            throw new DelegationHashInvalidException("No hash given!");
        }

        IJsonDelegationResult result = null;
        try {
            result = DatabaseController.getInstance().readAllDelegations(hash);
            if ( result ==  null ) {
                throw new DelegationHashInvalidException("No results found!");
            }
        } catch (SQLException e) {
            mLog.error(e);
            return null;
        }

        return result;
    }

    @Override
    public DelegationDataSet readDelegation(String id) throws DelegationIdInvalidException, SQLException {
        if ( id == null ) {
            throw new DelegationIdInvalidException("No id given!");
        }

        return DatabaseController.getInstance().readDelegation(new Integer(id));
    }

    @Override
    public List<DelegationDataSet> readDelegationHash(String hash) throws DelegationIdInvalidException, SQLException, DelegationHashMissingException {
        if ( hash == null ) {
            throw new DelegationHashMissingException();
        }

        return DatabaseController.getInstance().readDelegation(hash);
    }

    @Override
    public DelegationKeyDataSet readDelegationKey(String id) throws DelegationIdInvalidException, SQLException {
        if ( id ==  null ) {
            throw new DelegationIdInvalidException("No id given!");
        }

        return DatabaseController.getInstance().readDelegationKey(new Integer(id));
    }

    @Override
    public String readTokenFromChallengeAndResponse(String challenge, String response) throws SQLException {
        return DatabaseController.getInstance().readTokenFromChallengeAndResponse(challenge, response);
    }

    @Override
    public boolean verifyToken(String challenge, String token) throws SQLException {
        return DatabaseController.getInstance().verifyToken(challenge, token);
    }

    @Override
    public boolean verifyToken(String token) throws SQLException {
        return DatabaseController.getInstance().verifyToken(token);
    }

    @Override
    public DelegationDataSet readRevokedDelegation(String hash) throws SQLException {
        return DatabaseController.getInstance().readRevokedDelegationByHash(hash);
    }
}
