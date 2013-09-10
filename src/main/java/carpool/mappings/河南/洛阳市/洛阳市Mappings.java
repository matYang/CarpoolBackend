package carpool.mappings.河南.洛阳市;

import carpool.mappings.MappingBase;
import carpool.mappings.河南.洛阳市.洛阳区.洛阳区Mappings;

public class 洛阳市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("洛阳区",new 洛阳区Mappings());

    }

}
