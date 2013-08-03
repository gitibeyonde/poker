package com.onlinepoker.skin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.golconda.game.util.Card;
import com.onlinepoker.ClientConfig;
import com.onlinepoker.ClientPlayerModel;
import com.onlinepoker.Utils;

public class SkinTester extends JFrame  {
    /** Skin of this room */
    protected RoomSkin _skin = null;
    int _rs;

    /** _background */
    protected ImageIcon _board;

    public SkinTester(int s) {
        _rs = s;
        _skin = RoomSkinFactory.getRoomSkin(_rs);
        _board = _skin.getBoard();
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
       // Thread t = new Thread(this);
       // t.start();
    }

    int counter = 0;
    
    //resize code
    double _width,_height;
	double ratio_x=1, ratio_y=1;

      /** Constants */public static final int CYCLE_COUNT = 40;
      public static final int CARD_CLOSE_WIDTH = 30;
      public static final int CARD_CLOSE_HEIGHT = 50;
      public static final int CARD_OPEN_WIDTH = 54;
      public static final int CARD_OPEN_HEIGHT = 78;
      public static final int CARD_EXPOSED_WIDTH = 35;
      public static final int CARD_GAP = 0;


    public void paint(Graphics g) {
        _skin.getBoard().paintIcon(this, g, -counter, 0);
        _board.paintIcon(this, g, 0, 0);
        for (int i=0;i<_rs;i++){
            Point p = _skin.getPlayerPlace(i, ClientPlayerModel.MALE);
            //_skin.getPlayersSkin(i, ClientPlayerModel.MALE).paintIcon(this, g, p.x, p.y);
            //_skin.getPlayersSkin(ClientPlayerModel.getA);
            Utils.getIcon("images/avatars/1.png").paintIcon(this, g, p.x, p.y);
            
            Point playerCardsPlaceClose = 
                    new Point(_skin.getPlayerCardsPlaceClose(i, ClientPlayerModel.MALE));
            System.out.println("For Position " + i + " cards x=" + playerCardsPlaceClose.x + " y=" + playerCardsPlaceClose.y);
            ImageIcon closeCard = _skin.getCloseCard();
            Point playerCardsPlaceOpen = 
                    new Point(_skin.getPlayerCardsPlaceOpen(i, ClientPlayerModel.MALE));
            //ImageIcon openCard = _skin.getOpenCards();
           
            
            p = _skin.getDealerPos(i);
            ImageIcon di = _skin.getDealerIcon();
            di.paintIcon(this, g, p.x - 10, p.y - 10);  
            System.out.println("For Position " + i + " , " + p.x + ", " + p.y);

            p=_skin.getNamePos(i, ClientPlayerModel.MALE);
            _skin.getNamePlate().paintIcon(this, g, p.x, p.y);
            
            p = _skin.getPlayersBublesCoords(i, ClientPlayerModel.MALE);
            int or = _skin.getBublesOrientation(i);
            Graphics gcopy = g.create(p.x, p.y, 80, 44);
            //_skin.getBublesIcon().paintIcon(this, gcopy, -80 * or, 0);
            gcopy.setColor(Color.black);
            //gcopy.setFont(Utils.bubbleFont);
           // gcopy.drawString("hello" + i, 12, 21);

            Card card = new Card(20,false);
            gcopy = g.create(playerCardsPlaceOpen.x, playerCardsPlaceOpen.y, CARD_OPEN_WIDTH,
                                      CARD_OPEN_HEIGHT);
//            openCard.paintIcon(this, gcopy, -CARD_OPEN_WIDTH * getRank(card.getRank()),
//                               -CARD_OPEN_HEIGHT * getSuit(card.getSuit()));
            gcopy.dispose();            
            
            card = new Card(70,false);
            gcopy = g.create(playerCardsPlaceOpen.x, playerCardsPlaceOpen.y, CARD_OPEN_WIDTH *2,
                                      CARD_OPEN_HEIGHT);
//            openCard.paintIcon(this, gcopy, -CARD_OPEN_WIDTH * getRank(card.getRank()),
//                               -CARD_OPEN_HEIGHT * getSuit(card.getSuit()));
            gcopy.dispose();
            
            
            gcopy = g.create(playerCardsPlaceClose.x, playerCardsPlaceClose.y, CARD_CLOSE_WIDTH,
                                      CARD_CLOSE_HEIGHT);
            closeCard.paintIcon(this, gcopy, 0,0);
            gcopy.dispose();            
            
            gcopy = g.create(playerCardsPlaceClose.x, playerCardsPlaceClose.y, CARD_CLOSE_WIDTH *2,
                                      CARD_CLOSE_HEIGHT);
            closeCard.paintIcon(this, gcopy, 0,0);
            
            
            Utils.getIcon("images/slider_bg.png").paintIcon(this, g, 625, 450);
            p = _skin.getChipsPlace(i, ClientPlayerModel.MALE);           
            gcopy = g.create(p.x, p.y, 20, 20);
            _skin.getChips().paintIcon(this, gcopy, 0, 0);
            gcopy.dispose();
       }
    }


      public int getRank(int rank) {
        if (rank >= 0 && rank <= 8) {
          return 8 - rank;
        }
        if (rank >= 9 && rank <= 12) {
          return 12 - rank + 9;
        }
        return -1;
      }


      public int getSuit(int suit) {
        switch (suit) {
          case 1:
            return 3;
          case 2:
            return 1;
          case 3:
            return 2;
        }
        return suit;
      }


    private void jbInit() throws Exception {
        setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
        Dimension screenSize;
        Dimension frameSize;
        Point framePos;

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frameSize = new Dimension(795 + 1, 560 + 14 + 1);
        framePos =
            new Point(
            (screenSize.width - frameSize.width) / 2,
            (screenSize.height - frameSize.height) / 2);

        setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
        setResizable(true);
        setVisible(true);
        
        //resize code
        this.addMouseMotionListener(new MouseMotionAdapter() {
        	public void mouseDragged(MouseEvent me){
        		
        	}
		});
        this.addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent me){
        		
        	}
		});
        this.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener(){

			@Override
			public void ancestorMoved(HierarchyEvent e) {
				//System.out.println(e);				
			}
			@Override
			public void ancestorResized(HierarchyEvent e) {
//				Component c = (Component)e.getSource();
//				Dimension d = c.getSize();
				
				if(isResizable())
		    	{
					 _height = getHeight();
		    		 _width = getWidth();
		    		 
		    		 ratio_x = _width/800.00;
		    		 ratio_y = _height/600.00;
		    		 
		    		 _skin.setResizeRatio(ratio_x,ratio_y);
		    		 
		    		System.out.println("_height:"+_height+",_width:"+_width);
		    		//repaint();
		    	}
			}			
		});

        this.addComponentListener(new java.awt.event.ComponentAdapter() 
		{
			public void componentResized(ComponentEvent e)
			{
				
			}
		});
    }
    
    public void setSize (int width, int height)
    {
    	
    }

    public static void main(String[] args) {
        SkinTester ra = new SkinTester(9);
    }


}
