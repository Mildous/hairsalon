package com.ubn.hairsalon.review.repository;

import com.ubn.hairsalon.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ReviewRepository extends JpaRepository<Review, Long>, QuerydslPredicateExecutor<Review>, ReviewRepositoryCustom {

}
