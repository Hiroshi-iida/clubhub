package clubHub;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
//import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table
public class PostData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	@NotNull
	private int id;
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String category;	//カテゴリー（運動or文化）
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String type;		//野球部とか
	
	@Column(nullable = true)
	private String categoryDetails;		//詳細その他　硬式テニスとか
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String title;		//タイトル
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String content;		//内容
	
	@Column(nullable = true)
	private boolean reword;		//報酬の有無
	
	@Column(nullable = true)
	private String rewordDetails;		//報酬詳細
	
	@Column(nullable = true)
	private String want;		//求める内容
	
	@Column(nullable = true)
	private String wantDate;		//求める日時
	
	@Column(nullable = true)
	private String situation;		//部活の状況
	
	@Column(nullable = true)
	private String otherText;		//その他
	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getCategory() {
		return category; 
	}
	public void	setCategory(String category) {
		this.category = category; 
	}
	 
	public String getType() {
		return type; 
	}
	public void setType(String type) {
	this.type = type; 
	}
	 
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCategoryDetails() {
		return categoryDetails;
	}
	public void setCategoryDetails(String categoryDetails) {
		this.categoryDetails = categoryDetails;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public boolean getReword() {
		return reword;
	}
	public void setReword(boolean reword) {
		this.reword = reword;
	}
	
	public String getRewordDetails() {
		return rewordDetails;
	}
	public void setRewordDetails(String rewordDetails) {
		this.rewordDetails = rewordDetails;
	}
	
	public String getWant() {
		return want;
	}
	public void setWant(String want) {
		this.want = want;
	}
	
	public String getWantDate() {
		return wantDate;
	}
	public void setWantDate(String wantDate) {
		this.wantDate = wantDate;
	}
	
	public String getSituation() {
		return situation;
	}
	public void setSituation(String situation) {
		this.situation = situation;
	}
	
	public String getOtherText() {
		return otherText;
	}
	public void setOtherText(String otherText) {
		this.otherText = otherText;
	}
	
	
	//=============ここまではpost用　ここからschooldata用=================//
	
	@Column(nullable = true)
	private int schoolId;
	
	@Column(nullable = true)
	private String schoolName;
	
	@Column(nullable = true)
	private String schoolCategory;
	
	@Column(nullable = true)
	private String lastName;
	
	@Column(nullable = true)
	private String firstName;
	
	@Column(nullable = true)
	private String mail;
	
	@Column(nullable = true)
	private String password;
	
	@Column(nullable = true)
	private String area;
	
	@Column(nullable = true)
	private String address;
	
	@Column(nullable = true)
	private String tel;

	
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getSchoolCategory() {
		return schoolCategory;
	}
	public void setSchoolCategory(String schoolCategory) {
		this.schoolCategory = schoolCategory;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	
}