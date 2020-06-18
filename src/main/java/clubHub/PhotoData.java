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
public class PhotoData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	@NotNull
	private long id;
	
	@Column(nullable = false)
	@NotEmpty(message="空白NG")
	private String lastName;
}