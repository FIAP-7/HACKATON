package br.com.sus.ingestao.infra.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converter para extrair roles do Keycloak (realm_access.roles) e mapear para authorities com prefixo ROLE_.
 */
public class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> authorities = extractRealmRoles(source).stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(String::toUpperCase)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
        return new JwtAuthenticationToken(source, authorities, getPrincipalName(source));
    }

    private String getPrincipalName(Jwt jwt) {
        // usa preferred_username se existir; senão, subject
        Object preferred = jwt.getClaims().get("preferred_username");
        return preferred != null ? preferred.toString() : jwt.getSubject();
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractRealmRoles(Jwt jwt) {
        Object realmAccessObj = jwt.getClaims().get("realm_access");
        if (!(realmAccessObj instanceof Map<?, ?> realmAccess)) {
            return Set.of();
        }
        Object rolesObj = realmAccess.get("roles");
        if (rolesObj instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }
}
