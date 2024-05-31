package com.tuanyi.voting.repository;

import com.tuanyi.voting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.userId = ?1 AND v.nomineeId = ?2 AND DATE(v.voteTime) = DATE(?3)")
    int countVotesByUserForNomineeOnDate(String userId, Integer nomineeId, Date date);
}
