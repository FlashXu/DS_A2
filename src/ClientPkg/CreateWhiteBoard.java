/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ClientPkg;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JList;
import javax.swing.JOptionPane;
import java.util.Scanner;

public class CreateWhiteBoard {
	
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
			LoginFrame login = new LoginFrame(ip, port, "Admin");
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
			whiteBoard.mode = "Admin";
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
		String command;
		try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(true) {

			try {
				
				if(input.available() > 0){ 
					command = input.readUTF();
					
					// Check login info;
					if(command.contains("CHECK_LOGIN")) {
						System.out.println(command);
						String userName =  command.split("/")[1];

						int resp =  JOptionPane.showConfirmDialog(null, "Client "+ userName +  " wants to share your whiteboard.", "Check Join", JOptionPane.YES_NO_OPTION);

						if(resp == 0) {
							output.writeUTF("Yes/" + userName);
							System.out.println("Accept client " + userName);
						}else {
							output.writeUTF("No/" + userName);
							System.out.println("Reject client " + userName);

						}
						continue;
					}
					
					// Update userList info.
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
	
}
