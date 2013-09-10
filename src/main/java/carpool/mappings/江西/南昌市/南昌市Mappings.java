package carpool.mappings.江西.南昌市;

import carpool.mappings.MappingBase;
import carpool.mappings.江西.南昌市.南昌区.南昌区Mappings;

public class 南昌市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("南昌区",new 南昌区Mappings());

    }

}
