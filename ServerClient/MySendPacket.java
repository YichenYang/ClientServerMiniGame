import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class MySendPacket extends MyPacket {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MySendPacket(String command, List<String> params) {
		if (command.equals("login")) {
			this.command = "login";
			this.content = new LoginSendPacketContent(params);
		} else if (command.equals("list")) {
			this.command = "list";
			this.content = new ListSendPacketContent(params);
		} else if (command.equals("choose")) {
			this.command = "choose";
			this.content = new ChooseSendPacketContent(params);
		} else if (command.equals("ackchoose")) {
			this.command = "ackchoose";
			this.content = new AckchooseSendPacketContent(params);
		} else if (command.equals("play")) {
			this.command = "play";
			this.content = new PlaySendPacketContent(params);
		} else if (command.equals("logout")) {
			this.command = "logout";
			this.content = new LogoutSendPacketContent(params);
		}
	}

	public MySendPacket(byte[] data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			MySendPacket mySendPacket = (MySendPacket) ois.readObject();
			this.command = mySendPacket.command;
			this.content = mySendPacket.content;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] getData(MySendPacket mySendPacket) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(mySendPacket);
			oos.close();
		} catch (Exception e) {

		}
		return baos.toByteArray();
	}
}
