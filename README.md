                                      HTTP SERVER


</br>

  Introduction:


  HEAD METHOD: 

  The HEAD method is used to ask only for information about a document. So implemented head method below just check whether client requested file is present or not and basing on that sends XML response. 


  POST METHOD:

  By design, the POST request method requests that a web server accepts the data enclosed in the body of the request message, most likely for storing it. It is often used when uploading a file or when submitting a completed web form.


  Here implemented a POST method where it retrives username and password sent by client from the request body and then validates it with local database and then check for resource client request 


  if yes then gives the file contents 

  else send error response in the form of XML

  if username and password are not valid itself then it throws

  401 unauthorised expection and then error response in form of XML.

  (please check screen shots below)


</br>


                          Requirements for the Assignment:



Implement the HEAD method, by making the necessary changes in RequestProcessor.java


Implement the POST method, by making the necessary changes in RequestProcessor.java



Create a simple Java client that sends a HEAD request to  revised JHTTP server (JHTTP.java & RequestProcessor.java) to test your implementation.



Create a simple Java client that sends a POST request to  revised JHTTP server (JHTTP.java & RequestProcessor.java) to test your implementation.


</br>

Testing Scenarios:


Case 1 : correct filename and correct username and password in post method Correct filename in head method


Case 2: In correct filename and correct username and password in post method In Correct filename in head method


Case 3: In correct username and correct filename in post method 


Case 4: In correct Password and correct filename in post method


OUTPUT SCREENSHOTS for all the above scenarios.


![alt text](https://github.com/RepakaRamateja/HTTP-Server/blob/master/Images/1.png)


![alt text](https://github.com/RepakaRamateja/HTTP-Server/blob/master/Images/2.png)


![alt text](https://github.com/RepakaRamateja/HTTP-Server/blob/master/Images/3.png)


![alt text](https://github.com/RepakaRamateja/HTTP-Server/blob/master/Images/4.png)


![alt text](https://github.com/RepakaRamateja/HTTP-Server/blob/master/Images/5.png)


![alt text](https://github.com/RepakaRamateja/HTTP-Server/blob/master/Images/6.png)


![alt text](https://github.com/RepakaRamateja/HTTP-Server/blob/master/Images/7.png)


![alt text](https://github.com/RepakaRamateja/HTTP-Server/blob/master/Images/8.png)


![alt text](https://github.com/RepakaRamateja/HTTP-Server/blob/master/Images/9.png)


![alt text](https://github.com/RepakaRamateja/HTTP-Server/blob/master/Images/10.png)


![alt text](https://github.com/RepakaRamateja/HTTP-Server/blob/master/Images/11.png)


please go through the IpAsignment2.pdf to understand clearly.


