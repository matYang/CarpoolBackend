package badstudent.mappings.河南.洛阳市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.河南.洛阳市.洛阳区.洛阳区Mappings;

public class 洛阳市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("洛阳区",new 洛阳区Mappings());

    }

}
