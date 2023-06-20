package com.example.uzum.service;

import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public interface AttachmentService {


    Result<?> uploadFile(MultipartHttpServletRequest request) throws IOException;

    Result<?> getAll(String page);

    void download(Long id, HttpServletResponse response) throws IOException;

    Result<?> editByIdList(List<String> attachmentIDs, MultipartHttpServletRequest request) throws IOException;
}
