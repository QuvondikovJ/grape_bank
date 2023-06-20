package com.example.uzum.serviceImpl;

import com.example.uzum.dto.user.FillBalanceDTO;
import com.example.uzum.dto.user.BuyerDTO;
import com.example.uzum.dto.user.UserLoginDto;
import com.example.uzum.dto.Result;
import com.example.uzum.dto.user.UserPhonesDto;
import com.example.uzum.entity.*;
import com.example.uzum.entity.enums.CauseOfTransaction;
import com.example.uzum.entity.enums.Gender;
import com.example.uzum.entity.enums.Role;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.*;
import com.example.uzum.security.EncryptAndDecrypt;
import com.example.uzum.security.jwt.JwtUtil;
import com.example.uzum.service.EmailService;
import com.example.uzum.service.TwilioSmsSender;
import com.example.uzum.service.BuyerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.example.uzum.helper.StringUtils.*;


@Service
public class BuyerServiceImpl implements BuyerService {


    @Autowired
    private BuyerRepo buyerRepo;
    @Autowired
    private TwilioSmsSender twilioSmsSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ConfirmationTokenRepo confirmationTokenRepo;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private EncryptAndDecrypt encryptAndDecrypt;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthorityRepo authorityRepo;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TransactionRepo transactionRepo;


    @Value("${webhook.jwt.token.username}")
    private String webhookUsername;

    private static final Logger logger = LogManager.getLogger(BuyerServiceImpl.class);

    @Override
    public Result<?> register(BuyerDTO dto) {
        String phoneNumber = dto.getPhoneNumber();
        Result<?> result = validatePhoneNumber(phoneNumber);
        if (!result.getSuccess()) return result;
        boolean isExistsByPhoneNumber = buyerRepo.existsByPhoneNumber(dto.getPhoneNumber());
        if (isExistsByPhoneNumber) return new Result<>(false, Messages.THIS_PHONE_NUMBER_REGISTERED_ALREADY);
        boolean isExistsEmployeeByPhoneNumber = employeeRepo.existsByPhoneNumber(dto.getPhoneNumber());
        if (isExistsEmployeeByPhoneNumber) return new Result<>(false, Messages.THIS_NUMBER_REGISTERED_AS_EMPLOYEE);
        Buyer buyer = new Buyer();
        Set<Authority> authorities = authorityRepo.findAllByAuthorityIn(Role.BUYER.getPermissions());
        buyer.setRole(Role.BUYER);
        buyer.setAuthorities(authorities);
        buyer.setPhoneNumber(phoneNumber);
        String verificationCode = generateVerificationCode();
        twilioSmsSender.sendSms(phoneNumber, String.format(Messages.SEND_SMS_REGISTER, verificationCode));
        logger.info("SMS sent to {} for registering. Verification code: {} ", dto.getPhoneNumber(), verificationCode);
        buyer = buyerRepo.save(buyer);
        buyer.setReferralLink(buyer.getReferralLink() + buyer.getId());
        buyerRepo.save(buyer);
        logger.info("New buyer saved. ID: {} ", buyer.getId());
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setBuyer(buyer);
        confirmationToken.setCode(verificationCode);
        confirmationToken.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(3)));
        confirmationTokenRepo.save(confirmationToken);
        return new Result<>(true, Messages.BUYER_ADDED);
    }


    @Override
    public Result<?> confirmPhoneNumber(UserLoginDto dto) {
        Optional<ConfirmationToken> optional = confirmationTokenRepo.findByCodeAndBuyer_PhoneNumber(dto.getCode(), dto.getPhoneNumber());
        if (optional.isEmpty()) return new Result<>(false, Messages.THIS_CODE_WRONG);
        ConfirmationToken token = optional.get();
        LocalDateTime expireTime = token.getExpiresAt().toLocalDateTime();
        if (expireTime.isBefore(LocalDateTime.now()))
            return new Result<>(false, Messages.THIS_CODE_EXPIRED);
        if (token.getConfirmedAt() != null)
            return new Result<>(false, Messages.THIS_CODE_ALREADY_CONFIRMED);
        token.setConfirmedAt(Timestamp.valueOf(LocalDateTime.now()));
        confirmationTokenRepo.save(token);
        Buyer buyer = token.getBuyer();
        if (!buyer.getIsActive()) {
            buyer.setIsActive(true);
            buyerRepo.save(buyer);
            logger.info("Buyer is activated. ID: {} ", buyer.getId());
        }
        String jwtToken = jwtUtil.generateToken(dto.getPhoneNumber());
        return new Result<>(true, jwtToken);
    }


    @Override
    public Result<?> loginOrRegister(UserLoginDto dto) {
        String phoneNumber = dto.getPhoneNumber();
        Result<?> result = validatePhoneNumber(phoneNumber);
        if (!result.getSuccess()) return result;
        boolean existsByPhoneNumber = buyerRepo.existsByPhoneNumber(phoneNumber);
        if (existsByPhoneNumber) return loginByPhoneNumber(phoneNumber);
        else return register(BuyerDTO.builder().phoneNumber(phoneNumber).build());
    }

    @Override
    public Result<?> loginByPhoneNumber(String phoneNumber) {
        return getCodeToRegisterOrLogin(phoneNumber);
    }

    @Override
    public Result<?> loginByPhoneNumberAndPassword(UserLoginDto dto) {
        Optional<Buyer> optional = buyerRepo.findByPhoneNumberAndIsActive(dto.getPhoneNumber(), true);
        if (optional.isEmpty()) return new Result<>(false, Messages.THIS_PHONE_NUMBER_NOT_FOUND);
        Buyer buyer = optional.get();
        boolean checkPassword = passwordEncoder.matches(passwordEncoder.encode(dto.getPassword()), buyer.getPassword());
        if (!checkPassword) return new Result<>(false, Messages.PASSWORD_IS_WRONG);
        String jwtToken = jwtUtil.generateToken(dto.getPhoneNumber());
        return new Result<>(true, jwtToken);
    }


    // When user login or register, him is sent token.

    @Override
    public Result<?> getCodeToRegisterOrLogin(String phoneNumber) {
        return getCodeToRegisterOrLoginOrChangePhoneNumber(phoneNumber, null);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> employeeOptional = employeeRepo.findByPhoneNumberAndIsActive(username, true);
        if (employeeOptional.isPresent()) return employeeOptional.get();
        Optional<Buyer> buyerOptional = buyerRepo.findByPhoneNumberAndIsActive(username, true);
        if (buyerOptional.isPresent()) return buyerOptional.get();
        throw new UsernameNotFoundException(Messages.YOU_DELETED_IN_SYSTEM_ETC);  // This user maybe is deleted in system, because when this method works
        //  token is valid, so before this user registered and him was sent token and token still is valid
    }


    @Override
    public Result<?> confirmEmail(String token) {
        Optional<ConfirmationToken> optional = confirmationTokenRepo.findByCode(token);
        if (optional.isEmpty()) return new Result<>(false, Messages.THIS_TOKEN_IS_WRONG);
        ConfirmationToken confirmationToken = optional.get();
        if (confirmationToken.getConfirmedAt() != null)
            return new Result<>(false, Messages.EMAIL_ALREADY_CONFIRMED);
        if (confirmationToken.getExpiresAt().before(Timestamp.valueOf(LocalDateTime.now())))
            return new Result<>(false, Messages.TOKEN_EXPIRED);
        Buyer buyer = confirmationToken.getBuyer();
        buyer.setEmail(confirmationToken.getTemporaryField());
        buyerRepo.save(buyer);
        logger.info("Email confirmed. Email: {}, Phone: {} ", buyer.getEmail(), buyer.getPhoneNumber());
        confirmationToken.setConfirmedAt(Timestamp.valueOf(LocalDateTime.now()));
        confirmationTokenRepo.save(confirmationToken);
        return new Result<>(true, Messages.EMAIL_CONFIRMED);
    }

    @Override
    public Result<?> getById(Integer id) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<Buyer> optional = buyerRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
        Buyer buyer = optional.get();
        Buyer buyerThatEnteredToSystem = null;
        String cipheredCardNumber = buyer.getCardNumber();
        String cardNumber = encryptAndDecrypt.decryptCardDetail(cipheredCardNumber);
        String secretCardNumber = "**** **** **" + cardNumber.substring(10, 12) + " " + cardNumber.substring(12);
        String secretCardExpireDate = "**/**";
        buyer.setCardNumber(secretCardNumber);
        buyer.setCardExpireDate(secretCardExpireDate);
        try {
            buyerThatEnteredToSystem = (Buyer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return new Result<>(true, buyer);
        }
        if (!buyerThatEnteredToSystem.getId().equals(id))
            return new Result<>(false, String.format(Messages.YOU_CANT_SEE_INFORMATION_OF_ANOTHER_BUYER, buyerThatEnteredToSystem.getFirstname(), buyerThatEnteredToSystem.getLastname()));
        return new Result<>(true, buyer);
    }

    @Override
    public Result<?> getByFirstNameAndLastNameAndPhone(String name, String phone, String page) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (name == null && phone == null)
            return new Result<>(false, Messages.NAME_OR_PHONE_NUMBER_REQUIRED);
        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) name = null;
        }
        if (phone != null) {
            phone = phone.trim();
            if (phone.isEmpty()) phone = null;
        }
        if (name == null && phone == null) return new Result<>(false, Messages.ENTER_VALID_PHONE_OR_NAME);
        String query = "SELECT buy FROM Buyer AS buy WHERE buy.isActive=TRUE  ";
        String firstName;
        String lastName;
        int pageInt = Integer.parseInt(page);
        if (name != null) {
            if (name.split(" ").length > 1) {
                String[] names = name.split(" ");
                firstName = capitalizeText(names[0]);
                lastName = capitalizeText(names[1]);
                query = query.concat("AND (buy.firstname LIKE '" + firstName + "%' OR buy.firstname='" + firstName + "') AND (buy.lastname LIKE '" + lastName + "%' OR buy.lastname='" + lastName + "') ");
            } else {
                firstName = capitalizeText(name);
                query = query.concat("AND (buy.firstname LIKE '" + firstName + "%' OR buy.firstname='" + firstName + "') ");
            }
        }
        if (phone != null) {
            phone = "+".concat(phone);
            query = query.concat("AND (buy.phoneNumber LIKE '" + phone + "%' OR buy.phoneNumber='" + phone + "') ");
        }
        query = query.concat(" ORDER BY buy.createdDate DESC ");
        Query createQuery = entityManager.createQuery(query).setFirstResult(pageInt * 20).setMaxResults(20);
        logger.info(query);
        List<Buyer> buyers = createQuery.getResultList();
        for (Buyer buyer : buyers) {
            String cipheredCardNumber = buyer.getCardNumber();
            String cardNumber = encryptAndDecrypt.decryptCardDetail(cipheredCardNumber);
            String secretCardNumber = "**** **** **" + cardNumber.substring(10, 12) + " " + cardNumber.substring(12);
            String secretCardExpireDate = "**/**";
            buyer.setCardNumber(secretCardNumber);
            buyer.setCardExpireDate(secretCardExpireDate);
        }
        return new Result<>(true, buyers);
    }


    @Override
    public Result<?> getBuyersAmount(List<String> time, String isActive) {
        List<LocalDateTime> times = getFromAndToInterval(time);
        LocalDateTime from = times.get(0);
        LocalDateTime to = times.get(1);
        Boolean isActiveValue = Boolean.parseBoolean(isActive);
        Integer amountOfActiveBuyers = buyerRepo.getAmountOfActiveBuyers(Timestamp.valueOf(from), Timestamp.valueOf(to), isActiveValue);
        return new Result<>(true, amountOfActiveBuyers);
    }

    @Override
    public Result<?> edit(Integer id, BuyerDTO dto) throws IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Optional<Buyer> optional = buyerRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
        Buyer buyer = optional.get();
        try {
            Buyer buyerThatEnteredToSystem = (Buyer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!buyerThatEnteredToSystem.getId().equals(buyer.getId()))
                return new Result<>(false, String.format(Messages.YOU_CANT_EDIT_INFORMATION_OF_ANOTHER_BUYER, buyerThatEnteredToSystem.getFirstname(), buyerThatEnteredToSystem.getLastname()));
        } catch (Exception e) {
            /* DO NOTHING! */ // because to method entered Director or Admin.
        }
        String newPhoneNumber = dto.getPhoneNumber();
        Result<?> result;
        if (dto.getPassword() != null) {
            result = validatePassword(dto.getPassword());
            if (!result.getSuccess()) return result;
            boolean checkPassword = passwordEncoder.matches(dto.getPassword(), buyer.getPassword());
            if (!checkPassword) buyer.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getCardNumber() != null && dto.getCardExpiredDate() != null) {
            result = validateCardDetails(dto.getCardNumber(), dto.getCardExpiredDate());
            if (!result.getSuccess()) return result;
            String oldCardNumber = encryptAndDecrypt.decryptCardDetail(buyer.getCardNumber());
            if (!oldCardNumber.equals(dto.getCardNumber())) {
                buyer.setCardNumber(encryptAndDecrypt.encryptCardDetails(dto.getCardNumber()));
                buyer.setCardExpireDate(encryptAndDecrypt.encryptCardDetails(dto.getCardExpiredDate()));
            }
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(buyer.getEmail())) {
            boolean existsByEmail = buyerRepo.existsByEmail(dto.getEmail());
            if (existsByEmail) return new Result<>(false, Messages.THIS_EMAIL_BELONGS_TO_ANOTHER_USER);
            String uuid = UUID.randomUUID().toString();
            String emailContent = emailService.buildEmailForAuthorization(dto.getFirstname() + " " + dto.getLastname(), dto.getEmail(), "Request to change email or set email.", uuid, true);
            emailService.sendEmail(emailContent, buyer.getPhoneNumber(), "Request to change for email or set email", dto.getEmail());
            ConfirmationToken confirmationToken = ConfirmationToken.builder()
                    .code(uuid)
                    .buyer(buyer)
                    .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(5)))
                    .temporaryField(dto.getEmail())
                    .isBlocked(false)
                    .build();
            confirmationTokenRepo.save(confirmationToken);
        }
        if (!buyer.getPhoneNumber().equals(newPhoneNumber)) {
            result = validatePhoneNumber(newPhoneNumber);
            if (!result.getSuccess()) return result;
            result = getCodeToRegisterOrLoginOrChangePhoneNumber(buyer.getPhoneNumber(), newPhoneNumber);
            if (!result.getSuccess()) return result;
        }
        buyer.setFirstname(capitalizeText(dto.getFirstname()));
        buyer.setLastname(capitalizeText(dto.getLastname()));
        buyer.setMiddleName(capitalizeText(dto.getMiddleName()));
        buyer.setBirthDate(Timestamp.valueOf(LocalDate.parse(dto.getBirthDate()).atStartOfDay()));
        buyer.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        buyerRepo.save(buyer);
        logger.info("Buyer updated. ID: {} ", buyer.getId());
        return new Result<>(true, Messages.BUYER_UPDATED);
    }


    @Override
    public Result<?> getCodeToConfirmNewPhoneNumber(UserPhonesDto dto) {
        return getCodeToRegisterOrLoginOrChangePhoneNumber(dto.getPhoneNumber(), dto.getNewPhoneNumber());
    }

    public Result<?> getCodeToRegisterOrLoginOrChangePhoneNumber(String phoneNumber, String newPhoneNumber) {
        Result<?> result = validatePhoneNumber(phoneNumber);
        if (!result.getSuccess()) return result;
        boolean isUserRegistrationOrLogin = confirmationTokenRepo.isBuyerRegistrationOrLogin(phoneNumber);
        Optional<Buyer> optional;
        if (isUserRegistrationOrLogin) {    // if isUserRegistrationOrLogin is true, then user is logging in, otherwise user is registering.
            optional = buyerRepo.findByPhoneNumberAndIsActive(phoneNumber, true);
            if (optional.isEmpty()) return new Result<>(false, Messages.YOU_DELETED_IN_SYSTEM);
        } else {
            optional = buyerRepo.findByPhoneNumber(phoneNumber);
            if (optional.isEmpty()) return new Result<>(false, Messages.PHONE_NUMBER_WRONG);
        }
        Buyer buyer = optional.get();
        List<ConfirmationToken> tokens;
        if (newPhoneNumber != null) // user is changing his phoneNumber
            tokens = confirmationTokenRepo.getLastTokensByNewPhoneNumber(newPhoneNumber);
        else
            tokens = confirmationTokenRepo.getLastTokensByBuyerPhoneNumber(phoneNumber);
        LocalDateTime firstTokenCreatedAt;
        LocalDateTime thirdTokenCreatedAt;
        if (tokens.size() == 3 && tokens.get(0).getIsBlocked()) {
            ConfirmationToken firstToken = tokens.get(0);
            firstTokenCreatedAt = firstToken.getCreatedAt().toLocalDateTime();
            if (firstTokenCreatedAt.plusMinutes(15).isAfter(LocalDateTime.now())) { // if token is blocked, then after inactive 15 min it will be unblocked
                firstToken.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                confirmationTokenRepo.save(firstToken);
                return new Result<>(false, Messages.TOO_MANY_ATTEMPTS_ETC);
            } else {
                firstToken.setIsBlocked(false);
                confirmationTokenRepo.save(firstToken);
                sendSmsAndSaveNewToken(phoneNumber, newPhoneNumber, buyer, isUserRegistrationOrLogin);
            }
        } else if (tokens.size() == 3) {
            firstTokenCreatedAt = tokens.get(0).getCreatedAt().toLocalDateTime();
            thirdTokenCreatedAt = tokens.get(2).getCreatedAt().toLocalDateTime();
            if (thirdTokenCreatedAt.plusMinutes(5).isAfter(firstTokenCreatedAt)) { // if difference less than 5 minutes among last 3 tokens, then last one token will be blocked, and after 15 min will be unblocked.
                ConfirmationToken firstToken = tokens.get(0);
                firstToken.setIsBlocked(true);
                confirmationTokenRepo.save(firstToken);
                return new Result<>(false, Messages.TOO_MANY_ATTEMPTS_ETC);
            } else {
                sendSmsAndSaveNewToken(phoneNumber, newPhoneNumber, buyer, isUserRegistrationOrLogin);
            }
        } else {
            sendSmsAndSaveNewToken(phoneNumber, newPhoneNumber, buyer, isUserRegistrationOrLogin);
        }
        return new Result<>(true, Messages.NEW_CODE_SENT);
    }

    private void sendSmsAndSaveNewToken(String phoneNumber, String newPhoneNumber, Buyer buyer, boolean isUserRegistrationOrLogin) {
        String verificationCode = generateVerificationCode();
        if (newPhoneNumber != null) {
                        twilioSmsSender.sendSms(newPhoneNumber, String.format(Messages.SEND_SMS_TO_CHANGE_PHONE_NUMBER, verificationCode));
            logger.info("SMS sent to {} for changing phone number. Verification code is {} .", newPhoneNumber, verificationCode);
        } else if (isUserRegistrationOrLogin) {
            twilioSmsSender.sendSms(phoneNumber, String.format(Messages.SEND_SMS_LOGIN, verificationCode));
            logger.info("SMS sent to {} for logging in. Verification code is {} ", phoneNumber, verificationCode);
        } else {
                        twilioSmsSender.sendSms(phoneNumber, String.format(Messages.SEND_SMS_REGISTER, verificationCode));
            logger.info("SMS sent to {} for registering. Verification code is {} ", phoneNumber, verificationCode);
        }
        ConfirmationToken newToken = ConfirmationToken.builder()
                .code(verificationCode)
                .buyer(buyer)
                .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(3)))
                .temporaryField(newPhoneNumber) // this field is needed to change phone number and email
                .isBlocked(false)
                .build();
        confirmationTokenRepo.save(newToken);
    }

    @Override
    public Result<?> confirmNewPhoneNumberToEdit(UserLoginDto dto) {
        Optional<ConfirmationToken> optional = confirmationTokenRepo.findByCodeAndTemporaryField(dto.getCode(), dto.getPhoneNumber()); // phone number is already validated before sending its code to buyer, so we won't validate it again.
        if (optional.isEmpty()) return new Result<>(false, Messages.THIS_CODE_WRONG);
        ConfirmationToken token = optional.get();
        if (token.getConfirmedAt() != null)
            return new Result<>(false, Messages.PHONE_NUMBER_ALREADY_CHANGED);
        LocalDateTime tokenExpiredDate = token.getExpiresAt().toLocalDateTime();
        if (tokenExpiredDate.isBefore(LocalDateTime.now()))
            return new Result<>(false, Messages.THIS_CODE_EXPIRED);
        String newPhoneNumber = token.getTemporaryField();
        Buyer buyer = token.getBuyer();
        logger.info("Buyer new phone number confirmed. Buyer ID: {}. Old phone number: {}. New phone number: {} ", buyer.getId(), buyer.getPhoneNumber(), token.getTemporaryField());
        buyer.setPhoneNumber(newPhoneNumber);
        buyerRepo.save(buyer);
        token.setConfirmedAt(Timestamp.valueOf(LocalDateTime.now()));
        confirmationTokenRepo.save(token);
        return new Result<>(true, Messages.PHONE_NUMBER_UPDATED);
    }

    public Result<?> fillBalance(FillBalanceDTO dto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<Buyer> optional = buyerRepo.findByIdAndIsActive(dto.getBuyerId(), true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
        Buyer buyer = optional.get();
        try {
            Buyer buyerThatEnteredToSystem = (Buyer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!buyerThatEnteredToSystem.getId().equals(buyer.getId()))
                return new Result<>(false, Messages.YOU_CANT_FILL_ANOTHER_BUYER_BALANCE);
        } catch (Exception e) {
            /* DO NOTHING! */ // because Director or Admin entered.
        }
        String buyerCardNumber;
        String buyerCardExpireDate;
        if (dto.getAmountOfMoney() < 1000) return new Result<>(false, Messages.ENTER_1000_UZS_AT_LEAST);
        if (dto.getCardNumber() != null && dto.getCardExpireDate() != null) {
            Result<?> result = validateCardDetails(dto.getCardNumber(), dto.getCardExpireDate());
            if (!result.getSuccess()) return result;
            buyerCardNumber = dto.getCardNumber();
            buyerCardExpireDate = dto.getCardExpireDate();
        } else {
            String cipheredCardNumber = buyer.getCardNumber();
            if (cipheredCardNumber == null) return new Result<>(false, Messages.CARD_DETAILS_REQUIRED_ETC);
            buyerCardNumber = encryptAndDecrypt.decryptCardDetail(cipheredCardNumber);
            buyerCardExpireDate = encryptAndDecrypt.decryptCardDetail(buyer.getCardExpireDate());
        }
        Optional<Employee> optionalEmployee = employeeRepo.findByIdAndIsActive(1, true);
        Employee director = optionalEmployee.get();
        String cipheredCardNumber = director.getCardNumber();
        String directorCardNumber = encryptAndDecrypt.decryptCardDetail(cipheredCardNumber);
        String directorCardExpireDate = encryptAndDecrypt.decryptCardDetail(director.getCardExpireDate());
        Integer buyerCardBalance = sendDataToPaypalToCheckCardBalance(buyerCardNumber, buyerCardExpireDate);
        if (buyerCardBalance < dto.getAmountOfMoney())
            return new Result<>(false, Messages.BALANCE_IS_NOT_ENOUGH_IN_YOUR_CARD);
        sendDataToPaypalToWithdrawMoney(buyerCardNumber, buyerCardExpireDate, directorCardNumber, directorCardExpireDate, dto.getAmountOfMoney());
        Transaction transaction = Transaction.builder()
                .fromCardNumber(buyerCardNumber)
                .fromCardExpireDate(buyerCardExpireDate)
                .toCardNumber(directorCardNumber)
                .toCardExpireDate(directorCardExpireDate)
                .amountOfMoney(dto.getAmountOfMoney())
                .createdDate(Timestamp.valueOf(LocalDateTime.now()))
                .buyer(buyer)
                .causeOfTransaction(CauseOfTransaction.FILL_BUYER_BALANCE)
                .build();
        transactionRepo.save(transaction);
        double balance = buyer.getBalance();
        if (dto.getAmountOfMoney() >= 1_000_000)
            balance = balance + dto.getAmountOfMoney() + dto.getAmountOfMoney() * 0.005;
        else balance = balance + dto.getAmountOfMoney();
        buyer.setBalance((int) balance);
        buyerRepo.save(buyer);
        logger.info("Buyer balance filled. Buyer ID: {}, Balance : {}, Amount transferred : {} ", buyer.getId(), balance, dto.getAmountOfMoney());
        return new Result<>(true, Messages.YOUR_BALANCE_FILLED);
    }

    public void cleanCashbackPercent(String token) throws Exception { // this method works on start of every month one time.
        String username = jwtUtil.getUsernameFromToken(token);
        if (username == null || !username.equals(webhookUsername))
            throw new Exception("Hey you, don't do that again. If you do that again, you will be in trouble. Believe me!");
        buyerRepo.cleanCashbackPercent();
    }


    public Result<?> referralLink(Integer buyerId) {
        Cookie cookie = new Cookie();
        cookie.setName("ID:" + buyerId);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(Duration.of(7, ChronoUnit.DAYS));
        Map<String, Cookie> cookieMap = new HashMap<>();
        cookieMap.put("cookie", cookie);
        return new Result<>(true, cookieMap);
    }

    @Override
    public Result<?> block(Integer id) { // when buyer orders many times and cancels order again and again when is delivered, then he will be blocked
        Optional<Buyer> optional = buyerRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
        buyerRepo.blockBuyer(id);
        logger.info("Buyer blocked. ID: {} ", id);
        return new Result<>(true, Messages.BUYER_BLOCKED);
    }

    @Override
    public Result<?> unblock(Integer id) { // if buyer begs to unblock from admin and admin buyer ni bir qoshiq qonidan kechsa bu metod ishlatiladi.
        Optional<Buyer> optional = buyerRepo.findByIdAndIsActive(id, false);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_DELETED_USER_ID_NOT_EXIST);
        buyerRepo.unblockBuyer(id);
        logger.info("Buyer unblocked. ID: {}  ", id);
        return new Result<>(true, Messages.BUYER_UNBLOCKED);
    }



}




