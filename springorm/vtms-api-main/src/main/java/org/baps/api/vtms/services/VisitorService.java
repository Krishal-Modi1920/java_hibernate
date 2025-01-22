package org.baps.api.vtms.services;

import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.mappers.VisitorMapper;
import org.baps.api.vtms.models.VisitorModel;
import org.baps.api.vtms.models.entities.Visitor;
import org.baps.api.vtms.repositories.VisitorRepository;
import org.baps.api.vtms.repositories.specifications.VisitorSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;
    
    private final VisitorSpecification visitorSpecification;

    private final VisitorMapper visitorMapper;

    /**
     * Retrieves a list of VisitorModels based on specified filters.
     *
     * @param search The search string to be used for filtering visitor information.
     * @return A list of VisitorModels that match the specified filters.
     */
    @Transactional(readOnly = true)
    public List<VisitorModel> getVisitorByFilters(final String search) {

        // Retrieve existing visitors based on the specified search criteria and visit type
        final List<String> existingVisitorIdList =  visitorSpecification.groupBydVisitorIdSearchExecuteSpecification(
                search, VisitTypeEnum.VISIT);
        
        final List<Visitor> existingVisitorList = visitorRepository.findAllById(existingVisitorIdList);

        return visitorMapper.visitorListToVisitorModelList(existingVisitorList);
    }

}
