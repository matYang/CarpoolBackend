package carpool.mappings.陕西;

import carpool.mappings.MappingBase;
import carpool.mappings.陕西.延安市.延安市Mappings;
import carpool.mappings.陕西.杨凌市.杨凌市Mappings;
import carpool.mappings.陕西.西安市.西安市Mappings;

public class 陕西Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("西安市",new 西安市Mappings());
        subAreaMappings.put("延安市",new 延安市Mappings());
        subAreaMappings.put("杨凌市",new 杨凌市Mappings());


    }

}
