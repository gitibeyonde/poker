package com.onlinepoker.lobby;

import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.golconda.db.DBPlayer;
import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;
import com.onlinepoker.resources.Bundle;

public class ForgotPasswordFrame
    extends JDialog
    implements ActionListener {

	static Logger _cat = Logger.getLogger(ForgotPasswordFrame.class.getName());
  private JTextField fieldUname;
  private JTextField fieldEmail;
  private ResourceBundle bundle;

  private Composite composite = null;

  public ForgotPasswordFrame(JFrame parent, String title) {
    super( parent);
 
    bundle = Bundle.getBundle();
    parent.setVisible(false);
    parent.setEnabled(false);

    
     
    setTitle(title);
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
    
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize =
        new Dimension(ClientConfig.DEFAULT_FORGOTPW_SCREEN_SIZE_X,
                      ClientConfig.DEFAULT_FORGOTPW_SCREEN_SIZE_Y);
    setBounds(new Rectangle( (screenSize.width - frameSize.width) / 2,
                            (screenSize.height - frameSize.height) / 2,
                            ClientConfig.DEFAULT_FORGOTPW_SCREEN_SIZE_X,
                            ClientConfig.DEFAULT_FORGOTPW_SCREEN_SIZE_Y));
    setResizable(false);

    fieldUname = new JTextField(30);
    fieldUname.addActionListener(this);
    getContentPane().add(fieldUname);

    fieldEmail = new JTextField(30);
    fieldEmail.addActionListener(this);
    getContentPane().add(fieldEmail);
    fieldUname.setBounds(100, 80, 100, 20);
    fieldEmail.setBounds(100, 110, 100, 20);
    
    
    JButton ok = createButton(Utils.getIcon( ("")),//ClientConfig.IMG_FORGOTPW_OK_1
				            Utils.getIcon( ("")),//ClientConfig.IMG_FORGOTPW_OK_2
				            Utils.getIcon( ("")));//ClientConfig.IMG_FORGOTPW_OK_3
    ok.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
    	DBPlayer dbp = new DBPlayer();
		try
		{
			dbp.get(fieldUname.getText());
		}
		catch (Exception ex) {
	      ex.printStackTrace();
	    }
    	if(!isValidEmail(fieldEmail.getText()))
    	{
    		_cat.fine("not a valid email id");
    	}
    	else if(!dbp.getEmailId().equals(fieldEmail.getText()))
		{
			_cat.fine("email id is not registered wit us");
		}
    	else
    	{
    		_cat.fine("sending email....");
    	}
    	
    }
    });
    ok.setLocation(50, 200);
    getContentPane().add(ok);

    JButton cancel = createButton(Utils.getIcon( (ClientConfig.IMG_FORGOTPW_CANCEL_1)),
                          Utils.getIcon( (ClientConfig.IMG_FORGOTPW_CANCEL_2)),
                          Utils.getIcon( (ClientConfig.IMG_FORGOTPW_CANCEL_3)));
    cancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        System.exit(0);
      }
    });
    cancel.setLocation(135, 200);
    getContentPane().add(cancel);

    
	
    getContentPane().setLayout(null);
    ( (JPanel) getContentPane()).setOpaque(false);

       
    fieldUname.setOpaque(false);
    fieldEmail.setOpaque(false);

    final ImageIcon m_image =
        Utils.getIcon(ClientConfig.IMG_FORGOTPASSWORD_BACKGROUND);
    final int winc = m_image.getIconWidth();
    final int hinc = m_image.getIconHeight();
    JLabel backlabel = new JLabel("");
    if (m_image.getIconWidth() > 0 && m_image.getIconHeight() > 0) {
      backlabel = new JLabel() {
        public void paintComponent(Graphics g) {
          int w = getParent().getWidth();
          int h = getParent().getHeight();
          if (composite != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(composite);
          }
          for (int i = 0; i < h + hinc; i = i + hinc) {
            for (int j = 0; j < w + winc; j = j + winc) {
              m_image.paintIcon(this, g, j, i);
            }
          }
        }

        public Dimension getPreferredSize() {
          return new Dimension(super.getSize());
        }

        public Dimension getMinimumSize() {
          return getPreferredSize();
        }
      };
    }

    getLayeredPane().add(backlabel, new Integer(Integer.MIN_VALUE));
    backlabel.setBounds(0, 0, 5000, 5000);

    

    setVisible(true);
    //Halt         setModal(true);
  }

  

  public void actionPerformed(ActionEvent e) {
    new Thread(new Runnable() {
      public void run() {
        
      }
    }).start();

  }

  private boolean isValidEmail(String email)
  {
	  Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	  Matcher m = p.matcher(email);
	  StringTokenizer st = new StringTokenizer(email, ".");
	  String lastToken = null;
	  while (st.hasMoreTokens()) {
	     lastToken = st.nextToken();
	  }
	  if (m.matches() && lastToken.length() >= 2
	      && email.length() - 1 != lastToken.length()) {

	      // validate the country code
	      return true;
	  }
	  else return false;
  }
  
  private JButton createButton(ImageIcon icon1, ImageIcon icon2,
                               ImageIcon icon3) {
    JButton button = new JButton(icon1);
    int w = icon1.getIconWidth();
    int h = icon1.getIconHeight();

    button.setPressedIcon(icon3);

    button.setRolloverEnabled(true);
    button.setRolloverIcon(icon2);

    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setMargin(new Insets(0, 0, 0, 0));
    button.setSize(w, h);

    return button;
  }
}
