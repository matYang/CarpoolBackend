package badstudent.mappings.湖北.荆州市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.湖北.荆州市.荆州区.荆州区Mappings;

public class 荆州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("荆州区",new 荆州区Mappings());

    }

}
