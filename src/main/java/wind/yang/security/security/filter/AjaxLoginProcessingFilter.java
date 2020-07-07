package wind.yang.security.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import wind.yang.security.domain.dto.AccountDto;
import wind.yang.security.security.token.AjaxAuthenticationToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private static final String REQUEST_PATH = "/api/login";
    private static final String AJAX_REQUEST_HEADER = "X-Requested-With";
    private static final String AJAX_REQUEST_HEADER_VALUE = "XMLHttpRequest";

    private ObjectMapper objectMapper = new ObjectMapper();

    public AjaxLoginProcessingFilter() {
        super(new AntPathRequestMatcher(REQUEST_PATH));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if(!isAjax(request)){ // Ajax 요청에 대해서만 처리
            throw new IllegalStateException("Authentication is not supported(only ajax)");
        }

        AccountDto accountDto = objectMapper.readValue(request.getReader(), AccountDto.class);
        if(StringUtils.isEmpty(accountDto.getUsername()) || StringUtils.isEmpty(accountDto.getPassword())){
            throw new IllegalArgumentException("Username or Password is empty");
        }

        AjaxAuthenticationToken token = new AjaxAuthenticationToken(accountDto.getUsername(), accountDto.getPassword());
        return getAuthenticationManager().authenticate(token);
    }

    private boolean isAjax(HttpServletRequest request) {
        if(AJAX_REQUEST_HEADER_VALUE.equals(request.getHeader(AJAX_REQUEST_HEADER))){
            return true;
        }
        return false;
    }
}
