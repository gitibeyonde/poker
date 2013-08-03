package com.agneya.util;

import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5 {

  public boolean compare(String password, String md5) {
    return md5.equals(encode(password));
  }

  public static String encode(String password) {
    return asHex(passwordToHash(password));
  }

  public static byte[] passwordToHash(String password) {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("MD5");
    }
    catch (NoSuchAlgorithmException ex) {
      return new byte[0];
    }
    byte[] passwordBytes = null;
    try {
      passwordBytes = password.getBytes("ISO-8859-1");
    }
    catch (UnsupportedEncodingException ex) {
      passwordBytes = new byte[0];
    }
    return md.digest(passwordBytes);
  }

  public static String asHex(byte buf[]) {
	  StringBuilder strbuf = new StringBuilder(buf.length * 2);
    int i;
    for (i = 0; i < buf.length; i++) {
      if ( ( (int) buf[i] & 0xff) < 0x10) {
        strbuf.append("0");
      }
      strbuf.append(Long.toString( (int) buf[i] & 0xff, 16));
    }
    return strbuf.toString();
  }

}
