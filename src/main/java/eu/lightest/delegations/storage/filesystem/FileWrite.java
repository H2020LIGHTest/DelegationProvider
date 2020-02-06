package eu.lightest.delegations.storage.filesystem;

import eu.lightest.delegations.impl.exceptions.ChallengeMissingException;
import eu.lightest.delegations.impl.exceptions.DelegationIdMissingException;
import eu.lightest.delegations.impl.exceptions.DelegationRevocationFailedException;
import eu.lightest.delegations.impl.exceptions.ResponseMissingException;
import eu.lightest.delegations.storage.IStorageWrite;
import org.apache.commons.lang3.NotImplementedException;

import java.sql.SQLException;

public class FileWrite implements IStorageWrite {
    @Override
    public Integer write(String hash, String pubkey, String delegation) {
        throw new NotImplementedException("Not yet implemented");
    }

    @Override
    public void writeKey(Integer id, String hash, String key) {
        throw new NotImplementedException("Not yet implemented");
    }

    @Override
    public void revokeDelegationById(int id, String reason, long revocationDate) throws DelegationIdMissingException, DelegationRevocationFailedException, SQLException {
        throw new NotImplementedException("Not yet implemented");
    }

    @Override
    public int writeChallenge(String response, String challenge, String token) throws ChallengeMissingException, SQLException, ResponseMissingException {
        return 0;
    }

}
