import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

	List<ClientHandler> con = Collections.synchronizedList(new ArrayList<>());

	ServerSocket server;

	Server()  {
		try {
			this.server = new ServerSocket(1111);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	class ClientHandler extends Thread {
		String userName;
		Socket client;
		String message;
		InputStream in;
		OutputStream out;

		public ClientHandler(Socket client) {
			this.client = client;

		}

		public void run() {
			try {
				in = client.getInputStream();
				out = client.getOutputStream();

				byte[] q = new byte[1024];
				int bR = in.read(q);
				userName = new String(q, 0, bR);
				while (true) {
					byte[] b = new byte[1024];
					int bytesRecieve = in.read(b);
					message = new String(b, 0, bytesRecieve);
					if (message.equals("exit")) {
						String msg = "exit";
						out.write(msg.getBytes());
						in.close();
						out.close();
						client.close();
						for (int i = 0; i < con.size(); i++) {
							if (userName.equals(con.get(i).userName)) {
								con.remove(i);
								return;
							}
						}
					}

					Date d = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
					String date = sdf.format(d);
					message = date + ": " + userName + ": " + message;

					synchronized (con) {
						Iterator<ClientHandler> iter = con.iterator();
						while (iter.hasNext()) {
							((ClientHandler) iter.next()).out.write(message
									.getBytes());
						}
					}
				}

			} catch (IOException ex) {
				Logger.getLogger(ClientHandler.class.getName()).log(
						Level.SEVERE, null, ex);
			} finally {
				try {
					in.close();
					out.close();
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}
	}

	public static void main(String[] args) throws IOException {
		
		Server go = new Server();
		while (true) {
			Socket client = go.server.accept();

			ClientHandler ch = go.new ClientHandler(client);		
			go.con.add(ch);
			ch.start();
		}
	}

}
