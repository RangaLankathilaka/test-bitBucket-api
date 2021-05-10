package com.wave.gitHubAPITest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GitHubAPITest2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(GitHubAPITest2.class);
	public static final String REQUEST_FOR_FETCHING_RECORDS = "Request for fetching records : {}";
	public static final String RESPONSE_FOR_FETCHING_RECORDS = "Response for fetching records ";

	public static final String LOGGER_STRING = "RequestURL: {}";

	private static final String authorization = "Bearer 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";
	private static final String baseUrl = "https://api.github.com/repos/RangaLankathilaka/test-gitHub-api";

	private HttpClient httpClient = HttpClientBuilder.create().build();
	private ObjectMapper mapper = new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true);

	// private static final ObjectMapper objectMapper = new ObjectMapper();

	public static void main(String[] args) throws IOException, InterruptedException {
		new GitHubAPITest2().executeExample();
	}

	private void executeExample() throws IOException, InterruptedException {
		String masterSHA = this.getMasterBranchSHA();
		System.out.println(">>>>>>>>>" + masterSHA);
		this.createBranch(masterSHA);
		//this.createFile();

//		String pullRequestResponse = this.createPullRequest();
//		String pullNumber = this.getPullNumber(pullRequestResponse);
//
//		this.mergePullRequest(pullNumber);
//		this.deleteBranch();
	}

	/**
	 * create branch
	 * 
	 * @param sha
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String createBranch(String sha) throws IOException, InterruptedException {

		Map<String, Object> branchMap = new HashMap<String, Object>();
		branchMap.put("ref", "refs/heads/new-test1-branch");
		branchMap.put("sha", sha);

		//String requestBody = convertMapToSting(branchMap);
		String requestBody = new ObjectMapper().writeValueAsString(branchMap);
		return this.post("/git/refs", requestBody);
	}

	/**
	 * HTTP POST
	 * 
	 * @param path
	 * @param body
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String post(String path, String body) throws IOException, InterruptedException {

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		HttpPost httpRequest = new HttpPost(baseUrl + path);
		httpRequest.setHeader("Authorization", authorization);

		String jsonString = mapper.writeValueAsString(body);
		LOGGER.info(REQUEST_FOR_FETCHING_RECORDS, body);
		httpRequest.setEntity(new StringEntity(jsonString, HTTP.UTF_8));

		HttpResponse httpResponse = httpClient.execute(httpRequest);

		jsonString = convertStreamToString(httpResponse.getEntity().getContent());

		LOGGER.info(RESPONSE_FOR_FETCHING_RECORDS + "response body : {}", jsonString);

		return jsonString;

	}

	/**
	 * getMasterBranchSHA
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String getMasterBranchSHA() throws IOException, InterruptedException {
		String body = this.get("/git/refs/heads");

		String sha = mapper.readTree(body).get(0).get("object").get("sha").asText();

		return sha;
	}

	/**
	 * HTTP GET
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String get(String path) throws IOException, InterruptedException {
		String jsonString = "";
		HttpGet httpRequest = new HttpGet(baseUrl + path);
		LOGGER.info(LOGGER_STRING, path);

		httpRequest.setHeader("Authorization", authorization);

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

	/**
	 * convert map to string
	 * 
	 * @param map
	 * @return
	 */
//	public String convertMapToSting(Map<String, ?> map) {
//		String mapAsString = map.keySet().stream().map(key -> key + "=" + map.get(key))
//				.collect(Collectors.joining(", ", "{", "}"));
//		
//		return mapAsString;
//
//	}
	
	/**
	 * create file
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String createFile() throws IOException, InterruptedException {

		String fileToAdd = getResourceFile("/home/noetic/git/test projects/gitHubAPITest/src/main/resources/testFile.txt");
		String encodedContent = java.util.Base64.getEncoder().encodeToString(fileToAdd.getBytes());

		Map<String, Object> createMap = new HashMap<String, Object>();
		createMap.put("message", "New file added");
		createMap.put("content", encodedContent);
		createMap.put("branch", "main");
		String requestBody = new ObjectMapper().writeValueAsString(createMap);
		LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+ requestBody);
		//String requestBody = convertMapToSting(createMap);
		return this.put("/contents/testFile.txt", requestBody);
	}
	
	/**
	 * get file from the given path
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	  private String getResourceFile(String filename) throws IOException {
		  
		FileInputStream fis = new FileInputStream(filename);// ("src/test/resources/fileTest.txt");
		String data = IOUtils.toString(fis, "UTF-8");
	    return data;
	    }
	  
	/**
	 * HTTP PUT
	 * @param path
	 * @param body
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String put(String path, String body) throws IOException, InterruptedException {

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		HttpPut httpRequest = new HttpPut(baseUrl + path);
		httpRequest.setHeader("Authorization", authorization);

		String jsonString = mapper.writeValueAsString(body);
		LOGGER.info(REQUEST_FOR_FETCHING_RECORDS, body);
		httpRequest.setEntity(new StringEntity(jsonString, HTTP.UTF_8));

		HttpResponse httpResponse = httpClient.execute(httpRequest);

		jsonString = convertStreamToString(httpResponse.getEntity().getContent());

		LOGGER.info(RESPONSE_FOR_FETCHING_RECORDS + "response body PUT: {}", jsonString);

		return jsonString;

	}
	

}
