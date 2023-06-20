package com.example.uzum.serviceImpl;

import com.example.uzum.dto.Result;
import com.example.uzum.entity.SearchedWords;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.CategoryRepo;
import com.example.uzum.repository.ProductRepo;
import com.example.uzum.repository.SearchedWordsRepo;
import com.example.uzum.repository.SellerRepo;
import com.example.uzum.service.SearchedWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class SearchedWordsServiceImpl implements SearchedWordsService {

    @Autowired
    private SearchedWordsRepo searchedWordsRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private SellerRepo sellerRepo;
    @Autowired
    private CategoryRepo categoryRepo;

    /* When Project is ready, then add sort function to searched words such as lowest week;  by month,year; by year; all the time */


    @Override
    public Result<?> add(String search, String sessionId) {
        boolean existsBySessionId = searchedWordsRepo.existsBySessionId(sessionId);
        if (!existsBySessionId) return new Result<>(false, Messages.SUCH_SESSION_ID_NOT_EXIST);
        SearchedWords searchedWords = new SearchedWords();
        searchedWords.setSearchedWord(search);
        searchedWords.setSessionId(sessionId);
        searchedWordsRepo.save(searchedWords);
        return new Result<>(true, null);
    }

    @Override
    public Result<?> getAll(String page) {
        int pageInt = Integer.parseInt(page);
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.Direction.DESC);
        Page<SearchedWords> searchedWords = searchedWordsRepo.getAll(pageable);
        if (searchedWords.getTotalElements() == 0)
            return new Result<>(true, Messages.THIS_PAGE_HAS_NOT_ANY_SEARCHED_WORDS);
        return new Result<>(true, searchedWords);
    }


    @Override
    public Result<?> getBySessionId(String sessionId) {
        List<String> searchedByOwnUser = searchedWordsRepo.getBySessionId(sessionId);
        List<SearchedWords> searchedWordsByPopular = searchedWordsRepo.getByPopular();
        List<String> popularWords = new ArrayList<>();
        int randomNumber = (int) Math.floor(Math.random() * 100);
        while (popularWords.size() < 5) {
            if (!popularWords.contains(searchedWordsByPopular.get(randomNumber).getSearchedWord()))
                popularWords.add(searchedWordsByPopular.get(randomNumber).getSearchedWord());
            randomNumber = (int) Math.floor(Math.random() * 100);
        }
        List<List<String>> searchedWords = List.of(searchedByOwnUser, popularWords);
        return new Result<>(true, searchedWords);
    }


    @Override
    public Result<?> getPromptWordsForSearching(String search, String sessionId) {
//        List<SearchedWords> searchedWords = searchedWordsRepo.getSearchedWords(search, sessionId);
//        if (searchedWords.isEmpty()){
//            Result<?> result = add(search, sessionId);
//            if (!result.getSuccess()) return result;
//        }
//        List<Product> productNames = productRepo.getPromptWords(search);
//        List<Category> categories = categoryRepo.getPromptCategories(search);
//        List<Category> categoriesContinue = categoryRepo.getPromptCategoriesByProduct(search);
//        categories.addAll(categoriesContinue);
//        if (categories.size() > 5) categories = categories.subList(0, 5);
//        List<Seller> sellers = sellerRepo.getPromptSellers(search);
//        PromptWords promptWords = new PromptWords(searchedWords, productNames, categories, sellers);
//        return new Result<>(true, promptWords);
        return  null;
    }

    @Override
    public Result<?> deleteBySessionId(String sessionId) {
        searchedWordsRepo.deleteBySessionId(sessionId);
        return new Result<>(true, Messages.SEARCHED_WORDS_DELETED);
    }

    @Override
    public Result<?> deleteById(Long id) {
        boolean existsById = searchedWordsRepo.existsById(id);
        if (!existsById) return new Result<>(false, Messages.SUCH_SEARCHED_WORD_ID_NOT_EXIST);
        searchedWordsRepo.deleteById(id);
        return new Result<>(true, Messages.SEARCHED_WORDS_DELETED);
    }
}
