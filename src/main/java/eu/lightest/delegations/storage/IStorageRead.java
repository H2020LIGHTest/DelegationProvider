package eu.lightest.delegations.storage;

import eu.lightest.delegations.impl.exceptions.DelegationHashMissingException;
import eu.lightest.delegations.model.database.DelegationDataSet;
import eu.lightest.delegations.model.database.DelegationKeyDataSet;
import eu.lightest.delegations.model.json.IJsonDelegationResult;
import eu.lightest.delegations.impl.exceptions.DelegationHashInvalidException;
import eu.lightest.delegations.impl.exceptions.DelegationIdInvalidException;

import java.sql.SQLException;
import java.util.List;

public interface IStorageRead {
    IJsonDelegationResult readAll(String hash) throws DelegationHashInvalidException;

    DelegationDataSet readDelegation(String id) throws DelegationIdInvalidException, SQLException;
    List<DelegationDataSet> readDelegationHash(String id) throws DelegationIdInvalidException, SQLException, DelegationHashMissingException;

    DelegationKeyDataSet readDelegationKey(String id) throws DelegationIdInvalidException, SQLException;

    String readTokenFromChallengeAndResponse(String challenge, String response) throws SQLException;

    boolean verifyToken(String challenge, String token) throws SQLException;
    boolean verifyToken(String token) throws SQLException;

    DelegationDataSet readRevokedDelegation(String hash) throws SQLException;
}
