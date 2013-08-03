package com.onlinepoker.lobby;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.golconda.db.DBPlayer;
import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;
import com.onlinepoker.resources.Bundle;
import com.onlinepoker.util.PasswordCheck;


public class RegisterFrame
    extends JDialog
    implements ActionListener {

	static Logger _cat = Logger.getLogger(RegisterFrame.class.getName());
  private JTextField fieldPlayerId;
  private JTextField fieldEmail;
  private JTextField fieldPassword;
  private JTextField fieldRePassword;
  private JTextField fieldRefCode;
  private ResourceBundle bundle;
  private JLabel labelPlayerId;
  private JLabel labelPlayerAvailability;
  private JLabel labelPassword;
  private JLabel labelrePassword;
  private JLabel labelPasswordStrength;
  private JLabel labelEmail;
  private JLabel labelRefCode;
  private JTextArea labelPlayerIdDesc;
  private JTextArea labelEmailDesc;
  private JCheckBox checkboxAge;
  private JCheckBox checkboxTnC;
  private ImageIcon avatar;
  private boolean playerIDAvaiable = false;
  
  private Composite composite = null;

  public RegisterFrame(JFrame parent, String title) {
    super( parent);
 
    bundle = Bundle.getBundle();
    parent.setVisible(false);
    parent.setEnabled(true);

    labelPlayerId = new JLabel(bundle.getString("register.playerid"));
    labelPlayerAvailability = new JLabel();
	labelPassword = new JLabel(bundle.getString("register.password"));
    labelrePassword = new JLabel(bundle.getString("register.repassword"));
    labelPasswordStrength = new JLabel(bundle.getString("register.password.strength"));
    labelEmail = new JLabel(bundle.getString("register.email"));
    labelRefCode = new JLabel(bundle.getString("register.refcode"));
    labelPlayerIdDesc = new JTextArea(4, 125);
    labelPlayerIdDesc.setLineWrap(true);
    labelPlayerIdDesc.setWrapStyleWord(true);
    labelPlayerIdDesc.setBackground(new Color(0xFFFFFF));
    labelPlayerIdDesc.setText(bundle.getString("register.playerid.desc"));
    labelPlayerIdDesc.setFont(new Font("Helvetica", Font.PLAIN, 11));
    labelEmailDesc = new JTextArea(2, 125);
    labelEmailDesc.setLineWrap(true);
    labelEmailDesc.setWrapStyleWord(true);
    labelEmailDesc.setBackground(new Color(0xFFFFFF));
    labelEmailDesc.setText(bundle.getString("register.email.desc"));
    labelEmailDesc.setFont(new Font("Helvetica", Font.PLAIN, 11));
    checkboxAge = new JCheckBox(bundle.getString("register.checkbox.age"));
    checkboxAge.setBackground(new Color(0xFFFFFF));
    checkboxTnC = new JCheckBox(bundle.getString("register.checkbox.terms"));
    checkboxTnC.setBackground(new Color(0xFFFFFF));
    
    
    
    setTitle(title);
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
    
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize =
        new Dimension(ClientConfig.DEFAULT_REGISTER_SCREEN_SIZE_X,
                      ClientConfig.DEFAULT_REGISTER_SCREEN_SIZE_Y);
    setBounds(new Rectangle( (screenSize.width - frameSize.width) / 2,
                            (screenSize.height - frameSize.height) / 2,
                            ClientConfig.DEFAULT_REGISTER_SCREEN_SIZE_X,
                            ClientConfig.DEFAULT_REGISTER_SCREEN_SIZE_Y));
    setResizable(false);

    //labelPlayerId.setFont(new Font("Helvetica", Font.BOLD, 18));
    labelPlayerId.setBounds(10, 25 , 200, 20);
    labelPlayerIdDesc.setBounds(10, 70, 225, 75);
    labelPlayerAvailability.setBounds(300, 65, 225, 30);
	labelPassword.setBounds(10, 150, 75, 20);
    labelrePassword.setBounds(10, 200, 125, 30);
    labelPasswordStrength.setBounds(325, 175, 200, 20);
    labelEmail.setBounds(10, 250, 60, 30);
    labelEmailDesc.setBounds(10, 295, 225, 30);
    labelRefCode.setBounds(10, 335, 150, 30);
    checkboxAge.setBounds(10, 400, 225, 20);
    checkboxTnC.setBounds(10, 420, 225, 20);
    getContentPane().add(labelPlayerId);
    getContentPane().add(labelPlayerIdDesc);
    getContentPane().add(labelPassword);
    getContentPane().add(labelrePassword);
    getContentPane().add(labelPasswordStrength);
    getContentPane().add(labelEmail);
    getContentPane().add(labelEmailDesc);
    getContentPane().add(labelRefCode);
    getContentPane().add(checkboxAge);
    getContentPane().add(checkboxTnC);
    getContentPane().add(labelPlayerAvailability);
    
    avatar = Utils.getIcon(ClientConfig.IMG_REGISTER_AVATAR);
    JLabel labelavatar = new JLabel("",avatar,JLabel.CENTER);
    labelavatar.setBounds(325, 200, 117, 150);
    getContentPane().add(labelavatar);
    
    fieldPlayerId = new JTextField(30);
    fieldPlayerId.setBackground(new Color(0xFFFFFF));
    fieldPlayerId.addActionListener(this);
    fieldPlayerId.setBounds(10, 50, 225, 20);
    getContentPane().add(fieldPlayerId);

    fieldPassword = new JPasswordField(30);
    fieldPassword.setBackground(new Color(0xFFFFFF));
    fieldPassword.addKeyListener(new KeyListener() {
    	@Override
		public void keyPressed(KeyEvent arg0) {
			
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			PasswordCheck.CheckPasswordStrength(fieldPassword.getText());
    		//_cat.fine("Result Strength "+PasswordCheck.RESULT_STRENGTH);
    		labelPasswordStrength.setText(bundle.getString("register.password.strength")+" "+PasswordCheck.RESULT_STRENGTH.substring(0, PasswordCheck.RESULT_STRENGTH.indexOf("-")));
		}
    		});
    fieldPassword.setBounds(10, 175, 225, 20);
    getContentPane().add(fieldPassword);
    
    fieldRePassword = new JPasswordField(30);
    fieldRePassword.setBackground(new Color(0xFFFFFF));
    fieldRePassword.addActionListener(this);
    fieldRePassword.setBounds(10, 225, 225, 20);
    getContentPane().add(fieldRePassword);
    
    fieldEmail = new JTextField(30);
    fieldEmail.setBackground(new Color(0xFFFFFF));
    fieldEmail.addActionListener(this);
    fieldEmail.setBounds(10, 275, 225, 20);
    getContentPane().add(fieldEmail);
    
    fieldRefCode = new JTextField(30);
    fieldRefCode.setBackground(new Color(0xFFFFFF));
    fieldRefCode.addActionListener(this);
    fieldRefCode.setBounds(10, 360, 225, 20);
    getContentPane().add(fieldRefCode);
    
    JButton qmark1 = createButton(Utils.getIcon( ("")),//ClientConfig.IMG_REGISTER_QMARK_1
					              Utils.getIcon( ("")),//ClientConfig.IMG_REGISTER_QMARK_2
					              Utils.getIcon( ("")));//ClientConfig.IMG_REGISTER_QMARK_3
    qmark1.addActionListener(this);
    qmark1.setLocation(250, 50);
    getContentPane().add(qmark1);

    JButton qmark2 = createButton(Utils.getIcon( ("")),//ClientConfig.IMG_REGISTER_QMARK_1
						            Utils.getIcon( ("")),//ClientConfig.IMG_REGISTER_QMARK_2
						            Utils.getIcon( ("")));//ClientConfig.IMG_REGISTER_QMARK_3
    qmark2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        //System.exit(0);
      }
    });
    qmark2.setLocation(250, 175);
    getContentPane().add(qmark2);
    
    JButton qmark3 = createButton(Utils.getIcon( ("")),//ClientConfig.IMG_REGISTER_QMARK_1
						            Utils.getIcon( ("")),//ClientConfig.IMG_REGISTER_QMARK_2
						            Utils.getIcon( ("")));//ClientConfig.IMG_REGISTER_QMARK_3
    qmark3.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent event) {
	//System.exit(0);
	}
	});
    qmark3.setLocation(250, 275);
	getContentPane().add(qmark3);

	JButton check_avail = createButton(Utils.getIcon( ("")),//ClientConfig.IMG_REGISTER_QMARK_1
							            Utils.getIcon( ("")),//ClientConfig.IMG_REGISTER_QMARK_2
							            Utils.getIcon( ("")));//ClientConfig.IMG_REGISTER_QMARK_3
	check_avail.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent event) {
		if(fieldPlayerId.getText().length() > 5)
		{
			try
			{
				DBPlayer dbp = new DBPlayer();
				if(dbp.get(fieldPlayerId.getText()))
				{
					labelPlayerAvailability.setText(fieldPlayerId.getText()+" is already exists");
					playerIDAvaiable = false;
				}
				else
				{
					labelPlayerAvailability.setText(fieldPlayerId.getText()+" is available");
					playerIDAvaiable = true;
				}
				
			}
			catch (Exception ex) {
		      ex.printStackTrace();
		    }
		}
		else
		{
			_cat.fine("check aval len "+fieldPlayerId.getText().length());
			
		}
		
	}
	});
	check_avail.setLocation(300, 50);
	getContentPane().add(check_avail);
	
	JButton change_avatar = createButton(Utils.getIcon( (ClientConfig.IMG_REGISTER_CHANGE_AVATAR_1)),
						                 Utils.getIcon( (ClientConfig.IMG_REGISTER_CHANGE_AVATAR_2)),
						                 Utils.getIcon( (ClientConfig.IMG_REGISTER_CHANGE_AVATAR_3)));
	change_avatar.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent event) {
	//System.exit(0);
	}
	});
	change_avatar.setLocation(325, 360);
	getContentPane().add(change_avatar);
	
	JButton signup = createButton(Utils.getIcon( ("")),//ClientConfig.IMG_REGISTER_SIGNUP_1
            Utils.getIcon( ("")),//ClientConfig.IMG_REGISTER_SIGNUP_2
            Utils.getIcon( ("")));//ClientConfig.IMG_REGISTER_SIGNUP_3
	signup.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent event) {
		if(!playerIDAvaiable)
		{
			_cat.fine("Player id not available");
		}
		else if(!fieldPassword.getText().equals(fieldRePassword.getText()))
		{
			_cat.fine("password not matched"+fieldPassword.getText()+"---"+fieldRePassword.getText());
		}
		else if(!isValidEmail(fieldEmail.getText()))
		{
			_cat.fine("not a valid email id");
		}
		else if(!checkboxAge.isSelected())
		{
			_cat.fine("age check box not checked");
		}
		else if(!checkboxTnC.isSelected())
		{
			_cat.fine("terms and conditions not checked");
		}
		else
		{
			_cat.fine("Sending to register");
			
		}
	}
	});
	signup.setLocation(300, 425);
	getContentPane().add(signup);
	
	getContentPane().setLayout(null);
    ( (JPanel) getContentPane()).setOpaque(false);

       
    fieldPlayerId.setOpaque(false);
    fieldEmail.setOpaque(false);

    final ImageIcon m_image =
        Utils.getIcon(ClientConfig.IMG_REGISTER_BACKGROUND);
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
