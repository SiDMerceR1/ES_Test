package com.elastic.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.elastic.elasticsearch.document.Person;

public interface PersonRepository extends ElasticsearchRepository<Person, String> {

}
