package eu.lightest.delegations.impl;

import eu.lightest.delegations.impl.exceptions.DelegationHashInvalidException;
import eu.lightest.delegations.impl.exceptions.DelegationIdInvalidException;
import eu.lightest.delegations.model.database.DelegationDataSet;
import eu.lightest.delegations.model.json.IJsonDelegationResult;
import eu.lightest.delegations.storage.IStorageRead;
import eu.lightest.delegations.storage.StorageFactory;

import java.io.IOException;
import java.sql.SQLException;

public class DiscoveryImpl {
    public IJsonDelegationResult getAllDelegationsForHash(String hash) throws IOException, DelegationHashInvalidException {
        IStorageRead reader = StorageFactory.getInstance().getStorageRead();
        return reader.readAll(hash);
    }

    public DelegationDataSet getSpecificDelegation(String id) throws IOException, SQLException, DelegationIdInvalidException {
        IStorageRead reader = StorageFactory.getInstance().getStorageRead();
        return reader.readDelegation(id);
    }
}
