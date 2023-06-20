package com.example.uzum.controller;

import com.example.uzum.dto.comment.CommentDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.dto.comment.CommentReplyDTO;
import com.example.uzum.service.CommentService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation(value = "This method is used to add new comment.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_SELLER', 'ROLE_BUYER', 'COMMENT_ADD')")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody CommentDTO commentDto) {
        return commentService.add(commentDto);
    }

    @ApiOperation(value = "This method is used to get comments by its product ID.")
    @GetMapping("/getByProductId/{productId}")
    public Result<?> getByProductId(@PathVariable Integer productId,
                                    @RequestParam(defaultValue = "createdDateDesc") String order,
                                    @RequestParam(defaultValue = "0") String page) {
        return commentService.getByProductId(productId, order, page);
    }

    @ApiOperation(value = "This method is used to get comments by buyer ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_BUYER', 'COMMENT_GET_BY_BUYER_ID')")
    @GetMapping("/getByBuyerId/{buyerId}")
    public Result<?> getByBuyerId(@PathVariable Integer buyerId,
                                  @RequestParam(defaultValue = "0") String page) {
        return commentService.getByBuyerId(buyerId, page);
    }

    @ApiOperation(value = "This method is used to get unanswered comments by product ID. It is used used to fastly reply to comments.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_SELLER', 'COMMENT_GET_UNANSWERED_COMMENTS_BY_PRODUCT_ID')")
    @GetMapping("/getUnansweredComments/{productId}")
    public Result<?> getUnansweredComments(@PathVariable Integer productId,
                                           @RequestParam(defaultValue = "createdDateDesc") String order,
                                           @RequestParam(defaultValue = "0") String page) {
        return commentService.unansweredComments(productId, order, page);
    }

    @ApiOperation(value = "This method is used to reply comments.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_SELLER', 'COMMENT_REPLY')")
    @PutMapping("/replyComment/{id}")
    public Result<?> replyComment(@PathVariable Long id,
                                  @Valid @RequestBody CommentDTO dto) {
        return commentService.replyComment(id, dto);
    }

    @ApiOperation(value = "This method is used to edit comment details.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_SELLER', 'ROLE_BUYER', 'COMMENT_EDIT')")
    @PutMapping("/edit/{id}")
    public Result<?> edit(@PathVariable Long id, @Valid @RequestBody CommentDTO dto) {
        return commentService.edit(id, dto);
    }

    @ApiOperation(value = "This method is used to delete comment details.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN','ROLE_SELLER', 'ROLE_BUYER', 'COMMENT_DELETE')")
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return commentService.delete(id);
    }

}
