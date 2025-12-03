package com.supportlink.backend.dto;

import com.supportlink.backend.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AgentResponse {
    private Long agentId;
    private String email;
    private String name;
    private Agent.Role role;
    private Boolean isActive;

    public static AgentResponse from(Agent agent) {
        return new AgentResponse(
                agent.getAgentId(),
                agent.getEmail(),
                agent.getName(),
                agent.getRole(),
                agent.getIsActive()
        );
    }
}
