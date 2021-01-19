package com.dsoft.m2u.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dsoft.m2u.domain.Facility;
import com.dsoft.m2u.domain.Order;
import com.dsoft.m2u.domain.OrderStatus;
/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order>{

    Page<Order> findAllByActive(Pageable pageable, boolean active);
    
    Optional<Order> findByIdAndActive(String id, boolean active);
    
    Optional<Order> findByOrderNoAndActive(String orderNo, boolean active);
    
    @Query(value = "SELECT * FROM orders WHERE DATE(date_display) = CURDATE() and facility_id = :facilityId and active = true", nativeQuery = true)
    List<Order> getOrderByDateAndFacility(@Param("facilityId") String facilityId);
    
    @Query(value = "SELECT * FROM orders WHERE DATE(date_display) between DATE(date_start) and DATE(date_end) and type = 'B2B' and active = true", nativeQuery = true)
    List<Order> getOrderByDateDisplay(@Param("date_display") String dateDisplay);
    
    @Query(value = "SELECT * FROM orders WHERE DATE(:date_display) between DATE(date_start) and DATE(date_end) and type = 'B2B' and facility_id = :facilityId and active = true", nativeQuery = true)
    List<Order> getOrderByDateDisplayAndFacilityId(@Param("date_display") String dateDisplay, @Param("facilityId") String facilityId);
	
	@Query(value = "SELECT * FROM orders WHERE DATE(date_display) = CURDATE() and active = true", nativeQuery = true)
	List<Order> getOrderByDate();

    Boolean existsByFacilityIdAndActive(String facilityId, boolean active);
    
    Integer countByActive(boolean active);

    List<Order> findAllByActive(boolean active);
    
    Page<Order> findByActiveAndCreatedBy(Pageable pageable, boolean active, String createdBy);
    
    List<Order> findByActiveAndCreatedBy(boolean active, String createdBy);
    
    Optional<Order> findByActiveAndIdAndCreatedBy(boolean active, String orderId, String createdBy);
    
    List<Order> findByIdAndActiveAndFacilityIn(String orderId, boolean active, Set<Facility> facilities);
       
    List<Order> findByStatusAndFacilityIn(OrderStatus orderStatus, Set<Facility> facilities);
    
    Page<Order> findByStatusAndFacilityIn(Pageable pageable,OrderStatus orderStatus, Set<Facility> facilities);
    
    @Query(value = "SELECT a.* FROM orders a INNER JOIN facilities b ON a.facility_id = b.id WHERE b.review_by = :reviwerId and a.active = true", nativeQuery = true)
    List<Order> getAllOrderByReviewerId(@Param("reviwerId") String reviwerId);
    
    @Query(value = "SELECT a.* FROM orders a INNER JOIN facilities b ON a.facility_id = b.id WHERE b.review_by = :reviwerId and a.active = true", nativeQuery = true)
    Page<Order> getAllOrderByReviewerId(Pageable pageable, @Param("reviwerId") String reviwerId);
    
	@Query(value = "SELECT * FROM orders WHERE ADDTIME(created_at,1000)<= CURRENT_TIMESTAMP() AND active = TRUE  AND status = 'NEW'", nativeQuery = true)
	List<Order> getOrderTimeout();
}