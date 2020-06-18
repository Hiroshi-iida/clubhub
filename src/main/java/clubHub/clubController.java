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

@Controller
@SpringBootApplication
public class clubController {
	@Autowired
	SchoolDataRepository schoolrepository;
//	@Autowired
//	PostDataRepository postrepository;
//	@Autowired
//	CoachDataRepository coachrepository;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@ModelAttribute("formModel")
//	PostData postdata,
	SchoolData schooldata, 
	ModelAndView mav) {
		mav.setViewName("index");
		mav.addObject("msg", "this is sample content.");
		List<SchoolData> slist = schoolrepository.findAll();
//		List<PostData> plist = postrepository.findAll();
//		for (PostData p : plist) {
//			String schoolName = slist.get(p.getSchoolId()).getSchoolName();
//			p.setSchoolName(schoolName);
//		}
		mav.addObject("sdatalist", slist);
//		mav.addObject("pdatalist", plist);
		return mav;
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView form(@ModelAttribute("formModel") 
//			@Validated PostData postdata,
			@Validated SchoolData schooldata,
			BindingResult result,
			ModelAndView mov) {
		ModelAndView res = null;
//		if (!result.hasErrors()) {
//			postrepository.saveAndFlush(postdata);
//			Iterable<PostData> list = postrepository.findAll();
//			mov.addObject("pdatalist", list);
//			res = new ModelAndView("redirect:/");
//		} else {
//			mov.setViewName("index");
//			mov.addObject("msg", "sorry");
//			Iterable<PostData> plist = postrepository.findAll();
//			mov.addObject("pdatalist", plist);
//			res = mov;
//		}
		if (!result.hasErrors()) {
			schoolrepository.saveAndFlush(schooldata);
			Iterable<SchoolData> list = schoolrepository.findAll();
			mov.addObject("datalist", list);
			res = new ModelAndView("redirect:/");
		} else {
			mov.setViewName("index");
			mov.addObject("msg", "sorry");
			Iterable<SchoolData> slist = schoolrepository.findAll();
			mov.addObject("sdatalist", slist);
			res = mov;
		}
		return res;
	}
	
//	@RequestMapping(value = "index/{id}",method = RequestMethod.GET)
//	public ModelAndView index(@ModelAttribute SchoolData schooldata,
//			@ModelAttribute PostData postdata,
//			@PathVariable int id, ModelAndView mav) {
//		mav.setViewName("index");
//		Optional<SchoolData> sdata = schoolrepository.findById(id);
//		Optional<PostData> pdata = postrepository.findById(3);
////		Iterable<SchoolData> slist = schoolrepository.findAll();
//		mav.addObject("sdatalist", sdata.get());
//		mav.addObject("pdatalist", pdata.get());
//		
//		return mav;
//	}

	@PostConstruct
	public void init() {
		SchoolData s1 = new SchoolData();
		s1.setArea("奈良");
		s1.setSchoolName("奈良県立鹿高校");
		s1.setCategory("高校");
		s1.setLastName("なら");
		s1.setFirstName("しかまる");
		s1.setMail("l@l");
		s1.setPassword("0000");
		s1.setAddress("奈良町");
		s1.setTel("000-0000");
		schoolrepository.saveAndFlush(s1);	
		
		SchoolData s2 = new SchoolData();
		s2.setArea("大阪");
		s2.setSchoolName("大阪府立くいだおれ中学校");
		s2.setCategory("中学校");
		s2.setLastName("おおさか");
		s2.setFirstName("たこ");
		s2.setMail("l@l");
		s2.setPassword("0000");
		s2.setAddress("大阪町");
		s2.setTel("000-0000");
		schoolrepository.saveAndFlush(s2);	
//
//		PostData p1 = new PostData();
//		p1.setTitle("全国大会出場目指しています！経験者のコーチを募集しています。");
//		p1.setType("バレーボール");
//		p1.setCategory("運動部");
//		p1.setCategoryDetails("女子バレー");
//		p1.setContent(
//				"前回の大会では県内ベスト４でした。優秀な生徒が集まっていますが、昨年監督していた先生が定年退職されて、生徒が求めるような高い指導が出来ていません。今年こそは全国大会出場を目指しています。一緒に指導して頂ける方を募集しています。よろしくお願いします。");
////		p1.setSchoolId(1);
//		p1.setReword(false);
//		p1.setRewordDetails("お給料はお支払いできませんが、交通費・スポーツドリンクは支給させていただきます。");
//		p1.setWant("abc");
//		p1.setWantDate("abc");
//		p1.setSituation("abc");
//		p1.setOtherText("expe");
//		
//		postrepository.saveAndFlush(p1);
//		
//		PostData p2 = new PostData();
//		p2.setTitle("試合で1勝したい");
//		p2.setType("吹奏楽");
//		p2.setCategory("文化部");
//		p2.setCategoryDetails("ラッパ");
//		p2.setContent(
//				"初心者ばかりです。よろしくお願いします。");
////		p2.setSchoolId(1);
//		p2.setReword(false);
//		p2.setRewordDetails("なしです");
//		p2.setWant("abc");
//		p2.setWantDate("abc");
//		p2.setSituation("abc");
//		p2.setOtherText("expe");
//		postrepository.saveAndFlush(p2);


	}

}