package badstudent.mappings.浙江;

import badstudent.mappings.MappingBase;
import badstudent.mappings.浙江.宁波市.宁波市Mappings;
import badstudent.mappings.浙江.杭州市.杭州市Mappings;
import badstudent.mappings.浙江.温州市.温州市Mappings;
import badstudent.mappings.浙江.金华市.金华市Mappings;

public class 浙江Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("杭州市",new 杭州市Mappings());
        subAreaMappings.put("金华市",new 金华市Mappings());
        subAreaMappings.put("宁波市",new 宁波市Mappings());
        subAreaMappings.put("温州市",new 温州市Mappings());


    }

}
