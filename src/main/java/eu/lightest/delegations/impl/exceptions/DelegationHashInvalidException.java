package eu.lightest.delegations.impl.exceptions;

public class DelegationHashInvalidException extends Exception{

    private String mReason = null;

    public DelegationHashInvalidException(String reason) {
       mReason = reason;
    }
}
