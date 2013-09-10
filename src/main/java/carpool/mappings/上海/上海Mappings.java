package carpool.mappings.上海;

import carpool.mappings.MappingBase;
import carpool.mappings.上海.上海市.上海市Mappings;

public class 上海Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("上海市",new 上海市Mappings());


    }

}
