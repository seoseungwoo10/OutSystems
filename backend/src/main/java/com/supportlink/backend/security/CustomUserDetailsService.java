package com.supportlink.backend.security;

import com.supportlink.backend.domain.Agent;
import com.supportlink.backend.domain.User;
import com.supportlink.backend.repository.AgentRepository;
import com.supportlink.backend.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AgentRepository agentRepository;

    public CustomUserDetailsService(UserRepository userRepository, AgentRepository agentRepository) {
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to find Agent first
        Optional<Agent> agent = agentRepository.findByEmail(email);
        if (agent.isPresent()) {
            if (!agent.get().getIsActive()) {
                throw new UsernameNotFoundException("Agent account is inactive");
            }
            return new org.springframework.security.core.userdetails.User(
                    agent.get().getEmail(),
                    agent.get().getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + agent.get().getRole().name())));
        }

        // Try to find User
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (!user.get().getIsActive()) {
                throw new UsernameNotFoundException("User account is inactive");
            }
            return new org.springframework.security.core.userdetails.User(
                    user.get().getEmail(),
                    user.get().getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
