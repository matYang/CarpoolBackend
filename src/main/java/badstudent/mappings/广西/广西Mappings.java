package badstudent.mappings.广西;

import badstudent.mappings.MappingBase;
import badstudent.mappings.广西.南宁市.南宁市Mappings;
import badstudent.mappings.广西.桂林市.桂林市Mappings;

public class 广西Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("桂林市",new 桂林市Mappings());
        subAreaMappings.put("南宁市",new 南宁市Mappings());


    }

}
