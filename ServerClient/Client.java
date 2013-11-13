import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class InputListen extends Thread {
	int port;
	InetAddress IPAddress;
	int serverPort;
	final int waitTime = 500;
	public InputListen(int port, InetAddress IPAddress, int serverPort) {
		this.port = port;
		this.IPAddress = IPAddress;
		this.serverPort = serverPort;
	}

	public void run() {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
				System.in));
		MySendPacket sendPacket = null;

		byte[] sendData;
		int packetId = 0;
		while (true) {
			try {
				Client.sentence = inFromUser.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Client.lastReq = String.valueOf(packetId);
			if (Client.sentence.split(" ")[0].equals("login")) {
				String name = Client.sentence.split(" ")[1];
				List<String> params = new ArrayList<String>();
				params.add(0, String.valueOf(packetId));
				params.add(1, name);
				params.add(2, String.valueOf(port));
				sendPacket = new MySendPacket("login", params);
			} else if (Client.sentence.equals("ls")) {
				List<String> params = new ArrayList<String>();
				params.add(0, String.valueOf(packetId));
				params.add(1, String.valueOf(port));
				sendPacket = new MySendPacket("list", params);
			} else if (Client.sentence.split(" ")[0].equals("choose")) {
				String name = Client.sentence.split(" ")[1];
				List<String> params = new ArrayList<String>();
				params.add(0, String.valueOf(packetId));
				params.add(1, name);
				params.add(2, String.valueOf(port));
				sendPacket = new MySendPacket("choose", params);
			} else if (Client.sentence.split(" ")[0].equals("accept")) {
				String name = Client.sentence.split(" ")[1];
				String ack = "A";
				List<String> params = new ArrayList<String>();
				params.add(0, String.valueOf(packetId));
				params.add(1, name);
				params.add(2, ack);
				params.add(3, String.valueOf(port));
				sendPacket = new MySendPacket("ackchoose", params);
			} else if (Client.sentence.split(" ")[0].equals("deny")) {
				String name = Client.sentence.split(" ")[1];
				String ack = "D";
				List<String> params = new ArrayList<String>();
				params.add(0, String.valueOf(packetId));
				params.add(1, name);
				params.add(2, ack);
				params.add(3, String.valueOf(port));
				sendPacket = new MySendPacket("ackchoose", params);
			} else if (Client.sentence.split(" ")[0].equals("play")) {
				String number = Client.sentence.split(" ")[1];
				List<String> params = new ArrayList<String>();
				params.add(0, String.valueOf(packetId));
				params.add(1, number);
				params.add(2, String.valueOf(port));
				sendPacket = new MySendPacket("play", params);
				System.out.println(Client.localName + " " + number);
			} else if (Client.sentence.equals("logout")
					&& Client.localName != null) {
				List<String> params = new ArrayList<String>();
				params.add(0, String.valueOf(packetId));
				params.add(1, String.valueOf(port));
				sendPacket = new MySendPacket("logout", params);
			}
			if (sendPacket != null) {
				sendData = sendPacket.getData(sendPacket);
				while (!Client.lastAck.equals(String.valueOf(packetId))) {
					int number = sendData.length;
					byte[] data = new byte[4 + sendData.length];
					for (int i = 0; i < 4; ++i) {
						int shift = i << 3; // i * 8
						data[3 - i] = (byte) ((number & (0xff << shift)) >>> shift);
					}
					for (int i = 4; i < 4 + sendData.length; ++i) {
						data[i] = sendData[i - 4];
					}
					DatagramPacket packet = new DatagramPacket(data,
							data.length, IPAddress, serverPort);
					try {
						Client.clientSocket.send(packet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						sleep(waitTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			packetId++;
			if (Client.sentence.equals("logout")
					&& Client.localName != null) {
				break;
			}
		}
	}
}

class SockListen extends Thread {
	public void run() {
		byte[] receiveData;
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket receivedPacket = new DatagramPacket(data,
					data.length);
			try {
				Client.clientSocket.receive(receivedPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int len = 0;

			for (int i = 0; i < 4; ++i) {
				len |= (data[3 - i] & 0xff) << (i << 3);
			}

			receiveData = new byte[len];
			for (int i = 4; i < 4 + len; ++i) {
				receiveData[i - 4] = data[i];
			}
			MyReceivePackect receivePacket = new MyReceivePackect(receiveData);
			if (receivePacket.command.equals("ack")) {
				AckReceivePacketContent ackReceivePacketContent = (AckReceivePacketContent) receivePacket.content;
				Client.lastAck = ackReceivePacketContent.packetId;
				//System.out.println("ack " + UDPClient.lastAck);
			} else if (receivePacket.command.equals("acklogin")) {
				AckloginReceivePacketContent ackloginReceivePacketContent = (AckloginReceivePacketContent) receivePacket.content;
				if (ackloginReceivePacketContent.ack.equals("F")) {
					System.out.println("login fail "
							+ Client.sentence.split(" ")[1]);
				}
				if (ackloginReceivePacketContent.ack.equals("S")) {
					System.out.println("login success "
							+ Client.sentence.split(" ")[1]);
					Client.localName = Client.sentence.split(" ")[1];
				}
			} else if (receivePacket.command.equals("ackls")) {
				AcklsReceivePacketContent acklsReceivePacketContent = (AcklsReceivePacketContent) receivePacket.content;
				Iterator<ClientsStates> it = acklsReceivePacketContent.states
						.iterator();
				while (it.hasNext()) {
					ClientsStates clientsStates = it.next();
					System.out.println(clientsStates.name + " "
							+ clientsStates.state);
				}
				System.out.println("EOL");
			} else if (receivePacket.command.equals("request")) {
				RequestReceivePacketContent requestReceivePacketContent = (RequestReceivePacketContent) receivePacket.content;
				System.out.println("request from "
						+ requestReceivePacketContent.name);
			} else if (receivePacket.command.equals("ackchoose")) {
				AckchooseReceivePacketContent ackchooseReceivePacketContent = (AckchooseReceivePacketContent) receivePacket.content;
				if (ackchooseReceivePacketContent.ack.equals("A")) {
					System.out.println("request accepted by "
							+ ackchooseReceivePacketContent.name);
				} else if (ackchooseReceivePacketContent.ack.equals("D")) {
					System.out.println("request denied by "
							+ ackchooseReceivePacketContent.name);
				} else if (ackchooseReceivePacketContent.ack.equals("F")) {
					System.out.println("request to "
							+ ackchooseReceivePacketContent.name + " failed");
				}
			} else if (receivePacket.command.equals("play")) {
				PlayReceivePacketContent playReceivePacketContent = (PlayReceivePacketContent) receivePacket.content;
				System.out.println(playReceivePacketContent.playStates.state[0]
						+ " " + playReceivePacketContent.playStates.state[1]
						+ " " + playReceivePacketContent.playStates.state[2]);
				System.out.println(playReceivePacketContent.playStates.state[3]
						+ " " + playReceivePacketContent.playStates.state[4]
						+ " " + playReceivePacketContent.playStates.state[5]);
				System.out.println(playReceivePacketContent.playStates.state[6]
						+ " " + playReceivePacketContent.playStates.state[7]
						+ " " + playReceivePacketContent.playStates.state[8]);
			} else if (receivePacket.command.equals("ackplay")) {
				AckplayReceivePacketContent ackplayReceivePacketContent = (AckplayReceivePacketContent) receivePacket.content;
				if (ackplayReceivePacketContent.ack.equals("O")) {
					System.out.println("occupied");
				} else if (ackplayReceivePacketContent.ack.equals("T")) {
					System.out.println("not your turn");
				}
			} else if (receivePacket.command.equals("result")) {
				ResultReceivePacketContent resultReceivePacketContent = (ResultReceivePacketContent) receivePacket.content;
				if (resultReceivePacketContent.res.equals("W")) {
					System.out.println(Client.localName + " win");
				} else if (resultReceivePacketContent.res.equals("L")) {
					System.out.println(Client.localName + " lose");
				} else if (resultReceivePacketContent.res.equals("F")) {
					System.out.println(Client.localName + " fair");
				}
			}
			if (Client.sentence != null
					&& Client.sentence.equals("logout")
					&& Client.localName != null
					&& Client.lastReq.equals(Client.lastAck)) {
				Client.clientSocket.close();
				System.out.println(Client.localName + " logout");
				break;
			}
		}
	}
}

public class Client {
	static String sentence = null;
	static String localName = null;
	static DatagramSocket clientSocket = null;
	static String lastAck = "-1";
	static String lastReq = "-1";

	public static void main(String args[]) throws Exception {
		int port = Integer.parseInt(args[0]);
		InetAddress IPAddress = InetAddress.getByName(args[1]);
		int serverPort = Integer.parseInt(args[2]);
		clientSocket = new DatagramSocket(port);
		InputListen inputListen = new InputListen(port, IPAddress, serverPort);
		SockListen sockListen = new SockListen();
		inputListen.start();
		sockListen.start();
	}
}
