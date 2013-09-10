package badstudent.mappings.江苏.南京市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.江苏.南京市.仙林大学城.仙林大学城Mappings;
import badstudent.mappings.江苏.南京市.南京区.南京区Mappings;
import badstudent.mappings.江苏.南京市.江宁大学城.江宁大学城Mappings;
import badstudent.mappings.江苏.南京市.江浦大学城.江浦大学城Mappings;

public class 南京市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("南京区",new 南京区Mappings());
        subAreaMappings.put("江宁大学城",new 江宁大学城Mappings());
        subAreaMappings.put("江浦大学城",new 江浦大学城Mappings());
        subAreaMappings.put("仙林大学城",new 仙林大学城Mappings());

    }

}
