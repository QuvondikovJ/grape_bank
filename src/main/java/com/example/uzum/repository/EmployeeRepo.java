package com.example.uzum.repository;

import com.example.uzum.entity.Employee;
import com.example.uzum.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer> {

    @Query(value = "SELECT emp FROM Employee AS emp WHERE emp.isActive=TRUE AND emp.role<>'DIRECTOR'")
    Page<Employee> getAllEmployees(Pageable pageable);

    @Query(value = "SELECT emp FROM Employee AS emp WHERE emp.isActive=TRUE AND emp.role<>'DIRECTOR' AND emp.role=:role AND emp.createdDate<CURRENT_TIMESTAMP ")
    Page<Employee> getEmployeesByRole(Role role, Pageable pageable);

    Optional<Employee> findByPhoneNumberAndIsActive(String phoneNumber, Boolean isActive);

    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Integer id);

    Optional<Employee> findByPhoneNumber(String phoneNumber);

    Optional<Employee> findByIdAndIsActive(Integer id, Boolean isActive);

    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Employee AS emp SET emp.isActive=FALSE WHERE emp.id=:id ")
    void block(Integer id);

    @Query(value = "SELECT COUNT(emp) FROM Employee AS emp WHERE emp.isActive=TRUE AND emp.role<>'DIRECTOR' AND emp.createdDate<:currentMonth ")
    Integer getAmountOfAllEmployees(Timestamp currentMonth);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Employee AS emp SET emp.isActive=TRUE WHERE emp.id=:id ")
    void unblock(Integer id);

    @Query(value = "SELECT emp FROM Employee AS emp WHERE emp.createdDate<:currentMonth AND emp.isActive=TRUE AND emp.role<>'DIRECTOR' ")
    Page<Employee> findByCreatedDate(Timestamp currentMonth, Pageable pageable);
    List<Employee> findAllByRole(Role role);

    @Query(value = "SELECT SUM(CASE WHEN emp.created_date<:startOfPreviousMonth THEN emp.salary WHEN emp.created_date>=:startOfPreviousMonth AND emp.created_date<:startOfCurrentMonth THEN emp.salary/:amountAllDaysOfMonth * (SELECT EXTRACT(DAY FROM :startOfCurrentMonth-emp.created_date)) END) FROM employee AS emp WHERE emp.is_active=TRUE AND emp.role<>'DIRECTOR' ", nativeQuery = true)
    Integer getAmountOfAllMoneyToBePaid(Timestamp startOfPreviousMonth, Timestamp startOfCurrentMonth, Integer amountAllDaysOfMonth);

    @Query(value = "SELECT emp FROM Employee AS emp WHERE emp.id IN :sellerIDs AND emp.isActive=TRUE AND emp.role='SELLER' ")
    Set<Employee> findAllByIdAndIsActive(List<Integer> sellerIDs);
}
