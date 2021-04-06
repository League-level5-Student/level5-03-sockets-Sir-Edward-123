package _02_Chat_Application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class Client implements ActionListener {
	String ip;
	int port;
	Socket connection;
	DataInputStream dis;
	DataOutputStream dos;

	JFrame window;
	JPanel panel;
	JButton sendMessageButton;
	JButton exitButton;
	JLabel info;
	JTextArea chatLog;
	JScrollPane scrollPane;

	final int WIDTH = 400;
	final int HEIGHT = 600;
	
	String name;

	Client(String ip, int port, String name) {
		this.ip = ip;
		this.port = port;
		this.name = name;
		setup();
	}

	void setup() {
		try {
			connection = new Socket(ip, port);

			window = new JFrame("Chat (Client)");
			panel = new JPanel();
			sendMessageButton = new JButton("Send Message");
			exitButton = new JButton("Exit");
			info = new JLabel();
			chatLog = new JTextArea((HEIGHT - 20) / 18, (WIDTH - 40) / 12);
			scrollPane = new JScrollPane(chatLog);

			sendMessageButton.addActionListener(this);
			exitButton.addActionListener(this);
			info.setText("IP: " + ip + " | Port: " + port);
			chatLog.setEditable(false);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			panel.add(info);
			panel.add(sendMessageButton);
			panel.add(exitButton);
			panel.add(scrollPane);
			window.add(panel);
			window.setSize(new Dimension(WIDTH, HEIGHT));
			window.setResizable(false);
			window.setVisible(true);

		} catch (UnknownHostException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: Unknown Host");
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: IO Exception");
		}

		try {
			dis = new DataInputStream(connection.getInputStream());
			dos = new DataOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: IO Exception");
			closeConnection();
		}

		while (connection.isConnected()) {
			try {
				String message = dis.readUTF();
				chatLog.setText(chatLog.getText() + message);
			} catch (IOException e) {
				e.printStackTrace();
				if (!connection.isClosed()) {
					JOptionPane.showMessageDialog(null, "Lost Connection to Server. Closing Connection...");
					closeConnection();
				}
				break;
			}
		}

	}

	void closeConnection() {
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: Problem Closing Connection");
		}
		window.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == sendMessageButton) {
			String message = JOptionPane.showInputDialog("Send Message");
			try {
				dos.writeUTF("\n\n  " + name + "(Client): " + message);
				chatLog.setText(chatLog.getText() + "\n\n  " + name + "(Client | You): " + message);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: IO Exception while Sending Message");
			}
		} else if (ae.getSource() == exitButton) {
			closeConnection();
		}
	}
}
