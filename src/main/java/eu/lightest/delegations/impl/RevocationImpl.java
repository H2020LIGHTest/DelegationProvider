package eu.lightest.delegations.impl;

import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.model.database.DelegationDataSet;
import eu.lightest.delegations.model.json.JsonRevokedDelegationResponse;
import eu.lightest.delegations.storage.IStorageRead;
import eu.lightest.delegations.storage.IStorageWrite;
import eu.lightest.delegations.storage.StorageFactory;

import java.io.IOException;
import java.sql.SQLException;

public class RevocationImpl {
    public JsonRevokedDelegationResponse revokeDelegation(String id, String reason) throws IOException, SQLException, DelegationIdInvalidException, DelegationIdMissingException, DelegationRevocationFailedException, DelegationIdAlreadyRevokedException, DelegationAlreadyRevokedException {
        if ( id == null ) {
            throw new DelegationIdInvalidException("No Id provided");
        }

        IStorageRead reader = StorageFactory.getInstance().getStorageRead();

        DelegationDataSet data = reader.readDelegation(id);

        if ( data == null ) {
            throw new DelegationIdInvalidException("Id has not been found in database!");
        }

        if ( data.getStatus().contains("REVOKED") ) {
            throw new DelegationIdAlreadyRevokedException("The delegation is already revoked!");
        }

        IStorageWrite writer = StorageFactory.getInstance().getStorageWrite();
        writer.revokeDelegationById(data.getId(), reason, System.currentTimeMillis());

        DelegationDataSet revokedData = reader.readDelegation(id);

        return new JsonRevokedDelegationResponse(revokedData.getId(), revokedData.getHash(), revokedData.getStatus());
    }

    public JsonRevokedDelegationResponse delegationRevoked(String hash) throws DelegationIdInvalidException, IOException, SQLException {
        if ( hash == null ) {
            throw new DelegationIdInvalidException("No hash provided!");
        }

        IStorageRead reader = StorageFactory.getInstance().getStorageRead();

        DelegationDataSet data = reader.readRevokedDelegation(hash);

        JsonRevokedDelegationResponse rsp = null;

        if ( data == null ) {
            rsp = new JsonRevokedDelegationResponse(0, hash, "UNKNOWN");
        } else {
            rsp = new JsonRevokedDelegationResponse(data.getId(), data.getHash(), data.getStatus());
        }

        return rsp;
    }
}
