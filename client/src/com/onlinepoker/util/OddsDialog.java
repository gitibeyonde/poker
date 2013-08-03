package com.onlinepoker.util;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;

import com.agneya.util.Utils;
import com.onlinepoker.ClientPlayerController;
import com.onlinepoker.ClientPlayerModel;
import com.poker.game.util.Hand;
import com.poker.game.util.HandOddCalculator;
import com.poker.game.util.OddsPlayer;
import com.poker.game.util.PokerOdds;

public class OddsDialog extends JDialog {
    private JEditorPane jEditorPane1= new JEditorPane("text/html", initText());
    private JButton okButton = new JButton();
    OddsPlayer[] _podds;
    HandOddCalculator _hoc;
    long _cards=0;
    long _cc=0;
    long _pc=0;

    public OddsDialog(long cc, ClientPlayerController[] pm, ClientPlayerModel me) {
        this(null, "", false);
        _cc=cc;
        Vector pv = new Vector();
        for (int i=0;i<pm.length;i++){
            ClientPlayerController pd = pm[i];
            if (pd != null && pd.getPlayerPosition()==me.getPlayerPosition()){
                //mine player
                _pc = pd.getHand();
                pv.add(new OddsPlayer(_pc, pd.getPlayerName(), pd.getPlayerPosition(), true));
                System.out.println("My cards " + me.getCards().openStringValue());
            }
            else if (pd != null){
                pv.add(new OddsPlayer(0, pd.getPlayerName(), pd.getPlayerPosition(), false));
            }
        }
        _cards = _pc|_cc;
        PokerOdds podd = new PokerOdds();
        _podds =(OddsPlayer[])pv.toArray(new OddsPlayer[pv.size()]);
        podd.calcProbability(_podds, cc, 1);
        
        _hoc = new HandOddCalculator();
        
        jEditorPane1.setText(initText());
        jEditorPane1.setEditable(false);
        
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public OddsDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setSize(new Dimension(505, 185));
        this.getContentPane().setLayout( null );
        jEditorPane1.setBounds(new Rectangle(0, 0, 500, 180));
        okButton.setText("OK");
        okButton.setBounds(new Rectangle(200, 120, 65, 30));
        okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        okButton_actionPerformed(e);
                    }
                });
        this.getContentPane().add(okButton, null);
        this.getContentPane().add(jEditorPane1, null);
    }
    
    
    public String initText(){
       if (_podds == null){
           StringBuilder sbuf = new StringBuilder();           
           sbuf.append("<html>Let the hand start !</html>");
           return sbuf.toString();
       }
        
    
    
       double mypercent=0;
       double otherper=0;
        for (int i=0; i<_podds.length;i++){
            if (_podds[i]._me){
                mypercent=_podds[i]._percent_win;
            }
            else {
                otherper += _podds[i]._percent_win;
            }
        }
        StringBuilder sbuf = new StringBuilder();
        
        
        sbuf.append("<html>");
        sbuf.append("<table border=\"0\" width=\"100%\" id=\"table1\" bgcolor=\"#C2D8DA\">\n" + 
        "	<tr>\n" + 
        "		<td width=\"155\">\n" + 
        "		<table  width=\"100%\" id=\"table2\" height=\"158\" style=\"border-collapse: collapse\">\n" + 
        "			<tr>\n" + 
        "				<td>\n" + 
        "					Hand:&nbsp;&nbsp;\n" + 
        "					" + new Hand(_pc).getAllCardsString() +"\n" + 
        "				<br/>\n" + 
        "					Comm.:&nbsp;\n" + 
        "					" + new Hand(_cc).getAllCardsString()  + 
        "				</td>\n" + 
        "			</tr>\n" + 
        "			<tr>\n" + 
        "				<td bgcolor=\"#00FF00\">You win: " + Utils.getRounded(mypercent) + "%</td>\n" + 
        "			</tr>\n" + 
        "			<tr>\n" + 
        "				<td bgcolor=\"#FF00FF\">Opponent win: " + Utils.getRounded(otherper) + "%</td>\n" + 
        "			</tr>\n" + 
        "		</table>\n" + 
        "		</td>\n" + 
        "		<td>\n" + 
        "		<table width=\"100%\" id=\"table3\" style=\"border-collapse: collapse\" bgcolor=\"#87B0B4\" height=\"158\">\n" + 
        "			<tr>\n" + 
        "				<td>Straight Flush: " + Utils.getRounded(_hoc.getHandOdds(_cards, HandOddCalculator.STRAIGHT_FLUSH) *100)+"%" +
        "                               <p>Four of a Kind: " + Utils.getRounded(_hoc.getHandOdds(_cards, HandOddCalculator.FOUR_OF_A_KIND) *100)+"%</p>\n" + 
        "				<p>Full House: " + Utils.getRounded(_hoc.getHandOdds(_cards, HandOddCalculator.FULLHOUSE)*100) +"%</p>\n" + 
        "				<p>Flush: " + Utils.getRounded(_hoc.getHandOdds(_cards, HandOddCalculator.FLUSH)*100) +"%</td>\n" + 
        "				<td>Straight: " + Utils.getRounded(_hoc.getHandOdds(_cards, HandOddCalculator.STRAIGHT)*100) +"%" +
        "                               <p>Three of a Kind: " + Utils.getRounded(_hoc.getHandOdds(_cards, HandOddCalculator.THREE_OF_A_KIND)*100) +"%</p>\n" + 
        "				<p>Two Pair: " + Utils.getRounded(_hoc.getHandOdds(_cards, HandOddCalculator.TWO_PAIR)*100) +"%</p>\n" + 
        "				<p>One Pair: " + Utils.getRounded(_hoc.getHandOdds(_cards, HandOddCalculator.ONE_PAIR)*100) +"%</td>\n" + 
        "			</tr>\n" + 
        "		</table>\n" + 
        "		</td>\n" + 
        "	</tr>\n" + 
        "</table>\n" + 
        "\n");
        sbuf.append("</html>");
        return sbuf.toString();
    }

    private void okButton_actionPerformed(ActionEvent e) {
        dispose();
    }
    
    public static void main(String args[]){
        //OddsDialog od = new OddsDialog(new GameEvent("max-bet=10,community-cards=4H'TS'AS,rake=0.00,player-details=0|408.28|0.00|jayanta|2322|0|`1|510.03|10.00|honda|2068|0|__'__`4|506.30|10.00|motor|16|0|__'__`6|1985.00|0.00|abhi|16779268|1|__'__`9|478.70|0.00|bogosip|17|0|__'__,msgGID=83560,dealer-pos=9,round=3,max-players=10,target-position=6,type=1,response-id=21,last-move=4|motor|call|10.0,hand=3D'7D,next-move=6|call|10.00`6|raise|20.00`6|fold|0.00,min-players=2,marker=1,partners=,name=Manhatten,grid=3546645,ab=2.50,pots=main|62.50,min-bet=5, "));
        //od.setVisible(true);
    }
}
