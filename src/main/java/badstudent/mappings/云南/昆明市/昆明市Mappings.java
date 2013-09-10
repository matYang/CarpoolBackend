package badstudent.mappings.云南.昆明市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.云南.昆明市.呈贡大学城.呈贡大学城Mappings;
import badstudent.mappings.云南.昆明市.昆明区.昆明区Mappings;

public class 昆明市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("昆明区",new 昆明区Mappings());
        subAreaMappings.put("呈贡大学城",new 呈贡大学城Mappings());

    }

}
