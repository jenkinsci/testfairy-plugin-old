package org.jenkinsci.plugins.testfairy.api;

public class APIException extends Exception {

    public APIException(String s) {
        super(s);
    }

    public APIException(String s, Throwable throwable) {
        super(s,throwable);
    }
}
