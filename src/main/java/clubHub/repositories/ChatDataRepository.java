package clubHub.repositories;

import clubHub.ChatData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;

@Repository
public interface ChatDataRepository extends JpaRepository<ChatData, Long>{

}
