package badstudent.mappings.山东.济南市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.山东.济南市.济南区.济南区Mappings;

public class 济南市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("济南区",new 济南区Mappings());

    }

}
