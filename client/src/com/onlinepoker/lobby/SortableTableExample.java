package com.onlinepoker.lobby;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
  
/**
 * @version 1.0 02/25/99
 */
public class SortableTableExample extends JPanel {
 
  public SortableTableExample(){
    setLayout(new BorderLayout());
    String[] headerStr = {"Name","Date","Size","Dir"};
    int[] columnWidth = {100,150,100,50};
    
    SortableTableModel dm = new SortableTableModel() {
      public Class getColumnClass(int col) {
        switch (col) {
          case  0: return String.class;
          case  1: return Date.class;
          case  2: return Integer.class;
          case  3: return Boolean.class;
          default: return Object.class;
        }
      }
      public boolean isCellEditable(int row, int col) {
        switch (col) {
          case  1: return false;
          default: return true;
        }
      }  
      public void setValueAt(Object obj, int row, int col) {
        switch (col) {
          case  2: super.setValueAt(new Integer(obj.toString()), row, col); return;
          default: super.setValueAt(obj, row, col); return;
        }
      }
    };
    dm.setDataVector(new Object[][]{
      {"b"   ,getDate("98/12/02"),new Integer(14),new Boolean(false)},
      {"a"   ,getDate("99/01/01"),new Integer(67),new Boolean(false)},
      {"d"   ,getDate("99/02/11"),new Integer(2) ,new Boolean(false)},
      {"c"   ,getDate("99/02/27"),new Integer(7) ,new Boolean(false)},
      {"foo" ,new Date()         ,new Integer(5) ,new Boolean(true)},
      {"bar" ,new Date()         ,new Integer(10),new Boolean(true)}},
      headerStr);
     
    JTable table = new JTable(dm);
    //table.setShowGrid(false);
    table.setShowVerticalLines(true);
    table.setShowHorizontalLines(false);
    SortButtonRenderer renderer = new SortButtonRenderer();
    TableColumnModel model = table.getColumnModel();
    int n = headerStr.length;
    for (int i=0;i<n;i++) {
      model.getColumn(i).setHeaderRenderer(renderer);
      model.getColumn(i).setPreferredWidth(columnWidth[i]);
    }
    
    JTableHeader header = table.getTableHeader();
    header.addMouseListener(new HeaderListener(header,renderer));
    JScrollPane pane = new JScrollPane(table);
    add(pane, BorderLayout.CENTER);
  }
 
  public static void main(String[] args) {
    JFrame f= new JFrame("SortableTable Example");
    f.getContentPane().add(new SortableTableExample(), BorderLayout.CENTER);
    f.setSize(400, 160);
    f.setVisible(true);
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {System.exit(0);}
    });
  }
  
  private static DateFormat dateFormat = 
    DateFormat.getDateInstance(DateFormat.SHORT, Locale.JAPAN);
 
  private static Date getDate(String dateString) {
    Date date = null;
    try {
      date = dateFormat.parse(dateString);
    } catch(ParseException ex) {
      date = new Date();
    }
    return date;
  }
 
  class HeaderListener extends MouseAdapter {
    JTableHeader   header;
    SortButtonRenderer renderer;
  
    HeaderListener(JTableHeader header,SortButtonRenderer renderer) {
      this.header   = header;
      this.renderer = renderer;
    }
  
    public void mousePressed(MouseEvent e) {
      int col = header.columnAtPoint(e.getPoint());
      int sortCol = header.getTable().convertColumnIndexToModel(col);
      renderer.setPressedColumn(col);
      renderer.setSelectedColumn(col);
      header.repaint();
      
      if (header.getTable().isEditing()) {
        header.getTable().getCellEditor().stopCellEditing();
      }
      
      boolean isAscent;
      if (SortButtonRenderer.DOWN == renderer.getState(col)) {
        isAscent = true;
      } else {
        isAscent = false;
      }
      ((SortableTableModel)header.getTable().getModel())
        .sortByColumn(sortCol, isAscent);    
    }
  
    public void mouseReleased(MouseEvent e) {
      int col = header.columnAtPoint(e.getPoint());
      renderer.setPressedColumn(-1);                // clear
      header.repaint();
    }
  }
}
