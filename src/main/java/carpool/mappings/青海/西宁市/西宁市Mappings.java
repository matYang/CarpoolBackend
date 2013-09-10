package carpool.mappings.青海.西宁市;

import carpool.mappings.MappingBase;
import carpool.mappings.青海.西宁市.西宁区.西宁区Mappings;

public class 西宁市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("西宁区",new 西宁区Mappings());

    }

}
