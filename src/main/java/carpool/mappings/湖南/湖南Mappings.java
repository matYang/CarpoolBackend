package carpool.mappings.湖南;

import carpool.mappings.MappingBase;
import carpool.mappings.湖南.吉首市.吉首市Mappings;
import carpool.mappings.湖南.株洲市.株洲市Mappings;
import carpool.mappings.湖南.湘潭市.湘潭市Mappings;
import carpool.mappings.湖南.衡阳市.衡阳市Mappings;
import carpool.mappings.湖南.长沙市.长沙市Mappings;

public class 湖南Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("衡阳市",new 衡阳市Mappings());
        subAreaMappings.put("吉首市",new 吉首市Mappings());
        subAreaMappings.put("湘潭市",new 湘潭市Mappings());
        subAreaMappings.put("长沙市",new 长沙市Mappings());
        subAreaMappings.put("株洲市",new 株洲市Mappings());


    }

}
