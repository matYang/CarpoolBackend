package carpool.mappings.广东;

import carpool.mappings.MappingBase;
import carpool.mappings.广东.广州市.广州市Mappings;
import carpool.mappings.广东.汕头市.汕头市Mappings;
import carpool.mappings.广东.江门市.江门市Mappings;
import carpool.mappings.广东.深圳市.深圳市Mappings;
import carpool.mappings.广东.湛江市.湛江市Mappings;
import carpool.mappings.广东.珠海市.珠海市Mappings;

public class 广东Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("广州市",new 广州市Mappings());
        subAreaMappings.put("江门市",new 江门市Mappings());
        subAreaMappings.put("汕头市",new 汕头市Mappings());
        subAreaMappings.put("深圳市",new 深圳市Mappings());
        subAreaMappings.put("湛江市",new 湛江市Mappings());
        subAreaMappings.put("珠海市",new 珠海市Mappings());


    }

}