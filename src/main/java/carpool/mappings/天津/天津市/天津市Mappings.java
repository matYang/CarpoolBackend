package carpool.mappings.天津.天津市;

import carpool.mappings.MappingBase;
import carpool.mappings.天津.天津市.天津区.天津区Mappings;

public class 天津市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("天津区",new 天津区Mappings());


    }

}
