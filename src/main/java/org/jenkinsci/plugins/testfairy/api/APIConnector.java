package org.jenkinsci.plugins.testfairy.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.util.regex.Pattern;

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


    public APIResponse uploadAPK() throws APIException {

        APIResponse apiResponse = sendUploadRequest();

        return apiResponse;

    }

    /**
     * Sends an upload request to the TestFairy API
     * @return
     */
    APIResponse sendUploadRequest() throws APIException {
        APIResponse apiResponse = null;

        try {
            HttpPost httpPost = new HttpPost(apiParams.getApiURI());

            httpPost.setEntity(buildMultipartRequest());

            CloseableHttpClient httpClient;
            ProxyConfiguration proxy = Jenkins.getInstance().proxy;
            if (shouldUseProxy(proxy, apiParams.getApiURI().getHost())) {

                HttpHost proxyHost = new HttpHost(proxy.name, proxy.port);
                RequestConfig config = RequestConfig.custom()
                        .setProxy(proxyHost)
                        .build();
                httpPost.setConfig(config);

                if(proxy.getUserName() != null) {
                    CredentialsProvider credsProvider = new BasicCredentialsProvider();
                    credsProvider.setCredentials(
                            new AuthScope(proxy.getUserName(), proxy.port),
                            new UsernamePasswordCredentials(proxy.getUserName(), proxy.getPassword()));

                    httpClient = HttpClientBuilder.create()
                            .setDefaultCredentialsProvider(credsProvider).build();
                } else {
                    httpClient = HttpClientBuilder.create().build();
                }

            } else {

                httpClient = HttpClientBuilder.create().build();

            }

            HttpResponse response = httpClient.execute(httpPost);

            InputStream responseStream = response.getEntity().getContent();

            apiResponse = mapper.readValue(responseStream, APIResponse.class);

        } catch (IOException ex) {
            throw new APIException(ex.getMessage(), ex);
        }

        return apiResponse;
    }

    private Boolean shouldUseProxy(ProxyConfiguration proxy, String hostname) {
        if(proxy == null) {
            return false;
        }
        boolean shouldProxy = true;
        for(Pattern p : proxy.getNoProxyHostPatterns()) {
            if(p.matcher(hostname).matches()) {
                shouldProxy = false;
                break;
            }
        }

        return shouldProxy;
    }
    /**
     * Builds the http multipart request to be sent to the TestFairy API
     * @return
     */
    private HttpEntity buildMultipartRequest() {

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

    private void addFilePartIfNotEmpty(MultipartEntityBuilder entityBuilder,
                                       String requestParam, File file){
        if (file != null) {
            entityBuilder.addPart(requestParam, new FileBody(file));
        }
    }

    private void addTextBodyIfNotEmpty(MultipartEntityBuilder entityBuilder,
                                       String requestParam, String value){
        if(value!=null && !"".equals(value)) {
            entityBuilder.addTextBody(requestParam, value);
        }
    }

}
