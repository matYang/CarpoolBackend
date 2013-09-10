package carpool.mappings.江苏.徐州市;

import carpool.mappings.MappingBase;
import carpool.mappings.江苏.徐州市.徐州区.徐州区Mappings;

public class 徐州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("徐州区",new 徐州区Mappings());

    }

}
