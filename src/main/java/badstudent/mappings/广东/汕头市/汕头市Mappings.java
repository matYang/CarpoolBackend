package badstudent.mappings.广东.汕头市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.广东.汕头市.汕头区.汕头区Mappings;

public class 汕头市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("汕头区",new 汕头区Mappings());

    }

}
