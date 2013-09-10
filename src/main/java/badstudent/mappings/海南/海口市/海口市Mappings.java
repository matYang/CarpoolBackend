package badstudent.mappings.海南.海口市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.海南.海口市.海口区.海口区Mappings;

public class 海口市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("海口区",new 海口区Mappings());

    }

}
