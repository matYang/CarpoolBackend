package carpool.mappings.北京;

import carpool.mappings.MappingBase;
import carpool.mappings.北京.北京市.北京市Mappings;

public class 北京Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("北京市",new 北京市Mappings());

    }

}
