package com.example.uzum.serviceImpl;

import com.example.uzum.dto.faq.FAQDto;
import com.example.uzum.dto.Result;
import com.example.uzum.dto.faq.GetFAQ;
import com.example.uzum.entity.FAQ;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.FAQRepo;
import com.example.uzum.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FAQServiceImpl implements FAQService {

    @Autowired
    private FAQRepo faqRepo;


    @Override
    public Result<?> add(FAQDto dto) {
        Optional<FAQ> optional = faqRepo.findByQueEnOrQueRu(dto.getQueEn(), dto.getQueRu());
        if (optional.isPresent()) return new Result<>(false, Messages.THIS_QUESTION_ALREADY_ADDED);
        if (dto.getAnsEn() != null && dto.getAnsRu() != null) {
            Optional<FAQ> optionalFAQ = faqRepo.findByAnsEnOrAnsRu(dto.getAnsEn(), dto.getAnsRu());
            if (optionalFAQ.isPresent()) return new Result<>(false, Messages.THIS_ANSWER_ADDED_FOR_ANOTHER_QUESTION);
        }
        FAQ parentFAQ;
        if (dto.getParentFaqId() == 0) parentFAQ = null;
        else {
            Optional<FAQ> optionalParentFAQ = faqRepo.findById(dto.getParentFaqId());
            if (optionalParentFAQ.isEmpty()) return new Result<>(false, Messages.SUCH_PARENT_FAQ_ID_NOT_EXIST);
            parentFAQ = optionalParentFAQ.get();
        }
        FAQ faq = FAQ.builder()
                .queEn(dto.getQueEn())
                .queRu(dto.getQueRu())
                .ansEn(dto.getAnsEn())
                .ansRu(dto.getAnsRu())
                .parentFAQ(parentFAQ)
                .build();
        faqRepo.save(faq);
        return new Result<>(true, Messages.FAQ_SAVED);
    }


    @Override
    public Result<?> getAll() {
        List<FAQ> faqs = faqRepo.getGrandFAQs();
        List<GetFAQ> faqList = new ArrayList<>();
        for (FAQ faq : faqs) {
//            GetFAQ getFAQ = (GetFAQ) faq;
//            faqList.add(getChildrenFAQs(getFAQ, new ArrayList<>()));
        }
        return new Result<>(true, faqList);
    }

    private GetFAQ getChildrenFAQs(GetFAQ getFAQ, List<GetFAQ> getFAQS) {
        List<FAQ> faqList = faqRepo.findByParentFAQId(getFAQ.getId());
        GetFAQ childGetFAQ;
        List<GetFAQ> childGetFAQs = new ArrayList<>();
        for (FAQ faq : faqList) {
//            childGetFAQ = (GetFAQ) faq;
//            getFAQS.add(getChildrenFAQs(childGetFAQ, childGetFAQs));
        }
        getFAQ.setChildFAQs(getFAQS);
        return getFAQ;
    }


    @Override
    public Result<?> getById(Integer id) {
        Optional<FAQ> optional = faqRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_FAQ_ID_NOT_EXIST);
        return new Result<>(true, optional.get());
    }

    @Override
    public Result<?> edit(Integer id, FAQDto faqDto) {
        Optional<FAQ> optional = faqRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_FAQ_ID_NOT_EXIST);
        FAQ faq = optional.get();
        boolean existsByQueEnOrQueRu = faqRepo.existsByQueOrAns(faqDto.getQueEn(), faqDto.getQueRu(), faqDto.getAnsEn(), faqDto.getAnsRu(), id);
        if (existsByQueEnOrQueRu) return new Result<>(false, Messages.THIS_QUESTION_OR_ANSWER_ALREADY_ADDED);
        faq.setQueEn(faqDto.getQueEn());
        faq.setQueRu(faqDto.getQueRu());
        faq.setAnsEn(faqDto.getAnsEn());
        faq.setAnsRu(faqDto.getAnsRu());
        faqRepo.save(faq);
        return new Result<>(true, Messages.FAQ_UPDATED);
    }

    @Override
    public Result<?> delete(Integer id) {
        Optional<FAQ> optional = faqRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_FAQ_ID_NOT_EXIST);
        FAQ faq = optional.get();
        deleteEachFaq(faq.getId());
        return new Result<>(true, Messages.FAQ_DELETED);
    }

    private void deleteEachFaq(Integer id){
        List<FAQ> faqs = faqRepo.findByParentFAQId(id);
        for (FAQ faq : faqs) {
            deleteEachFaq(faq.getId());
        }
        faqRepo.deleteById(id);
    }

}
