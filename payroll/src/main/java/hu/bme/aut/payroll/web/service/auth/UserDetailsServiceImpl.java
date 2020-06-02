package hu.bme.aut.payroll.web.service.auth;

import hu.bme.aut.payroll.domain.User;
import hu.bme.aut.payroll.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Represents the service that helps to load users
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByName(username);
        if (user == null)
            throw new UsernameNotFoundException(username + " is an invalid username");
        else
            return new UserDetailsImpl(user);
    }
}
