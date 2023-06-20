package com.example.uzum.serviceImpl;

import com.example.uzum.dto.Result;
import com.example.uzum.entity.Chat;
import com.example.uzum.entity.Message;
import com.example.uzum.helper.Filter;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.ChatRepo;
import com.example.uzum.repository.MessageRepo;
import com.example.uzum.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepo chatRepo;
    @Autowired
    private MessageRepo messageRepo;

    @Override
    public Result<?> getByCookie(String cookie, String page) {
        int pageInt = Integer.parseInt(page);
        Optional<Chat> optional = chatRepo.findByBuyerCookie(cookie);
        if (optional.isEmpty())
            return new Result<>(true, Messages.NO_CONVERSATION_IN_THIS_CHAT_YET);
        Chat chat = optional.get();
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.by("created_at").descending());
        Page<Message> messages = messageRepo.getByChatId(chat.getId(), pageable);
        return new Result<>(true, messages);
    }

    @Override
    public Result<?> getById(Long id, String page) {
        int pageInt = Integer.parseInt(page);
        Optional<Chat> optional = chatRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_CHAT_ID_NOT_EXIST);
        Chat chat = optional.get();
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "created_at"));
        Page<Message> messages = messageRepo.getByChatId(chat.getId(), pageable);
        return new Result<>(true, messages);
    }

    @Override
    public Result<?> getUnreadChats(String page, String order) {
        int pageInt = Integer.parseInt(page);
        Integer amountOfUnreadChats = chatRepo.getAmountOfUnreadChats();
        if (amountOfUnreadChats == 0) return new Result<>(true, Messages.NO_ANY_UNREAD_CHATS);
        Pageable pageable = PageRequest.of(pageInt, 20);
        switch (order) {
            case Filter.NOW_WRITTEN -> {
                pageable = PageRequest.of(pageInt, 20, Sort.Direction.DESC);
            }
            case Filter.BEFORE_WRITTEN -> {
                pageable = PageRequest.of(pageInt, 20, Sort.Direction.ASC);
            }
        }
        Page<Long> chatIds = messageRepo.getChatIdsByUnreadMessages(pageable);
        List<Chat> chats = chatRepo.findAllById(chatIds);
//        List<Chat> chats = new ArrayList<>();
//        for (Message message : messages) {
//            chats.add(message.getChat());
//        }
        return new Result<>(true, chats);
    }

    @Override
    public Result<?> getAllChats(String page, String order) {
        int pageInt = Integer.parseInt(page);
        Pageable pageable = PageRequest.of(pageInt, 20);
        Page<Chat> chats;
        List<Chat> chatList = new ArrayList<>();
        switch (order) {
            case Filter.CREATED_DATE_ASC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.ASC, "created_at"));
            }
            case Filter.CREATED_DATE_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.by(Sort.Direction.DESC, "created_at"));
            }
            case Filter.ACTIVE_ACS -> {
                pageable = PageRequest.of(pageInt, 20, Sort.Direction.ASC);
                Page<Message> messages = messageRepo.getMessageByChatActive(pageable);
                for (Message message : messages) {
                    chatList.add(message.getChat());
                }
            }
            case Filter.ACTIVE_DESC -> {
                pageable = PageRequest.of(pageInt, 20, Sort.Direction.DESC);
                Page<Message> messages = messageRepo.getMessageByChatActive(pageable);
                for (Message message : messages) {
                    chatList.add(message.getChat());
                }
            }
        }
        chats = chatRepo.findAll(pageable);
        if (chatList.isEmpty() && chats.isEmpty())
            return new Result<>(true, Messages.NO_ANY_CONVERSATIONS_IN_ALL_CHATS);
        if (chatList.isEmpty()) return new Result<>(true, chats);
        return new Result<>(true, chatList);
    }

    @Override
    public Result<?> edit(Long id, String name, String isBlocked) {
        boolean block = Boolean.parseBoolean(isBlocked);
        Optional<Chat> optional = chatRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_CHAT_ID_NOT_EXIST);
        Chat chat = optional.get();
        if (chat.getBlock()) return new Result<>(false, Messages.CHAT_ALREADY_BLOCKED);
        boolean existsByName = chatRepo.existsByNameAndIdNot(name, id);
        if (existsByName) return new Result<>(false, Messages.THIS_CHAT_NAME_ALREADY_EXIST);
        chat.setName(name);
        chat.setBlock(block);
        chatRepo.save(chat);
        return new Result<>(true, Messages.CHAT_UPDATED);
    }
}
