/**
 * 
 */
package com.virtusa.gto.chatter.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

import com.virtusa.gto.chatter.model.ChatInfo;

/**
 * @author admin
 *
 */
@ServerEndpoint("/chat/{sender}/{receiver}/{order}")
public class ChatSocketEndPoint {

	private static final Logger LOGGER = Logger.getLogger(ChatSocketEndPoint.class.getName());

	private static final String SENDER = "sender";
	private static final String RECEIVER = "receiver";
	private static final String ORDER = "order";

	private static Map<String, ChatInfo> chatSessions = Collections
			.synchronizedMap(new LinkedHashMap<String, ChatInfo>());

	@OnOpen
	public void onOpen(final Session session, @PathParam("sender") String sender,
			@PathParam("receiver") String receiver, @PathParam("order") String order) {
		LOGGER.info("Connected => Sender: " + sender + " Receiver: " + receiver + " Order: " + order);
		session.getUserProperties().put(SENDER, sender);
		session.getUserProperties().put(RECEIVER, receiver);
		session.getUserProperties().put(ORDER, order);

		String sessionID = sender + receiver + order;
		String receiverSessionID = receiver + sender + order;

		List<String> chatMessages = new ArrayList<String>();
		chatSessions.put(sessionID, new ChatInfo(sender, receiver, order, session, chatMessages));

		if (chatSessions.get(receiverSessionID) != null) {
			ChatInfo previousChatInfo = chatSessions.get(receiverSessionID);
			if (previousChatInfo.getMessages().size() != 0) {
//				JSONObject chatDetails = new JSONObject();
//				chatDetails.put("sender", receiver);
//				chatDetails.put("receiver", sender);
//				chatDetails.put("order", order);
//				try {
//					session.getBasicRemote().sendText(chatDetails.toString());
//				} catch (IOException e1) {
//					LOGGER.log(Level.SEVERE, "Cannot send message...");
//				}
				for (String message : previousChatInfo.getMessages()) {
					try {
						session.getBasicRemote().sendText(message);
					} catch (IOException e) {
						LOGGER.log(Level.SEVERE, "Cannot send message...");
					}
				}
			}
		}
	}

	@OnMessage
	public void onMessage(final Session session, @PathParam("sender") String sender,
			@PathParam("receiver") String receiver, @PathParam("order") String order, final String message) {
		LOGGER.info("Received Message => Sender: " + sender + " Receiver: " + receiver + " Order: " + order);

		String sessionID = sender + receiver + order;
		String receiverSessionID = receiver + sender + order;

		ChatInfo chatInfo = chatSessions.get(sessionID);
		chatInfo.getMessages().add(message);

		if (chatSessions.get(receiverSessionID) != null) {
			try {
				chatSessions.get(receiverSessionID).getSession().getBasicRemote().sendText(message);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Cannot send message...");
			}
		}
	}

	@OnClose
	public void onClose(final Session session, @PathParam("sender") String sender,
			@PathParam("receiver") String receiver, @PathParam("order") String order) {
		LOGGER.info("Disconnected => Sender: " + sender + " Receiver: " + receiver + " Order: " + order);

		String sessionID = sender + receiver + order;
		chatSessions.remove(sessionID);
	}

	@OnError
	public void onError(final Session session, @PathParam("sender") String sender,
			@PathParam("receiver") String receiver, @PathParam("order") String order, final Throwable t) {
		LOGGER.info("Error => Sender: " + sender + " Receiver: " + receiver + " Order: " + order);
	}

}
