package badstudent.mappings.上海;

import badstudent.mappings.MappingBase;
import badstudent.mappings.上海.上海市.上海市Mappings;

public class 上海Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("上海市",new 上海市Mappings());


    }

}
