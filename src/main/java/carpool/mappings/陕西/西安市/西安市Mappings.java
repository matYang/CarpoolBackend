package carpool.mappings.陕西.西安市;

import carpool.mappings.MappingBase;
import carpool.mappings.陕西.西安市.西安区.西安区Mappings;
import carpool.mappings.陕西.西安市.长安大学城.长安大学城Mappings;

public class 西安市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("西安区",new 西安区Mappings());
        subAreaMappings.put("长安大学城",new 长安大学城Mappings());

    }

}
