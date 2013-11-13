import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class MyReceivePackect extends MyPacket {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyReceivePackect(String command, List<String> params,
			List<ClientsStates> clientsStates, GameStates playStates) {
		if (command.equals("acklogin")) {
			this.command = "acklogin";
			this.content = new AckloginReceivePacketContent(params);
		} else if (command.equals("ackls")) {
			this.command = "ackls";
			this.content = new AcklsReceivePacketContent(clientsStates);
		} else if (command.equals("request")) {
			this.command = "request";
			this.content = new RequestReceivePacketContent(params);
		} else if (command.equals("ackchoose")) {
			this.command = "ackchoose";
			this.content = new AckchooseReceivePacketContent(params);
		} else if (command.equals("ack")) {
			this.command = "ack";
			this.content = new AckReceivePacketContent(params);
		} else if (command.equals("play")) {
			this.command = "play";
			this.content = new PlayReceivePacketContent(playStates);
		} else if (command.equals("ackplay")) {
			this.command = "ackplay";
			this.content = new AckplayReceivePacketContent(params);
		} else if (command.equals("result")) {
			this.command = "result";
			this.content = new ResultReceivePacketContent(params);
		}
	}

	public MyReceivePackect(byte[] data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			MyReceivePackect myReceivePackect = (MyReceivePackect) ois
					.readObject();
			this.command = myReceivePackect.command;
			this.content = myReceivePackect.content;
		} catch (Exception e) {

		}
	}

	public byte[] getData(MyReceivePackect myReceivePackect) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(myReceivePackect);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}
}
