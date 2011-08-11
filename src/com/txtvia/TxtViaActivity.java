package com.txtvia;

//import gov.nist.siplite.message.Message;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;

public class TxtViaActivity extends MIDlet implements CommandListener, Runnable {
Thread background = new Thread(this);
MessageConnection mc;
boolean done;
Command send, exit;
TextField phone, message;
StringItem smsResult;

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		Form form = new Form("SMS Text");
		
		phone = new TextField("Phone Number", "", 255, TextField.PHONENUMBER);
		
		form.append(phone);
		
		message = new TextField("Message", "", 160, TextField.ANY);
		
		form.append(message);
		
		smsResult = new StringItem("SMS result", "");
		
		form.append(smsResult);
		
		form.addCommand(send = new Command("Send", Command.OK, 1));
		
		form.addCommand(exit = new Command("Exit", Command.EXIT, 1));
		
		form.setCommandListener(this);
		
		Display.getDisplay(this).setCurrent(form);

	}
	
	public void processMessage(){
		Message msg = null;
		try{
			msg = conn.receive();
		} catch (Exception e){
			System.out.println("processMessage.received " + e);
		}
		if(msg instanceof TextMessage){
			TextMessage sms = (TextMessage) msg;
			System.out.println(sms.getPayloadText());
		}else if(msg instanceof BinaryMessage){
			// TODO implement when a binary message is received
		}else{
			// TODO implement what happens when the message is not a TextMessage
		}
	}
	
	public void receiveSms(){
		MessageConnection conn = (MessageConnection) Connector.open("sms://:50001");
		
		try {
			conn.setMessageListener(
				new MessageListener(){
					public void processMessage(){
						Message msg = null;
						try{
							msg = conn.receive();
						} catch (Exception e){
							System.out.println("processMessage.received " + e);
						}
						if(msg instanceof TextMessage){
							TextMessage sms = (TextMessage) msg;
							System.out.println(sms.getPayloadText());
						}else if(msg instanceof BinaryMessage){
							// TODO implement when a binary message is received
						}else{
							// TODO implement what happens when the message is not a TextMessage
						}
					}
				}
			);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean sendSms(String number, String message) {
		boolean result = true;
		try{
			String addr = "sms://" + number;
			
			MessageConnection conn = (MessageConnection) Connector.open(addr);
			
			TextMessage msg = (TextMessage)conn.newMessage(MessageConnection.TEXT_MESSAGE);
			
			msg.setPayloadText(message);
			
			conn.send(msg);
			conn.close();
		} catch (SecurityException se){
			result = false;
		} catch (Exception e){
			result = false;
		}
		return result;
	}
	
	public void commandAction(Command c, Displayable d){
		if(c == exit){
			notifyDestroyed();
		}else{
			new Thread(this).start();
		}
	}
	public void run(){
		boolean result = sendSms(phone.getString(), message.getString());
		smsResult.setText("RESULT:"+result);
	}

}
