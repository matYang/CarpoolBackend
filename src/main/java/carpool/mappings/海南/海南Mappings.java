package carpool.mappings.海南;

import carpool.mappings.MappingBase;
import carpool.mappings.海南.海口市.海口市Mappings;

public class 海南Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("海口市",new 海口市Mappings());


    }

}
