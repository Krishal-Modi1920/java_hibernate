package org.baps.api.vtms.repositories;

import org.baps.api.vtms.models.entities.Visitor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, String>, JpaSpecificationExecutor<Visitor> {

}
