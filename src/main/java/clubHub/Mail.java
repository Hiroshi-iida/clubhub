package clubHub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class Mail {
  private static final Logger log = LoggerFactory.getLogger(Mail.class);

  private final JavaMailSender javaMailSender;

  @Autowired
  Mail(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  public SimpleMailMessage send(String subject, String content) {

    SimpleMailMessage mailMessage = new SimpleMailMessage();

    mailMessage.setTo("iichan.hiro@gmail.com");
//    mailMessage.setReplyTo("*****.*****.*****@gmail.com");
    mailMessage.setFrom("clubhub.h@gmail.com");
    mailMessage.setSubject(subject);
    mailMessage.setText(content);

    javaMailSender.send(mailMessage);	// このコード実行のタイミングでメール送信

    return mailMessage;
  }

}