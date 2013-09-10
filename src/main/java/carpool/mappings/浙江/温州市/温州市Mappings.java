package carpool.mappings.浙江.温州市;

import carpool.mappings.MappingBase;
import carpool.mappings.浙江.温州市.温州区.温州区Mappings;

public class 温州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("温州区",new 温州区Mappings());

    }

}
