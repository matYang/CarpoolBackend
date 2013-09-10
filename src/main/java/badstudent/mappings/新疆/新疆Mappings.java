package badstudent.mappings.新疆;

import badstudent.mappings.MappingBase;
import badstudent.mappings.新疆.乌鲁木齐市.乌鲁木齐市Mappings;
import badstudent.mappings.新疆.石河子市.石河子市Mappings;
import badstudent.mappings.新疆.阿拉尔市.阿拉尔市Mappings;


public class 新疆Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("阿拉尔市",new 阿拉尔市Mappings());
        subAreaMappings.put("石河子市",new 石河子市Mappings());
        subAreaMappings.put("乌鲁木齐市",new 乌鲁木齐市Mappings());


    }

}
