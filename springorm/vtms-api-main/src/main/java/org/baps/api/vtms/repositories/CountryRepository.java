package org.baps.api.vtms.repositories;


import org.baps.api.vtms.models.entities.Country;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, String>, JpaSpecificationExecutor<Country> {

    List<Country> findByIsdCode(String isdCode);
}
