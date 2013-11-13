import java.io.Serializable;
import java.util.List;

class PacketContent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}

class SendPacketContent extends PacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String packetId;
	String port;
}

class LoginSendPacketContent extends SendPacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	
	LoginSendPacketContent(List<String> params) {
		this.packetId = params.get(0);
		this.name = params.get(1);
		this.port = params.get(2);
	}
}

class ListSendPacketContent extends SendPacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ListSendPacketContent(List<String> params) {
		this.packetId = params.get(0);
		this.port = params.get(1);
	}
}

class ChooseSendPacketContent extends SendPacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;

	ChooseSendPacketContent(List<String> params) {
		this.packetId = params.get(0);
		this.name = params.get(1);
		this.port = params.get(2);
	}
}

class AckchooseSendPacketContent extends SendPacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	String ack;
	AckchooseSendPacketContent(List<String> params) {
		this.packetId = params.get(0);
		this.name = params.get(1);
		this.ack = params.get(2);
		this.port = params.get(3);
	}
}

class PlaySendPacketContent extends SendPacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String number;

	PlaySendPacketContent(List<String> params) {
		this.packetId = params.get(0);
		this.number = params.get(1);
		this.port = params.get(2);
	}
}

class LogoutSendPacketContent extends SendPacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	LogoutSendPacketContent(List<String> params) {
		this.packetId = params.get(0);
		this.port = params.get(1);
	}
}

class ReceivePacketContent extends PacketContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

class AckloginReceivePacketContent extends ReceivePacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String ack;

	AckloginReceivePacketContent(List<String> params) {
		this.ack = params.get(0);
	}
}

class AcklsReceivePacketContent extends ReceivePacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<ClientsStates> states;

	AcklsReceivePacketContent(List<ClientsStates> params) {
		this.states = params;
	}
}

class RequestReceivePacketContent extends ReceivePacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;

	RequestReceivePacketContent(List<String> params) {
		this.name = params.get(0);
	}
}

class AckchooseReceivePacketContent extends ReceivePacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	String ack;

	AckchooseReceivePacketContent(List<String> params) {
		this.name = params.get(0);
		this.ack = params.get(1);
	}
}

class AckReceivePacketContent extends ReceivePacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String packetId;

	AckReceivePacketContent(List<String> params) {
		this.packetId = params.get(0);
	}
}

class PlayReceivePacketContent extends ReceivePacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	GameStates playStates;

	PlayReceivePacketContent(GameStates params) {
		this.playStates = params;
	}
}

class AckplayReceivePacketContent extends ReceivePacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String ack;

	AckplayReceivePacketContent(List<String> params) {
		this.ack = params.get(0);
	}
}

class ResultReceivePacketContent extends ReceivePacketContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String res;

	ResultReceivePacketContent(List<String> params) {
		this.res = params.get(0);
	}
}

public class MyPacket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String command;
	PacketContent content;
}
