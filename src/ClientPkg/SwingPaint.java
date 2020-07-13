/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ClientPkg;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class SwingPaint {
	JList userList;
	JFrame frame;
	Container currentboard;
	JButton lineBtn, rectBtn, circleBtn, ovalBtn, txtBtn, eraserBtn, colorBtn, kickBtn ;
	JComboBox fileList;
	DataInputStream input ;
    DataOutputStream output ;
	DrawCircle drawCircle;
	DrawOval drawOval;
	DrawLine drawLine;
	Eraser eraser;
	WriteText writeTxt;
	DrawRectangle drawRect;
	String mode;
	String[] currentUsers;
	
	public Container getContainer() {
		return currentboard;
	}
	
	public void show() {
		frame = new JFrame("WhiteBoard");
		currentboard = frame.getContentPane();
		currentboard.setLayout(new BorderLayout());

		JPanel panel = new JPanel();

		lineBtn = new JButton("Line");
		lineBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentboard.getComponentCount() > 2) {
					DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
					drawLine = new DrawLine(currentArea.img, currentArea.g2);
					currentboard.remove(currentboard.getComponentCount() - 1);
				} else {
					drawLine = new DrawLine();
				}
				String url = "src/CursorImg/pen.gif";
				Toolkit tk = Toolkit.getDefaultToolkit();
				Image eraserImg = new ImageIcon(url).getImage();
				Cursor cursor = tk.createCustomCursor(eraserImg, new Point(10, 10), "norm");
				drawLine.setCursor(cursor);
				drawLine.setOutput(output);
				currentboard.add(drawLine, BorderLayout.CENTER);
				frame.revalidate();
				frame.repaint();
			}		
		});

		
		
		
		rectBtn = new JButton("Rect");
		rectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (currentboard.getComponentCount() > 2) {
					DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
					drawRect = new DrawRectangle(currentArea.img, currentArea.g2);
					currentboard.remove(currentboard.getComponentCount() - 1);
				} else {
					drawRect = new DrawRectangle();
				}
				
				drawRect.setOutput(output);
				currentboard.add(drawRect, BorderLayout.CENTER);
				frame.revalidate();
				frame.repaint();
			}
		});

		circleBtn = new JButton("Circle");
		circleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (currentboard.getComponentCount() > 2) {
					DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
					drawCircle = new DrawCircle(currentArea.img, currentArea.g2);
					currentboard.remove(currentboard.getComponentCount() - 1);
				} else {
					drawCircle = new DrawCircle();
				}
				
				drawCircle.setOutput(output);
				currentboard.add(drawCircle, BorderLayout.CENTER);
				frame.revalidate();
				frame.repaint();
			}
		});

		ovalBtn = new JButton("Oval");
		ovalBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (currentboard.getComponentCount() > 2) {
					DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
					drawOval = new DrawOval(currentArea.img, currentArea.g2);
					currentboard.remove(currentboard.getComponentCount() - 1);
				} else {
					drawOval = new DrawOval();
				}
				
				drawOval.setOutput(output);
				currentboard.add(drawOval, BorderLayout.CENTER);
				frame.revalidate();
				frame.repaint();
			}
		});

		JTextField wordBox = new JTextField(10);
		wordBox.setText("Please input the word.");
		wordBox.setFont(new Font("Times New Roman", Font.PLAIN, 15));

		txtBtn = new JButton("Text");
		txtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentboard.getComponentCount() > 2) {
					DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
					writeTxt = new WriteText(currentArea.img, currentArea.g2);
					currentboard.remove(currentboard.getComponentCount() - 1);
				} else {
					writeTxt = new WriteText();
				}
				writeTxt.setOutput(output);
				writeTxt.inputStr = wordBox.getText();
				currentboard.add(writeTxt, BorderLayout.CENTER);
				frame.revalidate();
				frame.repaint();
			}
		});

		eraserBtn = new JButton("Eraser");
		eraserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (currentboard.getComponentCount() > 2) {
					DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
					eraser = new Eraser(currentArea.img, currentArea.g2);
					currentboard.remove(currentboard.getComponentCount() - 1);
					String url = "src/CursorImg/eraser.gif";
					Toolkit tk = Toolkit.getDefaultToolkit();
					Image eraserImg = new ImageIcon(url).getImage();
					Cursor cursor = tk.createCustomCursor(eraserImg, new Point(10, 10), "norm");
					eraser.setCursor(cursor);
					
					eraser.setOutput(output);
					currentboard.add(eraser, BorderLayout.CENTER);
					frame.revalidate();
					frame.repaint();
				}
			}
		});

		colorBtn = new JButton("");
		colorBtn.setBackground(new Color(0, 0, 0));
		colorBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentboard.getComponentCount() > 2) {
					DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
					ColorPanel.newScreen(colorBtn, currentArea.g2);
				} else {
					ColorPanel.newScreen(colorBtn, null);
				}
			}
		});


		if(mode == "Admin") {
			String[] func = {"New", "Open", "Save",  "Save As", "Close"};
			
			fileList = new JComboBox(func);
			fileList.setFont(new Font("Arial", Font.PLAIN, 16));
			fileList.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				   {
						
						String chosenFunc = (String) fileList.getSelectedItem();
						if(chosenFunc.equals("New")) {
							try {
								output.writeUTF("NEW_FILE");
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (currentboard.getComponentCount() > 2) {
								DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
								Color originColor = currentArea.g2.getColor();
								currentArea.loadImg(currentArea.createImage(currentArea.getSize().width, currentArea.getSize().height));
								currentArea.loadG2((Graphics2D) currentArea.img.getGraphics());
								currentArea.g2.setColor(originColor);
								currentArea.clearScreen();
							}
						}else if(chosenFunc.equals("Close")) {
							try {
								output.writeUTF("QUIT");
								System.exit(0);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
							
						}else if(chosenFunc.equals("Save")) {
							 FileProcessFrame fileFrm = new FileProcessFrame(currentboard, output, "Save");
							 fileFrm.setVisible(true);
						}else if(chosenFunc.equals("Save As")) {
							 FileProcessFrame fileFrm = new FileProcessFrame(currentboard, output, "Save As");
							 fileFrm.setVisible(true);
						}else if(chosenFunc.equals("Open")) {
							 FileProcessFrame fileFrm = new FileProcessFrame(currentboard, output, "Open");
							 fileFrm.setVisible(true);
						}
						
				   }
				
			});
			panel.add(fileList);
		}
		

		panel.add(lineBtn);
		panel.add(rectBtn);
		panel.add(circleBtn);
		panel.add(ovalBtn);
		panel.add(txtBtn);
		panel.add(wordBox);
		panel.add(eraserBtn);
		panel.add(colorBtn);
		
		if(mode == "Admin") {
			
			kickBtn = new JButton("Kick");
			kickBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(currentUsers != null) {
						if(currentUsers.length ==1) {
							JOptionPane.showOptionDialog(null, 
							        "No client joined!", 
							        "Notification", 
							        JOptionPane.DEFAULT_OPTION, 
							        JOptionPane.WARNING_MESSAGE, 
							        null, 
							        new String[]{"Back"}, // this is the array
							        "default");
							return;
						}else {
							String[] ul = new String[currentUsers.length -1];
							for (int i = 1; i < currentUsers.length; i++) {
									ul[i-1] = currentUsers[i];
							}
							KickingFrame kickFrm = new KickingFrame(output, ul);
							kickFrm.setVisible(true);
						}	
					}
				}
			});
			panel.add(kickBtn);
		}

		currentboard.add(panel, BorderLayout.NORTH);
		
	    userList = new JList<String>();
	    userList.setPreferredSize(new Dimension(150, 100));
	    userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    currentboard.add(userList, BorderLayout.EAST);
		
		drawLine = new DrawLine();
		currentboard.add(drawLine, BorderLayout.CENTER);
		

		frame.setSize(1000, 600);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	try {
					output.writeUTF("QUIT");
					System.exit(0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		    }
		});

		frame.setVisible(true);
	}
	
}

