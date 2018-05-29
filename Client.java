import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


public class Client 
{
	public static void headmethod(String port)
	{
		String url = "http://127.0.0.1:"+port+"/index1.html";
		String USER_AGENT = "Mozilla/5.0";
		HttpURLConnection conn = null;
		try 
		{
			System.out.println("\nSending 'Head' request to URL : " + url);
			URL obj = new URL(url);
			conn = (HttpURLConnection) obj.openConnection();
			conn.setReadTimeout(5000); //setting the time out
			conn.setRequestMethod("HEAD"); // setting the method head
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setDoInput(true);
            conn.setDoOutput(true);
            int responseCode=conn.getResponseCode(); //getting the response code
            System.out.println("\n Response Code: "+ conn.getResponseCode());
            System.out.println("\n Response Msg:  "+conn
            	.getResponseMessage());
			if (responseCode == HttpURLConnection.HTTP_OK) 
			{

			//storing the headers in the map from the connection
				
            Map<String, List<String>> map = conn.getHeaderFields();

	        System.out.println("\nPrinting Response Header...\n");

	       for (Map.Entry<String, List<String>> entry : map.entrySet()) 
	       {
		    System.out.println("Key : " + entry.getKey()
                           + " ,Value : " + entry.getValue());
	       }

	           //code for printing the response
				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String inputLine;
				//variable for storing the response
				StringBuffer response = new StringBuffer();
				System.out.println("\n");
				while ((inputLine = in.readLine()) != null) 
				{
					response.append(inputLine);
					response.append("\n");
				}
				in.close();
				System.out.println(response.toString());
				System.out.println("\nEnd of Head Response \n");
			}
			else
			{

			Map<String, List<String>> map = conn.getHeaderFields();

	        System.out.println("\nPrinting Response Header...\n");

	       for (Map.Entry<String, List<String>> entry : map.entrySet()) 
	       {
		    System.out.println("Key : " + entry.getKey()
                           + " ,Value : " + entry.getValue());
	       }
				System.out.println("\nBad HEAD request\n");
		 }

			
			
		} 
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally 
		{
	        if (conn != null)
	        {
	        	conn.disconnect(); //disconnecting the client
	        }
		}
					
	}

	public static void postmethod(String port) 
	{
		String url = "http://127.0.0.1:"+port+"/index.html";
		String USER_AGENT = "Mozilla/5.0";
		DataOutputStream wr;
		try {
			
			URL obj = new URL(url);
			String urlParameters = "username=Ram&password=syracuse";
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// Send post request
			//Setting all the connection properties
			con.setDoOutput(true);
			con.setRequestMethod("POST"); //setting the method post
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "UTF-8");
			wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters); // writing the parameters in the body
		    wr.flush();
		    wr.close();
		    System.out.println("\nSending 'POST' request to URL : " + url);
		    System.out.println("\nPost parameters : " + urlParameters);
			int responseCode = con.getResponseCode();
            System.out.println("\n Response Code: "+ con.getResponseCode());
            System.out.println("\n Response Msg:  "+con
            	.getResponseMessage());

			if (responseCode == HttpURLConnection.HTTP_OK) 
		    {

            Map<String, List<String>> map = con.getHeaderFields();

	        System.out.println("\nPrinting Response Header...\n");

	       for (Map.Entry<String, List<String>> entry : map.entrySet()) 
	       {
		    System.out.println("Key : " + entry.getKey()
                           + " ,Value : " + entry.getValue());
	       }
			
			
			System.out.println("Response Code : " + responseCode);
           
			//code for printing the response
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			//variable for storing the response
			StringBuffer response = new StringBuffer();
			System.out.println("\n The contents of the file are: \n");
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				response.append("\n");
			}
			in.close();
			//print response
			System.out.println(response.toString());
		}
         
		else
			{
				
		    System.out.println("\nBad POST request\n");

		    //storing the headers in the map from the connection
		    
		    Map<String, List<String>> map = con.getHeaderFields();

	        System.out.println("\nPrinting Response Header...\n");

	       for (Map.Entry<String, List<String>> entry : map.entrySet()) 
	       {
		    System.out.println("Key : " + entry.getKey()
                           + " ,Value : " + entry.getValue());
	       }
	       System.out.println("\n\n");
	       
	       //code for getting the error stream
	       
            Scanner scanner = new Scanner(con.getErrorStream());
            while(scanner.hasNext())
            System.out.println(scanner.next());
            scanner.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	public static void main(String args[])
	{
		System.out.println("\n --------------- Testing Head method ----------------- ");
		headmethod(args[0]);
		System.out.println("\n --------------- End of Head Testing ----------------- ");
		System.out.println("\n\n\n");
		System.out.println("--------------- Testing Post method ----------------- ");
		postmethod(args[0]);
		System.out.println("\n --------------- End of Post Testing ----------------- ");

}
}
