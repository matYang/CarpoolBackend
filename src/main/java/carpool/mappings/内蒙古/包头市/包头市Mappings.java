package carpool.mappings.内蒙古.包头市;

import carpool.mappings.MappingBase;
import carpool.mappings.内蒙古.包头市.包头区.包头区Mappings;

public class 包头市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("包头区",new 包头区Mappings());

    }

}
