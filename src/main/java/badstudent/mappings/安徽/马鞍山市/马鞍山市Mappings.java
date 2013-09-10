package badstudent.mappings.安徽.马鞍山市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.安徽.马鞍山市.马鞍山区.马鞍山区Mappings;

public class 马鞍山市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("马鞍山区",new 马鞍山区Mappings());

    }

}
