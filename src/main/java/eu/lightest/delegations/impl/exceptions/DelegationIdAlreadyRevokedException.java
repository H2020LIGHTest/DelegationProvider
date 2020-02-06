package eu.lightest.delegations.impl.exceptions;

public class DelegationIdAlreadyRevokedException extends Exception{
   private  String mMessage = null;
    public DelegationIdAlreadyRevokedException(String msg) {
        mMessage = msg;
    }

    public String getMessage() {
        return mMessage;
    }
}
