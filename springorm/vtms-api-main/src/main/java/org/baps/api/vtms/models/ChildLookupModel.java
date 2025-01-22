package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildLookupModel implements Serializable {

    @Serial
    private static final long serialVersionUID = -1363869399567636735L;

    private String key;
    
    private String value;
    
    private String sequenceNumber;

}
