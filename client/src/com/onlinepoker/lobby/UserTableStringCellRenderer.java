package com.onlinepoker.lobby;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;
import com.onlinepoker.resources.Bundle;

/** Cell renderer for user table at lobby
 * @author Halt
 */
public class UserTableStringCellRenderer
    extends DefaultTableCellRenderer {

  private ResourceBundle bundle = Bundle.getBundle();

  public UserTableStringCellRenderer() {
    super();

  }

  
  
  Color selectedBlueColor = new Color(15, 117, 240);  
  Color selectedRedColor = new Color(189,0,0);
  private JLabel jlHand; 
  private JLabel jlStack; 
  private JLabel jlText;
  private JPanel col2_panel; 
  
  
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus, int row,
                                                 int column) {
	 	  

	  super.getTableCellRendererComponent(table, value,  
	  isSelected, hasFocus, row, column);  
	  
	  Component cell = super.getTableCellRendererComponent(
              table, value, isSelected, hasFocus, row, column);
	  String str;
		try {
			str = value.toString();
		} catch (Exception e) {
			return null;
		}
	  
//	  col2_panel = new JPanel();
//	  col2_panel.setLayout(new BoxLayout(col2_panel,BoxLayout.X_AXIS));
	  
	  if (isSelected) 
	  {
	      if (table.getValueAt(row, 2).equals("  " + bundle.getString("points"))) {
	        return new boldLabelSel(value.toString());
	      }
	      if(value.toString().indexOf("-pro:true") > 0)
	      {
			  //if(str.indexOf("-pro:true") > 0)
			  	return new simplRedLabelSel(str.substring(0,str.indexOf("-pro:")));
			  //else return new simplLabelSel(str.substring(0,str.indexOf("-pro:")));
		  }
	      else if(column == 2 && value.toString().startsWith("2")){
		   	return new simplIconLabelSel(value == null ? "" : value.toString().substring(1,str.indexOf("-pro:")),Utils.getIcon(ClientConfig.ICON_2HANDED));
		  }
		  else if(column == 2 && value.toString().startsWith("6")){
		   	return new simplIconLabelSel(value == null ? "" : value.toString().substring(1,str.indexOf("-pro:")),Utils.getIcon(ClientConfig.ICON_6HANDED));
		  }
//	      else if(column == 2)
//	      {
//	      	if( value.toString().startsWith("2")){
//	        		jlHand = new JLabel(Utils.getIcon(ClientConfig.ICON_2HANDED));
//	        	}
//	        	else if(value.toString().startsWith("6")){
//	        		jlHand = new JLabel(Utils.getIcon(ClientConfig.ICON_6HANDED));
//	        	}
//	        	else jlHand = new JLabel("   ");
//	      	if( value.toString().contains("deep")){
//	        		jlStack = new JLabel(Utils.getIcon(ClientConfig.ICON_DEEPSTACK));
//	        	}
//	        	else if(value.toString().contains("shallow")){
//	        		jlStack = new JLabel(Utils.getIcon(ClientConfig.ICON_SPEED));
//	        	}
//	        	else jlStack = new JLabel("   ");
//	        	jlText = new JLabel(value.toString().substring(1,str.indexOf("-pro:")));
//	        	col2_panel.add(jlHand);
//	        	col2_panel.add(jlStack);
//	        	col2_panel.add(jlText);
//	        	return new simplPanelSel(col2_panel);
//	      }
	      else {
	        return new simplLabelSel(str.substring(0,str.indexOf("-pro:") != -1?str.indexOf("-pro:"):str.length()));
	      }
	  }
	  Object o = table.getValueAt(row, 2);
	  if (o != null && o.equals("  " + bundle.getString("points"))) 
	  {
	    return new boldLabel(value.toString());
	  }
	  if(value.toString().indexOf("-pro:true") > 0)
	  {
		  //if(str.indexOf("-pro:true") > 0)
		  	return new simplRedLabel(str.substring(0,str.indexOf("-pro:")));
		  //else return new simplLabel(str.substring(0,str.indexOf("-pro:")));
	  }
	  else if(column == 2 && value.toString().startsWith("2")){
    	return new simplIconLabel(value == null ? "" : value.toString().substring(1,str.indexOf("-pro:")),Utils.getIcon(ClientConfig.ICON_2HANDED));
	  }
	  else if(column == 2 && value.toString().startsWith("6")){
    	return new simplIconLabel(value == null ? "" : value.toString().substring(1,str.indexOf("-pro:")),Utils.getIcon(ClientConfig.ICON_6HANDED));
	  }
    
//    else if(column == 2)
//    {
//    	if( value.toString().startsWith("2")){
//      		jlHand = new JLabel(Utils.getIcon(ClientConfig.ICON_2HANDED));
//      	}
//      	else if(value.toString().startsWith("6")){
//      		jlHand = new JLabel(Utils.getIcon(ClientConfig.ICON_6HANDED));
//      	}
//      	else jlHand = new JLabel("   ");
//    	if( value.toString().contains("deep")){
//      		jlStack = new JLabel(Utils.getIcon(ClientConfig.ICON_DEEPSTACK));
//      	}
//      	else if(value.toString().contains("shallow")){
//      		jlStack = new JLabel(Utils.getIcon(ClientConfig.ICON_SPEED));
//      	}
//      	else jlStack = new JLabel("   ");
//      	jlText = new JLabel(value.toString().substring(1,str.indexOf("-pro:")));
//      	col2_panel.add(jlHand);
//      	col2_panel.add(jlStack);
//      	col2_panel.add(jlText);
//      	return new simplPanel(col2_panel);
//    }
    else return new simplLabel(value == null ? "" : str.substring(0,str.indexOf("-pro:") != -1?str.indexOf("-pro:"):str.length()));
    
  }

  protected class simplLabelSel
      extends JLabel {
    simplLabelSel(Object title) {
      super(title.toString());
      setFont(new Font("Verdana", Font.PLAIN, 9));
      setForeground(Color.WHITE);
      setHorizontalAlignment(SwingConstants.LEFT);

    }

    public void paint(Graphics g) {
      g.setColor(selectedBlueColor);
      g.fillRect(0, 0, getWidth(), getHeight());
      super.paint(g);
    }
  }
  
  protected class simplRedLabelSel
	  extends JLabel {
	simplRedLabelSel(Object title) {
	  super(title.toString());
	  setFont(new Font("Verdana", Font.PLAIN, 9));
	  setForeground(Color.WHITE);
	  setHorizontalAlignment(SwingConstants.LEFT);
	}
	
	public void paint(Graphics g) {
	  g.setColor(selectedRedColor);
	  g.fillRect(0, 0, getWidth(), getHeight());
	  super.paint(g);
	}
	}
  
  protected class simplPanelSel
	  extends JPanel {
	simplPanelSel(JPanel p) {
	  super();
	  setFont(new Font("Verdana", Font.PLAIN, 9));
	  setForeground(Color.WHITE);
	  setHorizontalAlignment(SwingConstants.LEFT);
	
	}
	
	public void paint(Graphics g) {
	  g.setColor(selectedBlueColor);
	  g.fillRect(0, 0, getWidth(), getHeight());
	  super.paint(g);
	}
	}

  protected class boldLabelSel
      extends JLabel {
    boldLabelSel(String title) {
      super(title);
      setFont(Utils.boldFont);
      setForeground(Color.WHITE);
      //setHorizontalAlignment(SwingConstants.LEFT);
    }

    public void paint(Graphics g) {
      g.setColor(selectedBlueColor);
      g.fillRect(0, 0, getWidth(), getHeight());
      super.paint(g);
    }
  }

  protected class simplIconLabel
      extends JLabel {
    simplIconLabel(String title, ImageIcon icon) {
      super(title);
      setIconTextGap(5);
      setIcon(icon);
      setFont(new Font("Verdana", Font.PLAIN, 9));
      setForeground(Color.BLACK);
      setHorizontalAlignment(SwingConstants.LEFT);
    }
    
  }
  
  protected class simplIconLabelSel
	  extends JLabel {
	  simplIconLabelSel(String title, ImageIcon icon) {
	  super(title);
	  setIconTextGap(5);
      setFont(new Font("Verdana", Font.PLAIN, 9));
	  setIcon(icon);
      setForeground(Color.WHITE);
	  setHorizontalAlignment(SwingConstants.LEFT);
	
	}
	
	public void paint(Graphics g) {
	  g.setColor(selectedBlueColor);
	  g.fillRect(0, 0, getWidth(), getHeight());
	  super.paint(g);
	}
	}
  
  
  protected class simplLabel
  extends JLabel {
	simplLabel(Object title) {
	  super(title.toString());
	  setFont(new Font("Verdana", Font.PLAIN, 9));
	  setForeground(Color.BLACK);
	  setHorizontalAlignment(SwingConstants.LEFT);
	}
	}
  
  protected class simplPanel
  extends JPanel {
	simplPanel(JPanel p) {
	  super();
	  setFont(new Font("Verdana", Font.PLAIN, 9));
	  setForeground(Color.BLACK);
	  setHorizontalAlignment(SwingConstants.LEFT);
	}
	}
  
  
  protected class simplRedLabel
  extends JLabel {
	simplRedLabel(Object title) {
	  super(title.toString());
	  setFont(new Font("Verdana", Font.PLAIN, 9));
	  setForeground(selectedRedColor);
	  setHorizontalAlignment(SwingConstants.LEFT);
	}
	}

  protected class boldLabel
      extends JLabel {
    boldLabel(String title) {
      super(title);
      setFont(Utils.boldFont);
      setForeground(Color.BLACK);
      //setHorizontalAlignment(SwingConstants.LEFT);
    }
  }

}
