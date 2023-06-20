package com.example.uzum.controller;

import com.example.uzum.dto.Result;
import com.example.uzum.service.AttachmentService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/attachment")
public class AttachmentController {

    @Autowired
    AttachmentService attachmentService;

    @ApiOperation(value = "This method is used to upload images.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR','ROLE_ADMIN','ATTACHMENT_UPLOAD')")
    @PostMapping("/upload")
    public Result<?> uploadFile(MultipartHttpServletRequest request) throws IOException {
        return attachmentService.uploadFile(request);
    }

    @ApiOperation(value = "This method is used to get infos about attachments.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR','ROLE_ADMIN','ATTACHMENT_GET_INFO')")
    @GetMapping("/info")
    public Result<?> getAll(@RequestParam(defaultValue = "0") String page) {
        return attachmentService.getAll(page);
    }

    @ApiOperation(value = "This method is used to download images.")
    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        attachmentService.download(id, response);
    }

    @ApiOperation(value = "This method is used to edit attachments.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR','ROLE_ADMIN','ATTACHMENT_EDIT')")
    @PutMapping("/edit")
    public Result<?> editByIdList(@RequestParam List<String> attachmentIDs, MultipartHttpServletRequest request) throws IOException {
        return attachmentService.editByIdList(attachmentIDs, request);
    }

}
