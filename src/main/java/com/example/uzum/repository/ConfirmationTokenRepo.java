package com.example.uzum.repository;

import com.example.uzum.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepo extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByCodeAndBuyer_PhoneNumber(String code, String buyer_phoneNumber);

    Optional<ConfirmationToken> findByCodeAndEmployee_PhoneNumber(String code, String employee_phoneNumber);

    Optional<ConfirmationToken> findByCodeAndTemporaryField(String code, String temporaryField);

    Optional<ConfirmationToken> findByCode(String code);

    @Query(value = "SELECT ct.id, ct.code, ct.created_at, ct.expires_at, ct.buyer_id, ct.employee_id, ct.confirmed_at, ct.is_blocked, ct.temporary_field FROM confirmation_token AS ct INNER JOIN buyer AS buy ON ct.buyer_id=buy.id WHERE buy.phone_number=:phoneNumber ORDER BY ct.created_at DESC LIMIT 3 ", nativeQuery = true)
    List<ConfirmationToken> getLastTokensByBuyerPhoneNumber(String phoneNumber);

    @Query(value = "SELECT * FROM confirmation_token AS ct WHERE ct.temporary_field=:newPhoneNumber ORDER BY ct.created_at DESC LIMIT 3", nativeQuery = true)
    List<ConfirmationToken> getLastTokensByNewPhoneNumber(String newPhoneNumber);


    @Query(value = "SELECT * FROM confirmation_token AS ct WHERE ct.employee_id=:employeeId AND ct.temporary_field IS NULL ORDER BY ct.created_at DESC LIMIT 3", nativeQuery = true)
    List<ConfirmationToken> getLastTokensByEmployeeId(Integer employeeId);

    @Query(value = "SELECT COUNT(*) > 0 FROM ConfirmationToken AS ct WHERE ct.buyer.phoneNumber=:phoneNumber AND ct.confirmedAt IS NOT NULL ")
    boolean isBuyerRegistrationOrLogin(String phoneNumber);
   @Query(value = "SELECT COUNT(*) > 0 FROM ConfirmationToken AS ct WHERE ct.employee.phoneNumber=:phoneNumber AND ct.confirmedAt IS NOT NULL ")
    boolean isEmployeeRegistrationOrLogin(String phoneNumber);

}
