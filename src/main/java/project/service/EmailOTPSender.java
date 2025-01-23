package project.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

public class EmailOTPSender {

    private static final Logger logger = Logger.getLogger(EmailOTPSender.class.getName());

    public static boolean sendEmail(String recipientEmail, String otp) {
        final String senderEmail = "stellixor.contact@gmail.com";
        final String senderPassword = "lxp iqo ucfz crj";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );

            message.setSubject("🔒 Secure Access: Your OTP Code Inside");
            String htmlContent = "<html>" +
                    "<body>" +
                    "<p>Hi there,</p>" +
                    "<p>Your One-Time Password (OTP) for secure access is :</p>" +
                    "<h3><b>  " + otp + "</b></h3>" +
                    "<p>Please enter this code on the application to proceed. For your security, this OTP is valid for the next <b>5 minutes</b> only.</p>" +
                    "<p>If you didn’t request this code, please ignore this email.</p>" +
                    "<p>Stay safe,<br><span style='color: #2E86C1;'>The Stellixor Team<br>stellixor.contact@gmail.com</span></p>" +
                    "</body>" +
                    "</html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);

            return true;

        } catch (AuthenticationFailedException e) {
            logger.log(Level.SEVERE, "Authentication failed: Invalid email or app password.", e);
            //showMessageDialog("Authentication failed\nPlease check your email and password");
        } catch (SendFailedException e) {
            logger.log(Level.SEVERE, "Email sending failed: Invalid recipient address.", e);
            //showMessageDialog("The recipient's email address seems invalid");
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "Error sending email.", e);
            //showMessageDialog("An unexpected issue occurred while sending the OTP");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error occurred.", e);
            //showMessageDialog("An unexpected error occurred\nPlease try again later");
        }
        
        return false;
    }

    private static void showMessageDialog(String content) {
        JOptionPane.showMessageDialog(
                null,
                content,
                "OTP Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

