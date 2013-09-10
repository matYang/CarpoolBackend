package badstudent.mappings.四川.雅安市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.四川.雅安市.雅安区.雅安区Mappings;

public class 雅安市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("雅安区",new 雅安区Mappings());

    }

}
