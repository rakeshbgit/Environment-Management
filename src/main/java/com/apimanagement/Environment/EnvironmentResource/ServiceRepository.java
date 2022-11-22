package com.apimanagement.Environment.EnvironmentResource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;;
import javax.transaction.Transactional;
import java.util.List;

public interface ServiceRepository extends JpaRepository<Service,Integer> {
  // Native SQL QUERY to get all Services for the respective Environment ID
  @Query(value = "select * from service where service_id = ANY (select service_service_id from environment_services where environment_id  = :enid)",nativeQuery = true)
    List<Service>allByEnvId(@Param("enid") Integer enid);

  // Native SQL QUERY to get all Services for the respective Environment ID
  @Query(value = "select * from service where status = 'AVAILABLE' AND service_id = ANY (select service_service_id from environment_services where environment_id  = :enid)",nativeQuery = true)
  List<Service>allByAvailable(@Param("enid") Integer enid);

  // Native SQL QUERY to retrieve the service belongs to the environment by its id
  @Query(value = "select * from service where service_id = :serid AND service_id = ANY(select service_service_id from environment_services = :enid)",nativeQuery = true)
  Service tookByEnvId(@Param("enid")Integer enid,@Param("serid")Integer serid);

  //Native SQL QUERY to delete the relation of the environment and its services from the environment services
  @Transactional
  @Modifying
  @Query( value = "delete from environment_services where environment_id = :enid and service_service_id = :ser_id",nativeQuery = true)
  public void preDelete(@Param("enid")Integer enid, @Param("ser_id")Integer ser_id);


  // HQL Query to update the service by the occupied date
  @Transactional
  @Modifying
  @Query("update Service ser set ser.status = 'AVAILABLE',ser.busyTill=null, ser.lastUser = ser.currUser ,ser.currUser = null where ser.busyTill < current_date()")
  //@Query("update Service ser case when ser.busyTill < current_date() then status='AVAILABLE' else end")
  public void updateService();



}
