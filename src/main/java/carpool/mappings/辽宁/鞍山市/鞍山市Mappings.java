package carpool.mappings.辽宁.鞍山市;

import carpool.mappings.MappingBase;
import carpool.mappings.辽宁.鞍山市.鞍山区.鞍山区Mappings;

public class 鞍山市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("鞍山区",new 鞍山区Mappings());

    }

}
