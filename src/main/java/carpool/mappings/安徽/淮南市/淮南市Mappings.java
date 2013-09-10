package carpool.mappings.安徽.淮南市;

import carpool.mappings.MappingBase;
import carpool.mappings.安徽.淮南市.淮南区.淮南区Mappings;

public class 淮南市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("淮南区",new 淮南区Mappings());

    }

}

