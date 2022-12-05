package com.apimanagement.Environment.repository;

import com.apimanagement.Environment.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;;
import javax.transaction.Transactional;
import java.util.List;

public interface ServiceRepository extends JpaRepository<Service,Integer> {
  // HQL QUERY to get all Services for the respective Environment ID
  @Query(value = "select s from Service s where environment_id  = :enid")
    List<Service>allByEnvId(@Param("enid") Integer enid);

  // Native SQL QUERY to get all Services for the respective Environment ID
  @Query(value = "select s from Service s where s.status = 'AVAILABLE' AND  environment_id  = :enid")
  List<Service>allByAvailable(@Param("enid") Integer enid);


  //HQL QUERY to delete the relation of the environment and its services from the environment services
  @Transactional
  @Modifying
  @Query( value = "delete from Service s where  environment_id = :enid and service_id = :ser_id")
  void preDelete(@Param("enid")Integer enid, @Param("ser_id")Integer ser_id);


  // HQL Query to update the service by the occupied date
  @Transactional
  @Modifying
  @Query("update Service ser set ser.status = 'AVAILABLE',ser.busyTill=null, ser.lastUser = ser.currUser ,ser.currUser = null where ser.busyTill < current_date()")
  //@Query("update Service ser case when ser.busyTill < current_date() then status='AVAILABLE' else end")
  void updateService();

  @Query("select case when count(service) >0 then true else false end from Service service where serviceName =:name")
  boolean existsByServiceName(@Param("name")String name);

  @Transactional
  @Modifying
  @Query("delete   from Service   where serviceName =:name")
  void deleteByServiceName(@Param("name")String name);

  @Query("select ser from Service ser where serviceName =:name")
  Service getByName(@Param("name")String name);

@Query("select ser from Service ser where serviceId = :id")
  Service getByServiceId(@Param("id") Integer idd);

  @Transactional
  @Modifying
  @Query("delete   from Service   where serviceId =:id")
  void deleteByServiceId(@Param("id") Integer id);
}
