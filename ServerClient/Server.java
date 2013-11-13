import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Server {
	public static boolean isLegal(GameStates gameState, int number) {
		if (number >= 0 && number < 9 && gameState.state[number].equals("-"))
			return true;
		else
			return false;
	}

	public static String isOver(GameStates gameState, String player) {
		for (int i = 0; i < 3; i++) {
			if (gameState.state[i * 3].equals("1")
					&& gameState.state[i * 3 + 1].equals("1")
					&& gameState.state[i * 3 + 2].equals("1")) {
				if (player.equals("1")) {
					return "W";
				} else {
					return "L";
				}
			}

			if (gameState.state[i * 3].equals("2")
					&& gameState.state[i * 3 + 1].equals("2")
					&& gameState.state[i * 3 + 2].equals("2")) {
				if (player.equals("2")) {
					return "W";
				} else {
					return "L";
				}
			}

			if (gameState.state[i].equals("1")
					&& gameState.state[i + 3].equals("1")
					&& gameState.state[i + 6].equals("1")) {
				if (player.equals("1")) {
					return "W";
				} else {
					return "L";
				}
			}
			if (gameState.state[i].equals("2")
					&& gameState.state[i + 3].equals("2")
					&& gameState.state[i + 6].equals("2")) {
				if (player.equals("2")) {
					return "W";
				} else {
					return "L";
				}
			}
		}
		if ((gameState.state[0].equals("1") && gameState.state[4].equals("1") && gameState.state[8]
				.equals("1"))
				|| (gameState.state[2].equals("1")
						&& gameState.state[4].equals("1") && gameState.state[6]
							.equals("1"))) {
			if (player.equals("1")) {
				return "W";
			} else {
				return "L";
			}
		}

		if ((gameState.state[0].equals("2") && gameState.state[4].equals("2") && gameState.state[8]
				.equals("2"))
				|| (gameState.state[2].equals("2")
						&& gameState.state[4].equals("2") && gameState.state[6]
							.equals("2"))) {
			if (player.equals("2")) {
				return "W";
			} else {
				return "L";
			}
		}
		for (int i = 0; i < 9; i++)
			if (gameState.state[i].equals("-"))
				return "";
		return "F";
	}

	public static void main(String args[]) throws Exception {
		Comparator<ClientsStates> comparator = new Comparator<ClientsStates>() {
			public int compare(ClientsStates c1, ClientsStates c2) {
				return c1.name.compareTo(c2.name);
			}
		};
		List<GameStates> gameStates = new ArrayList<GameStates>();
		DatagramSocket serverSocket = new DatagramSocket(4110);
		List<ClientsStates> clientsList = new ArrayList<ClientsStates>();
		HashMap<String, String> names = new HashMap<String, String>();
		byte[] receiveData;
		byte[] sendData;
		String packetId = null;
		String lastAck = "-1";
		int lastPort = -1;
		while (true) {
			sendData = null;
			byte[] data = new byte[1024];
			DatagramPacket receivedPacket = new DatagramPacket(data,
					data.length);
			serverSocket.receive(receivedPacket);

			int len = 0;

			for (int i = 0; i < 4; ++i) {
				len |= (data[3 - i] & 0xff) << (i << 3);
			}

			receiveData = new byte[len];
			for (int i = 4; i < 4 + len; ++i) {
				receiveData[i - 4] = data[i];
			}
			InetAddress IPAddress = InetAddress.getByName("localhost");
			int port = 0;
			int sourcePort = 0;
			MySendPacket sendPacket = new MySendPacket(receiveData);
			MyReceivePackect receivePacket = null;
			if (sendPacket.command.equals("login")) {
				InetAddress Address = receivedPacket.getAddress();
				LoginSendPacketContent loginSendPacketContent = (LoginSendPacketContent) sendPacket.content;
				packetId = loginSendPacketContent.packetId;
				port = Integer.parseInt(loginSendPacketContent.port);
				sourcePort = Integer.parseInt(loginSendPacketContent.port);
				System.out.println("RECEIVED: "
						+ loginSendPacketContent.packetId + " "
						+ loginSendPacketContent.port + " "
						+ loginSendPacketContent.name + " login");
				String ack;
				int sameIPClientsCount=0;
				ClientsStates clientsStates = null;
				Iterator<ClientsStates> it = clientsList.iterator();
				while (it.hasNext()) {
					clientsStates = it.next();
					if (clientsStates.IPAddress.equals(Address
							.toString())) {
						sameIPClientsCount++;
					}
				}
				if (sameIPClientsCount == 5) {
					ack = "F";
				} else {
					if (names.containsValue(loginSendPacketContent.name)||names.containsKey(loginSendPacketContent.port)) {
						ack = "F";
					} else {
						ack = "S";
						names.put(loginSendPacketContent.port,
								loginSendPacketContent.name);
						clientsList.add(new ClientsStates(
								loginSendPacketContent.name, Address
										.toString(),
								loginSendPacketContent.port, "free", ""));
						Collections.sort(clientsList, comparator);
					}
				}
				List<String> params = new ArrayList<String>();
				params.add(0, ack);
				receivePacket = new MyReceivePackect("acklogin", params, null,
						null);
			} else if (sendPacket.command.equals("list")) {
				ListSendPacketContent listSendPacketContent = (ListSendPacketContent) sendPacket.content;
				port = Integer.parseInt(listSendPacketContent.port);
				sourcePort = Integer.parseInt(listSendPacketContent.port);
				packetId = listSendPacketContent.packetId;
				System.out.println("RECEIVED: "
						+ listSendPacketContent.packetId + " "
						+ names.get(String.valueOf(port)) + " list");
				receivePacket = new MyReceivePackect("ackls", null,
						clientsList, null);
			} else if (sendPacket.command.equals("choose")) {
				ChooseSendPacketContent chooseSendPacketContent = (ChooseSendPacketContent) sendPacket.content;
				packetId = chooseSendPacketContent.packetId;
				port = Integer.parseInt(chooseSendPacketContent.port);
				sourcePort = Integer.parseInt(chooseSendPacketContent.port);
				System.out.println("RECEIVED: "
						+ chooseSendPacketContent.packetId + " "
						+ names.get(String.valueOf(port)) + " choose "
						+ chooseSendPacketContent.name);
				String name = names.get(String.valueOf(port));
				List<String> params = new ArrayList<String>();

				String state1 = "";
				String state2 = "";
				ClientsStates clientsStates = null;
				Iterator<ClientsStates> it = clientsList.iterator();
				while (it.hasNext()) {
					clientsStates = it.next();
					if (clientsStates.name.equals(chooseSendPacketContent.name)) {
						state1 = clientsStates.state;
						port = Integer.parseInt(clientsStates.ports);
					} else if (clientsStates.name.equals(name)) {
						state2 = clientsStates.state;
					}
				}
				if (names.containsValue(chooseSendPacketContent.name)
						&& (!names.equals(chooseSendPacketContent.name))
						&& state1.equals("free") && state2.equals("free")) {
					params.add(0, name);
					receivePacket = new MyReceivePackect("request", params,
							null, null);
					it = clientsList.iterator();
					while (it.hasNext()) {
						clientsStates = it.next();
						if (clientsStates.name
								.equals(chooseSendPacketContent.name)) {
							clientsStates.state = "decision";
							clientsStates.opponent = name;
						} else if (clientsStates.name.equals(name)) {
							clientsStates.state = "decision";
							clientsStates.opponent = chooseSendPacketContent.name;
						}
					}
				} else {
					name = chooseSendPacketContent.name;
					String ack = "F";
					params = new ArrayList<String>();
					params.add(0, name);
					params.add(1, ack);
					port = Integer.parseInt(chooseSendPacketContent.port);
					receivePacket = new MyReceivePackect("ackchoose", params,
							null, null);
				}
			} else if (sendPacket.command.equals("ackchoose")) {
				AckchooseSendPacketContent ackchooseSendPacketContent = (AckchooseSendPacketContent) sendPacket.content;
				packetId = ackchooseSendPacketContent.packetId;
				port = Integer.parseInt(ackchooseSendPacketContent.port);
				sourcePort = Integer.parseInt(ackchooseSendPacketContent.port);
				System.out.println("RECEIVED: "
						+ ackchooseSendPacketContent.packetId + " "
						+ names.get(String.valueOf(port)) + " ackchoose "
						+ ackchooseSendPacketContent.name + " "
						+ ackchooseSendPacketContent.ack);
				String name = names.get(String.valueOf(port));
				String ack = ackchooseSendPacketContent.ack;
				List<String> params = new ArrayList<String>();
				ClientsStates clientsStates = null;
				Iterator<ClientsStates> it = clientsList.iterator();
				while (it.hasNext()) {
					clientsStates = it.next();
					if (clientsStates.name
							.equals(ackchooseSendPacketContent.name)) {
						port = Integer.parseInt(clientsStates.ports);
						break;
					}
				}
				if (clientsStates.opponent.equals(name)) {
					params.add(0, name);
					params.add(1, ack);
					receivePacket = new MyReceivePackect("ackchoose", params,
							null, null);
					if (ack.equals("A")) {
						it = clientsList.iterator();
						while (it.hasNext()) {
							clientsStates = it.next();
							if (clientsStates.name
									.equals(ackchooseSendPacketContent.name)) {
								clientsStates.state = "busy";
								clientsStates.opponent = name;
							} else if (clientsStates.name.equals(name)) {
								clientsStates.state = "busy";
								clientsStates.opponent = ackchooseSendPacketContent.name;
							}
						}
						String[] state = new String[9];
						for (int i = 0; i < 9; i++) {
							state[i] = "-";
						}
						GameStates gameState = new GameStates(
								ackchooseSendPacketContent.name, name, state);
						gameStates.add(gameState);
					} else if (ack.equals("D")) {
						it = clientsList.iterator();
						while (it.hasNext()) {
							clientsStates = it.next();
							if (clientsStates.name
									.equals(ackchooseSendPacketContent.name)) {
								clientsStates.state = "free";
								clientsStates.opponent = "";
							} else if (clientsStates.name.equals(name)) {
								clientsStates.state = "free";
								clientsStates.opponent = "";
							}
						}
					}
				}
			} else if (sendPacket.command.equals("play")) {
				PlaySendPacketContent playSendPacketContent = (PlaySendPacketContent) sendPacket.content;
				port = Integer.parseInt(playSendPacketContent.port);
				sourcePort = Integer.parseInt(playSendPacketContent.port);
				packetId = playSendPacketContent.packetId;
				System.out.println("RECEIVED: "
						+ playSendPacketContent.packetId + " "
						+ names.get(String.valueOf(port)) + " play "
						+ playSendPacketContent.number);
				List<String> params = new ArrayList<String>();
				String name = names.get(String.valueOf(port));
				GameStates gameState = null;
				Iterator<GameStates> it = gameStates.iterator();
				while (it.hasNext()) {
					gameState = it.next();
					if (gameState.player1.equals(name)
							|| gameState.player2.equals(name)) {
						break;
					}
				}
				if (gameState != null) {
					if (gameState.turn.equals(name)) {
						int index = Integer
								.parseInt(playSendPacketContent.number) - 1;
						String player = null;
						String opponent = null;
						int oport = 0;
						if (gameState.player1.equals(name)) {
							player = "1";
							opponent = gameState.player2;
							ClientsStates clientsStates = null;
							Iterator<ClientsStates> ite = clientsList
									.iterator();
							while (ite.hasNext()) {
								clientsStates = ite.next();
								if (clientsStates.name.equals(opponent)) {
									oport = Integer
											.parseInt(clientsStates.ports);
									break;
								}
							}
						} else {
							player = "2";
							opponent = gameState.player1;
							ClientsStates clientsStates = null;
							Iterator<ClientsStates> ite = clientsList
									.iterator();
							while (ite.hasNext()) {
								clientsStates = ite.next();
								if (clientsStates.name.equals(opponent)) {
									oport = Integer
											.parseInt(clientsStates.ports);
									break;
								}
							}
						}
						if (isLegal(gameState, index)) {
							gameState.state[index] = player;
							gameState.turn = opponent;
							String res = isOver(gameState, player);
							if (res.equals("")) {
								port = oport;
								receivePacket = new MyReceivePackect("play",
										null, null, gameState);
							} else {
								String ores = null;
								if (res.equals("W")) {
									ores = "L";
								} else if (res.equals("L")) {
									ores = "W";
								} else if (res.equals("F")) {
									ores = "F";
								}
								params.add(0, ores);
								receivePacket = new MyReceivePackect("result",
										params, null, null);
								sendData = receivePacket.getData(receivePacket);
								int number = sendData.length;
								data = new byte[4 + sendData.length];
								for (int i = 0; i < 4; ++i) {
									int shift = i << 3; // i * 8
									data[3 - i] = (byte) ((number & (0xff << shift)) >>> shift);
								}
								for (int i = 4; i < 4 + sendData.length; ++i) {
									data[i] = sendData[i - 4];
								}
								DatagramPacket packet = new DatagramPacket(
										data, data.length, IPAddress, oport);
								serverSocket.send(packet);

								params.add(0, res);
								receivePacket = new MyReceivePackect("result",
										params, null, null);
								it.remove();
								ClientsStates clientsStates = null;
								Iterator<ClientsStates> i = clientsList
										.iterator();
								while (i.hasNext()) {
									clientsStates = i.next();
									if (clientsStates.name.equals(opponent)) {
										clientsStates.state = "free";
										clientsStates.opponent = "";
									} else if (clientsStates.name.equals(name)) {
										clientsStates.state = "free";
										clientsStates.opponent = "";
									}
								}
							}
						} else {
							params.add(0, "O");
							receivePacket = new MyReceivePackect("ackplay",
									params, null, null);
						}
					} else {
						params.add(0, "T");
						receivePacket = new MyReceivePackect("ackplay", params,
								null, null);
					}
				}
			} else if (sendPacket.command.equals("logout")) {
				LogoutSendPacketContent logoutSendPacketContent = (LogoutSendPacketContent) sendPacket.content;
				packetId = logoutSendPacketContent.packetId;
				port = Integer.parseInt(logoutSendPacketContent.port);
				sourcePort = Integer.parseInt(logoutSendPacketContent.port);
				System.out.println("RECEIVED: "
						+ logoutSendPacketContent.packetId + " "
						+ names.get(String.valueOf(port)) + " logout");
				names.remove(String.valueOf(port));
				Iterator<ClientsStates> it = clientsList.iterator();
				while (it.hasNext()) {
					ClientsStates clientsStates = it.next();
					if (clientsStates.ports.equals(String.valueOf(port))) {
						it.remove();
					}
				}
			}
			if (!(lastAck.equals(packetId) && lastPort == port)) {
				if (receivePacket != null) {
					sendData = receivePacket.getData(receivePacket);
					int number = sendData.length;
					data = new byte[4 + sendData.length];
					for (int i = 0; i < 4; ++i) {
						int shift = i << 3; // i * 8
						data[3 - i] = (byte) ((number & (0xff << shift)) >>> shift);
					}
					for (int i = 4; i < 4 + sendData.length; ++i) {
						data[i] = sendData[i - 4];
					}
					DatagramPacket packet = new DatagramPacket(data,
							data.length, IPAddress, port);
					serverSocket.send(packet);
				}
				List<String> params = new ArrayList<String>();
				params.add(0, packetId);
				receivePacket = new MyReceivePackect("ack", params, null, null);
				sendData = receivePacket.getData(receivePacket);
				int number = sendData.length;
				data = new byte[4 + sendData.length];
				for (int i = 0; i < 4; ++i) {
					int shift = i << 3; // i * 8
					data[3 - i] = (byte) ((number & (0xff << shift)) >>> shift);
				}
				for (int i = 4; i < 4 + sendData.length; ++i) {
					data[i] = sendData[i - 4];
				}
				DatagramPacket packet;
				if(IPAddress.equals(receivedPacket.getAddress()))
					packet = new DatagramPacket(data, data.length,
						IPAddress, sourcePort);
				else
					packet = new DatagramPacket(data, data.length,
							receivedPacket.getAddress(), receivedPacket.getPort());
				serverSocket.send(packet);
				lastAck = packetId;
				lastPort = sourcePort;
				System.out.println("SEND ack: " + lastAck + " " + lastPort);
				System.out.println("IPAddress: " + IPAddress + "sourcePort" + sourcePort);
				System.out.println("getAddress: " + receivedPacket.getAddress() + "getPort" + receivedPacket.getPort());
			}
		}
	}
}

