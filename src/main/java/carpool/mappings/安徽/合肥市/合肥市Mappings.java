package carpool.mappings.安徽.合肥市;

import carpool.mappings.MappingBase;
import carpool.mappings.安徽.合肥市.合肥区.合肥区Mappings;
import carpool.mappings.安徽.合肥市.合肥大学城.合肥大学城Mappings;

public class 合肥市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("合肥区",new 合肥区Mappings());
        subAreaMappings.put("合肥大学城",new 合肥大学城Mappings());

    }

}
