package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.User;
import com.wangping.ClaimCenter.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAllByRole(Role role);
}
