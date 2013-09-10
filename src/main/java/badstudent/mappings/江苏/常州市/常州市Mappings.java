package badstudent.mappings.江苏.常州市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.江苏.常州市.常州区.常州区Mappings;

public class 常州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("常州区",new 常州区Mappings());

    }

}
