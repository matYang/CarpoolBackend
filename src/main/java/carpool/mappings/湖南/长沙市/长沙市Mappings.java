package carpool.mappings.湖南.长沙市;

import carpool.mappings.MappingBase;
import carpool.mappings.湖南.长沙市.长沙区.长沙区Mappings;

public class 长沙市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("长沙区",new 长沙区Mappings());

    }

}
