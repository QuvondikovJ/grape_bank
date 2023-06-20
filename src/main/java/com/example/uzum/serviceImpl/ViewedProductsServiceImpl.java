package com.example.uzum.serviceImpl;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.viewedProducts.ViewedProductsDTO;
import com.example.uzum.entity.Product;
import com.example.uzum.entity.Seller;
import com.example.uzum.entity.ViewedProducts;
import com.example.uzum.helper.Filter;
import com.example.uzum.helper.Messages;
import com.example.uzum.dto.viewedProducts.ViewedProductCountsDTO;
import com.example.uzum.repository.ProductRepo;
import com.example.uzum.repository.SellerRepo;
import com.example.uzum.repository.ViewedProductsRepo;
import com.example.uzum.service.ViewedProductsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.uzum.helper.StringUtils.getFromAndToInterval;

@Service
public class ViewedProductsServiceImpl implements ViewedProductsService {

    @Autowired
    private ViewedProductsRepo viewedProductsRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private SellerRepo sellerRepo;
    @Autowired
    private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger(ViewedProductsServiceImpl.class);

    @Override
    public Result<?> add(ViewedProductsDTO dto) {
        Optional<Product> optional = productRepo.findByIdAndIsActive(dto.getProductId(), true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        Product product = optional.get();
        Optional<ViewedProducts> optionalVP = viewedProductsRepo.findBySessionIdAndProductId(dto.getSessionId(), dto.getProductId());
        ViewedProducts viewedProducts;
        if (optionalVP.isEmpty()) {
            viewedProducts = ViewedProducts.builder()
                    .sessionId(dto.getSessionId())
                    .product(product)
                    .build();
        } else {
            viewedProducts = optionalVP.get();
            viewedProducts.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        }
        viewedProducts = viewedProductsRepo.save(viewedProducts);
        logger.info("New viewed products added. ID : {}", viewedProducts.getId());
        return new Result<>(true, Messages.NEW_VIEWED_PRODUCT_ADDED);
    }

    @Override
    public Result<?> getBySessionId(String sessionId) {
        List<ViewedProducts> vps = viewedProductsRepo.getBySessionId(sessionId);
        if (vps.isEmpty()) return new Result<>(true, Messages.THIS_BUYER_HAS_NOT_SEEN_ANY_PRODUCTS);
        return new Result<>(true, vps);
    }

    @Override
    public Result<?> getByFilter(List<String> time, String sellerId, String order, String page) {
        List<LocalDateTime> times = getFromAndToInterval(time);
        LocalDateTime from = times.get(0);
        LocalDateTime to = times.get(1);
        String query = "SELECT pro, COUNT(vp.id) FROM ViewedProducts AS vp INNER JOIN Product AS pro ON vp.product.id=pro.id WHERE vp.createdAt>='" + Timestamp.valueOf(from) + "' AND vp.createdAt<'" + Timestamp.valueOf(to) + "' ";
        if (sellerId != null) {
            int sellerIdInt = Integer.parseInt(sellerId);
            Optional<Seller> optional = sellerRepo.findByIdAndIsActive(sellerIdInt, true);
            if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_SELLER_ID_NOT_EXIST);
            Seller seller = optional.get();
            query = query.concat(" AND pro.seller.id=" + seller.getId());
        }
        switch (order) {
            case Filter.MOST_VIEWED -> {
                query = query.concat(" GROUP BY pro.id ORDER BY COUNT(vp.id) DESC ");
            }
            case Filter.LEAST_VIEWED -> {
                query = query.concat(" GROUP BY pro.id ORDER BY COUNT(vp) ASC ");
            }
        }
        int pageInt = Integer.parseInt(page);
        Query createQuery = entityManager.createQuery(query).setFirstResult(20 * pageInt).setMaxResults(20);
        List productAndCountOfViews = createQuery.getResultList();
        logger.info("Getting viewed products by filter. Query is : {}", query);
        ViewedProductCountsDTO viewedProductCounts;
        List<ViewedProductCountsDTO> viewedProductsList = new ArrayList<>();
        if (!productAndCountOfViews.isEmpty()) {
            for (int i = 0; i < productAndCountOfViews.size(); i++) {
                Object[] obj = (Object[]) productAndCountOfViews.get(i);
                Product product = (Product) obj[0];
                Long amountOfView = (Long) obj[1];
                viewedProductCounts = ViewedProductCountsDTO.builder()
                        .product(product)
                        .amountOfView(amountOfView)
                        .build();
                viewedProductsList.add(viewedProductCounts);
            }
            return new Result<>(true, viewedProductsList);
        }
        return new Result<>(false, Messages.ANY_PRODUCTS_HAVE_NOT_SEEN_YET);
    }
}
