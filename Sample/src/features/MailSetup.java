package features;


import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



public class MailSetup 
{
	public static Properties MCONFIG;
	String formattedDate;
	public MailSetup() throws Exception
	{
		try
		{
		FileInputStream fs = new FileInputStream(System.getProperty("user.dir")+"//src//xtbills//configuration//MailSetup.properties");
		MCONFIG= new Properties();
		MCONFIG.load(fs);
		Zip.zipFolder(System.getProperty("user.dir")+"//XSLT_Reports",System.getProperty("user.dir")+"//emailable_XSLT_Reports.zip");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		formattedDate = sdf.format(date);
		
		Zip.zipFolder(System.getProperty("user.dir")+"//reports//"+formattedDate,System.getProperty("user.dir")+"//Report - "+formattedDate+".zip");
		
		}
		
		catch(Exception me)
		{
			me.printStackTrace();
			System.out.println("Config file not found for mail setup");
		}
		
	}
	public void SendEmail() throws Exception
	{
		
		/* Variables deceleration */
		final String username = MCONFIG.getProperty("MailUserName");
		final String password = MCONFIG.getProperty("MailUserpassword");
		String smtphost = MCONFIG.getProperty("SMTPHost");
		String smtpport = MCONFIG.getProperty("SMTPPort");
		String fromadd = MCONFIG.getProperty("FromAddress");
		String toadd = MCONFIG.getProperty("ToAddress");
		String tocc = MCONFIG.getProperty("CCAddress");
		String tobcc = MCONFIG.getProperty("BCCAddress");
		String subject = MCONFIG.getProperty("MailSubject");
		String xsltattachement = System.getProperty("user.dir")+"//emailable_XSLT_Reports.zip";
		String xsltfilename = "XSLT_Report - "+ formattedDate +".zip";
		String messagebody = MCONFIG.getProperty("MessageBody");
		String islocal = MCONFIG.getProperty("isLocal"); 
		String LogFilePath = System.getProperty("user.dir")+"//logs//XTBills.log";
		String LogFileName = "XTBills_Log - "+ formattedDate + ".log";
		String PDFFilePath = System.getProperty("user.dir")+"//Report - "+formattedDate+".zip";
		String PDFFileName = "PDFReport - "+formattedDate+".zip";
		
		
		Properties props = new Properties();
		 if (islocal.contentEquals("0"))
		 {
			 props.put("mail.smtp.auth", false);
			
		 }
		 else
		 {
			 props.put("mail.smtp.auth", "true");
		 }
	
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", smtphost);
		props.put("mail.smtp.port", smtpport);
		

		Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
	//	Session session = Session.getDefaultInstance(props, null);

		 try {

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromadd));
        
     // to send mail to Main user   
        message.setRecipients(Message.RecipientType.TO,
               InternetAddress.parse(toadd));
      
        //to send mail to cc users
        message.setRecipients(Message.RecipientType.CC,
                InternetAddress.parse(tocc));
     
        // to send mail to bcc users
        
        message.setRecipients(Message.RecipientType.BCC,
                InternetAddress.parse(tobcc)); 
    
        message.setSubject(subject);
        message.setText("PFA");
        
        // to add all the boby to mail
        Multipart multipart = new MimeMultipart();
        
        //to add attachement1 - XSLT Report
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(xsltattachement);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(xsltfilename);
        
        //to add attachement2 - Logs
        MimeBodyPart logs = new MimeBodyPart();
        DataSource logscr = new FileDataSource(LogFilePath);
        logs.setDataHandler(new DataHandler(logscr));
        logs.setFileName(LogFileName);
        
        //to add attachement3 - PDF Report
        MimeBodyPart pdf = new MimeBodyPart();
        DataSource pdfscr = new FileDataSource(PDFFilePath);
        pdf.setDataHandler(new DataHandler(pdfscr));
        pdf.setFileName(PDFFileName);
        
        //to add message body
        MimeBodyPart messageBodyPartCon1 = new MimeBodyPart();
        messageBodyPartCon1.setContent("Hi All,<br> <br>" + "&nbsp &nbsp &nbsp &nbsp &nbsp" + messagebody + "<br> <br> <br>"
        		+ "Regards, <br>"
        		+ "QA Team.", "text/html");
        
                        
        //to add all the parts to mail
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(logs);
        multipart.addBodyPart(pdf);
       
        multipart.addBodyPart(messageBodyPartCon1);
     
       
        message.setContent(multipart);

        System.out.println("Sending the mail to TO configured users " + toadd);
        System.out.println("Sending the mail to CC configured users " + tocc);
        System.out.println("Sending the mail to BCC configured users " + tobcc);
       
       /* Transport transport = session.getTransport("smtp");
       
        transport.connect(smtphost, username, password);*/

        Transport.send(message);

        System.out.println("Mail has sent to the user/users with the attachments");

		} 
		
		catch (MessagingException e) 
		{
			
        e.printStackTrace();
        
		}
		
	}
	
	public static void main(String[] args) throws Exception
	{
		MailSetup s = new MailSetup();
		s.SendEmail();
	}
	
}

