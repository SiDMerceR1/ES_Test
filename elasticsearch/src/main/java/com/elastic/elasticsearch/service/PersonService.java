package com.elastic.elasticsearch.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elastic.elasticsearch.document.Person;
import com.elastic.elasticsearch.helper.Indices;
import com.elastic.elasticsearch.repository.PersonRepository;
import com.elastic.elasticsearch.search.SearchRequestDTO;
import com.elastic.elasticsearch.search.util.SearchUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PersonService {
	private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(PersonService.class);

    private final RestHighLevelClient client;

//    @Autowired
//    public PersonService() {
//        
//    }
	
	private final PersonRepository repository;
	
	@Autowired
	public PersonService(PersonRepository repository, RestHighLevelClient client) {
		this.repository = repository;
		this.client = client;
	}
	
	public void save(final Person person) {
		repository.save(person);
	}
	
	public Person findById(final String id) {
		return repository.findById(id).orElse(null);
	}
	
	public List<Person> search(final SearchRequestDTO dto) {
    	final SearchRequest request = SearchUtil.buildSearchRequest(Indices.PERSON_INDEX, dto);
    	
        if (request == null) {
            LOG.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<Person> persons = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                persons.add(
                        MAPPER.readValue(hit.getSourceAsString(), Person.class)
                );
            }

            return persons;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
