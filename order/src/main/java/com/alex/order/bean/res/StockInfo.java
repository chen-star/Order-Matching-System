package com.alex.order.bean.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class StockInfo {

    private String code;

    private String name;

    private String abbrName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockInfo stockInfo = (StockInfo) o;

        if (code != stockInfo.code) return false;
        if (!name.equals(stockInfo.name)) return false;
        return abbrName.equals(stockInfo.abbrName);
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + abbrName.hashCode();
        return result;
    }
}
