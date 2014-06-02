package org.jenkinsci.plugins.testfairy.api;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class APIParams implements Serializable {
    public static final String DEFAULT_TEST_FAIRY_API_URL = "https://app.testfairy.com/api/upload/";

    private String apiUrl;
    private String apiKey;

    private String apkFilePath;

    private String proguardFilePath;
    private String testersGroups;

    private String metrics;
    private String maxDuration;
    private String video;
    private String videoQuality;
    private String videoRate;
    private String iconWatermark;
    private String comment;


    //processed params
    private URI apiURI;
    private File apkFile;
    private File proguardFile;

    public APIParams(String apiUrl, String apiKey, String apkFilePath, String proguardFilePath,
                     String testersGroups, String metrics, String maxDuration, String video,
                     String videoQuality, String videoRate, String iconWatermark, String comment) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.apkFilePath = apkFilePath;
        this.proguardFilePath = proguardFilePath;
        this.testersGroups = testersGroups;
        this.metrics = metrics;
        this.maxDuration = maxDuration;
        this.video = video;
        this.videoQuality = videoQuality;
        this.videoRate = videoRate;
        this.iconWatermark = iconWatermark;
        this.comment = comment;
    }


    public void initializeAndValidate(String remoteWorkspacePath) throws
            APIException {

        checkNotMissing(apiUrl, "API Url");

        try {
            apiURI = new URL(apiUrl).toURI();
        } catch (URISyntaxException e) {
            throw new APIException("Invalid API Url " + apiUrl, e);
        } catch (MalformedURLException e) {
            throw new APIException("Invalid API Url " + apiUrl, e);
        }

        checkNotMissing(apiKey, "API Key");

        checkNotMissing(apkFilePath, "APK File Path");

        apkFile = createFile(remoteWorkspacePath, apkFilePath, "APK");
        if (proguardFilePath!=null && !"".equals(proguardFilePath)) {
            proguardFile = createFile(remoteWorkspacePath, proguardFilePath, "Proguard");
        }
    }

    public URI getApiURI(){
        return apiURI;
    }

    public File getApkFile(){
        return apkFile;
    }

    public File getProguardFile(){
        return proguardFile;
    }


    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApkFilePath() {
        return apkFilePath;
    }

    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }

    public String getProguardFilePath() {
        return proguardFilePath;
    }

    public void setProguardFilePath(String proguardFilePath) {
        this.proguardFilePath = proguardFilePath;
    }

    public String getTestersGroups() {
        return testersGroups;
    }

    public void setTestersGroups(String testersGroups) {
        this.testersGroups = testersGroups;
    }

    public String getMetrics() {
        return metrics;
    }

    /**
     *
     * @param metrics
     */
    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public String getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(String videoQuality) {
        this.videoQuality = videoQuality;
    }

    public String getVideoRate() {
        return videoRate;
    }

    public void setVideoRate(String videoRate) {
        this.videoRate = videoRate;
    }

    public String getIconWatermark() {
        return iconWatermark;
    }

    public void setIconWatermark(String iconWatermark) {
        this.iconWatermark = iconWatermark;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    private static void checkNotMissing(String param, String logName) throws APIException {

        if(param == null || "".equals(param)){
            throw new APIException("Missing " + logName);
        }
    }

    private File createFile(String remoteWorkspacePath, String filePath, String fileContext)
            throws APIException {
        File file = findAbsoluteOrRelativeFile(remoteWorkspacePath, filePath);
        if (file == null || !file.isFile()) {
            throw new APIException("Invalid " + fileContext + " File Path: " + filePath);
        }
        return file;
    }

    private File findAbsoluteOrRelativeFile(String workspace, String path) {
        File f = new File(path);
        if (f.exists()) {
            return f;
        }
        f = new File(workspace, path);
        if (f.exists()) {
            return f;
        }
        return null;
    }
}
