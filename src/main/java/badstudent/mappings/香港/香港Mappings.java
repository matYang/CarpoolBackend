package badstudent.mappings.香港;

import badstudent.mappings.MappingBase;
import badstudent.mappings.香港.香港特区.香港特区Mappings;

public class 香港Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("香港特区",new 香港特区Mappings());


    }

}
