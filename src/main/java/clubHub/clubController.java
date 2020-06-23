package clubHub;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import clubHub.repositories.PostDataRepository;
import clubHub.repositories.SchoolDataRepository;
import clubHub.repositories.CoachDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

@Controller
@SpringBootApplication
public class clubController {
	@Autowired
	SchoolDataRepository schoolrepository;
	@Autowired
	HttpSession session;
	@Autowired
	PostDataRepository postrepository;
	@Autowired
	CoachDataRepository coachrepository;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute("formModel") CoachData coachdata, SchoolData schooldata,
			ModelAndView mav) {
		mav.setViewName("index");
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		return mav;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@ModelAttribute("formModel") CoachData coachdata, SchoolData schooldata,
			ModelAndView mav) {
		mav.setViewName("login");
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		return mav;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView login(@ModelAttribute("formModel") SchoolData schooldata, CoachData coachdata,
			ModelAndView mov) {
		ModelAndView res = null;
		List<SchoolData> schoolList = schoolrepository.findAll();
		List<CoachData> coachList = coachrepository.findAll();
		for (int i = 0; i < schoolList.size(); i++) {
			if (schooldata.getMail().equals(schoolList.get(i).getMail())) { // 学校アカウントメールアドレス一致確認
				if (schooldata.getPassword().equals(schoolList.get(i).getPassword())) { // パス確認
					mov.addObject("msg", "ログインしました");
					mov.setViewName("result");
					session.setAttribute("sessionAccountName", schoolList.get(i).getSchoolName()); // セッションにスクールネーム
					session.setAttribute("sessionSdata", schoolList.get(i)); // 以下のsessionはpostのために全保存
					session.setAttribute("sessionSid", schoolList.get(i).getId()); // 以下のsessionはpostのために全保存
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
					if (coachdata.getPassword().equals(coachList.get(i).getPassword())) { // パス確認
						session.setAttribute("sessionAccountName", coachList.get(i).getLastName()); // セッションにスクールネーム保存
						session.setAttribute("sessionCid", coachList.get(i).getId());
						session.setAttribute("sessionCdata", coachList.get(i));
						mov.addObject("AccountName", session.getAttribute("sessionAccountName"));
						mov.addObject("cdata", session.getAttribute("sessionCdata"));
						mov.addObject("msg", "ログインしました");
						mov.setViewName("result");
						res = mov;
						break;
					} else {
						mov.addObject("msg", "パスワードが違います。");
						mov.setViewName("login");
						res = mov;
						break;
					}
				} else {
					mov.addObject("msg", "メールアドレスが存在しません");
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
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		return mav;
	}

	@RequestMapping("/result")
	public ModelAndView result(ModelAndView mav) {
		mav.setViewName("result");
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		return mav;
	}

	@RequestMapping("/logout")
	public ModelAndView logout(ModelAndView mav) {
		mav.setViewName("logout");
		session.invalidate(); // クリア
		mav.addObject("msg", "ログアウトされました");
		return mav;
	}

	@RequestMapping("/board")
	public ModelAndView board(ModelAndView mav) {
		mav.setViewName("board");
		Iterable<PostData> plist = postrepository.findAll();
		mav.addObject("pdatalist", plist);
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		return mav;
	}

	
	@RequestMapping(value = "/coach", method = RequestMethod.GET)
	public ModelAndView coach(@ModelAttribute("formModel") CoachData coachdata, ModelAndView mav) {
		mav.setViewName("coach");
		mav.addObject("formModel", coachdata);
		Iterable<CoachData> clist = coachrepository.findAll();
		mav.addObject("cdatalist", clist);
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		return mav;
	}

	@RequestMapping(value = "/coach", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView coach(@ModelAttribute("formModel") @Validated CoachData coachdata, BindingResult result,
			ModelAndView mav) {
		ModelAndView res = null;
		if (!result.hasErrors()) {
			coachrepository.saveAndFlush(coachdata);
			res = new ModelAndView("redirect:/coach");
		} else {
			mav.setViewName("coach");
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
		Iterable<SchoolData> slist = schoolrepository.findAll();
		mav.addObject("sdatalist", slist);
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		return mav;
	}

	@RequestMapping(value = "/school", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView school(@ModelAttribute("formModel") @Validated SchoolData schooldata, BindingResult result,
			ModelAndView mav) {
		ModelAndView res = null;
		if (!result.hasErrors()) {
			schoolrepository.saveAndFlush(schooldata);
			session.setAttribute("sessionSdata", schooldata);
			session.setAttribute("AccountName", schooldata.getSchoolName());
			session.setAttribute("sessionSid", schooldata.getId());
			session.setAttribute("sessionSschoolName", schooldata.getSchoolName());
			session.setAttribute("sessionSlastname", schooldata.getLastName());
			session.setAttribute("sessionSfirstName", schooldata.getFirstName());
			session.setAttribute("sessionSmail", schooldata.getMail());
			session.setAttribute("sessionSpassword", schooldata.getPassword());
			session.setAttribute("sessionSarea", schooldata.getArea());
			session.setAttribute("sessionSaddress", schooldata.getAddress());
			session.setAttribute("sessionStel", schooldata.getTel());
			res = new ModelAndView("redirect:/school");

		} else {
			mav.setViewName("school");
			Iterable<SchoolData> slist = schoolrepository.findAll();
			mav.addObject("sdatalist", slist);
			mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
			mav.addObject("sdata", session.getAttribute("sessionSdata"));
			mav.addObject("cdata", session.getAttribute("sessionCdata"));
			res = mav;
		}

		res.addObject("AccountName", session.getAttribute("sessionAccountName"));
		res.addObject("sdata", session.getAttribute("sessionSdata"));
		res.addObject("cdata", session.getAttribute("sessionCdata"));
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
			ModelAndView mav) {
		ModelAndView res = null;
		res.addObject("AccountName", session.getAttribute("sessionAccountName"));
		res.addObject("sdata", session.getAttribute("sessionSdata"));
		res.addObject("cdata", session.getAttribute("sessionCdata"));
		if (!result.hasErrors()) {
			postrepository.saveAndFlush(postdata);

			postdata.setSchoolId((int) session.getAttribute("sessionSid"));
			postdata.setSchoolName(session.getAttribute("sessionSschoolName").toString());
			postdata.setLastName(session.getAttribute("sessionSlastname").toString());
			postdata.setFirstName(session.getAttribute("sessionSfirstName").toString());
			postdata.setMail(session.getAttribute("sessionSmail").toString());
			postdata.setPassword(session.getAttribute("sessionSpassword").toString());
			postdata.setArea(session.getAttribute("sessionSarea").toString());
			postdata.setAddress(session.getAttribute("sessionSaddress").toString());
			postdata.setTel(session.getAttribute("sessionStel").toString());
			res = new ModelAndView("redirect:/post");
			res.addObject("AccountName", session.getAttribute("sessionAccountName"));

		} else {
			mav.setViewName("post");
			Iterable<PostData> plist = postrepository.findAll();
			mav.addObject("pdatalist", plist);
			mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
			res = mav;
		}
		return res;
	}

	@RequestMapping("/article/{Id}")
	public ModelAndView article(@PathVariable int Id, ModelAndView mav) {
		mav.setViewName("article");
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		List<PostData> plist = postrepository.findAll();
		mav.addObject("pdatalist", plist.get(Id));
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
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
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		}
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
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
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		} else {
			mav.addObject("error", "ログイン情報とURLが一致しません");
		}
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		mav.addObject("path","/edit/coach/"+Id);
		return mav;
	}

	@RequestMapping(value = "/edit/coach/{Id}", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView coachedit(@ModelAttribute("formModel")
		@Validated CoachData coachdata,
		BindingResult result, ModelAndView mav) {
		List<CoachData> clist = coachrepository.findAll();
		if (!result.hasErrors()) {
			coachrepository.saveAndFlush(coachdata);
			mav.setViewName("result");
			mav.addObject("msg", "修正が完了しました");
		} else {
			mav.setViewName("coachedit");
			mav.addObject("formModel", coachdata);
			mav.addObject("path","/edit/coach/"+coachdata.getId());
		}
		
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
		return mav;
	}

	@RequestMapping("/mypage/school/{Id}")
	public ModelAndView schoolMypage(@PathVariable int Id, ModelAndView mav) {
		mav.setViewName("schoolMypage");
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		List<SchoolData> slist = schoolrepository.findAll();
		if (session.getAttribute("sessionSid") != null) {
			if ((int) session.getAttribute("sessionSid") == Id) {
				mav.addObject("sdatalist", slist.get(Id - 1));
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		}
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("cdata", session.getAttribute("sessionCdata"));
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
			} else {
				mav.addObject("error", "ログイン情報とURLが一致しません");
			}
		} else {
			mav.addObject("error", "ログイン情報とURLが一致しません");
		}
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		mav.addObject("path","/edit/school/"+Id);
		return mav;
	}

	@RequestMapping(value = "/edit/school/{Id}", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView schooledit(@ModelAttribute("formModel")
		@Validated SchoolData schooldata,
		BindingResult result, ModelAndView mav) {
		List<SchoolData> slist = schoolrepository.findAll();
		if (!result.hasErrors()) {
			schoolrepository.saveAndFlush(schooldata);
			mav.setViewName("result");
			mav.addObject("msg", "修正が完了しました");
		} else {
			mav.setViewName("schooledit");
			mav.addObject("formModel", schooldata);
			mav.addObject("path","/edit/school/"+schooldata.getId());
		}
		
		mav.addObject("AccountName", session.getAttribute("sessionAccountName"));
		mav.addObject("sdata", session.getAttribute("sessionSdata"));
		return mav;
	}

	@PostConstruct
	public void init() {
		SchoolData s1 = new SchoolData();
		s1.setArea("奈良県");
		s1.setSchoolName("奈良県立鹿高校");
		s1.setCategory("高校");
		s1.setLastName("なら");
		s1.setFirstName("しかまる");
		s1.setMail("n@n");
		s1.setPassword("0000");
		s1.setAddress("奈良町");
		s1.setTel("000-0000");
		schoolrepository.saveAndFlush(s1);

		SchoolData s2 = new SchoolData();
		s2.setArea("大阪府");
		s2.setSchoolName("大阪府立くいだおれ中学校");
		s2.setCategory("中学校");
		s2.setLastName("おおさか");
		s2.setFirstName("たこ");
		s2.setMail("o@o");
		s2.setPassword("1111");
		s2.setAddress("大阪町");
		s2.setTel("000-0000");
		schoolrepository.saveAndFlush(s2);

		CoachData c1 = new CoachData();
		c1.setLastName("山田");
		c1.setFirstName("太郎");
		c1.setJob("公務員");
		c1.setMail("yama@da");
		c1.setPassword("0000");
		c1.setArea("奈良県");
		c1.setAddress("鹿町");
		c1.setTel("00");
		c1.setExperience("バレーボール10年");
		c1.setMessage("土日空いてます");
		coachrepository.saveAndFlush(c1);

		CoachData c2 = new CoachData();
		c2.setLastName("鈴木");
		c2.setFirstName("花子");
		c2.setJob("会社員");
		c2.setMail("suzu@ki");
		c2.setPassword("1111");
		c2.setArea("大阪府");
		c2.setAddress("たこ町");
		c2.setTel("11");
		c2.setExperience("吹奏楽3年");
		c2.setMessage("金管楽器教えるのが得意です！");
		coachrepository.saveAndFlush(c2);

		PostData p1 = new PostData();
		p1.setArea("奈良");
		p1.setSchoolName("奈良県立鹿高校");
		p1.setCategory("高校");
		p1.setLastName("なら");
		p1.setFirstName("しかまる");
		p1.setMail("n@n");
		p1.setPassword("0000");
		p1.setAddress("奈良町");
		p1.setTel("000-0000");
		p1.setCategory("運動部");
		p1.setType("バレーボール部");
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
		postrepository.saveAndFlush(p1);

		PostData p2 = new PostData();
		p2.setArea("大阪");
		p2.setSchoolName("大阪府立くいだおれ中学校");
		p2.setCategory("中学校");
		p2.setLastName("おおさか");
		p2.setFirstName("たこ");
		p2.setMail("o@o");
		p2.setPassword("1111");
		p2.setAddress("大阪町");
		p2.setTel("000-0000");
		p2.setCategory("文化部");
		p2.setType("吹奏楽部");
		p2.setCategoryDetails("マーチングバンド");
		p2.setTitle("経験者の方募集してます");
		p2.setContent("経験者の指導者がいなくて困っています。");
		p2.setReword(false);
		p2.setRewordDetails("");
		p2.setWant("経験者の方であれば楽器などの指定はございません。");
		p2.setWantDate("平日 16:00-19:00で練習をしています。長期で来ていただける方だと嬉しいです。");
		p2.setSituation("部員数30人です。");
		p2.setOtherText("よろしくお願いします！");
		p2.setSchoolId(1);
		postrepository.saveAndFlush(p2);
	}

}