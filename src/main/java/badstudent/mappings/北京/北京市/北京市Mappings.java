package badstudent.mappings.北京.北京市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.北京.北京市.东方大学城.东方大学城Mappings;
import badstudent.mappings.北京.北京市.北京区.北京区Mappings;
import badstudent.mappings.北京.北京市.沙河大学城.沙河大学城Mappings;
import badstudent.mappings.北京.北京市.良乡大学城.良乡大学城Mappings;

public class 北京市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("北京区",new 北京区Mappings());
        subAreaMappings.put("东方大学城",new 东方大学城Mappings());
        subAreaMappings.put("良乡大学城",new 良乡大学城Mappings());
        subAreaMappings.put("沙河大学城",new 沙河大学城Mappings());

    }

}
