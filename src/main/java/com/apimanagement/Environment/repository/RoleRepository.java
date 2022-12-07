package com.apimanagement.Environment.repository;

import com.apimanagement.Environment.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Role findByName(String name);
    @org.springframework.data.jpa.repository.Query("select case when count(role) >0 then true else false end from Role role where role.name =:rolename")
    boolean existsByName(@Param("rolename")String name);
    @Transactional
    @Modifying
    @org.springframework.data.jpa.repository.Query(value = "delete  from users_roles  where user_id = :userid",nativeQuery = true)
    void deleteByUserID(@Param("userid") Integer userid);

    @Transactional
    @Modifying
    @Query(value = "insert into users_roles values(:userid,:roleid)",nativeQuery = true)
    void addRole(@Param( "userid")Integer userid,@Param("roleid")Integer roleid);

}
