package carpool.mappings.江西;

import carpool.mappings.MappingBase;
import carpool.mappings.江西.南昌市.南昌市Mappings;
import carpool.mappings.江西.吉安市.吉安市Mappings;
import carpool.mappings.江西.抚州市.抚州市Mappings;
import carpool.mappings.江西.赣州市.赣州市Mappings;

public class 江西Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("抚州市",new 抚州市Mappings());
        subAreaMappings.put("赣州市",new 赣州市Mappings());
        subAreaMappings.put("吉安市",new 吉安市Mappings());
        subAreaMappings.put("南昌市",new 南昌市Mappings());


    }

}
