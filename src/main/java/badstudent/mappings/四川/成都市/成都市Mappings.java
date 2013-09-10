package badstudent.mappings.四川.成都市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.四川.成都市.成都区.成都区Mappings;

public class 成都市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("成都区",new 成都区Mappings());

    }

}
