package badstudent.mappings.河南.开封市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.河南.开封市.开封区.开封区Mappings;

public class 开封市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("开封区",new 开封区Mappings());

    }

}
