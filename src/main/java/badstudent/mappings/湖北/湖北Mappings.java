package badstudent.mappings.湖北;

import badstudent.mappings.MappingBase;
import badstudent.mappings.湖北.宜昌市.宜昌市Mappings;
import badstudent.mappings.湖北.武汉市.武汉市Mappings;
import badstudent.mappings.湖北.荆州市.荆州市Mappings;

public class 湖北Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("荆州市",new 荆州市Mappings());
        subAreaMappings.put("宜昌市",new 宜昌市Mappings());
        subAreaMappings.put("武汉市",new 武汉市Mappings());


    }

}
