package webim;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class Client {

	private int port;
	private User user;
	private String domain;
	private String apikey;
	private String host;
	private String ticket = "";

	public Client(User user, String domain, String apikey, String host, int port) {
		this.user = user;
		this.domain = domain;
		this.apikey = apikey;
		this.host = host;
		this.port = port;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	/**
	 * User online
	 * @param buddies
	 * @param groups
	 * @return JSONObject
	 * @throws WebIMException
	 */
	public JSONObject online(List<String> buddies, List<String> groups)
			throws WebIMException {
		Map<String, String> data = newData();
		data.put("groups", this.listJoin(",", groups));
		data.put("buddies", this.listJoin(",", buddies));
		data.put("name", user.id);
		data.put("nick", user.nick);
		data.put("status", user.status);
		data.put("show", user.show);
		try {
			String body = httpost("/presences/online", data);
			JSONObject respObj = new JSONObject(body);
			setTicket(respObj.getString("ticket"));

			JSONObject connInfo = new JSONObject();
			connInfo.put("ticket", ticket);
			connInfo.put("domain", domain);
			connInfo.put("server",
					String.format("http://%s:%d/packets", host, port));

			JSONObject rtObj = new JSONObject();
			rtObj.put("success", true);
			rtObj.put("conn", connInfo);
			rtObj.put("buddies", respObj.get("buddies"));
			rtObj.put("groups", respObj.get("groups"));
			rtObj.put("server_time", 1000);// FIXME LATER
			rtObj.put("user", user.toJson());
			return rtObj;
		} catch (WebIMException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebIMException(500, e.getMessage());
		}
	}
	

	/**
	 * User Offline
	 * 
	 * @return JSONObject "{'status': 'ok'}" or "{'status': 'error', 'message': 'blabla'}"
	 * @throws WebIMException
	 */
	public JSONObject offline() throws WebIMException {
		Map<String, String> data = newData();
		data.put("ticket", this.ticket);
		try {
			String body = httpost("/presences/offline", data);
			return new JSONObject(body);
		} catch (WebIMException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebIMException(500, e.getMessage());
		}
	}

	/**
	 * Publish updated presence.
	 *  
	 * @param presence
	 * @return JSONObject "{'status': 'ok'}" or "{'status': 'error', 'message': 'blabla'}"
	 * @throws WebIMException
	 */
	public JSONObject publish(Presence presence) throws WebIMException {
		Map<String, String> data = newData();
		data.put("nick", user.nick);
		presence.feed(data);
		try {
			String body = httpost("/presences/show", data);
			return new JSONObject(body);
		} catch (WebIMException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebIMException(500, e.getMessage());
		}
	}

	/**
	 * Publish status
	 * @param status
	 * @return JSONObject "{'status': 'ok'}" or "{'status': 'error', 'message': 'blabla'}"
	 * @throws WebIMException
	 */
	public JSONObject publish(Status status) throws WebIMException {
		Map<String, String> data = newData();
		data.put("nick", user.nick);
		status.feed(data);
		try {
			String body = httpost("/statuses", data);
			return new JSONObject(body);
		} catch (WebIMException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebIMException(500, e.getMessage());
		}
	}

	/**
	 * Publish Message
	 * @param message
	 * @return JSONObject "{'status': 'ok'}" or "{'status': 'error', 'message': 'blabla'}"
	 * @throws WebIMException
	 */
	public JSONObject publish(Message message) throws WebIMException {
		Map<String, String> data = newData();
		data.put("type", "unicast"); //TODO: FIXLATER
		data.put("nick", user.nick);
		message.feed(data);
		try {
			String body = httpost("/messages", data);
			return new JSONObject(body);
		} catch (WebIMException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebIMException(500, e.getMessage());
		}
	}

	/**
	 * Get group members
	 * @param grpid
	 * @return member list
	 * @throws WebIMException
	 */
	public JSONObject members(String grpid) throws WebIMException {
		Map<String, String> data = newData();
		data.put("group", grpid);
		try {
			String body = httpget("/group/members", data);
			return new JSONObject(body);
		} catch (WebIMException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebIMException(500, e.getMessage());
		}
	}

	/**
	 * Join Group
	 * @param grpid
	 * @return JSONObject "{'id': 'grpid', 'count': '0'}"
	 * @throws WebIMException
	 */
	public JSONObject join(String grpid) throws WebIMException {
		Map<String, String> data = newData();
		data.put("nick", user.nick);
		data.put("group", grpid);
		try {
			String body = httpost("/group/join", data);
			JSONObject respObj = new JSONObject(body);
			JSONObject rtObj = new JSONObject();
			rtObj.put("id", grpid);
			rtObj.put("count", respObj.getInt(grpid)); 
			return rtObj;
		} catch (WebIMException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebIMException(500, e.getMessage());
		}
	}

	/**
	 * Leave Group
	 * @param grpid
	 * @return JSONObject "{'status': 'ok'}" or "{'status': 'error', 'message': 'blabla'}"
	 * @throws WebIMException
	 */
	public JSONObject leave(String grpid) throws WebIMException {
		Map<String, String> data = newData();
		data.put("nick", user.nick);
		data.put("group", grpid);
		try {
			String body = httpost("/group/leave", data);
			return new JSONObject(body);
		} catch (WebIMException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebIMException(500, e.getMessage());
		}
	}
	
	private String httpget(String path, Map<String, String> params)
			throws Exception {
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(apiurl(path) + "?" + encodeData(params));
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			initConn(conn);
			conn.connect();
			return readResonpse(conn);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

	}

	private String httpost(String path, Map<String, String> data)
			throws Exception {
		URL url;
		HttpURLConnection conn = null;
		try {
			// Create connection
			url = new URL(apiurl(path));
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			String urlParameters = encodeData(data);
			conn.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));

			initConn(conn);

			// Send request
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			return readResonpse(conn);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private void initConn(HttpURLConnection conn) {
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
	}

	private String readResonpse(HttpURLConnection conn) throws IOException,
			WebIMException {
		// Get Response
		if (conn.getResponseCode() != 200) {
			throw new WebIMException(conn.getResponseCode(),
					conn.getResponseMessage());
		}
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			response.append(line);
		}
		System.out.println(response.toString());
		rd.close();
		return response.toString();
	}

	private Map<String, String> newData() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("version", WebIM.APIVSN);
		data.put("domain", domain);
		data.put("apikey", apikey);
		data.put("ticket", ticket);
		return data;
	}

	private String encodeData(Map<String, String> data) throws Exception {
		List<String> list = new ArrayList<String>();
		Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pair = it.next();
			list.add(pair.getKey() + "="
					+ URLEncoder.encode(pair.getValue(), "utf-8"));
		}
		return listJoin("&", list);
	}

	private String listJoin(String sep, List<String> groups) {
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < groups.size(); i++) {
			if (first) {
				sb.append(groups.get(i));
				first = false;
			} else {
				sb.append(sep);
				sb.append(groups.get(i));
			}
		}
		return sb.toString();
	}

	private String apiurl(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return String.format("http://%s:%d/%s%s", host, port, WebIM.APIVSN, path);
	}

}
