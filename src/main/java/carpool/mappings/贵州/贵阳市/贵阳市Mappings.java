package carpool.mappings.贵州.贵阳市;

import carpool.mappings.MappingBase;
import carpool.mappings.贵州.贵阳市.花溪大学城.花溪大学城Mappings;
import carpool.mappings.贵州.贵阳市.贵阳区.贵阳区Mappings;

public class 贵阳市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("贵阳区",new 贵阳区Mappings());
        subAreaMappings.put("花溪大学城",new 花溪大学城Mappings());

    }

}
