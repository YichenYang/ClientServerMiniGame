import java.io.Serializable;

public class ClientsStates implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	String IPAddress;
	String ports;
	String state;
	String opponent;

	ClientsStates(String name, String IPAddress, String ports, String state,
			String opponent) {
		this.name = name;
		this.IPAddress = IPAddress;
		this.ports = ports;
		this.state = state;
		this.opponent = opponent;
	}
}
