package carpool.mappings.湖南.衡阳市;

import carpool.mappings.MappingBase;
import carpool.mappings.湖南.衡阳市.衡阳区.衡阳区Mappings;

public class 衡阳市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("衡阳区",new 衡阳区Mappings());

    }

}
