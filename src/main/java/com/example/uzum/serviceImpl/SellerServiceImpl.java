package com.example.uzum.serviceImpl;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.seller.SellerDTO;
import com.example.uzum.dto.seller.SellerDTOToSendUsers;
import com.example.uzum.entity.Attachment;
import com.example.uzum.entity.Employee;
import com.example.uzum.entity.Seller;
import com.example.uzum.entity.enums.Role;
import com.example.uzum.helper.Filter;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.*;
import com.example.uzum.service.SellerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.uzum.helper.StringUtils.getFromAndToInterval;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepo sellerRepo;
    @Autowired
    private AttachmentRepo attachmentRepo;
    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private CommentRepo commentRepo;

    private static final Logger logger = LogManager.getLogger(SellerServiceImpl.class);

    @Override
    public Result<?> add(SellerDTO sellerDto) {
        boolean existsByNameAndIsActive = sellerRepo.existsByNameAndIsActive(sellerDto.getName(), true);
        if (existsByNameAndIsActive) return new Result<>(false, Messages.THIS_SELLER_ALREADY_ADDED);
        Seller seller = new Seller();
        seller.setName(sellerDto.getName());
        seller.setInfo(sellerDto.getInfo());
        Result<?> result = duplicateCodeInRegisterAndEdit(seller, sellerDto);
        if (!result.getSuccess()) return result;
        seller = (Seller) result.getData();
        seller = sellerRepo.save(seller);
        logger.info("New seller added. ID : {}", seller.getId());
        return new Result<>(true, Messages.SELLER_SAVED);
    }

    @Override
    public Result<?> getByFilter(String search, String order, List<String> time, String page, String isActive) {
        List<LocalDateTime> times = getFromAndToInterval(time);
        LocalDateTime from = times.get(0);
        LocalDateTime to = times.get(1);
        boolean isActiveBoolean = Boolean.parseBoolean(isActive);
        int pageInt = Integer.parseInt(page);
        Sort sort = Sort.by("name");
        switch (order) {
            case Filter.ALPHABET -> {
                sort = Sort.by("name").ascending();
            }
            case Filter.RATING_ASC -> {
                sort = Sort.by("rating").ascending();
            }
            case Filter.RATING_DESC -> {
                sort = Sort.by("rating").descending();
            }
            case Filter.MOST_SOLD -> {
                sort = Sort.by("amount_sold_products").descending();
            }
            case Filter.LOWEST_SOLD -> {
                sort = Sort.by("amount_sold_products").ascending();
            }
            case Filter.COST_OF_SOLD_PRODUCTS_DESC -> {
                sort = Sort.by("cost_of_sold_products").descending();
            }
            case Filter.COST_OF_SOLD_PRODUCTS_ASC -> {
                sort = Sort.by("cost_of_sold_products").ascending();
            }
            case Filter.RECENTLY_ADDED -> {
                sort = Sort.by("created_date").descending();
            }
            case Filter.PERCENT_OF_ANSWERED_COMMENTS_ASC -> {
                sort = Sort.by("percent_of_answered_comments").ascending();
            }
            case Filter.PERCENT_OF_ANSWERED_COMMENTS_DESC -> {
                sort = Sort.by("percent_of_answered_comments").descending();
            }
        }
        Pageable pageable = PageRequest.of(pageInt, 20, sort);
        Page<Seller> sellers = sellerRepo.findByFilter(search, Timestamp.valueOf(from), Timestamp.valueOf(to), isActiveBoolean, pageable);
        return new Result<>(true, sellers);
    }


    @Override
    public Result<?> getById(Integer id) {
        Optional<Seller> optional = sellerRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_SELLER_ID_NOT_EXIST);
        Seller seller = optional.get();
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (employee.getRole().equals(Role.SELLER)) {
            if (!seller.getEmployees().contains(employee)) {
                return new Result<>(false, String.format(Messages.YOU_CANT_SEE_INFORMATION_OF_ANOTHER_SELLER, employee.getFirstname(), employee.getLastname()));
            }
        }
        List<Attachment> attachments = seller.getAttachments();
        List<Long> attachmentIds = attachments.stream().map(att -> att.getId()).toList();
        SellerDTOToSendUsers toSendUsers = SellerDTOToSendUsers.builder()
                .id(seller.getId())
                .name(seller.getName())
                .amountSoldProducts(seller.getAmountSoldProducts())
                .amountComments(seller.getAmountComments())
                .rating(seller.getRating())
                .info(seller.getInfo())
                .joiningDate(seller.getCreatedDate())
                .attachmentIds(attachmentIds)
                .build();
        return new Result<>(true, toSendUsers);
    }

    @Override
    public Result<?> edit(Integer id, SellerDTO sellerDto) {
        Optional<Seller> optional = sellerRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_SELLER_ID_NOT_EXIST);
        Seller seller = optional.get();
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (employee.getRole().equals(Role.SELLER)) {
            if (!seller.getEmployees().contains(employee)) {
                return new Result<>(false, String.format(Messages.YOU_CANT_EDIT_INFORMATION_OF_ANOTHER_SELLER, employee.getFirstname(), employee.getLastname()));
            }
        }
        boolean existsByNewNameAndIsActive = sellerRepo.existsByNameAndIsActiveAndIdNot(sellerDto.getName(), true, id);
        if (existsByNewNameAndIsActive) return new Result<>(false, Messages.THIS_SELLER_ALREADY_ADDED);
        seller.setName(sellerDto.getName());
        seller.setInfo(sellerDto.getInfo());
        Result<?> result = duplicateCodeInRegisterAndEdit(seller, sellerDto);
        if (!result.getSuccess()) return result;
        seller = (Seller) result.getData();
        sellerRepo.save(seller);
        logger.info("Seller updated. ID : {}", id);
        return new Result<>(true, Messages.SELLER_UPDATED);
    }

    @Override
    public Result<?> delete(Integer id) {
        Optional<Seller> optional = sellerRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_SELLER_ID_NOT_EXIST);
        commentRepo.deleteOrRestoreCommentsBySellerId(id, false);
        productRepo.deleteOrRestoreProductsBySellerId(id, false);
        sellerRepo.deleteOrRestoreSeller(id, false);
        logger.info("Seller deactivated. ID : {}", id);
        return new Result<>(true, Messages.SELLER_DELETED);
    }

    @Override
    public Result<?> restoreSeller(Integer id) {
        Optional<Seller> optional = sellerRepo.findByIdAndIsActive(id, false);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_DELETED_SELLER_ID_NOT_EXIST);
        commentRepo.deleteOrRestoreCommentsBySellerId(id, true);
        productRepo.deleteOrRestoreProductsBySellerId(id, true);
        sellerRepo.deleteOrRestoreSeller(id, true);
        logger.info("Seller restored. ID : {}", id);
        return new Result<>(true, Messages.SELLER_RECOVERED);
    }

    private Result<?> duplicateCodeInRegisterAndEdit(Seller seller, SellerDTO sellerDto) { // here just only employees and attachments are being set to seller.
        Set<Employee> employees = employeeRepo.findAllByIdAndIsActive(sellerDto.getSellerIDs());
        if (employees.isEmpty()) return new Result<>(false, Messages.THIS_EMPLOYEE_IDS_ARE_UNAVAILABLE_ETC);
        seller.setEmployees(employees);
        List<Attachment> attachments = new ArrayList<>();
        Optional<Attachment> optionalDefaultLogoAndTemplate = attachmentRepo.findById(1L);
        Attachment defaultLogoAndTemplate = optionalDefaultLogoAndTemplate.get();
        if (sellerDto.getLogoAttachmentId() != null) {
            Optional<Attachment> optionalLogo = attachmentRepo.findById(sellerDto.getLogoAttachmentId());
            if (optionalLogo.isPresent()) {
                Attachment logo = optionalLogo.get();
                attachments.add(logo);
            } else {
                attachments.add(defaultLogoAndTemplate);
            }
        } else {
            attachments.add(defaultLogoAndTemplate);
        }
        if (sellerDto.getTemplateAttachmentId() != null) {
            Optional<Attachment> optionalTemplate = attachmentRepo.findById(sellerDto.getTemplateAttachmentId());
            if (optionalTemplate.isPresent()) {
                Attachment template = optionalTemplate.get();
                attachments.add(template);
            } else {
                attachments.add(defaultLogoAndTemplate);
            }
        } else attachments.add(defaultLogoAndTemplate);
        seller.setAttachments(attachments);
        return new Result<>(true, seller);
    }

}
