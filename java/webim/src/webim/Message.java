package webim;

import java.util.Map;

public class Message {
	
	private String to;
	private String body;
	private String style;
	private int timestamp;
	
	public Message(String to, String body, String style, int timestamp) {
		this.to = to;
		this.body = body;
		this.style = style;
		this.timestamp = timestamp;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public void feed(Map<String, String> data) {
		data.put("to", to);
		data.put("body", body);
		data.put("style", style);
		data.put("timestamp", String.valueOf(timestamp));
	}
	
	public String toString() {
		return String.format("Message(to=%s, body=%s, style=%s, timestamp=%d",
				to,body, style, timestamp);
	}

}
