package carpool.mappings.陕西.杨凌市;

import carpool.mappings.MappingBase;
import carpool.mappings.陕西.杨凌市.杨凌区.杨凌区Mappings;

public class 杨凌市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("杨凌区",new 杨凌区Mappings());

    }

}
