package badstudent.mappings.福建;

import badstudent.mappings.MappingBase;
import badstudent.mappings.福建.厦门市.厦门市Mappings;
import badstudent.mappings.福建.泉州市.泉州市Mappings;
import badstudent.mappings.福建.福州市.福州市Mappings;

public class 福建Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("福州市",new 福州市Mappings());
        subAreaMappings.put("泉州市",new 泉州市Mappings());
        subAreaMappings.put("厦门市",new 厦门市Mappings());


    }

}
