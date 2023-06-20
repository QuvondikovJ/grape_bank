package com.example.uzum.serviceImpl;

import com.example.uzum.dto.order.OrderDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.dto.order.OrderDTOWithAmount;
import com.example.uzum.dto.order.OrderStat;
import com.example.uzum.entity.*;
import com.example.uzum.entity.enums.NotificationType;
import com.example.uzum.entity.enums.OrderStatus;
import com.example.uzum.entity.enums.PaymentType;
import com.example.uzum.helper.Filter;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.*;
import com.example.uzum.security.EncryptAndDecrypt;
import com.example.uzum.service.OrderService;
import com.example.uzum.service.TwilioSmsSender;
import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.uzum.helper.StringUtils.*;

@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private BranchRepo branchRepo;
    @Autowired
    private BasketRepo basketRepo;
    @Autowired
    private RegionRepo regionRepo;
    @Autowired
    private BuyerRepo buyerRepo;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private NotificationRepo notificationRepo;
    @Autowired
    private TwilioSmsSender twilioSmsSender;
    @Autowired
    private SoldProductsRepo soldProductsRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private SellerRepo sellerRepo;
    @Autowired
    private EncryptAndDecrypt encryptAndDecrypt;
    @Autowired
    private EmployeeRepo employeeRepo;


    @Value("${geoapify.api.key}")
    private String apiKey;


    @Override
    public Result<?> add(OrderDTO dto) throws IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        String buyerCardNumber = "";
        String buyerCardExpireDate = "";
        Orders orders = new Orders();
        Optional<Buyer> optionalBuyer = buyerRepo.findByIdAndIsActive(dto.getBuyerId(), true);
        if (optionalBuyer.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
        Buyer buyer = optionalBuyer.get();
        Optional<Basket> optionalBasket = basketRepo.findByBuyerIdAndOrderedIs(buyer.getId());
        if (optionalBasket.isEmpty())
            return new Result<>(false, Messages.THIS_BUYER_HAS_NOT_ANY_PRODUCTS_IN_HIS_BASKET);
        Basket basket = optionalBasket.get();
        int moneyOfProduct = 0;
        Iterator<Map.Entry<Product, Integer>> productsWithAmount = basket.getProductAmount().entrySet().iterator();
        while (productsWithAmount.hasNext()) {
            Map.Entry<Product, Integer> variable = productsWithAmount.next();
            moneyOfProduct += variable.getKey().getDiscountedPrice() * variable.getValue();
        }
        if (dto.getPaymentType().toUpperCase().equals(PaymentType.CARD.name())) {
            if (dto.getCreditCardNumber() == null || dto.getCreditCardExpireDate() == null) {
                if (buyer.getCardNumber() == null) return new Result<>(false, Messages.YOU_CANT_PAY_USING_CARD_ETC);
                buyerCardNumber = encryptAndDecrypt.decryptCardDetail(buyer.getCardNumber());
                buyerCardExpireDate = encryptAndDecrypt.decryptCardDetail(buyer.getCardExpireDate());
            } else {
                buyerCardNumber = dto.getCreditCardNumber();
                buyerCardExpireDate = dto.getCreditCardExpireDate();
            }
            Result<?> result = validateCardDetails(buyerCardNumber, buyerCardExpireDate);
            if (!result.getSuccess()) return result;
            int buyerCardBalance = sendDataToPaypalToCheckCardBalance(buyerCardNumber, buyerCardExpireDate);
            if (buyerCardBalance < moneyOfProduct)
                return new Result<>(false, Messages.BALANCE_IS_NOT_ENOUGH_IN_YOUR_CARD);
        }
        if (dto.isToHome()) {
            Result<?> result = setSomeDataToOrder(dto, orders);
            if (!result.getSuccess()) return result;
            orders = (Orders) result.getData();
        } else {
            Optional<Branch> optionalBranch = branchRepo.findByIdAndActive(dto.getBranchId(), true);
            if (optionalBranch.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
            Branch branch = optionalBranch.get();
            orders.setBranch(branch);
        }
        orders.setBuyer(buyer);
        orders.setBasket(basket);
        orders.setToHome(dto.isToHome());
        orders.setPaymentType(PaymentType.valueOf(dto.getPaymentType().toUpperCase()));
        orders.setStatus(OrderStatus.PREPARING);
        orders.setMoneyOfProducts(moneyOfProduct);
        if (dto.getPaymentType().toUpperCase().equals(PaymentType.CARD.name())) {
            Optional<Employee> optionalDirector = employeeRepo.findByIdAndIsActive(1, true);
            Employee director = optionalDirector.get();
            String directorCardNumber = encryptAndDecrypt.decryptCardDetail(director.getCardNumber());
            String directorCardExpireDate = encryptAndDecrypt.decryptCardDetail(director.getCardExpireDate());
            sendDataToPaypalToWithdrawMoney(buyerCardNumber, buyerCardExpireDate, directorCardNumber, directorCardExpireDate, moneyOfProduct);
            orders.setLastSixCharOFBuyerCard("**** **** **".concat(buyerCardNumber.substring(10, 12)).concat(" ").concat(buyerCardNumber.substring(12)));
            orders.setPaid(true);
        } else orders.setPaid(false);
        orderRepo.save(orders);
        return new Result<>(true, Messages.ORDER_ADDED);

    }

    private Result<?> setSomeDataToOrder(OrderDTO dto, Orders orders) throws IOException {
        if (dto.getHomeLatitude() == null || dto.getHomeLongitude() == null)
            return new Result<>(false, Messages.HOME_LATITUDE_AND_LONGITUDE_MUST);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.geoapify.com/v1/geocode/reverse?lat=" + dto.getHomeLatitude() + "&lon=" + dto.getHomeLongitude() + "&apiKey=" + apiKey)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        if (response.body() == null) return new Result<>(false, Messages.REVERSE_GEOCODING_API_RESPONSE_ERROR);
        String jsonData = response.body().string();
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = (JSONArray) jsonObject.get("features");
        JSONObject subJsonObject = jsonArray.getJSONObject(0);
        JSONObject properties = subJsonObject.getJSONObject("properties");
        String countryCode = properties.getString("country_code");
        String buyerStreet = properties.getString("street");
        String buyerRegion = properties.getString("state");
        String buyerDistrict = "";
        if (properties.keySet().contains("county")) {
            String county = properties.getString("county");
            buyerDistrict = county;
        } else if (properties.keySet().contains("city")) {
            String city = properties.getString("city");
            buyerDistrict = city;
        }
        if (!countryCode.equals("uz")) return new Result<>(false, Messages.GRAPE_BANK_SERVICES_ONLY_IN_UZBEKISTAN);
        List<Region> regions = regionRepo.findAllByActive(true);
        String regionLocations = "";
        for (Region region : regions) {
            String regionLocation = "{\"location\":[" + region.getLongitude() + ", " + region.getLatitude() + "]}";
            if (!regions.get(regions.size() - 1).equals(region)) regionLocation = regionLocation.concat(",");
            regionLocations = regionLocations.concat(regionLocation);
        }
        String jsonString = "{" +
                "    \"mode\" : \"drive\"," +
                "    \"sources\" : [" + regionLocations + "]," +
                "    \"targets\" : [" +
                "        {" +
                "            \"location\" : [" + dto.getHomeLongitude() + ", " + dto.getHomeLatitude() + "]" +
                "        }" +
                "    ]" +
                "}";
        List<Integer> list = getDistances(jsonString);
        int nearestDistance = list.get(0);
        int nearestRegionIndex = list.get(1);
        if (nearestRegionIndex == -1) return new Result<>(false, Messages.ROUTE_MATRIX_API_RESPONSE_ERROR);
        Region region = regions.get(nearestRegionIndex);
        List<Branch> branches = branchRepo.getByRegionIdAndActive(region.getId(), true);
        String branchLocations = "";
        for (Branch branch : branches) {
            String branchLocation = "{\"location\":[" + branch.getLongitude() + ", " + branch.getLatitude() + "]}";
            if (!branches.get(branches.size() - 1).equals(branch)) branchLocation = branchLocation.concat(",");
            branchLocations = branchLocations.concat(branchLocation);
        }
        jsonString = "{" +
                "    \"mode\" : \"drive\"," +
                "    \"sources\" : [" + branchLocations + "]," +
                "    \"targets\" : [" +
                "        {" +
                "            \"location\" : [" + dto.getHomeLongitude() + ", " + dto.getHomeLatitude() + "]" +
                "        }" +
                "    ]" +
                "}";
        list = getDistances(jsonString);
        nearestDistance = list.get(0);
        int nearestBranchIndex = list.get(1);
        if (nearestBranchIndex == -1) return new Result<>(false, Messages.ROUTE_MATRIX_API_RESPONSE_ERROR);
        Branch branch = branches.get(nearestBranchIndex);
        orders.setBranch(branch);
        orders.setHomeLatitude(dto.getHomeLatitude());
        orders.setHomeLongitude(dto.getHomeLongitude());
        orders.setRegion(buyerRegion);
        orders.setDistrict(buyerDistrict);
        orders.setStreet(buyerStreet);
        orders.setPriceOfDelivery((int) Math.ceil((double) nearestDistance / 1000) * 2000);
        return new Result<>(true, orders);
    }


    private List<Integer> getDistances(String jsonString) throws IOException {
        int nearestBranchOrRegionIndex = 0;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonString);
        Request request = new Request.Builder()
                .url("https://api.geoapify.com/v1/routematrix?apiKey=" + apiKey)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200 && response.body() != null) {
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = (JSONArray) jsonObject.get("sources_to_targets");
            List<Integer> distances = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray subJsonArray = (JSONArray) jsonArray.get(i);
                JSONObject subJsonObject = (JSONObject) subJsonArray.get(0);
                Integer distance = (Integer) subJsonObject.get("distance");
                distances.add(distance);
            }
            distances.sort(Comparator.naturalOrder());
            Integer nearestDistance = distances.get(0);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray subJsonArray = (JSONArray) jsonArray.get(i);
                JSONObject subJsonObject = (JSONObject) subJsonArray.get(0);
                Integer distance = (Integer) subJsonObject.get("distance");
                if (nearestDistance.equals(distance)) nearestBranchOrRegionIndex = i;
            }
            return List.of(nearestDistance, nearestBranchOrRegionIndex);
        }
        return List.of(-1, -1);  // Index will never be minus value.
    }

    @Override
    public Result<?> searchLocation(String locationName) throws IOException {
        locationName = locationName.trim();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.geoapify.com/v1/geocode/autocomplete?text=" + locationName + "&apiKey=" + apiKey)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() < 400) {
            if (response.body() == null) return new Result<>(false, Messages.SUCH_PLACE_NOT_FOUND_ETC);
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray features = jsonObject.getJSONArray("features");
            List<String> locations = new ArrayList<>();
            for (int i = 0; i < features.length(); i++) {
                JSONObject featuresJSONObject = features.getJSONObject(i);
                JSONObject propertiesJsonObject = featuresJSONObject.getJSONObject("properties");
                String formatted = propertiesJsonObject.getString("formatted");
                locations.add(formatted);
            }
            return new Result<>(true, locations);
        } else return new Result<>(false, Messages.CLIENT_ERROR);
    }


    @Override
    public Result<?> getPreparingByBranchId(Integer branchId, List<String> date, String page) {  // = new created orders in the date
        int pageInt = Integer.parseInt(page);
        LocalDateTime from = null;
        LocalDateTime to = null;
        List<LocalDateTime> times = getFromAndToInterval(date);
        if (times != null) {
            from = times.get(0);
            to = times.get(1);
        }
        Optional<Branch> optionalBranch = branchRepo.findByIdAndActive(branchId, true);
        if (optionalBranch.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Orders> orders;
        Integer getAmountOfOrders = null;
        if (from == null || to == null) {
            orders = orderRepo.getByPreparing(branchId, pageable);
            if (pageInt == 0) getAmountOfOrders = orderRepo.getAmountOfOrdersByPreparing(branchId);
        } else {
            orders = orderRepo.getByCreatedDate(branchId, Timestamp.valueOf(from), Timestamp.valueOf(to), pageable);
            if (pageInt == 0)
                getAmountOfOrders = orderRepo.getAmountOfOrdersByCreatedDate(branchId, Timestamp.valueOf(from), Timestamp.valueOf(to));
        }
        OrderDTOWithAmount dto = OrderDTOWithAmount.builder()
                .orders(orders)
                .amountOfOrders(getAmountOfOrders)
                .build();
        return new Result<>(true, dto);
    }

    @Override
    public Result<?> getDeliveringByBranchId(Integer branchId, List<String> date, String page) {
        int pageInt = Integer.parseInt(page);
        LocalDateTime from = null;
        LocalDateTime to = null;
        List<LocalDateTime> times;
        times = getFromAndToInterval(date);
        if (times != null) {
            from = times.get(0);
            to = times.get(1);
        }
        Optional<Branch> optionalBranch = branchRepo.findByIdAndActive(branchId, true);
        if (optionalBranch.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Orders> orders;
        Integer getAmountOfOrders = null;
        if (from == null || to == null) {
            orders = orderRepo.getByDelivering(branchId, pageable);
            if (pageInt == 0) getAmountOfOrders = orderRepo.getAmountOfOrdersByDelivering(branchId);
        } else {
            orders = orderRepo.getByDeliveredDate(branchId, Timestamp.valueOf(from), Timestamp.valueOf(to), pageable);
            if (pageInt == 0)
                getAmountOfOrders = orderRepo.getAmountOfOrdersByDeliveredDate(branchId, Timestamp.valueOf(from), Timestamp.valueOf(to));
        }
        OrderDTOWithAmount dto = OrderDTOWithAmount.builder()
                .orders(orders)
                .amountOfOrders(getAmountOfOrders)
                .build();
        return new Result<>(true, dto);
    }

    @Override
    public Result<?> getReturnedByBranchId(Integer branchId, List<String> date, String page) {
        int pageInt = Integer.parseInt(page);
        List<LocalDateTime> times = getFromAndToInterval(date);
        LocalDateTime from = times.get(0);
        LocalDateTime to = times.get(1);
        Optional<Branch> optionalBranch = branchRepo.findByIdAndActive(branchId, true);
        if (optionalBranch.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "timeOfReturning"));
        Page<Orders> orders = orderRepo.getByReturned(branchId, Timestamp.valueOf(from), Timestamp.valueOf(to), pageable);
        Integer getAmountOfOrdersByReturned = orderRepo.getAmountOfOrdersByReturnedDate(branchId, Timestamp.valueOf(from), Timestamp.valueOf(to));
        OrderDTOWithAmount dto = OrderDTOWithAmount.builder()
                .orders(orders)
                .amountOfOrders(getAmountOfOrdersByReturned)
                .build();
        return new Result<>(true, dto);
    }

    @Override
    public Result<?> getWaitingClientOrdersByBranchId(Integer branchId, String page) {
        int pageInt = Integer.parseInt(page);
        Optional<Branch> optionalBranch = branchRepo.findByIdAndActive(branchId, true);
        if (optionalBranch.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.Direction.DESC);
        Page<Orders> orders = orderRepo.getWaitingClientOrdersByBranchId(branchId, pageable);
        Integer getAmountOfWaitingClientOrders = orderRepo.getAmountOfWaitingClientOrders(branchId);
        OrderDTOWithAmount dto = OrderDTOWithAmount.builder()
                .orders(orders)
                .amountOfOrders(getAmountOfWaitingClientOrders)
                .build();
        return new Result<>(true, dto);
    }

    @Override
    public Result<?> getById(Long id) {
        Optional<Orders> optional = orderRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_ORDER_ID_NOT_EXIST);
        Orders orders = optional.get();
        return new Result<>(true, orders);
    }

    @Override
    public Result<?> getByFirstNameAndLastNameAndPhoneNumber(String name, String phone, String page) {
        String query = "SELECT ord FROM Orders AS ord WHERE ord.isPaid=TRUE";
        int pageInt = Integer.parseInt(page);
        if (name == null && phone == null) return new Result<>(false, Messages.NAME_OR_PHONE_NUMBER_REQUIRED);
        if (name != null) {
            name = name.trim();
            String[] names = name.split(" ");
            if (!name.equals("") && names.length > 1) {
                String firstName = names[0];
                String lastName = names[1];
                query = query.concat(" (ord.buyer.firstname LIKE " + firstName + "% OR ord.buyer.firstname=" + firstName + ") AND (ord.buyer.lastname LIKE " + lastName + "% OR ord.buyer.lastname=" + lastName + ") ");
            } else {
                query = query.concat(" (ord.buyer.firstname LIKE " + name + "% OR ord.buyer.firstname=" + name + ") ");  //here name equals firstname.
            }
        }
        if (phone != null) {
            if (name != null) query = query.concat(" AND ");
            query = query.concat(" (ord.buyer.phoneNumber LIKE " + phone + "% OR ord.buyer.phoneNumber=" + phone + ") ");
        }
        query = query.concat(" ORDER BY ord.createdAt DESC LIMIT 20 OFFSET " + (pageInt * 20));
        Query createQuery = entityManager.createQuery(query);
        List<Orders> orders = createQuery.getResultList();
        if (orders.isEmpty()) return new Result<>(false, String.format(Messages.IN_PAGE_NOT_ANY_ORDERS, pageInt));
        return new Result<>(true, orders);
    }

    @Override
    public Result<?> getByBuyerId(Integer userId, String page) {
        int pageInt = Integer.parseInt(page);
        Optional<Buyer> optionalBuyer = buyerRepo.findByIdAndIsActive(userId, true);
        if (optionalBuyer.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.Direction.DESC);
        Page<Orders> orders = orderRepo.getByBuyerIdOrderByCreatedAt(userId, pageable);
        if (orders.isEmpty()) return new Result<>(false, String.format(Messages.IN_PAGE_NOT_ANY_ORDERS, pageInt));
        return new Result<>(true, orders);
    }


    // date likes : allTime, latestWeek, byDayMonthYear, byMonthYear, byYear
    @Override
    public Result<?> getStatByBranchId(Integer branchId, List<String> date) {
        LocalDateTime from;
        LocalDateTime to;
        List<LocalDateTime> times = getFromAndToInterval(date);
        if (times != null) {
            from = times.get(0);
            to = times.get(1);
        } else return new Result<>(false, Messages.HEY_UI_DEVELOPER_SEND_ME_ETC);
        Optional<Branch> optionalBranch = branchRepo.findByIdAndActive(branchId, true);
        if (optionalBranch.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
        Branch branch = optionalBranch.get();
        Integer getAmountByCreated = orderRepo.getAmountOfOrdersByCreatedDate(branchId, Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer getAmountByDelivered = orderRepo.getAmountOfOrdersByDeliveredDate(branchId, Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer getAmountBySold = orderRepo.getAmountOfOrdersBySoldDate(branchId, Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer getAmountByReturned = orderRepo.getAmountOfOrdersByReturnedDate(branchId, Timestamp.valueOf(from), Timestamp.valueOf(to));
        OrderStat stat = OrderStat.builder()
                .branch(branch)
                .created(getAmountByCreated)
                .delivered(getAmountByDelivered)
                .sold(getAmountBySold)
                .returned(getAmountByReturned)
                .build();
        return new Result<>(true, stat);
    }

    @Override
    public Result<?> getStatByRegionId(Integer regionId, List<String> date) {
        LocalDateTime from;
        LocalDateTime to;
        List<LocalDateTime> times = getFromAndToInterval(date);
        if (times != null) {
            from = times.get(0);
            to = times.get(1);
        } else return new Result<>(false, Messages.HEY_UI_DEVELOPER_SEND_ME_ETC);
        Optional<Region> optionalRegion = regionRepo.findByIdAndActive(regionId, true);
        if (optionalRegion.isEmpty()) return new Result<>(false, Messages.SUCH_REGION_ID_NOT_EXIST);
        Region region = optionalRegion.get();
        Integer getAmountByCreated = orderRepo.getAmountOfOrdersByCreatedDateAndRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer getAmountByDelivered = orderRepo.getAmountOfOrdersByDeliveredDateAndRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer getAmountBySold = orderRepo.getAmountOfOrdersBySoldDateAndRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer getAmountByReturned = orderRepo.getAmountOfOrdersByReturnedDateAndRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
        OrderStat stat = OrderStat.builder()
                .region(region)
                .created(getAmountByCreated)
                .delivered(getAmountByDelivered)
                .sold(getAmountBySold)
                .returned(getAmountByReturned)
                .build();
        return new Result<>(true, stat);
    }

    @Override
    public Result<?> getAllRegionStat(List<String> date, String order) {
        LocalDateTime from;
        LocalDateTime to;
        List<LocalDateTime> times = getFromAndToInterval(date);
        if (times != null) {
            from = times.get(0);
            to = times.get(1);
        } else return new Result<>(false, Messages.HEY_UI_DEVELOPER_SEND_ME_ETC);
        List<Region> regions = regionRepo.findAllByActive(true);
        List<OrderStat> dto = new ArrayList<>();
        List<OrderStat> sortedDto = new ArrayList<>();
        OrderStat stat;
        for (Region region : regions) {
            Integer getAmountByCreated = orderRepo.getAmountOfOrdersByCreatedDateAndRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
            Integer getAmountByDelivered = orderRepo.getAmountOfOrdersByDeliveredDateAndRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
            Integer getAmountBySold = orderRepo.getAmountOfOrdersBySoldDateAndRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
            Integer getAmountByReturned = orderRepo.getAmountOfOrdersByReturnedDateAndRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
            stat = OrderStat.builder()
                    .region(region)
                    .created(getAmountByCreated)
                    .delivered(getAmountByDelivered)
                    .sold(getAmountBySold)
                    .returned(getAmountByReturned)
                    .build();
            dto.add(stat);
        }
        switch (order) {
            case Filter.BY_CREATED -> {
                sortedDto = dto.stream().sorted((ord1, ord2) -> ord2.getCreated() - ord1.getCreated()).collect(Collectors.toList());
            }
            case Filter.BY_DELIVERED -> {
                sortedDto = dto.stream().sorted((ord1, ord2) -> ord2.getDelivered() - ord1.getDelivered()).collect(Collectors.toList());
            }
            case Filter.BY_SOLD -> {
                sortedDto = dto.stream().sorted((ord1, ord2) -> ord2.getSold() - ord1.getSold()).collect(Collectors.toList());
            }
            case Filter.BY_RETURNED -> {
                sortedDto = dto.stream().sorted((ord1, ord2) -> ord2.getReturned() - ord1.getReturned()).collect(Collectors.toList());
            }
        }
        return new Result<>(true, sortedDto);
    }

    @Override
    public Result<?> getAllBranchStatByRegionId(Integer regionId, List<String> date, String order) {
        LocalDateTime from;
        LocalDateTime to;
        List<LocalDateTime> times = getFromAndToInterval(date);
        if (times != null) {
            from = times.get(0);
            to = times.get(1);
        } else return new Result<>(false, Messages.HEY_UI_DEVELOPER_SEND_ME_ETC);
        Optional<Region> optionalRegion = regionRepo.findByIdAndActive(regionId, true);
        if (optionalRegion.isEmpty()) return new Result<>(false, Messages.SUCH_REGION_ID_NOT_EXIST);
        List<Branch> branches = branchRepo.getByRegionIdAndActive(regionId, true);
        List<OrderStat> dto = new ArrayList<>();
        List<OrderStat> sortedDto = new ArrayList<>();
        OrderStat stat;
        for (Branch branch : branches) {
            Integer getAmountByCreatedDate = orderRepo.getAmountOfOrdersByCreatedDate(branch.getId(), Timestamp.valueOf(from), Timestamp.valueOf(to));
            Integer getAmountByDeliveredDate = orderRepo.getAmountOfOrdersByDeliveredDate(branch.getId(), Timestamp.valueOf(from), Timestamp.valueOf(to));
            Integer getAmountBySoldDate = orderRepo.getAmountOfOrdersBySoldDate(branch.getId(), Timestamp.valueOf(from), Timestamp.valueOf(to));
            Integer getAmountByReturnedDate = orderRepo.getAmountOfOrdersByReturnedDate(branch.getId(), Timestamp.valueOf(from), Timestamp.valueOf(to));
            stat = OrderStat.builder()
                    .branch(branch)
                    .created(getAmountByCreatedDate)
                    .delivered(getAmountByDeliveredDate)
                    .sold(getAmountBySoldDate)
                    .returned(getAmountByReturnedDate)
                    .build();
            dto.add(stat);
        }
        switch (order) {
            case Filter.BY_CREATED -> {
                sortedDto = dto.stream().sorted((ord1, ord2) -> ord2.getCreated() - ord1.getCreated()).collect(Collectors.toList());
            }
            case Filter.BY_DELIVERED -> {
                sortedDto = dto.stream().sorted((ord1, ord2) -> ord2.getDelivered() - ord1.getDelivered()).collect(Collectors.toList());
            }
            case Filter.BY_SOLD -> {
                sortedDto = dto.stream().sorted((ord1, ord2) -> ord2.getSold() - ord1.getSold()).collect(Collectors.toList());
            }
            case Filter.BY_RETURNED -> {
                sortedDto = dto.stream().sorted((ord1, ord2) -> ord2.getReturned() - ord1.getReturned()).collect(Collectors.toList());
            }
        }
        return new Result<>(true, sortedDto);
    }

    @Override
    public Result<?> getToHomeByBranchId(Integer branchId, String page) {
        int pageInt = Integer.parseInt(page);
        Optional<Branch> optionalBranch = branchRepo.findByIdAndActive(branchId, true);
        if (optionalBranch.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.Direction.DESC);
        Page<Orders> orders = orderRepo.getByToHome(branchId, pageable);
        if (orders.isEmpty()) return new Result<>(false, String.format(Messages.IN_PAGE_NOT_ANY_ORDERS, pageInt));
        return new Result<>(true, orders);
    }

    public void changeStatusAndNotifyBuyersAndAdmin() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        List<Orders> orders = orderRepo.getByDelayingToPrepareForWarning(Timestamp.valueOf(oneDayAgo), Timestamp.valueOf(twoDaysAgo));
        List<Notification> notifications;
        if (orders.size() > 0) {
            List<Long> orderIds = new ArrayList<>();
            List<Long> notifiedObjectIds = new ArrayList<>();
            orders.forEach(ord -> orderIds.add(ord.getId()));
            notifications = notificationRepo.getByNotificationTypeAndEntityNameAndCauseOfNotificationAndNotifiedObjectIDInAndCreatedAtGreaterThan(NotificationType.WARNING, "ORDER", Messages.DELAYING_TO_PREPARE, orderIds, Timestamp.valueOf(twoDaysAgo));
            notifications.forEach(not -> notifiedObjectIds.add(not.getNotifiedObjectID()));
            saveNotification(orders, notifiedObjectIds, Messages.NOTIFICATION_ORDER_PREPARING_WARNING, NotificationType.WARNING, Messages.DELAYING_TO_PREPARE);
        }
        orders = orderRepo.getByDelayingToPrepareForFire(Timestamp.valueOf(twoDaysAgo), Timestamp.valueOf(threeDaysAgo));
        if (orders.size() > 0) {
            List<Long> orderIds = new ArrayList<>();
            List<Long> notifiedObjectIds = new ArrayList<>();
            orders.forEach(ord -> orderIds.add(ord.getId()));
            notifications = notificationRepo.getByNotificationTypeAndEntityNameAndCauseOfNotificationAndNotifiedObjectIDInAndCreatedAtGreaterThan(NotificationType.FIRE, "ORDER", Messages.DELAYING_TO_PREPARE, orderIds, Timestamp.valueOf(threeDaysAgo));
            notifications.forEach(not -> notifiedObjectIds.add(not.getNotifiedObjectID()));
            saveNotification(orders, notifiedObjectIds, Messages.NOTIFICATION_ORDER_PREPARING_FIRE, NotificationType.FIRE, Messages.DELAYING_TO_PREPARE);
        }
        orders = orderRepo.getByDelayingToPrepareForExtremelyFire(Timestamp.valueOf(threeDaysAgo));
        if (orders.size() > 0) {
            List<Long> orderIds = new ArrayList<>();
            List<Long> notifiedObjectIds = new ArrayList<>();
            orders.forEach(ord -> orderIds.add(ord.getId()));
            notifications = notificationRepo.getByNotificationTypeAndEntityNameAndCauseOfNotificationAndNotifiedObjectIDInAndCreatedAtLessThan(NotificationType.EXTREMELY_FIRE, "ORDER", Messages.DELAYING_TO_PREPARE, orderIds, Timestamp.valueOf(threeDaysAgo));
            notifications.forEach(not -> notifiedObjectIds.add(not.getNotifiedObjectID()));
            saveNotification(orders, notifiedObjectIds, Messages.NOTIFICATION_ORDER_PREPARING_EXTREMELY_FIRE, NotificationType.EXTREMELY_FIRE, Messages.DELAYING_TO_PREPARE);
        }

        orders = orderRepo.getByDelayingToDeliveryForWarning(Timestamp.valueOf(oneDayAgo), Timestamp.valueOf(twoDaysAgo));
        if (orders.size() > 0) {
            List<Long> orderIds = new ArrayList<>();
            List<Long> notifiedObjectIds = new ArrayList<>();
            orders.forEach(ord -> orderIds.add(ord.getId()));
            notifications = notificationRepo.getByNotificationTypeAndEntityNameAndCauseOfNotificationAndNotifiedObjectIDInAndCreatedAtGreaterThan(NotificationType.WARNING, "ORDER", Messages.DELAYING_TO_DELIVER, orderIds, Timestamp.valueOf(twoDaysAgo));
            notifications.forEach(not -> notifiedObjectIds.add(not.getNotifiedObjectID()));
            saveNotification(orders, notifiedObjectIds, Messages.NOTIFICATION_ORDER_DELIVERING_WARNING, NotificationType.WARNING, Messages.DELAYING_TO_DELIVER);
        }
        orders = orderRepo.getByStatusAndTimeOfGivingToDeliverGreaterThanAndTimeOfGivingToDeliverLessThan(OrderStatus.DELIVERING, Timestamp.valueOf(threeDaysAgo), Timestamp.valueOf(twoDaysAgo));
        if (orders.size() > 0) {
            List<Long> orderIds = new ArrayList<>();
            List<Long> notifiedObjectIds = new ArrayList<>();
            orders.forEach(ord -> orderIds.add(ord.getId()));
            notifications = notificationRepo.getByNotificationTypeAndEntityNameAndCauseOfNotificationAndNotifiedObjectIDInAndCreatedAtGreaterThan(NotificationType.FIRE, "ORDER", Messages.DELAYING_TO_DELIVER, orderIds, Timestamp.valueOf(threeDaysAgo));
            notifications.forEach(not -> notifiedObjectIds.add(not.getNotifiedObjectID()));
            saveNotification(orders, notifiedObjectIds, Messages.NOTIFICATION_ORDER_DELIVERING_FIRE, NotificationType.FIRE, Messages.DELAYING_TO_DELIVER);
        }
        orders = orderRepo.getByStatusAndTimeOfGivingToDeliverLessThan(OrderStatus.DELIVERING, Timestamp.valueOf(threeDaysAgo));
        if (orders.size() > 0) {
            List<Long> orderIds = new ArrayList<>();
            List<Long> notifiedObjectIds = new ArrayList<>();
            orders.forEach(ord -> orderIds.add(ord.getId()));
            notifications = notificationRepo.getByNotificationTypeAndEntityNameAndCauseOfNotificationAndNotifiedObjectIDInAndCreatedAtLessThan(NotificationType.EXTREMELY_FIRE, "ORDER", Messages.DELAYING_TO_DELIVER, orderIds, Timestamp.valueOf(threeDaysAgo));
            notifications.forEach(not -> notifiedObjectIds.add(not.getNotifiedObjectID()));
            saveNotification(orders, notifiedObjectIds, Messages.NOTIFICATION_ORDER_DELIVERING_EXTREMELY_FIRE, NotificationType.EXTREMELY_FIRE, Messages.DELAYING_TO_DELIVER);
        }
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);
        orders = orderRepo.getByStatusAndTimeOfWaitingClientLessThan(OrderStatus.WAITING_CLIENT, Timestamp.valueOf(fiveDaysAgo));
        for (Orders order : orders) {
            String message;
            if (order.isPaid()) { // Bank kartasidan order egasiga pulini o'tkazib ber.
                message = String.format(Messages.NOTIFICATION_ORDER_RETURN_WITH_CARD, order.getBuyer().getLastname(), order.getBuyer().getFirstname(), order.getId());
                String lastSixCharOfBuyerCard = order.getLastSixCharOFBuyerCard();
                order.setPaid(false);
                order.setLastSixCharOFBuyerCard(null);
                orderRepo.save(order);
            } else
                message = String.format(Messages.NOTIFICATION_ORDER_RETURN, order.getBuyer().getLastname(), order.getBuyer().getFirstname(), order.getId());
            twilioSmsSender.sendSms(order.getBuyer().getPhoneNumber(), message);
            if (order.getBuyer().getEmail() != null) sendEmail(order.getBuyer().getEmail(), message);
        }
        orderRepo.changeOrderStatusToReturn(fiveDaysAgo);
        if (LocalDateTime.now().getHour() == 12 && LocalDateTime.now().getMinute() < 1) { // from 12 pm starts sending messages to buyers.
            LocalDateTime fourDaysAgo = LocalDateTime.now().minusDays(4);
            orders = orderRepo.getByStatusAndTimeOfWaitingClientGreaterThanAndTimeOfWaitingClientLessThan(OrderStatus.WAITING_CLIENT, Timestamp.valueOf(fiveDaysAgo), Timestamp.valueOf(fourDaysAgo));
            for (Orders order : orders) {
                LocalDateTime createdAt = order.getCreatedAt().toLocalDateTime();
                String expireDate = createdAt.getHour() < 12 ? "Tomorrow at ".concat(createdAt.getHour() + ":" + createdAt.getMinute() + " AM") : "Today at ".concat(createdAt.getHour() + ":" + createdAt.getMinute() + " PM");
                String message = String.format(Messages.NOTIFICATION_ORDER_WAITING_CLIENT_FIFTH_DAY, order.getBuyer().getLastname(), order.getBuyer().getFirstname(), order.getId(), order.getBranch(), expireDate);
                twilioSmsSender.sendSms(order.getBuyer().getPhoneNumber(), message);
                if (order.getBuyer().getEmail() != null)
                    sendEmail(order.getBuyer().getEmail(), message);
            }
            orders = orderRepo.getByStatusAndTimeOfWaitingClientGreaterThanAndTimeOfWaitingClientLessThan(OrderStatus.WAITING_CLIENT, Timestamp.valueOf(fourDaysAgo), Timestamp.valueOf(threeDaysAgo));
            for (Orders order : orders) {
                String message = String.format(Messages.NOTIFICATION_ORDER_WAITING_CLIENT_FOURTH_DAY, order.getBuyer().getLastname(), order.getBuyer().getFirstname(), order.getId(), order.getBranch());
                twilioSmsSender.sendSms(order.getBuyer().getPhoneNumber(), message);
                if (order.getBuyer().getEmail() != null)
                    sendEmail(order.getBuyer().getEmail(), message);
            }
            orders = orderRepo.getByStatusAndTimeOfWaitingClientGreaterThanAndTimeOfWaitingClientLessThan(OrderStatus.WAITING_CLIENT, Timestamp.valueOf(threeDaysAgo), Timestamp.valueOf(twoDaysAgo));
            for (Orders order : orders) {
                String message = String.format(Messages.NOTIFICATION_ORDER_WAITING_CLIENT_THIRD_DAY, order.getBuyer().getLastname(), order.getBuyer().getFirstname(), order.getId(), order.getBranch());
                twilioSmsSender.sendSms(order.getBuyer().getPhoneNumber(), message);
                if (order.getBuyer().getEmail() != null)
                    sendEmail(order.getBuyer().getEmail(), message);
            }
            orders = orderRepo.getByStatusAndTimeOfWaitingClientGreaterThanAndTimeOfWaitingClientLessThan(OrderStatus.WAITING_CLIENT, Timestamp.valueOf(twoDaysAgo), Timestamp.valueOf(oneDayAgo));
            for (Orders order : orders) {
                String message = String.format(Messages.NOTIFICATION_ORDER_WAITING_CLIENT_SECOND_DAY, order.getBuyer().getLastname(), order.getBuyer().getFirstname(), order.getId(), order.getBranch());
                twilioSmsSender.sendSms(order.getBuyer().getPhoneNumber(), message);
                if (order.getBuyer().getEmail() != null)
                    sendEmail(order.getBuyer().getEmail(), message);
            }
            orders = orderRepo.getByStatusAndTimeOfWaitingClientGreaterThanAndTimeOfWaitingClientLessThan(OrderStatus.WAITING_CLIENT, Timestamp.valueOf(oneDayAgo), Timestamp.valueOf(LocalDateTime.now()));
            for (Orders order : orders) {
                String message = String.format(Messages.NOTIFICATION_ORDER_WAITING_CLIENT_FIRST_DAY, order.getBuyer().getLastname(), order.getBuyer().getFirstname(), order.getId(), order.getBranch());
                twilioSmsSender.sendSms(order.getBuyer().getPhoneNumber(), message);
                if (order.getBuyer().getEmail() != null)
                    sendEmail(order.getBuyer().getEmail(), message);
            }
        }
    }

    private void saveNotification(List<Orders> orders, List<Long> notifiedObjectIds, String message, NotificationType notificationType, String causeOfNotification) {
        for (Orders order : orders) {
            if (!notifiedObjectIds.contains(order.getId())) {
                Notification notification = Notification.builder()
                        .notificationType(notificationType)
                        .message(String.format(message, order.getBranch().getRegion().getId(), order.getBranch().getRegion().getNameEn(), order.getBranch().getId(), order.getBranch().getNameEn(), order.getId()))
                        .entityName("ORDER")
                        .notifiedObjectID(order.getId())
                        .causeOfNotification(causeOfNotification)
                        .build();
                notificationRepo.save(notification);
            }
        }
    }


    @Override
    public Result<?> changeStatus(Long orderId, String statusName) {
        Optional<Orders> optional = orderRepo.findById(orderId);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_ORDER_ID_NOT_EXIST);
        Orders order = optional.get();
        statusName = statusName.toUpperCase();
        if (order.getStatus().name().equals(statusName))
            return new Result<>(false, Messages.ORDER_STATUS_ALREADY_CHANGED_TO_THIS_STATUS);
        switch (statusName) {
            case Filter.DELIVERING -> {
                if (!order.getStatus().equals(OrderStatus.PREPARING))
                    return new Result<>(false, Messages.YOU_CANT_CHANGE_THIS_ORDER_STATUS_TO_DELIVERING_ETC);
                order.setStatus(OrderStatus.DELIVERING);
                order.setTimeOfGivingToDeliver(Timestamp.valueOf(LocalDateTime.now()));
            }
            case Filter.WAITING_CLIENT -> {
                if (!order.getStatus().equals(OrderStatus.DELIVERING))
                    return new Result<>(false, Messages.YOU_CANT_CHANGE_THIS_ORDER_STATUS_TO_WAITING_CLIENT_ETC);
                order.setTimeOfWaitingClient(Timestamp.valueOf(LocalDateTime.now()));
                order.setStatus(OrderStatus.WAITING_CLIENT);
                order.setTimeOfDelivered(Timestamp.valueOf(LocalDateTime.now()));
            }
            case Filter.RETURNED -> {
                if (order.getStatus().equals(OrderStatus.PREPARING))
                    return new Result<>(false, Messages.YOU_CANT_CHANGE_THIS_ORDER_STATUS_TO_RETURNED_ETC);
                if (order.getStatus().equals(OrderStatus.SOLD)) {
                    LocalDateTime soldDate = order.getTimeOfSelling().toLocalDateTime();
                    if (soldDate.plusDays(7).isBefore(LocalDateTime.now()))
                        return new Result<>(false, Messages.CLIENT_CANT_CANCEL_THIS_ORDER_ETC);
                    if (order.getPaymentType().equals(PaymentType.CARD)) {
                        String lastSixCharOfCard = order.getLastSixCharOFBuyerCard();
                        order.setLastSixCharOFBuyerCard(null);
                        // return money to this card from Bank card
                    }
                    order.setPaid(false);
                    soldProductsRepo.deleteByOrderId(orderId);
                    Map<Product, Integer> map = order.getBasket().getProductAmount();
                    Set<Product> productSet = map.keySet();
                    for (Product product : productSet) {
                        Integer soldAmount = map.get(product);
                        product.setAmount(product.getAmount() + soldAmount);
                        product.setAmountOfSoldProducts(product.getAmountOfSoldProducts() - soldAmount);
                        productRepo.save(product);
                        Seller seller = product.getSeller();
                        seller.setAmountSoldProducts(seller.getAmountSoldProducts() - soldAmount);
                        seller.setCostOfSoldProducts(seller.getCostOfSoldProducts() - (long) product.getDiscountedPrice() * soldAmount);
                        seller.setAmountOfProductsReturned(seller.getAmountOfProductsReturned() + soldAmount);
                        sellerRepo.save(seller);
                    }
                }
                order.setStatus(OrderStatus.RETURNED);
                order.setTimeOfReturning(Timestamp.valueOf(LocalDateTime.now()));
                if (order.getTimeOfDelivered() == null)
                    order.setTimeOfDelivered(Timestamp.valueOf(LocalDateTime.now()));
            }
            case Filter.SOLD -> {
                if (!order.getStatus().equals(OrderStatus.DELIVERING) && !order.getStatus().equals(OrderStatus.WAITING_CLIENT))
                    return new Result<>(false, Messages.YOU_CANT_CHANGE_THIS_ORDER_STATUS_TO_SOLD_ETC);
                order.setStatus(OrderStatus.SOLD);
                order.setTimeOfSelling(Timestamp.valueOf(LocalDateTime.now()));
                if (order.getTimeOfDelivered() == null)
                    order.setTimeOfDelivered(Timestamp.valueOf(LocalDateTime.now()));
                SoldProducts soldProducts = SoldProducts.builder()
                        .order(order)
                        .soldDate(Timestamp.valueOf(LocalDateTime.now()))
                        .build();
                soldProductsRepo.save(soldProducts);
                Map<Product, Integer> map = order.getBasket().getProductAmount();
                Set<Product> productSet = map.keySet();
                for (Product product : productSet) {
                    Integer soldAmount = map.get(product);
                    product.setAmount(product.getAmount() - soldAmount);
                    product.setAmountOfSoldProducts(product.getAmountOfSoldProducts() + soldAmount);
                    productRepo.save(product);
                    Seller seller = product.getSeller();
                    seller.setAmountSoldProducts(seller.getAmountSoldProducts() + soldAmount);
                    seller.setCostOfSoldProducts(seller.getCostOfSoldProducts() + (long) product.getDiscountedPrice() * soldAmount);
                    sellerRepo.save(seller);
                }
            }
            default -> {
                return new Result<>(false, String.format(Messages.SUCH_ORDER_STATUS_NOT_EXIST, statusName));
            }
        }
        orderRepo.save(order);
        return new Result<>(true, Messages.ORDER_STATUS_UPDATED);
    }


    @Override
    public Result<?> edit(Long id, OrderDTO dto) throws IOException {
        Optional<Orders> optional = orderRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_ORDER_ID_NOT_EXIST);
        Orders orders = optional.get();
        if (!orders.getStatus().equals(OrderStatus.PREPARING) && !orders.getStatus().equals(OrderStatus.DELIVERING))
            return new Result<>(false, String.format(Messages.YOU_CANT_CHANGE_ORDER_ETC, orders.getStatus().name().toLowerCase()));
        if (dto.isToHome()) {
            if (!(orders.isToHome() && orders.getHomeLatitude().equals(dto.getHomeLatitude()) && orders.getHomeLongitude().equals(dto.getHomeLongitude()))) {
                Result<?> result = setSomeDataToOrder(dto, orders);
                if (!result.getSuccess()) return result;
                orders = (Orders) result.getData();
            }
        } else {
            Optional<Branch> optionalBranch = branchRepo.findByIdAndActive(dto.getBranchId(), true);
            if (optionalBranch.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
            Branch branch = optionalBranch.get();
            orders.setBranch(branch);
        }
        orders.setToHome(dto.isToHome());
        if (!orders.getPaymentType().name().equals(dto.getPaymentType())) {
            if (dto.getPaymentType().equals(PaymentType.CARD.name())) {
                String lastSixCharOfCard = dto.getCreditCardNumber().substring(10);
                // send to PayMe api
                orders.setPaid(true);
                orders.setLastSixCharOFBuyerCard(lastSixCharOfCard);
            } else {
                // from bank card return to client card his money.
                orders.setPaid(false);
                orders.setLastSixCharOFBuyerCard(null);
            }
            orders.setPaymentType(PaymentType.valueOf(dto.getPaymentType()));
        }
        orderRepo.save(orders);
        return new Result<>(true, Messages.ORDER_UPDATED);
    }


    public void sendEmail(String to, String orderMessage) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@grapebank.com");
            message.setTo(to);
            message.setSubject("New Notification");
            message.setText(buildEmail(orderMessage));
//            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildEmail(String orderMessage) {
        return "<div style=\"font-family: sans-serif\">\n" +
                "    <p>Hi " + orderMessage + "</p>\n" +
                "    <br>\n" +
                "</div>\n";
    }

}
