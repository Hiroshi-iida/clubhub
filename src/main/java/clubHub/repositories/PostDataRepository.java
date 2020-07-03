package clubHub.repositories;


import clubHub.PostData;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostDataRepository extends JpaRepository<PostData, Long>{
	public Optional<PostData> findById(int name);
	public List <PostData> findByType(String type);
	public List <PostData> findByArea(String area);
	public List <PostData> findByAreaAndType(String area, String type);
	
}