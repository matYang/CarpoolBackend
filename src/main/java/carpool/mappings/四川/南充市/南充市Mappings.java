package carpool.mappings.四川.南充市;

import carpool.mappings.MappingBase;
import carpool.mappings.四川.南充市.南充区.南充区Mappings;

public class 南充市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("南充区",new 南充区Mappings());

    }

}
