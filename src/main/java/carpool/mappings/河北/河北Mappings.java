package carpool.mappings.河北;

import carpool.mappings.MappingBase;
import carpool.mappings.河北.保定市.保定市Mappings;
import carpool.mappings.河北.唐山市.唐山市Mappings;
import carpool.mappings.河北.石家庄市.石家庄市Mappings;
import carpool.mappings.河北.秦皇岛市.秦皇岛市Mappings;
import carpool.mappings.河北.邯郸市.邯郸市Mappings;

public class 河北Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("保定市",new 保定市Mappings());
        subAreaMappings.put("邯郸市",new 邯郸市Mappings());
        subAreaMappings.put("秦皇岛市",new 秦皇岛市Mappings());
        subAreaMappings.put("石家庄市",new 石家庄市Mappings());
        subAreaMappings.put("唐山市",new 唐山市Mappings());


    }

}
