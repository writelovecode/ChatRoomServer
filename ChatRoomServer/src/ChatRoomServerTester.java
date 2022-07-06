import javax.swing.JFrame;

public class ChatRoomServerTester {

	public static void main(String[] args) {
		ChatRoomServer sally =  new ChatRoomServer();
		sally.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sally.startRunning();
	}

}
