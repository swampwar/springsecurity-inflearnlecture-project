package wind.yang.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wind.yang.security.domain.entity.RoleHierarchy;
import wind.yang.security.repository.RoleHierarchyRepository;
import wind.yang.security.service.RoleHierarchyService;

import java.util.Iterator;
import java.util.List;

/**
 * RoleHierarchy 엔티티용 서비스
 */
@Service
public class RoleHierarchyServiceImpl implements RoleHierarchyService {
    @Autowired
    RoleHierarchyRepository roleHierarchyRepository;

    /**
     * RoleHierarchy의 모든 데이터를 조회하여
     * @return
     */
    @Transactional
    @Override
    public String findAllHierarchy() {
        List<RoleHierarchy> roleHierarchyList = roleHierarchyRepository.findAll();

        Iterator<RoleHierarchy> iterator = roleHierarchyList.iterator();
        StringBuilder concatedRoles = new StringBuilder();
        while(iterator.hasNext()){
            RoleHierarchy roleHierarchy = iterator.next();
            if(roleHierarchy.getParentName() != null){
                concatedRoles.append(roleHierarchy.getParentName().getChildName());
                concatedRoles.append(" > ");
                concatedRoles.append(roleHierarchy.getChildName());
                concatedRoles.append("\n");
            }
        }

        return concatedRoles.toString();
    }
}
