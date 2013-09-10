package carpool.mappings.台湾;

import carpool.mappings.MappingBase;
import carpool.mappings.台湾.台北市.台北市Mappings;

public class 台湾Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("台北市",new 台北市Mappings());


    }

}
