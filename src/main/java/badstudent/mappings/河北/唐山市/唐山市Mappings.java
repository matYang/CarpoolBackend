package badstudent.mappings.河北.唐山市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.河北.唐山市.唐山区.唐山区Mappings;

public class 唐山市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("唐山区",new 唐山区Mappings());

    }

}
