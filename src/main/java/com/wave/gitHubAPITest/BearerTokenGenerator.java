package com.wave.gitHubAPITest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BearerTokenGenerator {
	

	private static final Logger LOGGER = LoggerFactory.getLogger(BearerTokenGenerator.class);
	public static final String REQUEST_FOR_FETCHING_RECORDS = "Request for fetching records : {}";
	public static final String RESPONSE_FOR_FETCHING_RECORDS = "Response for fetching records ";

	private HttpClient httpClient = HttpClientBuilder.create().build();
	private ObjectMapper mapper = new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true);
	
//	public static void main(String[] args) throws IOException, InterruptedException {
//		new BearerTokenGenerator().post("/token","grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA&client_id=xxx&client_secret=xxxxx");
//	}
	
	
	private String post(String path, String body) throws IOException, InterruptedException {

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		HttpPost httpRequest = new HttpPost(path);

		//httpRequest.setHeader("User-Agent", USER_AGENT);
		String jsonString = mapper.writeValueAsString(body);
		LOGGER.info(REQUEST_FOR_FETCHING_RECORDS, body);
		httpRequest.setEntity(new StringEntity(jsonString, HTTP.UTF_8));

		HttpResponse httpResponse = httpClient.execute(httpRequest);

		jsonString = convertStreamToString(httpResponse.getEntity().getContent());

		LOGGER.info(RESPONSE_FOR_FETCHING_RECORDS + "response body : {}", jsonString);

		return jsonString;

	}

	/**
	 * convert stream to String
	 * 
	 * @param is
	 * @return
	 */
	private String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			throw (new RuntimeException(e.getMessage()));
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw (new RuntimeException(e.getMessage()));
			}
		}
		return sb.toString();
	}
	
	public static void main(String args[]) throws IOException, InterruptedException {
		BearerTokenGenerator bearerTokenGenerator = new BearerTokenGenerator();
		bearerTokenGenerator.post("http://localhost:8080/api/v1/paypal/createPayment","grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA&client_id=xxx&client_secret=xxxxx");
	}


}
