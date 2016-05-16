/**
 * @author Rodi Jolak
 * @version 1.0, 2016-05-13
 * @see http://goo.gl/dy4tUd
 *
 * Class to create email, send it and attach files. Gmail API has been used
 * to attach files properly to an email. Files needed to be added to the library
 * in Android Studio, in order for this class to work, are activation.jar,
 * additionnal.jar and mail.jar.
 *
 * The email will be sent in the background and cannot be edited by the app user.
 * The instance variable _multipart is used to set the body of the email as well
 * as to add an attachment.
 *
 * The code has been created by Rodi Jolak with inspiration from the tutorial
 * linked above. The javadoc has been added by Annie Söderström and Sara Kinell on 2016-05-16.
 *
 */


package com.example.eliasvensson.busify;

import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class Mail extends javax.mail.Authenticator {
    private String _user;
    private String _pass;

    private String[] _to;
    private String _from;

    private String _port;
    private String _sport;

    private String _host;

    private String _subject;
    private String _body;

    private boolean _auth;

    private boolean _debuggable;

    private Multipart _multipart;


    /**
     * Empty constructor of the Mail class
     */
    public Mail() {
        _host = "smtp.gmail.com"; // default smtp server
        _port = "465"; // default smtp port
        _sport = "465"; // default socketfactory port

        _user = ""; // username
        _pass = ""; // password
        _from = ""; // email sent from
        _subject = ""; // email subject
        _body = ""; // email body

        _debuggable = false; // debug mode on or off - default off
        _auth = true; // smtp authentication - default on

        _multipart = new MimeMultipart();

        // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    /**
     * Constructor of the Mail class
     * @param user The username of the email sender
     * @param pass The password of the email sender
     */
    public Mail(String user, String pass) {
        this();

        _user = user;
        _pass = pass;
    }

    /**
     * Creates message and send to recipient(s)
     * @return True is returned if email was sent, false otherwise
     * @throws Exception Thrown if error occurs while sending the email
     */
    public boolean send() throws Exception {
        Properties props = _setProperties();

        // Creates a session while not username, password, recipient email, sender email, subject or body are empty
        if (!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("") && !_body.equals("")) {
            Session session = Session.getInstance(props, this);

            final MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(_from));

            InternetAddress[] addressTo = new InternetAddress[_to.length];
            for (int i = 0; i < _to.length; i++) {
                addressTo[i] = new InternetAddress(_to[i]);
            }

            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
            msg.setSubject(_subject);
            msg.setSentDate(new Date());

            // Setup message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(_body);
            _multipart.addBodyPart(messageBodyPart);

            // Puts parts in message
            msg.setContent(_multipart);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Sends email
                        Transport.send(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();


            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds attachment to email
     * @param filename Name of the file to be attached
     * @throws Exception Thrown if file cannot be attached properly
     */
    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        _multipart.addBodyPart(messageBodyPart);
    }

    /**
     * Creates and returns new PasswordAuthentication object
     * @return Repository with a username and a password in a PasswordAuthentication object
     */
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }

    /**
     * Sets debug and SMTP, email server, authentication
     * @return Properties object with values
     */
    private Properties _setProperties() {
        Properties props = new Properties();
        // Sets host of email server
        props.put("mail.smtp.host", _host);

        if(_debuggable) {
            props.put("mail.debug", "true");
        }

        if(_auth) {
            props.put("mail.smtp.auth", "true");
        }

        props.put("mail.smtp.port", _port);
        props.put("mail.smtp.socketFactory.port", _sport);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        return props;
    }

    /**
     * Getter for the body
     * @return Body of the email
     */
    public String getBody() {
        return _body;
    }

    /**
     * Setter for the body
     * @param _body Text to be included in the email
     */
    public void setBody(String _body) {
        this._body = _body;
    }

    /**
     * Setter for the recipient(s)
     * @param _to Email addresses of the recipient(s) in an array
     */
    public void set_to(String[] _to) {
        this._to = _to;
    }

    /**
     * Setter for the sender
     * @param _from Senders email address
     */
    public void set_from(String _from) {
        this._from = _from;
    }

    /**
     * Setter for the subject of the email
     * @param _subject Subject of the email
     */
    public void set_subject(String _subject) {
        this._subject = _subject;
    }


}
