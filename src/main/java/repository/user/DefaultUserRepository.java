package repository.user;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DefaultUserRepository implements UserRepository {
    
    private final CrudUserRepository crudUserRepository;

    @Autowired
    public DefaultUserRepository(CrudUserRepository crudUserRepository) {
        this.crudUserRepository = crudUserRepository;
    }

    @Override
    public User getByEmail(String email) {
        return crudUserRepository.getByEmail(email);
    }

    @Override
    public User save(User currentUser) {
        return crudUserRepository.save(currentUser);
    }

    @Override
    public List<User> findAll() {
        return crudUserRepository.findAll();
    }

    @Override
    public User findOne(String userId) {
        return crudUserRepository.findOne(userId);
    }

    @Override
    public User update(User currentUser) {
        return crudUserRepository.save(currentUser);
    }
}
