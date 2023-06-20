package com.example.uzum.repository;

import com.example.uzum.entity.Attachment;
import com.example.uzum.entity.AttachmentCore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

@Repository
public interface AttachmentCoreRepo extends JpaRepository<AttachmentCore, Long> {

    @Transactional
    @Modifying
    void deleteByAttachment(Attachment attachment);



    AttachmentCore findByAttachmentId(Long attachment_id);


}
