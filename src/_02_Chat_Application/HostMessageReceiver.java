package _02_Chat_Application;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class HostMessageReceiver implements Runnable{

	Host host;
	ServerSocket serverSocket;
	Socket connection;
	DataInputStream dis;
	JTextArea chatLog;
	private int index;
	
	HostMessageReceiver(Host host, ServerSocket serverSocket, Socket connection, JTextArea chatLog, int index) {
		this.host = host;
		this.serverSocket = serverSocket;
		this.connection = connection;
		this.chatLog = chatLog;
		this.index = index;
	}
	
	int getIndex() {
		return index;
	}
	
	void setIndex(int index) {
		this.index = index;
	}
	
	@Override
	public void run() {
		
		try {
			dis = new DataInputStream(connection.getInputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error: IO Exception");
			e.printStackTrace();
		}
		
		while (connection.isConnected()) {
			try {
				String message = dis.readUTF();
				chatLog.setText(chatLog.getText() + message);
				host.forwardMessage(message, connection);
			} catch (IOException e) {
				e.printStackTrace();
				if (!connection.isClosed()) {
					try {
						host.removeConnection(index);
						connection.close();
						serverSocket.close();
						if(host.connections.size() < 1) {
							host.window.setTitle("Chat (Host | Waiting For Connection)");
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "Error: Problem while Closing Connection");
					}
				}
				break;
			}
		}
	}
	
}
