package webim;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientTest {
	
	private Client c;

	@Before
	public void setUp() throws Exception {
		c = new Client(new User("1", "erylee", "online", "I am Online"),
				"webim20.cn", "public", "localhost", 5000);
		c.setTicket("ticket");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOnline() throws Exception {
		List<String> buddies = new ArrayList<String>();
		buddies.add("2");
		buddies.add("3");
		List<String> groups = new ArrayList<String>();
		groups.add("group1");
		groups.add("group2");
		JSONObject obj = c.online(buddies, groups);
		System.out.print(obj.toString());
	}

	@Test
	public void testOffline()  throws Exception {
		c.offline();
	}

	@Test 
	public void testPublishStatus() throws Exception {
		c.publish(new Status("2", "typing", "Coding..."));
	}
	
	@Test
	public void testPublishMessage() throws Exception {
		c.publish(new Message("2", "body", "", 100));
	}
	
	@Test
	public void testPublishPresence() throws Exception {
		c.publish(new Presence("busy", "Coding..."));
	}
	
	@Test
	public void testMembers() throws Exception {
		c.members("group1");
	}
	
	@Test
	public void testLeave() throws Exception {
		c.leave("group1");
	}
	
	@Test
	public void testJoin() throws Exception {
		c.join("group1");
	}

}