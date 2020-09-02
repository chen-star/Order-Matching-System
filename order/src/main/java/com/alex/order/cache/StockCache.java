package com.alex.order.cache;

import com.alex.order.bean.res.StockInfo;
import com.alex.order.util.DbUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class StockCache {

    //Map<String,List<StockInfo>>
    // 6 --> 600086,600025...
    private HashMultimap<String, StockInfo> invertIndex =
            HashMultimap.create();

    public Collection<StockInfo> getStocks(String key) {
        return invertIndex.get(key);
    }

    @PostConstruct
    private void createInvertIndex() {
        log.info("load stock from db");
        long st = System.currentTimeMillis();

        //1.stock data
        List<Map<String, Object>> res = DbUtil.queryAllSotckInfo();
        if (CollectionUtils.isEmpty(res)) {
            log.error("no stock find in db");
            return;
        }
        //2.invert index
        for (Map<String, Object> r : res) {
            String code = r.get("code").toString();
            String name = r.get("name").toString();
            String abbrname = r.get("abbrname").toString();
            StockInfo stock = new StockInfo(code, name, abbrname);
            List<String> codeMetas = splitData(code);
            List<String> abbrNameMetas = splitData
                    (abbrname);
            codeMetas.addAll(abbrNameMetas);

            for (String key : codeMetas) {
                Collection<StockInfo> stockInfos = invertIndex.get(key);
                if (!CollectionUtils.isEmpty(stockInfos)
                        && stockInfos.size() > 10) {
                    continue;
                }
                invertIndex.put(key, stock);
            }
        }
        log.info("load stock finish,take :" +
                (System.currentTimeMillis() - st) + "ms");
    }

    private List<String> splitData(String code) {
        // payh -->
        // p pa pay payh
        // a ay ayh
        // y yh
        // h
        List<String> list = Lists.newArrayList();
        int outLength = code.length();
        for (int i = 0; i < outLength; i++) {
            int inLength = outLength + 1;
            for (int j = i + 1; j < inLength; j++) {
                list.add(code.substring(i, j));
            }
        }
        return list;
    }


}