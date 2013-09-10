package carpool.mappings.江苏.苏州市;

import carpool.mappings.MappingBase;
import carpool.mappings.江苏.苏州市.苏州区.苏州区Mappings;

public class 苏州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("苏州区",new 苏州区Mappings());

    }

}
