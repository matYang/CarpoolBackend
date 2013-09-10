package badstudent.mappings.安徽.蚌埠市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.安徽.蚌埠市.蚌埠区.蚌埠区Mappings;

public class 蚌埠市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("蚌埠区",new 蚌埠区Mappings());

    }

}
