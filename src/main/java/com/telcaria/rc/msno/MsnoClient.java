package com.telcaria.rc.msno;

import com.telcaria.rc.core.wrappers.msno.NsInstance;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name ="MsnoClient", url = "http://${msno.ip.address}:8000/nslcm/v1/ns_instances")
public interface MsnoClient {

  // E.g. "http://localhost:8000/nslcm/v1/ns_instances/{nsInstanceId}?filter=true&map=false"
  @RequestMapping(method = RequestMethod.GET, value="{nsInstanceId}")
  Response getNsInstance(@RequestHeader("Version") String version,
                         @RequestHeader("Content-Type") String contentType,
                         @RequestHeader("Accept") String accept,
                         @PathVariable(value="nsInstanceId") String nsInstanceId,
                         @RequestParam(value="filter") boolean filter,
                         @RequestParam(value="map") boolean map);
}