package org.baps.api.vtms.models.notification;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@Builder
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class NotificationUser {

    private String email;

    private String emailType;

    private Map<String, String> bodyVars;

    private Map<String, String> titleVars;
}
