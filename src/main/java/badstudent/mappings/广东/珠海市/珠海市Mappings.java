package badstudent.mappings.广东.珠海市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.广东.珠海市.珠海区.珠海区Mappings;

public class 珠海市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("珠海区",new 珠海区Mappings());

    }

}
