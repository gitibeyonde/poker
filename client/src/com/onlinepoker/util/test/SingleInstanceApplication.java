package com.onlinepoker.util.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.border.EmptyBorder;
import java.util.Date;

// classes of the web-start API, used in this example.
//import javax.jnlp.SingleInstanceListener;
//import javax.jnlp.SingleInstanceService;
//import javax.jnlp.ServiceManager;
//import javax.jnlp.UnavailableServiceException;

/** A test of the SingleInstanceService using the web-start API.
@author Andrew Thompson
@version 2007/1/8
*/
public class SingleInstanceApplication
    extends JFrame{
    //implements SingleInstanceListener {

//    /** A simple editing area. */
//    JTextArea document;
//
//    /** Assemble the GUI. */
//    SingleInstanceApplication() {
//        super("JNLP API single instance service");
//
//        try {
////        	String names[] =ServiceManager.getServiceNames();
////        	for(int i=0;i<names.length;i++){
////        		System.out.println(names[i]);
////        	}
//            SingleInstanceService singleInstanceService =
//                (SingleInstanceService)ServiceManager.
//                    lookup("javax.jnlp.SingleInstanceService");
//            // add the listener to this application!
//            singleInstanceService.addSingleInstanceListener(
//                (SingleInstanceListener)this );
//        } catch(UnavailableServiceException use) {
//            use.printStackTrace();
//            System.exit(-1);
//        }
//
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        document = new JTextArea(
//            "Try openning another version of this application\n");
//        document.setEditable(false);
//
//        JPanel main = new JPanel(new BorderLayout());
//        main.add(new JScrollPane(document));
//        main.setBorder( new EmptyBorder(8,8,8,8) );
//
//        getContentPane().add(main);
//
//        pack();
//        setSize(400,300);
//        setLocationRelativeTo(null);
//    }
//
//    /** Specified by the SingleInstanceListener interface
//    @param args The command line parameters used for this invocation */
//    public void newActivation(String[] args) {
//        StringBuilder sb = new StringBuilder();
//        for (int ii=0; ii<args.length; ii++) {
//            sb.append("'" + args[ii] + "' ");
//        }
//        String message = "Got new args: " + sb.toString();
//        // this usually serves to alert the user the app.
//        // wants attention.  On Win. it will flash the
//        // apps. icon in the task bar.
//        JOptionPane.showMessageDialog(this, message);
//        // also add the new args and time to the document.
//        document.append( new Date() + "\t" + message + "\n" );
//    }

    /** Construct the GUI and display it.  If the user double clicked
    a file to start the application, begin measures to load that file. */
    public static void main(String[] args) {
        SingleInstanceApplication app =
            new SingleInstanceApplication();
        app.setVisible(true);
    }
}