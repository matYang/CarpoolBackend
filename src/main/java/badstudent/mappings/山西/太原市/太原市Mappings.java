package badstudent.mappings.山西.太原市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.山西.太原市.太原区.太原区Mappings;

public class 太原市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("太原区",new 太原区Mappings());

    }

}
