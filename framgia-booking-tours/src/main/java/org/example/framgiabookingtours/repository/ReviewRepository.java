package org.example.framgiabookingtours.repository;

import org.example.framgiabookingtours.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);

    @Query("SELECT r FROM Review r " +
           "JOIN r.booking b " +
           "WHERE b.tour.id = :tourId " +
           "AND r.isDeleted = false " +
           "ORDER BY r.createdAt DESC")
    Page<Review> findByTourId(@Param("tourId") Long tourId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r " +
           "JOIN r.booking b " +
           "WHERE b.tour.id = :tourId " +
           "AND r.isDeleted = false")
    Double getAverageRatingByTourId(@Param("tourId") Long tourId);
}
