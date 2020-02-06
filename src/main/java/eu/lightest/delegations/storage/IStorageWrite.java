package eu.lightest.delegations.storage;

import eu.lightest.delegations.impl.exceptions.*;

import java.sql.SQLException;

public interface IStorageWrite {

    Integer write(String hash, String pubkey, String delegation) throws DelegationWriteException, DelegationHashMissingException, DelegationKeyMissingException, DelegationDataMissingException;
    void writeKey(Integer id, String hash, String key) throws DelegationIdMissingException, DelegationHashMissingException, DelegationKeyMissingException, DelegationWriteException;
    void revokeDelegationById(int id, String reason, long revocationDate) throws DelegationIdMissingException, DelegationRevocationFailedException, SQLException, DelegationAlreadyRevokedException;

    int writeChallenge(String response, String challenge, String token) throws ChallengeMissingException, SQLException, ResponseMissingException, TokenMissingException;
}
