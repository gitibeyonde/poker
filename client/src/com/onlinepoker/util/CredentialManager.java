package com.onlinepoker.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.agneya.util.Base64;
import com.onlinepoker.lobby.LobbyUserImp;

public class CredentialManager {
    static Logger _cat = Logger.getLogger(CredentialManager.class.getName());
	public static String _user_home;
    public CredentialManager() {
    	_user_home = System.getProperty("user.home").replace("\\", "/");
    }
    
    public static void saveAuthInfo(String user, String pass) {
    	try {
            File pf = new File(_user_home + "/.bs/.cred");
            if (!pf.exists()){
                pf.mkdirs();
            }
            File pwf = new File(_user_home + "/.bs/.cred/auth");
            if (!pwf.exists()){
                pf.createNewFile();
            }
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(pwf)), true);
            // write name
            pw.write("cred_name=" + Base64.encodeString(user) + "\n");
            pw.write("cred_pass=" + Base64.encodeString(pass) + "\n"); 
            pw.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static String[] getAuthInfo(){
        String []av = new String[2];
        try {
            File pf = new File(_user_home + "/.bs/.cred/auth");
            Properties p = new Properties();
            FileInputStream is = new FileInputStream(pf);
            p.load(is);
            av[0]=Base64.decodeString(p.getProperty("cred_name"));
            av[1]=Base64.decodeString(p.getProperty("cred_pass"));
            is.close();
        }
        catch (Exception e){
            return null;
        }
        return av;
    }
    
    public static void saveLanguage(String language) {
    	try {
            File pf = new File(_user_home + "/.bs/.cred");
            if (!pf.exists()){
                pf.mkdirs();
            }
            File pwf = new File(_user_home + "/.bs/.cred/lang");
            if (!pwf.exists()){
                pf.createNewFile();
            }
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(pwf)), true);
            // write name
            pw.write("language_name=" + Base64.encodeString(language) + "\n");
            pw.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static String getLanguageInfo(){
        String av = "";
        try {
            File pf = new File(_user_home + "/.bs/.cred/lang");
            Properties p = new Properties();
            FileInputStream is = new FileInputStream(pf);
            p.load(is);
            av=Base64.decodeString(p.getProperty("language_name"));
            is.close();
        }
        catch (Exception e){
        	saveLanguage("english");
        	av="english";
        }
        return av;
    }
    public static String getPlayerNoteInfo(String playerName){
        String prev_note = "";
        try {
        	//System.out.println("playername "+playerName);
        	Map<String, String>map = LobbyUserImp.map;
            Set propertySet = map.entrySet();
	           for (Object o : propertySet) {
	             Map.Entry entry = (Map.Entry) o;
	             if(entry.getKey().equals("note."+playerName)){
	           	  prev_note =  Base64.decodeString((String)entry.getValue());
	              //System.out.printf("%s = %s%n", entry.getKey(), prev_note);
	              break;
	             }
	          }
        }
        catch (Exception e){
        	//savePlayerComment("english");
        	//av="english";
        	e.printStackTrace();
        }
        return prev_note;
    }
    
    public static void savePlayerNotes() {
   	 String av = "";
   	try {
           File pf = new File(_user_home + "/.bs/.cred");
           if (!pf.exists()){
               pf.mkdirs();
           }
           File pwf = new File(_user_home + "/.bs/.cred/note");
           if (!pwf.exists()){
               pwf.createNewFile();
           }
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(pwf)), true);
            // write note
            Map<String, String>map = LobbyUserImp.map;
            Set propertySet = map.entrySet();
	           for (Object o : propertySet) {
	             Map.Entry entry = (Map.Entry) o;
	             String com = (String)entry.getValue();
	            // System.out.printf("%s = %s%n", entry.getKey(), Base64.decodeString(com));
	             pw.write(entry.getKey() +"="+ com + "\n");
	             
	           }
	           _cat.fine("notes saved");
            pw.close();
       }
       catch (Exception e){
           e.printStackTrace();
       }
   }
   
   public static Map<String, String> getPlayerNotes(){
       Map<String, String> map = null;
       try {
           File pwf = new File(_user_home + "/.bs/.cred/note");
           _cat.finest(_user_home +" note file exist "+pwf.exists());
           if (!pwf.exists()){
        	   //System.out.println("new note file");
               pwf.createNewFile();
           }
           Properties p = new Properties();
           FileInputStream is = new FileInputStream(pwf);
           p.load(is);
           map = new ConcurrentHashMap<String, String>((Map) p);
           _cat.finest("notes loaded");
           //System.out.println("HashMap size "+map.entrySet().size());
       }
       catch (Exception e){
    	   e.printStackTrace();
    	  // return new HashMap<String, String>();
       	 
       }
       return map;
   }
    
    
    
    public static void savePreferences(int pref) {
    	try {
            File pf = new File(_user_home + "/.bs/.cred");
            if (!pf.exists()){
                pf.mkdirs();
            }
            File pwf = new File(_user_home + "/.bs/.cred/pref");
            if (!pwf.exists()){
                pwf.createNewFile();
            }
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(pwf)), true);
            // write name
            pw.write("preferences=" + pref + "\n");
            pw.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static int getPreferences(){
        int av = -1;
        try {
            File pf = new File(_user_home + "/.bs/.cred/pref");
            Properties p = new Properties();
            FileInputStream is = new FileInputStream(pf);
            p.load(is);
            av=Integer.parseInt(p.getProperty("preferences"));
            is.close();
        }
        catch (Exception e){
        	
        }
        return av;
    }
    
    public static void saveGameStatusInfo(int cgcol1, int cgcol2, int cgcol3, int tpcol2, int tpcol3,
							    		  int sngcol2, int sngcol3, int sngcol4,
							    		  int mttcol2, int mttcol3, int mttcol4,
							    		  boolean cgMicro, boolean cgLow, boolean cgMedium, boolean cgHigh,
							    		  boolean sngfreerol, boolean sngMicro, boolean sngLow, boolean sngMedium, boolean sngHigh,
							    		  boolean mttfreerol, boolean mttMicro, boolean mttLow, boolean mttMedium, boolean mttHigh,
							    		  boolean playorreal) {
    	try {
            File pf = new File(_user_home + "/.bs/.cred");
            if (!pf.exists()){
                pf.mkdirs();
            }
            File pwf = new File(_user_home + "/.bs/.cred/status");
            if (!pwf.exists()){
                pf.createNewFile();
            }
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(pwf)), true);
            // write name
            pw.write("cg-col1=" + cgcol1 + "\n");
            pw.write("cg-col2=" + cgcol2 + "\n"); 
            pw.write("cg-col3=" + cgcol3 + "\n"); 
            pw.write("tp-col2=" + tpcol2 + "\n"); 
            pw.write("tp-col3=" + tpcol3 + "\n"); 
            pw.write("sng-col2=" + sngcol2 + "\n");
            pw.write("sng-col3=" + sngcol3 + "\n"); 
            pw.write("sng-col4=" + sngcol4 + "\n"); 
            pw.write("mtt-col2=" + mttcol2 + "\n");
            pw.write("mtt-col3=" + mttcol3 + "\n"); 
            pw.write("mtt-col4=" + mttcol4 + "\n"); 
            pw.write("cg-micro=" + (cgMicro?1:0) + "\n"); 
            pw.write("cg-low=" + (cgLow?1:0) + "\n");
            pw.write("cg-medium=" + (cgMedium?1:0) + "\n"); 
            pw.write("cg-high=" + (cgHigh?1:0) + "\n"); 
            pw.write("sng-freerol=" + (sngfreerol?1:0) + "\n"); 
            pw.write("sng-micro=" + (sngMicro?1:0) + "\n"); 
            pw.write("sng-low=" + (sngLow?1:0) + "\n");
            pw.write("sng-medium=" + (sngMedium?1:0) + "\n"); 
            pw.write("sng-high=" + (sngHigh?1:0) + "\n"); 
            pw.write("mtt-freerol=" + (mttfreerol?1:0) + "\n"); 
            pw.write("mtt-micro=" + (mttMicro?1:0) + "\n"); 
            pw.write("mtt-low=" + (mttLow?1:0) + "\n");
            pw.write("mtt-medium=" + (mttMedium?1:0) + "\n"); 
            pw.write("mtt-high=" + (mttHigh?1:0) + "\n"); 
            pw.write("type-play=" + (playorreal?1:0) + "\n"); 
            pw.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static int[] getGameStatusInfo(){
        int []av = new int[26];
        try {
            File pf = new File(_user_home + "/.bs/.cred/status");
            Properties p = new Properties();
            FileInputStream is = new FileInputStream(pf);
            p.load(is);
            av[0]=Integer.parseInt(p.getProperty("cg-col1"));
            av[1]=Integer.parseInt(p.getProperty("cg-col2"));
            av[2]=Integer.parseInt(p.getProperty("cg-col3"));
            av[3]=Integer.parseInt(p.getProperty("sng-col2"));
            av[4]=Integer.parseInt(p.getProperty("sng-col3"));
            av[5]=Integer.parseInt(p.getProperty("sng-col4"));
            av[6]=Integer.parseInt(p.getProperty("mtt-col2"));
            av[7]=Integer.parseInt(p.getProperty("mtt-col3"));
            av[8]=Integer.parseInt(p.getProperty("mtt-col4"));
            av[9]=Integer.parseInt(p.getProperty("cg-micro"));
            av[10]=Integer.parseInt(p.getProperty("cg-low"));
            av[11]=Integer.parseInt(p.getProperty("cg-medium"));
            av[12]=Integer.parseInt(p.getProperty("cg-high"));
            av[13]=Integer.parseInt(p.getProperty("sng-freerol"));
            av[14]=Integer.parseInt(p.getProperty("sng-micro"));
            av[15]=Integer.parseInt(p.getProperty("sng-low"));
            av[16]=Integer.parseInt(p.getProperty("sng-medium"));
            av[17]=Integer.parseInt(p.getProperty("sng-high"));
            av[18]=Integer.parseInt(p.getProperty("mtt-freerol"));
            av[19]=Integer.parseInt(p.getProperty("mtt-micro"));
            av[20]=Integer.parseInt(p.getProperty("mtt-low"));
            av[21]=Integer.parseInt(p.getProperty("mtt-medium"));
            av[22]=Integer.parseInt(p.getProperty("mtt-high"));
            av[23]=Integer.parseInt(p.getProperty("type-play"));
            av[24]=Integer.parseInt(p.getProperty("tp-col2"));
            av[25]=Integer.parseInt(p.getProperty("tp-col3"));
            is.close();
        }
        catch (Exception e){
        	e.printStackTrace();
        	return null;
        }
        return av;
    }
    
}
