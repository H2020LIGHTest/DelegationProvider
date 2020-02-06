package eu.lightest.delegations.storage.database;

import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.storage.IStorageWrite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;

public class DatabaseWrite implements IStorageWrite {

    private Log mLog = LogFactory.getLog(DatabaseWrite.class);

    @Override
    public Integer write(String hash, String pubkey, String delegation) throws DelegationWriteException, DelegationHashMissingException, DelegationKeyMissingException, DelegationDataMissingException {
        if ( hash == null || hash.isEmpty() ) {
            throw new DelegationHashMissingException();
        }

        if ( pubkey == null || pubkey.isEmpty() ) {
            throw new DelegationKeyMissingException();
        }

        if ( delegation == null || delegation.isEmpty() ) {
            throw new DelegationDataMissingException();
        }

        try {
            return DatabaseController.getInstance().addDelegation(hash, pubkey, delegation);
        } catch (SQLException e) {
            mLog.error(e.getMessage());
            throw new DelegationWriteException();
        }
    }

    @Override
    public void writeKey(Integer id, String hash, String key) throws DelegationIdMissingException, DelegationHashMissingException, DelegationKeyMissingException, DelegationWriteException {
        if ( id == 0 ) {
            throw new DelegationIdMissingException();
        }

        if ( hash == null || hash.isEmpty() ) {
            throw new DelegationHashMissingException();
        }

        if ( key == null || key.isEmpty() ) {
            throw new DelegationKeyMissingException();
        }

        try {
            DatabaseController.getInstance().addDelegationKey( id, key );
        } catch (SQLException e) {
            mLog.error(e.getMessage());
            throw new DelegationWriteException();
        }
    }

    @Override
    public void revokeDelegationById(int id, String reason, long revocationDate) throws DelegationIdMissingException, DelegationRevocationFailedException, SQLException, DelegationAlreadyRevokedException {
       if ( id == 0 ) {
           throw new DelegationIdMissingException();
       }

       DatabaseController.getInstance().revokeDelegationById( id, reason, revocationDate);
    }

    @Override
    public int writeChallenge(String response, String challenge, String token) throws ChallengeMissingException, SQLException, ResponseMissingException, TokenMissingException {
        if ( challenge ==  null ) {
            throw new ChallengeMissingException();
        }
        if ( response == null ) {
            throw new ResponseMissingException();
        }

        if ( token == null ) {
            throw new TokenMissingException();
        }
        return DatabaseController.getInstance().writeChallenge(challenge, response, token);
    }
}
