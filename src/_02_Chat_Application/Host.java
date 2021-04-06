package _02_Chat_Application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class Host implements ActionListener {
	private int port;
	final int SERVER_TIMEOUT = 60000;
	ServerSocket serverSocket;
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

	Host(int port) {
		this.port = port;
		setup();
	}

	void setup() {
		try {
			serverSocket = new ServerSocket(port);
			window = new JFrame("Chat (Host | Waiting for Connection)");
			panel = new JPanel();
			sendMessageButton = new JButton("Send Message");
			exitButton = new JButton("Exit");
			info = new JLabel();
			chatLog = new JTextArea((HEIGHT - 20) / 18, (WIDTH - 40) / 12);
			scrollPane = new JScrollPane(chatLog);

			sendMessageButton.addActionListener(this);
			exitButton.addActionListener(this);
			info.setText("IP: " + getIPAddress() + " | Port: " + getPort());
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			chatLog.setEditable(false);
			panel.add(info);
			panel.add(sendMessageButton);
			panel.add(exitButton);
			panel.add(scrollPane);
			window.add(panel);
			window.setSize(new Dimension(WIDTH, HEIGHT));
			window.setResizable(false);
			window.setVisible(true);

			serverSocket.setSoTimeout(SERVER_TIMEOUT);
			connection = serverSocket.accept();
			window.setTitle("Chat (Host | Connected)");

			dis = new DataInputStream(connection.getInputStream());
			dos = new DataOutputStream(connection.getOutputStream());

		} catch (SocketTimeoutException e) {
			try {
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			window.dispose();
			JOptionPane.showMessageDialog(null, "Error: Socket Timeout Exception");
		} catch (IllegalArgumentException e) {
			window.dispose();
			JOptionPane.showMessageDialog(null, "Error: Invalid Port Number");
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Connection Waiting Cancelled");
		}
		
		while (connection.isConnected()) {
			System.out.println("huh");
			try {
				String message = dis.readUTF();
				chatLog.setText(chatLog.getText() + "\n\n  Client: " + message);
			} catch (IOException e) {
				e.printStackTrace();
				if (!connection.isClosed()) {
					JOptionPane.showMessageDialog(null, "Lost Connection to Client. Closing Connection...");
					closeServer();
				}
				break;
			}
		}
	}

	public String getIPAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: Unknown Host");
			return "Error";
		}
	}

	public int getPort() {
		return serverSocket.getLocalPort();
	}

	void closeServer() {
		try {
			if (connection != null) {
				connection.close();
			}
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: Problem Closing Socket");
		}
		window.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == sendMessageButton) {
			if (connection != null) {
				String message = JOptionPane.showInputDialog("Send Message");
				try {
					dos.writeUTF(message);
					chatLog.setText(chatLog.getText() + "\n\n  Host(You): " + message);
					dos.flush();
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error: IO Exception while Sending Message");
				}
			} else {
				JOptionPane.showMessageDialog(null, "No Client Connection");
			}
		} else if (ae.getSource() == exitButton) {
			closeServer();
		}
	}
}
