package com.onlinepoker.util.test;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DialogDesktop extends JFrame {

  public DialogDesktop(String title) {
    super(title);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    final JDesktopPane desk = new JDesktopPane();
    setContentPane(desk);

    // Create our "real" application container; use any layout manager we
    // want.
    final JPanel p = new JPanel(new GridBagLayout());

    // Listen for desktop resize events so we can resize p. This will ensure
    // that
    // our container always fills the entire desktop.
    desk.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent ev) {
        Dimension deskSize = desk.getSize();
        p.setBounds(0, 0, deskSize.width, deskSize.height);
        p.validate();
      }
    });

    // Add our application panel to the desktop. Any layer below the
    // MODAL_LAYER
    // (where the dialogs will appear) is fine. We'll just use the default
    // in
    // this example.
    desk.add(p);

    // Fill out our app with a few buttons that create dialogs
    JButton input = new JButton("Input");
    JButton confirm = new JButton("Confirm");
    JButton message = new JButton("Message");
    p.add(input);
    p.add(confirm);
    p.add(message);

    input.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        JOptionPane.showInternalInputDialog(desk, "Enter Name");
      }
    });

    confirm.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        JOptionPane.showInternalConfirmDialog(desk, "Is this OK?");
      }
    });

    message.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        JOptionPane.showInternalMessageDialog(desk, "The End");
      }
    });
  }

  // A simple test program
  public static void main(String[] args) {
    DialogDesktop td = new DialogDesktop("Desktop");
    td.setSize(350, 250);
    td.setVisible(true);
  }
}
