package _02_Chat_Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
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
	
	Host host;
	Client client;
	
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
		window.setVisible(true);
	}
	
	public static void main(String[] args) {
		new ChatApp();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == hostButton) {
			
			window.setVisible(false);
		} else if(ae.getSource() == clientButton) {
			
			window.setVisible(false);
		} else if(ae.getSource() == exitButton) {
			System.exit(0);
		}
	}
}
