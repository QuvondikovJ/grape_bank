package com.example.uzum.serviceImpl;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.user.EmployeeDTO;
import com.example.uzum.dto.user.UserLoginDto;
import com.example.uzum.dto.user.UserPhonesDto;
import com.example.uzum.entity.*;
import com.example.uzum.entity.enums.CauseOfTransaction;
import com.example.uzum.entity.enums.Gender;
import com.example.uzum.entity.enums.NotificationType;
import com.example.uzum.entity.enums.Role;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.*;
import com.example.uzum.security.EncryptAndDecrypt;
import com.example.uzum.security.jwt.JwtUtil;
import com.example.uzum.service.BuyerService;
import com.example.uzum.service.EmailService;
import com.example.uzum.service.EmployeeService;
import com.example.uzum.service.TwilioSmsSender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.uzum.helper.Filter;

import javax.crypto.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.Year;
import java.util.*;

import static com.example.uzum.helper.StringUtils.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private BuyerService buyerService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TwilioSmsSender twilioSmsSender;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ConfirmationTokenRepo confirmationTokenRepo;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthorityRepo authorityRepo;
    @Autowired
    private EncryptAndDecrypt encryptAndDecrypt;
    @Autowired
    private NotificationRepo notificationRepo;
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private BuyerRepo buyerRepo;

    @Value("${webhook.jwt.token.username}")
    private String webhookUsername;
    private static final Logger logger = LogManager.getLogger(EmployeeServiceImpl.class);

    @Override
    public Result<?> register(EmployeeDTO dto) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            Employee employeeThatEnteredToSystem = (Employee) authentication.getPrincipal();
            if (employeeThatEnteredToSystem.getRole().equals(Role.ADMIN)) {
                if (dto.getRoleName().equals(Role.DIRECTOR.name()) || dto.getRoleName().equals(Role.ADMIN.name()) || dto.getRoleName().equals(Role.BUYER.name()))
                    return new Result<>(false, Messages.ADMIN_YOU_CANT_ADD_EMPLOYEE_ETC);
            }
        }
        Result<?> result = validatePhoneNumber(dto.getPhoneNumber());
        Employee employee = new Employee();
        if (!result.getSuccess()) return result;
        boolean roleNameMatch = false;
        for (int i = 0; i < Role.values().length; i++) {
            if (Role.values()[i].name().equals(dto.getRoleName().toUpperCase()))
                roleNameMatch = true;
        }
        if (!roleNameMatch) return new Result<>(false, Messages.ROLE_NAME_IS_WRONG_ETC);
        if (dto.getSalary() <= 0) return new Result<>(false, Messages.EMPLOYEE_SALARY_CAN_NOT_BE_ZEO_OR_MINUS);
        if (dto.getGender() != null) {
            boolean genderNameMatch = false;
            for (int i = 0; i < Gender.values().length; i++) {
                if (Gender.values()[i].name().equals(dto.getGender().toUpperCase()))
                    genderNameMatch = true;
            }
            if (!genderNameMatch) return new Result<>(false, Messages.GENDER_NAME_IS_WRONG_ETC);
        }
        if (dto.getPassword() == null) return new Result<>(false, Messages.PASSWORD_REQUIRED);
        else {
            result = validatePassword(dto.getPassword());
            if (!result.getSuccess()) return result;
            employee.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getCardNumber() != null && dto.getCardExpiredDate() != null) {
            result = validateCardDetails(dto.getCardNumber(), dto.getCardExpiredDate());
            if (!result.getSuccess()) return result;
            String cipheredCardNumber = encryptAndDecrypt.encryptCardDetails(dto.getCardNumber());
            String cipheredCardExpireDate = encryptAndDecrypt.encryptCardDetails(dto.getCardExpiredDate());
            employee.setCardNumber(cipheredCardNumber);
            employee.setCardExpireDate(cipheredCardExpireDate);
        }
        boolean existsByPhoneNumber = employeeRepo.existsByPhoneNumber(dto.getPhoneNumber());
        if (existsByPhoneNumber) return new Result<>(false, Messages.THIS_PHONE_NUMBER_BELONGS_TO_ANOTHER_USER);
        boolean existsBuyerByPhoneNumber = buyerRepo.existsByPhoneNumber(dto.getPhoneNumber());
        if (existsBuyerByPhoneNumber) return new Result<>(false, Messages.THIS_NUMBER_REGISTERED_AS_BUYER);
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setFirstname(capitalizeText(dto.getFirstname()));
        employee.setLastname(capitalizeText(dto.getLastname()));
        employee.setMiddleName(capitalizeText(dto.getMiddleName()));
        employee.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        employee.setBirthDate(Timestamp.valueOf(LocalDate.parse(dto.getBirthDate()).atStartOfDay()));
        employee.setRole(Role.valueOf(dto.getRoleName().toUpperCase()));
        employee.setSalary(dto.getSalary());
        employee.setAuthorities(authorityRepo.findAllByAuthorityIn(Role.valueOf(dto.getRoleName().toUpperCase()).getPermissions()));
        employee = employeeRepo.save(employee);
        if (dto.getEmail() != null) {
            boolean existsByEmail = employeeRepo.existsByEmail(dto.getEmail());
            if (existsByEmail) return new Result<>(false, Messages.THIS_EMAIL_BELONGS_TO_ANOTHER_USER);
            String uuid = UUID.randomUUID().toString();
            String emailContent = emailService.buildEmailForAuthorization(dto.getFirstname() + " " + dto.getLastname(), dto.getEmail(), "Authorization", uuid, false);
            emailService.sendEmail(emailContent, dto.getPhoneNumber(), "Authorization", dto.getEmail());
            ConfirmationToken token = ConfirmationToken.builder()
                    .code(uuid)
                    .employee(employee)
                    .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(5)))
                    .temporaryField(dto.getEmail())
                    .isBlocked(false)
                    .build();
            logger.info("Confirmation token saved for email and token sent to {} ", dto.getEmail());
            confirmationTokenRepo.save(token);
        }
        String verificationCode = generateVerificationCode();
        logger.info("SMS sent to {} ", dto.getPhoneNumber());
        twilioSmsSender.sendSms(dto.getPhoneNumber(), String.format(Messages.SEND_SMS_REGISTER, verificationCode));
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .employee(employee)
                .code(verificationCode)
                .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(3)))
                .isBlocked(false)
                .build();
        logger.info("Confirmation token saved and {} code sent to {} ", verificationCode, dto.getPhoneNumber());
        confirmationTokenRepo.save(confirmationToken);
        logger.info("New employee saved. ID: {} ", employee.getId());
        return new Result<>(true, Messages.EMPLOYEE_SAVED);
    }


    @Override
    public Result<?> confirmPhoneNumber(UserLoginDto dto) {
        Optional<ConfirmationToken> optional = confirmationTokenRepo.findByCodeAndEmployee_PhoneNumber(dto.getCode(), dto.getPhoneNumber());
        if (optional.isEmpty()) return new Result<>(false, Messages.THIS_CODE_WRONG);
        ConfirmationToken token = optional.get();

        LocalDateTime tokenExpiredDate = token.getExpiresAt().toLocalDateTime();
        if (tokenExpiredDate.isBefore(LocalDateTime.now()))
            return new Result<>(false, Messages.THIS_CODE_EXPIRED);
        if (token.getConfirmedAt() != null)
            return new Result<>(false, Messages.THIS_CODE_ALREADY_CONFIRMED);
        token.setConfirmedAt(Timestamp.valueOf(LocalDateTime.now()));
        confirmationTokenRepo.save(token);
        logger.info("Confirmation token confirmed for {} number ", dto.getPhoneNumber());
        Employee employee = token.getEmployee();
        employee.setIsActive(true);
        employeeRepo.save(employee);
        String jwtToken = jwtUtil.generateToken(dto.getPhoneNumber());
        return new Result<>(true, jwtToken);
    }

    @Override
    public Result<?> confirmEmail(String token) {
        Optional<ConfirmationToken> optional = confirmationTokenRepo.findByCode(token); // uuid is unique, therefore we checked confirmation token by uuid
        if (optional.isEmpty()) return new Result<>(false, Messages.THIS_TOKEN_IS_WRONG);
        ConfirmationToken confirmationToken = optional.get();
        LocalDateTime tokenExpireDate = confirmationToken.getExpiresAt().toLocalDateTime();
        if (confirmationToken.getConfirmedAt() != null)
            return new Result<>(false, Messages.EMAIL_ALREADY_CONFIRMED);
        if (tokenExpireDate.isBefore(LocalDateTime.now()))
            return new Result<>(false, Messages.TOKEN_EXPIRED);
        String email = confirmationToken.getTemporaryField();
        Employee employee = confirmationToken.getEmployee();
        employee.setEmail(email);
        employeeRepo.save(employee);
        confirmationToken.setConfirmedAt(Timestamp.valueOf(LocalDateTime.now()));
        confirmationTokenRepo.save(confirmationToken);
        logger.info("Email token confirmed for {} ", employee.getPhoneNumber());
        return new Result<>(true, Messages.EMAIL_CONFIRMED);
    }


    @Override
    public Result<?> login(UserLoginDto dto) {   // Employees only login via phone number and password, not phone number and verification code, because verification code isn't free.
        if (dto.getPassword() == null) return new Result<>(false, Messages.PASSWORD_REQUIRED);
        Optional<Employee> optional = employeeRepo.findByPhoneNumberAndIsActive(dto.getPhoneNumber(), true);
        if (optional.isEmpty()) return new Result<>(false, Messages.THIS_PHONE_NUMBER_NOT_FOUND);
        Employee employee = optional.get();
        boolean checkPassword = passwordEncoder.matches(dto.getPassword(), employee.getPassword());
        if (!checkPassword) return new Result<>(false, Messages.PASSWORD_IS_WRONG);
        logger.info("JWT token sent for {} to login ", dto.getPhoneNumber());
        String jwtToken = jwtUtil.generateToken(dto.getPhoneNumber());
        return new Result<>(true, jwtToken);
    }

    @Override
    public Result<?> getCodeToConfirmPhoneNumber(UserLoginDto dto) {
        return getCodeToRegisterOrChangePhoneNumber(dto.getPhoneNumber(), null);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> optional = employeeRepo.findByPhoneNumberAndIsActive(username, true);
        if (optional.isEmpty()) throw new UsernameNotFoundException("Such phone number not found.");
        return optional.get();
    }

    @Override
    public Result<?> getEmployees(String role, String page) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        int pageInt = Integer.parseInt(page);
        Pageable pageable = PageRequest.of(pageInt, 20, Sort.by("role").ascending());
        Page<Employee> employeePage;
        if (role.equals(Filter.ALL_EMPLOYEE)) {
            employeePage = employeeRepo.getAllEmployees(pageable);
        } else {
            employeePage = employeeRepo.getEmployeesByRole(Role.valueOf(role.toUpperCase()), pageable);
        }
//        for (Employee employee : employeePage) {   //WHEN PROJECT IS READY TO PRODUCTION THEN DO UNCOMMENT THIS CODE!
//            String cipheredCardNumber = employee.getCardNumber();
//            String cardNumber = encryptAndDecrypt.decryptCardDetail(cipheredCardNumber);
//            String secretCardNumber = "**** **** **"+cardNumber.substring(10,12)+" "+cardNumber.substring(12);
//            String secretCardExpireDate = "**/**";
//            employee.setCardNumber(secretCardNumber);
//            employee.setCardExpireDate(secretCardExpireDate);
//        }
        return new Result<>(true, employeePage);
    }


    @Override
    public Result<?> getById(Integer id) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Employee employeeThatEnteredToSystem = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Employee> optional = employeeRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
        Employee employee = optional.get();
//        String cipheredCardNumber = employee.getCardNumber();   //WHEN PROJECT IS READY TO PRODUCTION THEN DO UNCOMMENT THIS CODE!
//        String cardNumber = encryptAndDecrypt.decryptCardDetail(cipheredCardNumber);
//        String secretCardNumbers = "**** **** **";
//        secretCardNumbers = secretCardNumbers + cardNumber.substring(10, 12) + " " + cardNumber.substring(12);
//        employee.setCardNumber(secretCardNumbers);
//        employee.setCardExpireDate("**/**");
        if (employeeThatEnteredToSystem.getRole().equals(Role.DIRECTOR) || employeeThatEnteredToSystem.getRole().equals(Role.ADMIN)) {
            if (employeeThatEnteredToSystem.getId().equals(id)) return new Result<>(true, employee);
            if (employeeThatEnteredToSystem.getRole().equals(Role.ADMIN) && (employee.getRole().equals(Role.DIRECTOR) || employee.getRole().equals(Role.ADMIN)))
                return new Result<>(false, Messages.DEAR_ADMIN_YOU_CANT_SEE_DIRECTOR_ADMIN_ETC);
            else return new Result<>(true, employee);
        } else {
            if (employeeThatEnteredToSystem.getId().equals(id)) {
                return new Result<>(true, employee);
            } else return new Result<>(false, Messages.YOU_CANT_SEE_INFORMATION_OF_ANOTHER_EMPLOYEE);
        }
    }

    @Override
    public Result<?> getByFirstNameAndLastNameAndPhone(String name, String phone, String page) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException { // name like be : Javohir Quv, Jav, ...
        Employee employeeThatEnteredToSystem = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdminEntered = false;
        if (employeeThatEnteredToSystem.getRole().equals(Role.ADMIN)) {
            isAdminEntered = true;
        }
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
        String query = "SELECT emp FROM Employee AS emp WHERE emp.isActive=TRUE AND emp.role<>'DIRECTOR' ";
        String firstName;
        String lastName;
        int pageInt = Integer.parseInt(page);
        if (name != null) {
            if (name.split(" ").length > 1) {
                String[] names = name.split(" ");
                firstName = capitalizeText(names[0]);
                lastName = capitalizeText(names[1]);
                query = query.concat("AND (emp.firstname LIKE '" + firstName + "%' OR emp.firstname='" + firstName + "') AND (emp.lastname LIKE '" + lastName + "%' OR emp.lastname='" + lastName + "') ");
            } else {
                firstName = capitalizeText(name);
                query = query.concat("AND (emp.firstname LIKE '" + firstName + "%' OR emp.firstname='" + firstName + "') ");
            }
        }
        if (phone != null) {
            phone = "+".concat(phone);
            query = query.concat("AND (emp.phoneNumber LIKE '" + phone + "%' OR emp.phoneNumber='" + phone + "') ");
        }
        if (isAdminEntered)
            query = query.concat(" AND emp.role<>'ADMIN' ");
        query = query.concat(" ORDER BY emp.role ASC ");
        Query createQuery = entityManager.createQuery(query).setFirstResult(pageInt * 20).setMaxResults(20);
        logger.info(query);
        List<Employee> employees = createQuery.getResultList();
        for (Employee employee : employees) {
            String cipheredCardNumber = employee.getCardNumber();
            String cardNumber = encryptAndDecrypt.decryptCardDetail(cipheredCardNumber);
            String secretCardNumber = "**** **** **" + cardNumber.substring(10, 12) + " " + cardNumber.substring(12);
            String secretCardExpireDate = "**/**";
            employee.setCardNumber(secretCardNumber);
            employee.setCardExpireDate(secretCardExpireDate);
        }
        return new Result<>(true, employees);

    }

    @Override
    public Result<?> edit(Integer id, EmployeeDTO dto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        Optional<Employee> optional = employeeRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
        Employee employee = optional.get();
        Employee employeeThatEnteredToSystem = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (employeeThatEnteredToSystem.getRole().equals(Role.ADMIN) && employee.getRole().equals(Role.ADMIN) && !employeeThatEnteredToSystem.getId().equals(employee.getId()))
            return new Result<>(false, String.format(Messages.ADMIN_YOU_CANT_CHANGE_INFORMATION_OF_ANOTHER_ADMIN, employeeThatEnteredToSystem.getFirstname()));
        if (employeeThatEnteredToSystem.getRole().equals(Role.ADMIN) && employee.getRole().equals(Role.DIRECTOR))
            return new Result<>(false, String.format(Messages.ADMIN_YOU_CANT_CHANGE_INFORMATION_OF_DIRECTOR, employeeThatEnteredToSystem.getFirstname()));
        if (!employeeThatEnteredToSystem.getRole().equals(Role.DIRECTOR) && !employeeThatEnteredToSystem.getRole().equals(Role.ADMIN)) {
            if (!employeeThatEnteredToSystem.getId().equals(employee.getId()))
                return new Result<>(false, String.format(Messages.YOU_CANT_CHANGE_INFORMATION_OF_ANOTHER_EMPLOYEE, employeeThatEnteredToSystem.getFirstname()));
        }
        Result<?> result;
        if (dto.getPassword() != null) {
            result = validatePassword(dto.getPassword());
            if (!result.getSuccess()) return result;
            boolean checkPassword = passwordEncoder.matches(dto.getPassword(), employee.getPassword());
            if (!checkPassword) employee.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        //        if (dto.getCardNumber() != null && dto.getCardExpiredDate() != null) {  // WHEN PROJECT IS READY TO PRODUCTION THEN DO UNCOMMENT THIS CODE!
//            result = validateCardDetails(dto.getCardNumber(), dto.getCardExpiredDate());
//            if (!result.getSuccess()) return result;
//            String oldCardNumber = encryptAndDecrypt.decryptCardDetail(employee.getCardNumber());
//            if (!oldCardNumber.equals(dto.getCardNumber())) {
//                employee.setCardNumber(encryptAndDecrypt.encryptCardDetails(dto.getCardNumber()));
//                employee.setCardExpireDate(encryptAndDecrypt.encryptCardDetails(dto.getCardExpiredDate()));
//            }
//        }
        if (dto.getEmail() != null && !dto.getEmail().equals(employee.getEmail())) {
            boolean isExistsByEmail = employeeRepo.existsByEmail(dto.getEmail());
            if (isExistsByEmail) return new Result<>(false, Messages.THIS_EMAIL_BELONGS_TO_ANOTHER_USER);
            String uuid = UUID.randomUUID().toString();
            String emailContent = emailService.buildEmailForChangingEmail(dto.getFirstname() + " " + dto.getLastname(), dto.getEmail(), "Request to change email", uuid, false);
            emailService.sendEmail(emailContent, employee.getPhoneNumber(), "Request to change email", dto.getEmail());
            ConfirmationToken confirmationToken = ConfirmationToken.builder()
                    .employee(employee)
                    .code(uuid)
                    .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(5)))
                    .temporaryField(dto.getEmail())
                    .isBlocked(false)
                    .build();
            confirmationTokenRepo.save(confirmationToken);
        }
        if (!employee.getPhoneNumber().equals(dto.getPhoneNumber())) {
            String newPhoneNumber = dto.getPhoneNumber();
            result = validatePhoneNumber(newPhoneNumber);
            if (!result.getSuccess()) return result;
            boolean isExistsByPhoneNumber = employeeRepo.existsByPhoneNumber(newPhoneNumber);
            if (isExistsByPhoneNumber) return new Result<>(false, Messages.THIS_PHONE_NUMBER_BELONGS_TO_ANOTHER_USER);
            result = getCodeToRegisterOrChangePhoneNumber(employee.getPhoneNumber(), newPhoneNumber);
            if (!result.getSuccess()) return result;
        }
        employee.setFirstname(capitalizeText(dto.getFirstname()));
        employee.setLastname(capitalizeText(dto.getLastname()));
        employee.setMiddleName(capitalizeText(dto.getMiddleName()));
        employee.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        employee.setBirthDate(Timestamp.valueOf(LocalDate.parse(dto.getBirthDate()).atStartOfDay()));
        Role newRole = Role.valueOf(dto.getRoleName().toUpperCase());
        if ((employeeThatEnteredToSystem.getRole().equals(Role.ADMIN) && !newRole.equals(Role.ADMIN) && !newRole.equals(Role.DIRECTOR)) || employeeThatEnteredToSystem.getRole().equals(Role.DIRECTOR)) {
            employee.setRole(Role.valueOf(dto.getRoleName().toUpperCase()));
            Set<Authority> authorities = authorityRepo.findAllByAuthorityIn(newRole.getPermissions());
            employee.setAuthorities(authorities);
        }
        if ((employeeThatEnteredToSystem.getRole().equals(Role.ADMIN) && !employeeThatEnteredToSystem.getId().equals(employee.getId())) || employeeThatEnteredToSystem.getRole().equals(Role.DIRECTOR)) {
            employee.setSalary(dto.getSalary());
        }
        logger.info("Employee updated. ID: {} ", employee.getId());
        employeeRepo.save(employee);
        return new Result<>(true, Messages.EMPLOYEE_UPDATED);
    }

    @Override
    public Result<?> getCodeToConfirmNewPhoneNumber(UserPhonesDto dto) {
        return getCodeToRegisterOrChangePhoneNumber(dto.getPhoneNumber(), dto.getNewPhoneNumber());
    }

    public Result<?> getCodeToRegisterOrChangePhoneNumber(String phoneNumber, String newPhoneNumber) {
        Result<?> result = validatePhoneNumber(phoneNumber);
        if (!result.getSuccess()) return result;
        boolean isUserRegistrationOrLogin = confirmationTokenRepo.isEmployeeRegistrationOrLogin(phoneNumber);
        Optional<Employee> optional;
        if (isUserRegistrationOrLogin) {   // if isUserRegistrationOrLogin is true, then user is logging in, otherwise user is registering.
            optional = employeeRepo.findByPhoneNumberAndIsActive(phoneNumber, true);
            if (optional.isEmpty()) return new Result<>(false, Messages.YOU_DELETED_IN_SYSTEM);
        } else {
            optional = employeeRepo.findByPhoneNumber(phoneNumber);
            if (optional.isEmpty()) return new Result<>(false, Messages.PHONE_NUMBER_WRONG);
        }
        Employee employee = optional.get();
        List<ConfirmationToken> tokenList;
        if (newPhoneNumber != null)
            tokenList = confirmationTokenRepo.getLastTokensByNewPhoneNumber(newPhoneNumber);
        else
            tokenList = confirmationTokenRepo.getLastTokensByEmployeeId(employee.getId());
        if (tokenList.size() == 3 && tokenList.get(0).getIsBlocked()) {
            LocalDateTime firstTokenCreatedAt = tokenList.get(0).getCreatedAt().toLocalDateTime();
            if (firstTokenCreatedAt.plusMinutes(15).isAfter(LocalDateTime.now())) {
                ConfirmationToken firstToken = tokenList.get(0);
                firstToken.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                confirmationTokenRepo.save(firstToken);
                return new Result<>(false, Messages.TOO_MANY_ATTEMPTS_ETC);
            } else {
                ConfirmationToken firstToken = tokenList.get(0);
                firstToken.setIsBlocked(false);
                confirmationTokenRepo.save(firstToken);
                sendSmsAndSaveNewToken(phoneNumber, employee, newPhoneNumber);
                return new Result<>(true, Messages.NEW_CODE_SENT);
            }
        } else if (tokenList.size() == 3) {
            LocalDateTime firstTokenCreatedAt = tokenList.get(0).getCreatedAt().toLocalDateTime();
            LocalDateTime thirdTokenCreatedAt = tokenList.get(2).getCreatedAt().toLocalDateTime();
            if (thirdTokenCreatedAt.plusMinutes(5).isAfter(firstTokenCreatedAt)) {
                ConfirmationToken firstToken = tokenList.get(0);
                firstToken.setIsBlocked(true);
                confirmationTokenRepo.save(firstToken);
                return new Result<>(false, Messages.TOO_MANY_ATTEMPTS_ETC);
            } else {
                sendSmsAndSaveNewToken(phoneNumber, employee, newPhoneNumber);
            }
        } else {
            sendSmsAndSaveNewToken(phoneNumber, employee, newPhoneNumber);
        }
        return new Result<>(true, Messages.NEW_CODE_SENT);
    }

    private void sendSmsAndSaveNewToken(String phoneNumber, Employee employee, String newPhoneNumber) {
        String verificationCode = generateVerificationCode();
        if (newPhoneNumber == null) {
            twilioSmsSender.sendSms(phoneNumber, String.format(Messages.SEND_SMS_REGISTER, verificationCode));
            logger.info("Confirmation token saved and {} code sent to {} ", verificationCode, phoneNumber);
        } else {
            twilioSmsSender.sendSms(newPhoneNumber, String.format(Messages.SEND_SMS_TO_CHANGE_PHONE_NUMBER, verificationCode));
            logger.info("Confirmation token saved and {} code sent to {} for change phone number.", verificationCode, newPhoneNumber);
        }
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .employee(employee)
                .code(verificationCode)
                .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(3)))
                .temporaryField(newPhoneNumber)
                .isBlocked(false)
                .build();
        confirmationTokenRepo.save(confirmationToken);
    }


    @Override
    public Result<?> confirmNewPhoneNumberToEdit(UserLoginDto dto) {
        Optional<ConfirmationToken> optional = confirmationTokenRepo.findByCodeAndTemporaryField(dto.getCode(), dto.getPhoneNumber());
        if (optional.isEmpty()) return new Result<>(false, Messages.THIS_CODE_WRONG);
        ConfirmationToken token = optional.get();
        if (token.getConfirmedAt() != null) return new Result<>(false, Messages.PHONE_NUMBER_ALREADY_CHANGED);
        LocalDateTime tokenExpireDate = token.getExpiresAt().toLocalDateTime();
        if (tokenExpireDate.isBefore(LocalDateTime.now()))
            return new Result<>(false, Messages.THIS_CODE_EXPIRED);
        String newPhoneNumber = token.getTemporaryField();
        Employee employee = token.getEmployee();
        employee.setPhoneNumber(newPhoneNumber);
        employeeRepo.save(employee);
        token.setConfirmedAt(Timestamp.valueOf(LocalDateTime.now()));
        confirmationTokenRepo.save(token);
        logger.info("Confirmation token confirmed for {} cause: change phone number", dto.getPhoneNumber());
        return new Result<>(true, Messages.PHONE_NUMBER_UPDATED);
    }

    public void payStaffSalaries(String jwtToken) throws Exception { // on the 10th of every month.
        String username = jwtUtil.getUsernameFromToken(jwtToken);
        if (username == null || !username.equals(webhookUsername))
            throw new Exception("Hey you, don't do that again. If you do that again, you will be in trouble. Believe me!");
        Optional<Employee> optionalEmployee = employeeRepo.findById(1);
        Employee director = optionalEmployee.get();
        int monthOfCurrentMonth = LocalDateTime.now().getMonthValue();
        int yearOfCurrentMonth = LocalDateTime.now().getYear();
        LocalDateTime startOfCurrentMonth = LocalDateTime.of(yearOfCurrentMonth, monthOfCurrentMonth, 1, 0, 0);
        Integer amountOfAllEmployees = employeeRepo.getAmountOfAllEmployees(Timestamp.valueOf(startOfCurrentMonth));
        int pageAmount = (int) Math.ceil((float) amountOfAllEmployees / 20);
        Pageable pageable;
        for (int i = 0; i < pageAmount; i++) {
            pageable = PageRequest.of(i, 20, Sort.by("salary").ascending());
            Page<Employee> employees = employeeRepo.findByCreatedDate(Timestamp.valueOf(startOfCurrentMonth), pageable);
            for (Employee employee : employees) {
                LocalDateTime employeeCreatedDate = employee.getCreatedDate().toLocalDateTime();
                LocalDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);
                int yearOfPreviousMonth = startOfPreviousMonth.getYear();
                String monthOfPreviousMonth = startOfPreviousMonth.getMonth().toString();
                int amountAllDaysOfPreviousMonth;
                if (Year.isLeap(yearOfPreviousMonth))
                    amountAllDaysOfPreviousMonth = startOfPreviousMonth.getMonth().maxLength();
                else amountAllDaysOfPreviousMonth = startOfPreviousMonth.getMonth().minLength();
                String employeeCardNumber = encryptAndDecrypt.decryptCardDetail(employee.getCardNumber());
                String employeeCardExpireDate = encryptAndDecrypt.decryptCardDetail(employee.getCardExpireDate());
                String directorCardNumber = encryptAndDecrypt.decryptCardDetail(director.getCardNumber());
                String directorCardExpireDate = encryptAndDecrypt.decryptCardDetail(director.getCardExpireDate());
                Integer directorCardBalance = sendDataToPaypalToCheckCardBalance(directorCardNumber, directorCardExpireDate);
                if (employeeCreatedDate.isBefore(startOfPreviousMonth)) {
                    if (directorCardBalance < employee.getSalary()) {
                        warningAboutThatMoneyIsNotEnough(director, startOfPreviousMonth, startOfCurrentMonth, yearOfPreviousMonth, monthOfPreviousMonth, amountAllDaysOfPreviousMonth);
                        return;
                    } else {
                        sendDataToPaypalToWithdrawMoney(directorCardNumber, directorCardExpireDate, employeeCardNumber, employeeCardExpireDate, employee.getSalary());
                        Transaction transaction = Transaction.builder()
                                .fromCardNumber(director.getCardNumber())
                                .fromCardExpireDate(director.getCardExpireDate())
                                .toCardNumber(employee.getCardNumber())
                                .toCardExpireDate(employee.getCardExpireDate())
                                .employee(employee)
                                .amountOfMoney(employee.getSalary())
                                .forWhichMonth(monthOfPreviousMonth)
                                .forWhichYear(yearOfPreviousMonth)
                                .createdDate(Timestamp.valueOf(LocalDateTime.now()))
                                .causeOfTransaction(CauseOfTransaction.PAY_EMPLOYEE_SALARY)
                                .build();
                        transactionRepo.save(transaction);
                    }
                } else {
                    Period period = Period.between(employeeCreatedDate.toLocalDate(), startOfCurrentMonth.toLocalDate());
                    int amountOfWorkedDaysInPreviousMonth = period.getDays();
                    int amountOfMoneyToBePaidToEmployee = Math.round((float) employee.getSalary() / amountAllDaysOfPreviousMonth * (amountOfWorkedDaysInPreviousMonth - 1));
                    if (directorCardBalance < amountOfMoneyToBePaidToEmployee) {
                        warningAboutThatMoneyIsNotEnough(director, startOfPreviousMonth, startOfCurrentMonth, yearOfPreviousMonth, monthOfPreviousMonth, amountAllDaysOfPreviousMonth);
                        return;
                    } else {
                        sendDataToPaypalToWithdrawMoney(directorCardNumber, directorCardExpireDate, employeeCardNumber, employeeCardExpireDate, employee.getSalary());
                        Transaction transaction = Transaction.builder()
                                .fromCardNumber(director.getCardNumber())
                                .fromCardExpireDate(director.getCardExpireDate())
                                .toCardNumber(employee.getCardNumber())
                                .toCardExpireDate(employee.getCardExpireDate())
                                .employee(employee)
                                .amountOfMoney(amountOfMoneyToBePaidToEmployee)
                                .forWhichMonth(monthOfPreviousMonth)
                                .forWhichYear(yearOfPreviousMonth)
                                .createdDate(Timestamp.valueOf(LocalDateTime.now()))
                                .causeOfTransaction(CauseOfTransaction.PAY_EMPLOYEE_SALARY)
                                .build();
                        transactionRepo.save(transaction);
                    }
                }
            }
        }
    }

    private void warningAboutThatMoneyIsNotEnough(Employee director, LocalDateTime startOfPreviousMonth, LocalDateTime startOfCurrentMonth, int yearOfPreviousMonth, String monthOfPreviousMonth, int amountAllDaysOfPreviousMonth) throws IOException {
        Integer amountOfEmployeesThatArePaidSalaries = transactionRepo.getAmountOfEmployeesThatArePaidSalaries(yearOfPreviousMonth, monthOfPreviousMonth);
        Integer amountOfPaidMoney = transactionRepo.getAmountOPaidMoney(yearOfPreviousMonth, monthOfPreviousMonth);
        Integer amountOfAllEmployees = employeeRepo.getAmountOfAllEmployees(Timestamp.valueOf(startOfCurrentMonth));
        Integer amountOfAllMoneyToBePaid = employeeRepo.getAmountOfAllMoneyToBePaid(Timestamp.valueOf(startOfPreviousMonth), Timestamp.valueOf(startOfCurrentMonth), amountAllDaysOfPreviousMonth);
        int amountOfEmployeesThatAreNotPaidSalaries = amountOfAllEmployees - amountOfEmployeesThatArePaidSalaries;
        int amountOfMoneyToBePaid = amountOfAllMoneyToBePaid - amountOfPaidMoney;
        Notification notification = Notification.builder()
                .causeOfNotification("Money isn't enough to pay employees salaries.")
                .message(String.format(Messages.NOTIFICATION_MESSAGE, amountOfEmployeesThatAreNotPaidSalaries, amountOfMoneyToBePaid))
                .notificationType(NotificationType.FIRE)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        notificationRepo.save(notification);
        List<Employee> admins = employeeRepo.findAllByRole(Role.ADMIN);
        for (Employee admin : admins) {
            String emailContent = emailService.buildEmailForWarningAboutPaySalaries(admin.getFirstname() + " " + admin.getLastname(), admin.getEmail(), "FIRE! Money isn't enough to pay salaries.", amountOfEmployeesThatAreNotPaidSalaries, amountOfMoneyToBePaid, yearOfPreviousMonth, monthOfPreviousMonth, false);
            emailService.sendEmail(emailContent, admin.getPhoneNumber(), "Money isn't enough to pay salaries.", admin.getEmail());
//            twilioSmsSender.sendSms(admin.getPhoneNumber(), String.format(Messages.MESSAGE_FOR_SMS, admin.getFirstname(), "Director", amountOfEmployeesThatAreNotPaidSalaries, amountOfMoneyToBePaid));
            logger.info("Email sent to {} for warning about that money isn't enough to pay salaries. ", admin.getPhoneNumber());
            logger.info("SMS sent to {} for warning about that money isn't enough to pay salaries. ", admin.getPhoneNumber());
        }
        String emailContent = emailService.buildEmailForWarningAboutPaySalaries(director.getFirstname() + " " + director.getLastname(), director.getEmail(), "FIRE! Money isn't enough to pay salaries.", amountOfEmployeesThatAreNotPaidSalaries, amountOfMoneyToBePaid, yearOfPreviousMonth, monthOfPreviousMonth, true);
        emailService.sendEmail(emailContent, director.getPhoneNumber(), "Money isn't enough to pay salaries.", director.getEmail());
//        twilioSmsSender.sendSms(director.getPhoneNumber(), String.format(Messages.MESSAGE_FOR_SMS, director.getFirstname(), "Admins", amountOfEmployeesThatAreNotPaidSalaries, amountOfMoneyToBePaid));
        logger.info("Email sent to {} for warning about that money isn't enough to pay salaries. ", director.getPhoneNumber());
        logger.info("SMS sent to {} for warning about that money isn't enough to pay salaries. ", director.getPhoneNumber());
    }

    @Override
    public Result<?> unblock(Integer id) {
        Optional<Employee> optional = employeeRepo.findByIdAndIsActive(id, false);
        if (optional.isEmpty()) return new Result<>(false, Messages.THIS_USER_NOT_FOUND_OR_THIS_WASNT_DELETED);
        Employee employee = optional.get();
        Employee employeeThatEnteredToSystem = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (employeeThatEnteredToSystem.getRole().equals(Role.ADMIN) && (employee.getRole().equals(Role.DIRECTOR) || employee.getRole().equals(Role.ADMIN)))
            return new Result<>(false, String.format(Messages.ADMIN_YOU_CANT_RECOVER_EMPLOYEE_TO_SYSTEM, employeeThatEnteredToSystem.getFirstname()));
        employeeRepo.unblock(id);
        logger.info("Employee unblocked. ID: {} ", employee.getId());
        return new Result<>(true, Messages.EMPLOYEE_RESTORED);
    }

    @Override
    public Result<?> block(Integer id) {
        Optional<Employee> optional = employeeRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_USER_ID_NOT_EXIST);
        Employee employee = optional.get();
        Employee employeeThatEnteredToSystem = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (employeeThatEnteredToSystem.getRole().equals(Role.ADMIN) && (employee.getRole().equals(Role.DIRECTOR) || employee.getRole().equals(Role.ADMIN)))
            return new Result<>(false, String.format(Messages.ADMIN_YOU_CANT_DELETE_EMPLOYEE_FROM_SYSTEM, employeeThatEnteredToSystem.getFirstname()));
        employeeRepo.block(id);
        logger.info("Employee blocked. ID: {} ", employee.getId());
        return new Result<>(true, Messages.EMPLOYEE_DELETED);
    }

}
