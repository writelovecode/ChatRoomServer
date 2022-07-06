import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ChatRoomServer extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private ServerSocket server;
	private Socket connection;
	
	//constructor
	public ChatRoomServer() {
		super("Instant Messenger - Server");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sendMessage(event.getActionCommand());
						userText.setText("");
						
					}

				}	
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300, 150);
		setVisible(true);
	}
	
	
	//setup and run the server
	public void startRunning() {
		try {
			server = new ServerSocket(6789,100);
			
			while(true) {
				try {
					
					waitForConnection();
					setupStreams();
					whileChatting();
					
				}catch(EOFException eofException) {
					
					showMessage("\n Server ended the connection! ");
					
				}finally {
					
					closeCrap();
					
				}
			}
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	//wait for connection, then display connection information
	private void waitForConnection() throws IOException{
		showMessage(" Waiting for someone to connect... \n");
		connection =  server.accept();
		showMessage(" Now conncted to "+ connection.getInetAddress().getHostName());
	}
	
	//get stream to send and receive data
	private void setupStreams() throws IOException {
		outputStream = new ObjectOutputStream(connection.getOutputStream());
		outputStream.flush();
		inputStream = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now set up! \n");
	}
	
	//during the chat conversation
	private void whileChatting() throws IOException{
		String message = " You are now connected! ";
		sendMessage(message);
		ableToType(true);
		do {
			//have a conversation
			try {
				message = (String) inputStream.readObject();
				showMessage("\n"+message);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n idk(i don't know) wtf that user sent!");
			}
		}while(!message.equals("CLIENT - END"));
	}
	
	//Close streams and Sockets after you are done chatting
	//Housekeeping method
	private void closeCrap() {
		showMessage("\n Closing Connections...\n");
		ableToType(false);
		try {
			outputStream.close();
			inputStream.close();
			connection.close();
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	//send a message to client
	private void sendMessage(String message){
		try {
			outputStream.writeObject("SERVER - "+message);
			outputStream.flush();
			showMessage("\nSERVER - "+message);
		}catch(IOException ioException) {
			chatWindow.append("\n ERROR: Dude I cant send that message!");
		}
	}
	
	//updates chatWindow
	private void showMessage(final String text) {
		//update part of the GUI; chatWindow
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						chatWindow.append(text);
					}
				}
		);
	}
	
	//let the user type stuff into their box
	private void ableToType(final boolean tof) {
		
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						userText.setEditable(tof);
					}
				}
		);
	}
	
}
