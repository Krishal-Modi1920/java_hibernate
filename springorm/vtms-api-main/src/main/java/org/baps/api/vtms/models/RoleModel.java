package org.baps.api.vtms.models;

import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class RoleModel {

    private String roleId;

    private String name;
    
    private String uucode;

    private JsonNode langMeta;

    private List<PermissionModel> permissionModelList;
    
    private List<RoleTagModel> roleTagModelList;
}
