package com.onlinepoker.lobby;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.onlinepoker.ClientConfig;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;


/** Main lobby frame.
 *  Contains all JTables with all game tables.
 */
public class LobbyFrame
    extends JFrame {


  public LobbyFrame(LobbyFrameInterface lobbyInterface) {
    super();

// ---  Init ---
    JPanel pane = (JPanel) getContentPane();
    pane.setLayout(null);
    pane.setOpaque(false);
//		---  Init ---

//		---  Create environment ---
    setLookAndFeel(lobbyInterface.getLookAndFeel());
    createBackground(lobbyInterface.getBackground());
    createExitEvent(lobbyInterface.getWindowCloseEvent());
    createPosAndSize();
    setJMenuBar(lobbyInterface.getLobbyMenu());
//		---  Create environment ---

//		---  Create components ---
    Object[] component = lobbyInterface.getComponents();
    for (int i = 0; i < component.length; i++) {
      pane.add( (JComponent) component[i]);
//		---  Create components ---

    }
    lobbyInterface.init(this);
  }

  private void setLookAndFeel(SyntheticaBlackEyeLookAndFeel theme) {
    try {
    	
    	UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
    	UIManager.put("Synthetica.window.decoration", Boolean.FALSE);
    	
    	
//      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      }
    catch (Exception ex) {
      ex.printStackTrace();
      
    }
  }

  private void createBackground(ImageIcon background) {
    final ImageIcon m_image = background;
    final int winc = m_image.getIconWidth();
    final int hinc = m_image.getIconHeight();
    JLabel backlabel = new JLabel("");
    if (m_image.getIconWidth() > 0 && m_image.getIconHeight() > 0) {
      backlabel = new JLabel() {
        public void paintComponent(Graphics g) {
          int w = getParent().getWidth();
          int h = getParent().getHeight();
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
    backlabel.setBounds(0, 27, 5000, 5000);
  }

  private void createPosAndSize() {
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = new Dimension(ClientConfig.
                                        DEFAULT_LOBBY_SCREEN_SIZE_X,
                                        ClientConfig.
                                        DEFAULT_LOBBY_SCREEN_SIZE_Y);
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
//    setBounds(
//        new Rectangle(
//        (screenSize.width - frameSize.width) / 2,
//        (screenSize.height - frameSize.height) / 2,
//        ClientConfig.DEFAULT_LOBBY_SCREEN_SIZE_X,
//        ClientConfig.DEFAULT_LOBBY_SCREEN_SIZE_Y));
    setBounds(new Rectangle( 0,
            0,
            ClientConfig.DEFAULT_LOBBY_SCREEN_SIZE_X,
          ClientConfig.DEFAULT_LOBBY_SCREEN_SIZE_Y));
    setResizable(false);
  }

  private void createExitEvent(WindowListener listener) {
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(listener);
  }
}
