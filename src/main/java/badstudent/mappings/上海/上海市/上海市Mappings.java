package badstudent.mappings.上海.上海市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.上海.上海市.奉贤大学城.奉贤大学城Mappings;
import badstudent.mappings.上海.上海市.闵行大学城.闵行大学城Mappings;
import badstudent.mappings.上海.上海市.浦东新区.浦东新区Mappings;
import badstudent.mappings.上海.上海市.松江大学城.松江大学城Mappings;
import badstudent.mappings.上海.上海市.杨浦大学城.杨浦大学城Mappings;

public class 上海市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("奉贤大学城",new 奉贤大学城Mappings());
        subAreaMappings.put("闵行大学城",new 闵行大学城Mappings());
        subAreaMappings.put("浦东新区",new 浦东新区Mappings());
        subAreaMappings.put("松江大学城",new 松江大学城Mappings());
        subAreaMappings.put("杨浦大学城",new 杨浦大学城Mappings());


    }

}