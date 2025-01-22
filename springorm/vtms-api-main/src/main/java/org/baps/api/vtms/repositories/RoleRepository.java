package org.baps.api.vtms.repositories;

import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    Optional<Role> findByRoleIdAndRoleTagListRoleTagEnumAndRoleTagListStatusNot(String roleId,
                                                                                RoleTagEnum roleTagEnum, Status status);

    List<Role> findByRoleIdInAndRoleTagListRoleTagEnumInAndRoleTagListStatusNot(List<String> roleIdList,
                                                                                List<RoleTagEnum> roleTagEnumList, Status status);

    Optional<Role> findFirstByUucode(String uucode);
}
