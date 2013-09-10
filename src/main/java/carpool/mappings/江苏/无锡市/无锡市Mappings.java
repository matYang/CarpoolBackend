package carpool.mappings.江苏.无锡市;

import carpool.mappings.MappingBase;
import carpool.mappings.江苏.无锡市.无锡区.无锡区Mappings;

public class 无锡市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("无锡区",new 无锡区Mappings());

    }

}
