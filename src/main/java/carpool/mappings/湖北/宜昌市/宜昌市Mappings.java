package carpool.mappings.湖北.宜昌市;

import carpool.mappings.MappingBase;
import carpool.mappings.湖北.宜昌市.宜昌区.宜昌区Mappings;

public class 宜昌市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("宜昌区",new 宜昌区Mappings());

    }

}
