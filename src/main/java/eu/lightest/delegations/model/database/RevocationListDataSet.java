package eu.lightest.delegations.model.database;

import java.sql.Date;

public class RevocationListDataSet {
    private int mId;
    private String mReason;
    private Date mRevocationTime;

    public RevocationListDataSet( int id, String reason, Date revocationTime ) {
        mId = id;
        mReason = reason;
        mRevocationTime = revocationTime;
    }

    public int getId() {
        return mId;
    }

    public String getReason() {
        return mReason;
    }

    public Date getRevocationTime() {
        return mRevocationTime;
    }

}
