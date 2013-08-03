package com.agneya.util;

import com.bitmechanic.sql.ConnectionPool;
import com.bitmechanic.sql.ConnectionPoolManager;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Enumeration;


public class ConnectionManager {

  private static ConnectionPoolManager cpm = null;
  public static String _driver;
  private static String _url;
  private static String _user;
  private static String _passwd;

  public static boolean isOracle() {
    return _driver.startsWith("oracle");
  }

  public static boolean isMysql() {
    return _driver.startsWith("com.mysql");
  }

  static {
    try {
      // If we haven't initialized the pool, initialize it
      if (cpm == null) {
        ConnectionManager.cpm = ConnectionManager.getInstance();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static synchronized ConnectionPoolManager getInstance() throws
      Exception {
    if (cpm == null) {
      try {
        Configuration c = Configuration.instance();
        _url = c.getProperty("JDBC.URL");
        _user = c.getProperty("JDBC.User");
        _passwd = c.getProperty("JDBC.Pwd");
        _driver = c.getProperty("JDBC.Driver");
        cpm = new ConnectionPoolManager(Integer.parseInt(c.getProperty(
            "JDBC.MonitorInterval")));
        //Get the connection pool names
        int poolCount = Integer.parseInt(c.getProperty("JDBC.pool.PoolCount"));
        for (int i = 0; i < poolCount; i++) {
          String poolAlias = c.getProperty("JDBC.pool.Pool_" + (i + 1));
          if (poolAlias == null) {
            poolAlias = "Pool_" + (i + 1);
          }
          _driver = c.getProperty("JDBC.Driver");
          //cpmLog.addAlias(aliasLog, DBMgr.driverClassName, DBMgr.DBLog, DBMgr.User, DBMgr.Password, DBMgr.maxConn, DBMgr.idleTimeout, DBMgr.checkoutTimeout, DBMgr.maxCheckout);
        
          cpm.addAlias(poolAlias, _driver,
                       _url, _user, _passwd,
                       Integer.parseInt(c.getProperty("JDBC.MaxConn")),
                       Integer.parseInt(c.getProperty("JDBC.IdleTimeOut")),
                       Integer.parseInt(c.getProperty("JDBC.CheckoutTimeout")));
        }
        //cpm.setTracing(true);
      }
      catch (SQLException sqle) {
        sqle.printStackTrace();
      }
      catch (IllegalAccessException iae) {
        iae.printStackTrace();
      }
      catch (ConfigurationException ce) {
        ce.printStackTrace();
      }
      catch (InstantiationException ie) {
        ie.printStackTrace();
      }
      catch (ClassNotFoundException cne) {
        cne.printStackTrace();
      }
    }

    return cpm;
  }

  public static synchronized Connection getConnection(String alias) throws
      SQLException {
    return DriverManager.getConnection(ConnectionPoolManager.URL_PREFIX +
                                       alias);
  }

  public synchronized void closeConnection(Connection conn) throws SQLException {
    // This will return the connection back to the pool
    conn.close();
  }

  public static synchronized void clearPool(Connection conn){
    Enumeration enumz=cpm.getPools();
    for (;enumz.hasMoreElements();){
      ConnectionPool cp = (ConnectionPool)enumz.nextElement();
      cp.removeAllConnections();
    }
  }

  public static void main(String args[]) {
    try {
      Configuration c = Configuration.instance();
      String driver = c.getProperty("JDBC.Driver");
      String dsn = c.getProperty("JDBC.URL");
      String usr = c.getProperty("JDBC.User");
      String pwd = c.getProperty("JDBC.Pwd");
      System.out.println("dsn= " + dsn + ", usr= " + usr + ", pwd= " + pwd);
      Class.forName(driver).newInstance();

      Connection connection = DriverManager.getConnection(dsn, usr, pwd);

      DatabaseMetaData meta = connection.getMetaData();
      System.out.println("\nDriver Information");
      System.out.println("Driver Name: "
                         + meta.getDriverName());
      System.out.println("Driver Version: "
                         + meta.getDriverVersion());
      System.out.println("\nDatabase Information ");
      System.out.println("Database Name: "
                         + meta.getDatabaseProductName());
      System.out.println("Database Version: " +
                         meta.getDatabaseProductVersion());

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

}
