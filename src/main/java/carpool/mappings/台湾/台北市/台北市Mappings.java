package carpool.mappings.台湾.台北市;

import carpool.mappings.MappingBase;
import carpool.mappings.台湾.台北市.台北区.台北区Mappings;

public class 台北市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("台北区",new 台北区Mappings());

    }

}
