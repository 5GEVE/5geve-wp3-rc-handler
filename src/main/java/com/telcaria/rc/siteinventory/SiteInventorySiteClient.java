package com.telcaria.rc.siteinventory;

import com.telcaria.rc.core.wrappers.Site;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name ="SiteInventorySiteClient", url = "http://${inventory.ip.address}:8087/sites/search/findByNameIgnoreCase")
public interface SiteInventorySiteClient {

  // E.g. "http://localhost:8080/users/search/findByName?name=test"
  @RequestMapping(method = RequestMethod.GET)
  CollectionModel<EntityModel<Site>> getSite(@RequestParam(value="name") String name);
}