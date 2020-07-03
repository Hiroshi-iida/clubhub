package clubHub.repositories;

import clubHub.CoachData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CoachDataRepository extends JpaRepository<CoachData, Long>{

	public List<CoachData> findByMail(String mail);
	public List<CoachData> findByUuid(String uuid);
	
}
