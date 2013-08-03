package com.onlinepoker.util.test;

/*import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.Timer;

public class MainClass {
  public static void main(String[] args) {
    Timer t = new Timer(1000, new Ticker());
    t.start();
    JOptionPane.showMessageDialog(null, "Click OK to exit program");
    System.exit(0);
  }

  static class Ticker implements ActionListener {
    private boolean tick = true;

    public void actionPerformed(ActionEvent event) {
      if (tick) {
        System.out.println("Tick...");
      } else {
        System.out.println("Tock...");
      }
      tick = !tick;
    }
  }
}*/
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class MainClass {

  public static void main(String[] args) throws IOException {

    URL u = new URL("http://www.supergameasia.com");
    String host = u.getHost();
    int port = 80;
    String file = "/";

    SocketAddress remote = new InetSocketAddress(host, port);
    SocketChannel channel = SocketChannel.open(remote);
    FileOutputStream out = new FileOutputStream("yourfile.htm");
    FileChannel localFile = out.getChannel();

    String request = "GET " + file + " HTTP/1.1\r\n" + "User-Agent: HTTPGrab\r\n"
        + "Accept: text/*\r\n" + "Connection: close\r\n" + "Host: " + host + "\r\n" + "\r\n";

    ByteBuffer header = ByteBuffer.wrap(request.getBytes("US-ASCII"));
    channel.write(header);

    ByteBuffer buffer = ByteBuffer.allocate(8192);
    while (channel.read(buffer) != -1) {
      buffer.flip();
      localFile.write(buffer);
      buffer.clear();
    }

    localFile.close();
    channel.close();
  }
}
