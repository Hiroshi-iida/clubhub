package clubHub;

import java.lang.CharSequence;
import java.lang.String;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.lang.Object.*;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

//import org.springframework.security.crypto.password;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import clubHub.repositories.ChatDataRepository;
import clubHub.repositories.CoachDataRepository;
import clubHub.repositories.PhotoDataRepository;
import clubHub.repositories.PostDataRepository;
import clubHub.repositories.SchoolDataRepository;

import java.util.UUID;
import javax.net.ssl.SSLEngineResult.Status;

@Controller
@SpringBootApplication
public class clubController  {
	@Autowired
	HttpSession session;
	@Autowired
	SchoolDataRepository schoolrepository;
	@Autowired
	PostDataRepository postrepository;
	@Autowired
	CoachDataRepository coachrepository;
	@Autowired
	ChatDataRepository chatrepository;
	@Autowired
	PhotoDataRepository photorepository;

	
	@Component
	public class JavaMailSample {
//	  private static final Logger log = LoggerFactory.getLogger(JavaMailSample.class);

	  public void send(String subject, String content,String address) {

	    final String to = address;
	    final String from = "clubhub.h@gmail.com";

	    // Google account mail address
	    final String username = "clubhub.h@gmail.com";
	    // Google App password
	    final String password = "vmzebqndbtejjplb";

	    //final String charset = "ISO-2022-JP";
	    final String charset = "UTF-8";

	    final String encoding = "base64";

	    // for gmail
	    String host = "smtp.gmail.com";
	    String port = "587";
	    String starttls = "true";

	    // for local
	    //String host = "localhost";
	    //String port = "2525";
	    //String starttls = "false";

	    Properties props = new Properties();
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.port", port);
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", starttls);

	    props.put("mail.smtp.connectiontimeout", "10000");
	    props.put("mail.smtp.timeout", "10000");

	    props.put("mail.debug", "true");

	    Session session = Session.getInstance(props,
	    new javax.mail.Authenticator() {
	       protected PasswordAuthentication getPasswordAuthentication() {
	          return new PasswordAuthentication(username, password);
	       }
	    });

	    try {
	      MimeMessage message = new MimeMessage(session);

	      // Set From:
	      message.setFrom(new InternetAddress(from, "clubHub"));
	      // Set ReplyTo:
	      message.setReplyTo(new Address[]{new InternetAddress(from)});
	      // Set To:
	      message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

	      message.setSubject(subject, charset);
	      message.setText(content, charset);

	      message.setHeader("Content-Transfer-Encoding", encoding);

	      Transport.send(message);

	    } catch (MessagingException e) {
	      throw new RuntimeException(e);
	    } catch (UnsupportedEncodingException e) {
	      throw new RuntimeException(e);
	    }

	  }

	}
	
	public ModelAndView required(ModelAndView mav) {
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		return mav;
		// header用に必ず送るsession
	}

	public String imageConversion(MultipartFile uploadfile,int length) throws Exception {

		try {		
		
		final int MAX_LENGTH = length; // サイズの横or縦幅の最大値
		InputStream image = uploadfile.getInputStream(); // 受け取ったMultipartfileを変換して受け取る

		BufferedImage images = ImageIO.read(image); // 受け取った画像の縦横サイズを受け取る
		double width = images.getWidth();
		double height = images.getHeight();
		double ratio = 0; // 比率

		// 縮尺がずれないように
		if (width > height) {
			ratio = MAX_LENGTH / width;
			width = MAX_LENGTH;
			height *= ratio;
		} else {
			ratio = MAX_LENGTH / height;
			height = MAX_LENGTH;
			width *= ratio;
		}
		int wid = (int) width;
		int hei = (int) height;

		// 縮小処理
		BufferedImage img = new BufferedImage(wid, hei, BufferedImage.TYPE_3BYTE_BGR);
		img.createGraphics().drawImage(images.getScaledInstance(wid, hei, Image.SCALE_AREA_AVERAGING), 0, 0, wid, hei,
				null);

		// 一度byte配列へ変換
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();

		// byte配列をInputStreamに変換
		InputStream in = new ByteArrayInputStream(imageInByte);

		// 画像データをbase64エンコードの準備
		StringBuffer data = new StringBuffer();
		InputStream is = in;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] indata = new byte[10240 * 16];
		int siz;
		while ((siz = is.read(indata, 0, indata.length)) > 0) {
			os.write(indata, 0, siz);
		}
		// 画像データをbase64エンコードする
		String base64 = new String(Base64.encodeBase64(os.toByteArray()), "ASCII");
		return base64;
		}catch(Exception e){
			return "";
			
			// 表示方法
//			StringBuffer data = new StringBuffer();
//			String base64 = getImage();
//			data.append("data:image/jpeg;base64,");
//			data.append(base64);
//			mav.addObject("base64data", data.toString());
	
			// 受取方法
//			<img th:if="${base64data}" th:src="${base64data}" />
			
			
		}

	}
	
	@RequestMapping("/mail")
	public ModelAndView mail(ModelAndView mav) {
		mav.setViewName("result");
		mav.addObject("msg","メールを送信しました！！");
	    JavaMailSample mailSend = new JavaMailSample();
	    mailSend.send("JavaMail テストメール", "テストメールの本文","iichan.hiro@gmail.com");
	    return mav;
	}

    @RequestMapping("/validate/{id}")
    public ModelAndView validate(ModelAndView mav, @PathVariable("id") String id) throws Exception {
		List<CoachData> cdata = coachrepository.findByUuid(id);
		List<SchoolData> sdata = schoolrepository.findByUuid(id);
		
        if(cdata.size() != 0) {
        	cdata.get(0).setAuthentication(true);
        	mav.addObject("msg","登録完了！");
        	mav.addObject("cdata",cdata.get(0));
        	coachrepository.saveAndFlush(cdata.get(0));
			session.setAttribute("sessionAccountName", cdata.get(0).getLastName()); // セッションにスクールネーム保存
			session.setAttribute("sessionCid", cdata.get(0).getId());
			session.setAttribute("sessionCdata", cdata.get(0));
			required(mav);
        }else if(sdata.size() != 0){
         	sdata.get(0).setAuthentication(true);
        	mav.addObject("msg","登録完了！");
        	mav.addObject("sdata",sdata.get(0));
        	schoolrepository.saveAndFlush(sdata.get(0));
        	session.setAttribute("sessionAccountName", sdata.get(0).getSchoolName()); // セッションにスクールネーム
			session.setAttribute("sessionSdata", sdata.get(0)); // 以下のsessionはpostのために全保存
			session.setAttribute("sessionSid", sdata.get(0).getId()); // 以下のsessionはpostのために全保存
			session.setAttribute("sessionScategory", sdata.get(0).getCategory());
			session.setAttribute("sessionSschoolName", sdata.get(0).getSchoolName());
			session.setAttribute("sessionSlastname", sdata.get(0).getLastName());
			session.setAttribute("sessionSfirstName", sdata.get(0).getFirstName());
			session.setAttribute("sessionSmail", sdata.get(0).getMail());
			session.setAttribute("sessionSpassword", sdata.get(0).getPassword());
			session.setAttribute("sessionSarea", sdata.get(0).getArea());
			session.setAttribute("sessionSaddress", sdata.get(0).getAddress());
			session.setAttribute("sessionStel", sdata.get(0).getTel());
			required(mav);
        }else {
        	mav.addObject("msg","エラーです。メールのURLを確認するか      お手数ですが再度ご登録ください。");
        }
        mav.setViewName("validate");
         return mav;
    }
		
	@RequestMapping(value = "/demo", method = RequestMethod.GET)
	public ModelAndView demo(ModelAndView mav) {
		mav.setViewName("demo");
		List<PhotoData> list = photorepository.findAll();
		mav.addObject("photodata", list);
		return mav;
	}
	
	@RequestMapping(value = "/demo", method = RequestMethod.POST)
	public ModelAndView demo(@RequestParam("uploadfile") MultipartFile file,ModelAndView mav)throws Exception{
		String image = imageConversion(file,150);
		mav.addObject("image",image);		
		return mav;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute("formModel") CoachData coachdata, SchoolData schooldata,
			ModelAndView mav) {
		mav.setViewName("index");
		required(mav);
		return mav;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@ModelAttribute("formModel") CoachData coachdata, SchoolData schooldata,
			ModelAndView mav) {
		mav.setViewName("login");
		required(mav);
		return mav;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView login(@ModelAttribute("formModel") SchoolData schooldata, CoachData coachdata,
			ModelAndView mov) {
		ModelAndView res = null;
		List<SchoolData> schoolList = schoolrepository.findAll();
		List<CoachData> coachList = coachrepository.findAll();
        String cpass = DigestUtils.sha1Hex(coachdata.getPassword());	
        String spass = DigestUtils.sha1Hex(schooldata.getPassword());	
        
		for (int i = 0; i < schoolList.size(); i++) {
			if (schooldata.getMail().equals(schoolList.get(i).getMail())) { // 学校アカウントメールアドレス一致確認
				if (spass.equals(schoolList.get(i).getPassword())) { // ハッシュ化された同士でパス確認
					if(schoolList.get(i).isAuthentication()) {
					mov.addObject("msg", "ログインしました");
					mov.setViewName("result");
					session.setAttribute("sessionAccountName", schoolList.get(i).getSchoolName()); // セッションにスクールネーム
					session.setAttribute("sessionSdata", schoolList.get(i)); // 以下のsessionはpostのために全保存
					session.setAttribute("sessionSid", schoolList.get(i).getId()); // 以下のsessionはpostのために全保存
					session.setAttribute("sessionScategory", schoolList.get(i).getCategory());
					session.setAttribute("sessionSschoolName", schoolList.get(i).getSchoolName());
					session.setAttribute("sessionSlastname", schoolList.get(i).getLastName());
					session.setAttribute("sessionSfirstName", schoolList.get(i).getFirstName());
					session.setAttribute("sessionSmail", schoolList.get(i).getMail());
					session.setAttribute("sessionSpassword", schoolList.get(i).getPassword());
					session.setAttribute("sessionSarea", schoolList.get(i).getArea());
					session.setAttribute("sessionSaddress", schoolList.get(i).getAddress());
					session.setAttribute("sessionStel", schoolList.get(i).getTel());
					mov.addObject("AccountName", session.getAttribute("sessionAccountName"));
					mov.addObject("sdata", session.getAttribute("sessionSdata"));
					res = mov;
					break;
				}else {
					mov.addObject("msg", "メール認証が出来ていません。");
					mov.setViewName("login");
					res = mov;
					break;
				}
				} else { // メールアドレスは一致してるけど、パスが違う時
					mov.addObject("msg", "パスワードが違います。");
					mov.setViewName("login");
					res = mov;
					break;
				}
			}
		}

		if (res == null) { // スクールデータで照合されなかった場合、コーチデータと照合

			for (int i = 0; i < coachList.size(); i++) {
				if (coachdata.getMail().equals(coachList.get(i).getMail())) { // コーチメールアドレス一致確認
					if (cpass.equals(coachList.get(i).getPassword())) { // パス確認
						if(coachList.get(i).isAuthentication()) {
						session.setAttribute("sessionAccountName", coachList.get(i).getLastName()); // セッションにスクールネーム保存
						session.setAttribute("sessionCid", coachList.get(i).getId());
						session.setAttribute("sessionCdata", coachList.get(i));
						mov.addObject("AccountName", session.getAttribute("sessionAccountName"));
						mov.addObject("cdata", session.getAttribute("sessionCdata"));
						mov.addObject("msg", "ログインしました");
						mov.setViewName("result");
						res = mov;
						break;
					}else {
							mov.addObject("msg", "メール認証が出来ていません。");
							mov.setViewName("login");
							res = mov;
							break;
					}}else {
						mov.addObject("msg", "パスワードが違います");
						mov.setViewName("login");
						res = mov;
						break;
					}} else {
					mov.addObject("msg", "メールアドレスが存在しません。");
					mov.setViewName("login");
					res = mov;
				}
			}
		}
		return res;
	}

	@RequestMapping("/select")
	public ModelAndView select(ModelAndView mav) {
		mav.setViewName("select");
		required(mav);
		return mav;
	}

	@RequestMapping("/result")
	public ModelAndView result(ModelAndView mav) {
		mav.setViewName("result");
		required(mav);
		return mav;
	}

	@RequestMapping("/logout")
	public ModelAndView logout(ModelAndView mav) {
		mav.setViewName("logout");
		session.invalidate(); // クリア
		mav = new ModelAndView("logout");
		mav.addObject("msg","ログアウトされました");
		return mav;
	}
	
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public ModelAndView info(ModelAndView mav) {
		mav.setViewName("info");
		required(mav);
		return mav;
	}
	
	@RequestMapping(value = "/info", method = RequestMethod.POST)
	public ModelAndView info(
			@RequestParam("name")String name,
			@RequestParam("title")String title,
			@RequestParam("content")String content,
			@RequestParam("mail")String mail,
			ModelAndView mav) {
		String mails = "iichan.hiro@gmail.com";
		String contents = name + "様から"+"\n お問い合わせが投稿されました。\n【件名】"+title+ "\n【内容】\n"+content+"\n【アドレス】\n"+mail;
		JavaMailSample mailSend = new JavaMailSample();
	    mailSend.send("clubHubお問い合わせを受信しました", contents,mails);
		mav.setViewName("result");
		mav.addObject("msg","お問い合わせが送信されました");
		required(mav);
		return mav;
	}
	
	@RequestMapping(value = "/board", method = RequestMethod.GET)
	public ModelAndView board(ModelAndView mav) {
		mav.setViewName("board");
		List<PostData> plist = postrepository.findAll();
		mav.addObject("pdatalist", plist);
		mav.addObject("size", plist.size());
		mav.addObject("all","すべてを表示");
		required(mav);
		return mav;
	}
	
	@RequestMapping(value = "/board", method = RequestMethod.POST)
	public ModelAndView board(
			@RequestParam(name = "area", required = false) String area,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "category", required = false) String category,
			ModelAndView mav) {
		mav.setViewName("board");
		List<PostData> plist = new ArrayList<PostData>();
		if (area != null) { // エリア入力されている場合
			if (type != null) { // かつ部活も入力
				plist = postrepository.findByAreaAndType(area,type);				
				mav.addObject("size",plist.size());				
				mav.addObject("area",area);				
				mav.addObject("type",type);				
			}else if(category != null) {		// エリア入力＆運動or文化 
				plist = postrepository.findByAreaAndCategory(area,category);				
				mav.addObject("size",plist.size());				
				mav.addObject("area",area);				
				mav.addObject("category",category);				
			}else {		// エリアだけ入力
				plist = postrepository.findByArea(area);				
				mav.addObject("size",plist.size());	
				mav.addObject("area",area);	
			}
		}else if(category != null && type == null) {		// 運動or文化 だけ
			plist = postrepository.findByCategory(category);				
			mav.addObject("size",plist.size());						
			mav.addObject("category",category);			
		}else if(type != null) {	// 部活入力
			plist = postrepository.findByType(type);			
			mav.addObject("size",plist.size());		
			mav.addObject("type",type);	
		}else{ 		// 入力なしの場合
			plist = postrepository.findAll();			
			mav.addObject("size",plist.size());		
			mav.addObject("all","すべてを表示");
		}
		
		if(plist.size() == 0) { // 入力はしたけど抽出されなかったとき
			mav.addObject("msg","該当する部活動がありません。よければclubHub周知のご協力をお願いします。");
		}
		mav.addObject("pdatalist",plist);
		required(mav);
		return mav;
	}

	@RequestMapping(value = "/coach", method = RequestMethod.GET)
	public ModelAndView coach(@ModelAttribute("formModel") CoachData coachdata, ModelAndView mav) {
		mav.setViewName("coach");
		mav.addObject("formModel", coachdata);
//		Iterable<CoachData> clist = coachrepository.findAll();
//		mav.addObject("cdatalist", clist);
		required(mav);
		return mav;
	}

	@RequestMapping(value = "/coach", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView coach(@ModelAttribute("formModel") @Validated CoachData coachdata, BindingResult result,
			@RequestParam(value = "consent", required=false) boolean consent,
			@RequestParam("uploadfile") MultipartFile file,
			ModelAndView mav) throws Exception {
		ModelAndView res = null;
		if (!result.hasErrors() && consent) {
			String mail = coachdata.getMail();
			List<CoachData> cdata = coachrepository.findByMail(mail);
			List<SchoolData> sdata = schoolrepository.findByMail(mail);
			
			if(cdata.size()==0 && sdata.size()==0) {		// 登録アドレスがDBに存在しない場合
				UUID uuid = UUID.randomUUID();		// uuid発行
				String vali = uuid.toString();		// uuid Stringに
	            String URL = "localhost:8080/validate/"+uuid;
	        	List<SchoolData> slist = schoolrepository.findAll();
	        	// メール
	    		JavaMailSample mailSend = new JavaMailSample();
	    		mailSend.send("clubHub メール認証", "アクセスしてアカウント認証を完了してください"+"\n"+URL+"\n", coachdata.getMail());
	    		// メール認証
	    		coachdata.setUuid(vali);
	    		coachdata.setAuthentication(false);
	    		
	    		String image = imageConversion(file,150);
	    		coachdata.setImage(image);
	    		
	            String pass = DigestUtils.sha1Hex(coachdata.getPassword());		// ハッシュ化
	    		coachdata.setPassword(pass);	            // ハッシュ化したパスワードをDBに代入
	    		
	    		coachrepository.saveAndFlush(coachdata);
	    		       
	    		res = new ModelAndView("redirect:/coach");
			}else {
				mav.setViewName("coach");
				mav.addObject("msg","メールアドレスがすでに使われています。");
				Iterable<CoachData> clist = coachrepository.findAll();
				mav.addObject("cdatalist", clist);
				res = mav;
			}
								
		} else {
			mav.setViewName("coach");
			mav.addObject("consent","ご登録には同意が必須です。");
			Iterable<CoachData> clist = coachrepository.findAll();
			mav.addObject("cdatalist", clist);
			res = mav;
		}

		res.addObject("AccountName", session.getAttribute("sessionAccountName"));
		res.addObject("cdata", session.getAttribute("sessionCdata"));
		return res;
	}

	@RequestMapping(value = "/school", method = RequestMethod.GET)
	public ModelAndView school(@ModelAttribute("formModel") SchoolData schooldata, ModelAndView mav) {
		mav.setViewName("school");
		mav.addObject("formModel", schooldata);
//		Iterable<SchoolData> slist = schoolrepository.findAll();
//		mav.addObject("sdatalist", slist);
		required(mav);
		return mav;
	}

	@RequestMapping(value = "/school", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView school(@ModelAttribute("formModel") @Validated SchoolData schooldata, BindingResult result,
			@RequestParam("uploadfile") MultipartFile file,
			ModelAndView mav) throws Exception{
		ModelAndView res = null;
		String mail = schooldata.getMail();
		if (!result.hasErrors()) {
			
			List<CoachData> cdata = coachrepository.findByMail(mail);
			List<SchoolData> sdata = schoolrepository.findByMail(mail);
			
			if(cdata.size()==0 && sdata.size()==0) {		// 登録アドレスがDBに存在しない場合
				UUID uuid = UUID.randomUUID();		// uuid発行
				String vali = uuid.toString();		// uuid Stringに
	            String URL = "localhost:8080/validate/"+uuid;
	        	// メール
	    		JavaMailSample mailSend = new JavaMailSample();
	    		mailSend.send("clubHub メール認証", "アクセスしてアカウント認証を完了してください"+"\n"+URL+"\n", schooldata.getMail());
	    		// メール認証
	    		schooldata.setUuid(vali);
	    		schooldata.setAuthentication(false);
	    		
	            String pass = DigestUtils.sha1Hex(schooldata.getPassword());		// ハッシュ化
	    		schooldata.setPassword(pass);	            // ハッシュ化したパスワードをDBに代入
	    		

	    		// メール認証
	    		schooldata.setUuid(vali);
	    		schooldata.setAuthentication(false);	    					
			
			if(file != null) {
				String image = imageConversion(file,150);
				schooldata.setImage(image);				
			}
			schoolrepository.saveAndFlush(schooldata);
			
			mav.addObject("msg","メールをご確認ください");
			mav.setViewName("result");
			res = mav;
		}else {		// メールアドレス重複
			mav.setViewName("school");
			mav.addObject("msg","メールアドレスがすでに使われています。");
			Iterable<SchoolData> slist = schoolrepository.findAll();
			mav.addObject("sdatalist", slist);
			res = mav;
		}} else {	// エラーを持ってるとき
			mav.setViewName("school");
			Iterable<SchoolData> slist = schoolrepository.findAll();
			mav.addObject("sdatalist", slist);
			res = mav;
		}
		required(res);
		return res;
	}

	@RequestMapping(value = "/post", method = RequestMethod.GET)
	public ModelAndView post(@ModelAttribute("formModel") PostData postdata, ModelAndView mav) {
		mav.setViewName("post");
		mav.addObject("formModel", postdata);
		mav.addObject("sessionSid", session.getAttribute("sessionSid"));
		Iterable<PostData> plist = postrepository.findAll();
		mav.addObject("pdatalist", plist);
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		return mav;
	}

	@RequestMapping(value = "/post", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView post(@ModelAttribute("formModel") @Validated PostData postdata, BindingResult result,
			@RequestParam("uploadfile") MultipartFile file,
			ModelAndView mav)throws Exception{
		ModelAndView res = null;
	
		if (!result.hasErrors()) {
			postdata.setSchoolId((int)session.getAttribute("sessionSid"));
			postdata.setSchoolName(session.getAttribute("sessionSschoolName").toString());
			postdata.setSchoolCategory(session.getAttribute("sessionScategory").toString());
			postdata.setLastName(session.getAttribute("sessionSlastname").toString());
			postdata.setFirstName(session.getAttribute("sessionSfirstName").toString());
			postdata.setMail(session.getAttribute("sessionSmail").toString());
			postdata.setPassword(session.getAttribute("sessionSpassword").toString());
			postdata.setArea(session.getAttribute("sessionSarea").toString());
			postdata.setAddress(session.getAttribute("sessionSaddress").toString());
			postdata.setTel(session.getAttribute("sessionStel").toString());

			String image = imageConversion(file,400);
			if(image.length() > 10) {		// 添付なしのfileがnullにならないっぽい							
				postdata.setImage(image);													
			}	
			postrepository.saveAndFlush(postdata);
			res = new ModelAndView("redirect:/post");
		} else {
			mav.setViewName("post");
			Iterable<PostData> plist = postrepository.findAll();
			mav.addObject("pdatalist", plist);
			mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
			mav.addObject("sessionSid", session.getAttribute("sessionSid"));
			res = mav;
		}
		required(res);
		return res;
	}

	@RequestMapping("/article/{Id}")
	public ModelAndView article(@PathVariable int Id, ModelAndView mav) {
		mav.setViewName("article");
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		List<PostData> plist = postrepository.findAll();
		mav.addObject("pdatalist", plist.get(Id));
				
		if(plist.get(Id).getImage() != null) {
			StringBuffer data = new StringBuffer();
			String base64 = plist.get(Id).getImage();
			data.append("data:image/jpeg;base64,");
			data.append(base64);
			mav.addObject("base64data", data.toString());			
		}else {
			mav.addObject("base64data", getNoimage());			
		}
		
		required(mav);
		return mav;
	}

	@RequestMapping(value = "/chat/school/{sid}/coach/{cid}", method = RequestMethod.GET)
	public ModelAndView schoolChat(@PathVariable("sid") int sid, @PathVariable("cid") int cid,
			@ModelAttribute("formModel") ChatData chatdata, ModelAndView mav) {
		mav.setViewName("schoolChat");
		required(mav);
		if (session.getAttribute("sessionSid") != null) {
			if ((int) session.getAttribute("sessionSid") == sid) { // sessionとURLが一致しているか
				List<SchoolData> slist = schoolrepository.findAll();
				List<CoachData> clist = coachrepository.findAll();
				List<ChatData> chlist = chatrepository.findAll();
				List<ChatData> printchat = new ArrayList<>();

				for (int Sid = 0; Sid < chlist.size(); Sid++) { // 下の１はあとでコーチIDとして変数に
					if (sid == chlist.get(Sid).getSchoolId() && chlist.get(Sid).getCoachId() == cid) { // ログインしている人のチャット呼出
						printchat.add(chlist.get(Sid));
					}
					mav.addObject("receivechat", printchat);
					mav.addObject("sdatalist", slist.get(sid - 1));
					mav.addObject("cdatalist", clist.get(cid - 1));
					mav.addObject("sid", sid);
					mav.addObject("cid", cid);
					
					StringBuffer data = new StringBuffer();
					String base64 = slist.get(sid-1).getImage();
					data.append("data:image/jpeg;base64,");
					data.append(base64);
					mav.addObject("base64data", data.toString());
				}
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		}
		return mav;
	}

	@RequestMapping(value = "/chat/school/{sid}/coach/{cid}", method = RequestMethod.POST)
	public ModelAndView schoolChat(@PathVariable("sid") int sid, @PathVariable("cid") int cid,
			@RequestParam(value = "message", required = true) String message, ModelAndView mav) {
		required(mav);
		ChatData ch = new ChatData();
		ch.setMessage(message);
		ch.setCoachId(cid);
		ch.setSchoolId(sid);
		ch.setSender(false);
		ch.setDate(new Date());

		List<CoachData> clist = coachrepository.findAll();
		String mail = clist.get(cid-1).getMail();
		String content = clist.get(cid-1).getLastName() + "様から"+"\n チャットが投稿されました。\nログインして返信しましょう！";
		JavaMailSample mailSend = new JavaMailSample();
	    mailSend.send("clubHub チャットが投稿されました", content,mail);
		
		chatrepository.saveAndFlush(ch);
		mav.setViewName("redirect:/chat/school/" + sid + "/coach/" + cid);
		return mav;
	}

	@RequestMapping(value = "/chat/coach/{cid}/school/{sid}", method = RequestMethod.GET)
	public ModelAndView coachChat(@PathVariable("cid") int cid, @PathVariable("sid") int sid,
			@ModelAttribute("formModel") ChatData chatdata, ModelAndView mav) {
		mav.setViewName("coachChat");
		required(mav);
		if (session.getAttribute("sessionCid") != null) {
			if ((int) session.getAttribute("sessionCid") == cid) { // sessionとURLが一致しているか
				List<SchoolData> slist = schoolrepository.findAll();
				List<CoachData> clist = coachrepository.findAll();
				List<ChatData> chlist = chatrepository.findAll();
				List<ChatData> printchat = new ArrayList<>();

				for (int Cid = 0; Cid < chlist.size(); Cid++) { // 下の１はあとでコーチIDとして変数に
					if (cid == chlist.get(Cid).getCoachId() && chlist.get(Cid).getSchoolId() == sid) { // ログインしている人のチャット呼出
						printchat.add(chlist.get(Cid));
					}
					mav.addObject("receivechat", printchat);
					mav.addObject("cdatalist", clist.get(cid - 1));
					mav.addObject("sdatalist", slist.get(sid - 1));
					mav.addObject("sid", sid);
					mav.addObject("cid", cid);
					StringBuffer data = new StringBuffer();
					String base64 = clist.get(cid-1).getImage();
					data.append("data:image/jpeg;base64,");
					data.append(base64);
					mav.addObject("base64data", data.toString());

				}
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		}
		return mav;
	}

	@RequestMapping(value = "/chat/coach/{cid}/school/{sid}", method = RequestMethod.POST)
	public ModelAndView coachChat(@PathVariable("cid") int cid, @PathVariable("sid") int sid,
			@RequestParam(value = "message", required = true) String message, ModelAndView mav) {
		required(mav);
		ChatData ch = new ChatData();
		ch.setMessage(message);
		ch.setCoachId(cid);
		ch.setSchoolId(sid);
		ch.setSender(true);
		ch.setDate(new Date());
		chatrepository.saveAndFlush(ch);

		List<SchoolData> slist = schoolrepository.findAll();
		String mail = slist.get(sid-1).getMail();
		String content = slist.get(sid-1).getLastName() + "様"+"\n チャットが投稿されました。\nログインして返信しましょう！";
		JavaMailSample mailSend = new JavaMailSample();
	    mailSend.send("clubHub チャットが投稿されました", content,mail);
		
		mav.setViewName("redirect:/chat/coach/" + cid + "/school/" + sid);
		return mav;
	}

	@RequestMapping("/mypage/coach/{Id}")
	public ModelAndView coachMypage(@PathVariable int Id, ModelAndView mav) {
		mav.setViewName("coachMypage");
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		List<CoachData> clist = coachrepository.findAll();
		if (session.getAttribute("sessionCid") != null) {
			if ((int) session.getAttribute("sessionCid") == Id) {
				mav.addObject("cdatalist", clist.get(Id - 1));
				StringBuffer data = new StringBuffer();
				String base64 = clist.get(Id - 1).getImage();
				data.append("data:image/jpeg;base64,");
				data.append(base64);
				mav.addObject("base64data", data.toString());
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		}
		required(mav);
		return mav;
	}

	@RequestMapping("/mailbox/coach/{Id}")
	public ModelAndView coachMailbox(@PathVariable int Id, ModelAndView mav) {
		mav.setViewName("coachMailbox");
		required(mav);
		List<SchoolData> slist = schoolrepository.findAll();
		List<CoachData> clist = coachrepository.findAll();
		List<ChatData> chlist = chatrepository.findAll();
		if (session.getAttribute("sessionCid") != null) {
			if ((int) session.getAttribute("sessionCid") == Id) { // 他人のメールボックスを見るのを阻止
				List<ChatData> hozonA = new ArrayList<ChatData>();
//				mav.addObject("sdatalist", slist.get(Id - 1));
				for (int i = 0; i < chlist.size(); i++) {
					if (Id == chlist.get(i).getCoachId()) { // schoolID一致のID(チャットデータベースの行)全保存
						hozonA.add(chlist.get(i)); // schoolID一致チャットデータベース全保存
					}
				}
				List<ChatData> chatprint = new ArrayList();
				HashSet hs = new HashSet();
				for (int i = hozonA.size() - 1; i >= 0; i--) { // ここでは一旦コーチIDだけのリスト作成 hozonAに。
					if (hs.add(hozonA.get(i).getSchoolId())) { // hashにaddされることがtrueになるっぽい?
						chatprint.add(hozonA.get(i));
					}
				}
				mav.addObject("schoollist", slist);
				mav.addObject("chatlist", chatprint);
				mav.addObject("path", "/chat/coach/" + Id);
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		} else {
			mav.addObject("error", "ログインしてください");
		}
		return mav;
	}

	@RequestMapping(value = "/edit/coach/{Id}", method = RequestMethod.GET)
	public ModelAndView coachedit(@ModelAttribute("formModel") CoachData coachdata, @PathVariable int Id,
			ModelAndView mav) {
		mav.setViewName("coachedit");
		List<CoachData> clist = coachrepository.findAll();
		if (session.getAttribute("sessionCid") != null) {
			if ((int) session.getAttribute("sessionCid") == Id) {
				mav.addObject("formModel", clist.get(Id - 1));
				StringBuffer data = new StringBuffer();
				String base64 = clist.get(Id-1).getImage();
				data.append("data:image/jpeg;base64,");
				data.append(base64);
				mav.addObject("base64data", data.toString());
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		} else {
			mav.addObject("error", "ログイン情報とURLが一致しません");
		}
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		mav.addObject("path", "/edit/coach/" + Id);
		return mav;
	}

	@RequestMapping(value = "/edit/coach/{Id}", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView coachedit(@ModelAttribute("formModel") @Validated CoachData coachdata, BindingResult result,
			 @PathVariable int Id,@RequestParam("uploadfile") MultipartFile file,
			 @RequestParam("image")String img,
			ModelAndView mav) throws Exception {
		List<CoachData> clist = coachrepository.findAll();
		if (!result.hasErrors()) {
				String image = imageConversion(file,150);
				
				if(image.length() < 10) {		// 添付なしのfileがnullにならないっぽい
					coachdata.setImage(img);									
				}else {
					coachdata.setImage(image);													
				}	
				
			String pass = DigestUtils.sha1Hex(coachdata.getPassword());		// ハッシュ化
    		coachdata.setPassword(pass);	            // ハッシュ化したパスワードをDBに代入
			
			coachrepository.saveAndFlush(coachdata);
			mav.setViewName("result");
			mav.addObject("msg", "修正が完了しました");
		} else {
			mav.setViewName("coachedit");
			mav.addObject("formModel", coachdata);
			mav.addObject("path", "/edit/coach/" + coachdata.getId());
		}

		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		return mav;
	}

	
	@RequestMapping("/mypage/school/{Id}")
	public ModelAndView schoolMypage(@PathVariable int Id, ModelAndView mav) {
		mav.setViewName("schoolMypage");
		required(mav);
		List<SchoolData> slist = schoolrepository.findAll();
		if (session.getAttribute("sessionSid") != null) {
			if ((int) session.getAttribute("sessionSid") == Id) {
				mav.addObject("sdatalist", slist.get(Id - 1));
				StringBuffer data = new StringBuffer();
				String base64 = slist.get(Id - 1).getImage();
				data.append("data:image/jpeg;base64,");
				data.append(base64);
				mav.addObject("base64data", data.toString());
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		}
		return mav;
	}

	
	@RequestMapping("/mailbox/school/{Id}")
	public ModelAndView schoolMailbox(@PathVariable int Id, ModelAndView mav) {
		mav.setViewName("schoolMailbox");
		required(mav);
		List<SchoolData> slist = schoolrepository.findAll();
		List<CoachData> clist = coachrepository.findAll();
		List<ChatData> chlist = chatrepository.findAll();
		if (session.getAttribute("sessionSid") != null) {
			if ((int) session.getAttribute("sessionSid") == Id) { // 他人のメールボックスを見るのを阻止
				List<ChatData> hozonA = new ArrayList<ChatData>();
//				mav.addObject("sdatalist", slist.get(Id - 1));
				for (int i = 0; i < chlist.size(); i++) {
					if (Id == chlist.get(i).getSchoolId()) { // schoolID一致のID(チャットデータベースの行)全保存
						hozonA.add(chlist.get(i)); // schoolID一致チャットデータベース全保存
					}
				}
				List<ChatData> chatprint = new ArrayList();
				HashSet hs = new HashSet();
				for (int i = hozonA.size() - 1; i >= 0; i--) { // ここでは一旦コーチIDだけのリスト作成 hozonAに。
					if (hs.add(hozonA.get(i).getCoachId())) { // hashにaddされることがtrueになるっぽい?
						chatprint.add(hozonA.get(i));
					}
				}
				mav.addObject("coachlist", clist);
				mav.addObject("chatlist", chatprint);
				mav.addObject("path", "/chat/school/" + Id);
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		} else {
			mav.addObject("error", "ログインしてください");
		}
		return mav;
	}

	
	@RequestMapping(value = "/edit/school/{Id}", method = RequestMethod.GET)
	public ModelAndView schooledit(@ModelAttribute("formModel") SchoolData schooldata, @PathVariable int Id,
			ModelAndView mav) {
		mav.setViewName("schooledit");
		List<SchoolData> slist = schoolrepository.findAll();
		if (session.getAttribute("sessionSid") != null) {
			if ((int) session.getAttribute("sessionSid") == Id) {
				mav.addObject("formModel", slist.get(Id - 1));
				StringBuffer data = new StringBuffer();
				String base64 = slist.get(Id-1).getImage();
				data.append("data:image/jpeg;base64,");
				data.append(base64);
				mav.addObject("base64data", data.toString());
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		} else {
			mav.addObject("error", "ログイン情報とURLが一致しません");
		}
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("path", "/edit/school/" + Id);
		return mav;
	}

	
	@RequestMapping(value = "/edit/school/{Id}", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView schooledit(@ModelAttribute("formModel") @Validated SchoolData schooldata, BindingResult result,
			@PathVariable int Id,@RequestParam("uploadfile") MultipartFile file,
			 @RequestParam("image")String img,
			ModelAndView mav) throws Exception {
		List<SchoolData> slist = schoolrepository.findAll();
		if (!result.hasErrors()) {
			String image = imageConversion(file,150);
			
			if(image.length() < 10) {		// 添付なしのfileがnullにならないっぽい
				schooldata.setImage(img);									
			}else {
				schooldata.setImage(image);													
			}	
			String pass = DigestUtils.sha1Hex(schooldata.getPassword());		// ハッシュ化
			schooldata.setPassword(pass);	            // ハッシュ化したパスワードをDBに代入
			schoolrepository.saveAndFlush(schooldata);
			mav.setViewName("result");
			mav.addObject("msg", "修正が完了しました");
		} else {
			mav.setViewName("schooledit");
			mav.addObject("formModel", schooldata);
			mav.addObject("path", "/edit/school/" + schooldata.getId());
		}

		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		return mav;
	}
	
	@PostConstruct
	public void init() {
		SchoolData s1 = new SchoolData();
		s1.setAuthentication(true);
		s1.setArea("奈良県");
		s1.setSchoolName("奈良県立鹿高校");
		s1.setCategory("高校");
		s1.setLastName("なら");
		s1.setFirstName("しかまる");
		s1.setMail("n@n");
		String pass = DigestUtils.sha1Hex("0000");		// ハッシュ化
		s1.setPassword(pass);	            // ハッシュ化したパスワードをDBに代入
		s1.setAddress("奈良町");
		s1.setTel("000-0000");
		s1.setImage("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");
		schoolrepository.saveAndFlush(s1);

		SchoolData s2 = new SchoolData();
		s2.setAuthentication(true);
		s2.setArea("大阪府");
		s2.setSchoolName("大阪府立くいだおれ中学校");
		s2.setCategory("中学校");
		s2.setLastName("おおさか");
		s2.setFirstName("たこ");
		s2.setMail("o@o");
		String pass1 = DigestUtils.sha1Hex("1111");		// ハッシュ化
		s2.setPassword(pass1);	            // ハッシュ化したパスワードをDBに代入
		s2.setAddress("大阪町");
		s2.setTel("000-0000");
		s2.setImage("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");
		schoolrepository.saveAndFlush(s2);

		CoachData c1 = new CoachData();
		c1.setAuthentication(true);
		c1.setLastName("山田");
		c1.setFirstName("太郎");
		c1.setJob("公務員");
		c1.setMail("yama@da");
		String pass2 = DigestUtils.sha1Hex("0000");		// ハッシュ化	            // ハッシュ化したパスワードをDBに代入
		c1.setPassword(pass2);
		c1.setArea("奈良県");
		c1.setAddress("鹿町");
		c1.setTel("00");
		c1.setExperience("バレーボール10年");
		c1.setMessage("土日空いてます");
		c1.setImage("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");
		coachrepository.saveAndFlush(c1);

		CoachData c2 = new CoachData();
		c2.setUuid("5676d035-f44c-45d9-8497-f4420f598fab");
		c2.setAuthentication(false);
		c2.setLastName("鈴木");
		c2.setFirstName("花子");
		c2.setJob("会社員");
		c2.setMail("suzu@ki");
		String pass3 = DigestUtils.sha1Hex("1111");		// ハッシュ化	            // ハッシュ化したパスワードをDBに代入
		c2.setPassword(pass3);
		c2.setPassword("1111");
		c2.setArea("大阪府");
		c2.setAddress("たこ町");
		c2.setTel("11");
		c2.setExperience("吹奏楽3年");
		c2.setMessage("金管楽器教えるのが得意です！金管楽器教えるのが得意です！金管楽器教えるのが得意です！金管楽器教えるのが得意です！金管楽器教えるのが得意です！金管楽器教えるのが得意です！金管楽器教えるのが得意です！金管楽器教えるのが得意です！");
		coachrepository.saveAndFlush(c2);

		CoachData c3 = new CoachData();
		c3.setAuthentication(true);
		c3.setLastName("佐藤");
		c3.setFirstName("一郎");
		c3.setJob("経営者");
		c3.setMail("sa@to");
		c3.setPassword("2222");
		c3.setArea("京都府");
		c3.setAddress("金閣村");
		c3.setTel("22");
		c3.setExperience("野球部15年");
		c3.setMessage("監督経験あり！");
		coachrepository.saveAndFlush(c3);

		PostData p1 = new PostData();
		p1.setArea("奈良県");
		p1.setSchoolName("奈良県立鹿高校");
		p1.setCategory("高校");
		p1.setLastName("なら");
		p1.setFirstName("しかまる");
		p1.setMail("n@n");
		p1.setPassword("0000");
		p1.setAddress("奈良町");
		p1.setTel("000-0000");
		p1.setCategory("運動部");
		p1.setType("バレーボール");
		p1.setCategoryDetails("女子6人制");
		p1.setTitle("全国大会出場を目指してます");
		p1.setContent(
				"前回大会は県ベスト４でした。優秀な生徒が集まっていますが、昨年監督していた先生が定年退職されて、生徒が求めるような高い指導が出来ていません。今年こそは全国大会出場を目指しています。一緒に指導して頂ける方を募集しています。よろしくお願いします。");
		p1.setReword(true);
		p1.setRewordDetails("お給料はお支払いできませんが、交通費・スポーツドリンクは支給させていただきます。");
		p1.setWant("全国大会出場などの実績のある方だと望ましいですが、その他熱い気持ちがあれば是非一度練習にいらしてください。");
		p1.setWantDate("平日 16:00-19:00（水曜のみ外練）土日 他の部活との兼ね合いで不規則です。曜日の指定はないですが、最低でも週1回程度来ていただけると助かります。");
		p1.setSituation("部員数12人 マネージャー2人います。");
		p1.setOtherText("男子バレー部もコーチ募集しています。");
		p1.setSchoolId(0);
		p1.setImage("/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCABwAJYDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDqh4r15uItAA+trKf51Kuu+LJVPl6IB9LbH82p48OXzD97r8p9Ruc/1qWPwmXT95q0zj6N/U1epjoVTqvjhs7dPeIf7kI/m1RNf+OW6yrF/vPAv8qtt4Pst37y9nY/7gp6+ENKHWe5P/AVpcrGmjNM3jFvv6taJ/vXyL/IVEw8RH/WeI7FM/8AUVP9FrbXwno+fvXDfiBUy+FdGHJimP8A20o5WO6OYNtqTn974pss/wDX5I/9KifTWf8A13iiz/COV/61148NaKnWzkb/ALampx4f0YdLD85CaOUfMjhTpNj/AMtPEkDf7tjIf/ZqhOm6Ut3Gra7K0exyWS0xg5XjB/zxXoX9i6SnTT4vxJNUprDT012zgXT7cI8MpI2dwU/+vRyjuciLDQQfm1TUn/3LdB/MVILfw8gH77WH/CIV3YsLND8thbD/ALZ1YSC34xbQKfaMUcgcx55t8PjpZ6nJ/vT7f5U8DRSP3ehXr/W9kr0NkVThUQf8AH+FJvYdAvX0o5RORwCw2Lcx+F5G/wB+eRv6VKtqh+54TtT/ANdEZv513fmOQRuI+hpnmNnBZj9Wp8guY4xbO5H+q8K6aPrak/1p62mrrkx6DYx/7tsB/M12YcgYzx7mo3JJ5waOUXMcp5PiT+Gxt0HoIUorpmOOMjNFHKS2iPI3DFWEYBCQe9eTnXdRckDWGBBwdqPx/wCOili1HUpXWIa1cjewXhJOp/EU+cfKu56qxG4/pQCOvNeUmeaQ86/cN7CCX+rUqxmR1VtTu2yQP9S39Wo5vIEl3PVtyjPFL5ig4JA/GuAm8H3ivEsd5NMXXceVGPzqO78GXcFpLcSyT7YkaQhXTJAGTjj2p3fYdl3PQTcQg/NJGPqwpjX9qG+a5hH1kAryeHT7do3lli1QBcAAeVk5z7e1Siys8ZFpqrfV4x/Sp5mP3e56i2qWCjm+th9ZV/xrMutSsTrVlKl5blFSVWYSjC5C4z+VcVYWOnyTyGezv0jiTzG/eglhuVcDGOfm/StIweHxPE0el6o67iGDzEE8HGMGk5MpJPY6x9e02M86hb49nzTG8VaOvW+j/AE/0rktUsLKS2SXT9IlikMqxbLueQhsgnOQR6VUOhX6xl20/TQP9+Vv5ms5V4wdpMpU29jtJPGGir0vN30ib/Cq7eN9IUcSSt9IzXDIVgkuDPp2mv5URYKYiQTuA55z3NVW12NfuaRpKf7trn+ZNWp82qJcUtzvH8faUvGLg/8AAR/U1Xf4haWv3Y5vxKD+tcrourvd65Y2zW1msUs6IwW3UZBPNbvjmRtIi05rMiEyq24qo5wFx1Huaq7J0LJ+IliR8lvIx/31qM+P0Y/u9Omb6P8A/Y1wx13VX4F9OCeykD+QrU0yXXHF1JJeXKf6P8hZySD5ic4+mfzpXYvdOgfxxMxyNHm/Nj/7LRXJXmvanbP5ba3ebh1CTNx+Roo1C6OosbePEnyjIdu3vUGravb6M9uv2dp7iVsxxRjk4I/+tVfUtai0KNnkjaV5JnCKpx39awz40b7cJ54fsbrbOEIUuTkg+3GFGMd6mcrLTcdGg5z12Ou0mSC8sklWMqfusjjBVhwQfxFUtQ8UaLYakNPmm2zAgMwQlUJ6AntVHwz4w0y+tit1ItrcmXAQjHmFmwCPxI+n0rlL3xZp8/8AbCR6bHtvGOyRlBY/KBknqOhIweppuTSTsEaKc2nse33motZPDKlsl1iMjy2xhsZ9eKz7LxNDri3yNpz2TrbOgBRVVuO2CeetP0rVtM1TToL+zvYVgEUy+a3SEhTjcO2Mg81y/g19MtLiWOLV5JJL6ykZoZjnznyrbhzwQC34ZNVKSTSLjTbi32F1TX7LQ7ZWuklfeu7bEm4gAgEnkYHzD861dNvrXVNOhvbVt0MoypIwfQg/iK44eMLSPV4ozEhtzbyBpHQEk4ztAweuMduvtVzQtesIPB5nG208gEPGozsd2OMDqQTk98Cp5tSHR9xNbmnqV3r1tq9qugQRyS7S0xkAxt3LgD8RXQCXxLcrp1ybu0i8474coFIzGx5GPT1rz2HWIrrxylnpWp/aLWdFkQO2Qr8AqGJOPX2yfSuhudOsPCekGXW5PtMkd15bLbsFAzEWVAPYkcnsaxdaPOoN6vY39lywstyvP44vrm8W01KCZlFwfs1zsVUYruBHAHXHFLqPibVJVjt9KtUnuHJyrtgBQOTkkD071zd14kWTT1SOYFWkLpGpYiNcnAJ6EgHHA7ZqnZeInsL+KS3ucsQCwAIIHXHbnpUVaDk1OS2NqfxKMepqLftcyXfmRujm3fKEYIII4I+orHYyCESspEbHAY9CfarFvcNrfiCbZcSWglR5NzOd5GcAZHbkenA+lVINYiuNQtf7cYmGG3kiXYPkZt7ZOeMnGORnt3q4NRViamHfPvoafhqU/wDCUaX/ANfUf/oQrv8Ax3p0upjSkjYBVRizHtkL/hXn/hC+0ttRtjeXSwPblZ1kdchgpzgkdDXQeLfEFl4l06z07StSEZklWG4Loykx4OSCR04H1zW3OuW5g6M72sL9l0rQIBJcSBpSMgdXP0Hasa48QzXkGpLCghiW1yuD83+tjHX8aw9Nt9Oi1STTr+/uWhSXy47mKIMAvQZBIP8AhXp2n/De3tg0qTzXcFxFtO6NACNysCCHOfuihdzN02nqebaZoGp6srPaQGQL1O4D+Zor2rTPDK6UrC2t5Qr4yBsH/s1FT+87CcYnkvivxdYazq8UcMZXTY5FVEm+bClTvPHIJOO/bpWV4w8TWuvW9tDa2kMPkn5pEjVSRjGAR1HTt2FcTCxlbZnGTXZeAfClp4s1G6gn1dLP7OFZUKjdKpyDj0xgfnW1WjdKokdFOdvcuU9N0trjR7jVrq+hW1s5FxbAne/I6Y+7169eK6uPSvDdhp12sVrLcQXseFkY/cPJ299pBIOT2A64rkfFUFlpfiPUtKsbkS2ayLloz8u8D5gPYNkfhTp70LZiUlm2YMzI5w4yBjg45rGrCajFvqaQlFNjvC2pXOnNJYPIyRG4AljBz/CwPTr0FTSaLqtvPNeWdvLLFAxVWUbgqnsO+OetZ/h+3v5L+e4tVjlaMNLIZOhxk8e56fjWmviKcJcxPdeXDMhdkxnc3Yfj9cVi1JSbTOn20PYxg46p3uYIS4ScXEisPLY7Ux71o6lFdp4ctdTe0MdpcSSRRy5++w4xjtghqrajqALRSIip5iBmVBgZyR0rY1G/nTwNb6RqFjcQs12Lu0aThShUglfxJ/PNaxd3qcvTQ5rQ7t9Ov4r2NiHhdSBnrzyPxr0Hxjrn/CQeDUv2g+zm51RnKq27G2JV9BXnpYhNsaFmzjAXJrYn1J5vDFrpUitDJayPOVkBBkDYxgbfQH8xSqU4Nxlb3k9/LX9bAr/Ijgv4rBZXmgMySxlCCSvXuKzJJJLfULiKSPY8eEZGIJDDjtXU+CdA03xRNdfb2cJDtEcavtJznJ4Bpbf4aX+qQX91Z6nZv5E5jKuzbiBg5zjrz+lbc/PPliEk4wUnsZOsXNtb3Pm2NwJo7m3RiM7jExA3KSSTwQfeqOo602o2llbRQRW0NqhTbD1c5JLEnnJzVO0sWkuGtxICdxXcOh+lLJp7Wl6qyN8p5JA4B96xaS9TRSnNq+xpyNp8VtZ/Z5laXyy1zgtzk52cjsBg1raxb6RePBf2lw8ZlY77ZWOQRg5wRgD6dfwrlJ4Ywcpjj0NWo73EKq/OPumofc7Yxje0+h2fhPxTa6ak1jDFGk3luRcLCC7Ankc98dOwxUHhTxTq/kf2fJqd7FHG3yhZmBUE8jGR71wsU09vcLcQj5lJOMZyK6Kz1lJLlZZLcK7feI4p7bCpQp1JWn93dHslnpMupQCSDxfcyccgSPuX6jPFFeVZGprudZI3UnDwybWx6H2oq1ONtTGpltTmfI7o42HCb2xyBjP1qNJGWcMu7Of4Tiuk8S6Lf2Vvb6jNYpZ2l6f3aJj5QAMbsdCc9+eKxFVYzuJXg9PWu51f3UVE4HG0ncgcMigd85rpLG7UaPGDAFRJMtMRlS2cgY7nFYuq36X85lWCKDgARxLhQAMVt2tkr+Abq8a5VXN2hWLOSQoKk8dDlxweSOe1Z88ZUmpbobi1L3dSPSvGOo6JbXlnbrHsnLMM7gUY9xggH8azrRra3milngEoEgDK7YBHp7VnyAtkE5A6ECtfSfD99r0q2NoqG4lVnUO+1QAM8n17fjWCguhTqN7jtODefbywwpcTfdSJ+57Hr29OnFa/irxPNr9qkM9nFB5GdrZB3P3IIA4OOnNc1Hc3elSRSRCW3mjOBLtwQfY0gc6hLLLLO7uSXJb8c5984rvUaLcYcqbS/F73/rQy5pJPUs6Y8EVlPfPeNb3sDBrYIgO/r+meD9e9Q6je3dzcx3k7N50gU5yMYxxjH41VumYYjcxqYkCAL3Hr+ta/hzw1qeshryKIG1tlMpaRgofaDkLnqRgk+mPcV59rO7NuZtWRLpiazFeR/wBlrMskz+QGj6M7DJU/zrftrvVdIufsDwXNvBONheaEp9ozwXywBPXIx0475z3/AMA9Ys5tM1bTm2Lcm589VPVlKgfpj9ab8b9StZ5NPto5CJrZHeQ9AA20AZ/A0vtk3drHm1mj+G9Uhl8lEvIppElgduHQkjIPPBB4+nfNXtc0O88QNdXy/ZIYoovOZ4T+7VeNqkgDLHceMZqrqFzdXOu3TWMjyWdxIcMp3B8HjB9OTj2rPiumsLv7HyiMgOB8uc+o6HtWTkm721PVjhbRWvusTwh4bTWzOss0UOBujaTGGIPI6j1/UU280C7hOoOYolisGKykPwcNt3KOpGam8P31pYWVza6g1xazx5+zzwN8ySbuR9Dgf56Psr0Mslhcs4+0RlXY8/eHr/nn6VTWtzPDwuuW9mc55sKkMGBHfA61ctHVJNjDaD0Y/pV698PyaR4cN1O0ZlnfCAOv3MkAgHk5w3Tpx71jSLGLS2yhVyhLHP3hkgH9KrkvsZrEuEnzLVG/bSlAQjKx7kHiiovCFul1eyiV0CLF1kCkZyP7340VSo36mn9ptaKP4mTqGp3t3EYZ7hpELBmz3IGBk+gHQdBViDRXk0uC5e6hVpyQkbD5sDP+FO1zSZrXVJEWF0ikc+UxUqpHsT1x0roLO1uY7WCIRKdkYG5VIx+YH6U78ux5zdziJraSC9e2YLuTPQ5B4zkH6VsaDai/juNOSZYri5ZFiDjgkNnrjjgVevfDd9NeG5jzuPB6+mMUml+F9QutT8tN9u0Y3xsUzlh0GKFJdQ6Fqy8OXWm+IYbXWLOCPcjMHuGPlEeoK8E+x/Guj0e8ju9Tn1Y2kFmka4KwAKCqgnPH8/as/UdM8S6sFtr6RFaJsFVBGfrV600bWhZiwndTAuMGMA5XrtPtRzK9wS0scF5k2rlLdpApkfC7uee1bQ02/wBI8J6haLZwXRuGWRpY8logvPcfXp6mvQH8LW1zMJniiTAAVEGFGP8AOauR+HLXbgDp1BPWobuWrrY8JtrN7i7R7oOsZcbyVIzXS3OoeVpFxZWdw0Fuyk7BgbhzkA9RkdR3/n6i/hu0liKNAjg9nHFZUvw/0llO202t/e3E4/M4pNXHF8qPJdH1G+0vUbe4s7uSzfhDImOQTyCDwR9a6Dxlqdpf3Fklm8jSSqftM0h++5PBPuPbA9BXTSfDW0HzNJM/90s/+FULv4cNKu2FypB/Sn1uT0sc3ba5c6dpQ0+G4KiKTzF3Dvjn/P8AiaZHdTa1qxvZ4/OnuHwQpx8x7YrRvPh7qij9ywf13daq2VjcaHq0EN7A6jzlO4rwRkVcYxk9S1XqRVr7GHqcr/2jOpLbw2H3ddw65/Gp01i5fTYrFtpjjl81WI5Bx0+ldVP4Dm1K9ubxJljWeVpFXb0BJOO38qpXPgPUbKVRChnB/u8UXtoRKbk+Z7mPeXqy6dFARlyd4b06jBrPlkkZER3LKi4QegPOPzrv4vAYaBvOjH2gjghiuPr2NYUvg/VIbmRjYSGCM5IU5LKPQDPWmrJFVXzPmuHhq/0vTY7kXbHzCwCkZII56YH9aKuaB4QubxpJLy1lSIDC7uCx/Giq57aGLVz0y509bwr9ojVQg+VAc/rgfpQulxDAI+Ud81oADd1XdnBwvNLtZkHGASc5BJNc5ZAunxIMLjnsO3+eaeLONgQyqcZODzU+1GbAPHcFuP8APFOVVOSQCc88imBCLeKPEaABR7f0qQREhgB2+9TsgoOOeOo/XmnbxgLhS3cL6/h3pDFCbSxPT9acAXz8uSDgY+tRqAytjkjPzDPSneaGUEKMAALj0pgAwemQcdqbztHzHnsKUy7lySGVQMkCmkjguMrnqKQxACxwCDjn65oMYzswTj2zSyOCF3DIbHTGPwpowMsF7ducUANC55zk+38qaYlJBVFJ7GpNxKZJ+bPUnmk5GCRkjov4fSmBGWGc4woOOB+tDfdyRkep4z2pwfjbs+Ukd+aTO75VDBgT24oFYiaPktgYNNI3AN6cZ609iNzdD2Ybh+lNaVHcEIcg5yO2KLiIpM4HzgdOtFOnZQRhVHbJbj8qKBH/2Q==");
		postrepository.saveAndFlush(p1);
//		
		PostData p3 = new PostData();
		p3.setArea("奈良県");
		p3.setSchoolName("大仏高校");
		p3.setCategory("高校");
		p3.setLastName("なら");
		p3.setFirstName("しかまる");
		p3.setMail("n@n");
		p3.setPassword("0000");
		p3.setAddress("奈良町");
		p3.setTel("000-0000");
		p3.setCategory("文化部");
		p3.setType("吹奏楽");
		p3.setCategoryDetails("女子6人制");
		p3.setTitle("全国大会出場を目指してます");
		p3.setContent(
				"前回大会は県ベスト４でした。優秀な生徒が集まっていますが、昨年監督していた先生が定年退職されて、生徒が求めるような高い指導が出来ていません。今年こそは全国大会出場を目指しています。一緒に指導して頂ける方を募集しています。よろしくお願いします。");
		p3.setReword(true);
		p3.setRewordDetails("お給料はお支払いできませんが、交通費・スポーツドリンクは支給させていただきます。");
		p3.setWant("全国大会出場などの実績のある方だと望ましいですが、その他熱い気持ちがあれば是非一度練習にいらしてください。");
		p3.setWantDate("平日 16:00-19:00（水曜のみ外練）土日 他の部活との兼ね合いで不規則です。曜日の指定はないですが、最低でも週1回程度来ていただけると助かります。");
		p3.setSituation("部員数12人 マネージャー2人います。");
		p3.setOtherText("男子バレー部もコーチ募集しています。");
		p3.setSchoolId(1);
		p3.setImage("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");
		postrepository.saveAndFlush(p3);
		
		PostData p2 = new PostData();
		p2.setArea("大阪府");
		p2.setSchoolName("くいだおれ中学");
		p2.setCategory("中学");
		p2.setLastName("おおさか");
		p2.setFirstName("たこ");
		p2.setMail("o@o");
		p2.setPassword("1111");
		p2.setAddress("大阪町");
		p2.setTel("000-0000");
		p2.setCategory("文化部");
		p2.setType("書道");
		p2.setCategoryDetails("女子");
		p2.setTitle("経験者の方募集中です");
		p2.setContent("経験者の教師がおらず、困っています。一緒に指導して頂ける方を募集しています。よろしくお願いします。");
		p2.setReword(true);
		p2.setRewordDetails("");
		p2.setWant("経験者だと嬉しいです。");
		p2.setWantDate("平日 16:00-19:00。長期出来ていただける方が嬉しいです。");
		p2.setSituation("4人います。");
		p2.setOtherText("よろしくお願いします");
		p2.setSchoolId(1);

//		p2.setImage("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");
		postrepository.saveAndFlush(p2);

		ChatData ch1 = new ChatData();
		ch1.setSender(true);
		ch1.setMessage("鹿高校さんこんにちは！");
		ch1.setSchoolId(1);// 鹿高校
		ch1.setCoachId(1);// 山田
		ch1.setDate(new Date());
		chatrepository.saveAndFlush(ch1);

		ChatData ch2 = new ChatData();
		ch2.setSender(false);
		ch2.setMessage("山田さんこんばんは！！");
		ch2.setSchoolId(1);// 鹿高校
		ch2.setCoachId(1);// 山田
		ch2.setDate(new Date());
		chatrepository.saveAndFlush(ch2);

		ChatData ch3 = new ChatData();
		ch3.setSender(true);
		ch3.setMessage("くいだおれ中学さんこんにちは！");
		ch3.setSchoolId(2);// くいだおれ中
		ch3.setCoachId(1);// 山田
		ch3.setDate(new Date());
		chatrepository.saveAndFlush(ch3);

//		ChatData ch33 = new ChatData();
//		ch33.setSender(true);
//		ch33.setMessage("くいだおれ中学さんこんにちは！");
//		ch33.setSchoolId(2);// くいだおれ中
//		ch33.setCoachId(1);// 山田
//		ch33.setDate(new Date());
//		chatrepository.saveAndFlush(ch33);

		ChatData ch4 = new ChatData();
		ch4.setSender(false);
		ch4.setMessage("おめでとう！");
		ch4.setSchoolId(1);// 鹿高校
		ch4.setCoachId(2);// 鈴木
		ch4.setDate(new Date());
		chatrepository.saveAndFlush(ch4);

		ChatData ch5 = new ChatData();
		ch5.setSender(true);
		ch5.setMessage("すごい！");
		ch5.setSchoolId(1);// 鹿
		ch5.setCoachId(3);// ？？
		ch5.setDate(new Date());
		chatrepository.saveAndFlush(ch5);

		ChatData ch6 = new ChatData();
		ch6.setSender(false);
		ch6.setMessage("よくやった！");
		ch6.setSchoolId(1);// 鹿高校
		ch6.setCoachId(2);// 鈴木
		ch6.setDate(new Date());
		chatrepository.saveAndFlush(ch6);

		PhotoData ph1 = new PhotoData();
//		ph1.setId(1);
		photorepository.saveAndFlush(ph1);
	}

	public String getNoimage() {
		return "data:image/jpeg;base64,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icG4Cs8g1KLyG03k6va6Ja/nBsYOiCPW3t0sVhnObiKFRmQ5S4tOSQJs/wB10i4IJkQgx2GpF9mtc49GguP7/RB1/hPwHrV6ra2MpupUAA4U3iHVDyBEy0deoQShw+HDWhrQGtaAAAIAA0AA0CC6goUELOPewHUtqYgx6arhWBiB62iQDz9UyepQdR8KOHqZcUXAinmZlHLO4HNFr+lrSTPSyC74q90n1KWHxDGZvLcab8rSXAP90wOQIjsSOqDn3ATcuudp0HPo1W06IfUe57HMDXZCGXI1JOmqCXzUAoOC+Jrc6tW8jEU2VKoptex7Gguaxs5s8C4JmCY0aOiDj3DHh7i8ZiWClSNNlJzKj6tVrmMYA8EQCJe4xZrfnAkoJA+Irc6visHTOHa6pUo1cxY2JLHNIeQD94WiLwSg4vwr4P4nE4ym6vh6lLDU4qVDUYWZiDLWNkAyXCTGgCCYFJkCOlkGF313Up43DVcNUALajYBi7XC7Ht6Oa4AgoIZbV4S46lXNF2GquDXNb5jKbnMLC4AOkWgi8A6oJuswDTTFNzQWlgYWkSC3LBEHkgjFxR8P9fDvfWwbXVqBJf5bR9pSn7obfOwdrxysg43W2dUDogsM5YcHTPSCAZnkg+3Z+6mIrVDSZQrVKjbuaGuzNgxJaR6b2Qd14N+Ht7KjcRtCm3K0B1LDk53ZiZDqkGBkizCXSTfRBIPG4fMx7YBzNc2CJBkEQQRBF7joggjt7drFYaoadek5hBcB6fQ65s0gZcsaRyQfA3DEjNkcGzlktME9JiJgEjsEHtlF2WQ1xZOsEiQJidJHTVB3jwubRxD312l1X2ZtMFuaTT8wuiGOOjtSQ20IJFoCAgICAgICAgICAgICDzlQAxB6QUhAhBVBSEHyY/ZFOqAKtNlQA5gHtDgDyMEET/jog84bYlFjnPZRpMe6Mz202Nc6BAzODQTAsJKD7QgqgIKFB8uP2XTqtLKtNlRpsWva1zSOhDgQUHvCYBlMQxjGDUhjQ0E/BoCC85k6gH4oDWRYCPggqgqg8lqAG/BAhBVBVAQeYQVQCEFl2DaSCWtJGhygkfAkSEHttEAkgAE6kWJ+KD2EBB4fQBsQCO4B/NBYxGyaT25HU6bmSDlcxrmy24OUiJB0PJBWhs2m0FrabGtJLiGtaAXHUwBEnmeaD1g8Aym0MpsZTaNGsaGtHwAAAQfQgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICD/9k=";
	}
	
}