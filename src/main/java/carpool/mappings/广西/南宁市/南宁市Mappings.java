package carpool.mappings.广西.南宁市;

import carpool.mappings.MappingBase;
import carpool.mappings.广西.南宁市.南宁区.南宁区Mappings;

public class 南宁市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("南宁区",new 南宁区Mappings());

    }

}
