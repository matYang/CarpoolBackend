package carpool.mappings.辽宁.锦州市;

import carpool.mappings.MappingBase;
import carpool.mappings.辽宁.锦州市.锦州区.锦州区Mappings;

public class 锦州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("锦州区",new 锦州区Mappings());

    }

}
