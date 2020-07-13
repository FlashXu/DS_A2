/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ClientPkg;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.JTextField;
import javax.swing.JButton;

public class LoginFrame extends JFrame {

	private JPanel contentPane;
	private JTextField unField;
	private Socket currentSocket;
	private String currentMode;
	private static String ip ;
	private static int port ;
	private String currentStatus = "Disconnected";

	/**
	 * Create the frame.
	 */
	public LoginFrame(String ip, int port, String mode) {
		
		setTitle("Login Whiteboard");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 441, 236);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("User Name:");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		lblNewLabel.setBounds(83, 53, 111, 29);
		contentPane.add(lblNewLabel);
		
		unField = new JTextField();
		unField.setBounds(83, 92, 245, 21);
		contentPane.add(unField);
		unField.setColumns(10);
		
		currentMode = mode;
		JButton joinButton;
		if (mode.equals("Admin")){
			 joinButton = new JButton("Create");
		}
		else {
			 joinButton = new JButton("Join");
		}
		
		joinButton.setFont(new Font("Arial", Font.PLAIN, 18));
		joinButton.setBounds(101, 139, 93, 23);
		contentPane.add(joinButton);
		joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				
				String username = unField.getText();
				if(username == null || username.equals("")) {
					JOptionPane.showOptionDialog(null, 
					        "Username cannot be empty!", 
					        "Notification", 
					        JOptionPane.DEFAULT_OPTION, 
					        JOptionPane.WARNING_MESSAGE, 
					        null, 
					        new String[]{"Back"}, // this is the array
					        "default");

					return;
				}
				currentSocket = new Socket(ip, port);
				DataOutputStream output = new DataOutputStream(currentSocket.getOutputStream());
				DataInputStream input = new DataInputStream(currentSocket.getInputStream());
				
				output.writeUTF("LOGIN/" + username + "/" + currentMode);
				System.out.println("LOGIN/" + username + "/" + currentMode);
				while(true) {
				if(input.available()>0) {
					String resp = input.readUTF();
					System.out.println(resp);
					switch(resp) {
					case "ACCEPT":
						JOptionPane.showOptionDialog(null, 
						        "You are successfully login, client " + username + "!", 
						        "Server Response", 
						        JOptionPane.DEFAULT_OPTION, 
						        JOptionPane.INFORMATION_MESSAGE, 
						        null, 
						        new String[]{"OK"}, // this is the array
						        "default");
						setVisible(false);
						currentStatus = "Connected";
						break;
					case "ACCEPT_ADMIN":
						JOptionPane.showOptionDialog(null, 
						        "Welcome, administrator "+ username + "!", 
						        "Server Response", 
						        JOptionPane.DEFAULT_OPTION, 
						        JOptionPane.INFORMATION_MESSAGE, 
						        null, 
						        new String[]{"OK"}, // this is the array
						        "default");
						setVisible(false);
						currentStatus = "Connected";
						break;
					case "DUPLICATE":
						JOptionPane.showOptionDialog(null, 
						        "Duplicated username!", 
						        "Server Response", 
						        JOptionPane.DEFAULT_OPTION, 
						        JOptionPane.ERROR_MESSAGE, 
						        null, 
						        new String[]{"Back"}, // this is the array
						        "default");
						break;
					case "ACCESS_REJECTED":
						JOptionPane.showOptionDialog(null, 
						        "You are rejected by administrator!", 
						        "Server Response", 
						        JOptionPane.DEFAULT_OPTION, 
						        JOptionPane.WARNING_MESSAGE, 
						        null, 
						        new String[]{"Back"}, // this is the array
						        "default");
						break;
					case "NOT_ADMIN":
						JOptionPane.showOptionDialog(null, 
						        "There is no white board you can join!", 
						        "Server Response", 
						        JOptionPane.DEFAULT_OPTION, 
						        JOptionPane.WARNING_MESSAGE, 
						        null, 
						        new String[]{"Back"}, // this is the array
						        "default");
						break;
					}
					break;
					
				}
				}

				}catch(ConnectException err) {
					JOptionPane.showOptionDialog(null, 
					        "Cannot connect to the server! ", 
					        "Warning", 
					        JOptionPane.DEFAULT_OPTION, 
					        JOptionPane.ERROR_MESSAGE, 
					        null, 
					        new String[]{"Back"}, // this is the array
					        "default");
					
				}catch(IOException err) {
					err.printStackTrace();
				}
				
	
			}
		});
		
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setFont(new Font("Arial", Font.PLAIN, 18));
		cancelBtn.setBounds(223, 141, 93, 23);
		contentPane.add(cancelBtn);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
	
	}
	
	public Socket getSocket() {
		return currentSocket;
	}
	
	public String getStatus() {
		return currentStatus;
	}
}
