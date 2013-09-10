package badstudent.mappings.广东.深圳市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.广东.深圳市.深圳区.深圳区Mappings;

public class 深圳市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("深圳区",new 深圳区Mappings());

    }

}
