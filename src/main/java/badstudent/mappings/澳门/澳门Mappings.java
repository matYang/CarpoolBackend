package badstudent.mappings.澳门;

import badstudent.mappings.MappingBase;
import badstudent.mappings.澳门.澳门特区.澳门特区Mappings;

public class 澳门Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("澳门特区",new 澳门特区Mappings());


    }

}
