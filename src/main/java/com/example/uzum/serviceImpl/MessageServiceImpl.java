package com.example.uzum.serviceImpl;

import com.example.uzum.dto.message.MessageDto;
import com.example.uzum.dto.Result;
import com.example.uzum.entity.Attachment;
import com.example.uzum.entity.Chat;
import com.example.uzum.entity.Employee;
import com.example.uzum.entity.Message;
import com.example.uzum.entity.enums.MessageWrittenBy;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.AttachmentRepo;
import com.example.uzum.repository.ChatRepo;
import com.example.uzum.repository.EmployeeRepo;
import com.example.uzum.repository.MessageRepo;
import com.example.uzum.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private AttachmentRepo attachmentRepo;
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private ChatRepo chatRepo;

    @Override
    public Result<?> add(MessageDto messageDto) {
        List<Attachment> attachments = attachmentRepo.findAllById(messageDto.getAttachmentIds());
        if (messageDto.getText() == null) {
            if (messageDto.getAttachmentIds() == null)
                return new Result<>(false, Messages.TEXT_OR_ATTACHMENT_IS_REQUIRED);
            else if (attachments.isEmpty()) return new Result<>(false, Messages.UNAVAILABLE_DATA_TYPES);
        }
        Message message;
        Chat chat;
        Optional<Chat> optionalChat = chatRepo.findByBuyerCookie(messageDto.getBuyerCookie());
        if (optionalChat.isEmpty()) {
            chat = new Chat();
            chat.setName("ID : " + chatRepo.getByAmountOfChats());
        } else chat = optionalChat.get();
        if (chat.getBlock()) return new Result<>(false, Messages.CHAT_ALREADY_BLOCKED);
        if (messageDto.getByWritten().equals(MessageWrittenBy.BUYER.name())) {
            message = Message.builder()
                    .buyerCookie(messageDto.getBuyerCookie())
                    .text(messageDto.getText())
                    .attachments(attachments)
                    .operator(null)
                    .writtenBy(MessageWrittenBy.BUYER)
                    .chat(chat)
                    .build();
        } else {
            Optional<Employee> optional = employeeRepo.findByIdAndIsActive(messageDto.getOperatorId(), true);
            if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
            message = Message.builder()
                    .buyerCookie(messageDto.getBuyerCookie())
                    .text(messageDto.getText())
                    .attachments(attachments)
                    .operator(optional.get())
                    .writtenBy(MessageWrittenBy.OPERATOR)
                    .chat(chat)
                    .build();
        }
        messageRepo.save(message);
        chat.setAmountOfUnreadMessages(chat.getAmountOfUnreadMessages() + 1);
        chatRepo.save(chat);
        return new Result<>(true, Messages.MESSAGE_ADDED);
    }

    @Override
    public Result<?> hasRead(Long id) {
        Optional<Message> optional = messageRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_MESSAGE_ID_NOT_EXIST);
        Message message = optional.get();
        message.setIsRead(true);
        messageRepo.save(message);
        Chat chat = message.getChat();
        chat.setAmountOfUnreadMessages(chat.getAmountOfUnreadMessages() - 1);
        chatRepo.save(chat);
        return new Result<>(true, Messages.OK);
    }

    @Override
    public Result<?> edit(Long id, MessageDto dto) {
        Optional<Message> optional = messageRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_MESSAGE_ID_NOT_EXIST);
        Message message = optional.get();
        Chat chat = message.getChat();
        if (chat.getBlock()) return new Result<>(false, Messages.CHAT_ALREADY_BLOCKED);

        if (!message.getBuyerCookie().equals(dto.getBuyerCookie()))
            return new Result<>(false, Messages.MESSAGE_OF_CHAT_ONLY_CAN_BE_CHANGED_BY_OPERATOR_ETC);
        if (message.getOperator() != null) {
            if (!message.getOperator().getId().equals(dto.getOperatorId()))
                return new Result<>(false, Messages.MESSAGE_OF_CHAT_ONLY_CAN_BE_CHANGED_BY_OPERATOR_ETC);
        }
        List<Attachment> attachments = attachmentRepo.findAllById(dto.getAttachmentIds());
        if (dto.getText() == null) {
            if (dto.getAttachmentIds() == null)
                return new Result<>(false, Messages.TEXT_OR_ATTACHMENT_IS_REQUIRED);
            else if (attachments.isEmpty()) return new Result<>(false, Messages.UNAVAILABLE_DATA_TYPES);
        }
        message.setText(dto.getText());
        message.setAttachments(attachments);
        message.setIsEdited(true);
        messageRepo.save(message);
        return new Result<>(true, Messages.MESSAGE_UPDATED);
    }

    @Override
    public Result<?> delete(Long id, String operatorId, String buyerCookie) {
        int operatorIdInt = Integer.parseInt(operatorId);
        Optional<Message> optional = messageRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_MESSAGE_ID_NOT_EXIST);
        Message message = optional.get();
        Chat chat = message.getChat();
        if (chat.getBlock()) return new Result<>(false, Messages.CHAT_ALREADY_BLOCKED);
        if (!message.getBuyerCookie().equals(buyerCookie))
            return new Result<>(false, Messages.MESSAGE_OF_CHAT_ONLY_CAN_BE_CHANGED_BY_OPERATOR_ETC);
        if (message.getOperator() != null) {
            if (!message.getOperator().getId().equals(operatorIdInt))
                return new Result<>(false, Messages.MESSAGE_OF_CHAT_ONLY_CAN_BE_CHANGED_BY_OPERATOR_ETC);
        }
        messageRepo.deleteById(id);
        return new Result<>(true, Messages.MESSAGE_DELETED);
    }
}
