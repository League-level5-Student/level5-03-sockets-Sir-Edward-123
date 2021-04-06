package _02_Chat_Application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class Host implements ActionListener {
	final int SERVER_TIMEOUT = 60000;
	ArrayList<ServerSocket> serverSockets;
	ArrayList<Socket> connections;
	ArrayList<DataOutputStream> dos = new ArrayList<DataOutputStream>();
	ArrayList<HostMessageReceiver> hmr = new ArrayList<HostMessageReceiver>();

	JFrame window;
	JPanel panel;
	JButton portsButton;
	JButton sendMessageButton;
	JButton exitButton;
	JLabel info;
	JTextArea chatLog;
	JScrollPane scrollPane;

	final int WIDTH = 400;
	final int HEIGHT = 600;

	String name;

	Host(String name) {
		this.name = name;
		setup();
	}

	void setup() {
		try {
			serverSockets = new ArrayList<ServerSocket>();
			connections = new ArrayList<Socket>();
			window = new JFrame("Chat (Host | Waiting for Connection)");
			panel = new JPanel();
			portsButton = new JButton("Ports");
			sendMessageButton = new JButton("Send Message");
			exitButton = new JButton("Exit");
			info = new JLabel();
			chatLog = new JTextArea((HEIGHT - 20) / 18, (WIDTH - 40) / 12);
			scrollPane = new JScrollPane(chatLog);

			portsButton.addActionListener(this);
			sendMessageButton.addActionListener(this);
			exitButton.addActionListener(this);
			info.setText("IP: " + getIPAddress());
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			chatLog.setEditable(false);
			panel.add(info);
			panel.add(portsButton);
			panel.add(sendMessageButton);
			panel.add(exitButton);
			panel.add(scrollPane);
			window.add(panel);
			window.setSize(new Dimension(WIDTH, HEIGHT));
			window.setResizable(false);
			window.setVisible(true);

			waitForConnection();

		} catch (IllegalArgumentException e) {
			window.dispose();
			JOptionPane.showMessageDialog(null, "Error: Invalid Port Number");
		} catch (IOException e) {
			e.printStackTrace();
			if (connections.size() == 0) {
				JOptionPane.showMessageDialog(null, "Connection Waiting Cancelled");
			}
		}
	}

	public void waitForConnection() throws IOException {
		while (true) {
			System.out.println(connections.size());
			if (connections.size() < 4) {
				ServerSocket newServerSocket = new ServerSocket(0);
				serverSockets.add(newServerSocket);
				Socket newConnection = newServerSocket.accept();
				window.setTitle("Chat (Host | Connected)");
				connections.add(newConnection);
				dos.add(new DataOutputStream(connections.get(connections.size() - 1).getOutputStream()));
				hmr.add(new HostMessageReceiver(this, newServerSocket, newConnection, chatLog, connections.size() - 1));
				new Thread(() -> {
					hmr.get(connections.size() - 1).run();
				}).start();
			}
		}
	}

	public void forwardMessage(String message, Socket skip) {
		for (int i = 0; i < connections.size(); i++) {
			try {
				if(!connections.get(i).equals(skip)) {
					dos.get(i).writeUTF(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: IO Exception");
			}
		}
	}

	public void removeConnection(int index) {
		serverSockets.remove(index);
		connections.remove(index);
		dos.remove(index);
		hmr.remove(index);
		for (int i = index; i < connections.size(); i++) {
			hmr.get(i).setIndex(hmr.get(i).getIndex() - 1);
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

	public String getPorts() {
		String ports = "Max Clients: 4\n\nAvailable Ports: ";
		for (int i = 0; i < serverSockets.size(); i++) {
			ports += ("\n" + serverSockets.get(i).getLocalPort());
		}
		return ports;
	}

	void closeServer() {
		try {
			for (int i = 0; i < connections.size(); i++) {
				if (connections.get(i) != null) {
					connections.get(i).close();
					serverSockets.get(i).close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: Problem Closing Socket");
		}
		window.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == sendMessageButton) {
			if (!connections.isEmpty()) {
				String message = JOptionPane.showInputDialog("Send Message");
				try {
					for (int i = 0; i < connections.size(); i++) {
						dos.get(i).writeUTF("\n\n  " + name + "(Host): " + message);
						dos.get(i).flush();
					}
					chatLog.setText(chatLog.getText() + "\n\n  " + name + "(Host | You): " + message);
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error: IO Exception while Sending Message");
				}
			} else {
				JOptionPane.showMessageDialog(null, "No Client Connection");
			}
		} else if (ae.getSource() == exitButton) {
			closeServer();
		} else if (ae.getSource() == portsButton) {
			JOptionPane.showMessageDialog(null, getPorts());
		}
	}
}
