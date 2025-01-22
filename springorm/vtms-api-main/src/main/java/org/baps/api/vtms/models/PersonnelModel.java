package org.baps.api.vtms.models;

import lombok.Data;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class PersonnelModel {

    private String personnelId;
    
    private String personnelUUId;
    
    private String uucode;
    
    private Integer externalId;
    
    private String firstName;

    private String middleName;

    private String lastName;

    private String gender;

    private String ageGroup;

    private String mandal;

    private String centerId;
    
    private String parazoneId;

    private String email;

    private String phoneCountryCode;
    
    private String phoneNumber;
    
    private List<RoleModel> roleModelList;
}
