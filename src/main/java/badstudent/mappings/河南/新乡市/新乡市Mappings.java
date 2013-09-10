package badstudent.mappings.河南.新乡市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.河南.新乡市.新乡区.新乡区Mappings;

public class 新乡市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("新乡区",new 新乡区Mappings());

    }

}
