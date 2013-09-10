package badstudent.mappings.黑龙江.哈尔滨市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.黑龙江.哈尔滨市.哈尔滨区.哈尔滨区Mappings;

public class 哈尔滨市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("哈尔滨区",new 哈尔滨区Mappings());

    }

}
