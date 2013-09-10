package badstudent.mappings.甘肃.兰州市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.甘肃.兰州市.兰州区.兰州区Mappings;

public class 兰州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("兰州区",new 兰州区Mappings());

    }

}
