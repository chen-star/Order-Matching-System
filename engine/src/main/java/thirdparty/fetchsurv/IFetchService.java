package thirdparty.fetchsurv;

import thirdparty.order.OrderCmd;

import java.util.List;

public interface IFetchService {
    
    List<OrderCmd> fetchData();
}
