package com.sportygroup.betting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestApiResponse {
    private String status;
    private String message;
    private String error;
    private Object data;
}
