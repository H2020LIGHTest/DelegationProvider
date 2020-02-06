package eu.lightest.delegations.storage.filesystem;

import eu.lightest.delegations.model.database.DelegationDataSet;
import eu.lightest.delegations.model.database.DelegationKeyDataSet;
import eu.lightest.delegations.model.json.IJsonDelegationResult;
import eu.lightest.delegations.impl.exceptions.DelegationHashInvalidException;
import eu.lightest.delegations.impl.exceptions.DelegationIdInvalidException;
import eu.lightest.delegations.storage.IStorageRead;

import java.sql.SQLException;
import java.util.List;

public class FileRead implements IStorageRead {
    @Override
    public IJsonDelegationResult readAll(String hash) throws DelegationHashInvalidException {
        return null;
    }

    @Override
    public DelegationDataSet readDelegation(String id) throws DelegationIdInvalidException, SQLException {
        return null;
    }

    @Override
    public List<DelegationDataSet> readDelegationHash(String id) throws DelegationIdInvalidException, SQLException {
        return null;
    }

    @Override
    public DelegationKeyDataSet readDelegationKey(String id) throws DelegationIdInvalidException, SQLException {
        return null;
    }

    @Override
    public String readTokenFromChallengeAndResponse(String challenge, String response) throws SQLException {
        return null;
    }

    @Override
    public boolean verifyToken(String challenge, String token) {
        return false;
    }

    @Override
    public boolean verifyToken(String token) throws SQLException {
        return false;
    }

    @Override
    public DelegationDataSet readRevokedDelegation(String hash) throws SQLException {
        return null;
    }
}
