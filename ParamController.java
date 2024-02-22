package com.API.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.API.Service.ParamMstService;
import com.API.pojo.ParamMst;

@RestController
public class ParamController {

	@Autowired
	ParamMstService paramMstService;

	    @GetMapping("/paramData/{paramName}")
	    public ResponseEntity<List<ParamMst>> getParamMstByParamName(@PathVariable String paramName) {
	        List<ParamMst> paramMsts = paramMstService.findByParamName(paramName);
	        return ResponseEntity.ok(paramMsts);
	    }
}
