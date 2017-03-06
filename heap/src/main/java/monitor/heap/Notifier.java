package monitor.heap;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import org.apache.commons.mail.DefaultAuthenticator;

public class Notifier implements Consumer{
	private String smtp_host;
	private int smtp_port;
	private String username;
	private String password;
	private String emailTo;

	public void notify(String message){
		// TODO Auto-generated method stub
		Email email = new SimpleEmail();
		email.setHostName(smtp_host);
		email.setSmtpPort(smtp_port);
		email.setAuthenticator(new DefaultAuthenticator("username", "password"));
		email.setSSLOnConnect(true);
		try {
			email.setFrom("user@gmail.com");
			email.setSubject("Comopnent memory exceeded threshold");
			email.setMsg(message);
			email.addTo(emailTo);
			email.send();
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	Notifier(String smtp_host,int smtp_port,String username,String password,String email)
	{
		this.password=password;
		this.username=username;
		this.smtp_host=smtp_host;
		this.emailTo=email;
		this.smtp_port=smtp_port;
				
	}

}
