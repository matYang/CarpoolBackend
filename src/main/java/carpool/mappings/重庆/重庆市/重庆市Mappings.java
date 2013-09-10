package carpool.mappings.重庆.重庆市;

import carpool.mappings.MappingBase;
import carpool.mappings.重庆.重庆市.重庆区.重庆区Mappings;

public class 重庆市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("重庆区",new 重庆区Mappings());


    }

}
