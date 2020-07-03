package clubHub.repositories;

import clubHub.SchoolData;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolDataRepository extends JpaRepository<SchoolData, Long>{
	public Optional<SchoolData> findById(int name);
	public List<SchoolData> findByMail(String mail);
	public List<SchoolData> findByUuid(String uuid);
}
