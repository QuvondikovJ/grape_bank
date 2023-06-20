package com.example.uzum.serviceImpl;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.soldProducts.GetAllSeller;
import com.example.uzum.dto.soldProducts.GetByBranchAndRegionDto;
import com.example.uzum.dto.soldProducts.GetProductBySeller;
import com.example.uzum.dto.soldProducts.GetStatForBank;
import com.example.uzum.entity.Branch;
import com.example.uzum.entity.Product;
import com.example.uzum.entity.Region;
import com.example.uzum.entity.Seller;
import com.example.uzum.helper.Filter;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.*;
import com.example.uzum.service.OrderService;
import com.example.uzum.service.SoldProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.uzum.helper.StringUtils.getFromAndToInterval;

@Service
public class SoldProductsServiceImpl implements SoldProductsService {


    @Autowired
    private SoldProductsRepo soldProductsRepo;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BranchRepo branchRepo;
    @Autowired
    private RegionRepo regionRepo;
    @Autowired
    private SellerRepo sellerRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private OrderRepo orderRepo;

    @Override
    public Result<?> getByBranch(List<String> date, String branchId) {
        int branchIdInt = Integer.parseInt(branchId);
        List<LocalDateTime> times = getFromAndToInterval(date);
        LocalDateTime from = times.get(0);
        LocalDateTime to = times.get(1);
        Optional<Branch> optionalBranch = branchRepo.findByIdAndActive(branchIdInt, true);
        if (optionalBranch.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
        Integer soldProductAmount = soldProductsRepo.getSoldProductAmountByBranch(branchIdInt, Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer byCard = soldProductsRepo.getCardByBranch(branchIdInt, Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer byCash = soldProductsRepo.getCashByBranch(branchIdInt, Timestamp.valueOf(from), Timestamp.valueOf(to));
        GetByBranchAndRegionDto dto = GetByBranchAndRegionDto.builder()
                .branch(optionalBranch.get())
                .totalSales(byCash + byCard)
                .salesByCash(byCash)
                .salesByCard(byCard)
                .soldProductAmount(soldProductAmount)
                .build();
        return new Result<>(true, dto);
    }

    @Override
    public Result<?> getByRegion(List<String> date, String regionId) {
        int regionIdInt = Integer.parseInt(regionId);
        List<LocalDateTime> times = getFromAndToInterval(date);
        LocalDateTime from = times.get(0);
        LocalDateTime to = times.get(1);
        Optional<Region> optional = regionRepo.findByIdAndActive(regionIdInt, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_REGION_ID_NOT_EXIST);
        Region region = optional.get();
        Integer soldProductAmount = soldProductsRepo.getSoldProductAmountByRegion(regionIdInt, Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer byCard = soldProductsRepo.getCardByRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer byCash = soldProductsRepo.getCashByRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
        GetByBranchAndRegionDto dto = GetByBranchAndRegionDto.builder()
                .region(region)
                .soldProductAmount(soldProductAmount)
                .totalSales(byCash + byCard)
                .salesByCash(byCash)
                .salesByCard(byCard)
                .build();
        return new Result<>(true, dto);
    }

    @Override
    public Result<?> getAllRegion(List<String> date, String order) {
        List<LocalDateTime> times = getFromAndToInterval(date);
        LocalDateTime from = times.get(0);
        LocalDateTime to = times.get(1);
        List<Region> regions = regionRepo.findAllByActive(true);
        List<GetByBranchAndRegionDto> dtos = new ArrayList<>();
        GetByBranchAndRegionDto dto;
        for (Region region : regions) {
            Integer soldProductAmount = soldProductsRepo.getSoldProductAmountByRegion(region.getId(), Timestamp.valueOf(from), Timestamp.valueOf(to));
            Integer byCard = soldProductsRepo.getCardByRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
            Integer byCash = soldProductsRepo.getCashByRegion(region, Timestamp.valueOf(from), Timestamp.valueOf(to));
            dto = GetByBranchAndRegionDto.builder()
                    .region(region)
                    .soldProductAmount(soldProductAmount)
                    .totalSales(byCash + byCard)
                    .salesByCash(byCash)
                    .salesByCard(byCard)
                    .build();
            dtos.add(dto);
        }
        List<GetByBranchAndRegionDto> sortedDtos = new ArrayList<>();
        switch (order) {
            case Filter.ALPHABET -> {
                sortedDtos = dtos.stream().sorted(Comparator.comparing(d -> d.getRegion().getNameEn())).collect(Collectors.toList());
            }
            case Filter.TOTAL_SALES_ASC -> {
                sortedDtos = dtos.stream().sorted(Comparator.comparingInt(GetByBranchAndRegionDto::getTotalSales)).collect(Collectors.toList());
            }
            case Filter.TOTAL_SALES_DESC -> {
                sortedDtos = dtos.stream().sorted((d1, d2) -> d2.getTotalSales() - d1.getTotalSales()).collect(Collectors.toList());
            }
            case Filter.SOLD_PRODUCT_AMOUNT_ASC -> {
                sortedDtos = dtos.stream().sorted(Comparator.comparingInt(GetByBranchAndRegionDto::getSoldProductAmount)).collect(Collectors.toList());
            }
            case Filter.SOLD_PRODUCT_AMOUNT_DESC -> {
                sortedDtos = dtos.stream().sorted((d1, d2) -> d2.getSoldProductAmount() - d1.getSoldProductAmount()).collect(Collectors.toList());
            }
        }
        return new Result<>(true, sortedDtos);
    }

    @Override
    public Result<?> getBranchesByRegion(List<String> date, String order, String regionId) {
        List<LocalDateTime> times = getFromAndToInterval(date);
        LocalDateTime from = times.get(0);
        LocalDateTime to = times.get(1);
        int regionIdInt = Integer.parseInt(regionId);
        Optional<Region> optionalRegion = regionRepo.findByIdAndActive(regionIdInt, true);
        if (optionalRegion.isEmpty()) return new Result<>(false, Messages.SUCH_REGION_ID_NOT_EXIST);
        List<Branch> branches = branchRepo.getByRegionIdAndActive(regionIdInt, true);
        List<GetByBranchAndRegionDto> dtos = new ArrayList<>();
        GetByBranchAndRegionDto dto;
        for (Branch branch : branches) {
            Integer soldProductAmount = soldProductsRepo.getSoldProductAmountByBranch(branch.getId(), Timestamp.valueOf(from), Timestamp.valueOf(to));
            Integer byCard = soldProductsRepo.getCardByBranch(branch.getId(), Timestamp.valueOf(from), Timestamp.valueOf(to));
            Integer byCash = soldProductsRepo.getCashByBranch(branch.getId(), Timestamp.valueOf(from), Timestamp.valueOf(to));
            dto = GetByBranchAndRegionDto.builder()
                    .branch(branch)
                    .totalSales(byCash + byCard)
                    .salesByCash(byCash)
                    .salesByCard(byCard)
                    .soldProductAmount(soldProductAmount)
                    .build();
            dtos.add(dto);
        }
        List<GetByBranchAndRegionDto> sortedDtos = new ArrayList<>();
        switch (order) {
            case Filter.ALPHABET -> {
                sortedDtos = dtos.stream().sorted(Comparator.comparing(d -> d.getRegion().getNameEn())).collect(Collectors.toList());
            }
            case Filter.TOTAL_SALES_ASC -> {
                sortedDtos = dtos.stream().sorted(Comparator.comparingInt(GetByBranchAndRegionDto::getTotalSales)).collect(Collectors.toList());
            }
            case Filter.TOTAL_SALES_DESC -> {
                sortedDtos = dtos.stream().sorted((d1, d2) -> d2.getTotalSales() - d1.getTotalSales()).collect(Collectors.toList());
            }
            case Filter.SOLD_PRODUCT_AMOUNT_ASC -> {
                sortedDtos = dtos.stream().sorted(Comparator.comparingInt(GetByBranchAndRegionDto::getSoldProductAmount)).collect(Collectors.toList());
            }
            case Filter.SOLD_PRODUCT_AMOUNT_DESC -> {
                sortedDtos = dtos.stream().sorted((d1, d2) -> d2.getSoldProductAmount() - d1.getSoldProductAmount()).collect(Collectors.toList());
            }
        }
        return new Result<>(true, sortedDtos);
    }

    @Override
    public Result<?> getBySellerIdForSellers(Integer sellerId, String order, String page) {
        int pageInt = Integer.parseInt(page);
        Optional<Seller> optional = sellerRepo.findByIdAndIsActive(sellerId, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_SELLER_ID_NOT_EXIST);
        Pageable pageable = PageRequest.of(pageInt, 20);
        switch (order) {
            case Filter.ALPHABET -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("name_en").ascending());
            }
            case Filter.SOLD_PRODUCT_AMOUNT_ASC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("amount_of_sold_products").ascending());
            }
            case Filter.SOLD_PRODUCT_AMOUNT_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("amount_of_sold_products").descending());
            }
            case Filter.LEFT_PRODUCT_AMOUNT_ASC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("amount").ascending());
            }
            case Filter.LEFT_PRODUCT_AMOUNT_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by("amount").descending());
            }
            case Filter.COST_OF_SOLD_PRODUCTS_ASC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.Direction.ASC);
            }
            case Filter.COST_OF_SOLD_PRODUCTS_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.Direction.DESC);
            }
        }

        if (order.equals(Filter.COST_OF_SOLD_PRODUCTS_ASC) || order.equals(Filter.COST_OF_SOLD_PRODUCTS_DESC)) {
            Page<GetProductBySeller> dtos = productRepo.getByCostOfSoldProducts(sellerId, pageable);
            return new Result<>(true, dtos);
        } else {
            Page<Product> products = productRepo.findAllBySellerIdAndIsActive(sellerId, true, pageable);
            List<GetProductBySeller> dtos = new ArrayList<>();
            GetProductBySeller dto;
            for (Product product : products) {
                dto = GetProductBySeller.builder()
                        .id(product.getId())
                        .nameEn(product.getNameEn())
                        .nameRu(product.getNameUz())
                        .soldProductAmount(product.getAmountOfSoldProducts())
                        .leftProductAmount(product.getAmount())
                        .price(product.getPurchasedPriceFromSeller())
                        .costOfSoldProduct(product.getPurchasedPriceFromSeller() * product.getAmountOfSoldProducts())
                        .build();
                dtos.add(dto);
            }
            return new Result<>(true, dtos);

        }
    }

    @Override
    public Result<?> getAllSeller(List<String> date, String order, String page) {
        int pageInt = Integer.parseInt(page);
        List<LocalDateTime> times = getFromAndToInterval(date);
        LocalDateTime from = times.get(0);
        LocalDateTime to = times.get(1);
        Pageable pageable = PageRequest.of(pageInt, 20);
        switch (order) {
            case Filter.ALPHABET -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.ASC, "sel.name"));
            }
            case Filter.SOLD_PRODUCT_AMOUNT_ASC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.ASC, "sel.amount_sold_products"));
            }
            case Filter.SOLD_PRODUCT_AMOUNT_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "sel.amount_sold_products"));
            }
            case Filter.RATING_ASC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.ASC, "sel.rating"));
            }
            case Filter.RATING_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "sel.rating"));
            }
            case Filter.COST_OF_SOLD_PRODUCTS_ASC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.ASC, "sel.cost_of_sold_products"));
            }
            case Filter.COST_OF_SOLD_PRODUCTS_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "sel.cost_of_sold_products"));
            }
        }

        Page<GetAllSeller> sellers = sellerRepo.getAllSellers(Timestamp.valueOf(from), Timestamp.valueOf(to), pageable);
        return new Result<>(true, sellers);
    }

    @Override
    public Result<?> getStatForBank(List<String> date) {
        List<LocalDateTime> times = getFromAndToInterval(date);
        LocalDateTime from = times.get(0);
        LocalDateTime to = times.get(1);
        Integer byCard = orderRepo.getByCard(Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer byCash = orderRepo.getByCash(Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer originalPriceOfProducts = orderRepo.getByOriginalPriceOfProducts(Timestamp.valueOf(from), Timestamp.valueOf(to));
        Integer amountOfSoldProducts = orderRepo.getAmountOfSoldProducts(Timestamp.valueOf(from), Timestamp.valueOf(to));
        GetStatForBank dto = GetStatForBank.builder()
                .totalSales(byCard + byCash)
                .amountOfSoldProducts(amountOfSoldProducts)
                .benefit(byCard + byCash - originalPriceOfProducts)
                .byCard(byCard)
                .byCash(byCash)
                .build();
        return new Result<>(true, dto);
    }
}
