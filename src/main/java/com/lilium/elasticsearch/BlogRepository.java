package com.lilium.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


public interface BlogRepository extends ElasticsearchRepository<Blog, String> {
}
