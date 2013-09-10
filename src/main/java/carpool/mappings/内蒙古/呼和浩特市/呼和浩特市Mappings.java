package carpool.mappings.内蒙古.呼和浩特市;

import carpool.mappings.MappingBase;
import carpool.mappings.内蒙古.呼和浩特市.呼和浩特区.呼和浩特区Mappings;

public class 呼和浩特市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("呼和浩特区",new 呼和浩特区Mappings());

    }

}
