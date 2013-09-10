package badstudent.mappings.河北.保定市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.河北.保定市.保定区.保定区Mappings;

public class 保定市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("保定区",new 保定区Mappings());

    }

}
