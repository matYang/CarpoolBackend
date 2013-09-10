package carpool.mappings.山西.临汾市;

import carpool.mappings.MappingBase;
import carpool.mappings.山西.临汾市.临汾区.临汾区Mappings;

public class 临汾市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("临汾区",new 临汾区Mappings());

    }

}
