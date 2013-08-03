package com.agneya.util;

import java.io.*;
import java.net.*;

public class HttpClient {
  public static void main(String[] args) {
// declaration section:
// smtpClient: our client socket
// os: output stream
// is: input stream
    Socket httpSocket = null;
    DataOutputStream os = null;
    DataInputStream is = null;
// Initialization section:
// Try to open a socket on port 25
    // Try to open input and output streams
    while (true) {
      try {
        httpSocket = new Socket("www.cricketparty.com", 80);
        os = new DataOutputStream(httpSocket.getOutputStream());
        is = new DataInputStream(httpSocket.getInputStream());
      }
      catch (UnknownHostException e) {
        System.err.println("Don't know about host: hostname");
      }
      catch (IOException e) {
        System.err.println("Couldn't get I/O for the connection to: hostname");
      }
// If everything has been initialized then we want to write some data
// to the socket we have opened a connection to on port 80
      if (httpSocket != null && os != null && is != null) {
        System.out.print(".");
        try {
// The capital string before each colon has a special meaning to SMTP
// you may want to read the SMTP specification, RFC1822/3
//<SCRIPT type='text/javascript' language='JavaScript' src='http://xslt.alexa.com/site_stats/js/s/a?url=www.cricketparty.com'></SCRIPT>
          os.writeBytes("GET / \n\n");
// keep on reading from/to the socket till we receive the "Ok" from SMTP,
// once we received that then we want to break.
          String responseLine;
          while ( (responseLine = is.readLine()) != null) {
            System.out.println("Server: " + responseLine);
          }
// clean up:
// close the output stream
// close the input stream
// close the socket
          os.close();
          is.close();
          httpSocket.close();
          System.out.println("------------DONE...WAITING FOR 50 SECONDS--------------");
          Thread.currentThread().sleep(5000);
        }
        catch (UnknownHostException e) {
          System.err.println("Trying to connect to unknown host: " + e);
        }
        catch (IOException e) {
          System.err.println("IOException:  " + e);
        }
        catch (Exception e) {
          System.err.println("IOException:  " + e);
        }

      }
    }
  }
}
