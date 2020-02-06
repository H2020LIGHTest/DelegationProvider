package eu.lightest.delegations.impl;

import eu.lightest.delegations.impl.exceptions.AuthenticationTokenInvalidException;
import eu.lightest.delegations.impl.exceptions.ChallengeMissingException;
import eu.lightest.delegations.impl.exceptions.ResponseMissingException;
import eu.lightest.delegations.impl.exceptions.TokenMissingException;
import eu.lightest.delegations.storage.IStorageRead;
import eu.lightest.delegations.storage.IStorageWrite;
import eu.lightest.delegations.storage.StorageFactory;
import iaik.security.provider.IAIK;
import iaik.xml.crypto.XSecProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;

public class AuthenticationImpl {

    private static Log mLog = LogFactory.getLog(AuthenticationImpl.class);

    public AuthenticationImpl() {
        if (Security.getProvider("IAIK") == null) {
            IAIK.addAsProvider();
        }

        Provider provider = new XSecProvider();
        Security.addProvider(provider);

        Provider otherXMLDsigProvider = Security.getProvider("XMLDSig");
        if ( otherXMLDsigProvider != null ) {
            Security.removeProvider(otherXMLDsigProvider.getName());
            Security.addProvider(otherXMLDsigProvider);
        }
    }

    public String generateChallenge(String key) throws InvalidKeyException, ChallengeMissingException, SQLException, IOException, ResponseMissingException, TokenMissingException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {

        if ( key == null ) {
            throw new InvalidKeyException("Key is null!");
        }

        int responseValue = generateSecureChallengeValue(1);
        String response = String.valueOf(responseValue);

        mLog.debug("Response(S): " + response);
        mLog.debug("Response(B): " + response.getBytes());

        byte[] challenge = encryptChallenge( responseValue, key );

        String base64Challenge = Base64.encodeBase64String(challenge);

        String token = generateSecurityToken(responseValue);

        saveChallenge(base64Challenge, response, token);
        return base64Challenge;
    }

    public String answerChallengeForToken(String challenge, String response) throws IOException, SQLException {

       IStorageRead reader = StorageFactory.getInstance().getStorageRead();

       return reader.readTokenFromChallengeAndResponse(challenge, response);
    }

    public boolean verifyToken(String challenge, String token) throws IOException, SQLException {
        IStorageRead reader = StorageFactory.getInstance().getStorageRead();

        return reader.verifyToken(challenge, token);
    }

    public boolean verifyToken(String token) throws IOException, SQLException, AuthenticationTokenInvalidException {
        IStorageRead reader = StorageFactory.getInstance().getStorageRead();

        if( !reader.verifyToken(token) ) {
            throw new AuthenticationTokenInvalidException();
        }
        return true;
    }

    private int saveChallenge(String challenge, String response, String token) throws IOException, ChallengeMissingException, SQLException, ResponseMissingException, TokenMissingException {
        IStorageWrite writer = StorageFactory.getInstance().getStorageWrite();
        return writer.writeChallenge(challenge, response, token);
    }

    private int generateSecureChallengeValue(int numBytes) {
        SecureRandom rsec = new SecureRandom();
        byte[] seed = rsec.generateSeed(numBytes);
        rsec.setSeed(seed);

        int val = rsec.nextInt() % 512;

        if (val > 0)
            val = -val;

        return val;
    }

    private byte[] encryptChallenge(int value, String publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String challenge = String.valueOf(value);

        Cipher cipher = Cipher.getInstance("RSA/None/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));

        return cipher.doFinal(challenge.getBytes());
    }

    public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(base64PublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            mLog.error(e);
        }
        return publicKey;
    }

    private String generateSecurityToken(int challengeValue) {
       String token = null;

       challengeValue += generateSecureChallengeValue(2046);

       token = Base64.encodeBase64String(String.valueOf(challengeValue).getBytes());

       return token;
    }
}
