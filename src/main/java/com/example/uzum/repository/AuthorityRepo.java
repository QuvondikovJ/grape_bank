package com.example.uzum.repository;

import com.example.uzum.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface AuthorityRepo extends JpaRepository<Authority, Integer> {


    Set<Authority> findAllByAuthorityIn(Collection<String> authority);



}
