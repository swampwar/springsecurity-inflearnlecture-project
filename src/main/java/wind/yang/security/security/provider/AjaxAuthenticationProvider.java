package wind.yang.security.security.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import wind.yang.security.security.common.FormWebAuthenticationDetails;
import wind.yang.security.security.service.AccountContext;
import wind.yang.security.security.token.AjaxAuthenticationToken;

public class AjaxAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(username);
        if(!passwordEncoder.matches(password, accountContext.getPassword())){
            throw new BadCredentialsException("Password Fail");
        }

        AjaxAuthenticationToken token =
                new AjaxAuthenticationToken(accountContext.getAccount(), null
                                          , accountContext.getAuthorities());

        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AjaxAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
