package com.onlinepoker.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;


public class MessagePopup
    extends JDialog
    implements FocusListener {

  protected JPanel panel;
  protected JEditorPane editor;
  protected JButton okButton;
  private String inputText;

  public MessagePopup(ClientFrameInterface p, String text) {
    super();
    editor = new JEditorPane();
    panel = new JPanel();
    okButton = new JButton("Ok");

    editor.setEditable(false);
    editor.setContentType("text/html");
    editor.setText("<html><center><b>" + text + "</b></center></html>");
    setSize(320, 140);
    setLocation(p.getX() + 250, p.getY() + 250);
    editor.addFocusListener(this);
    editor.setBackground(Color.GRAY);
    panel.setBackground(Color.GRAY);

    panel.setLayout(new FlowLayout());
    panel.add(okButton);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(panel, BorderLayout.SOUTH);
    this.getContentPane().add(editor, BorderLayout.CENTER);
    setTitle("Message");
//    pack();
    setVisible(true);
  }

    public MessagePopup(String url) {
      super();
      setSize(600, 500);
      setLocation(200, 200);
      try {
          editor = new JEditorPane();
          editor.setBackground(Color.BLACK);
          editor.setEditable(false);
    
          getContentPane().add(editor);
          setTitle("Message");
          setVisible(true);
          editor.setPage(new URL(url));
      }
      catch (IOException ioe){
        editor.setText("<html><center><font color=#FFFFFF><b>Unable to load the page.</b></font></center></html>");
        ioe.printStackTrace();
        return;
      }
    }

  public void focusGained(FocusEvent e) {
    if (e.getSource() == editor) {
      //System.out.println("Dialog Focus Gained");
    }
  }

  public void focusLost(FocusEvent e) {
    if (e.getSource() == editor) {
      //System.out.println("Dialog Focus Lost");
      this.dispose();
    }
  }

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
  }
}
