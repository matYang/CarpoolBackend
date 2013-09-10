package carpool.mappings.山西;

import carpool.mappings.MappingBase;
import carpool.mappings.山西.临汾市.临汾市Mappings;
import carpool.mappings.山西.大同市.大同市Mappings;
import carpool.mappings.山西.太原市.太原市Mappings;
import carpool.mappings.山西.太谷县.太谷县Mappings;

public class 山西Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("大同市",new 大同市Mappings());
        subAreaMappings.put("临汾市",new 临汾市Mappings());
        subAreaMappings.put("太谷县",new 太谷县Mappings());
        subAreaMappings.put("太原市",new 太原市Mappings());


    }

}
