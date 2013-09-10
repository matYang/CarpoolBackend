package badstudent.mappings.福建.泉州市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.福建.泉州市.泉州区.泉州区Mappings;

public class 泉州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("泉州区",new 泉州区Mappings());

    }

}
