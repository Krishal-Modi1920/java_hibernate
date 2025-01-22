package org.baps.api.vtms.models.base;

import lombok.Data;

@Data
public class RedisResponse<T> {

    private T response;
}
