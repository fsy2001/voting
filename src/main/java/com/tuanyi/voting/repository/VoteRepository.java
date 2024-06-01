package com.tuanyi.voting.repository;

import com.tuanyi.voting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    List<Vote> findAllByUserIdAndNomineeId(String userId, Integer nomineeId);
}
