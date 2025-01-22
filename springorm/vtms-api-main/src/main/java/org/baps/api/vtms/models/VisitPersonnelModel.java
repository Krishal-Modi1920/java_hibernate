package org.baps.api.vtms.models;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class VisitPersonnelModel {

    private String visitPersonnelId;
    
    @NotBlank
    private String personnelId;
    
    private String personnelName;
    
    private String phoneNumber;
    
    private String email;

    @NotBlank
    private String roleId;
    
    private String roleUucode;
    
    private String roleName;
}
