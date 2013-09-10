package badstudent.mappings.浙江.杭州市;

import badstudent.mappings.MappingBase;
import badstudent.mappings.浙江.杭州市.下沙大学城.下沙大学城Mappings;
import badstudent.mappings.浙江.杭州市.杭州区.杭州区Mappings;
import badstudent.mappings.浙江.杭州市.滨江大学城.滨江大学城Mappings;

public class 杭州市Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("杭州区",new 杭州区Mappings());
        subAreaMappings.put("滨江大学城",new 滨江大学城Mappings());
        subAreaMappings.put("下沙大学城",new 下沙大学城Mappings());

    }

}
