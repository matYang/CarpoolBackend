package carpool.mappings.辽宁.沈阳市;

import carpool.mappings.MappingBase;
import carpool.mappings.辽宁.沈阳市.沈北大学城.沈北大学城Mappings;
import carpool.mappings.辽宁.沈阳市.沈阳区.沈阳区Mappings;
import carpool.mappings.辽宁.沈阳市.浑南地区.浑南地区Mappings;

public class 沈阳市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("沈阳区",new 沈阳区Mappings());
        subAreaMappings.put("浑南地区",new 浑南地区Mappings());
        subAreaMappings.put("沈北大学城",new 沈北大学城Mappings());

    }

}
