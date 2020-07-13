/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ClientPkg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.io.DataOutputStream;
import java.util.ArrayList;

import javax.swing.JComponent;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;  

public class DrawArea extends JComponent{
	public Image img;
	public Graphics2D g2;
	public int newX, newY, oldX, oldY;
	public DataOutputStream output;
	public String status = "null";
	
	public DrawArea() {
	}
	
	public void clearScreen() {
		Color originColor = g2.getColor();
		g2.setPaint(Color.white);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setPaint(originColor);
		repaint();
	}

	public void setColor(String color) {
		switch(color) {
			case "red":
				g2.setPaint(Color.red);
				break;
			case "black":
			default:
				g2.setPaint(Color.black);
		}
	}
	
	public Image getImg() {
		return img;
	}
	
	public void loadImg(Image currentImg) {
		img = currentImg;
	}
	
	public void loadG2(Graphics2D  currentG2) {
		g2= currentG2;
	}
	
	public Graphics2D getG2() {
		return g2;
	}
	
	public String genOp(String type, Graphics2D g2, int oldX, int oldY, int newX, int newY) {
		String trace = Integer.toString(oldX) + ","  + Integer.toString(oldY) + ","  + Integer.toString(newX) + ","  + Integer.toString(newY);
		JSONObject newCommand = new JSONObject();
		newCommand.put("Type", type);
		newCommand.put("Points", trace);
		newCommand.put("Color", Integer.toString(g2.getColor().getRGB()));
		return newCommand.toJSONString();
	}
	
	public void sendOp(String command) {
		try {
		if (output!=null)output.writeUTF(command);
		}catch(Exception e) {	
			e.printStackTrace();
		}
	}
	
	public void  setOutput(DataOutputStream currentOutput) {
		output = currentOutput;
	}
	
	public void drawOp(String operation, Graphics2D g2) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject command = (JSONObject) parser.parse(operation);
			String type =  (String) command.get("Type");
			String points;
			String[] cordArr;
			int px, py, pw, ph, radius;
			Color drawColor;
			Color originColor = g2.getColor(); 
			switch(type) {
				case "Line":
					drawColor = new Color(Integer.parseInt((String) command.get("Color")));
					g2.setColor(drawColor);
					points =  (String) command.get("Points");
					cordArr =  points.split(",");
					g2.drawLine(Integer.parseInt(cordArr[0]), Integer.parseInt(cordArr[1]), Integer.parseInt(cordArr[2]), Integer.parseInt(cordArr[3]));
					repaint();
					break;
				case "Rect":
					drawColor = new Color(Integer.parseInt((String) command.get("Color")));
					g2.setColor(drawColor);
					points =  (String) command.get("Points");
					cordArr =  points.split(",");
				    px = Math.min(Integer.parseInt(cordArr[0]),Integer.parseInt(cordArr[2]));
				    py = Math.min(Integer.parseInt(cordArr[1]),Integer.parseInt(cordArr[3]));
				    pw=Math.abs(Integer.parseInt(cordArr[0]) - Integer.parseInt(cordArr[2]));
				    ph=Math.abs(Integer.parseInt(cordArr[1]) - Integer.parseInt(cordArr[3]));
					g2.drawRect(px, py, pw, ph);
					repaint();
					break;
				case "Circle":
					drawColor = new Color(Integer.parseInt((String) command.get("Color")));
					g2.setColor(drawColor);
					points =  (String) command.get("Points");
					cordArr =  points.split(",");
					px = Math.min(Integer.parseInt(cordArr[0]),Integer.parseInt(cordArr[2]));
				    py = Math.min(Integer.parseInt(cordArr[1]),Integer.parseInt(cordArr[3]));
				    pw=Math.abs(Integer.parseInt(cordArr[0]) - Integer.parseInt(cordArr[2]));
				    ph=Math.abs(Integer.parseInt(cordArr[1]) - Integer.parseInt(cordArr[3]));
				    radius = Math.max(pw, ph);
				    g2.drawOval(px, py, radius, radius);
				    repaint();
					break;
				case "Oval":
					drawColor = new Color(Integer.parseInt((String) command.get("Color")));
					g2.setColor(drawColor);
					points =  (String) command.get("Points");
					cordArr =  points.split(",");
				    px = Math.min(Integer.parseInt(cordArr[0]),Integer.parseInt(cordArr[2]));
				    py = Math.min(Integer.parseInt(cordArr[1]),Integer.parseInt(cordArr[3]));
				    pw=Math.abs(Integer.parseInt(cordArr[0]) - Integer.parseInt(cordArr[2]));
				    ph=Math.abs(Integer.parseInt(cordArr[1]) - Integer.parseInt(cordArr[3]));
					g2.drawOval(px, py, pw, ph);
					repaint();
					break;
				case "Eraser":
					points =  (String) command.get("Points");
					cordArr =  points.split(",");
					radius =Integer.parseInt((String) command.get("Radius"));
					g2.setColor(Color.white);
			        g2.fillRect(Integer.parseInt(cordArr[0]), Integer.parseInt(cordArr[1]), radius, radius);
			        repaint();
					break;
				case "Text":
					drawColor = new Color(Integer.parseInt((String) command.get("Color")));
					g2.setColor(drawColor);
					points =  (String) command.get("Points");
					cordArr =  points.split(",");
					String inputStr = (String) command.get("String");
					g2.drawString(inputStr, Integer.parseInt(cordArr[0]), Integer.parseInt(cordArr[1]));
					repaint();
					break;
	
			}
			g2.setColor(originColor);
			
			
		} catch (ParseException e) {

		}
		
		
	}
}
