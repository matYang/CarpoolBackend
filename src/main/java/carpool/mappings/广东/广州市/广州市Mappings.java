package carpool.mappings.广东.广州市;

import carpool.mappings.MappingBase;
import carpool.mappings.广东.广州市.广州区.广州区Mappings;
import carpool.mappings.广东.广州市.广州大学城.广州大学城Mappings;

public class 广州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("广州区",new 广州区Mappings());
        subAreaMappings.put("广州大学城",new 广州大学城Mappings());

    }

}
