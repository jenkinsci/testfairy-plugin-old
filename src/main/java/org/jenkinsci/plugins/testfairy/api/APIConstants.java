package org.jenkinsci.plugins.testfairy.api;

/**
 * From: <a href="http://docs.testfairy.com/Upload_API.html">http://docs.testfairy.com/Upload_API.html</a>
 */
public final class APIConstants {

    private APIConstants() {
        //utility class
    }

    public static final String REQUEST_PARAM_APK_FILE = "apk_file";
    public static final String REQUEST_PARAM_APK_KEY = "api_key";
    public static final String REQUEST_PARAM_PROGUARD_FILE = "proguard_file";
    public static final String REQUEST_PARAM_TESTERS_GROUPS = "testers_groups";
    public static final String REQUEST_PARAM_METRICS = "metrics";
    public static final String REQUEST_PARAM_MAX_DURATION = "max-duration";
    public static final String REQUEST_PARAM_VIDEO = "video";
    public static final String REQUEST_PARAM_VIDEO_QUALITY = "video-quality";
    public static final String REQUEST_PARAM_VIDEO_RATE = "video-rate";
    public static final String REQUEST_PARAM_ICON_WATERMARK = "icon-watermark";
    public static final String REQUEST_PARAM_COMMENT = "comment";
}
