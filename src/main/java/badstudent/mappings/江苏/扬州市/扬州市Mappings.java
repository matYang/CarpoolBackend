package badstudent.mappings.江苏.扬州市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.江苏.扬州市.扬州区.扬州区Mappings;

public class 扬州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("扬州区",new 扬州区Mappings());

    }

}
