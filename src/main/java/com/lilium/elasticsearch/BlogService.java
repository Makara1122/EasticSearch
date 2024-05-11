package com.lilium.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BlogService {
    private static final Logger logger = LoggerFactory.getLogger(BlogService.class);

    private final BlogRepository blogRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public void saveBlog(Blog blog) {
        // Assume blog is populated here
        logger.debug("Blog object before indexing: {}", blog);

        // Index the blog object
        blogRepository.save(blog);
    }

    public Iterable<Blog> findAll() {
        return blogRepository.findAll();
    }

    public void deleteById(String id) {
        blogRepository.deleteById(id);
    }

    public Blog findById(String id) {
        return blogRepository.findById(id).orElseThrow();
    }

    public List<Blog> searchByContent(String content) {
        // Create a match query that searches the "content" field for the provided content
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("content", content);

        // Create a search request for the "blog" index
        SearchRequest searchRequest = new SearchRequest("blog");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(matchQueryBuilder);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = null;
        try {
            // Execute the search request
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the response body
        if (searchResponse != null) {
            System.out.println(searchResponse.toString());
        }

        // Parse the response
        List<Blog> blogs = new ArrayList<>();
        if (searchResponse != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode responseJson = mapper.readTree(searchResponse.toString());
                JsonNode hits = responseJson.get("hits").get("hits");
                for (JsonNode hit : hits) {
                    JsonNode source = hit.get("_source");
                    Blog blog = new Blog();
                    blog.setId(hit.get("_id").asText());
                    blog.setTitle(source.get("title").asText());
                    blog.setContent(source.get("content").asText());
                    blogs.add(blog);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return blogs;
    }

}
