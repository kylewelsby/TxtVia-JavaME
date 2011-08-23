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

class MessageProcessor implements Runnable{
	Thread th = new Thread(this);
	MessageConnection mc;
	boolean done;
	public void run() {
		processMessage();	
	}
	
	public void processMessage(){
		Message msg = null;
		
		try{
			msg = mc.receive();
		}catch(Exception e){
			System.out.println("processMessage.receive " + e);
		}
		
		if(msg instanceof TextMessage){
			TextMessage tmsg = (TextMessage)msg;
			// ticker.setString(tmsg.getPaylodText());
		}else if(msg instanceof BinaryMessage){
			BinaryMessage bmsg = (BinaryMessage)msg;
			byte[] data = bmsg.getPayloadData();
			// TODO handle binary message.
		}else{
			// ignore
		}
	}
	
}
