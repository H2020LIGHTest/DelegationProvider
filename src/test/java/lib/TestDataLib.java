package lib;

import eu.lightest.delegations.DelegationProviderProperties;
import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.storage.IStorageWrite;
import eu.lightest.delegations.storage.StorageFactory;
import eu.lightest.delegations.storage.database.DatabaseController;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import unit.AuthenticationTest;

import javax.crypto.*;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDataLib {

    private Log mLog = LogFactory.getLog(TestDataLib.class);
    private String[] mDelegationData = {
            "Delegation 01",
            "Delegation 02",
            "Delegation 03",
            "Delegation 04",
            "Delegation 05",
            "Delegation 06",
            "Delegation 07",
            "Delegation 08",
            "Delegation 09",
            "Delegation 10",
    };


    private KeyPair[] mDelegationKey = new KeyPair[10];

    private String[] mDelegationHash = new String[10];

    private SecretKey[] mDelegationEncryptionKey = new SecretKey[10];


    public String[] getDelegationData() {
        return mDelegationData;
    }

    public KeyPair[] getDelegationKey() {
        return mDelegationKey;
    }

    public String[] getDelegationHash() {
        return mDelegationHash;
    }

    public SecretKey[] getDelegationEncryptionKey() {
        return mDelegationEncryptionKey;
    }

    public void beforeSetup() throws SQLException, ClassNotFoundException, IOException, DelegationHashMissingException, DelegationWriteException, NoSuchAlgorithmException, DelegationDataMissingException, DelegationKeyMissingException, DelegationIdMissingException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        DatabaseController.getInstance().connect(properties.getPropertyDatabaseAddress());
        DatabaseController.getInstance().setupDatabase();
    }

    public void setup() throws SQLException, ClassNotFoundException, IOException, DelegationHashMissingException, DelegationWriteException, NoSuchAlgorithmException, DelegationDataMissingException, DelegationKeyMissingException, DelegationIdMissingException {
        beforeSetup();
        setupDelegationDatabase();
        DatabaseController.getInstance().close();
    }

    public void setup(boolean leaveOpen) throws SQLException, ClassNotFoundException, IOException, DelegationHashMissingException, DelegationWriteException, NoSuchAlgorithmException, DelegationDataMissingException, DelegationKeyMissingException, DelegationIdMissingException {
        beforeSetup();
        setupDelegationDatabase();
        if ( !leaveOpen  )
            DatabaseController.getInstance().close();
    }

    public void cleanup() throws SQLException {
        DatabaseController.getInstance().close();

        File database = new File("delegations.db");
        if (database.delete()) {
            mLog.info("Successfully removed delegationstest.db database");
        } else {
            mLog.error("Could not remove database delegationstest.db!");
        }
    }


    public void cleanupDatabase() throws IOException, SQLException, ClassNotFoundException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        while( DatabaseController.getInstance().getConnection() != null ) {
            DatabaseController.getInstance().close();
        };
        DatabaseController.getInstance().connect(properties.getPropertyDatabaseAddress());
        DatabaseController.getInstance().setupDatabase();
        Connection con = DatabaseController.getInstance().getConnection();
        if ( con != null ) {
            try(PreparedStatement clearDelegation = con.prepareStatement("DELETE FROM delegation")) {
                clearDelegation.execute();
            }

            try(PreparedStatement clearKeys = con.prepareStatement("DELETE FROM keys")) {
                clearKeys.execute();
            }

            try(PreparedStatement clearAuth = con.prepareStatement("DELETE FROM authentication")) {
                clearAuth.execute();
            }

            try(PreparedStatement clearRevocation = con.prepareStatement("DELETE FROM revocation_list")) {
                clearRevocation.execute();
            }

            DatabaseController.getInstance().commit();
        }
        DatabaseController.getInstance().close();
    }

    public void dumpAuthenticationTable() throws SQLException {
        Connection con = DatabaseController.getInstance().getConnection();
        try(PreparedStatement stmt = con.prepareStatement("SELECT uid, challenge FROM authentication")) {

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    mLog.error("Uid: " + rs.getInt(1) + " Challenge: " + rs.getString(2));
                }
            }
        }

    }

    public PrivateKey string2privatekey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Remove the "BEGIN" and "END" lines, as well as any whitespace

        String pkcs8Pem = key.toString();

        byte [] pkcs8EncodedBytes = Base64.decodeBase64(pkcs8Pem);

        // extract the private key

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    public String decode(String challenge, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        Cipher cipher = Cipher.getInstance("RSA/None/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] byteChallenge = Base64.decodeBase64(challenge);

        byte[] result = cipher.doFinal(byteChallenge);

        byte[] num = new byte[4];
        /*
        num[0] = result[124];
        num[1] = result[125];
        num[2] = result[126];
        num[3] = result[127];

         */

        num[0] = result[result.length-4];
        num[1] = result[result.length-3];
        num[2] = result[result.length-2];
        num[3] = result[result.length-1];

        mLog.debug(new String(num));
        return new String(num);

    }


    public void injectToken(String token) throws SQLException, IOException, ClassNotFoundException {
        DelegationProviderProperties properties = new DelegationProviderProperties();
        DatabaseController.getInstance().connect(properties.getPropertyDatabaseAddress());
        Connection con = DatabaseController.getInstance().getConnection();
        if ( con != null ) {
            try (PreparedStatement injectToken = con.prepareStatement("INSERT INTO authentication (challenge, response, exp_date, token) VALUES ('', '', '',  ?)")) {
                injectToken.setString(1, token);
                injectToken.execute();
            }
        }
        DatabaseController.getInstance().close();
    }


    public void setupDelegationDatabase() throws NoSuchAlgorithmException, SQLException, IOException, DelegationKeyMissingException, DelegationDataMissingException, DelegationHashMissingException, DelegationWriteException, DelegationIdMissingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        for (int i = 0; i < mDelegationData.length; i++) {
            byte[] digested = digest.digest(mDelegationData[i].getBytes("UTF-8"));
            mDelegationHash[i] = Base64.encodeBase64String(digested);
        }

        for (int i = 0; i < mDelegationData.length; i++) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            mDelegationKey[i] = keyPairGenerator.generateKeyPair();
        }

        for (int i = 0; i < mDelegationData.length; i++) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            mDelegationEncryptionKey[i] = keyGenerator.generateKey();
        }

        for (int i = 0; i < mDelegationData.length; i++) {
            IStorageWrite writer = StorageFactory.getInstance().getStorageWrite();
            writer.write(mDelegationHash[i], Base64.encodeBase64String(mDelegationKey[i].getPublic().getEncoded()), mDelegationData[i]);
            writer.writeKey(i + 1, mDelegationHash[i], Base64.encodeBase64String(mDelegationEncryptionKey[i].getEncoded()));
        }

    }
}
