package badstudent.mappings.安徽;

import badstudent.mappings.MappingBase;
import badstudent.mappings.安徽.合肥市.合肥市Mappings;
import badstudent.mappings.安徽.淮北市.淮北市Mappings;
import badstudent.mappings.安徽.淮南市.淮南市Mappings;
import badstudent.mappings.安徽.芜湖市.芜湖市Mappings;
import badstudent.mappings.安徽.蚌埠市.蚌埠市Mappings;
import badstudent.mappings.安徽.马鞍山市.马鞍山市Mappings;

public class 安徽Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("合肥市",new 合肥市Mappings());
        subAreaMappings.put("淮北市",new 淮北市Mappings());
        subAreaMappings.put("蚌埠市",new 蚌埠市Mappings());
        subAreaMappings.put("淮南市",new 淮南市Mappings());
        subAreaMappings.put("马鞍山市",new 马鞍山市Mappings());
        subAreaMappings.put("芜湖市",new 芜湖市Mappings());


    }

}
