package org.baps.api.vtms.models;

import lombok.Data;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class PermissionModel {

    private String permissionId;

    private String name;
}
