package badstudent.mappings.重庆;

import badstudent.mappings.MappingBase;
import badstudent.mappings.重庆.重庆市.重庆市Mappings;

public class 重庆Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("重庆市",new 重庆市Mappings());


    }

}
