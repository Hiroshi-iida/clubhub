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
import java.util.Date;

@Entity
@Table
public class ChatData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	@NotNull
	private int id;

	@Column(nullable = false)
	@NotEmpty(message = "空白NG")
	private String message; //

	@Column
	private boolean sender; //trueは送り主
	
	@Column
	private Date date; //

	@Column
	private int coachId; //

	@Column
	private int schoolId; //


	public int getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

	public String getMessage() {
		return message;
	}


	public boolean isSender() {
		return sender;
	}

	public void setSender(boolean sender) {
		this.sender = sender;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getCoachId() {
		return coachId;
	}

	public void setCoachId(int coachId) {
		this.coachId = coachId;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}