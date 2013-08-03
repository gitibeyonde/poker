package com.onlinepoker.lobby;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;


public class LoginSettings
{
  
  private InputStream is;
  private OutputStream os;
  private boolean isServiceAvailable;

  private URL urlCodebase;
  private URL urlFile;
  private URL[] urlFiles;
  private String[] fileNames;
  private long maxlength;
  private long filesize;
  private int i, j;

  private String login;
  private String password;
  private boolean rememberPassword;
  private boolean rememberLogin;
  private boolean autoLogin;
  private String fileName = System.getProperty("user.home").replace("\\", "/")+"/remember.txt";
  
  public LoginSettings()
  {
    login = new String();
    password = new String();
    rememberPassword = false;
    autoLogin = false;
    isServiceAvailable = false;
    loadSettings ();
  }
  
  public void loadSettings ()
  {
	  File f = new File(fileName);
	  FileInputStream fis = null;
	  BufferedInputStream bis = null;
	  DataInputStream dis = null;
	  try 
	  {
	      fis = new FileInputStream(f);
	      bis = new BufferedInputStream(fis);
	      dis = new DataInputStream(bis);
	      while (dis.available() != 0) {
	        System.out.println(dis.readLine());
	      }
	      fis.close();
	      bis.close();
	      dis.close();
	
	   } 
	   catch (Exception e) 
	   {
	      e.printStackTrace();
	   } 

  }
  public void saveSettings ()
  {
	  File f = new File(fileName);
	  try 
	  {
		if(!f.exists())f.createNewFile();
  		BufferedWriter out = new BufferedWriter(new FileWriter(f));
	    out.write(login+"\r\n");
	    out.write(password);
	    out.close();
	  } 
	  catch (Exception e) {
		e.printStackTrace();
	  }
  }
  public boolean isAutoLogin ()
  {
    return autoLogin;
  }
  public void setAutoLogin (boolean autoLogin)
  {
    this.autoLogin = autoLogin;
  }

  public boolean isRememberPassword ()
  {
    return rememberPassword;
  }
  public void setRememberPassword (boolean rememberPassword)
  {
    this.rememberPassword = rememberPassword;
  }
  
  public boolean isRememberLogin ()
  {
    return rememberLogin;
  }
  public void setRememberLogin (boolean rememberLogin)
  {
    this.rememberLogin = rememberLogin;
  }

  public String getLogin ()
  {
    return login;
  }
  public void setLogin (String login)
  {
    this.login = login;
  }

  public String getPassword ()
  {
    return password;
  }
  public void setPassword (String password)
  {
    this.password = password;
  }
}
