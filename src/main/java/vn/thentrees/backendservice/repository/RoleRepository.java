package vn.thentrees.backendservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.thentrees.backendservice.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
