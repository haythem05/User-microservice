package tn.esprit.pokerplaning.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pokerplaning.Entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User , Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findUserByEmail(String email);
    User getUserByResetToken(String resetToken);
}
