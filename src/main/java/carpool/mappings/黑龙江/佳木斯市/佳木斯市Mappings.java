package carpool.mappings.黑龙江.佳木斯市;

import carpool.mappings.MappingBase;
import carpool.mappings.黑龙江.佳木斯市.佳木斯区.佳木斯区Mappings;

public class 佳木斯市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("佳木斯区",new 佳木斯区Mappings());

    }

}
