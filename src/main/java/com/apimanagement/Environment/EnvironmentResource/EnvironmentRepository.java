package com.apimanagement.Environment.EnvironmentResource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Integer> {
    @Override
    Optional<Environment> findById(Integer integer);

    @Override
    Environment getById(Integer integer);


}
