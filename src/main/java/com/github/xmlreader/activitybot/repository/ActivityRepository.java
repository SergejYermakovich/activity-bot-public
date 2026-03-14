package com.github.xmlreader.activitybot.repository;

import com.github.xmlreader.activitybot.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {
    
    List<Activity> findByCategoryIdAndIsActiveTrue(Long categoryId);
    
    List<Activity> findByLocationContainingIgnoreCaseAndIsActiveTrue(String location);
    
    List<Activity> findByIsActiveTrue();
}
