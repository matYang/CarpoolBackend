package carpool.mappings.新疆.阿拉尔市;

import carpool.mappings.MappingBase;
import carpool.mappings.新疆.阿拉尔市.阿拉尔区.阿拉尔区Mappings;

public class 阿拉尔市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("阿拉尔区",new 阿拉尔区Mappings());

    }

}
