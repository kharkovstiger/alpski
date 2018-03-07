package repository.user;

import model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CrudUserRepository extends MongoRepository<User, String> {
    @Transactional
    void delete(@Param("id") String id);

    @Override
    @Transactional
    User save(User user);

    @Override
    List<User> findAll();

    User getByEmail(String email);

    @Override
    User findOne(String id);
}
