package carpool.mappings.吉林.四平市;

import carpool.mappings.MappingBase;
import carpool.mappings.吉林.四平市.四平区.四平区Mappings;

public class 四平市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("四平区",new 四平区Mappings());

    }

}
