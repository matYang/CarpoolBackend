package carpool.mappings.安徽.淮北市;

import carpool.mappings.MappingBase;
import carpool.mappings.安徽.淮北市.淮北区.淮北区Mappings;

public class 淮北市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("淮北区",new 淮北区Mappings());

    }

}
