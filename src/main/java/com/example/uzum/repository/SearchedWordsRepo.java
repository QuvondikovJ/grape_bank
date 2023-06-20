package com.example.uzum.repository;

import com.example.uzum.entity.SearchedWords;
import org.hibernate.sql.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SearchedWordsRepo extends JpaRepository<SearchedWords, Long> {


    @Query(value = "SELECT sword FROM SearchedWords AS sword GROUP BY sword.searchedWord ORDER BY COUNT(sword.id)")
    Page<SearchedWords> getAll(Pageable pageable);

    @Query(value = "SELECT sword.searched_word FROM searchedwords AS sword WHERE sword.session_id=:sessionId AND sword.is_active=TRUE ORDER BY sword.createdDate DESC LIMIT 5", nativeQuery = true)
    List<String> getBySessionId(String sessionId);

    @Query(value = "SELECT * FROM searchedwords AS sword WHERE sword.created_date > (CURRENT_TIMESTAMP - 30) GROUP BY sword.searched_word ORDER BY COUNT(sword.id) DESC LIMIT 100", nativeQuery = true)
    List<SearchedWords> getByPopular();

    @Transactional
    @Modifying
    @Query(value = "UPDATE SearchedWords as sword SET sword.isActive=FALSE WHERE sword.sessionId=:sessionId")
    void deleteBySessionId(String sessionId);

//    @Query(value = "SELECT sword FROM SearchedWords AS sword WHERE (sword.searchedWord LIKE :search% OR sword.searchedWord LIKE %:search% OR sword.searchedWord LIKE %:search) AND sword.sessionId=:sessionId AND sword.isActive=TRUE ORDER BY sword.createdDate DESC LIMIT 5")
//    List<SearchedWords> getSearchedWords(String search, String sessionId);

    boolean existsBySessionId(String sessionId);
}
