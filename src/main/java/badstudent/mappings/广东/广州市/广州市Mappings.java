package badstudent.mappings.广东.广州市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.广东.广州市.广州区.广州区Mappings;
import badstudent.mappings.广东.广州市.广州大学城.广州大学城Mappings;

public class 广州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("广州区",new 广州区Mappings());
        subAreaMappings.put("广州大学城",new 广州大学城Mappings());

    }

}
