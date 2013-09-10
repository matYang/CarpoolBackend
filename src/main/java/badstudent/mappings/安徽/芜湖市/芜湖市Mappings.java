package badstudent.mappings.安徽.芜湖市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.安徽.芜湖市.芜湖区.芜湖区Mappings;

public class 芜湖市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("芜湖区",new 芜湖区Mappings());

    }

}
