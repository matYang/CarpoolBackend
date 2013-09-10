package carpool.mappings.浙江.宁波市;

import carpool.mappings.MappingBase;
import carpool.mappings.浙江.宁波市.宁波区.宁波区Mappings;

public class 宁波市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("宁波区",new 宁波区Mappings());

    }

}
