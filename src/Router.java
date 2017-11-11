import java.io.*;
import java.util.*;
import org.jgroups.*;
import org.jgroups.util.Util;
import java.util.UUID;

public class Router extends ReceiverAdapter{
	JChannel channel;

	// To save list of router clients
	Map<String, String[]> map = new HashMap<String, String[]>();
	// Creating a random UUID (Universally unique identifier).
  String RouterUUID = UUID.randomUUID().toString();

	private void start() throws Exception {
		this.channel = new JChannel();
		this.channel.setReceiver(this);
		this.channel.connect("Router");
		this.channel.getState(null, 1000);
		this.channel.setDiscardOwnMessages(true);
		this.map.put(this.channel.getAddressAsString(), new String[] {UUID.randomUUID().toString(), UUID.randomUUID().toString()});
		this.SendValues();
		//this.channel.disconnect();
	}

	private void SendValues() {
		Protocol protocol = new Protocol("INITIAL", this.map);

		try {
			Message msg = new Message(null, protocol);
			channel.send(msg);
		} catch(Exception e) {
			System.out.println(e);
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		while(true) {
			try{
				System.out.println("> ");
				System.out.flush();
				String line = in.readLine().toLowerCase();
				if (line.startsWith("quit") || line.startsWith("exit") || line.startsWith("close")) {
					this.channel.close();
					break;
				}else if (line.startsWith("disconnect")) {
					this.channel.disconnect();
					break;
				}
			}catch(Exception e){
				System.out.println(e);
			}
		}


	}

	public void viewAccepted(View new_view) {

    System.out.println("** view: " + new_view.getMembers());
		ArrayList<String> address = new ArrayList<String>();
		for (Address a : new_view.getMembers()) {
			address.add(a.toString());
		}

		for (String mapKey: this.map.keySet()) {
				if (!address.contains(mapKey)) {
					this.map.remove(mapKey);
					System.out.println("-------------------------------------------------\n");
					System.out.println("            Address "+mapKey+ " Left\n");
					System.out.println("-------------------------------------------------\n");
					System.out.println("\t\tUpdated Table\n");
					for(Map.Entry<String, String[]> entry : this.map.entrySet()) {
						String key = entry.getKey();
						String[] elements = entry.getValue();
						System.out.println("-------------------------------------------------\n");
						if (this.channel.getAddressAsString().equals(key)) {
							System.out.println("Router - "+ key + " | ME |\n");
						}else {
							System.out.println("Router - "+ key + "\n");
						}
						System.out.println("-------------------------------------------------\n");
						for (String s: elements) {
							System.out.println("Network - " + s + "\n");
						}
						System.out.println("-------------------------------------------------\n");
						System.out.println("\n");
					}
				}
		}
	}

	public void suspect(Address mbr) {
		System.out.println("** suspect: " + mbr);


	}

	public void receive(Message msg) {

		Protocol protocol = msg.getObject();
		Map<String, String[]> mapReceive =  protocol.map;
		System.out.println("EAE TIOZAo\n");
		System.out.println(map.equals(mapReceive));
		if (protocol.type.equals("INITIAL")) {
			System.out.println("-------------------------------------------------\n");
			System.out.println("\t\tSEND UPDATE\n");
			System.out.println("-------------------------------------------------\n");
			Protocol prot = new Protocol("UPDATE",this.map);
			try{
				Message message = new Message(msg.getSrc(), prot);
				channel.send(message);
			}catch(Exception e){
				System.out.println("ERRO");
			}

		}else if (protocol.type.equals("UPDATE")) {
			System.out.println("-------------------------------------------------\n");
			System.out.println("\t\tUPDATE\n");
			System.out.println("-------------------------------------------------\n");
		}

		for(Map.Entry<String, String[]> entry : mapReceive.entrySet()) {
	    String key = entry.getKey();
	    String[] elements = entry.getValue();
			this.map.put(key, elements);
		}

		for(Map.Entry<String, String[]> entry : this.map.entrySet()) {
	    String key = entry.getKey();
	    String[] elements = entry.getValue();
			if (this.channel.getAddressAsString().equals(key)) {
				System.out.println("Router - "+ key + " | ME |\n");
			}else {
				System.out.println("Router - "+ key + "\n");
			}
			System.out.println("-------------------------------------------------\n");
			for (String s: elements) {
        System.out.println("Network - " + s + "\n");
    	}
			System.out.println("-------------------------------------------------\n");
		}
	}

	public static void main(String[] args) throws Exception {

		try {
			new Router().start();
    }catch(Exception e) {
        System.err.println(e);
    }
	}
}
