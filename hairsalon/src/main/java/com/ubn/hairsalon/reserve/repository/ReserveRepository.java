package com.ubn.hairsalon.reserve.repository;

import com.ubn.hairsalon.reserve.constant.ReserveStatus;
import com.ubn.hairsalon.reserve.constant.ServiceStatus;
import com.ubn.hairsalon.reserve.entity.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReserveRepository extends JpaRepository<Reserve, Long>, QuerydslPredicateExecutor<Reserve>, ReserveRepositoryCustom {
    List<Reserve> findReservesByRsvDateOrderByIdAsc(LocalDate rsvDate) throws Exception;

    @Modifying
    @Query("update Reserve r set r.reserveStatus = :status where r.id = :id")
    int updateReserveStatusById(@Param("status") ReserveStatus reserveStatus, @Param("id") Long id);

    @Modifying
    @Query("update Reserve r set r.serviceStatus = :status where r.id = :id")
    int updateServiceStatusById(@Param("status") ServiceStatus serviceStatus, @Param("id") Long id);

    @Modifying
    @Query("update Reserve r set r.reviewYn = :reviewYn where r.id = :id")
    int updateReviewYnById(@Param("reviewYn") String reviewYn, @Param("id") Long id);

}
