package com.apimanagement.Environment.repository;
import com.apimanagement.Environment.model.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Integer> {


    @Override
    Environment getById(Integer integer);
    @Query("select id from Environment env where env.envName = :envi")
    Integer getByEnvName(@Param("envi") String environment);

    @Query("select case when count(e) >0 then true else false end from Environment e where envName = :envi")
    boolean existByEnvName(@Param("envi")String environment);



}
