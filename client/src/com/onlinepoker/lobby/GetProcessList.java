package com.onlinepoker.lobby;
	import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
	
	public class GetProcessList
	{
	 
	 private Vector GetProcessListData()
	 {
	 Process p=null;
	 Runtime runTime;
	 Vector v = new Vector();
	 try {
	 //Get Runtime environment of System
	 runTime = Runtime.getRuntime();
//	 Map m = System.getProperties();
//	 Set s = m.keySet();
//	 Iterator ie = s.iterator();
//	 while(ie.hasNext()){
//		 String key = (String)ie.next();
//		 System.out.println(key+" - "+m.get(key));
//	 }
     String osName =System.getProperty("os.name");
     System.out.println("osName "+osName);
     
	 //Execute command thru Runtime
     if(osName.contains("Windows"))
    	 p = runTime.exec("tasklist"); // For Windows
    	 
	 //p=r.exec("ps ux");              //For Linux
	 //Create Inputstream for Read Processes
	 InputStream inputStream = p.getInputStream();
	 InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	 BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	 //Read the processes from sysrtem and add & as delimeter for tokenize the output
	 String line = bufferedReader.readLine();
	 while (line != null) {
		 line = bufferedReader.readLine();
		 if(!"null".equals(line) && line != null){
			 if(line.startsWith("BAP.exe")){
				 StringTokenizer st = new StringTokenizer(line, " ");
				 String[] process = new String[10];
				 int i=0;
				 while (st.hasMoreTokens()) {
					 process[i] = st.nextToken();
					 i++;
				 }
				 v.add(process);
			 }
		 }
	 }
	 //Close the Streams
	 bufferedReader.close();
	 inputStreamReader.close();
	 inputStream.close();
	 
	 } catch (IOException e) {
	 System.out.println("Exception arise during the read Processes");
	 e.printStackTrace();
	 }
	 return v;
	 }
	 
	 public Vector showProcessData()
	 {
		 Vector pv = null;
	 try {
		 pv = new Vector();
		 pv = GetProcessListData();
	 } catch (Exception ioe) {
		 ioe.printStackTrace();
//		 Process p;
//		 Runtime runTime = Runtime.getRuntime();
//		 try {
//			p= runTime.exec("taskkill /IM javaw.exe");
//			System.exit(-1);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	 }
	 	return pv;
	 }
	 
	 public static void main(String[] args)
	 {
	 GetProcessList gpl = new GetProcessList();
	 gpl.showProcessData();
	 
	 }
	}
