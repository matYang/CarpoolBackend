package carpool.mappings.贵州;

import carpool.mappings.MappingBase;
import carpool.mappings.贵州.贵阳市.贵阳市Mappings;

public class 贵州Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("贵阳市",new 贵阳市Mappings());


    }

}
