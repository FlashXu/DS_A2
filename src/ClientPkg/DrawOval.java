/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ClientPkg;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.json.simple.JSONObject;

import ClientPkg.DrawRectangle.MyMouseListener;

public class DrawOval extends DrawArea {
	
	public DrawOval(){
		setDoubleBuffered(false);
		MyMouseListener listener = new MyMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
	}
	
	public DrawOval(Image currentImg, Graphics2D currentG2) {
		img = currentImg;
		g2 = currentG2;
		MyMouseListener listener = new MyMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
	}
	

	class MyMouseListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
        	oldX = e.getX();
            oldY = e.getY();        
        }

        public void mouseDragged(MouseEvent e) {
        	 newX = e.getX();
             newY = e.getY();
            repaint();
            if(status == "null") {
            	JSONObject statusCommand = new JSONObject();
            	statusCommand.put("STATUS", "Drawing Oval");
            	sendOp(statusCommand.toString());
            	status = "Dragged";
            }
            
        }

        public void mouseReleased(MouseEvent e) {
        	newX = e.getX();
            newY = e.getY();
            drawPerfectOval(g2, oldX, oldY, newX, newY);
            
            String newCommand = genOp("Oval", g2, oldX, oldY, newX, newY);
			sendOp(newCommand);
			if(status != "null") {
				JSONObject statusCommand = new JSONObject();
	            statusCommand.put("STATUS", "");
	            sendOp(statusCommand.toString());
	            status = "null";
			}
			
            repaint();
        	oldX = newX;
			oldY = newY;
			
        }
    }
	
	public void drawPerfectOval(Graphics g, int x1, int y1, int x2, int y2) {
        int px = Math.min(x1,x2);
        int py = Math.min(y1,y2);
        int pw=Math.abs(x1-x2);
        int ph=Math.abs(y1-y2);
        g.drawOval(px, py, pw, ph);
    }
	
	@Override
	protected void paintComponent(Graphics g) {
		if(img == null) {
			img = createImage(getSize().width, getSize().height);
			g2 = (Graphics2D) img.getGraphics();
			clearScreen();
		}
		g.drawImage(img, 0, 0, null);
		g.setColor(g2.getColor());
		drawPerfectOval(g, oldX, oldY, newX, newY);	
		
	}

	

}
