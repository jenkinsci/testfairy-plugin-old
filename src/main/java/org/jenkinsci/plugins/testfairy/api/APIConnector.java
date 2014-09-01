package org.jenkinsci.plugins.testfairy.api;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

import static org.jenkinsci.plugins.testfairy.api.APIConstants.*;

public class APIConnector implements FilePath.FileCallable<APIResponse> {
	private static final long serialVersionUID = 1L;

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

	/**
	 * Sends an upload request to the TestFairy API
	 *
	 * @return
	 * @throws IOException
	 */
	APIResponse sendUploadRequest(String workspacePath) throws IOException {
		HttpPost httpPost = new HttpPost(apiParams.getApiURI());

		httpPost.setEntity(buildMultipartRequest(workspacePath));

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		HttpResponse response = httpClient.execute(httpPost);

		InputStream responseStream = response.getEntity().getContent();

		return mapper.readValue(responseStream, APIResponse.class);

	}

    /**
     * Builds the http multipart request to be sent to the TestFairy API
     * @return
     */
    private HttpEntity buildMultipartRequest(String workspacePath) {

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        /*
         *   Set Required Params
         */

        addTextBodyIfNotEmpty(entityBuilder, REQUEST_PARAM_APK_KEY, apiParams.getApiKey());

        addFilePartIfNotEmpty(entityBuilder, REQUEST_PARAM_APK_FILE, localFile(workspacePath, apiParams.getApkFilePath()));

        /*
         *   Set Optional Params
         */
        addFilePartIfNotEmpty(entityBuilder, REQUEST_PARAM_PROGUARD_FILE, localFile(workspacePath, apiParams.getProguardFilePath()));

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

    private File localFile(String workspacePath, String filePath) {
		File f = new File(workspacePath + File.separator + filePath);
		if (f.exists())
			return f;
		return new File(filePath);
	}

	public APIResponse invoke(File workspace, VirtualChannel channel)
			throws IOException, InterruptedException {
		return sendUploadRequest(workspace.getAbsolutePath());
	}

}
