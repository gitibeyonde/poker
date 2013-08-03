/**
 * JDBCConnectionPool extends ObjectPool to pool connections efficiently
 **/

package com.agneya.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.logging.Logger;


public class JDBCConnectionPool
    extends ObjectPool {
  private String driver, dsn, usr, pwd;
  static Logger _cat = Logger.getLogger(JDBCConnectionPool.class.getName());

  public JDBCConnectionPool() {
    try {
      Configuration c = Configuration.instance();
      driver = c.getProperty("JDBC.Driver");
      dsn = c.getProperty("JDBC.Url");
      usr = c.getProperty("JDBC.User");
      pwd = c.getProperty("JDBC.Passwd");
      _cat.info("1dsn= " + dsn + " usr= " + usr + "pwd= " + pwd);
      Class.forName(driver).newInstance();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public JDBCConnectionPool(String driver, String dsn, String usr, String pwd) {
    try {
      _cat.info("2dsn= " + dsn + " usr= " + usr + "pwd= " + pwd);
      Class.forName(driver).newInstance();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    this.dsn = dsn;
    this.usr = usr;
    this.pwd = pwd;
  }

  Object create() {
    try {
      _cat.info("dsn= " + dsn + " usr= " + usr + "pwd= " + pwd);
      return (DriverManager.getConnection(dsn, usr, pwd));
    }
    catch (SQLException e) {
      e.printStackTrace();
      return (null);
    }
  }

  boolean validate(Object o) {
    try {
      return (! ( (Connection) o).isClosed());
    }
    catch (SQLException e) {
      e.printStackTrace();
      return (false);
    }
  }

  void expire(Object o) {
    try {
      ( (Connection) o).close();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Connection borrowConnection() {
    return ( (Connection)super.checkOut());
  }

  public void returnConnection(Connection c) {
    super.checkIn(c);
  }

}
