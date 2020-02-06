package eu.lightest.delegations.impl.exceptions;

public class DelegationIdInvalidException extends Exception {
   private String mMessage = null;
    public DelegationIdInvalidException(String message) {
        mMessage = message;
    }
}
