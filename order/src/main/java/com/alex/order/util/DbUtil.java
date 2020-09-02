package com.alex.order.util;

import com.alex.order.bean.res.Account;
import com.alex.order.bean.res.OrderInfo;
import com.alex.order.bean.res.PosiInfo;
import com.alex.order.bean.res.TradeInfo;
import com.alex.order.cache.CacheType;
import com.alex.order.cache.RedisStringCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import thirdparty.order.OrderCmd;
import thirdparty.order.OrderStatus;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class DbUtil {

    private static DbUtil dbUtil = null;

    private DbUtil() {
    }

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    @PostConstruct
    private void init() {
        dbUtil = new DbUtil();
        dbUtil.setSqlSessionTemplate(this.sqlSessionTemplate);
    }

    ////////////////////////////// Login Auth /////////////////////////////////////
    public static Account queryAccount(long uid, String password) {
        return dbUtil.getSqlSessionTemplate().selectOne(
                "userMapper.queryAccount",
                ImmutableMap.of("UId", uid, "Password", password)
        );
    }

    public static void updateLoginTime(long uid, String nowDate,
                                       String nowTime) {
        System.out.println(nowDate + " " + nowTime);
        dbUtil.getSqlSessionTemplate().update(
                "userMapper.updateAccountLoginTime",
                ImmutableMap.of(
                        "UId", uid,
                        "ModifyDate", nowDate,
                        "ModifyTime", nowTime
                )
        );
    }

    public static int updatePwd(long uid, String oldPwd,
                                String newPwd) {
        return dbUtil.getSqlSessionTemplate().update(
                "userMapper.updatePwd",
                ImmutableMap.of("UId", uid,
                        "NewPwd", newPwd,
                        "OldPwd", oldPwd));
    }

    ////////////////////////////// Balance ////////////////////////////////////////
    public static long getBalance(long uid) {
        Long res = dbUtil.getSqlSessionTemplate()
                .selectOne("orderMapper.queryBalance",
                        ImmutableMap.of("UId", uid));
        if (res == null) {
            return -1;
        } else {
            return res;
        }
    }

    public static void addBalance(long uid, long balance) {
        dbUtil.getSqlSessionTemplate().update("orderMapper.updateBalance",
                ImmutableMap.of("UId", uid, "Balance", balance));
    }

    public static void minusBalance(long uid, long balance) {
        addBalance(uid, -balance);
    }

    ////////////////////////////// Possession ////////////////////////////////////////
    public static List<PosiInfo> getPosiList(long uid) {
        // query cache
        String suid = Long.toString(uid);
        String posiS = RedisStringCache.get(suid, CacheType.POSI);
//        if (StringUtils.isEmpty(posiS)) {
            // cache miss: query DB
            List<PosiInfo> tmp = dbUtil.getSqlSessionTemplate().selectList(
                    "orderMapper.queryPosi",
                    ImmutableMap.of("UId", uid)
            );
            List<PosiInfo> result =
                    CollectionUtils.isEmpty(tmp) ? Lists.newArrayList()
                            : tmp;
            // update cache
            RedisStringCache.cache(suid, JsonUtil.toJson(result), CacheType.POSI);
            return result;
//        } else {
//            // cache hit
//            return JsonUtil.fromJsonArr(posiS, PosiInfo.class);
//        }
    }

    public static PosiInfo getPosi(long uid, String code) {
        return dbUtil.getSqlSessionTemplate().selectOne("orderMapper.queryPosi",
                ImmutableMap.of("UId", uid, "Code", code));
    }

    public static void addPosi(long uid, String code, long volume, long price) {
        //持仓是否存在
        PosiInfo posiInfo = getPosi(uid, code);
        if (posiInfo == null) {
            //新增一条持仓
            insertPosi(uid, code, volume, price);
        } else {
            //修改持仓
            posiInfo.setCount(posiInfo.getCount() + volume);
            posiInfo.setCost(posiInfo.getCost() + price * volume);
//            if(posiInfo.getCount() == 0){
//                deletePosi(posi);
//            }else {
            updatePosi(posiInfo);
//            }

        }
    }

    public static void minusPosi(long uid, String code, long volume, long price) {
        addPosi(uid, code, -volume, price);
    }

    private static void updatePosi(PosiInfo posiInfo) {
        dbUtil.getSqlSessionTemplate().insert("orderMapper.updatePosi",
                ImmutableMap.of("UId", posiInfo.getUid(),
                        "Code", posiInfo.getCode(),
                        "Count", posiInfo.getCount(),
                        "Cost", posiInfo.getCost())
        );
    }

    private static void insertPosi(long uid, String code, long volume, long price) {
        dbUtil.getSqlSessionTemplate().insert("orderMapper.insertPosi",
                ImmutableMap.of("UId", uid,
                        "Code", code,
                        "Count", volume,
                        "Cost", volume * price)
        );
    }

    ////////////////////////////// Order ////////////////////////////////////////
    public static List<OrderInfo> getOrderList(long uid){
        // query cache
        String suid = Long.toString(uid);
        String orderS = RedisStringCache.get(suid, CacheType.ORDER);
//        if(StringUtils.isEmpty(orderS)){
            // cache miss: query DB
            List<OrderInfo> tmp = dbUtil.getSqlSessionTemplate().selectList(
                    "orderMapper.queryOrder",
                    ImmutableMap.of("UId", uid)
            );
            List<OrderInfo> result =
                    CollectionUtils.isEmpty(tmp) ? Lists.newArrayList()
                            : tmp;
            // update cache
            RedisStringCache.cache(suid,JsonUtil.toJson(result),CacheType.ORDER);
            return result;
//        }else {
//            // cache hit
//            return JsonUtil.fromJsonArr(orderS,OrderInfo.class);
//        }
    }

    ////////////////////////////// Transaction ////////////////////////////////////////
    public static List<TradeInfo> getTradeList(long uid){
        // query cache
        String suid = Long.toString(uid);
        String tradeS = RedisStringCache.get(suid, CacheType.TRADE);
//        if(StringUtils.isEmpty(tradeS)){
            // cache miss: query DB
            List<TradeInfo> tmp = dbUtil.getSqlSessionTemplate().selectList(
                    "orderMapper.queryTrade",
                    ImmutableMap.of("UId", uid)
            );
            List<TradeInfo> result =
                    CollectionUtils.isEmpty(tmp) ? Lists.newArrayList()
                            : tmp;
            // update cache
            RedisStringCache.cache(suid,JsonUtil.toJson(result),CacheType.TRADE);
            return result;
//        }else {
//            // cache hit
//            return JsonUtil.fromJsonArr(tradeS,TradeInfo.class);
//        }
    }

    ////////////////////////////// Order ///////////////////////////////////////
    public static int saveOrder(OrderCmd orderCmd){
        Map<String, Object> param = Maps.newHashMap();
        param.put("UId",orderCmd.uid);
        param.put("Code",orderCmd.code);
        param.put("Direction",orderCmd.direction.getDirection());
        param.put("Type",orderCmd.orderType.getType());
        param.put("Price",orderCmd.price);
        param.put("OCount",orderCmd.volume);
        param.put("TCount",0);
        param.put("Status", OrderStatus.NOT_SET.getCode());

        param.put("Data", TimeFormatUtil.yyyyMMdd(orderCmd.timestamp));
        param.put("Time", TimeFormatUtil.hhMMss(orderCmd.timestamp));

        int count = dbUtil.getSqlSessionTemplate().insert(
                "orderMapper.saveOrder",param
        );

        if(count > 0){
            return Integer.parseInt(param.get("ID").toString());
        }else {
            return -1;
        }
    }



    ////////////////////////////// query stock info ///////////////////////////////////////
    public static List<Map<String,Object>> queryAllSotckInfo(){
        return dbUtil.getSqlSessionTemplate()
                .selectList("stockMapper.queryStock");
    }


}