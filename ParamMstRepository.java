package com.API.Repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.API.pojo.ParamMst;

public interface ParamMstRepository extends ElasticsearchRepository<ParamMst, String> {
	List<ParamMst> findByParamName(String paramName);
}
