/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ClientPkg;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JList;
import javax.swing.JOptionPane;

public class JoinWhiteBoard {
	
	public static void main(String[] args) {
		
		// Check the input parameters.
		if(args.length!=2) {
			System.out.println("Wrong parameters!");
			return;
		}
		String ip = args[0];
		int port;
		try {
			port = Integer.parseInt(args[1]);
		}catch(NumberFormatException err) {
			System.out.println("The port number must be an integer.");
			return;
		}
		
		try {
			LoginFrame login = new LoginFrame(ip, port, "Guest");
			login.setVisible(true);
			while(login.getStatus().equals("Disconnected")) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Socket socket = login.getSocket();
			
			SwingPaint whiteBoard = new SwingPaint();
		
			DataInputStream input = new DataInputStream(socket.getInputStream());
		    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
	    
			whiteBoard.input = input; 
			whiteBoard.output = output;
			whiteBoard.mode = "Guest";
			whiteBoard.show();
			
			Thread t = new Thread(() -> recImg(whiteBoard, socket));
			t.start();
			
		    
		    
		}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
		
		}	
	}
	
	private static void recImg(SwingPaint whiteBoard, Socket socket) {
		DataInputStream input = null;
		DataOutputStream output = null;
		Container currentboard = whiteBoard.currentboard;
		try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(true) {

			try {
				
				if(input.available() > 0){ 
					String command = input.readUTF();
					//Update user list.
					if(command.contains("Client")) {
						JList userList = (JList) currentboard.getComponent(1);
						String[] infoList = command.split("USER_BREAKER");
						String[] currentUserList = new String[infoList.length];
						for (int i = 0; i < infoList.length; i++) {
							currentUserList[i] = infoList[i].split("ACTION_BREAKER")[0];
							currentUserList[i] = currentUserList[i].split(": ")[1];
							infoList[i] = infoList[i].replace("ACTION_BREAKER"," ");
						}
						whiteBoard.currentUsers = currentUserList;
						userList.setListData(infoList);
						continue;
					}
					//Admin quits.
					if(command.equals("ADMIN_QUIT")) {
						JOptionPane.showOptionDialog(null, 
						        "Administrator has quit!", 
						        "Server Notification", 
						        JOptionPane.DEFAULT_OPTION, 
						        JOptionPane.ERROR_MESSAGE, 
						        null, 
						        new String[]{"Leave"}, // this is the array
						        "default");
						System.exit(0);
					}
					// Kick by admin.
					if(command.equals("KICKED")) {
						JOptionPane.showOptionDialog(null, 
						        "You are kicked by administrator!", 
						        "Server Notification", 
						        JOptionPane.DEFAULT_OPTION, 
						        JOptionPane.WARNING_MESSAGE, 
						        null, 
						        new String[]{"Leave"}, // this is the array
						        "default");
						System.exit(0);
					}
					// NewFile
					if(command.equals("NEW_FILE")) {
						if (currentboard.getComponentCount() > 2) {
							DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
							currentArea.clearScreen();
							}
						continue;
					}
					
					if(command.contains("OPEN_IMG")) {
						long fileSize = Long.parseLong(command.split("/")[1]);
						long fileSizeRemaining = Long.parseLong(command.split("/")[1]);
						int chunkSize = setChunkSize(fileSizeRemaining);
						byte[] receiveBuffer = new byte[chunkSize];
						int num;
						byte[] imgByte = null;
						RandomAccessFile downloadingFile = new RandomAccessFile("recServerImg.jpg", "rw");
						while((num=input.read(receiveBuffer))>0){
								downloadingFile.write(Arrays.copyOf(receiveBuffer, num));
								// Reduce the file size left to read..
    							fileSizeRemaining-=num;

    							chunkSize = setChunkSize(fileSizeRemaining);
    							receiveBuffer = new byte[chunkSize];

    							if(fileSizeRemaining==0){
    								break;
    							}
							}
						downloadingFile.close();
						File imgFile = new File("recServerImg.jpg");
						BufferedImage imgBuffer = ImageIO.read(imgFile);
						BufferedImage jpgBuffer = new BufferedImage(imgBuffer.getWidth(), imgBuffer.getHeight(), BufferedImage.TYPE_INT_RGB);
						 jpgBuffer.createGraphics().drawImage(imgBuffer, 0, 0, null);
						 ByteArrayOutputStream baos = new ByteArrayOutputStream();
						 ImageIO.write(jpgBuffer, "jpg", baos);
						 
						 if (currentboard.getComponentCount() > 2) {
								DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
								Color orginColor = currentArea.getG2().getColor();
								Graphics2D g2 = (Graphics2D) jpgBuffer.getGraphics();
								g2.setColor(orginColor);
								currentArea.loadImg(jpgBuffer);
								currentArea.loadG2(g2);
								currentArea.repaint();	
							}
						continue;
					}
					
					
					
					if (currentboard.getComponentCount() > 2) {
						DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
						if (currentArea.g2 == null) {
							Image img = currentArea.createImage(currentArea.getSize().width, currentArea.getSize().height);
							Graphics2D g2 = (Graphics2D) img.getGraphics();
							currentArea.loadImg(img);
							currentArea.loadG2(g2);
							currentArea.clearScreen();
							g2.drawImage(img, 0, 0, null);
						}
						currentArea.drawOp(command, currentArea.g2);

					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static int setChunkSize(long fileSizeRemaining){
		// Determine the chunkSize
		int chunkSize=1024*1024;
		if(fileSizeRemaining<chunkSize){
			chunkSize=(int) fileSizeRemaining;
		}
		return chunkSize;
	}
	
	public static byte[] combineByte(byte[] b1, byte[] b2){
		if(b1 == null) return b2;
		byte[] newb = new byte[b1.length+b2.length];
		for (int i = 0; i < b1.length; i++) {
			newb[i] = b1[i];
		}
		for (int i = b1.length; i < newb.length; i++) {
			newb[i] = b2[i - b1.length];
		}
		return newb;
		
	}

}

