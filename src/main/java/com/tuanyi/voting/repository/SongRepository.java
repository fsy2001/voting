package com.tuanyi.voting.repository;

import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Integer> {
    Song getSongById(Integer id);
    List<Song> getByStateOrderByVotesDesc(NominationState state);
    List<Song> getByStateOrderByIdDesc(NominationState state);
    List<Song> getByUserId(String userId);
    List<Song> findAllByOrderByIdDesc();
}
