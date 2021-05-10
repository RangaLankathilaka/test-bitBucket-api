package com.wave.gitHubAPITest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.io.IOUtils;

public class BitBucketAPITest {
	
	static String  username = "RangaLankathilaka";
	static String appToken = "WMwpgwEyrnu4JM2d6zca";//enter your 20 character app password here
	String encodedCredentials = Base64.getEncoder().encodeToString((username+":"+appToken).getBytes("UTF-8")); //Bitbucket REST API needs the credentials to be Base64 encoded with "UTF-8" formatting
	static String repositoryName = "bitBucket-api";
	static String branchName = "master";
	static String filename = "hello.txt";
	static String sourceBranch = "master";
	static String destinationBranch = "branch-creted-via-java";
	static String pullRequestTitle = "master-merge-pr";
	
	public BitBucketAPITest() throws UnsupportedEncodingException {
    }

	
	static void getFileContentFromRepository(String username, String repositoryName, String branchName, String filename, String encodedCredentials) throws IOException{
		System.out.println(">>>>>>>>>>>>>>>>" + encodedCredentials);   
		String urlToAccess = "https://api.bitbucket.org/2.0/repositories/"+username+"/"+repositoryName+"/src/"+branchName+"/"+filename;
		   URL repositoryUrl = new URL (urlToAccess);
		   HttpURLConnection connection = (HttpURLConnection) repositoryUrl.openConnection();
		   //For authentication
		   connection.addRequestProperty("Authorization", "Basic "+encodedCredentials); 
		   connection.setRequestMethod("GET");
		   connection.connect();
		   System.out.println(connection.getResponseCode()+" "+connection.getResponseMessage());
		   //The InputStream is required to read in the data of the GET request
		   InputStream connectionDataStream = connection.getInputStream();
		   String connectionStreamData = IOUtils.toString(connectionDataStream, StandardCharsets.UTF_8);
		   System.out.println(connectionStreamData); 
		   //the data can be returned as well, just change the return type of the function to String.
		}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		//new BitBucketAPITest().getFileContentFromRepository(username, repositoryName, branchName, filename, new BitBucketAPITest().encodedCredentials);
	    new BitBucketAPITest().createFileInRepository(username, repositoryName, branchName, new BitBucketAPITest().getResourceFile(), "testFile.txt", new BitBucketAPITest().encodedCredentials);
	
	}
	
	
	
	
	static void createFileInRepository(String username, String repositoryName, String branchName, String data, String filename, String encodedCredentials) throws IOException {
        String urlToAccess = "https://api.bitbucket.org/2.0/repositories/"+username+"/"+repositoryName+"/src/";
        URL repositoryUrl = new URL (urlToAccess);
        HttpURLConnection connection = (HttpURLConnection) repositoryUrl.openConnection();
		//For authentication
		connection.addRequestProperty("Authorization", "Basic "+encodedCredentials);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		//For setting the form data in the post request to add a new file and fill it with your data
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		out.write("branch="+branchName+"&"+"/"+filename+"="+data);
		out.close();
        try{
            connection.connect();
      		System.out.println(connection.getResponseCode()+" "+connection.getResponseMessage());
            System.out.println("File Created");
        }catch (Exception e){
           System.out.println("File creation failed.");
        }
	}
	
	 private String getResourceFile() throws IOException {
		  
			FileInputStream fis = new FileInputStream("/home/noetic/git/test projects/gitHubAPITest/src/main/resources/testFile.txt");// ("src/test/resources/fileTest.txt");
			String data = IOUtils.toString(fis, "UTF-8");
		    return data;
		    }
}
