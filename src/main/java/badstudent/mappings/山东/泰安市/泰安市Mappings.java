package badstudent.mappings.山东.泰安市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.山东.泰安市.泰安区.泰安区Mappings;

public class 泰安市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("泰安区",new 泰安区Mappings());

    }

}