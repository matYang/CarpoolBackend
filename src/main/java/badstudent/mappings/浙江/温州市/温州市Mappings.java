package badstudent.mappings.浙江.温州市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.浙江.温州市.温州区.温州区Mappings;

public class 温州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("温州区",new 温州区Mappings());

    }

}
