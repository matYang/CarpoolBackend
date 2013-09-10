package badstudent.mappings.江苏.南通市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.江苏.南通市.南通区.南通区Mappings;

public class 南通市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("南通区",new 南通区Mappings());

    }

}
