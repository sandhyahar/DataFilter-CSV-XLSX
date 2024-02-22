package com.API;


import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
public class YourApplication {

    ElasticRestHighLevelClient elasticresthighlevelclient = new ElasticRestHighLevelClient();
    
    RestHighLevelClient resthighlevelclient = elasticresthighlevelclient.getRestHighLevelClient();
    
    public static void main(String args[]) {
    	
    	
    	
    	
    }
    

}
