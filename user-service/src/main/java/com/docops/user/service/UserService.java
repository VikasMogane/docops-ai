package com.docops.user.service;

import com.docops.user.dto.BootstrapRequest;
import com.docops.user.dto.LoginRequest;
import com.docops.user.entity.*;
import com.docops.user.repository.*;
import com.docops.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void bootstrap(BootstrapRequest request) {
        Organization org = new Organization();
        org.setName(request.getOrgName());
        organizationRepository.save(org);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        user.setOrgId(org.getId());

        userRepository.save(user);
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getId(), user.getOrgId(), user.getRole().name());
    }
}
