package com.onlinepoker.util.test;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
 
public class WindowTest
{
    JFrame frame;
    JDialog dialog1, dialog2;
 
    public WindowTest()
    {
        JFrame f = new JFrame();
        initWindows(f);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(getUIPanel(), "North");
        f.getContentPane().add(new WindowGraphicPanel());
        f.setSize(300,140);
        f.setLocation(200,200);
        f.setVisible(true);
    }
 
    private JPanel getUIPanel()
    {
        final JButton
            show1 = new JButton("show 1"),
            show2 = new JButton("show 2"),
            show3 = new JButton("show 3");
        ActionListener l = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JButton button = (JButton)e.getSource();
                if(button == show1)
                    dialog1.setVisible(true);
                if(button == show2)
                    dialog2.setVisible(true);
                if(button == show3)
                    frame.setVisible(true);
             }
         };
         show1.addActionListener(l);
         show2.addActionListener(l);
         show3.addActionListener(l);
         JPanel panel = new JPanel();
         panel.add(show1);
         panel.add(show2);
         panel.add(show3);
         return panel;
    }
 
    private void initWindows(Frame f)
    {
        dialog1 = new JDialog(f, "dialog 1");
        dialog1.getContentPane().add(new WindowGraphicPanel());
        dialog1.setSize(250,120);
        dialog1.setLocation(100,275);
        dialog2 = new JDialog(f, "dialog 2");
        dialog2.getContentPane().add(new WindowGraphicPanel());
        dialog2.setSize(200,140);
        dialog2.setLocation(275,320);
        frame = new JFrame("frame");
        frame.getContentPane().add(new WindowGraphicPanel());
        frame.setSize(100,100);
        frame.setLocation(400,375);
    }
 
    public static void main(String[] args)
    {
        new WindowTest();
    }
}
 
class WindowGraphicPanel extends JPanel
{
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int d = Math.min(w,h)/3;
        g2.setPaint(Color.blue);
        g2.draw(new Rectangle2D.Double(w/16, h/16, w*7/8, h*7/8));
        g2.setPaint(Color.red);
        g2.fill(new Ellipse2D.Double(w/2-d/2, h/2-d/2, d, d));
        g2.setPaint(Color.green.darker());
        g2.draw(new Line2D.Double(w/16, h/16, w*15/16, h*15/16));
    }
}

