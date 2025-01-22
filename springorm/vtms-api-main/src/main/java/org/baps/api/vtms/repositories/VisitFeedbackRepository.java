package org.baps.api.vtms.repositories;

import org.baps.api.vtms.models.entities.VisitFeedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisitFeedbackRepository extends JpaRepository<VisitFeedback, String>, JpaSpecificationExecutor<VisitFeedback> {

    Optional<VisitFeedback> findByVisitFeedbackIdAndVisitSiteUuCode(String visitFeedbackId, String siteUUCode);
}
