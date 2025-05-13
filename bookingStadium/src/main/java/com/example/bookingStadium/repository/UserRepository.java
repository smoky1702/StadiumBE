package com.example.bookingStadium.repository;

import com.example.bookingStadium.entity.Roles;
import com.example.bookingStadium.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    boolean existsByEmail(String email);
    Optional<Users> findByRole(Roles role);
    Optional<Users> findByEmail(String email);
}
