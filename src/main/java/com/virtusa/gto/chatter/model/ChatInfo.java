/**
 * 
 */
package com.virtusa.gto.chatter.model;

import java.util.List;

import javax.websocket.Session;

/**
 * @author admin
 *
 */
public class ChatInfo {

	private String sender;
	private String receiver;
	private String order;
	private Session session;
	private List<String> messages;

	public ChatInfo(String sender, String receiver, String order, Session session, List<String> messages) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.order = order;
		this.session = session;
		this.messages = messages;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

}
