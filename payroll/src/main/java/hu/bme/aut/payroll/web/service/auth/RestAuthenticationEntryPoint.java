package hu.bme.aut.payroll.web.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException, ServletException {
        String authHeader = String.format("Auth realm=\"%s\"", getRealmName());
        response.addHeader("WWW-Authenticate", authHeader);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String json = objectMapper.writeValueAsString("{\"error\":\"Unauthorized\"}");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("hu.bme.aut");
        super.afterPropertiesSet();
    }
}
