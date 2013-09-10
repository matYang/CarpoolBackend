package carpool.mappings.山东;

import carpool.mappings.MappingBase;
import carpool.mappings.山东.泰安市.泰安市Mappings;
import carpool.mappings.山东.济南市.济南市Mappings;
import carpool.mappings.山东.济宁市.济宁市Mappings;
import carpool.mappings.山东.淄博市.淄博市Mappings;
import carpool.mappings.山东.烟台市.烟台市Mappings;
import carpool.mappings.山东.聊城市.聊城市Mappings;
import carpool.mappings.山东.青岛市.青岛市Mappings;

public class 山东Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("济南市",new 济南市Mappings());
        subAreaMappings.put("济宁市",new 济宁市Mappings());
        subAreaMappings.put("聊城市",new 聊城市Mappings());
        subAreaMappings.put("青岛市",new 青岛市Mappings());
        subAreaMappings.put("泰安市",new 泰安市Mappings());
        subAreaMappings.put("烟台市",new 烟台市Mappings());
        subAreaMappings.put("淄博市",new 淄博市Mappings());


    }

}