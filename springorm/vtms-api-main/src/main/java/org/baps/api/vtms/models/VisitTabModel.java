package org.baps.api.vtms.models;

import lombok.Data;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitTabModel {
    private boolean secondaryVisitorAvailable;

    private boolean tourAvailable;

    private boolean servicesAvailable;

    private boolean meetingsAvailable;

    private boolean documentsAvailable;

    private boolean interviewCoordinatorAvailable;

    private boolean externalFeedback;
}
