package org.jenkinsci.plugins.testfairy.api;


import hudson.EnvVars;
import hudson.FilePath;
import hudson.util.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

import static org.jenkinsci.plugins.testfairy.api.APIConstants.*;

public class APIConnector {

    private APIParams apiParams;
    private ObjectMapper mapper;

    public APIConnector(APIParams apiParams) {
        this.apiParams = apiParams;

        //JAVA 7 issue handled for SSL Handshake
        //http://stackoverflow.com/questions/7615645/ssl-handshake-alert-unrecognized-name-error-since-upgrade-to-java-1-7-0
        System.setProperty("jsse.enableSNIExtension", "false");

        //initialize jackson object mapper
        mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    }


    public APIResponse uploadAPK(EnvVars environment, FilePath remoteWorkspacePath) throws APIException {

        APIResponse apiResponse = sendUploadRequest(environment, remoteWorkspacePath);

        return apiResponse;

    }

    /**
     * Sends an upload request to the TestFairy API
     * @return
     */
    APIResponse sendUploadRequest(EnvVars environment, FilePath remoteWorkspacePath) throws APIException {
        APIResponse apiResponse = null;

        try {
            HttpPost httpPost = new HttpPost(apiParams.getApiURI());

            httpPost.setEntity(buildMultipartRequest(environment, remoteWorkspacePath));

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            HttpResponse response = httpClient.execute(httpPost);

            InputStream responseStream = response.getEntity().getContent();

            apiResponse = mapper.readValue(responseStream, APIResponse.class);

        } catch (IOException ex) {
            throw new APIException(ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            throw new APIException(ex.getMessage(), ex);            
        }

        return apiResponse;
    }

    /**
     * Builds the http multipart request to be sent to the TestFairy API
     * @return
     */
    private HttpEntity buildMultipartRequest(EnvVars environment, FilePath remoteWorkspacePath) throws java.io.IOException, APIException, InterruptedException {

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        /*
         *   Set Required Params
         */

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_APK_KEY, environment.expand(apiParams.getApiKey()));

        addFilePartIfNotEmpty(entityBuilder, REQUEST_PARAM_APK_FILE, createFile(remoteWorkspacePath, environment.expand(apiParams.getApkFilePath()), "APK"));

        /*
         *   Set Optional Params
         */
        String proguardFilePath = environment.expand(apiParams.getProguardFilePath());
        if (proguardFilePath!=null && !"".equals(proguardFilePath)) {
            addFilePartIfNotEmpty(entityBuilder, REQUEST_PARAM_PROGUARD_FILE,createFile(remoteWorkspacePath, proguardFilePath, "PROGUARD"));
        }

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_TESTERS_GROUPS, environment.expand(apiParams.getTestersGroups()));

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_METRICS, apiParams.getMetrics());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_MAX_DURATION, apiParams.getMaxDuration());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_VIDEO, apiParams.getVideo());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_VIDEO_QUALITY, apiParams.getVideoQuality());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_VIDEO_RATE,  apiParams.getVideoRate());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_ICON_WATERMARK,  apiParams.getIconWatermark());

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_COMMENT,  apiParams.getComment());

        return entityBuilder.build();
    }

    private void addFilePartIfNotEmpty(MultipartEntityBuilder entityBuilder,
                                       String requestParam, FilePath file) throws java.io.IOException {
        if (file != null) {
            entityBuilder.addPart(requestParam, new ByteArrayBody(IOUtils.toByteArray(file.read()), file.getName()));
        }
    }

    private void addTextBodyIfNotEmpty(MultipartEntityBuilder entityBuilder,
                                       String requestParam, String value){
        if(value!=null && !"".equals(value)) {
            entityBuilder.addTextBody(requestParam, value);
        }
    }

    private FilePath createFile(FilePath remoteWorkspacePath, String filePath, String fileContext)
            throws APIException, java.io.IOException, java.lang.InterruptedException {
        FilePath file = new FilePath(remoteWorkspacePath, filePath);
        if (file == null || !file.exists() || file.isDirectory()) {
            throw new APIException("Invalid " + fileContext + " File Path: " + filePath);
        }
        return file;
    }
}
