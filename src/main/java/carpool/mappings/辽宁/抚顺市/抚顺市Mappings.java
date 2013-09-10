package carpool.mappings.辽宁.抚顺市;

import carpool.mappings.MappingBase;
import carpool.mappings.辽宁.抚顺市.抚顺区.抚顺区Mappings;

public class 抚顺市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("抚顺区",new 抚顺区Mappings());

    }

}
