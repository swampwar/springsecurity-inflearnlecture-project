package wind.yang.security.security.voter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import wind.yang.security.service.SecurityResourceService;

import java.util.Collection;
import java.util.List;

/**
 * AccessDecisionManager에서 사용할 Voter로써 접근IP와 사용자의 IP를 비교하여, 접근가능한IP만 통과시킨다.
 * AffirmativeBased를 사용하므로 접근가능한 IP가 아니면 종료를 위해 예외를 발생시킨다.
 */
@Component
public class IpAddressAccessVoter implements AccessDecisionVoter<Object> {
    @Autowired
    SecurityResourceService securityResourceService;

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        String remoteAddress = details.getRemoteAddress();
        List<String> accessIpList = securityResourceService.getAccessIpList();

        int result = ACCESS_DENIED;
        for(String ipAddress : accessIpList){
            if(remoteAddress.equals(ipAddress)){
                result = ACCESS_ABSTAIN; // 다음 Voter에게 인가체크를 넘기기 위해 GRANTED가 아닌 ABSTAIN을 넘긴다.
                break;
            }
        }

        if(result == ACCESS_DENIED){ // 허용가능IP가 아니면 다음 Voter로 인가체크를 넘기는 것이 아니라 예외를 발생시켜 종료한다.
            throw new AccessDeniedException("Invalid IpAddress");
        }

        return result;
    }
}
