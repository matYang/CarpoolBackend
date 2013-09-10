package carpool.mappings.山东.烟台市;

import carpool.mappings.MappingBase;
import carpool.mappings.山东.烟台市.烟台区.烟台区Mappings;

public class 烟台市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("烟台区",new 烟台区Mappings());

    }

}
