package com.netaporter.productservice.util;

import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;

import static com.netaporter.test.utils.enums.RegionEnum.AM;
import static com.netaporter.test.utils.enums.RegionEnum.APAC;
import static com.netaporter.test.utils.enums.RegionEnum.INTL;

/**
 * Created by jgchristian on 14/01/2014.
 */
public class ChannelConverter {

    public static RegionEnum convertProductChannel(SalesChannelEnum channel){
        RegionEnum region = null;
        switch (channel) {
            case NAP_INTL:
                region = INTL;
                break;
            case NAP_AM:
                region = AM;
                break;
            case NAP_APAC:
                region = APAC;
                break;
            case MRP_INTL:
                break;
            case MRP_AM:
                break;
        }

        return region;
    }
}
