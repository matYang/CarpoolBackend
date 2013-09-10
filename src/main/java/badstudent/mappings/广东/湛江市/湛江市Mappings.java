package badstudent.mappings.广东.湛江市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.广东.湛江市.湛江区.湛江区Mappings;

public class 湛江市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("湛江区",new 湛江区Mappings());

    }

}
