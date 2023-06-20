package com.example.uzum.repository;

import com.example.uzum.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FAQRepo extends JpaRepository<FAQ, Integer> {

    Optional<FAQ> findByQueEnOrQueRu(String queEn, String queRu);

Optional<FAQ> findByAnsEnOrAnsRu(String ansEn, String ansRu);

@Query(value = "SELECT faq FROM FAQ AS faq WHERE faq.parentFAQ=NULL ")
    List<FAQ> getGrandFAQs();

List<FAQ> findByParentFAQId(Integer parentFAQId);


@Query(value = "SELECT COUNT(faq)>0 FROM FAQ AS faq WHERE (faq.queEn=:queEn OR faq.queRu=:queRu OR faq.ansEn=:ansEn OR faq.ansRu=:ansRu) AND faq.id<>:id")
boolean existsByQueOrAns(String queEn, String queRu, String ansEn, String ansRu, Integer id);

}
