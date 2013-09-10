package badstudent.mappings.河北.邯郸市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.河北.邯郸市.邯郸区.邯郸区Mappings;

public class 邯郸市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("邯郸区",new 邯郸区Mappings());

    }

}
