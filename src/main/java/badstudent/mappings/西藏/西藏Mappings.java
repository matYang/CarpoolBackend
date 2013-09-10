package badstudent.mappings.西藏;

import badstudent.mappings.MappingBase;
import badstudent.mappings.西藏.拉萨市.拉萨市Mappings;

public class 西藏Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("拉萨市",new 拉萨市Mappings());


    }

}
