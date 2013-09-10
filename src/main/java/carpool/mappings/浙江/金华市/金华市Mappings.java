package carpool.mappings.浙江.金华市;

import carpool.mappings.MappingBase;
import carpool.mappings.浙江.金华市.金华区.金华区Mappings;

public class 金华市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("金华区",new 金华区Mappings());

    }

}
