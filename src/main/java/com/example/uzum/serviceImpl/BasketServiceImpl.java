package com.example.uzum.serviceImpl;

import com.example.uzum.dto.Result;
import com.example.uzum.entity.*;
import com.example.uzum.entity.enums.Role;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.*;
import com.example.uzum.service.BasketService;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BasketServiceImpl implements BasketService {

    @Autowired
    private BasketRepo basketRepo;
    @Autowired
    private BuyerRepo buyerRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private RegionRepo regionRepo;
    @Autowired
    private BranchRepo branchRepo;

    @Value(value = "${geoapify.api.key}")
    private String geoapifyApiKey;
    @Value(value = "${ip.access.key}")
    private String ipAccessKey;

    private static final Logger logger = LogManager.getLogger(BasketServiceImpl.class);

    @Override
    public Result<?> add(String sessionId, String buyerId, String productId) {
        if (sessionId == null && buyerId == null) return new Result<>(false, Messages.SESSION_ID_OR_BUYER_ID_REQUIRED);
        int productIdInt = Integer.parseInt(productId);
        Optional<Product> optionalProduct = productRepo.findByIdAndIsActive(productIdInt, true);
        if (optionalProduct.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        Product product = optionalProduct.get();
        if (buyerId != null) {
            int buyerIdInt = Integer.parseInt(buyerId);
            Optional<Buyer> optionalBuyer = buyerRepo.findByIdAndIsActive(buyerIdInt, true);
            if (optionalBuyer.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
            Buyer buyer = optionalBuyer.get();
            Optional<Basket> optionalBasket = basketRepo.findByBuyerIdAndOrderedIs(buyerIdInt);
            Basket basket;
            if (optionalBasket.isEmpty()) {
                Map<Product, Integer> map = Map.of(product, 1);
                basket = Basket.builder()
                        .productAmount(map)
                        .buyer(buyer)
                        .sessionId(null)
                        .isOrdered(Boolean.FALSE)
                        .build();
            } else {
                basket = optionalBasket.get();
                Map<Product, Integer> map = basket.getProductAmount();
                Optional<Product> optionalBasketProduct = map.keySet().stream().filter(pro -> pro.getId().equals(productIdInt)).findFirst();
                if (optionalBasketProduct.isEmpty()) {
                    map.put(product, 1);
                } else {
                    Product basketProduct = optionalBasketProduct.get();
                    Integer productAmount = map.get(basketProduct);
                    map.replace(basketProduct, productAmount, productAmount + 1);
                }
                basket.setProductAmount(map);
            }
            Set<Product> buyerBasketProducts = basket.getProductAmount().keySet();
            Map<Product, Integer> basketImmutableMap = basket.getProductAmount();
            Map<Product, Integer> basketMap = new HashMap<>(basketImmutableMap);

            /* Copy session ID basket to Buyer ID basket */
            Optional<Basket> optionalSessionBasket = basketRepo.findBySessionIdAndOrderedIs(sessionId);
            if (optionalSessionBasket.isPresent()) {
                Basket sessionBasket = optionalSessionBasket.get();
                Map<Product, Integer> sessionMap = sessionBasket.getProductAmount();
                Set<Product> products = sessionMap.keySet().stream().filter(pro -> !buyerBasketProducts.contains(pro)).collect(Collectors.toSet());
                for (Product sessionProduct : products) {
                    basketMap.put(sessionProduct, 1);
                    logger.info("Product copied from session basket to buyer basket. ID : {}", sessionProduct.getId());
                }
            }
            basket.setProductAmount(basketMap);
            basket = basketRepo.save(basket);
            logger.info("Basket saved. ID : {}", basket.getId());
        } else {
            Basket basket;
            Optional<Basket> optionalBasket = basketRepo.findBySessionIdAndOrderedIs(sessionId);
            if (optionalBasket.isEmpty()) {
                Map<Product, Integer> map = Map.of(product, 1);
                basket = Basket.builder()
                        .productAmount(map)
                        .sessionId(sessionId)
                        .buyer(null)
                        .isOrdered(Boolean.FALSE)
                        .build();
            } else {
                basket = optionalBasket.get();
                Map<Product, Integer> map = basket.getProductAmount();
                Optional<Product> optionalBasketProduct = map.keySet().stream().filter(pro -> pro.getId().equals(productIdInt)).findFirst();
                if (optionalBasketProduct.isEmpty()) {
                    map.put(product, 1);
                } else {
                    Product basketProduct = optionalBasketProduct.get();
                    Integer productAmount = map.get(basketProduct);
                    map.replace(basketProduct, productAmount, productAmount + 1);
                }
                basket.setProductAmount(map);
            }
            basket = basketRepo.save(basket);
            logger.info("Basket saved. ID : {}", basket.getId());
        }
        return new Result<>(true, Messages.PRODUCT_ADDED_TO_BASKET);
    }

    @Override
    public Result<?> getBySessionId(String sessionId) {
        Optional<Basket> optionalBasket = basketRepo.findBySessionIdAndOrderedIs(sessionId);
        if (optionalBasket.isEmpty()) {
            return new Result<>(true, Messages.THIS_BUYER_HAS_NOT_ADDED_ANY_PRODUCTS_TO_BASKET_YET);
        }
        Basket basket = optionalBasket.get();
        return new Result<>(true, basket);
    }

    @Override
    public Result<?> getByBuyerId(Integer buyerId) {
        try {
            Buyer buyer = (Buyer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!buyer.getId().equals(buyerId))
                return new Result<>(false, Messages.YOU_CANT_SEE_PRODUCTS_OF_ANOTHER_BUYERS_BASKET);
        } catch (Exception e) {
            /* DO NOTHING! BECAUSE ADMIN OR DIRECTOR ENTERED TO SYSTEM. */
        }
        Optional<Buyer> optional = buyerRepo.findByIdAndIsActive(buyerId, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
        Optional<Basket> optionalBasket = basketRepo.findByBuyerIdAndOrderedIs(buyerId);
        if (optionalBasket.isEmpty())
            return new Result<>(false, Messages.THERE_IS_NO_ANY_PRODUCTS_THIS_BUYERS_BASKET);
        return new Result<>(true, optionalBasket.get());
    }

    @Override
    public Result<?> calculateDeliveryFeeToHome(String ip) throws IOException {
        ip = ip.trim();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("http://apiip.net/api/check?ip=" + ip + "&accessKey=" + ipAccessKey)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() >= 400) return new Result<>(false, Messages.CLIENT_INPUT_ERROR_OR_API_IP_SERVER_ERROR);
        if (response.body() == null) return new Result<>(false, Messages.API_IP_SERVER_ERROR);
        String jsonData = response.body().string();
        JSONObject jsonObject = new JSONObject(jsonData);
        double buyerLatitude = jsonObject.getDouble("latitude");
        double buyerLongitude = jsonObject.getDouble("longitude");
        List<Region> regions = regionRepo.findAllByActive(Boolean.TRUE);
        String allRegionLocations = "";
        for (Region region : regions) {
            allRegionLocations = allRegionLocations.concat("{\"location\":[" + region.getLongitude() + ", " + region.getLatitude() + "]}");
            if (regions.indexOf(region) != regions.size() - 1) allRegionLocations = allRegionLocations.concat(", ");
        }
        jsonData = "{" +
                "\"mode\":\"drive\"," +
                "\"sources\":[" +
                allRegionLocations +
                "]," +
                "\"targets\":[" +
                "{\"location\":[" + buyerLongitude + ", " + buyerLatitude + "]}" +
                "]" +
                "}";
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, jsonData);
        request = new Request.Builder()
                .url("https://api.geoapify.com/v1/routematrix?apiKey=" + geoapifyApiKey)
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
        response = client.newCall(request).execute();
        if (response.code() >= 400 || response.body() == null)
            return new Result<>(false, Messages.GEOAPIFY_SERVER_ERROR);
        Map<Region, Integer> map = new HashMap<>();
        jsonData = response.body().string();
        jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("sources_to_targets");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray subJsonArray = jsonArray.getJSONArray(i);
            JSONObject subJsonObject = subJsonArray.getJSONObject(0);
            int distance = subJsonObject.getInt("distance");
            map.put(regions.get(i), distance);
        }
        List<Integer> sortedRegionDistance = map.values().stream().sorted(Comparator.naturalOrder()).toList();
        Region nearestRegionToBuyer = map.entrySet().stream().filter(ent -> ent.getValue().equals(sortedRegionDistance.get(0))).findFirst().get().getKey();
        List<Branch> branches = branchRepo.getByRegionIdAndActive(nearestRegionToBuyer.getId(), true);
        String allBranchLocations = "";
        for (Branch branch : branches) {
            allBranchLocations = allBranchLocations.concat("{\"location\":[" + branch.getLongitude() + ", " + branch.getLatitude() + "]}");
            if (branches.indexOf(branch) != branches.size() - 1) allBranchLocations = allBranchLocations.concat(", ");
        }
        jsonData = "{" +
                "\"mode\":\"drive\"," +
                "\"sources\":[" +
                allBranchLocations +
                "]," +
                "\"targets\":[" +
                "{\"location\":[" + buyerLongitude + ", " + buyerLatitude + "]}" +
                "]" +
                "}";
        mediaType = MediaType.parse("application/json");
        requestBody = RequestBody.create(mediaType, jsonData);
        request = new Request.Builder()
                .url("https://api.geoapify.com/v1/routematrix?apiKey=" + geoapifyApiKey)
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
        response = client.newCall(request).execute();
        if (response.code() >= 400 || response.body() == null)
            return new Result<>(false, Messages.GEOAPIFY_SERVER_ERROR);
        Map<Branch, Integer> branchMap = new HashMap<>();
        jsonData = response.body().string();
        jsonObject = new JSONObject(jsonData);
        jsonArray = jsonObject.getJSONArray("sources_to_targets");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray subJsonArray = jsonArray.getJSONArray(i);
            JSONObject subJsonObject = subJsonArray.getJSONObject(0);
            int distance = subJsonObject.getInt("distance");
            branchMap.put(branches.get(i), distance);
        }
        List<Integer> sortedBranchDistance = branchMap.values().stream().sorted(Comparator.naturalOrder()).toList();
        Optional<Map.Entry<Branch, Integer>> optionalEntry = branchMap.entrySet().stream().filter(ent -> ent.getValue().equals(sortedBranchDistance.get(0))).findFirst();
        Map.Entry<Branch, Integer> entry = optionalEntry.get();
        Branch nearestBranchToBuyer = entry.getKey();
        Integer nearestDistanceFromBranchToBuyer = entry.getValue();
        int calculateFee = nearestDistanceFromBranchToBuyer / 1000 * 2000;
        if (calculateFee <= 30_000) return new Result<>(true, 30_000);
        else return new Result<>(true, calculateFee);
    }


    @Override
    public Result<?> getAmountOfBuyersByProductId(Integer productId) {
        Optional<Product> optional = productRepo.findByIdAndIsActive(productId, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        Integer amountOfBuyers = basketRepo.getAmountOfBuyers(productId);
        return new Result<>(true, amountOfBuyers);
    }

    @Override
    public Result<?> getAmountOfBaskets() {
        Integer amountOfBaskets = basketRepo.getAmountOfBaskets();
        return new Result<>(true, amountOfBaskets);
    }

    @Override
    public Result<?> edit(String basketId, String productId, String amount, String sessionId) {
        long basketIdInt = Integer.parseInt(basketId);
        int productIdInt = Integer.parseInt(productId);
        int amountInt = Integer.parseInt(amount);
        if (amountInt < 1) return new Result<>(false, Messages.AMOUNT_OF_PRODUCT_MUST_NOT_BE_ZERO_OR_MINUS);
        Optional<Basket> optionalBasket = basketRepo.findById(basketIdInt);
        if (optionalBasket.isEmpty()) return new Result<>(false, Messages.SUCH_BASKET_ID_NOT_EXIST);
        Basket basket = optionalBasket.get();
        Result<?> result = verifyUser(basket, sessionId);
        if (result != null) return result;
        Optional<Product> optionalProduct = productRepo.findByIdAndIsActive(productIdInt, true);
        if (optionalProduct.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        Product product = optionalProduct.get();
        if (amountInt > product.getAmount())
            return new Result<>(false, String.format(Messages.NOT_ENOUGH_TO_THIS_AMOUNT, product.getAmount()));
        if (basket.isOrdered()) return new Result<>(false, Messages.YOU_CANT_CHANGE_THIS_BASKET_ETC);
        Map<Product, Integer> map = basket.getProductAmount();
        boolean existsThisProductInBasket = map.keySet().stream().anyMatch(pro -> pro.getId().equals(productIdInt));
        if (!existsThisProductInBasket) return new Result<>(false, Messages.YOU_MUST_ADD_THIS_PRODUCT_BEFORE_ETC);
        map.replace(product, amountInt);
        basket.setProductAmount(map);
        basketRepo.save(basket);
        return new Result<>(true, Messages.BASKET_UPDATED);
    }

    @Override
    public Result<?> delete(String basketId, String productId, String sessionId) {
        long basketIdInt = Integer.parseInt(basketId);
        int productIdInt = Integer.parseInt(productId);
        Optional<Basket> optionalBasket = basketRepo.findById(basketIdInt);
        if (optionalBasket.isEmpty()) return new Result<>(false, Messages.SUCH_BASKET_ID_NOT_EXIST);
        Basket basket = optionalBasket.get();
        Result<?> result = verifyUser(basket, sessionId);
        if (result != null) return result;
        if (basket.isOrdered()) return new Result<>(false, Messages.YOU_CANT_DELETE_THIS_BASKET_ETC);
        Optional<Product> optionalProduct = productRepo.findByIdAndIsActive(productIdInt, true);
        if (optionalProduct.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        Product product = optionalProduct.get();
        Map<Product, Integer> map = basket.getProductAmount();
        boolean existsThisProductInBasket = map.keySet().stream().anyMatch(pro -> pro.getId().equals(productIdInt));
        if (!existsThisProductInBasket)
            return new Result<>(false, Messages.YOU_MUST_ADD_THIS_PRODUCT_BEFORE_DELETING_IT);
        map.remove(product);
        if (map.isEmpty()) {
            basketRepo.delete(basket);
        } else {
            basket.setProductAmount(map);
            basketRepo.save(basket);
        }
        return new Result<>(true, Messages.PRODUCT_DELETED_FROM_BASKET);
    }

    private Result<?> verifyUser(Basket basket, String sessionId) {
        if (basket.getBuyer() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getPrincipal().equals("anonymousUser") || !authentication.isAuthenticated())
                return new Result<>(false, Messages.YOU_CANT_SEE_BUYER_BASKET_PRODUCTS);
            try {
                Buyer buyer = (Buyer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (!buyer.equals(basket.getBuyer()))
                    return new Result<>(false, Messages.YOU_CANT_SEE_ANOTHER_BUYER_BASKET);
            } catch (Exception e) {
                Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication();
                if (!employee.getRole().equals(Role.DIRECTOR) && !employee.getRole().equals(Role.ADMIN))
                    return new Result<>(false, String.format(Messages.YOU_CANT_SEE_BUYER_BASKET_PRODUCTS_AS_EMPLOYEES, employee.getFirstname(), employee.getLastname()));
            }
        } else {
            if (!sessionId.equals(basket.getSessionId()))
                return new Result<>(false, Messages.THIS_BASKET_DOES_NOT_BELONG_YOU);
        }
        return null;
    }

}
