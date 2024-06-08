package com.tuanyi.voting.repository;

import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.model.Nominee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NomineeRepository extends JpaRepository<Nominee, Integer> {
    Nominee getNomineeById(Integer id);
    List<Nominee> getNomineeByStateOrderByVotesDesc(NominationState state);
    List<Nominee> getNomineesByStateOrderByIdDesc(NominationState state);
    List<Nominee> getNomineeByUserId(String userId);
    List<Nominee> findAllByOrderByIdDesc();
}
