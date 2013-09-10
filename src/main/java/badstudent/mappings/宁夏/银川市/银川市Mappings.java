package badstudent.mappings.宁夏.银川市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.宁夏.银川市.银川区.银川区Mappings;

public class 银川市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("银川区",new 银川区Mappings());

    }

}
