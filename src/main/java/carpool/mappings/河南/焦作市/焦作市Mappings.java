package carpool.mappings.河南.焦作市;

import carpool.mappings.MappingBase;
import carpool.mappings.河南.焦作市.焦作区.焦作区Mappings;

public class 焦作市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("焦作区",new 焦作区Mappings());

    }

}
