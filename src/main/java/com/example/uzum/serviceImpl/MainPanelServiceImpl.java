package com.example.uzum.serviceImpl;

import com.example.uzum.dto.mainPanel.MainPanelDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.entity.*;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.*;
import com.example.uzum.service.MainPanelService;
import com.example.uzum.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MainPanelServiceImpl implements MainPanelService {

    @Autowired
    private MainPanelRepo mainPanelRepo;
    @Autowired
    private AttachmentRepo attachmentRepo;
    @Autowired
    private ProductService productService;
    @Autowired
    private PanelConnectMethodRepo panelConnectMethodRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private SellerRepo sellerRepo;

    private static final Logger logger = LogManager.getLogger(MainPanelServiceImpl.class);


    @Override
    public Result<?> add(MainPanelDTO mainPanelDto) {
        if ((mainPanelDto.getNameEn() == null || mainPanelDto.getNameUz() == null) && mainPanelDto.getAttachmentId() == null)
            return new Result<>(false, Messages.NAME_OR_ATTACHMENT_MUST_BE_AT_LEAST);
        MainPanel mainPanel = new MainPanel();
        if (mainPanelDto.getNameEn() != null && mainPanelDto.getNameUz() != null) {
            boolean existsByName = mainPanelRepo.existsByNameEnAndIsActiveOrNameUzAndIsActive(mainPanelDto.getNameEn(), true, mainPanelDto.getNameUz(), true);
            if (existsByName) return new Result<>(false, Messages.THIS_PANEL_IS_ALREADY_ADDED);
            mainPanel.setNameEn(mainPanelDto.getNameEn());
            mainPanel.setNameUz(mainPanelDto.getNameUz());
            mainPanel.setHowManyGetProduct(mainPanelDto.getHowManyGetProduct());
            mainPanel.setIsDrawCarousel(mainPanelDto.getIsDrawCarousel());
            int maxOrder = mainPanelRepo.getMaxOrder() != null ? mainPanelRepo.getMaxOrder() : 0;
            if (mainPanelDto.getOrder() > maxOrder) {
                mainPanel.setPanelOrder(mainPanelDto.getOrder());
            } else {
                mainPanelRepo.updateOrderToAdd(mainPanelDto.getOrder());
                mainPanel.setPanelOrder(mainPanelDto.getOrder());
            }

        } else {
            Optional<Attachment> optionalAttachment = attachmentRepo.findById(mainPanelDto.getAttachmentId());
            if (optionalAttachment.isEmpty()) return new Result<>(false, Messages.SUCH_ATTACHMENT_ID_NOT_EXIST);
            Attachment attachment = optionalAttachment.get();
            mainPanel.setAttachment(attachment);
            mainPanel.setLink(mainPanelDto.getLink());
            int maxOrder = mainPanelRepo.getMaxOrder() != null ? mainPanelRepo.getMaxOrder() : 0;
            if (mainPanelDto.getOrder() > maxOrder) {
                mainPanel.setPanelOrder(mainPanelDto.getOrder());
            } else {
                mainPanelRepo.updateOrderToAdd(mainPanelDto.getOrder());
                mainPanel.setPanelOrder(mainPanelDto.getOrder());
            }
        }
        mainPanel = mainPanelRepo.save(mainPanel);
        logger.info("New panel added. ID : {}", mainPanel.getId());
        return new Result<>(true, Messages.NEW_PANEL_SAVED);
    }


    @Override
    public Result<?> getAll() {
        List<MainPanel> mainPanels = mainPanelRepo.findAllByIsActiveOrderByPanelOrder(true);
        if (mainPanels.isEmpty()) return new Result<>(true, Messages.ANY_PANEL_IS_NOT_ADDED_YET);
        return new Result<>(true, mainPanels);
    }


    @Override
    public Result<?> getById(Integer id) {
        Optional<MainPanel> optional = mainPanelRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_MAIN_PANEL_ID_NOT_EXIST);
        return new Result<>(true, optional.get());
    }

    @Override
    public Result<?> getOrdersCount() {
        Integer maxOrder = mainPanelRepo.getMaxOrder();
        return new Result<>(true, maxOrder);
    }

    @Override
    public Result<?> edit(Integer id, MainPanelDTO mainPanelDto) {
        Optional<MainPanel> optional = mainPanelRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_MAIN_PANEL_ID_NOT_EXIST);
        MainPanel mainPanel = optional.get();
        if ((mainPanelDto.getNameUz() == null || mainPanelDto.getNameEn() == null) && mainPanelDto.getAttachmentId() == null)
            return new Result<>(false, Messages.NAME_OR_ATTACHMENT_MUST_BE_AT_LEAST);
        if (mainPanelDto.getNameEn() != null && mainPanelDto.getNameUz() != null) {
            boolean existsByName = mainPanelRepo.existsByNameEnAndIsActiveAndIdNotOrNameUzAndIsActiveAndIdNot(mainPanelDto.getNameEn(), true, id, mainPanelDto.getNameUz(), true, id);
            if (existsByName) return new Result<>(false, Messages.THIS_PANEL_IS_ALREADY_ADDED);
            mainPanel.setNameEn(mainPanelDto.getNameEn());
            mainPanel.setNameUz(mainPanelDto.getNameUz());
            mainPanel.setHowManyGetProduct(mainPanelDto.getHowManyGetProduct());
            mainPanel.setIsDrawCarousel(mainPanelDto.getIsDrawCarousel());
            if (mainPanel.getPanelOrder() < mainPanelDto.getOrder()) {
                mainPanelRepo.updateOrderFromOldOrder(mainPanel.getPanelOrder(), mainPanelDto.getOrder());
            } else if (mainPanel.getPanelOrder() > mainPanelDto.getOrder()) {
                mainPanelRepo.updateOrderToOldOrder(mainPanel.getPanelOrder(), mainPanelDto.getOrder());
            }
            mainPanel.setPanelOrder(mainPanelDto.getOrder());
        } else {
            Optional<Attachment> optionalAttachment = attachmentRepo.findById(mainPanelDto.getAttachmentId());
            if (optionalAttachment.isEmpty()) return new Result<>(false, Messages.SUCH_ATTACHMENT_ID_NOT_EXIST);
            Attachment attachment = optionalAttachment.get();
            mainPanel.setLink(mainPanelDto.getLink());
            mainPanel.setAttachment(attachment);
            if (mainPanelDto.getOrder() > mainPanel.getPanelOrder()) {
                mainPanelRepo.updateOrderFromOldOrder(mainPanel.getPanelOrder(), mainPanelDto.getOrder());
            } else if (mainPanelDto.getOrder() < mainPanel.getPanelOrder()) {
                mainPanelRepo.updateOrderToOldOrder(mainPanel.getPanelOrder(), mainPanelDto.getOrder());
            }
            mainPanel.setPanelOrder(mainPanelDto.getOrder());
        }
        mainPanelRepo.save(mainPanel);
        logger.info("Main panel updated. ID : {} ", id);
        return new Result<>(true, Messages.MAIN_PANEL_UPDATED);
    }


    @Override
    public Result<?> delete(Integer id) {
        Optional<MainPanel> optional = mainPanelRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_MAIN_PANEL_ID_NOT_EXIST);
        MainPanel mainPanel = optional.get();
        mainPanel.setIsActive(false);
        mainPanelRepo.updateOrderToDelete(mainPanel.getPanelOrder());
        disconnectPanel(id);
        logger.info("Main panel disconnected from everything and deactivated. ID : {}", id);
        return new Result<>(true, Messages.MAIN_PANEL_DELETED);
    }


    @Override
    public Result<?> connectPanelsToMethods(String panelId, String methodName) {
        Result<?> result = validateMainPanel(panelId, methodName, null, null, null);
        if (!result.getSuccess()) return result;
        MainPanel mainPanel = (MainPanel) result.getData();
        List<String> methodNames = productService.getMethodNamesForConnectToPanels();
        if (!methodNames.contains(methodName)) return new Result<>(false, Messages.SUCH_METHOD_NAME_NOT_EXIST);
        PanelConnectMethod connect = new PanelConnectMethod();
        connect.setMainPanel(mainPanel);
        connect.setMethodName(methodName);
        panelConnectMethodRepo.save(connect);
        logger.info("Panel connected to method. Panel ID : {}, Method Name : {}",panelId, methodName);
        return new Result<>(true, Messages.PANEL_CONNECTED_TO_METHOD);
    }


    @Override
    public Result<?> connectPanelsToSellers(String panelId, String sellerId) {
        Result<?> result = validateMainPanel(panelId, null, null, sellerId, null);
        if (!result.getSuccess()) return result;
        int sellerIdInt = Integer.parseInt(sellerId);
        MainPanel mainPanel = (MainPanel) result.getData();
        Optional<Seller> optionalSeller = sellerRepo.findByIdAndIsActive(sellerIdInt, true);
        if (optionalSeller.isEmpty()) return new Result<>(false, Messages.SUCH_SELLER_ID_NOT_EXIST);
        Seller seller = optionalSeller.get();
        seller.setMainPanel(mainPanel);
        sellerRepo.save(seller);
        logger.info("Panel connected to seller. Panel ID : {}, Seller ID : {}",panelId, sellerId);
        return new Result<>(true, Messages.MAIN_PANEL_CONNECTED_TO_SELLER);
    }

    @Override
    public Result<?> connectPanelsToCategories(String panelId, String categoryId) {
        Result<?> result = validateMainPanel(panelId, null, categoryId, null, null);
        if (!result.getSuccess()) return result;
        MainPanel mainPanel = (MainPanel) result.getData();
        int categoryIdInt = Integer.parseInt(categoryId);
        Optional<Category> optionalCategory = categoryRepo.findByIdAndIsActive(categoryIdInt, true);
        if (optionalCategory.isEmpty()) return new Result<>(false, Messages.NO_ID_CATEGORY);
        Category category = optionalCategory.get();
        category.setMainPanel(mainPanel);
        categoryRepo.save(category);
        logger.info("Panel connected to category. Panel ID : {}, Category ID : {}", panelId,categoryId);
        return new Result<>(true, Messages.MAIN_PANEL_CONNECTED_TO_CATEGORY);
    }

    @Override
    public Result<?> connectPanelsToProducts(String panelId, String productId) {
        Result<?> result = validateMainPanel(panelId, null, null, null, productId);
        MainPanel mainPanel;
        if (!result.getSuccess()) {
            if (!result.getData().toString().equals(String.format(Messages.THIS_PANEL_ALREADY_CONNECTED_WITH_SOMETHING, "product")))
                return result;
            Optional<MainPanel> optional = mainPanelRepo.findByIdAndIsActive(Integer.parseInt(panelId), true);
            mainPanel = optional.get();
        } else {
            mainPanel = (MainPanel) result.getData();
        }
        Optional<Product> optionalProduct = productRepo.findByIdAndIsActive(Integer.parseInt(productId), true);
        if (optionalProduct.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        Product product = optionalProduct.get();
        product.setMainPanel(mainPanel);
        productRepo.save(product);
        logger.info("Panel connected to product. Panel ID : {}, Product ID: {}", panelId, productId);
        return new Result<>(true, Messages.MAIN_PANEL_CONNECTED_TO_PRODUCT);
    }

    private Result<?> validateMainPanel(String panelId, String methodName, String categoryId, String sellerId, String productId) {
        int mainPanelId = Integer.parseInt(panelId);
        Optional<MainPanel> optional = mainPanelRepo.findByIdAndIsActive(mainPanelId, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_MAIN_PANEL_ID_NOT_EXIST);
        Optional<PanelConnectMethod> connectMethodOptional = panelConnectMethodRepo.findByMainPanelId(mainPanelId);
        if (connectMethodOptional.isPresent())
            return new Result<>(false, String.format(Messages.THIS_PANEL_ALREADY_CONNECTED_WITH_SOMETHING, "method"));
        boolean isPanelConnectedWithCategory = categoryRepo.existsByMainPanelIdAndIsActive(mainPanelId, Boolean.TRUE);
        if (isPanelConnectedWithCategory)
            return new Result<>(false, String.format(Messages.THIS_PANEL_ALREADY_CONNECTED_WITH_SOMETHING, "category"));
        boolean isPanelConnectedWithSeller = sellerRepo.existsByMainPanelIdAndIsActive(mainPanelId, Boolean.TRUE);
        if (isPanelConnectedWithSeller)
            return new Result<>(false, String.format(Messages.THIS_PANEL_ALREADY_CONNECTED_WITH_SOMETHING, "seller"));
        boolean isPanelConnectedWithProduct = productRepo.existsByMainPanelIdAndIsActive(mainPanelId, true);
        if (isPanelConnectedWithProduct)
            return new Result<>(false, String.format(Messages.THIS_PANEL_ALREADY_CONNECTED_WITH_SOMETHING, "product"));
        if (methodName != null) {
            connectMethodOptional = panelConnectMethodRepo.findByMethodName(methodName);
            if (connectMethodOptional.isPresent())
                return new Result<>(false, Messages.THIS_METHOD_ALREADY_CONNECTED_WITH_ANOTHER_PANEL);
        }
        if (categoryId != null) {
            Optional<Category> optionalCategory = categoryRepo.findByIdAndIsActive(Integer.parseInt(categoryId), true);
            Category category = optionalCategory.get();
            if (category.getMainPanel() != null)
                return new Result<>(false, Messages.THIS_CATEGORY_ALREADY_CONNECTED_WITH_ANOTHER_PANEL);
        }
        if (sellerId != null) {
            Optional<Seller> optionalSeller = sellerRepo.findByIdAndIsActive(Integer.parseInt(sellerId), Boolean.TRUE);
            Seller seller = optionalSeller.get();
            if (seller.getMainPanel() != null)
                return new Result<>(false, Messages.THIS_SELLER_ALREADY_CONNECTED_WITH_ANOTHER_PANEL);
        }
        if (productId != null) {
            Optional<Product> optionalProduct = productRepo.findByIdAndIsActive(Integer.parseInt(productId), true);
            Product product = optionalProduct.get();
            if (product.getMainPanel() != null)
                return new Result<>(false, Messages.THIS_PRODUCT_ALREADY_CONNECTED_WITH_ANOTHER_PANEL);
        }
        return new Result<>(true, optional.get());
    }


    @Override
    public Result<?> getMethodNamesToAddPanel() {
        List<String> connectedMethodNames = panelConnectMethodRepo.getMethodNames();
        List<String> allMethodNames = productService.getMethodNamesForConnectToPanels();
        List<String> unConnectedMethodNames;
        unConnectedMethodNames = allMethodNames.stream().filter(m -> !connectedMethodNames.contains(m)).toList();
        return new Result<>(true, unConnectedMethodNames);
    }


    @Override
    public Result<?> getUnconnectedPanels() {
        List<MainPanel> connectedPanels = mainPanelRepo.getConnectedPanels();
        List<MainPanel> getUnconnectedPanels = mainPanelRepo.getUnconnectedPanels(
                connectedPanels.stream().map(pan -> pan.getId()).toList()
        );
        if (getUnconnectedPanels.isEmpty()) return new Result<>(true, Messages.ALL_PANELS_CONNECTED);
        return new Result<>(true, getUnconnectedPanels);
    }

    @Override
    public Result<?> getConnectedPanels() {
        List<MainPanel> getConnectedPanels = mainPanelRepo.getConnectedPanels();
        if (getConnectedPanels.isEmpty()) return new Result<>(true, Messages.ANY_PANELS_NOT_CONNECTED);
        return new Result<>(true, getConnectedPanels);
    }

    @Override
    public Result<?> disconnectPanel(Integer id) {
        panelConnectMethodRepo.deleteByMainPanelId(id);
        categoryRepo.disconnectByMainPanelId(id);
        sellerRepo.disconnectByMainPanelId(id);
        productRepo.disconnectByMainPanelId(id);
        logger.info("Panel disconnected from everything. Panel ID : {}", id);
        return new Result<>(true, Messages.CONNECTION_DELETED);
    }
}
