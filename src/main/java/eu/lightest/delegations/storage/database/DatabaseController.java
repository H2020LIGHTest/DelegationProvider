package eu.lightest.delegations.storage.database;

import eu.lightest.delegations.impl.exceptions.DelegationAlreadyRevokedException;
import eu.lightest.delegations.model.database.DelegationDataSet;
import eu.lightest.delegations.model.database.DelegationKeyDataSet;
import eu.lightest.delegations.model.database.RevocationListDataSet;
import eu.lightest.delegations.model.json.IJsonDelegationResult;
import eu.lightest.delegations.model.json.JsonDelegationResult;
import eu.lightest.delegations.model.json.JsonDelegationResultEntry;
import eu.lightest.delegations.impl.exceptions.DelegationIdMissingException;
import eu.lightest.delegations.impl.exceptions.DelegationRevocationFailedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {

    private  Connection mConnection = null;

    private Log mLog = LogFactory.getLog(DatabaseController.class);

    private static DatabaseController mInstance = null;

    private boolean mConnectedStatus = false;

    private DatabaseController() {

    }

    public static DatabaseController getInstance() {
        if(mInstance == null) {
            mInstance = new DatabaseController();
        }
        return mInstance;
    }

    public boolean isConnected() {
        return mConnectedStatus;
    }

    public void connect(String server) throws ClassNotFoundException, SQLException {
        if ( mConnection == null || !mConnectedStatus) {
            Class.forName("org.sqlite.JDBC");
            mConnection = DriverManager.getConnection(server);
            mLog.info("Sucessfully connected to database '" + server + "'");
        } else {
            mLog.info("Connection to '" + server + "' already exists. Nothing neesd to be done.");
        }
        mConnectedStatus = true;
    }

    public void close() throws SQLException {
        if ( mConnection != null ) {
            mLog.debug("Closing connection to database!");
            if (mConnection != null )
                mConnection.close();
            mConnection = null;
            mConnectedStatus = false;
        }
    }

    public Connection getConnection() {
        return mConnection;
    }

    public void setupDatabase() throws SQLException {
        try(Statement stmt = mConnection.createStatement()) {

            if (!tableDeleationExists()) {
                createTableDelegation(stmt);
            }

            if (!tableDelegationKeysExists()) {
                createTableDelegationKeys(stmt);
            }

            if (!tableRevocationListExists()) {
                createTableRevocationList(stmt);
            }

            if (!tableAuthenticationExists()) {
                createTableAuthentication(stmt);
            }

            mLog.info("Successfully created tables!");
        }
    }

    private boolean tableAuthenticationExists() throws SQLException {
        return checkIfTableExists("authentication");
    }

    private boolean tableRevocationListExists() throws SQLException {
        return checkIfTableExists("revocation_list");
    }

    private boolean tableDelegationKeysExists() throws SQLException {
        return checkIfTableExists("keys");
    }

    private boolean tableDeleationExists() throws SQLException {
        return checkIfTableExists( "delegation");
    }

    private boolean checkIfTableExists(String tableName) throws SQLException {
        try(PreparedStatement checkTableExists = mConnection.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name=?;")) {
            checkTableExists.setString(1, tableName);

            try(ResultSet rs = checkTableExists.executeQuery()) {
                while (rs.next()) {
                    mLog.debug("Found table " + rs.getString("name"));
                    return true;
                }
            }
        }
        return false;
    }

    private void createTableAuthentication(Statement stmt) throws SQLException {
        String createTableAuthentication =
                "CREATE TABLE authentication (\n"
                        + "uid       INTEGER   Primary Key   AutoIncrement    NOT NULL,\n" // Primary key
                        + "challenge TEXT                                     NOT NULL,\n"
                        + "response  TEXT                                     NOT NULL,\n"
                        + "token     TEXT                                     NOT NULL,\n"
                        + "exp_date  DATETIME                                 NOT NULL\n"
                        + ")";

        mLog.debug("Creating authorisation table:\n" + createTableAuthentication);
        stmt.executeUpdate(createTableAuthentication);
    }

    private void createTableRevocationList(Statement stmt) throws SQLException {
        String createTableRevocationList =
                "CREATE TABLE revocation_list (\n"
                        + "id          INT               NOT NULL,\n" // Primary key, Foreign key to delegation
                        + "reason      TEXT              NOT NULL,\n"
                        + "revoke_date DATETIME          NOT NULL,\n"
                        + "FOREIGN KEY(id) REFERENCES delegation(id)\n"
                        + ")";

        mLog.debug("Creating table revocation_list:\n" + createTableRevocationList);
        stmt.executeUpdate( createTableRevocationList );
    }

    private void createTableDelegationKeys(Statement stmt) throws SQLException {
        String createTableKeys =
                "CREATE TABLE keys (\n"
                        + "id   INT                     NOT NULL,\n" // Primary key, Foreign key to delegation
                        + "key  TEXT                    NOT NULL,\n"
                        + "FOREIGN KEY(id) REFERENCES delegation(id)\n"
                        + ")";

        mLog.debug("Creating table keys:\n" + createTableKeys);
        stmt.executeUpdate( createTableKeys );
    }

    private void createTableDelegation(Statement stmt) throws SQLException {
        String createTableDelegation =
                "CREATE TABLE delegation (\n"
                        + "id   INT     Primary Key     NOT NULL,\n" // Primary key
                        + "hash TEXT                    NOT NULL,\n" // public key hash
                        + "key  TEXT                    NOT NULL,\n" // public key
                        + "data TEXT                    NOT NULL\n"  // delegation
                        + ")";

        mLog.debug("Creating table delegation:\n" + createTableDelegation);
        stmt.executeUpdate( createTableDelegation );
    }

    public void commit() throws SQLException {
        if(!mConnection.getAutoCommit()) {
            mConnection.commit();
        }
    }

    public Integer addDelegation(String hash, String key, String data) throws SQLException {

        Integer id = 0;

        try(Statement stmt = mConnection.createStatement()) {
            String getId = "SELECT max(id) as id from delegation";

            try(ResultSet rs = stmt.executeQuery(getId)) {

                while (rs.next()) {
                    id = rs.getInt("id") + 1;
                    mLog.debug("New Id: " + id);
                }
            }
        }

        mLog.debug("Inserting new delegation data:\n"
                    + "\thash: " + hash + "\n"
                    + "\tkey: " + key + "\n"
                    + "\tdata: " + data);

        try(PreparedStatement insertDelegation = mConnection.prepareStatement("INSERT INTO delegation (id, hash, key, data) VALUES (?,?,?,?)")) {
            insertDelegation.setInt(1, id);
            insertDelegation.setString(2, hash);
            insertDelegation.setString(3, key);
            insertDelegation.setString(4, data);
            insertDelegation.executeUpdate();
        }

        commit();

        mLog.info("New delegation added");

        return id;
    }

    public void addDelegationKey(int i, String s) throws SQLException {

        mLog.debug("Inserting new delegation key:\n"
                    + "\tid: " + i + "\n"
                    + "\tkey: " + s);

        try(PreparedStatement insertDelegationKey  = mConnection.prepareStatement("INSERT INTO keys (id, key) VALUES (?,?)")) {
            insertDelegationKey.setInt(1, i);
            insertDelegationKey.setString(2, s);
            insertDelegationKey.executeUpdate();
        }

        commit();

        mLog.info("New delegation key added");
    }

    public List<DelegationDataSet> readDelegation(String hash) throws SQLException {
        mLog.debug("Trying to find delegation with id: " + hash);

        List<DelegationDataSet> data = new ArrayList<>();
        try(PreparedStatement selectDelegation = mConnection.prepareStatement("SELECT * from delegation where hash= ?")) {
            selectDelegation.setString(1, hash);
            try(ResultSet rs = selectDelegation.executeQuery()) {
                while (rs.next()) {
                    mLog.debug("Found delegation:\n"
                            + "\tid: " + rs.getInt("id") + "\n"
                            + "\thash: " + rs.getString("hash") + "\n"
                            + "\tkey: " + rs.getString("key") + "\n"
                            + "\tdata: " + rs.getString("data"));
                    DelegationDataSet dataSet = new DelegationDataSet(rs.getInt("id"), rs.getString("hash"), rs.getString("key"), rs.getString("data"));
                    data.add( dataSet );
                }
            }
        }

        return data;

    }

    public DelegationDataSet readDelegation(int id) throws SQLException {
        mLog.debug("Trying to find delegation with id: " + id);

        DelegationDataSet dataSet = null;

        try(PreparedStatement selectDelegation =
                mConnection.prepareStatement(
                        "SELECT " +
                                "d.id as 'id', " +
                                "d.hash as 'hash', " +
                                "d.data as 'data', " +
                                "d.key as 'key', " +
                                "CASE when r.id IS NOT NULL " +
                                "then 'REVOKED' " +
                                "else 'ACTIVE' " +
                                "END AS 'status' " +
                            "from " +
                                "delegation d " +
                                " LEFT JOIN revocation_list as r " +
                                    "ON r.id = d.id "+
                            "where " +
                                "d.id = ?")) {
            selectDelegation.setInt(1, id);

            try(ResultSet rs = selectDelegation.executeQuery()) {


                while (rs.next()) {
                    mLog.debug("Found delegation:\n"
                            + "\tid: " + rs.getInt("id") + "\n"
                            + "\thash: " + rs.getString("hash") + "\n"
                            + "\tkey: " + rs.getString("key") + "\n"
                            + "\tdata: " + rs.getString("data") + "\n"
                            + "\tstatus: " + rs.getString("status"));

                    dataSet = new DelegationDataSet(rs.getInt("id"),
                            rs.getString("hash"),
                            rs.getString("key"),
                            rs.getString("data"),
                            rs.getString("status"));
                }
            }
        }


        return dataSet;
    }

    public DelegationKeyDataSet readDelegationKey(int id) throws SQLException {
        mLog.debug("Trying to find delegation key with id: " + id);
        DelegationKeyDataSet dkds = null;


        try(PreparedStatement selectDelegationKey =
                mConnection.prepareStatement(
                        "SELECT * " +
                            "from " +
                                "keys " +
                            "where " +
                                "id = ?")) {
            selectDelegationKey.setInt(1, id);

            try(ResultSet rs = selectDelegationKey.executeQuery()) {

                while (rs.next()) {
                    mLog.debug("Found key:\n"
                            + "\tId: " + rs.getInt("id") + "\n"
                            + "\tKey: " + rs.getString("key"));
                    dkds = new DelegationKeyDataSet(rs.getInt("id"), rs.getString("key"));
                }
            }

        }

        return dkds;
    }

    public void removeDelegation(int id) throws SQLException {
        try(PreparedStatement removeDelegationKey = mConnection.prepareStatement("DELETE FROM keys where id = ?")) {
            removeDelegationKey.setInt(1, id);

            if(!removeDelegationKey.execute()) {
                mLog.error("Couldn't remove delegation key linked to delegation with id " + id);
            }
        }

        try(PreparedStatement removeDelegation = mConnection.prepareStatement("DELETE FROM delegation where id = ?")) {
            removeDelegation.setInt(1, id);


            if (!removeDelegation.execute()) {
                mLog.error("Couldn't remove delegation with id " + id);
            }
        }

        commit();
    }

    public IJsonDelegationResult readAllDelegations(String hash) throws SQLException {
        IJsonDelegationResult results = new JsonDelegationResult();

        try(PreparedStatement readAllDelegations =
                mConnection.prepareStatement(
                        "SELECT " +
                                "d.id as 'id', " +
                                "d.hash as 'hash', " +
                                "d.data as 'data', " +
                                "d.key as 'key', " +
                                "CASE when r.id IS NOT NULL " +
                                    "then 'REVOKED' " +
                                    "else 'ACTIVE' " +
                                "END AS 'status' " +
                            "from " +
                                "delegation as d " +
                                " LEFT JOIN revocation_list as r " +
                                    "ON r.id = d.id "+
                            "where " +
                                "d.hash = ?"
                )) {
            readAllDelegations.setString(1, hash);

            try(ResultSet rs = readAllDelegations.executeQuery()) {

                while (rs.next()) {
                    mLog.debug("Delegation:\n"
                            + "\tId: " + rs.getInt("id") + "\n"
                            + "\tHash: " + rs.getString("hash") + "\n"
                            + "\tData: " + rs.getString("data") + "\n"
                            + "\tKey: " + rs.getString("key") + "\n"
                            + "\tStatus: " + rs.getString("status"));

                    JsonDelegationResultEntry entry = new JsonDelegationResultEntry();
                    entry.setId(String.valueOf(rs.getInt("id")));
                    entry.setData(rs.getString("data"));
                    entry.setStatus(rs.getString("status"));

                    results.add(entry);
                }
            }
        }

        commit();

        return results;
    }

    public void revokeDelegationById(int id, String reason, long nowMs) throws DelegationIdMissingException, SQLException, DelegationRevocationFailedException, DelegationAlreadyRevokedException {

        if ( id == 0) {
            throw new DelegationIdMissingException();
        }

        try(PreparedStatement checkExistingIdStmt = mConnection.prepareStatement("SELECT id FROM delegation WHERE id = ?")) {
            checkExistingIdStmt.setInt(1, id);

            try(ResultSet rs = checkExistingIdStmt.executeQuery()) {
                if (rs.getInt(1) != id) {
                    mLog.debug("Couldn't find a delegation with id '" + id + "'");
                    throw new DelegationIdMissingException();
                }
            }
        }

        mLog.debug("Found delegation -> starting revocation process");

        RevocationListDataSet revokedData = readRevokedDelegation(id);
        if ( revokedData != null && revokedData.getId() == id ) {
            throw new DelegationAlreadyRevokedException();
        }

        Date now = new Date(nowMs);

        try(PreparedStatement revokeDelegation = mConnection.prepareStatement("INSERT INTO revocation_list (id, reason, revoke_date) VALUES (?, ?, ?)")) {
            revokeDelegation.setInt(1, id);
            revokeDelegation.setString(2, reason);
            revokeDelegation.setDate(3, now);

            if (revokeDelegation.executeUpdate() == 0) {
                mLog.error("Failed to revoke delegation with id '" + id + "'");
                throw new DelegationRevocationFailedException();
            }

            mLog.info("Delegation " + id + " successfully revoked!");
        }


        commit();

    }

    public DelegationDataSet readRevokedDelegationByHash(String hash) throws SQLException {

        DelegationDataSet dds = null;

        try(PreparedStatement readRevokedDelegationFromList
                    = mConnection.prepareStatement(
                        "SELECT " +
                                "d.id, r.reason, r.revoke_date " +
                            "FROM " +
                                "delegation as d " +
                            "LEFT JOIN " +
                                "revocation_list as r " +
                            "ON " +
                                " r.id = d.id " +
                            "WHERE "+
                                " d.hash = ?")) {
            readRevokedDelegationFromList.setString(1, hash);

            try (ResultSet rs = readRevokedDelegationFromList.executeQuery()) {

                if (!rs.next()) {
                    mLog.debug("Nothing found!");
                    return null;
                }
                mLog.debug("Found data for hash " + hash);
                int id = rs.getInt(1);
                String reason = rs.getString(2);
                Date revokeDate = rs.getDate(3);

                if ( id == 0 ) {
                    dds = new DelegationDataSet(
                            rs.getInt(1),
                            hash,
                            null,
                            "UNKNOWN",
                            "UNKNOWN");
                } else
                if ( reason == null && revokeDate == null ) {
                    dds = new DelegationDataSet(
                            rs.getInt(1),
                            hash,
                            null,
                            "ACTIVE",
                            "ACTIVE");

                } else {
                    dds = new DelegationDataSet(
                            rs.getInt(1),
                            hash,
                            null,
                            "REVOKED",
                            "REVOKED");
                }

            }
        }

        commit();

        return dds;
    }

    public RevocationListDataSet readRevokedDelegation(int id ) throws SQLException {

        RevocationListDataSet rlds = null;

        try(PreparedStatement readRevokedDelegationFromList = mConnection.prepareStatement(
                "SELECT " +
                        " id, reason, revoke_date " +
                "FROM " +
                    " revocation_list " +
                "WHERE " +
                    "id = ?"
        )) {
            readRevokedDelegationFromList.setInt(1, id);

            try (ResultSet rs = readRevokedDelegationFromList.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }
                rlds = new RevocationListDataSet(rs.getInt(1), rs.getString(2), rs.getDate(3));

            }
        }

        commit();

        return rlds;
    }

    public int writeChallenge(String response, String challenge, String token) throws SQLException {


        Date exp = new Date(System.currentTimeMillis()+60000);
        try(PreparedStatement insertChallenge = mConnection.prepareStatement("INSERT INTO authentication (challenge, exp_date, response, token) VALUES (?,?,?,?)")) {
            insertChallenge.setString(1, challenge);
            insertChallenge.setDate(2, exp);
            insertChallenge.setString(3, response);
            insertChallenge.setString(4, token);

            insertChallenge.execute();

            mLog.debug("Challenge: " + challenge);
            mLog.debug("Response: " + response);
        }


        int uid = 0;

        try(PreparedStatement nextIdStmt = mConnection.prepareStatement("SELECT max(uid) FROM authentication")) {
            try(ResultSet rsId = nextIdStmt.executeQuery()) {

                while (rsId.next()) {
                    uid = rsId.getInt(1);
                    mLog.debug("max(UID) = " + uid);

                }
            }
        }

        mLog.debug("New UID: " + uid);

        commit();

        return uid;
    }

    public String readTokenFromChallengeAndResponse(String challenge, String response) throws SQLException {

        mLog.debug("Challenge: " + challenge);
        mLog.debug("Response: " + response);

        String token = null;

        try(PreparedStatement readToken = mConnection.prepareStatement("SELECT token from authentication WHERE challenge = ? AND response = ?")) {
            readToken.setString(1, challenge);
            readToken.setString(2, response);

            try(ResultSet rs = readToken.executeQuery()) {

                while (rs.next()) {
                    token = rs.getString(1);
                }
            }
        }

        mLog.debug("Token: " + token);
        return token;
    }

    public boolean verifyToken(String challenge, String token) throws SQLException {

        mLog.debug("Challenge: " + challenge);
        mLog.debug("Token: " + token);

        boolean rv = false;

        try(PreparedStatement verifyToken = mConnection.prepareStatement("SELECT token FROM authentication WHERE challenge = ? AND token = ?")) {
            verifyToken.setString(1, challenge);
            verifyToken.setString(2, token);

            try(ResultSet rs = verifyToken.executeQuery()) {

                while (rs.next()) {
                    String t = rs.getString(1);
                    if (t.compareTo(token) == 0) {
                        rv = true;
                    }
                }
            }
        }


        return rv;
    }

    public boolean verifyToken(String token) throws SQLException {
        mLog.debug("Token: " + token);

        boolean rv = false;

        if (token ==  null) {
            return false;
        }
        try(PreparedStatement verifyToken = mConnection.prepareStatement("SELECT token FROM authentication WHERE token = ?")) {
            if(verifyToken == null)
            {
                return false;
            }
            verifyToken.setString(1, token);

            try(ResultSet rs = verifyToken.executeQuery()) {

                while (rs.next()) {
                    String t = rs.getString(1);
                    if (t.compareTo(token) == 0) {
                        rv = true;
                    }
                }
            }
        }

        return rv;

    }
}
