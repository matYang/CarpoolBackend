package carpool.mappings.山西.太谷县;

import carpool.mappings.MappingBase;
import carpool.mappings.山西.太谷县.太谷区.太谷区Mappings;

public class 太谷县Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("太谷区",new 太谷区Mappings());

    }

}
