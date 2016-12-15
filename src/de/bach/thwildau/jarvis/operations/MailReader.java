package de.bach.thwildau.jarvis.operations;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class MailReader implements Function{
	
	private static MailReader instance;
	private String answer = null;
	
	private MailReader(String answer) {
		this.answer = answer;
	}

	public static MailReader getInstance(String answer) {
		if (instance == null) {
			instance = new MailReader(answer);
			return instance;
		} else {
			return instance;
		}
	}
	
	@Override
	public String operate() {
		 String emails ="";
		 try {
		      //create properties field
		      Properties properties = new Properties();

		      properties.put("mail.pop3.host", "zimbrastud.th-wildau.de");
		      properties.put("mail.pop3.port", "110");
		      properties.put("mail.pop3.starttls.enable", "true");
		      Session emailSession = Session.getDefaultInstance(properties);
		  
		      //create the POP3 store object and connect with the pop server
		      Store store = emailSession.getStore("pop3s");

		      store.connect("zimbrastud.th-wildau.de", "bach","walter45");

		      //create the folder object and open it
		      Folder emailFolder = store.getFolder("INBOX");
		      emailFolder.open(Folder.READ_ONLY);

		      // retrieve the messages from the folder in an array and print it
		      Message[] messages = emailFolder.getMessages();
		      System.out.println("messages.length---" + messages.length);

		      int index =  messages.length-1;
		      int threshold = 0;
		      
		      while(index > 0 && threshold < 3){
		    	  Message message = messages[index];
			   
			         if(message.getContentType().contains("text/plain")){
			         emails+="Nachricht von: "+message.getFrom()[0]+" ... Betreff: "+message.getSubject()+" ... Text: "+ message.getContent().toString();
			         emails+="\n";
			         emails+="... ";
			         threshold++;
			         }
			         index--;
		      }
		    
		      //close the store and folder objects
		      emailFolder.close(false);
		      store.close();
		     
		      } catch (NoSuchProviderException e) {
		         e.printStackTrace();
		      } catch (MessagingException e) {
		         e.printStackTrace();
		      } catch (Exception e) {
		         e.printStackTrace();
		      }
		 return this.answer+" "+emails;
	}

}
