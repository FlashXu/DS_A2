/****************************************************************************************
 COMP90015: Distributed Systems - Assignment 2
 Name: Yichao Xu
 Login: YICHAOX
 Student ID: 1045184  
*****************************************************************************************/
package ClientPkg;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class ColorPanel extends JFrame {
	
	private JPanel contentPane;
	public Color currentColor;

	/**
	 * Launch the application.
	 */
	public static void newScreen(JButton g2Color, Graphics2D g2) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ColorPanel frame = new ColorPanel(g2Color, g2);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ColorPanel(JButton g2Color, Graphics2D g2) {
		setTitle("Color Panel");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 544, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton ccolorBtn = new JButton("");
		ccolorBtn.setBackground(g2Color.getBackground());
		ccolorBtn.setBounds(215, 59, 93, 23);
		contentPane.add(ccolorBtn);
		
		JButton blackBtn = new JButton("");
		blackBtn.setBackground(new Color(0, 0, 0));
		blackBtn.setBounds(78, 106, 93, 23);
		contentPane.add(blackBtn);
		blackBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(Color.BLACK);
			}
		});
		
		JButton greyBtn = new JButton("");
		greyBtn.setBackground(Color.GRAY);
		greyBtn.setBounds(171, 106, 93, 23);
		contentPane.add(greyBtn);
		greyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(Color.GRAY);
			}
		});
		
		JButton whiteBtn = new JButton("");
		whiteBtn.setBackground(Color.WHITE);
		whiteBtn.setBounds(355, 106, 93, 23);
		contentPane.add(whiteBtn);
		whiteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(Color.WHITE);
			}
		});
		
		JButton lgreyBtn = new JButton("");
		lgreyBtn.setBackground(Color.LIGHT_GRAY);
		lgreyBtn.setBounds(263, 106, 93, 23);
		contentPane.add(lgreyBtn);
		lgreyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(Color.LIGHT_GRAY);
			}
		});
		
		JButton redBtn = new JButton("");
		redBtn.setBackground(Color.RED);
		redBtn.setBounds(78, 128, 93, 23);
		contentPane.add(redBtn);
		redBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(Color.RED);
			}
		});
		
		JButton magentaBtn = new JButton("");
		magentaBtn.setBackground(Color.MAGENTA);
		magentaBtn.setBounds(171, 128, 93, 23);
		contentPane.add(magentaBtn);
		magentaBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(Color.MAGENTA);
			}
		});
		
		JButton pinkBtn = new JButton("");
		pinkBtn.setBackground(Color.PINK);
		pinkBtn.setBounds(355, 128, 93, 23);
		contentPane.add(pinkBtn);
		pinkBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(Color.PINK);
			}
		});
		
		JButton lpinkBtn = new JButton("");
		lpinkBtn.setBackground(new Color(221, 160, 221));
		lpinkBtn.setBounds(263, 128, 93, 23);
		contentPane.add(lpinkBtn);
		lpinkBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(new Color(221, 160, 221));
			}
		});
		
		JButton horangeBtn = new JButton("");
		horangeBtn.setBackground(new Color(255, 99, 71));
		horangeBtn.setBounds(78, 150, 93, 23);
		contentPane.add(horangeBtn);
		horangeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(new Color(255, 99, 71));
			}
		});
		
		JButton orangeBtn = new JButton("");
		orangeBtn.setBackground(new Color(255, 165, 0));
		orangeBtn.setBounds(171, 150, 93, 23);
		contentPane.add(orangeBtn);
		orangeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(new Color(255, 165, 0));
			}
		});
		
		JButton yellowBtn = new JButton("");
		yellowBtn.setBackground(Color.YELLOW);
		yellowBtn.setBounds(263, 150, 93, 23);
		contentPane.add(yellowBtn);
		yellowBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(Color.YELLOW);
			}
		});
		
		JButton lyellowBtn = new JButton("");
		lyellowBtn.setBackground(new Color(240, 230, 140));
		lyellowBtn.setBounds(355, 150, 93, 23);
		contentPane.add(lyellowBtn);
		lyellowBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(new Color(240, 230, 140));
			}
		});
		
		JButton blueBtn = new JButton("");
		blueBtn.setBackground(Color.BLUE);
		blueBtn.setBounds(78, 171, 93, 23);
		contentPane.add(blueBtn);
		blueBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(Color.BLUE);
			}
		});
		
		JButton cyanBtn = new JButton("");
		cyanBtn.setBackground(Color.CYAN);
		cyanBtn.setBounds(171, 171, 93, 23);
		contentPane.add(cyanBtn);
		cyanBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(Color.CYAN);
			}
		});
		
		JButton greenBtn = new JButton("");
		greenBtn.setBackground(Color.GREEN);
		greenBtn.setBounds(263, 171, 93, 23);
		contentPane.add(greenBtn);
		greenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(Color.GREEN);
			}
		});
		
		JButton lgreenBtn = new JButton("");
		lgreenBtn.setBackground(new Color(60, 179, 113));
		lgreenBtn.setBounds(355, 171, 93, 23);
		contentPane.add(lgreenBtn);
		lgreenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccolorBtn.setBackground(new Color(60, 179, 113));
			}
		});
		
		
		JButton confirmBtn = new JButton("OK");
		confirmBtn.setFont(new Font("Arial", Font.PLAIN, 18));
		confirmBtn.setBounds(122, 243, 93, 23);
		contentPane.add(confirmBtn);
		confirmBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentColor = ccolorBtn.getBackground();
				g2Color.setBackground(currentColor);
				if (g2!=null)g2.setColor(currentColor);
				setVisible(false);
				
			}
		});
		
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setFont(new Font("Arial", Font.PLAIN, 18));
		cancelBtn.setBounds(316, 246, 93, 23);
		contentPane.add(cancelBtn);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				
			}
		});
		
		JLabel lblNewLabel = new JLabel("Current Color:");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		lblNewLabel.setBounds(78, 52, 147, 30);
		contentPane.add(lblNewLabel);
		

		
		
	}
}
