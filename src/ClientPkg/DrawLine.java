/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ClientPkg;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;

import ClientPkg.DrawRectangle.MyMouseListener;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;  

public class DrawLine extends DrawArea {
	
	
	public DrawLine(){
		setDoubleBuffered(false);
		MyMouseListener listener = new MyMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
	}

	public DrawLine(Image currentImg, Graphics2D currentG2) {
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

             if (g2 != null) {
					// Draw a line.
					g2.drawLine(oldX, oldY, newX, newY);
					String newCommand = genOp("Line", g2, oldX, oldY, newX, newY);
					sendOp(newCommand);
					
					if(status == "null") {
						JSONObject statusCommand = new JSONObject();
			            statusCommand.put("STATUS", "Drawing Line");
			            sendOp(statusCommand.toString());
			            status = "Dragged";
					}
					
					repaint();
					oldX = newX;
					oldY = newY;
				}
        }

		@Override
		public void mouseReleased(MouseEvent e) {
			if(status != "null") {
				JSONObject statusCommand = new JSONObject();
	            statusCommand.put("STATUS", "");
	            sendOp(statusCommand.toString());
	            status = "null";
			}
		}
        
    }
	
	@Override
	protected void paintComponent(Graphics g) {
		if(img == null) {
			img = createImage(getSize().width, getSize().height);
			g2 = (Graphics2D) img.getGraphics();
			clearScreen();
		}
		g.drawImage(img, 0, 0, null);
//		if (img != null) {
//			BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null),
//					BufferedImage.TYPE_INT_RGB);
//			Graphics bg = bi.getGraphics();
//			try {
//				bg.drawImage(img, 0, 0, null);
//				bg.dispose();
//				ImageIO.write(bi, "jpg", new File("xyc.jpg"));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		
	}
	

}


