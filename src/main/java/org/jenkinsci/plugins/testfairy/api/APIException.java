package org.jenkinsci.plugins.testfairy.api;

import java.io.Serializable;

public class APIException extends Exception implements Serializable {

    public APIException(String s) {
        super(s);
    }

    public APIException(String s, Throwable throwable) {
        super(s,throwable);
    }
}
