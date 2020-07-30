package wind.yang.security.service;

import wind.yang.security.domain.entity.Resources;

import java.util.List;

public interface ResourcesService {
    Resources getResources(Long id);

    List<Resources> getResources();

    void createResources(Resources Resources);

    void deleteResources(Long id);
}
