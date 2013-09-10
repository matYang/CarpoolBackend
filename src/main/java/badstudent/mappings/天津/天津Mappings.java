package badstudent.mappings.天津;

import badstudent.mappings.MappingBase;
import badstudent.mappings.天津.天津市.天津市Mappings;

public class 天津Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("天津市",new 天津市Mappings());


    }

}
