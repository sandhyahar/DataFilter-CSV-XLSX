package com.API.Common;

import java.util.function.Function;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.index.query.WrapperQueryBuilder;

import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhrasePrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery.Builder;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.ObjectBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public interface QueryBuilderUtils {

	public static Query termQuery(String field, String value) {
		QueryVariant queryVariant = new TermQuery.Builder().field(field).value(value).build();
		return new Query(queryVariant);
	}

	public static Query termQuery(String field, int value) {
		QueryVariant queryVariant = new TermQuery.Builder().field(field).value(value).build();
		return new Query(queryVariant);
	}

	public static Query matchQuery(String field, String value) {	
		   MatchPhrasePrefixQuery queryVariant = new MatchPhrasePrefixQuery.Builder()
		            .field(field)
		            .query(value)
		            .build();

		    return new Query((QueryVariant) queryVariant);
		}
		
}

			







