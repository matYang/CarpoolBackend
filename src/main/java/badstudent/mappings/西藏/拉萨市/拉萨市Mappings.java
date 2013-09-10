package badstudent.mappings.西藏.拉萨市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.西藏.拉萨市.拉萨区.拉萨区Mappings;

public class 拉萨市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("拉萨区",new 拉萨区Mappings());

    }

}
