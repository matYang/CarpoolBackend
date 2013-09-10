package badstudent.mappings.山东.淄博市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.山东.淄博市.淄博区.淄博区Mappings;

public class 淄博市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("淄博区",new 淄博区Mappings());

    }

}
