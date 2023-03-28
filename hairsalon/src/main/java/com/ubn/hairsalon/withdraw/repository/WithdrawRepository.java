package com.ubn.hairsalon.withdraw.repository;

import com.ubn.hairsalon.withdraw.entity.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
}
