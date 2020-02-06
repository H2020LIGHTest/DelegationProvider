package eu.lightest.delegations.storage;

import eu.lightest.delegations.DelegationProviderProperties;
import eu.lightest.delegations.storage.database.DatabaseController;
import eu.lightest.delegations.storage.database.DatabaseRead;
import eu.lightest.delegations.storage.database.DatabaseWrite;
import eu.lightest.delegations.storage.filesystem.FileRead;
import eu.lightest.delegations.storage.filesystem.FileWrite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.sql.SQLException;


public class StorageFactory {

    private static StorageFactory mInstance = null;
    private StorageSystem mStorageSystem = null;
    private Log mLog = LogFactory.getLog(StorageFactory.class);

    private StorageFactory() {

    }

    public static StorageFactory getInstance() throws IOException {
        if (mInstance == null) {
            mInstance = new StorageFactory();

            DelegationProviderProperties properties = new DelegationProviderProperties();

            mInstance.configure(properties);
        }

        return mInstance;
    }

    public void configure(DelegationProviderProperties properties) {
        String storage = properties.getPropertyStorageType();
        mStorageSystem = StorageSystem.valueOf(storage);

        if ( mStorageSystem == StorageSystem.database ) {
            try {
                if ( !DatabaseController.getInstance().isConnected() ) {
                    DatabaseController.getInstance().connect(properties.getPropertyDatabaseAddress());
                    DatabaseController.getInstance().setupDatabase();
                }
            } catch (ClassNotFoundException | SQLException e) {
                mLog.error(e);
            }
        }
    }

    public void close(DelegationProviderProperties properties) {
        String storage = properties.getPropertyStorageType();
        mStorageSystem = StorageSystem.valueOf(storage);

        if ( mStorageSystem == StorageSystem.database ) {
            try {
                DatabaseController.getInstance().close();
            } catch (SQLException e) {
                mLog.error(e);
            }
        }
    }

    public StorageSystem getStorageSystem() {
        return mStorageSystem;
    }

    public IStorageWrite getStorageWrite() {
        IStorageWrite writeImpl = null;

        switch(mStorageSystem) {
            case database:
                writeImpl = new DatabaseWrite();
                break;

            case filesystem:
                writeImpl = new FileWrite();
                break;

            default:
                mLog.error("Invalid storage system configured!");
                break;
        }

        return writeImpl;
    }

    public IStorageRead getStorageRead() {
        IStorageRead readImpl = null;

        switch(mStorageSystem) {

            case database:
                readImpl = new DatabaseRead();
                break;

            case filesystem:
                readImpl = new FileRead();
                break;

            default:
                mLog.error("Invalid storage system configured!");
                break;
        }

        return readImpl;
    }
}
