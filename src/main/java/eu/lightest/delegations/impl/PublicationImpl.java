package eu.lightest.delegations.impl;

import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.model.database.DelegationDataSet;
import eu.lightest.delegations.model.json.IJsonDelegationResult;
import eu.lightest.delegations.model.json.IJsonPublishDelegation;
import eu.lightest.delegations.model.json.IJsonPublishDelegationKey;
import eu.lightest.delegations.model.json.JsonDelegationCreated;
import eu.lightest.delegations.storage.IStorageRead;
import eu.lightest.delegations.storage.IStorageWrite;
import eu.lightest.delegations.storage.StorageFactory;

import java.io.IOException;
import java.sql.SQLException;

public class PublicationImpl {
    public JsonDelegationCreated publishDelegationJson(IJsonPublishDelegation jsonDelegation) throws IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationHashMissingException, DelegationWriteException, SQLException, DelegationIdInvalidException, DelegationHashInvalidException {

        IStorageRead reader = StorageFactory.getInstance().getStorageRead();
        IStorageWrite writer = StorageFactory.getInstance().getStorageWrite();
        Integer id = writer.write(jsonDelegation.getHash(), jsonDelegation.getPublicKey(), jsonDelegation.getData());

        return new JsonDelegationCreated(id);
    }

    public boolean publishDelegationKeyJson(IJsonPublishDelegationKey jsonKey) throws IOException, DelegationKeyMissingException, DelegationIdMissingException, DelegationHashMissingException, DelegationWriteException, SQLException, DelegationIdInvalidException {
        if ( jsonKey.getId() == 0 ) {
            throw new DelegationIdMissingException();
        }

        if ( jsonKey.getHash() == null || jsonKey.getHash().isEmpty() ) {
            throw new DelegationHashMissingException();
        }

        if ( jsonKey.getKey() == null || jsonKey.getKey().isEmpty() ) {
            throw new DelegationKeyMissingException();
        }

        IStorageRead reader = StorageFactory.getInstance().getStorageRead();
        DelegationDataSet data = reader.readDelegation(jsonKey.getId().toString());
        if ( data != null ) {
            IStorageWrite writer = StorageFactory.getInstance().getStorageWrite();
            writer.writeKey(jsonKey.getId(), jsonKey.getHash(), jsonKey.getKey());
            return true;
        }
        return false;
    }
}
