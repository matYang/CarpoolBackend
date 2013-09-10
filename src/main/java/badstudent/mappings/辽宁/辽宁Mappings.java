package badstudent.mappings.辽宁;

import badstudent.mappings.MappingBase;
import badstudent.mappings.辽宁.大连市.大连市Mappings;
import badstudent.mappings.辽宁.沈阳市.沈阳市Mappings;
import badstudent.mappings.辽宁.锦州市.锦州市Mappings;
import badstudent.mappings.辽宁.阜新市.阜新市Mappings;
import badstudent.mappings.辽宁.鞍山市.鞍山市Mappings;

public class 辽宁Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("鞍山市",new 鞍山市Mappings());
        subAreaMappings.put("大连市",new 大连市Mappings());
        subAreaMappings.put("阜新市",new 阜新市Mappings());
        subAreaMappings.put("锦州市",new 锦州市Mappings());
        subAreaMappings.put("沈阳市",new 沈阳市Mappings());


    }

}