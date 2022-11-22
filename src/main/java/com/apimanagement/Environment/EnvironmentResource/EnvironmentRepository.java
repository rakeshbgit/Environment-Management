package com.apimanagement.Environment.EnvironmentResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Integer> {


    @Override
    Environment getById(Integer integer);



}
