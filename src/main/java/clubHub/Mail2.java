package clubHub;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class Mail2 {
  private static final Logger log = LoggerFactory.getLogger(Mail2.class);

  private final JavaMailSender javaMailSender;

  @Autowired
  public Mail2(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  public void send(String subject, String content) {

    try {

      MimeMessage mail = javaMailSender.createMimeMessage();

      mail.setHeader("Content-Transfer-Encoding", "base64");

      MimeMessageHelper helper = new MimeMessageHelper(mail, false);

      helper.setTo("iichan.hiro@example.com");
//      helper.setReplyTo("*****.*****.*****@gmail.com");
      helper.setFrom("clubhub.h@gmail.com");
      helper.setSubject(subject);
      helper.setText(content);

      javaMailSender.send(mail);

    } catch (MessagingException e) {
      e.printStackTrace();
    }

  }

}