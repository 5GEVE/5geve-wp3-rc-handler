package com.telcaria.rc.core.service;

import com.telcaria.rc.core.wrappers.DataShipper;
import com.telcaria.rc.core.wrappers.Site;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name ="IWFSiteInventoryDataShipperClient", url = "http://${inventory.ip.address}:8087/dataShippers")
public interface IWFSiteInventoryDataShipperClient {

  @RequestMapping(method = RequestMethod.POST)
  CollectionModel<EntityModel<DataShipper>> createDataShipper(@RequestBody DataShipper dataShipper);

  // SITE_LINK: http://localhost:8087/dataShippers/1/site
  @RequestMapping(method = RequestMethod.PUT, path = "/{siteId}/site", consumes = "text/uri-list")
  CollectionModel<EntityModel<Site>> linkSite(@PathVariable(value = "siteId") String siteId, @RequestBody String siteURL);
}

