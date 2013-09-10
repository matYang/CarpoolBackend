package badstudent.mappings.四川;

import badstudent.mappings.MappingBase;
import badstudent.mappings.四川.南充市.南充市Mappings;
import badstudent.mappings.四川.成都市.成都市Mappings;
import badstudent.mappings.四川.绵阳市.绵阳市Mappings;
import badstudent.mappings.四川.雅安市.雅安市Mappings;

public class 四川Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("成都市",new 成都市Mappings());
        subAreaMappings.put("绵阳市",new 绵阳市Mappings());
        subAreaMappings.put("南充市",new 南充市Mappings());
        subAreaMappings.put("雅安市",new 雅安市Mappings());


    }

}
