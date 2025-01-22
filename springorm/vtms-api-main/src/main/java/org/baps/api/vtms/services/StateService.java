package org.baps.api.vtms.services;

import static io.github.perplexhub.rsql.RSQLJPASupport.toSpecification;

import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.mappers.StateMapper;
import org.baps.api.vtms.models.StateModel;
import org.baps.api.vtms.models.entities.State;
import org.baps.api.vtms.repositories.StateRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class StateService {

    private final StateRepository stateRepository;

    private final StateMapper stateMapper;

    private final Translator translator;

    /**
     * Finds a state by its ID.
     *
     * @param stateId The ID of the state to find.
     * @return The {@link State} object if found.
     * @throws DataNotFoundException If the state with the given ID is not found.
     */
    @Transactional(readOnly = true)
    public State findStateById(final String stateId) {
        
        return stateRepository.findById(stateId).orElseThrow(() -> 
            new DataNotFoundException(translator.toLocal("state.with.state_id.not_found", stateId)));
    }      
    

    /**
     * Retrieves a list of StateModel objects representing states or regions.
     * This method queries the database for State entities and converts them into
     * corresponding StateModel objects for use in the application.
     *
     * @param filter A filter string for narrowing down the results (can be null or empty).
     * @return A list of StateModel objects, or an empty list if no records match the filter.
     */
    @Transactional(readOnly = true)
    public List<StateModel> getStateList(final String filter) {
        
        final var existingStateList = StringUtils.isBlank(filter) ? stateRepository.findAll() :
            stateRepository.findAll(toSpecification(filter, true, null, null));

        List<StateModel> stateModelList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(existingStateList)) {
            stateModelList = stateMapper.stateListToStateModelList(existingStateList);
        }

        return stateModelList;
    }                                           
}
