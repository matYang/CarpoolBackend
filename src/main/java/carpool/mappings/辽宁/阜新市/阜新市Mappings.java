package carpool.mappings.辽宁.阜新市;

import carpool.mappings.MappingBase;
import carpool.mappings.辽宁.阜新市.阜新区.阜新区Mappings;

public class 阜新市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("阜新区",new 阜新区Mappings());

    }

}
