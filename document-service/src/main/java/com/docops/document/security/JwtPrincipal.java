package com.docops.document.security;


public record JwtPrincipal(Long userId, Long orgId, String role) {}