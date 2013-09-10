package badstudent.mappings.吉林.吉林市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.吉林.吉林市.吉林区.吉林区Mappings;

public class 吉林市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("吉林区",new 吉林区Mappings());

    }

}
