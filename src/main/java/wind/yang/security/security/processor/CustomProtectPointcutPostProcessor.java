package wind.yang.security.security.processor;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class CustomProtectPointcutPostProcessor implements BeanPostProcessor {
    private final Map<String, List<ConfigAttribute>> pointcutMap = new LinkedHashMap<>();
    private final MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource;
    private final Set<PointcutExpression> pointCutExpressions = new LinkedHashSet<>();
    private final PointcutParser parser;
    private final Set<String> processedBeans = new HashSet<>();

    public CustomProtectPointcutPostProcessor(
            MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource) {
        Assert.notNull(mapBasedMethodSecurityMetadataSource,
                "MapBasedMethodSecurityMetadataSource to populate is required");
        this.mapBasedMethodSecurityMetadataSource = mapBasedMethodSecurityMetadataSource;

        // Set up AspectJ pointcut expression parser
        Set<PointcutPrimitive> supportedPrimitives = new HashSet<>(3);
        supportedPrimitives.add(PointcutPrimitive.EXECUTION);
        supportedPrimitives.add(PointcutPrimitive.ARGS);
        supportedPrimitives.add(PointcutPrimitive.REFERENCE);
        // supportedPrimitives.add(PointcutPrimitive.THIS);
        // supportedPrimitives.add(PointcutPrimitive.TARGET);
        // supportedPrimitives.add(PointcutPrimitive.WITHIN);
        // supportedPrimitives.add(PointcutPrimitive.AT_ANNOTATION);
        // supportedPrimitives.add(PointcutPrimitive.AT_WITHIN);
        // supportedPrimitives.add(PointcutPrimitive.AT_ARGS);
        // supportedPrimitives.add(PointcutPrimitive.AT_TARGET);
        parser = PointcutParser
                .getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(supportedPrimitives);
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (processedBeans.contains(beanName)) {
            // We already have the metadata for this bean
            return bean;
        }

        synchronized (processedBeans) {
            // check again synchronized this time
            if (processedBeans.contains(beanName)) {
                return bean;
            }

            // Obtain methods for the present bean
            Method[] methods;
            try {
                methods = bean.getClass().getMethods();
            }
            catch (Exception e) {
                throw new IllegalStateException(e.getMessage());
            }

            // Check to see if any of those methods are compatible with our pointcut
            // expressions
            for (Method method : methods) {
                for (PointcutExpression expression : pointCutExpressions) {
                    // Try for the bean class directly
                    if (attemptMatch(bean.getClass(), method, expression, beanName)) {
                        // We've found the first expression that matches this method, so
                        // move onto the next method now
                        break; // the "while" loop, not the "for" loop
                    }
                }
            }

            processedBeans.add(beanName);
        }

        return bean;
    }

    private boolean attemptMatch(Class<?> targetClass, Method method,
                                 PointcutExpression expression, String beanName) {
        // Determine if the presented AspectJ pointcut expression matches this method

        boolean matches = false;
        try{
            matches = expression.matchesMethodExecution(method).alwaysMatches();
        }catch (Exception e){
            log.debug(e.getMessage());
            return false;
        }

        // Handle accordingly
        if (matches) {
            List<ConfigAttribute> attr = pointcutMap.get(expression
                    .getPointcutExpression());

            mapBasedMethodSecurityMetadataSource.addSecureMethod(targetClass, method,
                    attr);
        }

        return matches;
    }

    public void setPointcutMap(Map<String, List<ConfigAttribute>> map) {
        Assert.notEmpty(map, "configAttributes cannot be empty");
        for (String expression : map.keySet()) {
            List<ConfigAttribute> value = map.get(expression);
            addPointcut(expression, value);
        }
    }

    private void addPointcut(String pointcutExpression, List<ConfigAttribute> definition) {
        Assert.hasText(pointcutExpression, "An AspectJ pointcut expression is required");
        Assert.notNull(definition, "A List of ConfigAttributes is required");
        pointcutExpression = replaceBooleanOperators(pointcutExpression);
        pointcutMap.put(pointcutExpression, definition);
        // Parse the presented AspectJ pointcut expression and add it to the cache
        pointCutExpressions.add(parser.parsePointcutExpression(pointcutExpression));

    }

    private String replaceBooleanOperators(String pcExpr) {
        pcExpr = StringUtils.replace(pcExpr, " and ", " && ");
        pcExpr = StringUtils.replace(pcExpr, " or ", " || ");
        pcExpr = StringUtils.replace(pcExpr, " not ", " ! ");
        return pcExpr;
    }

}
