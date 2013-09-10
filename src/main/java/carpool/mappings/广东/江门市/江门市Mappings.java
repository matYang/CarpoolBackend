package carpool.mappings.广东.江门市;

import carpool.mappings.MappingBase;
import carpool.mappings.广东.江门市.江门区.江门区Mappings;

public class 江门市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("江门区",new 江门区Mappings());

    }

}
