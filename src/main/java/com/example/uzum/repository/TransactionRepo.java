package com.example.uzum.repository;

import com.example.uzum.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
    @Query(value = "SELECT SUM(trs.amountOfMoney) FROM Transaction AS trs WHERE trs.forWhichYear=:year AND trs.forWhichMonth=:month")
    Integer getAmountOPaidMoney(int year, String month);

    @Query(value = "SELECT COUNT(trs) FROM Transaction AS trs WHERE trs.forWhichYear=:year AND trs.forWhichMonth=:month")
    Integer getAmountOfEmployeesThatArePaidSalaries(int year, String month);
}
