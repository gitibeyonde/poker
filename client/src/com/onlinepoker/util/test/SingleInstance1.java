package com.onlinepoker.util.test;
	import java.io.File;
	import java.io.IOException;
	import java.io.RandomAccessFile;
	import java.nio.channels.FileChannel;
	import java.nio.channels.FileLock;
	 
	public class SingleInstance1 {
	 
	    private static File f;
	    private static FileChannel channel;
	    private static FileLock lock;
	 
	    public static void main(String[] args) {
	        try {
	            f = new File("RingOnRequest.lock");
	            // Check if the lock exist
	            if (f.exists()) {
	                // if exist try to delete it
	                f.delete();
	            }
	            // Try to get the lock
	            channel = new RandomAccessFile(f, "rw").getChannel();
	            lock = channel.tryLock();
	            if(lock == null)
	            {
	                // File is lock by other application
	                channel.close();
	                throw new RuntimeException("Only 1 instance of MyApp can run.");
	            }
	            // Add shutdown hook to release lock when application shutdown
	            ShutdownHook shutdownHook = new ShutdownHook();
	            Runtime.getRuntime().addShutdownHook(shutdownHook);
	 
	            //Your application tasks here..
	            System.out.println("Running");
	            try {
	                Thread.sleep(10000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	 
	        }
	        catch(IOException e)
	        {
	            throw new RuntimeException("Could not start process.", e);
	        }
	 
	    }
	 
	    public static void unlockFile() {
	        // release and delete file lock
	        try {
	            if(lock != null) {
	                lock.release();
	                channel.close();
	                f.delete();
	            }
	        } catch(IOException e) {
	            e.printStackTrace();
	        }
	    }
	 
	    static class ShutdownHook extends Thread {
	 
	        public void run() {
	            unlockFile();
	        }
	    }
	 
	}