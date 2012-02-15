package couk.Adamki11s.Sockets;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import couk.Adamki11s.Cryptography.HashEncoder;
import couk.Adamki11s.Parser.ActionParser;
import couk.Adamki11s.RemoteAdmin.RemoteAdmin;

public class RAServer extends Thread {

	final int port;

	private boolean active = false;

	private final HashEncoder encoder = new HashEncoder();

	final String password;

	InetAddress netAddress = null;

	byte[] receive_data = new byte[8048];

	public RAServer(int port, String password) {
		this.port = port;
		this.password = this.encoder.computeSHA2_256BitHash(password);
	}

	@Override
	public void run() {
		this.recieveData();
	}

	public void recieveData() {
		try {

			DatagramSocket server_socket = new DatagramSocket(5000);

			System.out.println("UDPServer Waiting for client on port 5000");

			while (this.isActive()) {

				DatagramPacket receive_packet = new DatagramPacket(receive_data, receive_data.length);

				server_socket.receive(receive_packet);

				String data = new String(receive_data, 0, receive_packet.getLength());

				InetAddress IPAddress = receive_packet.getAddress();

				boolean auth = true;

				if (this.netAddress == null) {
					if (data.equalsIgnoreCase(this.password)) {
						RemoteAdmin.logInfo("Client " + IPAddress.toString() + " connected.");
						this.sendData("AUTH:1", IPAddress);
						this.netAddress = IPAddress;
					} else {
						auth = false;
						RemoteAdmin.logInfo("Client " + IPAddress.toString() + " connection refused (Invalid Password).");
						this.sendData("AUTH:0", IPAddress);
					}
				} else {
					if (!IPAddress.toString().equalsIgnoreCase(this.netAddress.toString())) {
						auth = false;
						RemoteAdmin.logInfo("Client " + IPAddress.toString() + " connection refused (Session Active).");
						this.sendData("AUTH:2", IPAddress);
					} else if (data.equalsIgnoreCase("QUIT")) {
						auth = false;
						RemoteAdmin.logInfo("Client " + IPAddress.toString() + " disconnected.");
						this.netAddress = null;
						this.sendData("AUTH:3", IPAddress);
					}
				}

				if (auth) {
					ActionParser.parseAction(data, this);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sendData(String send, InetAddress ip) {
		try {
			DatagramSocket client_socket = new DatagramSocket();
			DatagramPacket send_packet = new DatagramPacket(send.getBytes(), send.getBytes().length, ip, 5100);
			client_socket.send(send_packet);
			client_socket.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return this.active;
	}

}
