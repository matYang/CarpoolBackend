package badstudent.mappings.山西.临汾市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.山西.临汾市.临汾区.临汾区Mappings;

public class 临汾市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("临汾区",new 临汾区Mappings());

    }

}
