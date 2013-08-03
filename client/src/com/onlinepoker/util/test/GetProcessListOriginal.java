package com.onlinepoker.util.test;

/*
02	 * This code get the process list which are currently running(shows in your task manager) in your system and
03	 * stored at the file named as "process.txt"
04	 *
05	 * Remarks:
06	 * The file is stored at where this java file is available.
07	 * Chache the file path which you need
08	 */
	 
	import java.io.*;
	import java.util.StringTokenizer;
	 
	/**
	 *
	 * @author Muneeswaran
	 */
	public class GetProcessListOriginal
	{
	 
	 private String GetProcessListData()
	 {
	 Process p;
	 Runtime runTime;
	 String process = null;
	 try {
	 //Get Runtime environment of System
	 runTime = Runtime.getRuntime();
	 //Execute command thru Runtime
	 p = runTime.exec("tasklist");      // For Windows
	 //p=r.exec("ps ux");              //For Linux
	 
	 
	 //Create Inputstream for Read Processes
	 InputStream inputStream = p.getInputStream();
	 InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	 BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	 
	 //Read the processes from sysrtem and add & as delimeter for tokenize the output
	 String line = bufferedReader.readLine();
	 process = "&";
	 while (line != null) {
	 line = bufferedReader.readLine();
	 //System.out.println(line+"");
	 process += line + "&";
	 }
	 
	 //Close the Streams
	 bufferedReader.close();
	 inputStreamReader.close();
	 inputStream.close();
	 
	 System.out.println("Processes are read.");
	 } catch (IOException e) {
	 System.out.println("Exception arise during the read Processes");
	 e.printStackTrace();
	 }
	 return process;
	 }
	 
	 private void showProcessData()
	 {
	 try {
	 
	 //Call the method For Read the process
	 String proc = GetProcessListData();
	 //System.out.println(proc);
	 //Create Streams for write processes
	 //Given the filepath which you need.Its store the file at where your java file.
	 OutputStreamWriter outputStreamWriter =
	 new OutputStreamWriter(new FileOutputStream("ProcessList.txt"));
	 BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
	 
	 //Tokenize the output for write the processes
	 StringTokenizer st = new StringTokenizer(proc, "&");
	 
	 while (st.hasMoreTokens()) {
		 System.out.println(st.nextToken());
		 if(st.nextToken().contains("BAP.exe")){
			 System.out.println("BAP.exe running");
		 }else{
			 //System.out.println("not running");
		 }
		 
		// System.out.println(st.nextToken());
	// bufferedWriter.write(st.nextToken());  //Write the data in file
	 //bufferedWriter.newLine();               //Allocate new line for next line
	 }
	 
	 //Close the outputStreams
	 bufferedWriter.close();
	 outputStreamWriter.close();
	 
	 } catch (IOException ioe) {
	 ioe.printStackTrace();
	 }
	 
	 }
	 
	 public static void main(String[] args)
	 {
	 GetProcessListOriginal gpl = new GetProcessListOriginal();
	 gpl.showProcessData();
	 
	 }
	}
