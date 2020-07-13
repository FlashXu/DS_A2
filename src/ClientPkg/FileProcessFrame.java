/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ClientPkg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;

import javax.swing.JTextField;

public class FileProcessFrame extends JFrame {

	private JPanel contentPane;
	public String type;
	public DataOutputStream output ;
	public JTextField filePath;
	public JButton opBtn;
	public Container currentboard;
	
	public FileProcessFrame(Container currentboard, DataOutputStream output, String type) {
		switch(type) {
		case "Open":
			setTitle("Open File");
			break;
		case "Save":
			setTitle("Save File");
			break;
		case "Save As":
			setTitle("Save As File");
			break;
		}
	
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.output = output;
		this.currentboard = currentboard;
		this.type = type;

		
		JLabel lblNewLabel = new JLabel("File Path:");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		lblNewLabel.setBounds(96, 58, 169, 34);
		contentPane.add(lblNewLabel);
		
		filePath = new JTextField();
		filePath.setBounds(96, 112, 229, 21);
		contentPane.add(filePath);
		filePath.setColumns(10);

		if (type.equals("Open")) {
			opBtn = new JButton("Open");
		}else {
			opBtn = new JButton("Save");
		}
		opBtn.setFont(new Font("Arial", Font.PLAIN, 18));
		opBtn.setBounds(96, 164, 93, 23);
		contentPane.add(opBtn);
		opBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				String currentPath = filePath.getText();
				File imgFile = new File(currentPath);
				if (type.equals("Save") || type.equals("Save As")) {
					// Check currentPath validity.
					if(!currentPath.contains(".jpg") && !currentPath.contains(".png") && !currentPath.contains(".bmp")) {
						JOptionPane.showOptionDialog(null, 
						        "The file shall be save as jpg, png or bmp!", 
						        "Warning", 
						        JOptionPane.DEFAULT_OPTION, 
						        JOptionPane.WARNING_MESSAGE, 
						        null, 
						        new String[]{"Back"}, // this is the array
						        "default");
						return;
					}
					int pointIndex = currentPath.indexOf(".");
					if (pointIndex==0) {
						JOptionPane.showOptionDialog(null, 
						        "Please input the file name!", 
						        "Warning", 
						        JOptionPane.DEFAULT_OPTION, 
						        JOptionPane.WARNING_MESSAGE, 
						        null, 
						        new String[]{"Back"}, // this is the array
						        "default");
						return;
					}
					
					if (currentboard.getComponentCount() > 2) {
						DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
						Image drawnImg = currentArea.img;
						BufferedImage bimage = new BufferedImage(drawnImg.getWidth(null), drawnImg.getHeight(null), BufferedImage.TYPE_INT_ARGB);
						Graphics2D g = bimage.createGraphics(); 
						g.drawImage(drawnImg, 0, 0, null);
						g.dispose();
						ImageIO.write(bimage, "png", imgFile);
					}
					setVisible(false);
				}
				
				if(type.equals("Open")) {
					if(!imgFile.exists()) {
						JOptionPane.showOptionDialog(null, 
						        "The file doe not exist!", 
						        "Warning", 
						        JOptionPane.DEFAULT_OPTION, 
						        JOptionPane.WARNING_MESSAGE, 
						        null, 
						        new String[]{"Back"}, // this is the array
						        "default");
						return;
					}
					
					BufferedImage imgBuffer = ImageIO.read(imgFile);
					BufferedImage jpgBuffer = new BufferedImage(imgBuffer.getWidth(), imgBuffer.getHeight(), BufferedImage.TYPE_INT_RGB);
					 jpgBuffer.createGraphics().drawImage(imgBuffer, 0, 0, null);
					 ByteArrayOutputStream baos = new ByteArrayOutputStream();
					 ImageIO.write(jpgBuffer, "jpg", baos);
					 
					 File temp = new File("temp.jpg");
					 ImageIO.write(jpgBuffer, "jpg", temp);
					 long fileSize = temp.length();
					 byte[] sendingBuffer = new byte[1024*1024];
					 RandomAccessFile byteFile = new RandomAccessFile(temp,"r");
					 
					// Send img to server.
		     		output.writeUTF("OPEN_IMG/" + Long.toString(fileSize));

		     		int num;
					// While there are still bytes to send..
					while((num = byteFile.read(sendingBuffer)) > 0){
						output.write(Arrays.copyOf(sendingBuffer, num));
					}
					byteFile.close();
	
		     		//Open img locally.
		     		if (currentboard.getComponentCount() > 2) {
						DrawArea currentArea = (DrawArea) currentboard.getComponent(2);
						Color orginColor = currentArea.getG2().getColor();
						Graphics2D g2 = (Graphics2D) jpgBuffer.getGraphics();
						g2.setColor(orginColor);
						currentArea.loadImg(jpgBuffer);
						currentArea.loadG2(g2);
						currentArea.repaint();
						
					}
		     		setVisible(false);
				}

				}catch(IOException e1) {
					e1.printStackTrace();
				}
			}		
		});

		
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setFont(new Font("Arial", Font.PLAIN, 18));
		cancelBtn.setBounds(232, 164, 93, 23);
		contentPane.add(cancelBtn);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}		
		});
		
		if(type.equals("Save As")){
			String[] formats = {"jpg", "bmp", "png"};
			JComboBox fileFormat = new JComboBox(formats);
			fileFormat.setBounds(232, 67, 93, 23);
			contentPane.add(fileFormat);
			fileFormat.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String chosenFunc = (String) fileFormat.getSelectedItem();
					String currentPath = filePath.getText();
					int index = currentPath.length();
					for (int i = 0; i < currentPath.length(); i++) {
						index --;
						if (currentPath.charAt(currentPath.length() - 1 - i) == '.') {
							break;
						}
					}
					String SavaPath = currentPath.substring(0, index) + "." + chosenFunc;
					filePath.setText(SavaPath);	
				}

			});
			
			
		}
		
		
	}
}
