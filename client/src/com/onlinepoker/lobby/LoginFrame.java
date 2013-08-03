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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;
import com.onlinepoker.exceptions.AuthenticationException;
import com.onlinepoker.proxies.PingDetailsListener;
import com.onlinepoker.resources.Bundle;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.util.BrowserLaunch;
import com.onlinepoker.util.CredentialManager;

public class LoginFrame
    extends JFrame
    implements ActionListener,PingDetailsListener{

  private LobbyFrameInterface lobbyFrameInterface;
  private JLabel labelPlayersOnline;
  private JLabel labelActiveTables;
  private JLabel labelPlayersOnlineCount;
  private JLabel labelActiveTablesCount;
  private JLabel labelLoginCaption;
  private JLabel labelForgotPassword;
  private JLabel labelDownload;
  private JLabel labelExistingReg;
  private JLabel labelComingSoon1;
  private JLabel labelComingSoon2;
  private JLabel labelComingSoon3;
  private JLabel labelComingSoon4;
  private JTextField field;
  private JPasswordField fieldPass;
  private JFrame parent;
  private JLabel status;
  private ResourceBundle bundle;

  private JCheckBox chbRemLogin;
  private JCheckBox chbRemPwd;
	
  //- begin codec's code --------------------------------------------------------------------
  private LoginSettings settings;
  private ImageIcon btn_blue;
  private ImageIcon btn_black;
  private ImageIcon btn_blue_ovr;
  private ImageIcon btn_black_ovr;
  private JLabel labelRemmPass;
  private JLabel labelRemmLogin;
  private JButton btnLogin;
  private JButton btnRegister;
  private JButton btnObserve;

  private CredentialManager credentialManager;
  static long PING_INTERVAL = 10000;
  ServerProxy _serverProxy;
  Timer t;Font dotsFont;
  //- end of codec's code -------------------------------------------------------------------

  private Composite composite = null;

  public LoginFrame(final JFrame parent, final LobbyFrameInterface lobbyFrameInterface,
                    final LoginSettings settings, String title, boolean needRegister,
                    final ServerProxy _serverProxy) {
	  super();
		bundle = Bundle.getBundle();
    this.parent = parent;
    this.settings = settings;
    this.lobbyFrameInterface = lobbyFrameInterface;
    this._serverProxy = _serverProxy;
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    credentialManager = new CredentialManager();
    
//   try{
//	    //dotsFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/dots.TTF"));
//
////    	InputStream fontStream = LoginFrame.class.getResourceAsStream( "/fonts/dots.TTF" );
////    	Font onePoint = Font.createFont( Font.TRUETYPE_FONT, fontStream );
////    	fontStream.close();
////    	dotsFont = onePoint.deriveFont( Font.PLAIN, 18 );
//   }catch (Exception e) {
//		// TODO: handle exception
//	}
    parent.setVisible(false);
    parent.setEnabled(false);
    _serverProxy.addPingDetailsListener(this);
    setJMenuBar(lobbyFrameInterface.getLoginMenu());
    startPTThread();
    btn_blue = Utils.getIcon(ClientConfig.BTN_BLUE); 
    btn_black = Utils.getIcon(ClientConfig.BTN_BLACK);
    btn_blue_ovr = Utils.getIcon(ClientConfig.BTN_BLUE_OVER); 
    btn_black_ovr = Utils.getIcon(ClientConfig.BTN_BLACK_OVER);

    labelLoginCaption = new JLabel(bundle.getString("login"));
    labelLoginCaption.setForeground(new Color(0xFFFFFF));
    
    labelPlayersOnline = new JLabel(bundle.getString("login.players.online"));
    labelPlayersOnline.setForeground(new Color(0x6B696C));
    labelPlayersOnline.setFont(new Font("Myriad Web", Font.PLAIN, 14));
    
    labelPlayersOnlineCount = new JLabel("000000");
    labelPlayersOnlineCount.setFont(new Font("Dots All For Now JL", Font.PLAIN, 18));
    labelPlayersOnlineCount.setForeground(new Color(0xFFFFFF));
    
    labelActiveTables = new JLabel(bundle.getString("login.activetables"));
    labelActiveTables.setForeground(new Color(0x6B696C));
    labelActiveTables.setFont(new Font("Myriad Web", Font.PLAIN, 14));
    
    labelActiveTablesCount = new JLabel("00000");
    //labelActiveTablesCount.setFont(dotsFont);
    labelActiveTablesCount.setFont(new Font("Dots All For Now JL", Font.PLAIN, 18));
    labelActiveTablesCount.setForeground(new Color(0xFFFFFF));
    
    labelForgotPassword = new JLabel(bundle.getString("forgot.password"));
    labelForgotPassword.setFont(new Font("Myriad Web", Font.ITALIC, 11));
    labelForgotPassword.setForeground(new Color(0x6B696C));
    
    labelDownload = new JLabel(bundle.getString("login.download"));
    //labelDownload.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    labelDownload.setForeground(new Color(0xFFFFFF));
    labelDownload.setFont(new Font("Myriad Web", Font.PLAIN, 19));
    
    labelExistingReg = new JLabel(bundle.getString("login.existing.reg"));
    //labelExistingReg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    labelExistingReg.setForeground(new Color(0x6B696C));
    labelExistingReg.setFont(new Font("Myriad Web", Font.PLAIN, 13));
    
    labelComingSoon1 = new JLabel(bundle.getString("login.comingsoon"));
    labelComingSoon1.setForeground(new Color(0xFFFFFF));
    labelComingSoon1.setFont(new Font("Myriad Web", Font.BOLD, 11));
    
    labelComingSoon2 = new JLabel(bundle.getString("login.comingsoon"));
    labelComingSoon2.setForeground(new Color(0xFFFFFF));
    labelComingSoon2.setFont(new Font("Myriad Web", Font.BOLD, 11));
    
    labelComingSoon3 = new JLabel(bundle.getString("login.comingsoon"));
    labelComingSoon3.setForeground(new Color(0xFFFFFF));
    labelComingSoon3.setFont(new Font("Myriad Web", Font.BOLD, 11));
    
    labelComingSoon4 = new JLabel(bundle.getString("login.comingsoon"));
    labelComingSoon4.setForeground(new Color(0xFFFFFF));
    labelComingSoon4.setFont(new Font("Myriad Web", Font.BOLD, 11));
    
    btnLogin = createButton("<html><font face=\"Myriad Web\" color=#ffffdd>Sign in</font></html>",btn_blue);
    btnLogin.addMouseListener(new MouseAdapter() {
		
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			btnLogin.setIcon(btn_blue);
		}
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			btnLogin.setIcon(btn_blue_ovr);
		}
	});
    btnLogin.addActionListener(new ActionListener() 
    {
    	public void actionPerformed(ActionEvent e) {
		new Thread(new Runnable() {
	      public void run() {

	        if ( (field.getText().length() > 0)
	            && (fieldPass.getPassword().length > 0)) {
//	          settings.setLogin(field.getText());
//	          settings.setPassword(new String(fieldPass.getPassword()));
//	          settings.saveSettings();
	          credentialManager.saveAuthInfo(
	        		  	chbRemLogin.isSelected()?field.getText():"",
	        		  	chbRemPwd.isSelected()?fieldPass.getText():"");
              

	          setStatusText(bundle.getString("connecting"));
	          try {
	            lobbyFrameInterface.login(
	                field.getText(),
	                fieldPass.getPassword());
	            setStatusText(bundle.getString("loading.tables"));
	            stopPTThread();
	            //this.setModal(false);
	            LoginFrame.this.setVisible(false);
	            LoginFrame.this.parent.setVisible(true);
	            LoginFrame.this.parent.setEnabled(true);
	            //this.parent.requestFocus();
	            //this.dispose();
	          }
	          catch (AuthenticationException aex) {
	            setStatusText(aex.getMessage());
	          }
	          catch (Exception ex) {
	            String message = ex.getMessage();
	            if (message == null || "".equals(message)) {
	              setStatusText(bundle.getString("fail.access"));
	              ex.printStackTrace();
	            }
	            else {
	              setStatusText(ex.getMessage());
	            }
	          }
	        }
	        else {
	          setStatusText(bundle.getString("empty.login"));
	          Toolkit.getDefaultToolkit().beep();
	        }
	      }
	    }).start();
		}
	});
  
    //- begin codec's code --------------------------------------------------------------------
    chbRemLogin = new JCheckBox();
    chbRemLogin.setBounds(62, 384, 21, 21);
    getContentPane().add(chbRemLogin);
    
    chbRemPwd = new JCheckBox();
    chbRemPwd.setBounds(62, 409, 21, 21);
    getContentPane().add(chbRemPwd);
    
    labelRemmPass = new JLabel(bundle.getString("remmpass"));
    labelRemmPass.setForeground(new Color(0x6B696C));
    labelRemmPass.setFont(new Font("Myriad Web", Font.PLAIN, 11));
    
    labelRemmLogin = new JLabel(bundle.getString("remmlogin"));
    labelRemmLogin.setForeground(new Color(0x6B696C));
    labelRemmLogin.setFont(new Font("Myriad Web", Font.PLAIN, 11));
    
    setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
    setTitle(title);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize =
        new Dimension(ClientConfig.DEFAULT_LOGIN_SCREEN_SIZE_X,
                      ClientConfig.DEFAULT_LOGIN_SCREEN_SIZE_Y);
//    setBounds(new Rectangle( (screenSize.width - frameSize.width) / 2,
//                            (screenSize.height - frameSize.height) / 2,
//                            ClientConfig.DEFAULT_LOGIN_SCREEN_SIZE_X,
//                            ClientConfig.DEFAULT_LOGIN_SCREEN_SIZE_Y));
    setBounds(new Rectangle( 0,
            0,
            ClientConfig.DEFAULT_LOGIN_SCREEN_SIZE_X,
            ClientConfig.DEFAULT_LOGIN_SCREEN_SIZE_Y));
    setResizable(false);

    field = new JTextField("username",12);
//    field.setFont(new Font("Myriad Web", Font.PLAIN, 11));
//    field.putClientProperty("Synthetica.opaque", Boolean.FALSE);
    //field.setBackground(new Color(0xFFFFFF));
    //field.setMargin(new Insets(0, 0, 0, 0));
    field.addFocusListener(new FocusAdapter() {
		
		@Override
		public void focusGained(FocusEvent arg0) {
			// TODO Auto-generated method stub
			//field.setText("");
			//status.setText("");
		}
		
	});
    //mouseListener added by rk
    field.addMouseListener(new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			field.setText("");
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	});
		
		
	
    
    field.addActionListener(new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			btnLogin.doClick();
		}
	});
    getContentPane().add(field);

    fieldPass = new JPasswordField("password",20);
    fieldPass.setFont(new Font("Myriad Web", Font.PLAIN, 11));
	//    fieldPass.setBackground(new Color(0xFFFFFF));
    //fieldPass.setMargin(new Insets(0, 0, 0, 0));
    fieldPass.addFocusListener(new FocusAdapter() {
		
		@Override
		public void focusGained(FocusEvent arg0) {
			// TODO Auto-generated method stub
			fieldPass.setText("");
		}
		
		
	});
    fieldPass.addActionListener(new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			btnLogin.doClick();
		}
	});
    getContentPane().add(fieldPass);

    status = new JLabel();
    status.setForeground(Color.RED);
    getContentPane().add(status);
    status.setBounds(70, 230, 200, 20);

    
    btnRegister = createButton("<html><font face=\"Myriad Web\" color=#ffffFF>Registration</font></html>",btn_black);
    btnRegister.addMouseListener(new MouseAdapter() {
 		
 		@Override
 		public void mouseExited(MouseEvent arg0) {
 			// TODO Auto-generated method stub
 			btnRegister.setIcon(btn_black);
 		}
 		
 		@Override
 		public void mouseEntered(MouseEvent arg0) {
 			// TODO Auto-generated method stub
 			btnRegister.setIcon(btn_black_ovr);
 		}
 		
 		@Override
 		public void mouseClicked(MouseEvent arg0) {
 			// TODO Auto-generated method stub
 			//new RegisterFrame(parent,bundle.getString("create.new.account"));
 			BrowserLaunch.openURL(bundle.getString("register.url"));
 		}
 	});
     btnRegister.setLocation(21, 463);
      getContentPane().add(btnRegister);
      
      btnObserve = createButton("<html><font face=\"Myriad Web\" color=#ffffFF>Observe Game</font></html>",btn_black);
      btnObserve.addMouseListener(new MouseAdapter() {
   		
   		@Override
   		public void mouseExited(MouseEvent arg0) {
   			// TODO Auto-generated method stub
   			btnObserve.setIcon(btn_black);
   		}
   		
   		@Override
   		public void mouseEntered(MouseEvent arg0) {
   			// TODO Auto-generated method stub
   			btnObserve.setIcon(btn_black_ovr);
   		}
   		
   		@Override
   		public void mouseClicked(MouseEvent arg0) {
   			// TODO Auto-generated method stub
   			try {
				_serverProxy.dummyLogin();
				lobbyFrameInterface.dummyLogin();
				LoginFrame.this.setVisible(false);
	            LoginFrame.this.parent.setVisible(true);
	            LoginFrame.this.parent.setEnabled(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		}
   	});
      
    btnObserve.setLocation(21, 524);
    getContentPane().add(btnObserve);
    
    getContentPane().setLayout(null);
    ( (JPanel) getContentPane()).setOpaque(false);

    labelLoginCaption.setFont(new Font("Myriad Web", Font.PLAIN, 21));
    labelLoginCaption.setBounds(52, 205, 60, 30);
	labelPlayersOnline.setBounds(19, 135, 150, 20); 
	labelPlayersOnlineCount.setBounds(22, 155, 150, 30); 
	labelActiveTables.setBounds(142, 135, 150, 20); 
	labelActiveTablesCount.setBounds(145, 155, 150, 30); 
	labelForgotPassword.setBounds(153, 307, 150, 20);
	labelDownload.setBounds(570, 140, 250, 20);
	labelExistingReg.setBounds(571, 157, 250, 20);

	labelRemmPass.setBounds(86, 413, 130, 14);
	labelRemmLogin.setBounds(86, 388, 130, 14);

	labelComingSoon1.setBounds(280, 462, 150, 20);
	labelComingSoon2.setBounds(489, 462, 150, 20);
	labelComingSoon3.setBounds(600, 462, 150, 20);
	labelComingSoon4.setBounds(702, 462, 150, 20);
	btnLogin.setBounds(64, 330, 165, 38);
	field.setBounds(70, 253, 150, 23);
	fieldPass.setBounds(70, 283, 150, 23);
    getContentPane().add(labelPlayersOnline);
    getContentPane().add(labelLoginCaption);
    getContentPane().add(labelActiveTables);
    getContentPane().add(labelPlayersOnlineCount);
    getContentPane().add(labelActiveTablesCount);
    getContentPane().add(labelForgotPassword);
    getContentPane().add(labelDownload);
    getContentPane().add(labelExistingReg);
    getContentPane().add(labelComingSoon1);
    getContentPane().add(labelComingSoon2);
    getContentPane().add(labelComingSoon3);
    getContentPane().add(labelComingSoon4);
    getContentPane().add(btnLogin);
    getContentPane().add(labelRemmPass);
    getContentPane().add(labelRemmLogin);

    
    labelForgotPassword.addMouseListener(new MouseAdapter() {
		
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			labelForgotPassword.setForeground(new Color(0x6B696C));
		}
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			labelForgotPassword.setForeground(new Color(0xFFFFFF));
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			//new ForgotPasswordFrame(parent,bundle.getString("forgot.password"));
			BrowserLaunch.openURL(bundle.getString("forgotpassword.url"));
		}
	});
            
          
    //- begin codec's code --------------------------------------------------------------------
    //if (isRememberPassword) {
      

//      imageRemmPass.addMouseListener(new MouseAdapter() {
//    	  
//    	public void mouseClicked(MouseEvent e) {
//          if(isRemPass)
//    	  {
//        	  imageRemmPass.setIcon(iconUnchecked);
//        	  isRemPass = false;
//    	  }
//          else 
//    	  {
//        	  imageRemmPass.setIcon(iconChecked);
//        	  isRemPass = true;
//    	  }
//        }
//      });
//      
//       
//        imageRemmLogin.addMouseListener(new MouseAdapter() {
//        	public void mouseClicked(MouseEvent e) {
//        	  if(isRemLogin)
//          	  {
//              	  imageRemmLogin.setIcon(iconUnchecked);
//              	  isRemLogin = false;
//          	  }
//                else 
//          	  {
//                  imageRemmLogin.setIcon(iconChecked);
//              	  isRemLogin = true;
//          	  }
//          }
//        });
        

    field.setOpaque(false);
    fieldPass.setOpaque(false);

    final ImageIcon m_image =
        Utils.getIcon(ClientConfig.IMG_LOGIN_BACKGROUND);
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
          //for (int i = 0; i < h + hinc; i = i + hinc) {
          //  for (int j = 0; j < w + winc; j = j + winc) {
              m_image.paintIcon(this, g, 0, 27);
          //  }
          //}
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

//    //----------- Reading login settings ---------------
    String av[] = credentialManager.getAuthInfo();
    if(av != null && av[0].length() > 0)
	{
    	field.setText(av[0]);
    	chbRemLogin.setSelected(true);
	}
    else
    {
    	chbRemLogin.setSelected(false);
    }
    if(av != null && av[1].length() > 0)
	{
    	fieldPass.setText(av[1]);
    	chbRemPwd.setSelected(true);
	}
    else
    {
    	chbRemPwd.setSelected(false);
    }

    setVisible(true);
  }

  public void setStatusText(String s) {
    status.setText("<html>" + s + "</html>");
  }

  private JButton createButton(String str, ImageIcon icon1) {
    JButton button = new JButton(str,icon1);
    int w = icon1.getIconWidth();
    int h = icon1.getIconHeight();

    //button.setPressedIcon(icon3);

    //button.setRolloverEnabled(true);
    //button.setRolloverIcon(icon2);

    //button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.setVerticalTextPosition(AbstractButton.CENTER);
    button.setHorizontalTextPosition(AbstractButton.CENTER);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setMargin(new Insets(0, 0, 0, 0));
    button.setSize(w, h);
    button.setFont(new Font("Myriad Web", Font.PLAIN, 18));
    return button;
  }

	@Override
  public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
  }
	
  public void startPTThread() {
    // start a heartbeat timer thread
    TLThread pt = new TLThread();
     t = new Timer();
    t.schedule(pt, 0, PING_INTERVAL);
  }
  
  public void stopPTThread() {
    // start a heartbeat timer thread
    t.cancel();
    t = null;
  }
  
  public class TLThread extends TimerTask {

      public void run() {
        try {
        	_serverProxy.pingBlocking();
        }
        catch (Exception ex) {
          //do nothing
          ex.printStackTrace();
        }
      }
    }
  public void pingDetailsReceived(long time, int at, int ap) {
	  if(labelPlayersOnlineCount == null)return;
	  labelPlayersOnlineCount.setText(String.format("%06d", ap));
	  labelActiveTablesCount.setText(String.format("%05d", at));
  }
}
