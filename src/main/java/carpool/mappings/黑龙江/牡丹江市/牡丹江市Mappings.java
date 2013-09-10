package carpool.mappings.黑龙江.牡丹江市;

import carpool.mappings.MappingBase;
import carpool.mappings.黑龙江.牡丹江市.牡丹江区.牡丹江区Mappings;

public class 牡丹江市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("牡丹江区",new 牡丹江区Mappings());

    }

}
