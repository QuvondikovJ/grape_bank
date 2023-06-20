package com.example.uzum.service;

import com.example.uzum.dto.comment.CommentDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.dto.comment.CommentReplyDTO;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    Result<?> add(CommentDTO commentDto);

    Result<?> getByProductId(Integer productId, String order, String page);

    Result<?> getByBuyerId(Integer buyerId, String page);

    Result<?> unansweredComments(Integer productId, String order, String page);

    Result<?> replyComment(Long id, CommentDTO dto);

    Result<?> edit(Long id, CommentDTO dto);

    Result<?> delete(Long id);
}
