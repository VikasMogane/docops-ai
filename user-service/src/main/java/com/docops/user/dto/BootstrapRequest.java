package com.docops.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BootstrapRequest {
    private String orgName;
    private String email;
    private String password;
}
