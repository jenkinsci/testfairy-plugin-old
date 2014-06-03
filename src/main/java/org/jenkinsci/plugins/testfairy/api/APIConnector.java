package org.jenkinsci.plugins.testfairy.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import hudson.Launcher;
import hudson.remoting.Callable;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jenkinsci.plugins.testfairy.ConsoleLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import static org.jenkinsci.plugins.testfairy.api.APIConstants.*;

public class APIConnector implements Serializable {

    //initialize jackson object mapper
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        return mapper;
    }

    /*
     * Sends an upload request to the TestFairy API
     * @return
     */
    public APIResponse sendUploadRequest(APIParams apiParams) throws APIException {
        APIResponse apiResponse = null;
        //JAVA 7 issue handled for SSL Handshake
        //http://stackoverflow.com/questions/7615645/ssl-handshake-alert-unrecognized-name-error-since-upgrade-to-java-1-7-0
        System.setProperty("jsse.enableSNIExtension", "false");

        try {
            ObjectMapper mapper = createObjectMapper();

            HttpPost httpPost = new HttpPost(apiParams.getApiURI());

            httpPost.setEntity(buildMultipartRequest(apiParams));

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            HttpResponse response = httpClient.execute(httpPost);

            InputStream responseStream = response.getEntity().getContent();

            apiResponse = mapper.readValue(responseStream, APIResponse.class);

        } catch (IOException ex) {
            throw new APIException(ex.getMessage(), ex);
        }

        return apiResponse;
    }

    /**
     * Builds the http multipart request to be sent to the TestFairy API
     * @return
     */
    private static HttpEntity buildMultipartRequest(APIParams apiParams) {

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        /*
         *   Set Required Params
         */



        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_APK_KEY, apiParams.getApiKey());

        addFilePartIfNotEmpty(entityBuilder, REQUEST_PARAM_APK_FILE, apiParams.getApkFile());

        /*
         *   Set Optional Params
         */
        addFilePartIfNotEmpty(entityBuilder, REQUEST_PARAM_PROGUARD_FILE,apiParams.getProguardFile());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_TESTERS_GROUPS, apiParams.getTestersGroups());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_METRICS, apiParams.getMetrics());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_MAX_DURATION, apiParams.getMaxDuration());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_VIDEO, apiParams.getVideo());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_VIDEO_QUALITY, apiParams.getVideoQuality());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_VIDEO_RATE,  apiParams.getVideoRate());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_ICON_WATERMARK,  apiParams.getIconWatermark());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_COMMENT,  apiParams.getComment());

        return entityBuilder.build();
    }

    private static void addFilePartIfNotEmpty(MultipartEntityBuilder entityBuilder,
                                       String requestParam, File file){
        if (file != null) {
            entityBuilder.addPart(requestParam, new FileBody(file));
        }
    }

    private static void addTextBodyIfNotEmpty(MultipartEntityBuilder entityBuilder,
                                       String requestParam, String value){
        if(value!=null && !"".equals(value)) {
            entityBuilder.addTextBody(requestParam, value);
        }
    }

}
