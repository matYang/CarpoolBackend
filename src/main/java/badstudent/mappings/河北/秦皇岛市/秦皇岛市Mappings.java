package badstudent.mappings.河北.秦皇岛市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.河北.秦皇岛市.秦皇岛区.秦皇岛区Mappings;

public class 秦皇岛市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("秦皇岛区",new 秦皇岛区Mappings());

    }

}
