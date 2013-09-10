package carpool.mappings.新疆.乌鲁木齐市;

import carpool.mappings.MappingBase;
import carpool.mappings.新疆.乌鲁木齐市.乌鲁木齐区.乌鲁木齐区Mappings;

public class 乌鲁木齐市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("乌鲁木齐区",new 乌鲁木齐区Mappings());

    }

}
