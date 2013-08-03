package com.onlinepoker.util.test;

import java.lang.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class SwingSample extends JFrame {
    private static JTextArea mTextArea;

    public SwingSample(String filename) {
	super("GB File Viewer");
	createUI();
	try {
	    loadfile(filename, "GB2312");  // or "BIG5"
	} 
	catch (Exception loadexc) {
	}
	setVisible(true);
    }

    public static void loadfile(String filename, String enc)
	throws IOException, UnsupportedEncodingException
    {
	String newline;
	String buffer;
	InputStream in;
	
	newline = System.getProperty("line.separator");
	
	in = new FileInputStream(filename);
	// Set up character stream
	BufferedReader r = new BufferedReader(new InputStreamReader(in, enc));
	while ((buffer = r.readLine()) != null) {
	    mTextArea.append(buffer + newline);
	}
	r.close();
    }

    protected void createUI() {
	setSize(500, 500);
	Container content = getContentPane();
	content.setLayout(new BorderLayout());
	
	mTextArea = new JTextArea();
	//mTextArea.setFont(new Font("Bitstream Cyberbit", Font.PLAIN, 12));
	JScrollPane scrollPane = new JScrollPane(mTextArea,
						 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						 JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	content.add(scrollPane, BorderLayout.CENTER);
	
	// Exit the application when the window is closed.
	addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		System.exit(0);
	    }
	});
    }

    public static void main(String[] args) {
	new SwingSample(args[0]);
    }
    
} // swingsample