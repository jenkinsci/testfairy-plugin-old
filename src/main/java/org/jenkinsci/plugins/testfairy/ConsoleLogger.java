package org.jenkinsci.plugins.testfairy;

import org.jenkinsci.plugins.testfairy.api.APIResponse;

import java.io.PrintStream;

public class ConsoleLogger {

    private PrintStream logger;

    public ConsoleLogger(PrintStream logger) {
        this.logger = logger;
    }

    public void logResponse(APIResponse response){
        if(APIResponse.TEST_FAIRY_STATUS_OK.equals(response.getStatus())){
            info("Upload successful");
            info("TestFairy build URL: " + response.getBuildUrl());
            info("Invite Testers URL: " + response.getInviteTestersUrl());
            info("Instrumented APK URL: " + response.getInstrumentedUrl());

        } else {
            //Fail
            error("Upload failed, please check your settings!");
            error("TestFairy API Error Message: " + response.getMessage());
            error("TestFairy API Error code: " + response.getCode());
        }
    }

    public void info(String msg){
        logger.println(msg);
    }

    public void error(String msg){
        logger.println("[ERROR] "+msg);
    }

    public void warn(String msg){
        logger.println("[WARNING] " + msg);
    }
}
