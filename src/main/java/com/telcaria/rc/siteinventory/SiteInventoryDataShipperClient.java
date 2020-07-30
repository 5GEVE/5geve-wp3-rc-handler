package com.telcaria.rc.siteinventory;


import com.telcaria.rc.core.wrappers.DataShipper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name ="SiteInventoryDataShipperClient", url = "http://localhost:8087/dataShippers/search/findByDataShipperId")
public interface SiteInventoryDataShipperClient {

  // http://127.0.0.1:8087/dataShippers/search/findByDataShipperId?dataShipperId=france_nice.expb_metricId"
  @RequestMapping(method = RequestMethod.GET)
  CollectionModel<EntityModel<DataShipper>> getDataShipper(@RequestParam(value="dataShipperId") String id);
}

