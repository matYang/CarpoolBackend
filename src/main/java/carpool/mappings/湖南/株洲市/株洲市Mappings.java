package carpool.mappings.湖南.株洲市;

import carpool.mappings.MappingBase;
import carpool.mappings.湖南.株洲市.株洲区.株洲区Mappings;

public class 株洲市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("株洲区",new 株洲区Mappings());

    }

}
