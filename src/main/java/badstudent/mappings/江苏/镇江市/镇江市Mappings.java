package badstudent.mappings.江苏.镇江市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.江苏.镇江市.镇江区.镇江区Mappings;

public class 镇江市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("镇江区",new 镇江区Mappings());

    }

}
