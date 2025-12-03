package com.supportlink.backend.dto;

import com.supportlink.backend.domain.Agent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentUpdateRequest {
    private String name;
    private String password;
    private Agent.Role role;
}
