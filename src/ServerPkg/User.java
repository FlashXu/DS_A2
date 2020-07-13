/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ServerPkg;

import java.net.Socket;

public class User {
	private Socket userSocket;
	private String userName;
	private String userStatus = "";
	
	public User(Socket userSocket, String userName) {
		this.userSocket = userSocket;
		this.userName = userName;
	}
	
	public Socket getSocket() {
		return userSocket;
	} 
	
	public String getUserName() {
		return userName;
	}
	
	public String getStatus() {
		return userStatus;
	}
	
	public void setStatus(String status) {
		userStatus = status;
	}
	

}
