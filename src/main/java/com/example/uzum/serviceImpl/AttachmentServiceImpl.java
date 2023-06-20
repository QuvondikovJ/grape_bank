package com.example.uzum.serviceImpl;

import com.example.uzum.dto.Result;
import com.example.uzum.entity.Attachment;
import com.example.uzum.entity.AttachmentCore;
import com.example.uzum.helper.Filter;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.AttachmentCoreRepo;
import com.example.uzum.repository.AttachmentRepo;
import com.example.uzum.service.AttachmentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttachmentServiceImpl implements AttachmentService {


    @Autowired
    private AttachmentRepo attachmentRepo;
    @Autowired
    private AttachmentCoreRepo attachmentCoreRepo;

    private static final Logger logger = LogManager.getLogger(AttachmentServiceImpl.class);

    @Override
    public Result<?> uploadFile(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        /* In maximum 10 file may be uploaded each time */
        List<MultipartFile> files = new ArrayList<>();
        while (fileNames.hasNext()) {
            files.add(request.getFile(fileNames.next()));
            if (files.size() > 10) return new Result<>(false, Messages.EXCEED_DATA_FROM_LIMIT);
        }
        List<Long> savedAttachIDs = new ArrayList<>();
        List<String> availableExtensionType = Arrays.asList("jpe", "jpeg", "jpg", "jfif", "svg", "png");
        for (MultipartFile file : files) {
            if (file != null) {
                String[] fileNameAndExtension = file.getOriginalFilename().split("\\.");
                String fileExtension = fileNameAndExtension[fileNameAndExtension.length - 1];
                if (availableExtensionType.contains(fileExtension)) {
                    String fileOriginalName = file.getOriginalFilename();
                    Long fileSize = file.getSize();
                    String fileContentType = file.getContentType();
                    Attachment attachment = new Attachment(null, fileOriginalName, fileSize, fileContentType, Timestamp.valueOf(LocalDateTime.now()));
                    Attachment savedAttachment = attachmentRepo.save(attachment);
                    logger.info("New attachment uploaded. ID : {}", savedAttachment.getId());
                    AttachmentCore attachmentCore = new AttachmentCore(null, file.getBytes(), savedAttachment);
                    attachmentCore = attachmentCoreRepo.save(attachmentCore);
                    logger.info("Attachment core uploaded. ID : {}", attachmentCore.getId());
                    savedAttachIDs.add(savedAttachment.getId());
                }
            }
        }
        if (savedAttachIDs.size() > 0) return new Result<>(true, savedAttachIDs);
        return new Result<>(false, Messages.UNAVAILABLE_DATA_TYPES);
    }

    @Override
    public Result<?> getAll(String page) {
        int pageInt = Integer.parseInt(page);
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Attachment> attachments = attachmentRepo.findAll(pageable);
        if (pageInt == 0 && attachments.getSize() == 0) return new Result<>(false, Messages.ANY_FILES_NOT_SAVED);
        if (attachments.getSize() == 0) return new Result<>(true, Messages.ATTACHMENTS_DO_NOT_EXIST_IN_THIS_PAGE);
        return new Result<>(true, attachments);
    }

    @Override
    public void download(Long id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optional = attachmentRepo.findById(id);
        if (optional.isPresent()) {
            Attachment attachment = optional.get();
            AttachmentCore attachmentCore = attachmentCoreRepo.findByAttachmentId(attachment.getId());
            logger.info("Attachment downloaded. ID : {}", id);
            response.setHeader("Content-Disposition", "attachment;filename=\"" + attachment.getName() + "\"");
            response.setContentType(attachment.getType());
            FileCopyUtils.copy(attachmentCore.getBytes(), response.getOutputStream());
        }
    }

    @Override
    public Result<?> editByIdList(List<String> attachmentIDs, MultipartHttpServletRequest request) throws IOException {
        List<Long> idList = attachmentIDs.stream().map(attachId -> Long.parseLong(attachId)).collect(Collectors.toList());
        if (idList.isEmpty()) return new Result<>(false, Messages.UNAVAILABLE_INPUT_DATA);
        List<Attachment> attachments = attachmentRepo.findAllById(idList);
        if (attachments.size() == 0) return new Result<>(false, Messages.ANY_FILES_NOT_ATTACHED);
        Iterator<String> getFileNames = request.getFileNames();
        List<MultipartFile> files = new ArrayList<>();
        List<String> availableExtensionType = Arrays.asList("jpe", "jpeg", "jpg", "jfif", "svg", "png");
        while (getFileNames.hasNext()) {
            files.add(request.getFile(getFileNames.next()));
        }
        List<Long> updatedAttachIDs = new ArrayList<>();
        Attachment attachment;
        AttachmentCore attachmentCore;
        int counter = 0;
        for (MultipartFile file : files) {
            if (file != null) {
                String[] fileNameAndExtension = file.getOriginalFilename().split("\\.");
                String fileExtension = fileNameAndExtension[fileNameAndExtension.length - 1];
                if (availableExtensionType.contains(fileExtension)) {
                    if (counter < attachments.size()) {
                        attachment = attachments.get(counter);
                        attachment.setName(file.getOriginalFilename());
                        attachment.setSize(file.getSize());
                        attachment.setType(file.getContentType());
                        attachmentRepo.save(attachment);
                        logger.info("Attachment info updated. ID : {}", attachment.getId());
                        attachmentCore = attachmentCoreRepo.findByAttachmentId(attachment.getId());
                        attachmentCore.setBytes(file.getBytes());
                        attachmentCoreRepo.save(attachmentCore);
                        logger.info("Attachment core updated. ID : {}", attachmentCore.getId());
                        updatedAttachIDs.add(attachment.getId());
                        counter++;
                    } else {
                        /* when user attach files more than before to product */
                        attachment = new Attachment();
                        attachment.setName(file.getOriginalFilename());
                        attachment.setSize(file.getSize());
                        attachment.setType(file.getContentType());
                        Attachment savedAttachment = attachmentRepo.save(attachment);
                        logger.info("New attachment uploaded. ID : {}", savedAttachment.getId());
                        attachmentCore = new AttachmentCore(null, file.getBytes(), savedAttachment);
                        attachmentCore = attachmentCoreRepo.save(attachmentCore);
                        logger.info("Attachment core uploaded. ID : {}", attachmentCore.getId());
                        updatedAttachIDs.add(savedAttachment.getId());
                    }
                }
            }
        }
        if (updatedAttachIDs.size() > 0 && updatedAttachIDs.size() < attachments.size()) {
            /* if user attach files less than before then redundant attachment is deleted */
            for (Attachment checkAttachment : attachments) {
                if (!updatedAttachIDs.contains(checkAttachment.getId())) {
                    attachmentCoreRepo.deleteByAttachment(checkAttachment);
                    attachmentCore = attachmentCoreRepo.findByAttachmentId(checkAttachment.getId());
                    logger.info("Attachment Core deleted. Id : {}", attachmentCore.getId());
                    attachmentRepo.delete(checkAttachment);
                    logger.info("Attachment info deleted. ID : {}", checkAttachment.getId());
                }
            }
        }

        if (updatedAttachIDs.size() > 0) return new Result<>(true, updatedAttachIDs);
        return new Result<>(false, Messages.UNAVAILABLE_DATA_TYPES);
    }
}
