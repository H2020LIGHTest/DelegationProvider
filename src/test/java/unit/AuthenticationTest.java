package unit;

import eu.lightest.delegations.impl.AuthenticationImpl;
import eu.lightest.delegations.impl.exceptions.*;
import eu.lightest.delegations.storage.database.DatabaseController;
import lib.TestDataLib;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.*;
import java.io.IOException;
import java.security.*;
import java.security.InvalidKeyException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class AuthenticationTest {

    private Log mLog = LogFactory.getLog(AuthenticationTest.class);
    private TestDataLib tl = new TestDataLib();

    @Before
    public void setup() throws DelegationHashMissingException, DelegationKeyMissingException, DelegationWriteException, NoSuchAlgorithmException, IOException, SQLException, DelegationDataMissingException, DelegationIdMissingException, ClassNotFoundException {
        tl.setup(true);
    }

    @After
    public void cleanup() throws IOException, SQLException {
        tl.cleanup();
    }

    @Test
    public void getAuthenticationToken() throws InvalidKeyException, SQLException, IOException, ChallengeMissingException, ResponseMissingException, TokenMissingException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        AuthenticationImpl impl = new AuthenticationImpl();

        String challenge = impl.generateChallenge(Base64.encodeBase64String(tl.getDelegationKey()[0].getPublic().getEncoded()));

        mLog.debug("Challenge: " + challenge);


        Connection con = DatabaseController.getInstance().getConnection();
        try(PreparedStatement stmt = con.prepareStatement("SELECT uid, challenge, response, token, exp_date from authentication where challenge = ?")) {
            stmt.setString(1, challenge);

            try(ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    mLog.debug("UID: " + rs.getInt(1));
                    mLog.debug("Challenge: " + rs.getString(2));
                    mLog.debug("Response: " + rs.getString(3));
                    mLog.debug("Token: " + rs.getString(4));
                    mLog.debug("ExpDate: " + rs.getDate(5));

                    assertEquals(1, rs.getInt(1));
                    assertEquals(challenge, rs.getString(2));
                    return;
                }
            }
        }

        tl.dumpAuthenticationTable();

        assertFalse(true);
    }

    @Test
    public void answerChallengeWrong() throws ChallengeMissingException, TokenMissingException, IOException, SQLException, ResponseMissingException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        AuthenticationImpl impl = new AuthenticationImpl();

        String challenge = impl.generateChallenge(Base64.encodeBase64String(tl.getDelegationKey()[0].getPublic().getEncoded()));

        mLog.debug("Challenge: " + challenge);

        String response = null;
        String valid_token = null;

        Connection con = DatabaseController.getInstance().getConnection();
        try(PreparedStatement stmt = con.prepareStatement("SELECT uid, challenge, response, token, exp_date from authentication where challenge = ?")) {
            stmt.setString(1, challenge);

            try(ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    mLog.debug("UID: " + rs.getInt(1));
                    mLog.debug("Challenge: " + rs.getString(2));
                    mLog.debug("Response: " + rs.getString(3));
                    mLog.debug("Token: " + rs.getString(4));
                    mLog.debug("ExpDate: " + rs.getDate(5));

                    assertEquals(1, rs.getInt(1));
                    assertEquals(challenge, rs.getString(2));

                    response = rs.getString(3);
                    valid_token = rs.getString(4);
                }
            }

        }
        String token = impl.answerChallengeForToken(challenge, response);

        assertEquals(valid_token, token);

    }

    @Test
    public void fullAuthenticationCycle() throws IOException, ResponseMissingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, ChallengeMissingException, SQLException, TokenMissingException, IllegalBlockSizeException {
        AuthenticationImpl impl = new AuthenticationImpl();

        String challenge = impl.generateChallenge(Base64.encodeBase64String(tl.getDelegationKey()[0].getPublic().getEncoded()));

        mLog.debug("Challenge: " + challenge);

        String answer = tl.decode(challenge, tl.getDelegationKey()[0].getPrivate());

        String token = impl.answerChallengeForToken(challenge, answer);

        mLog.debug("Token: " + token);

        assertNotEquals(null, token);
        assertTrue(impl.verifyToken(challenge, token));
    }

    @Test
    public void useInvalidToken() throws IOException, ResponseMissingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, ChallengeMissingException, SQLException, TokenMissingException, IllegalBlockSizeException {
        AuthenticationImpl impl = new AuthenticationImpl();

        String challenge = impl.generateChallenge(Base64.encodeBase64String(tl.getDelegationKey()[0].getPublic().getEncoded()));

        mLog.debug("Challenge: " + challenge);

        String answer = tl.decode(challenge, tl.getDelegationKey()[0].getPrivate());

        String token = impl.answerChallengeForToken(challenge, answer);

        mLog.debug("Token: " + token);

        token = token + "FAKE";

        assertFalse(impl.verifyToken(challenge, token));
    }


}
