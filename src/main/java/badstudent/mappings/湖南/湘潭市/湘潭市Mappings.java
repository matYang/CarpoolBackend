package badstudent.mappings.湖南.湘潭市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.湖南.湘潭市.湘潭区.湘潭区Mappings;

public class 湘潭市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("湘潭区",new 湘潭区Mappings());

    }

}
