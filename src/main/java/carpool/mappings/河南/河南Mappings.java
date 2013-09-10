package carpool.mappings.河南;

import carpool.mappings.MappingBase;
import carpool.mappings.河南.开封市.开封市Mappings;
import carpool.mappings.河南.新乡市.新乡市Mappings;
import carpool.mappings.河南.洛阳市.洛阳市Mappings;
import carpool.mappings.河南.焦作市.焦作市Mappings;
import carpool.mappings.河南.郑州市.郑州市Mappings;

public class 河南Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("焦作市",new 焦作市Mappings());
        subAreaMappings.put("开封市",new 开封市Mappings());
        subAreaMappings.put("洛阳市",new 洛阳市Mappings());
        subAreaMappings.put("新乡市",new 新乡市Mappings());
        subAreaMappings.put("郑州市",new 郑州市Mappings());


    }

}
