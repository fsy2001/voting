package com.tuanyi.voting.repository;

import com.tuanyi.voting.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserId(String userId);
}
