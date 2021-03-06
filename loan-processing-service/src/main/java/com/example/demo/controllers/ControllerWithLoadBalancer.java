package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@RestController
@CrossOrigin(origins = "*")
public class ControllerWithLoadBalancer {

	
	@Autowired
	@Qualifier("template2")
	private RestTemplate template2;
	
	@Autowired
	private LoadBalancerClient client;
	
	@GetMapping(path = "/api/v2/tax/{pan}",produces = "application/json")
	
	@HystrixCommand(fallbackMethod = "getDetailsByPanFallBack",commandProperties = @HystrixProperty(
			  name = "execution.isolation.thread.timeoutInMilliseconds",value="2500"))
	
	public String getDetailsByPan(@PathVariable("pan") String pan) {
		
	  ServiceInstance selectedInstance =   this.client.choose("TAX-DETAILS-SERVICE");
		
	    String baseURL = selectedInstance.getUri().toString();
		System.out.println("************** Load Balancer instance name"+client.getClass());
	    String url = baseURL+"/api/v1/taxdetails/"+pan;

		return template2.getForObject(url, String.class);
	}
	
	public String getDetailsByPanFallBack(String pan) {
		
		return "{id:103,name:ramesh}";
	}
}
