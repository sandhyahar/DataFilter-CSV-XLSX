package com.API.Service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.API.Repository.ParamMstRepository;
import com.API.pojo.ParamMst;

@Service
public class ParamMstService {
	
	private ParamMstRepository paramMstRepository;
	
	   @Autowired
	    public ParamMstService(ParamMstRepository paramMstRepository) {
	        this.paramMstRepository = paramMstRepository;
	    }

	    public List<ParamMst> findByParamName(String paramName) {
	        return paramMstRepository.findByParamName(paramName);
	    }
	    
	    
}
