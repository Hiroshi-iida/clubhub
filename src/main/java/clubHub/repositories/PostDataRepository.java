package clubHub.repositories;

import clubHub.PostData;
import clubHub.SchoolData;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostDataRepository extends JpaRepository<PostData, Long>{
	public Optional<PostData> findById(int name);
}
