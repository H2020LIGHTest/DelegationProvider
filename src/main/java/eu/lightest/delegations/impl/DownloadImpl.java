package eu.lightest.delegations.impl;

import eu.lightest.delegations.impl.exceptions.DelegationIdInvalidException;
import eu.lightest.delegations.model.database.DelegationKeyDataSet;
import eu.lightest.delegations.storage.IStorageRead;
import eu.lightest.delegations.storage.StorageFactory;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.sql.SQLException;

public class DownloadImpl {
    public DelegationKeyDataSet getEncryptionKey(String id) throws IOException, SQLException, DelegationIdInvalidException {

        if ( id == null ) {
            throw new NotImplementedException("Not implemented");
        }

        IStorageRead reader = StorageFactory.getInstance().getStorageRead();

        return reader.readDelegationKey(id);
    }
}
