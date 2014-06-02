package org.jenkinsci.plugins.testfairy.api;

import java.io.Serializable;

public class APIResponse implements Serializable {

    public static final String TEST_FAIRY_ERROR_CODE_MISSING_PARAMETER = "1";
    public static final String TEST_FAIRY_ERROR_CODE_INVALID_API_KEY = "5";
    public static final String TEST_FAIRY_ERROR_CODE_INVALID_APK_FILE = "105";
    public static final String TEST_FAIRY_STATUS_OK = "ok";
    public static final String TEST_FAIRY_STATUS_FAIL = "fail";

    private String status;
    private String appName;
    private String appVersion;
    private String fileSize;
    private String buildUrl;
    private String inviteTestersUrl;
    private String instrumentedUrl;
    private String iconUrl;
    private String notifiedTestersGroups;
    private String message;
    private String code;

    public APIResponse() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getBuildUrl() {
        return buildUrl;
    }

    public void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    public String getInviteTestersUrl() {
        return inviteTestersUrl;
    }

    public void setInviteTestersUrl(String inviteTestersUrl) {
        this.inviteTestersUrl = inviteTestersUrl;
    }

    public String getInstrumentedUrl() {
        return instrumentedUrl;
    }

    public void setInstrumentedUrl(String instrumentedUrl) {
        this.instrumentedUrl = instrumentedUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getNotifiedTestersGroups() {
        return notifiedTestersGroups;
    }

    public void setNotifiedTestersGroups(String notifiedTestersGroups) {
        this.notifiedTestersGroups = notifiedTestersGroups;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "APIResponse{" +
                "status='" + status + '\'' +
                ", appName='" + appName + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", buildUrl='" + buildUrl + '\'' +
                ", inviteTestersUrl='" + inviteTestersUrl + '\'' +
                ", instrumentedUrl='" + instrumentedUrl + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", notifiedTestersGroups='" + notifiedTestersGroups + '\'' +
                ", message='" + message + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
