package com.tuanyi.voting.repository;

import com.tuanyi.voting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {
    List<Vote> findByUserIdAndNomineeId(String userId, Integer nomineeId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.userId = ?1 AND v.nomineeId = ?2 AND DATE(v.voteTime) = DATE(?3)")
    int countVotesByUserForNomineeOnDate(String userId, Integer nomineeId, Date date);
}
