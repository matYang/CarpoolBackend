package carpool.mappings.内蒙古.通辽市;

import carpool.mappings.MappingBase;
import carpool.mappings.内蒙古.通辽市.通辽区.通辽区Mappings;

public class 通辽市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("通辽区",new 通辽区Mappings());

    }

}
