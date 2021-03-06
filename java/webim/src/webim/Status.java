package webim;

import java.util.Map;

public class Status {

	private String to;
	
	private String show;
	
	public Status(String to, String show, String status) {
		this.to = to;
		this.show = show;
	}

	public void feed(Map<String, String> data) {
		data.put("to", to);
		data.put("show", show);
	}
	
	public String toString() {
		return String.format("Status(to=%s, show=%s)", to, show);
	}

}