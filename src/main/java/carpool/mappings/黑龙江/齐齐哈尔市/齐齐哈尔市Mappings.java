package carpool.mappings.黑龙江.齐齐哈尔市;

import carpool.mappings.MappingBase;
import carpool.mappings.黑龙江.齐齐哈尔市.齐齐哈尔区.齐齐哈尔区Mappings;

public class 齐齐哈尔市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("齐齐哈尔区",new 齐齐哈尔区Mappings());

    }

}
