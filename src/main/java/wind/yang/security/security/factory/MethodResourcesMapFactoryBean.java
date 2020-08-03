package wind.yang.security.security.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import wind.yang.security.service.SecurityResourceService;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Method보안의 대상이 되는 Resource-Role을 생성하는 FactoryBean
 */
public class MethodResourcesMapFactoryBean implements FactoryBean<LinkedHashMap<String, List<ConfigAttribute>>> {
    private SecurityResourceService securityResourceService;
    private LinkedHashMap<String, List<ConfigAttribute>> resourceMap;
    private String resourceType;

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setSecurityResourceService(SecurityResourceService securityResourceService) {
        this.securityResourceService = securityResourceService;
    }

    @Override
    public LinkedHashMap<String, List<ConfigAttribute>> getObject() {
        if(resourceMap == null){
            init();
        }

        return resourceMap;
    }

    private void init() {
        if("method".equals(resourceType)){
            resourceMap = securityResourceService.getMethodResourceList();
        }else if("pointcut".equals(resourceType)){
            resourceMap = securityResourceService.getPointcutResourceList();
        }else {
            throw new IllegalArgumentException("resourceType is not method or pointcut");
        }
    }

    @Override
    public Class<?> getObjectType() {
        return LinkedHashMap.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
