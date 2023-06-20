package com.example.uzum.serviceImpl;

import com.example.uzum.dto.favourite.FavouriteDto;
import com.example.uzum.dto.Result;
import com.example.uzum.entity.Buyer;
import com.example.uzum.entity.Favourite;
import com.example.uzum.entity.Product;
import com.example.uzum.helper.Filter;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.BuyerRepo;
import com.example.uzum.repository.FavouriteRepo;
import com.example.uzum.repository.ProductRepo;
import com.example.uzum.service.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class FavouriteServiceImpl implements FavouriteService {

    @Autowired
    private FavouriteRepo favouriteRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private BuyerRepo buyerRepo;
    @Autowired
    private EntityManager entityManager;

    @Override
    public Result<?> add(FavouriteDto dto) { // qachonki user login va registered qilanda bu metod ishlab ketsin.
        Favourite favourite;
        if (dto.getBuyerId() != null) {
            Optional<Buyer> optionalBuyer = buyerRepo.findByIdAndIsActive(dto.getBuyerId(), true);
            if (optionalBuyer.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
            Buyer buyer = optionalBuyer.get();
            String sql = "INSERT INTO favourite(product_id, buyer_id, cookie) AS fav " +
                    " SELECT fav2.product_id AS pro_id, " + buyer.getId() + ", null FROM favourite AS fav2 WHERE pro_id IN " +
                    " (SELECT fav3.product_id FROM favourite AS fav3 WHERE fav3.cookie=" + dto.getCookie() + ") AND pro_id NOT IN " +
                    " (SELECT fav4.product_id FROM favourite AS fav4 WHERE fav4.buyer_id=" + buyer.getId() + ")";
            Query createQuery = entityManager.createNativeQuery(sql);
            createQuery.executeUpdate();
            Optional<Product> optionalProduct = productRepo.findByIdAndIsActive(dto.getProductId(), true);
            if (optionalProduct.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
            Product product = optionalProduct.get();
            Optional<Favourite> optionalFavourite = favouriteRepo.findByBuyerIdAndProductId(buyer.getId(), dto.getProductId());
            if (optionalFavourite.isPresent())
                return new Result<>(false, Messages.THIS_PRODUCT_ALREADY_ADDED_TO_FAVOURITE_PRODUCT_LIST);
            favourite = Favourite.builder()
                    .cookie(null)
                    .buyer(buyer)
                    .product(product)
                    .build();
        } else {
            Optional<Product> optionalProduct = productRepo.findByIdAndIsActive(dto.getProductId(), true);
            if (optionalProduct.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
            Product product = optionalProduct.get();
            Optional<Favourite> optionalFavourite = favouriteRepo.findByCookieAndProductId(dto.getCookie(), product.getId());
            if (optionalFavourite.isPresent())
                return new Result<>(false, Messages.THIS_PRODUCT_ALREADY_ADDED_TO_FAVOURITE_PRODUCT_LIST);
            favourite = Favourite.builder()
                    .buyer(null)
                    .cookie(dto.getCookie())
                    .product(product)
                    .build();
        }
        favouriteRepo.save(favourite);
        return new Result<>(true, Messages.PRODUCT_ADDED_TO_FAVOURITE_PRODUCT_LIST);
    }

    @Override
    public Result<?> getByFilter(String cookie, String buyerId, String order, String page) {
        int pageInt = Integer.parseInt(page);
        if (buyerId != null) {
            int buyerIdInt = Integer.parseInt(buyerId);
            Optional<Buyer> optionalBuyer = buyerRepo.findByIdAndIsActive(buyerIdInt, true);
            if (optionalBuyer.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
            Pageable pageable = PageRequest.of(pageInt, 20);
            List<LocalDateTime> times = getFromAndToInterval(List.of(Filter.LATEST_WEEK));
            Timestamp from = Timestamp.valueOf(times.get(0));
            Timestamp to = Timestamp.valueOf(times.get(1));
            List<Product> products = new ArrayList<>();
            String sqlQuery;
            switch (order) {
                case Filter.PRICE_ASC -> {
                    pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.ASC, "pro.discounted_price"));
                }
                case Filter.PRICE_DESC -> {
                    pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "pro.discounted_price"));
                }
                case Filter.RATING_DESC -> {
                    pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "pro.rating"));
                }
                case Filter.MOST_SOLD -> {
                    pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "pro.amount_of_sold_products"));
                }
                case Filter.RECENTLY_ADDED -> {
                    pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "pro.created_at"));
                }
            }
            Page<Product> productPage = productRepo.getFavouriteProductsByBuyerId(buyerIdInt, pageable);
        } else {
            Pageable pageable = PageRequest.of(pageInt, 20);
            List<LocalDateTime> times = getFromAndToInterval(List.of(Filter.LATEST_WEEK));
            Timestamp from = Timestamp.valueOf(times.get(0));
            Timestamp to = Timestamp.valueOf(times.get(1));
            List<Product> products = new ArrayList<>();
            String sqlQuery;
            switch (order) {
                case Filter.PRICE_ASC -> {
                    pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.ASC, "pro.discounted_price"));
                }
                case Filter.PRICE_DESC -> {
                    pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "pro.discounted_price"));
                }
                case Filter.RATING_DESC -> {
                    pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "pro.rating"));
                }
                case Filter.MOST_SOLD -> {
                    pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "pro.amount_of_sold_products"));
                }
                case Filter.RECENTLY_ADDED -> {
                    pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "pro.created_at"));
                }
            }
            Page<Product> productPage = productRepo.getFavouriteProductsByCookie(cookie, pageable);
        }
        return null;
    }

    @Override
    public Result<?> getAmountOfFavouriteProduct(Integer productId) {
        Optional<Product> optionalProduct = productRepo.findByIdAndIsActive(productId, true);
        if (optionalProduct.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        Integer getAmountOfFavouriteProducts = favouriteRepo.getAmountOfFavouriteProducts(productId);
        return new Result<>(true, getAmountOfFavouriteProducts);
    }

    @Override
    public Result<?> checkOutProduct(FavouriteDto dto) {
        Optional<Product> optionalProduct = productRepo.findByIdAndIsActive(dto.getProductId(), true);
        if (optionalProduct.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        if (dto.getBuyerId() != null) {
            Optional<Buyer> optionalBuyer = buyerRepo.findByIdAndIsActive(dto.getBuyerId(), true);
            if (optionalBuyer.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
            Optional<Favourite> optionalFavourite = favouriteRepo.findByBuyerIdAndProductId(dto.getBuyerId(), dto.getProductId());
            if (optionalFavourite.isEmpty()) return new Result<>(false, false);
        } else {
            Optional<Favourite> optionalFavourite = favouriteRepo.findByCookieAndProductId(dto.getCookie(), dto.getProductId());
            if (optionalFavourite.isEmpty()) return new Result<>(false, false);
        }
        return new Result<>(false, true);
    }

    @Override
    public Result<?> delete(FavouriteDto dto) {
        Optional<Product> optionalProduct = productRepo.findByIdAndIsActive(dto.getProductId(), true);
        if (optionalProduct.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        if (dto.getBuyerId() != null) {
            Optional<Buyer> optionalBuyer = buyerRepo.findByIdAndIsActive(dto.getBuyerId(), true);
            if (optionalBuyer.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
            Optional<Favourite> optionalFavourite = favouriteRepo.findByBuyerIdAndProductId(dto.getBuyerId(), dto.getProductId());
            if (optionalFavourite.isEmpty())
                return new Result<>(false, Messages.THIS_PRODUCT_NOT_EXIST_IN_YOUR_FAVOURITE_LIST);
            favouriteRepo.deleteByProductId(dto.getProductId());
        } else {
            Optional<Favourite> optionalFavourite = favouriteRepo.findByCookieAndProductId(dto.getCookie(), dto.getProductId());
            if (optionalFavourite.isEmpty())
                return new Result<>(false, Messages.THIS_PRODUCT_NOT_EXIST_IN_YOUR_FAVOURITE_LIST);
            favouriteRepo.deleteByCookie(dto.getCookie());
        }
        return new Result<>(false, Messages.PRODUCT_DELETED_FROM_FAVOURITE_LIST);
    }
}
