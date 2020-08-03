package wind.yang.security.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import wind.yang.security.domain.entity.Resources;
import wind.yang.security.repository.AccessIpRepository;
import wind.yang.security.repository.ResourcesRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 어플리케이션의 자원(Resources)에 대한 서비스
 */
@Service
public class SecurityResourceService {
    @Autowired
    private ResourcesRepository resourcesRepository;

    @Autowired
    private AccessIpRepository accessIpRepository;

    /**
     * 자원(Resources)을 조회하여 자원-역할 맵핑정보를 반환한다.
     */
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap = new LinkedHashMap<>();

        List<Resources> resourcesList = resourcesRepository.findAllResources();
        resourcesList.forEach(resources -> {
            ArrayList<ConfigAttribute> configAttributes = new ArrayList<>();
            resources.getRoleSet().forEach(
                    role -> configAttributes.add(new SecurityConfig(role.getRoleName())));

            requestMap.put(new AntPathRequestMatcher(resources.getResourceName()), configAttributes);
        });

        return requestMap;
    }

    /**
     * Method보안을 위한 자원(Resources)을 조회하여 자원-역할 맵핑정보를 반환한다.
     * 자원(Method)는 package 전체 경로로 저장된다.
     */
    public LinkedHashMap<String, List<ConfigAttribute>> getMethodResourceList() {
        LinkedHashMap<String, List<ConfigAttribute>> requestMap = new LinkedHashMap<>();

        List<Resources> resourcesList = resourcesRepository.findAllMethodResources();
        resourcesList.forEach(resources -> {
            ArrayList<ConfigAttribute> configAttributes = new ArrayList<>();
            resources.getRoleSet().forEach(
                    role -> configAttributes.add(new SecurityConfig(role.getRoleName()))
            );
            requestMap.put(resources.getResourceName(), configAttributes);
        });

        return requestMap;
    }

    /**
     * Method보안을 위한 자원(Resources)을 조회하여 자원-역할 맵핑정보를 반환한다.
     * 자원(Method)는 Pointcut 표현식으로 저장된다.
     */
    public LinkedHashMap<String, List<ConfigAttribute>> getPointcutResourceList() {
        LinkedHashMap<String, List<ConfigAttribute>> requestMap = new LinkedHashMap<>();

        List<Resources> resourcesList = resourcesRepository.findAllPointcutResources();
        resourcesList.forEach(resources -> {
            ArrayList<ConfigAttribute> configAttributes = new ArrayList<>();
            resources.getRoleSet().forEach(
                    role -> configAttributes.add(new SecurityConfig(role.getRoleName()))
            );
            requestMap.put(resources.getResourceName(), configAttributes);
        });

        return requestMap;
    }

    /**
     * 접근 가능한 IP주소 리스트 조회
     */
    public List<String> getAccessIpList() {
        List<String> accessIpList = accessIpRepository.findAll().stream()
                .map(accessIp -> accessIp.getIpAddress())
                .collect(Collectors.toList());

        return accessIpList;
    }


}
