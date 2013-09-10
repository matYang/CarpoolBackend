package badstudent.mappings.黑龙江;

import badstudent.mappings.MappingBase;
import badstudent.mappings.黑龙江.佳木斯市.佳木斯市Mappings;
import badstudent.mappings.黑龙江.哈尔滨市.哈尔滨市Mappings;
import badstudent.mappings.黑龙江.牡丹江市.牡丹江市Mappings;
import badstudent.mappings.黑龙江.鸡西市.鸡西市Mappings;
import badstudent.mappings.黑龙江.齐齐哈尔市.齐齐哈尔市Mappings;

public class 黑龙江Mappings extends MappingBase {

    @Override
    protected void initMappings() {
        subAreaMappings.put("哈尔滨市",new 哈尔滨市Mappings());
        subAreaMappings.put("鸡西市",new 鸡西市Mappings());
        subAreaMappings.put("佳木斯市",new 佳木斯市Mappings());
        subAreaMappings.put("牡丹江市",new 牡丹江市Mappings());
        subAreaMappings.put("齐齐哈尔市",new 齐齐哈尔市Mappings());


    }

}
