package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.mappers.SiteMapper;
import org.baps.api.vtms.models.SiteModel;
import org.baps.api.vtms.models.entities.Site;
import org.baps.api.vtms.repositories.SiteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    private final SiteMapper siteMapper;

    private final Translator translator;

    /**
     * Retrieves a site by UUCode.
     *
     * @param siteUUCode The unique identifier (UUCode) of the site to retrieve.
     * @return The site entity.
     * @throws DataNotFoundException if the site is not found.
     */
    @Transactional(readOnly = true)
    public Site findByUUCode(final String siteUUCode) {
        if (StringUtils.isBlank(siteUUCode)) {
            throw new DataNotFoundException(translator.toLocal("site.with.site_uucode.not_found", siteUUCode));
        } else {
            return siteRepository.findByUuCode(siteUUCode)
                .orElseThrow(() -> new DataNotFoundException(translator.toLocal("site.with.site_uucode.not_found", siteUUCode)));
        }
    }
    
    /**
     * Checks whether a site exists based on the provided UUCode.
     *
     * @param siteUUCode The UUCode (Unique Identifier) of the site to check.
     * @return {@code true} if the site with the given UUCode exists; otherwise, {@code false}.
     * @throws DataNotFoundException If the provided UUCode is blank or the site is not found in the repository.
     */
    @Transactional(readOnly = true)
    public boolean siteExistsByUUCode(final String siteUUCode) {
        if (StringUtils.isBlank(siteUUCode) || siteRepository.findByUuCode(siteUUCode).isEmpty()) {
            throw new DataNotFoundException(translator.toLocal("site.with.site_uucode.not_found", siteUUCode));
        }
        return true;
    }

    /**
     * Retrieves a site model by UUCode.
     *
     * @param siteUUCode The unique identifier (UUCode) of the site to retrieve.
     * @return The site model.
     */
    @Transactional(readOnly = true)
    public SiteModel findSiteByUUCode(final String siteUUCode) {
        return siteMapper.siteToSiteModel(findByUUCode(siteUUCode));
    }
    
    /**
     * Finds the current date and time for a specific site identified by its unique code.
     *
     * @param existingSite  The existing site containing the time zone information.
     * @return The current date and time in the time zone of the specified site.
     * @throws EntityNotFoundException If the site with the given unique code is not found.
     */
    public LocalDateTime getCurrentDateTimeFromExistingSite(final Site existingSite) { 
        
        final ZoneId systemDefaultZone = ZoneId.of(existingSite.getTimeZone());

        final ZoneId siteZone = ZoneId.of(existingSite.getTimeZone());

        return LocalDateTime.now().atZone(systemDefaultZone)
                .withZoneSameInstant(siteZone)
                .toLocalDateTime();
    }
    
    /**
     * Retrieves a list of all sites.
     * 
     * @return A list of Site objects representing all available sites.
     */
    @Transactional(readOnly = true)
    public List<Site> findAllSite() {
        return siteRepository.findAll();
    }

}
