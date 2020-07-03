package clubHub;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table
public class SchoolData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	@NotNull
	private int id;
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String schoolName;
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String category;
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String lastName;
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String firstName;
	
	@Column(nullable = false)
	@Email(message="正しく入力してください")
	@NotEmpty(message="空白NG")
	private String mail;
	
	@Column(nullable = false)
	@Pass
	@NotEmpty(message="空白NG")
	private String password;
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String area;
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String address;
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String tel;
	
	@Column(length=100000)
	private String image;
	
	@Column
	private String uuid;
	
	@Column
	private boolean authentication;
	
	public boolean isAuthentication() {
		return authentication;
	}
	public void setAuthentication(boolean authentication) {
		this.authentication = authentication;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName= schoolName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category= category;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area= area;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName= lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName= firstName;
	}
	
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail= mail;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address= address;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel= tel;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password= password;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	

	
}
