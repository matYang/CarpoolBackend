package carpool.mappings.山西.大同市;

import carpool.mappings.MappingBase;
import carpool.mappings.山西.大同市.大同区.大同区Mappings;

public class 大同市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("大同区",new 大同区Mappings());

    }

}
