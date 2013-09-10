package badstudent.mappings.江西.吉安市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.江西.吉安市.吉安区.吉安区Mappings;

public class 吉安市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("吉安区",new 吉安区Mappings());

    }

}
