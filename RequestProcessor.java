import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.*;
    
public class RequestProcessor implements Runnable 
{  
  private final static Logger logger = Logger.getLogger(
      RequestProcessor.class.getCanonicalName());

  private File rootDirectory;
  private String indexFileName = "index.html";
  private Socket connection;
  
  //map which is local database for storing  usernames and passwords

  HashMap<String, String> database = new HashMap<>();
  
  public RequestProcessor(File rootDirectory, 
      String indexFileName, Socket connection) 
  {
    
    database.put("Ram","syracuse");
    database.put("Ravi","syracuse");
    database.put("Venkatesh","syracuse");
    
    if (rootDirectory.isFile()) {
      throw new IllegalArgumentException(
          "rootDirectory must be a directory, not a file");   
    }
    try {
      rootDirectory = rootDirectory.getCanonicalFile();
    } catch (IOException ex) {
    }
    this.rootDirectory = rootDirectory;

    if (indexFileName != null) this.indexFileName = indexFileName;
    this.connection = connection;
  }
  
  @Override
  public void run() {

    // for security checks
    String root = rootDirectory.getPath();
    try {              
      OutputStream raw = new BufferedOutputStream(
                          connection.getOutputStream()
                         );         
      Writer out = new OutputStreamWriter(raw);
      Reader in = new InputStreamReader(
                   new BufferedInputStream(
                    connection.getInputStream()
                   ),"US-ASCII"
                  );
      StringBuilder requestLine = new StringBuilder();
      while (true) {
        int c = in.read();
        if (c == '\r' || c == '\n') break;
        requestLine.append((char) c);
      }
      
      String get = requestLine.toString();
      
      logger.info(connection.getRemoteSocketAddress() + " " + get);
      
      String[] tokens = get.split("\\s+");
      String method = tokens[0];
      String version = "";
      if (method.equals("GET")){
        String fileName = tokens[1];
        if (fileName.endsWith("/")) fileName += indexFileName;
        String contentType = 
            URLConnection.getFileNameMap().getContentTypeFor(fileName);
        if (tokens.length > 2) {
          version = tokens[2];
        }

        File theFile = new File(rootDirectory, 
            fileName.substring(1, fileName.length()));
        
        if (theFile.canRead() 
            // Don't let clients outside the document root
            && theFile.getCanonicalPath().startsWith(root)) {
          byte[] theData = Files.readAllBytes(theFile.toPath());
          if (version.startsWith("HTTP/")) { // send a MIME header
            sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);
          } 
      
          // send the file; it may be an image or other binary data 
          // so use the underlying output stream 
          // instead of the writer
          raw.write(theData);
          raw.flush();
        } else { // can't find the file
          String body = new StringBuilder("<HTML>\r\n")
              .append("<HEAD><TITLE>File Not Found</TITLE>\r\n")
              .append("</HEAD>\r\n")
              .append("<BODY>")
              .append("<H1>HTTP Error 404: File Not Found</H1>\r\n")
              .append("</BODY></HTML>\r\n").toString();
          if (version.startsWith("HTTP/")) { // send a MIME header
            sendHeader(out, "HTTP/1.0 404 File Not Found", 
                "text/html; charset=utf-8", body.length());
          } 
          out.write(body);
          out.flush();
        }
      }
      else if (method.equals("HEAD")) {
        
        /* if the method is head
         *  read the file name from the request and
         * check that file exists in the server
         * if exist then send Http 200 status code with headers
         * else send Http 400 error status code with headers
         * 
         * */
         
         //getting the file name
          String fileName = tokens[1];
          if (fileName.endsWith("/")) fileName += indexFileName;
          String contentType = 
              URLConnection.getFileNameMap().getContentTypeFor(fileName);
          if (tokens.length > 2) {
            version = tokens[2];
          }

          File theFile = new File(rootDirectory, 
              fileName.substring(1, fileName.length()));
          
          if (theFile.canRead() 
              // Don't let clients outside the document root
              && theFile.getCanonicalPath().startsWith(root)) {
            byte[] theData = Files.readAllBytes(theFile.toPath());
            if (version.startsWith("HTTP/")) { // send a MIME header
              sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);
            } 
        
            
          } else { // can't find the file
            String body = new StringBuilder("<HTML>\r\n")
                .append("<HEAD><TITLE>File Not Found</TITLE>\r\n")
                .append("</HEAD>\r\n")
                .append("<BODY>")
                .append("<H1> HTTP Error: File Not Found</H1>\r\n")
                .append("</BODY></HTML>\r\n").toString();
            if (version.startsWith("HTTP/")) { // send a MIME header
              //sending the header
              sendHeader(out, "HTTP/1.0 404 File Not Found", 
                  "text/html; charset=utf-8", body.length());
            } 
            //sending the error message
            out.write(body);
            out.flush();
          }
        }


else if (method.equals("POST")) 
      {
        
        /* if the method is post
         * then parse the request to get the parameters in the body message
         * and validate the parameters from the local map database
         * if success then send the requested file with headers and status 200 Ok
         * if not send the Http 400 error status with message
         */

      StringBuilder request = new StringBuilder();
      int count = 0;
      while (true) {
        int c = in.read();
        request.append((char) c);
        if (c == '\r' || c == '\n')
          count++;
        else
          count = 0;
        if (count == 4)
          break;
      }
      Scanner scan = new Scanner(request.toString());
      scan.useDelimiter("Content-Length: ");
      scan.next();
      String str = scan.next();
      scan = new Scanner(str).useDelimiter("\r\n");
      int contentLength = scan.nextInt();
      scan.close();

      String body = "";
      for (int i = 0; i < contentLength; i++)
        body += (char) in.read();

      logger.info("Body of the message : " + body);

      //using split function to get the username and password from request  body 

      String[] userdetails = body.split("[&]");
      
      logger.info("parsing the body to retrive username and password");
      
      String username = userdetails[0].substring(userdetails[0].indexOf("=")+1);
      
      String password = userdetails[1].substring(userdetails[1].indexOf("=")+1);

      logger.info("The username  is "+ username);
      logger.info("The password  is "+password);
      logger.info("Validating the user with the local database");
      username.replaceAll("\\s+","");
      //condition for checking the valid username
      if(database.containsKey(username))
      {
       String hashpass=database.get(username);
       //condition for checking the valid password
       if(hashpass.contains(password))
       {
         logger.info("Successfully authenticated");
         String fileName = tokens[1];
             if (fileName.endsWith("/")) fileName += indexFileName;
             String contentType = 
                 URLConnection.getFileNameMap().getContentTypeFor(fileName);
             if (tokens.length > 2) 
             {
               version = tokens[2];
             }

             File theFile = new File(rootDirectory, 
                 fileName.substring(1, fileName.length()));
             
             if (theFile.canRead() 
                 // Don't let clients outside the document root
                 && theFile.getCanonicalPath().startsWith(root)) {
               byte[] theData = Files.readAllBytes(theFile.toPath());
               if (version.startsWith("HTTP/")) { // send a MIME header
                 sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);
               } 
             
             //sending the content that is retrived
             raw.write(theData);
             raw.flush();
             }
             else { // can't find the file
                 String errormsg = new StringBuilder("<HTML>\r\n")
                     .append("<HEAD><TITLE>File Not Found</TITLE>\r\n")
                     .append("</HEAD>\r\n")
                     .append("<BODY>")
                     .append("<H1>HTTP Error 404: File Not Found</H1>\r\n")
                     .append("</BODY></HTML>\r\n").toString();
                 
                 if (version.startsWith("HTTP/")) { 
                   // send a MIME header
                   sendHeader(out, "HTTP/1.0 404 File Not Found", 
                       "text/html; charset=utf-8", errormsg.length());
                 }
                 //sending the message
                 out.write(errormsg);
                 out.flush();
             } 
       }

       else
       {
        logger.info("Invalid password");
        String errormsg = new StringBuilder("<HTML>\r\n")
         .append("<HEAD><TITLE></TITLE>\r\n")
         .append("</HEAD>\r\n")
         .append("<BODY>")
         .append("<H1>Invalid Password</H1>\r\n")
         .append("</BODY></HTML>\r\n").toString();
     
       // send a MIME header
       sendHeader(out, "HTTP/1.0 401 Unauthorized", 
           "text/html; charset=utf-8", errormsg.length());
     
     //sending the content errormsg
     out.write(errormsg);
     out.flush();

       }
            
      }
      
      else
      {
          //case for handling Invalid username
        logger.info("Invalid Username");
        String errormsg = new StringBuilder("<HTML>\r\n")
        .append("<HEAD><TITLE></TITLE>\r\n")
        .append("</HEAD>\r\n")
        .append("<BODY>")
        .append("<H1>Invalid Username</H1>\r\n")
        .append("</BODY></HTML>\r\n").toString();

        // sending the header 
        sendHeader(out, "HTTP/1.0 401 Unauthorized", 
          "text/html; charset=utf-8", errormsg.length());

        // sending the content errormsg
        out.write(errormsg);
        out.flush();

        }
      
    }   

      
      else { // method does not equal "GET"
        String body = new StringBuilder("<HTML>\r\n")
            .append("<HEAD><TITLE>Not Implemented</TITLE>\r\n")
            .append("</HEAD>\r\n")
            .append("<BODY>")
            .append("<H1>HTTP Error 501: Not Implemented</H1>\r\n")
            .append("</BODY></HTML>\r\n").toString();
        if (version.startsWith("HTTP/")) { // send a MIME header
          sendHeader(out, "HTTP/1.0 501 Not Implemented", 
                    "text/html; charset=utf-8", body.length());
        }
        out.write(body);
        out.flush();
      }
    }  
    catch (IOException ex) {
      logger.log(Level.WARNING, 
          "Error talking to " + connection.getRemoteSocketAddress(), ex);
    } 
    
    finally {
      try {
        connection.close();        
      }
      catch (IOException ex) {} 
    }
  }

  private void sendHeader(Writer out, String responseCode,
      String contentType, int length)
      throws IOException {
    out.write(responseCode + "\r\n");
    Date now = new Date();
    out.write("Date: " + now + "\r\n");
    out.write("Server: JHTTP 2.0\r\n");
    out.write("Content-length: " + length + "\r\n");
    out.write("Content-type: " + contentType + "\r\n\r\n");
    out.flush();
  }
}