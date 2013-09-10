package carpool.mappings.天津;

import carpool.mappings.MappingBase;
import carpool.mappings.天津.天津市.天津市Mappings;

public class 天津Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("天津市",new 天津市Mappings());


    }

}
