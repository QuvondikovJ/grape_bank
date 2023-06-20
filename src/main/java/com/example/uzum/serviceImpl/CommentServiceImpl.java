package com.example.uzum.serviceImpl;

import com.example.uzum.dto.comment.CommentDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.dto.comment.CommentReplyDTO;
import com.example.uzum.entity.*;
import com.example.uzum.entity.enums.Role;
import com.example.uzum.helper.Filter;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.*;
import com.example.uzum.service.CommentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

import static com.example.uzum.helper.StringUtils.capitalizeText;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepo commentRepo;
    @Autowired
    private BuyerRepo buyerRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private AttachmentRepo attachmentRepo;
    @Autowired
    private SellerRepo sellerRepo;
    @Autowired
    private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger(CommentServiceImpl.class);

    @Override
    public Result<?> add(CommentDTO dto) {
        Optional<Product> optionalProduct = productRepo.findByIdAndIsActive(dto.getProductId(), true);
        if (optionalProduct.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        if (dto.getAmountOfStars() == null || dto.getAmountOfStars() < 1 || dto.getAmountOfStars() > 5)
            return new Result<>(false, Messages.AMOUNT_OF_STARS_CAN_NOT_BE_NULL_ETC);
        Product product = optionalProduct.get();
        Comment comment = new Comment();
        Buyer buyer = null;
        Employee employee = null;
        try {
            buyer = (Buyer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            comment.setBuyer(buyer);
            if (buyer.getFirstname() == null)
                comment.setWhoWasWrittenBy("***-" + buyer.getPhoneNumber().substring(9, 11) + "-".concat(buyer.getPhoneNumber().substring(11)));
            else comment.setWhoWasWrittenBy(buyer.getFirstname());
        } catch (Exception e) {
            employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (employee.getRole().equals(Role.SELLER)) {
                int employeeId = employee.getId();
                boolean isProductBelongToThisSeller = product.getSeller().getEmployees().stream().anyMatch(emp -> emp.getId().equals(employeeId));
                if (!isProductBelongToThisSeller)
                    return new Result<>(false, String.format(Messages.YOU_CANT_ADD_COMMENT_TO_THIS_PRODUCT, employee.getFirstname(), employee.getLastname()));
            }
            comment.setEmployee(employee);
            comment.setWhoWasWrittenBy(capitalizeText(employee.getRole().name()));
        }
        String text = dto.getText().trim();
        boolean existsByUserIdAndProductIdAndText;
        if (buyer != null) {
            existsByUserIdAndProductIdAndText = commentRepo.existsByBuyerIdAndProductIdAndTextAndIsActive(buyer.getId(), dto.getProductId(), text, true);
        } else {
            existsByUserIdAndProductIdAndText = commentRepo.existsByEmployeeIdAndProductIdAndTextAndIsActive(employee.getId(), dto.getProductId(), text, true);
        }
        if (existsByUserIdAndProductIdAndText) return new Result<>(false, Messages.YOU_JUST_ADDED_THIS_COMMENT);
        if (dto.getAttachmentIds() != null && !dto.getAttachmentIds().isEmpty()) {
            List<Attachment> attachments = attachmentRepo.findAllById(dto.getAttachmentIds());
            if (attachments.isEmpty()) return new Result<>(false, Messages.THIS_ATTACHMENTS_NOT_AVAILABLE);
            comment.setAttachments(attachments);
        }
        comment.setProduct(product);
        comment.setText(text);
        comment.setAmountOfStars(dto.getAmountOfStars());
        comment.setReplyComments(new ArrayList<>());
        comment = commentRepo.save(comment);
        int countOfAllCommentsByProductId = commentRepo.getCountOfAllCommentsByProductId(product.getId());
        String averageValueOfComments = String.valueOf((float) commentRepo.getAmountOfStarsByProductId(dto.getProductId()) / countOfAllCommentsByProductId);
        product.setAmountComments(countOfAllCommentsByProductId);
        product.setRating(averageValueOfComments.length() > 3 ? averageValueOfComments.substring(0, 3) : averageValueOfComments);
        product.setPercentOfRepliedComments(Math.round((float) commentRepo.getCountOfAnsweredCommentsByProductId(product.getId()) / countOfAllCommentsByProductId * 100));
        productRepo.save(product);
        Seller seller = product.getSeller();
        int countOfAllCommentsBySellerId = commentRepo.getCountOfAllCommentsBySellerId(seller.getId());
        averageValueOfComments = String.valueOf((float) commentRepo.getAmountOfStarsBySellerId(seller.getId()) / countOfAllCommentsBySellerId);
        seller.setAmountComments(countOfAllCommentsBySellerId);
        seller.setRating(averageValueOfComments.length() > 3 ? averageValueOfComments.substring(0, 3) : averageValueOfComments);
        seller.setPercentOfAnsweredComments(Math.round((float) commentRepo.getCountOfAnsweredCommentsBySellerId(seller.getId()) / countOfAllCommentsBySellerId * 100));
        sellerRepo.save(seller);
        logger.info("New comment added. ID : {}", comment.getId());
        return new Result<>(true, Messages.COMMENT_SAVED);
    }

    @Override
    public Result<?> getByProductId(Integer productId, String order, String page) {
        Optional<Product> optional = productRepo.findByIdAndIsActive(productId, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        int pageInt = Integer.parseInt(page);
        Pageable pageable = PageRequest.of(pageInt, 20);
        switch (order) {
            case Filter.CREATED_DATE_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("createdAt").descending());
            }
            case Filter.CREATED_DATE_ASC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("createdAt").ascending());
            }
            case Filter.RATING_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("amountOfStars").descending());
            }
            case Filter.RATING_ASC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("amountOfStars").ascending());
            }
        }
        Page<Comment> comments = commentRepo.findByProductIdAndIsActive(productId, true, pageable);
        if (comments.isEmpty() && pageInt == 0) return new Result<>(true, Messages.ANY_COMMENTS_ADDED_TO_THIS_PRODUCT);
        if (comments.isEmpty()) return new Result<>(false, String.format(Messages.IN_PAGE_NOT_ANY_COMMENTS, pageInt));
        return new Result<>(true, comments);
    }

    @Override
    public Result<?> getByBuyerId(Integer buyerId, String page) {
        Optional<Buyer> optional = buyerRepo.findByIdAndIsActive(buyerId, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
        try {
            Buyer buyerThatEnteredSystem = (Buyer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!buyerThatEnteredSystem.getId().equals(buyerId))
                return new Result<>(false, String.format(Messages.YOU_CANT_SEE_COMMENTS_OF_ANOTHER_BUYER, buyerThatEnteredSystem.getFirstname(), buyerThatEnteredSystem.getLastname()));
        } catch (Exception e) {
            /* DO NOTHING!  because to system entered Director or Admin. */
        }
        int pageInt = Integer.parseInt(page);
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.by("createdAt").descending());
        Page<Comment> comments = commentRepo.findByBuyerIdAndIsActive(buyerId, true, pageable);
        if (comments.isEmpty() && pageInt == 0)
            return new Result<>(true, Messages.THIS_BUYER_HAS_NOT_WRITTEN_ANY_COMMENTS_YET);
        if (comments.isEmpty()) return new Result<>(false, String.format(Messages.IN_PAGE_NOT_ANY_COMMENTS, pageInt));
        return new Result<>(true, comments);
    }

    @Override
    public Result<?> unansweredComments(Integer productId, String order, String page) {
        Optional<Product> optional = productRepo.findByIdAndIsActive(productId, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        Product product = optional.get();
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (employee.getRole().equals(Role.SELLER)) {
            if (product.getSeller().getEmployees().stream().noneMatch(emp -> emp.getId().equals(employee.getId())))
                return new Result<>(false, String.format(Messages.YOU_CANT_SEE_UNANSWERED_COMMENTS_OF_ANOTHER_SELLER, employee.getFirstname(), employee.getLastname()));
        }
        int pageInt = Integer.parseInt(page);
        Pageable pageable = PageRequest.of(pageInt, 20);
        switch (order) {
            case Filter.CREATED_DATE_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("createdAt").descending());
            }
            case Filter.CREATED_DATE_ASC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("createdAt").ascending());
            }
            case Filter.RATING_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("amountOfStars").descending());
            }
            case Filter.RATING_ASC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("amountOfStars").ascending());
            }
        }
        Page<Comment> comments = commentRepo.getUnansweredComments(productId, pageable);
        if (comments.isEmpty() && pageInt == 0)
            return new Result<>(false, Messages.THIS_PRODUCT_HAS_NOT_ANY_UNANSWERED_COMMENTS);
        if (comments.isEmpty()) return new Result<>(false, String.format(Messages.IN_PAGE_NOT_ANY_COMMENTS, pageInt));
        return new Result<>(true, comments);
    }

    @Override
    public Result<?> replyComment(Long id, CommentDTO dto) {
        Optional<Comment> optional = commentRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_COMMENT_ID_NOT_EXIST);
        Comment comment = optional.get();
        if (comment.getProduct() == null) return new Result<>(false, Messages.ONLY_TO_BUYER_COMMENTS_POSSIBLE_WRITE);
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (employee.getRole().equals(Role.SELLER)) {
            Set<Employee> sellerEmployees = comment.getProduct().getSeller().getEmployees();
            if (!sellerEmployees.stream().map(emp -> emp.getId()).toList().contains(employee.getId()))
                return new Result<>(false, String.format(Messages.YOU_CANT_REPLY_TO_THIS_COMMENT_ETC, employee.getFirstname(), employee.getLastname()));
        }
        Comment replyComment = new Comment();
        if (dto.getAttachmentIds() != null && !dto.getAttachmentIds().isEmpty()) {
            List<Attachment> attachments = attachmentRepo.findAllById(dto.getAttachmentIds());
            if (attachments.isEmpty()) return new Result<>(false, Messages.THIS_ATTACHMENTS_NOT_AVAILABLE);
            replyComment.setAttachments(attachments);
        }
        String text = dto.getText().trim();
        List<Comment> repliedComments = commentRepo.getReplyCommentsByCommentId(id);
        boolean existsByEmployeeAndText = repliedComments.stream().anyMatch(com -> com.getEmployee().getId().equals(employee.getId()) && com.getText().equals(text) && com.getIsActive().equals(Boolean.TRUE));
        if (existsByEmployeeAndText) return new Result<>(false, Messages.YOU_JUST_ADDED_THIS_COMMENT);
        replyComment.setProduct(null);
        replyComment.setEmployee(employee);
        replyComment.setText(text);
        replyComment.setReplyComments(new ArrayList<>());
        replyComment.setAmountOfStars(0);
        replyComment.setWhoWasWrittenBy(capitalizeText(employee.getRole().name()));
        replyComment = commentRepo.save(replyComment);
        logger.info("Replied to comment. ID : {}", replyComment.getId());
        repliedComments.add(replyComment);
        comment.setReplyComments(repliedComments);
        commentRepo.save(comment);
        Product product = comment.getProduct();
        Integer countOfAnsweredComments = commentRepo.getCountOfAnsweredCommentsByProductId(product.getId());
        Integer countOfAllComments = product.getAmountComments();
        int percentOfRepliedComments = Math.round((float) countOfAnsweredComments / countOfAllComments * 100);
        product.setPercentOfRepliedComments(percentOfRepliedComments);
        productRepo.save(product);
        logger.info("Product comment details updated. ID : {}", product.getId());
        Seller seller = product.getSeller();
        countOfAnsweredComments = commentRepo.getCountOfAnsweredCommentsBySellerId(seller.getId());
        countOfAllComments = seller.getAmountComments();
        percentOfRepliedComments = Math.round((float) countOfAnsweredComments / countOfAllComments * 100);
        seller.setPercentOfAnsweredComments(percentOfRepliedComments);
        sellerRepo.save(seller);
        logger.info("Seller comment details updated. ID : {}", seller.getId());
        return new Result<>(true, Messages.COMMENT_REPLIED);
    }

    @Override
    public Result<?> edit(Long id, CommentDTO dto) {
        Optional<Comment> optional = commentRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_COMMENT_ID_NOT_EXIST);
        Comment comment = optional.get();
        Result<?> result = validatePersonThatEnteredToSystem(comment);
        if (result != null) return result;
        if (comment.getProduct() != null) {
            Product product = comment.getProduct();
            if (dto.getAmountOfStars() == null || dto.getAmountOfStars() < 1 || dto.getAmountOfStars() > 5)
                return new Result<>(false, Messages.AMOUNT_OF_STARS_CAN_NOT_BE_NULL_ETC);
            comment.setAmountOfStars(dto.getAmountOfStars());
            commentRepo.save(comment);
            String averageValueOfComments = String.valueOf((float) commentRepo.getAmountOfStarsByProductId(product.getId()) / commentRepo.getCountOfAllCommentsByProductId(product.getId()));
            product.setRating(averageValueOfComments.length() > 3 ? averageValueOfComments.substring(0, 3) : averageValueOfComments);
            productRepo.save(product);
            Seller seller = product.getSeller();
            averageValueOfComments = String.valueOf((float) commentRepo.getAmountOfStarsBySellerId(seller.getId()) / commentRepo.getCountOfAllCommentsBySellerId(seller.getId()));
            seller.setRating(averageValueOfComments.length() > 3 ? averageValueOfComments.substring(0, 3) : averageValueOfComments);
            sellerRepo.save(seller);
        }
        if (dto.getAttachmentIds() != null && !dto.getAttachmentIds().isEmpty()) {
            List<Attachment> attachments = attachmentRepo.findAllById(dto.getAttachmentIds());
            if (attachments.isEmpty()) return new Result<>(false, Messages.THIS_ATTACHMENTS_NOT_AVAILABLE);
            comment.setAttachments(attachments);
        }
        String text = dto.getText().trim();
        comment.setText(text);
        commentRepo.save(comment);
        logger.info("Comment updated. ID : {}", comment.getId());
        return new Result<>(true, Messages.COMMENT_UPDATED);
    }

    @Override
    @Transactional
    @Modifying
    public Result<?> delete(Long id) {
        Optional<Comment> optional = commentRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_COMMENT_ID_NOT_EXIST);
        Comment comment = optional.get();
        Result<?> result = validatePersonThatEnteredToSystem(comment);
        if (result != null) return result;
        if (comment.getProduct() == null) {
            Long fatherCommentId = commentRepo.findFatherCommentId(comment.getId());
            String query = "DELETE FROM comment_reply_comments AS crc WHERE crc.reply_comments_id=" + id + " ";
            Query createQuery = entityManager.createNativeQuery(query);
            createQuery.executeUpdate();
            commentRepo.deleteById(id);
            logger.info("Comment deleted. ID : {}", id);
            Comment fatherComment = commentRepo.getById(fatherCommentId);
            if (fatherComment.getReplyComments().size() == 0) {
                Product product = fatherComment.getProduct();
                int percentOfAnsweredComments = Math.round((float) commentRepo.getCountOfAnsweredCommentsByProductId(product.getId()) / product.getAmountComments() * 100);
                product.setPercentOfRepliedComments(percentOfAnsweredComments);
                productRepo.save(product);
                Seller seller = product.getSeller();
                percentOfAnsweredComments = Math.round((float) commentRepo.getCountOfAnsweredCommentsBySellerId(seller.getId()) / seller.getAmountComments() * 100);
                seller.setPercentOfAnsweredComments(percentOfAnsweredComments);
                sellerRepo.save(seller);
            }
        } else {
            Product product = comment.getProduct();
            Seller seller = product.getSeller();
            String query = "DELETE FROM comment_reply_comments AS crc WHERE crc.comment_id=" + id + " ";
            Query createQuery = entityManager.createNativeQuery(query);
            createQuery.executeUpdate();
            commentRepo.deleteById(id);
            logger.info("Comment deleted. ID : {} and its all of sub comments deleted if they are.", id);
            int countOfAllCommentsByProductId = commentRepo.getCountOfAllCommentsByProductId(product.getId());
            String averageValueOfComments;
            int percentOfAnsweredComments;
            if (countOfAllCommentsByProductId != 0) {
                averageValueOfComments = String.valueOf((float) commentRepo.getAmountOfStarsByProductId(product.getId()) / countOfAllCommentsByProductId);
                percentOfAnsweredComments = Math.round((float) commentRepo.getCountOfAnsweredCommentsByProductId(product.getId()) / countOfAllCommentsByProductId * 100);
                product.setRating(averageValueOfComments.length() > 3 ? averageValueOfComments.substring(0, 3) : averageValueOfComments);
                product.setPercentOfRepliedComments(percentOfAnsweredComments);
            } else {
                product.setRating("0");
                product.setPercentOfRepliedComments(0);
            }
            product.setAmountComments(countOfAllCommentsByProductId);

            productRepo.save(product);
            int countOfAllCommentsBySellerId = commentRepo.getCountOfAllCommentsBySellerId(seller.getId());
            if (countOfAllCommentsBySellerId != 0) {
                averageValueOfComments = String.valueOf((float) commentRepo.getAmountOfStarsBySellerId(seller.getId()) / countOfAllCommentsBySellerId);
                percentOfAnsweredComments = Math.round((float) commentRepo.getCountOfAnsweredCommentsBySellerId(seller.getId()) / countOfAllCommentsBySellerId * 100);
                seller.setRating(averageValueOfComments.length() > 3 ? averageValueOfComments.substring(0, 3) : averageValueOfComments);
                seller.setPercentOfAnsweredComments(percentOfAnsweredComments);
            } else {
                seller.setRating("0");
                seller.setPercentOfAnsweredComments(0);
            }
            seller.setAmountComments(countOfAllCommentsBySellerId);
            sellerRepo.save(seller);
        }
        return new Result<>(true, Messages.COMMENT_DELETED);
    }


    private Result<?> validatePersonThatEnteredToSystem(Comment comment) {
        try {
            Buyer buyer = (Buyer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (comment.getBuyer() == null) return new Result<>(false, Messages.YOU_CANT_EDIT_DELETE_THIS_COMMENT);
            if (!comment.getBuyer().getId().equals(buyer.getId()))
                return new Result<>(false, String.format(Messages.YOU_CANT_EDIT_ANOTHER_BUYERS_COMMENT, buyer.getFirstname(), buyer.getLastname()));
        } catch (Exception e) {
            Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (employee.getRole().equals(Role.SELLER)) {
                Employee ownOfComment = comment.getEmployee();
                if (ownOfComment == null || !ownOfComment.getId().equals(employee.getId()))
                    return new Result<>(false, String.format(Messages.YOU_CANT_EDIT_DELETE_COMMENTS_OF_ANOTHER_SELLER, employee.getFirstname(), employee.getLastname()));
            }
        }
        return null;
    }

}
