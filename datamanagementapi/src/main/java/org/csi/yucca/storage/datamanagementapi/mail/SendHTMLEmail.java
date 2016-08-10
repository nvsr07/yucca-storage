package org.csi.yucca.storage.datamanagementapi.mail;

//File Name SendEmail.java

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.csi.yucca.storage.datamanagementapi.singleton.Config;

import javax.activation.*;

public class SendHTMLEmail{

	private String toAddress;
	private String fromAddress;
	private String hostMailServer;
	private Properties properties;

	public SendHTMLEmail(){

		toAddress = Config.getInstance().getMailTo();
		fromAddress = Config.getInstance().getMailFrom();
		hostMailServer = Config.getInstance().getMailServer();
		
		properties = System.getProperties();
		properties.setProperty("mail.smtp.host", hostMailServer);
	}
	
	public void Send404MailForClearDataset(String bodyMail){
	
		   SendMailForClearDataset(Config.getInstance().getMailSubject404(), Config.getInstance().getMailBody404() + bodyMail);
	}
	
	public void Send500MailForClearDataset(String bodyMail){
	
		   SendMailForClearDataset(Config.getInstance().getMailSubject500(), Config.getInstance().getMailBody500() + bodyMail);
	}
	
	public void Send200MailForClearDataset(String bodyMail){
	
		   SendMailForClearDataset(Config.getInstance().getMailSubject200(), Config.getInstance().getMailBody200() + bodyMail);
	}
	
	public void SendMailForClearDataset(String subjectMail, String bodyMail){
	
	   try{
	      // Create a default MimeMessage object.
	      MimeMessage message = new MimeMessage(Session.getDefaultInstance(properties));
	
	      message.setFrom(new InternetAddress(fromAddress));
	      message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
	      message.setSubject(subjectMail);
	      message.setText(bodyMail);
	
	      // Send message
	      Transport.send(message);
	      System.out.println("Sent message successfully....");
	   }catch (MessagingException mex) {
	      mex.printStackTrace();
	   }
	}
}