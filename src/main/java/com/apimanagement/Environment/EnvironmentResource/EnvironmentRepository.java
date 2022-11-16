package com.apimanagement.Environment.EnvironmentResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Integer> {
    @Override

    Optional<Environment> findById(Integer integer);


    @Transactional
    @Modifying
    @Query( value = "UPDATE environment SET status = 'AVAILABLE' WHERE CONVERT(busy_till,DATE) < CURRENT_TIMESTAMP()",nativeQuery = true)
    void update();


    @Query(value = "SELECT * from environment e WHERE e.status ='AVAILABLE'",nativeQuery = true)
    List<Environment> findAllAvailableEnvironments();

    @Override
    Environment getById(Integer integer);



}
