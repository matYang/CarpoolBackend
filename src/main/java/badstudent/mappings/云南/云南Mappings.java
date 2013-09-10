package badstudent.mappings.云南;

import badstudent.mappings.MappingBase;
import badstudent.mappings.云南.昆明市.昆明市Mappings;

public class 云南Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("昆明市",new 昆明市Mappings());


    }

}
