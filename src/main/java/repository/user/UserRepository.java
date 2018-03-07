package repository.user;

import model.User;

import java.util.List;

public interface UserRepository {
    User getByEmail(String email);

    User save(User currentUser);

    List<User> findAll();

    User findOne(String userId);

    User update(User currentUser);
}
