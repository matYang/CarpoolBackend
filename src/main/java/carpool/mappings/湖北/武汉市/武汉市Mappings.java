package carpool.mappings.湖北.武汉市;

import carpool.mappings.MappingBase;
import carpool.mappings.湖北.武汉市.武昌区.武昌区Mappings;
import carpool.mappings.湖北.武汉市.武汉区.武汉区Mappings;

public class 武汉市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("武汉区",new 武汉区Mappings());
        subAreaMappings.put("武昌区",new 武昌区Mappings());

    }

}
