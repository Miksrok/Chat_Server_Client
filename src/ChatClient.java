import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatClient {
	private Frame mainFrame;
	private Button connect, send, disconnect;
	private Panel northPan, centerPan, southPan;
	private TextArea textArea;
	private TextField userNameField, messageField;
	String name;
	Socket so;
	InputStream in;
	OutputStream out;

	ChatClient() {
		mainFrame = new Frame("Chat Client");
		connect = new Button("add user");
		disconnect = new Button("disconnect");
		send = new Button("send");
		northPan = new Panel();
		centerPan = new Panel();
		southPan = new Panel();
		textArea = new TextArea();
		userNameField = new TextField();
		messageField = new TextField();
	}

	public void lF() {
		northPan.setLayout(new GridLayout(0, 3));
		southPan.setLayout(new BorderLayout());
		mainFrame.add(northPan, BorderLayout.NORTH);
		mainFrame.add(centerPan, BorderLayout.CENTER);
		mainFrame.add(southPan, BorderLayout.SOUTH);
		northPan.add(userNameField);
		userNameField.setEditable(true);
		northPan.add(connect);
		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				try {
					
					so = new Socket("127.0.0.1", 1111);
					in = so.getInputStream();
					out = so.getOutputStream();

					name = userNameField.getText();
					out.write(name.getBytes());

					userNameField.setEditable(false);
					messageField.setEditable(true);
					send.setEnabled(true);
					disconnect.setEnabled(true);
					connect.setEnabled(false);
					
					MyThread myThread=new MyThread();
					myThread.setDaemon(true);
					myThread.start();
					
					

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		northPan.add(disconnect);
		disconnect.setEnabled(false);
		disconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String message = "exit";
				try {
					out.write(message.getBytes());
					messageField.setText("");
					connect.setEnabled(true);
						
						in.close();
						out.close();
						so.close();
						userNameField.setEditable(true);
						disconnect.setEnabled(false);
						messageField.setEditable(false);
						send.setEnabled(false);
						
				} catch (IOException e) {
					e.printStackTrace();
					
				}
			}
		});

		centerPan.add(textArea);
		textArea.setEditable(false);

		southPan.add(messageField, BorderLayout.CENTER);
		messageField.setEditable(false);

		southPan.add(send, BorderLayout.EAST);
		send.setEnabled(false);
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String message = messageField.getText();
				try {
					out.write(message.getBytes());
					messageField.setText("");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		mainFrame.addWindowListener(new WindowClosingQ());
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	class MyThread extends Thread{
		
		public void run() {

			try {

				while (true) {
					byte[] b = new byte[1024];
					int bytesRecieve = in.read(b);
					String answer = new String(b, 0, bytesRecieve);
					if (answer.equals("exit")){	
						return;
					}
					textArea.append(answer + "\n");
				}

			} catch (IOException ex) {
				Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE,
						null, ex);
				
			}

		}
	}
	

	class WindowClosingQ extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			mainFrame.dispose();
		}
	}

	public static void main(String[] args) {
		ChatClient gui = new ChatClient();
		gui.lF();
		//gui.setDaemon(true);
		

	}

}
