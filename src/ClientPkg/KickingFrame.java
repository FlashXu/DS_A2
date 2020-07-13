/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ClientPkg;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class KickingFrame extends JFrame {

	private JPanel contentPane;
	DataOutputStream output ;
	String chosenUser;

	public KickingFrame(DataOutputStream output, String[] currentUserList) {
		setTitle("Kick Someone");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.output = output;
		chosenUser  = currentUserList[0];
		
		JComboBox userList = new JComboBox(currentUserList);
		userList.setFont(new Font("Arial", Font.PLAIN, 18));
		userList.setBounds(96, 102, 229, 38);
		contentPane.add(userList);
		
		userList.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			   {
						chosenUser = (String) userList.getSelectedItem();					
			   }
			
		});
		
		JLabel lblNewLabel = new JLabel("Choose one user:");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		lblNewLabel.setBounds(96, 58, 169, 34);
		contentPane.add(lblNewLabel);
		
		JButton kickBtn = new JButton("Kick");
		kickBtn.setFont(new Font("Arial", Font.PLAIN, 18));
		kickBtn.setBounds(96, 175, 93, 23);
		contentPane.add(kickBtn);
		kickBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chosenUser!=null)
					try {
						output.writeUTF("KICK/" + chosenUser);
						System.out.println("Kick " + chosenUser);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				setVisible(false);
			}		
		});

		
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setFont(new Font("Arial", Font.PLAIN, 18));
		cancelBtn.setBounds(232, 175, 93, 23);
		contentPane.add(cancelBtn);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}		
		});
	}
}
