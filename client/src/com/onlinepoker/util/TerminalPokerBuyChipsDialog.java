package com.onlinepoker.util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import com.onlinepoker.ClientRoom;
import com.onlinepoker.Utils;
import com.onlinepoker.resources.Bundle;

public class TerminalPokerBuyChipsDialog extends JInternalFrame implements ActionListener
{
	protected static Dimension screenSize;
	protected static Dimension frameSize;
	protected static Point framePos;
	ImageIcon icon;
	int hinc = -1;
	int winc = -1;
	JButton bBuyin,bClose;
	public String reply = "";
	ResourceBundle bundle = Bundle.getBundle();
	//JWebBrowser webBrowser = null;
	
	public TerminalPokerBuyChipsDialog(JFrame frame, String title) {
		super();
				screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frameSize = new Dimension(670, 350);
			framePos = new Point((frame.getWidth() - frameSize.width)/2, (frame.getHeight() - frameSize.height)/2);
			setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
				
	Container pane = getContentPane();
	Frame frames[] = ClientRoom.getFrames();
	int j=-1;
	for (int i = 0; i < frames.length; i++) {
		//System.out.println(frames[i].getName()+"--"+frames[i].getTitle()+"--"+frames[i].getSize());
//		if(frames[i].getTitle().contains(gid)){j=i;break;}
	}
	if(j != -1)
	{
		//System.out.println("frame matched"+frames[j].getName());
		//this.setLocationRelativeTo(frames[j]);
	}
	//setModalityType(ModalityType.MODELESS);
	setTitle(title);
	//setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
	//setModal(true);
	icon = Utils.getIcon("images/dialog_plain.jpg");
	hinc = icon.getIconHeight();
	winc = icon.getIconWidth();
	this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	setClosable(true);
	this.addInternalFrameListener(new InternalFrameAdapter() {
	      public void internalFrameClosing(InternalFrameEvent e) {
	    	  try {
	    		  dispose();
	    		  setClosed(true);
				  reply = "closed";
				  
			} catch (PropertyVetoException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	      }
	    });
	JPanel panel = new JPanel()
	 {
		public void paintComponent(Graphics g) {
			int w = getWidth();
			int h = getHeight();
			for (int i = 0; i < h + hinc; i = i + hinc)
				for (int j = 0; j < w + winc; j = j + winc)
					icon.paintIcon(this, g, j, i);
		}
	};
	panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
	panel.setOpaque(false);
	panel.setPreferredSize(new Dimension(600, 700));
	panel.setSize(700, 700);
	panel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
	
	
	final JPanel panel1 = new JPanel(new BorderLayout());
	panel1.setBounds(0, 0, 700, 700);
//	String path = "D:data/poker/logs/player/";
//	//http://saintbet.supergameasia.com/s/handhistory?gid=Black%20Diamonds&grid=14763221&pid=nagaraju
//	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//	DocumentBuilder db;
//	Document dom;
//	try {
//		db = dbf.newDocumentBuilder();
//		dom = db.parse(path+gid+"/"+grid+".xml");
//	} catch (ParserConfigurationException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (SAXException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
	/*webBrowser = new JWebBrowser();//JWebBrowser.constrainVisibility());	
	webBrowser.setSize(700, 650);
	webBrowser.setBarsVisible(false);   
	  
	try {
          webBrowser.navigate("http://www.blueacepoker.com/s/handhistory?gid=" + gid + "&grid=" + grid + "&pid=" +  ServerProxy._name);
      } catch (Exception e) {
          System.out.println(e.getMessage());
          return;
      }
    panel1.add(webBrowser);*/
	
	final JEditorPane editorPane = new JEditorPane();
    editorPane.setContentType("text/html");
    editorPane.setEditable(false);
    editorPane.addHyperlinkListener(new HyperlinkListener() {
    	 public void hyperlinkUpdate(HyperlinkEvent event) {
    	        HyperlinkEvent.EventType eventType = event.getEventType();
    	        if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
    	            if (event instanceof HTMLFrameHyperlinkEvent) {
    	                HTMLFrameHyperlinkEvent linkEvent =
    	                        (HTMLFrameHyperlinkEvent) event;
    	                HTMLDocument document =
    	                        (HTMLDocument) editorPane.getDocument();
    	                document.processHTMLFrameHyperlinkEvent(linkEvent);
    	            } else {
    	            	try {
							editorPane.setPage(event.getURL());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    	            }
    	        }
    	    }});
    //String url_str = "http://www.blueacepoker.com/s/handhistory?gid=" + gid.replace(" ", "%20") + "&grid=" + grid + "&pid=";//if pid="" then player able to see opp player cards
//	try {
//		editorPane.setPage(url_str);
//	} 
//	catch (MalformedURLException e) {
//		editorPane.setText("<html><body>Handhistory is loading failed due to malformed URL<br>"+ url_str+" </body></html>");
//	} catch (IOException e) {
//		editorPane.setText("<html><body>Handhistory is loading failed due to file IO Exception<br>"+ url_str+" </body></html>");
//	}
	
	JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	scrollPane.setViewportView(editorPane);

	panel1.add(scrollPane);
	panel.add(panel1);
	pane.add(panel);
	//---------
	//setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
	setResizable(true);
	//---------
	reply = "opened";
	System.out.println(reply);
	}

	public void actionPerformed(ActionEvent e) {
		/*Object source = e.getSource();
		if (source == bClose)
		{
			reply = "close";
			//if(webBrowser != null)remove(webBrowser);
		}
		*/
		reply = "closed";
		dispose();
	}
	
  

	      
	      
	      
	   
	
}
