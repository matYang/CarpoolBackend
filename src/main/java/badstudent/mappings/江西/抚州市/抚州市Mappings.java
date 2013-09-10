package badstudent.mappings.江西.抚州市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.江西.抚州市.抚州区.抚州区Mappings;

public class 抚州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("抚州区",new 抚州区Mappings());

    }

}
