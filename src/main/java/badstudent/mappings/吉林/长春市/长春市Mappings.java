package badstudent.mappings.吉林.长春市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.吉林.长春市.长春区.长春区Mappings;

public class 长春市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("长春区",new 长春区Mappings());

    }

}
