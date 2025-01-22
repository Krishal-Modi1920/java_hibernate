package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.mappers.PersonnelMapper;
import org.baps.api.vtms.models.PersonnelModel;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.repositories.PersonnelRepository;
import org.baps.api.vtms.repositories.specifications.PersonnelSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Service
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class PersonnelService {

    private final PersonnelMapper personnelMapper;

    private final PersonnelRepository personnelRepository;

    private final PersonnelSpecification personnelSpecification;

    private final Translator translator;

    /**
     * Retrieves a Personnel object by its unique identifier.
     *
     * @param personnelId The unique identifier of the Personnel to be retrieved.
     * @return The Personnel object if found; otherwise, it throws a DataNotFoundException.
     * @throws DataNotFoundException If no Personnel is found with the given id.
     */
    @Transactional(readOnly = true)
    public Personnel findById(final String personnelId) {
        if (StringUtils.isBlank(personnelId)) {
            throw new DataNotFoundException(translator.toLocal("personnel.with.personnel_id.not.found", personnelId));
        }
        return personnelRepository.findById(personnelId)
            .orElseThrow(() -> new DataNotFoundException(translator.toLocal("personnel.with.personnel_id.not.found", personnelId)));
    }

    /**
     * Retrieves a Personnel entity by its unique user code (uucode).
     * This method queries the database for a Personnel entity with the given uucode and
     * returns it. If the entity is not found, it throws a DataNotFoundException.
     *
     * @param pid The unique user code (uucode) identifying the Personnel entity to retrieve.
     * @return The retrieved Personnel entity.
     * @throws DataNotFoundException If no Personnel entity is found with the provided uucode.
     */
    @Transactional(readOnly = true)
    public Personnel findPersonnelByUucode(final String pid) {
        return personnelRepository.findByUucode(pid).orElseThrow(() -> new DataNotFoundException(
            translator.toLocal("personnel.with.uucode.not.found", pid)));
    }

    /**
     * Retrieves a PersonnelModel object by processing the Personnel entity obtained from the provided security token.
     * This method is transactional and read-only.
     *
     * @return A PersonnelModel object representing the Personnel entity from the token.
     */
    @Transactional(readOnly = true)
    public PersonnelModel getPersonnelModelByToken() {
        // Convert the Personnel entity into a PersonnelModel object
        return personnelMapper.personnelToPersonnelModel(getLoginedPersonnel());
    }

    /**
     * Retrieves a list of personnel based on specified sorting, search criteria, and filter properties.
     *
     * @param sortDirection The sorting direction ("asc" or "desc").
     * @param sortProperty  The property by which to sort the results.
     * @param search        The search keyword to filter personnel by.
     * @param roleName      The name of the role to filter personnel by.
     * @return A list of {@link PersonnelModel} objects that match the criteria.
     */
    @Transactional(readOnly = true)
    public List<PersonnelModel> getAllPersonnel(@Nullable final String sortDirection,
                                                @Nullable final String sortProperty,
                                                @Nullable final String search,
                                                @Nullable final String roleName) {

        final List<Personnel> personnelList;

        final Specification<Personnel> specification =
            personnelSpecification.buildPersonnelSearchFilterSpecification(sortProperty, sortDirection, search, roleName);

        personnelList = personnelRepository.findAll(specification);

        return CollectionUtils.isEmpty(personnelList) ? Collections.emptyList()
            : personnelMapper.personnelListToPersonnelModelList(personnelList);
    }

    /**
     * Retrieves a list of available personnel based on a specified date and time range.
     *
     * @param startDateTime The start date and time for filtering available personnel.
     * @param endDateTime   The end date and time for filtering available personnel.
     * @param siteUUCode The unique code associated with the site.
     * @return A list of PersonnelModel objects representing the available personnel within the specified time range.
     */
    @Transactional(readOnly = true)
    public List<PersonnelModel> getAvailablePersonnelListByFilter(
        final LocalDateTime startDateTime, final LocalDateTime endDateTime, final String siteUUCode) {

        final var associatedPersonnelList =
            personnelRepository.findAllAssociatePersonnelInVisitOrVisitService(startDateTime, endDateTime, siteUUCode);

        final var associatedPersonnelIdList =
            associatedPersonnelList.stream().map(Personnel::getPersonnelId).toList();

        List<Personnel> availablePersonnelList = null;

        if (CollectionUtils.isEmpty(associatedPersonnelIdList)) {
            availablePersonnelList = personnelRepository.findAll();
        } else {
            availablePersonnelList = personnelRepository.findByPersonnelIdNotIn(associatedPersonnelIdList);
        }

        return CollectionUtils.isEmpty(availablePersonnelList) ? Collections.emptyList()
            : personnelMapper.personnelListToPersonnelModelList(availablePersonnelList);
    }

    /**
     * Retrieves a mapping of personnel objects by their personnel IDs from a given list of IDs.
     *
     * @param personnelIds A list of personnel IDs to retrieve personnel objects for.
     * @return A map where personnel IDs are keys, and corresponding Personnel objects are values.
     * @throws DataNotFoundException if any of the specified personnel IDs are not found.
     */
    @Transactional(readOnly = true)
    public Map<String, Personnel> getMapOfPersonnelByPersonnelIds(final List<String> personnelIds) {

        // Find the existing role based on roleId and ACTIVE role tags.
        final var mapOfPersonnelIdWithExistingPersonnelList = personnelRepository.findAllById(personnelIds)
            .stream().collect(Collectors.toMap(Personnel::getPersonnelId, p -> p));

        if (MapUtils.isNotEmpty(mapOfPersonnelIdWithExistingPersonnelList)) {

            final var invalidPersonnelIds = personnelIds.stream().filter(
                personnelId -> !mapOfPersonnelIdWithExistingPersonnelList
                    .containsKey(personnelId)).toList();

            if (CollectionUtils.isNotEmpty(invalidPersonnelIds)) {
                throw new DataNotFoundException(translator.toLocal("personnel.with.personnel_id.not.found", invalidPersonnelIds));
            }
        } else {
            throw new DataNotFoundException(translator.toLocal("personnel.with.personnel_id.not.found", personnelIds));
        }
        return mapOfPersonnelIdWithExistingPersonnelList;
    }

    /**
     * Retrieve the currently authenticated user's personnel information.
     *
     * @return An Optional containing the user's personnel info, or an empty Optional if not authenticated.
     */
    @Transactional(readOnly = true)
    public Personnel getLoginedPersonnel() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (ObjectUtils.isNotEmpty(authentication) && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return findById(userDetails.getUsername());
        }
        throw new DataValidationException(translator.toLocal("token.is_required"));
    }
}
