/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ServerPkg;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UTFDataFormatException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.net.ServerSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {

		// Identifies the user number connected
		private static int counter = 0;

		public File localLog;
		
		public File currentImg;

		public volatile ArrayList<User> userList = new  ArrayList<User>();
		
		public volatile JSONObject adminResponse = new JSONObject();

		public Server(String logPath) {
			try {
			localLog = new File(logPath);
			if(!localLog.exists()){
				localLog.createNewFile();
	           }
			}catch(Exception e) {
				
			}
			
		}
		
		public static void main(String[] args) {
			// Check the input parameters.
			if(args.length!=1) {
				System.out.println("Wrong parameters!");
				return;
			}
			int port;
			try {
				port = Integer.parseInt(args[0]);
			}catch(NumberFormatException err) {
				System.out.println("The port number must be an integer.");
				return;
			}
			
			Server localServer = new Server("operationLog.txt");
			ServerSocket s = null;
			try {
				s = new ServerSocket(port);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Waiting for client connection..");
			
				// Wait for connections.
				while(true){
					
					String userName = "";
					try { 
						Socket client = s.accept();
						String mode = "";
						DataInputStream input = new DataInputStream(client.getInputStream());
						DataOutputStream output = new DataOutputStream(client.getOutputStream());
				
						while(true) {
						if(input.available() > 0){ 
								String loginCommand =  input.readUTF();
								userName = loginCommand.split("/")[1];
								mode = loginCommand.split("/")[2];
								break;
								
						}
						}
						
						// Send Msg to Admin and check whether it can be accepted
						if (counter>0) {
							// Ensure uniqueness.
							boolean duplicate = false;
							for (User user: localServer.userList) {
								if (user.getUserName().equals(userName)) {
									duplicate = true;
									output.writeUTF("DUPLICATE");
									client.close();
									break;
								}
							}
							if(duplicate)continue;
							
							Socket admin = localServer.userList.get(0).getSocket();
							DataInputStream adminInput = new DataInputStream(admin.getInputStream());
							DataOutputStream adminOutput = new DataOutputStream(admin.getOutputStream());
							
							adminOutput.writeUTF("CHECK_LOGIN/" + userName);
							while(!localServer.adminResponse.containsKey(userName)) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							String resp = (String) localServer.adminResponse.get(userName);

							// If admin reject;
							if(resp.equals("No")) {
								output.writeUTF("ACCESS_REJECTED");
								client.close();
								localServer.adminResponse.remove(userName);
								continue;
							}else {
								output.writeUTF("ACCEPT");
								localServer.adminResponse.remove(userName);
							}
							
						}else {
							if(mode.equals("Admin")) {
								output.writeUTF("ACCEPT_ADMIN");
							}else {
								output.writeUTF("NOT_ADMIN");
								client.close();
								continue;
							}
						}
						
						counter++;
						System.out.println("Client "+userName+" successfully connected!");
						localServer.userList.add(new User(client, userName));
						// Broadcast new user to everyone.
						localServer.updateUserList();
						
						// Send Record back.
						
						if (localServer.currentImg!=null)localServer.sendImg(output);
						localServer.loadDrawn(output);
					
					// Start a new thread for a connection
					final String socketUserName = userName;
					Thread t1 = new Thread(() -> updateImg(client, socketUserName, localServer));
					t1.start();

				}catch(SocketException e) {
					// If someone quit.
					if(counter>1) { 
						System.out.println("Client "+ userName+ " is disconnected.");
						int quitIndex = 0;
						for (int i = 0; i < localServer.userList.size(); i++) {
							User currentUser = localServer.userList.get(i);
							if(currentUser.getUserName().equals(userName)) {
								quitIndex = i;  
								break;
							}
						}
						localServer.userList.remove(quitIndex);
						localServer.updateUserList();
						}
					continue;	
				}catch(IOException e) {
					e.printStackTrace();
					continue;
				}
				}
			}

			
		private static  void updateImg(Socket client, String userName, Server localServer ) {
			try {
			DataInputStream input = new DataInputStream(client.getInputStream());
			DataOutputStream output = new DataOutputStream(client.getOutputStream());
				while(true) {
						if(input.available() > 0){ 

							String operation = input.readUTF();
							
							// If someone QUIT.
							if(operation.equals("QUIT")) {
								int quitIndex = 0;
								for (int i = 0; i < localServer.userList.size(); i++) {
									User currentUser = localServer.userList.get(i);
									if(currentUser.getUserName().equals(userName)) {
										quitIndex = i;  
										break;
									}
								}
								// Not Admin quit.
								if (quitIndex>0) {
									client.close();
									localServer.userList.remove(quitIndex);
									localServer.updateUserList();
									System.out.println("Client " + userName + "quits.");
									return;
								}else {
									// Admin quit.
									// Broadcast others.
									for (int i = 1; i < localServer.userList.size(); i++) {
										Socket currentSocket =  localServer.userList.get(i).getSocket();
										DataOutputStream currentOutput = new DataOutputStream(currentSocket.getOutputStream());
										currentOutput.writeUTF("ADMIN_QUIT");
									}
									
									System.out.println("ADMIN QUIT!");
									if(localServer.localLog!=null) {
										PrintWriter writer = new PrintWriter(localServer.localLog);
										writer.print("");
										writer.close();
										localServer.localLog.delete();
										localServer.localLog = new File("operationLog.txt");
									}
									
									if(localServer.currentImg!=null) {
										localServer.currentImg.delete();
										localServer.currentImg = null;
									}
									localServer.counter = 0;
									client.close();
									localServer.userList = new  ArrayList<User>();
									break;
									
								}
							}
							// Receive status info.
							if(operation.contains("STATUS")) {
								JSONParser parser = new JSONParser();
								try {
									JSONObject statusCommand = (JSONObject) parser.parse(operation);
									String currentStatus = (String) statusCommand.get("STATUS");
									for (int i = 0; i < localServer.userList.size(); i++) {
										User currentUser = localServer.userList.get(i);
										if(currentUser.getUserName().equals(userName)) {
											currentUser.setStatus(currentStatus); 
											localServer.updateUserList();
											break;
										}
		
									}
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							
								
								}
							
							// If someone wants to join and asks admin for acceptance.
							if(operation.contains("Yes") || operation.contains("No")) {
								
								String resp = operation.split("/")[0];
								String corspUser = operation.split("/")[1];
								localServer.adminResponse.put(corspUser,resp);
									
							}
							
							if(operation.contains("KICK")) {
								int kickIndex = 0;
								String kickUserName = operation.split("/")[1];
								
								for (int i = 1; i < localServer.userList.size(); i++) {
									User currentUser = localServer.userList.get(i);
									if(currentUser.getUserName().equals(kickUserName)) {
										kickIndex = i;
										Socket kickSocket = currentUser.getSocket();
										DataOutputStream kickOutput = new DataOutputStream(kickSocket.getOutputStream());
										kickOutput.writeUTF("KICKED");
										kickSocket.close();
										localServer.userList.remove(i);
										localServer.updateUserList();
										break;
									}
								}
								System.out.println("Client " + kickUserName + " was kicked by administrator.");
								continue;		
							}
							
							// File operation.
							if(operation.equals("NEW_FILE")) {
								PrintWriter writer = new PrintWriter(localServer.localLog);
								writer.print("");
								writer.close();
								if(localServer.currentImg!=null) {
									localServer.currentImg.delete();
									localServer.currentImg = null;
								}
								 for(int i = 1; i<localServer.userList.size(); i++){
										Socket user = localServer.userList.get(i).getSocket();
										DataOutputStream userOutput = new DataOutputStream(user.getOutputStream());
										 userOutput.writeUTF("NEW_FILE");
									}
							}
							
							if(operation.contains("OPEN_IMG")) {
								PrintWriter writer = new PrintWriter(localServer.localLog);
								writer.print("");
								writer.close();
								long fileSize = Long.parseLong(operation.split("/")[1]);
								long fileSizeRemaining = Long.parseLong(operation.split("/")[1]);
								int chunkSize = setChunkSize(fileSizeRemaining);
								byte[] receiveBuffer = new byte[chunkSize];
								int num;
								byte[] imgByte = null;
								RandomAccessFile downloadingFile = new RandomAccessFile("serverImg.jpg", "rw");
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
								localServer.currentImg = new File("serverImg.jpg");

								 for(int i = 1; i<localServer.userList.size(); i++){
										Socket user = localServer.userList.get(i).getSocket();
										DataOutputStream userOutput = new DataOutputStream(user.getOutputStream());
										localServer.sendImg(userOutput);
									}
							}
							
							
							if(!operation.contains("Type"))continue;
							localServer.updateRecord(operation);

							//Broadcast the new operation to other users.
							for(int i = 0; i<localServer.userList.size(); i++){
								String currentUserName = localServer.userList.get(i).getUserName();
								if(currentUserName.equals(userName))continue;
								Socket user = localServer.userList.get(i).getSocket();
								DataOutputStream userOutput = new DataOutputStream(user.getOutputStream());
								 userOutput.writeUTF(operation);
							}

						}
				}
				
			}catch(SocketException e) {
				System.out.println("Client " + userName + " disconnected.");
				
			}catch(IOException e) {
				System.out.println("Client " + userName + " disconnected.");
				
			}
		}
		
		public void updateRecord(String operation) {
			try {
				
				FileWriter fw = new FileWriter(localLog, true);
				PrintWriter pw = new PrintWriter(fw);
				pw.println(operation);
				pw.flush();
				pw.close();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		public void loadDrawn(DataOutputStream output) {
			try {
			BufferedReader br = new BufferedReader(new FileReader(localLog));
			String s = null;
            while((s = br.readLine())!=null){
            	output.writeUTF(s);
        
            	}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	
		public void updateUserList() {
			
			String userStr = "";
			for(User user: userList) {
				userStr += "Client: " + user.getUserName() +"ACTION_BREAKER" +  user.getStatus() +  "USER_BREAKER";
			}
			for(User user: userList) {
				try {
					DataOutputStream userOutput = new DataOutputStream(user.getSocket().getOutputStream());
					userOutput.writeUTF(userStr);	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	
		}
		
		public void sendImg(DataOutputStream output) {
			try {
				if(currentImg !=null && currentImg.exists()) {
					output.writeUTF("OPEN_IMG/" + Long.toString(currentImg.length()));
					 byte[] sendingBuffer = new byte[1024*1024];
					 RandomAccessFile byteFile = new RandomAccessFile(currentImg,"r");
					 int num;
						// While there are still bytes to send..
						while((num = byteFile.read(sendingBuffer)) > 0){
							output.write(Arrays.copyOf(sendingBuffer, num));
						}
						byteFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
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
		

			
		
		
}
