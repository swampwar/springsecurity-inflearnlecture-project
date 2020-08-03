package wind.yang.security.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import wind.yang.security.security.factory.MethodResourcesMapFactoryBean;
import wind.yang.security.security.processor.CustomProtectPointcutPostProcessor;
import wind.yang.security.service.SecurityResourceService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

//import wind.yang.security.security.processor.ProtectPointcutPostProcessor;

/**
 * Map기반의 Method보안 설정을 담당하는 설정클래스
 * 애노테이션 기반(@PreAuthorize, @PostAuthorize 등)과 다르게 Method보안대상을 DB에서 가져오도록 한다.
 * (애노테이션 기반은 Method보안 대상을 BeanPostProcessor가 빈을 순회하며 애노테이션이 마킹되어 있으면 대상에 포함시킨다.)
 */
@EnableGlobalMethodSecurity//(securedEnabled = true, prePostEnabled = true) // AOP기반 Method보안설정을 가능하게 한다.
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    @Autowired
    SecurityResourceService securityResourceService;

    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return mapBasedMethodSecurityMetadataSource();
    }

    @Bean
    public MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource() {
        return new MapBasedMethodSecurityMetadataSource(methodResourcesMapFactoryBean().getObject());
    }

    @Bean
    public MethodResourcesMapFactoryBean methodResourcesMapFactoryBean(){
        MethodResourcesMapFactoryBean methodResourcesFactoryBean = new MethodResourcesMapFactoryBean();
        methodResourcesFactoryBean.setResourceType("method");
        methodResourcesFactoryBean.setSecurityResourceService(securityResourceService);
        return methodResourcesFactoryBean;
    }

    @Bean
    public MethodResourcesMapFactoryBean pointcutResourcesMapFactoryBean(){
        MethodResourcesMapFactoryBean methodResourcesFactoryBean = new MethodResourcesMapFactoryBean();
        methodResourcesFactoryBean.setResourceType("pointcut");
        methodResourcesFactoryBean.setSecurityResourceService(securityResourceService);
        return methodResourcesFactoryBean;
    }

    /**
     * ProtectPointcutPostProcessor 빈을 생성한다.
     * pointcut표현식으로 작성된 Resource를 해석하여 일치하는 빈을 찾아 MapBasedMethodSecurityMetadataSource에게 전달한다.
     * default final 클래스이므로 리플렉션을 이용해 빈을 생성한다.
     *
     * 하지만 빈을 처리하며 발생하는 오류로 소스를 복사하여 try-catch문을 추가한 ProtectPointcutPostProcessor을 생성한다.
     */
//    @Bean
    BeanPostProcessor protectPointcutPostProcessor() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = Class.forName("org.springframework.security.config.method.ProtectPointcutPostProcessor");
        Constructor<?> declaredConstructor = clazz.getDeclaredConstructor(MapBasedMethodSecurityMetadataSource.class);
        declaredConstructor.setAccessible(true);
        Object instance = declaredConstructor.newInstance(mapBasedMethodSecurityMetadataSource());
        Method setPointMap = instance.getClass().getMethod("setPointcutMap", Map.class);
        setPointMap.setAccessible(true);
        setPointMap.invoke(instance, pointcutResourcesMapFactoryBean().getObject());

        return (BeanPostProcessor) instance;
    }

    @Bean
    BeanPostProcessor customProtectPointcutPostProcessor() {
        CustomProtectPointcutPostProcessor protectPointcutPostProcessor =
                new CustomProtectPointcutPostProcessor(mapBasedMethodSecurityMetadataSource());
        protectPointcutPostProcessor.setPointcutMap(pointcutResourcesMapFactoryBean().getObject());
        return protectPointcutPostProcessor;
    }
}
