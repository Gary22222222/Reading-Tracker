package com.group20.dailyreadingtracker.repository;

import com.group20.dailyreadingtracker.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>{
    
}
