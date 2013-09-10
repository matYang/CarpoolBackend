package badstudent.mappings.广西.桂林市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.广西.桂林市.桂林区.桂林区Mappings;

public class 桂林市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("桂林区",new 桂林区Mappings());

    }

}

