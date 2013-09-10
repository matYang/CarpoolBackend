package badstudent.mappings.河南.郑州市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.河南.郑州市.郑州区.郑州区Mappings;

public class 郑州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("郑州区",new 郑州区Mappings());

    }

}
