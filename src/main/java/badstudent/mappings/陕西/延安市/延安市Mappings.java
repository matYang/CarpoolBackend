package badstudent.mappings.陕西.延安市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.陕西.延安市.延安区.延安区Mappings;

public class 延安市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("延安区",new 延安区Mappings());

    }

}
