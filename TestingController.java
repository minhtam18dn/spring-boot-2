package com.learning;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learning/testing")
public class TestingController {
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String testing() {
		return "Hello";
	}
	

}
