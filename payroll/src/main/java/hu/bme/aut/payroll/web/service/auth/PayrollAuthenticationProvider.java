package hu.bme.aut.payroll.web.service.auth;

import hu.bme.aut.payroll.domain.User;
import hu.bme.aut.payroll.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class PayrollAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    public PayrollAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<? extends Object> auth) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(auth));
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String name = auth.getName();
        String password = auth.getCredentials().toString();

        User authenticatedUser = userRepository.findByName(name);
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        if(authenticatedUser == null || !encoder.matches(password, authenticatedUser.getPassword())) {
            throw new BadCredentialsException("Error BadCredentials!");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role: authenticatedUser.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        auth = new UsernamePasswordAuthenticationToken(name, password, authorities);
        return auth;
    }
}
