package hu.bme.aut.payroll.config;

import hu.bme.aut.payroll.repository.UserRepository;
import hu.bme.aut.payroll.web.service.auth.LogoutSuccessHandler;
import hu.bme.aut.payroll.web.service.auth.PayrollAuthenticationProvider;
import hu.bme.aut.payroll.web.service.auth.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final UserRepository userRepository;

    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint, UserRepository userRepository) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new PayrollAuthenticationProvider(userRepository));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin()
                .and().httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }
}
