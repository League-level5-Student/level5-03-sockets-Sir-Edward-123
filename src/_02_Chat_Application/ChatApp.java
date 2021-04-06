package _02_Chat_Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/*
 * Using the Click_Chat example, write an application that allows a server computer to chat with a client computer.
 */

public class ChatApp implements ActionListener {
	JFrame window;
	JPanel panel;
	JButton hostButton;
	JButton clientButton;
	JButton exitButton;
	
	ChatApp(){
		setup();
	}
	
	void setup() {
		window = new JFrame("\"Discord\"");
		panel = new JPanel();
		hostButton = new JButton("Host a Chat");
		clientButton = new JButton("Connect to a Chat");
		exitButton = new JButton("Exit");
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		hostButton.addActionListener(this);
		clientButton.addActionListener(this);
		exitButton.addActionListener(this);
		panel.add(hostButton);
		panel.add(clientButton);
		panel.add(exitButton);
		window.add(panel);
		window.pack();
		window.setResizable(false);
		window.setVisible(true);
	}
	
	public static void main(String[] args) {
		new ChatApp();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == hostButton) {
			Thread hostThread = new Thread(() -> {
				new Host(0);
			});
			hostThread.start();
		} else if(ae.getSource() == clientButton) {
			Thread clientThread = new Thread(() -> {
				try {
					String ip = JOptionPane.showInputDialog("Enter IP address of host");
					int port = Integer.parseInt(JOptionPane.showInputDialog("Enter port to connect to"));
					new Client(ip, port);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error: Invalid IP/Port");
				}	
			});
			clientThread.start();
		} else if(ae.getSource() == exitButton) {
			System.exit(0);
		}
	}
}
