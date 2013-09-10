package carpool.mappings.四川.绵阳市;

import carpool.mappings.MappingBase;
import carpool.mappings.四川.绵阳市.绵阳区.绵阳区Mappings;

public class 绵阳市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("绵阳区",new 绵阳区Mappings());

    }

}
