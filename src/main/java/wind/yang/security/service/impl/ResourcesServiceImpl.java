package wind.yang.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wind.yang.security.domain.entity.Resources;
import wind.yang.security.service.ResourcesService;

import java.util.List;

@Service("resourcesService")
public class ResourcesServiceImpl implements ResourcesService {
    @Autowired
    private wind.yang.security.repository.ResourcesRepository ResourcesRepository;

    @Transactional
    @Override
    public Resources getResources(Long id) {
        return ResourcesRepository.findById(id).orElse(new Resources());
    }

    @Transactional
    @Override
    public List<Resources> getResources() {
        return ResourcesRepository.findAll(Sort.by(Sort.Order.asc("orderNum")));
    }

    @Transactional
    @Override
    public void createResources(Resources resources){
        ResourcesRepository.save(resources);
    }

    @Transactional
    @Override
    public void deleteResources(Long id) {
        ResourcesRepository.deleteById(id);
    }
}
