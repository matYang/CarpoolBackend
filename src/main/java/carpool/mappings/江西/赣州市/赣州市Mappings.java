package carpool.mappings.江西.赣州市;

import carpool.mappings.MappingBase;
import carpool.mappings.江西.赣州市.赣州区.赣州区Mappings;

public class 赣州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("赣州区",new 赣州区Mappings());

    }

}
