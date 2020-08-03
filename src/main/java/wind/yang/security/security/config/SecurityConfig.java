package wind.yang.security.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import wind.yang.security.security.factory.UrlResourcesMapFactoryBean;
import wind.yang.security.security.filter.PermitAllFilter;
import wind.yang.security.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import wind.yang.security.security.provider.FormAuthenticationProvider;
import wind.yang.security.security.voter.IpAddressAccessVoter;
import wind.yang.security.service.SecurityResourceService;

import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    SecurityResourceService securityResourceService;

    @Autowired
    AuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    AuthenticationSuccessHandler formAuthenticationSuccessHandler;

    @Autowired
    AuthenticationFailureHandler formAuthenticationFailureHandler;

    @Autowired
    AccessDeniedHandler formAccessDeniedHandler;

    @Autowired
    IpAddressAccessVoter ipAddressAccessVoter;

    private String[] permitAllResources = {"/", "/login", "/user/login/**"};

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 인증시 직접 구현한 CustomUserDetailsService를 사용한다.
//        auth.userDetailsService(userDetailsService);

        // 인증시 직접 구현한 FormAuthenticationProvider를 사용한다.(내부적으로 CustomUserDetailsService를 프로퍼티로 가진다.)
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        return new FormAuthenticationProvider();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //---------------------------------------------------------------------------------------------------//
    // FilterSecurityInterceptor 빈생성
    /**
     * 커스텀 FilterSecurityInterceptor 빈을 생성한다.(PermitAllFilter 객체)
     * 1. HTTP 요청시 요청URL에 해당하는 권한을 조회하는 커스텀 SecurityMetadataSource를 바인딩 해준다.
     * 2. 불필요한 권한조회를 하지 않도록 PermitAllFilter 객체로 생성한다.
     */
    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {
        // 생성한 UrlFilterInvocationSecurityMetadataSource를 바인딩한 FilterSecurityInterceptor 객체를 생성한다.
        PermitAllFilter filterSecurityInterceptor = new PermitAllFilter(permitAllResources);
        filterSecurityInterceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource()); // 커스터마이징한 FilterInvocationSecurityMetadataSource
        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
        filterSecurityInterceptor.setAuthenticationManager(super.authenticationManagerBean());

        return filterSecurityInterceptor;
    }

    /**
     * 커스텀 SecurityMetadataSource 빈을 생성한다.
     * DB에서 요청받은 자원에 대한 권한을 조회한다.
     */
    @Bean
    public UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject(), securityResourceService);
    }

    @Bean
    public UrlResourcesMapFactoryBean urlResourcesMapFactoryBean(){
        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
        urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
        return urlResourcesMapFactoryBean;
    }

    /**
     * AccessDecisionManager 구현체 중 AffirmativeBased 빈을 생성한다.
     * Voter중 하나라도 ACCESS_GRANTED를 리턴하면 통과시킨다.
     */
    @Bean
    public AccessDecisionManager affirmativeBased(){
        AffirmativeBased affirmativeBased = new AffirmativeBased(getAccessDecisionVoters());
        return affirmativeBased;
    }

    /**
     * 다음 Voter를 반환한다.
     * 1. IP접근 체크를 위한 IpAddressAccessVoter
     * 2. 인가 체크를 위한 RoleHierarchyVoter
     */
    @Bean
    public List<AccessDecisionVoter<?>> getAccessDecisionVoters(){
        List<AccessDecisionVoter<? extends  Object>> accessDecisionVoters = new ArrayList<>();
        accessDecisionVoters.add(this.ipAddressAccessVoter); // IP체크가 선행되어야 한다.
        accessDecisionVoters.add(roleVoter());
        return accessDecisionVoters;
    }

    /**
     * 계층화된 Role을 이용하여 인가처리 할 수 있는 RoleVoter 빈을 생성한다.
     * 계층화된 Role은 SecurityInitializer.java에서 초기화한다.
     */
    @Bean
    public AccessDecisionVoter<? extends Object> roleVoter(){
        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
        return roleHierarchyVoter;
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy(){
        return new RoleHierarchyImpl();
    }

    // FilterSecurityInterceptor 빈생성 종료
    //---------------------------------------------------------------------------------------------------//

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
//                .antMatchers("/", "/users","/user/login/**", "/login*").permitAll()
//                .antMatchers("/mypage").hasRole("USER")
//                .antMatchers("/messages").hasRole("MANAGER")
//                .antMatchers("/config").hasRole("ADMIN")
//                .anyRequest().authenticated()
        .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")
                .defaultSuccessUrl("/")
                .authenticationDetailsSource(authenticationDetailsSource)
                .successHandler(formAuthenticationSuccessHandler)
                .failureHandler(formAuthenticationFailureHandler)
                .permitAll();

        http
            .exceptionHandling()
                .accessDeniedHandler(formAccessDeniedHandler);

        http.addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // permitAll()과 다르게 보안 Filter를 통과하지 않는다.
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
