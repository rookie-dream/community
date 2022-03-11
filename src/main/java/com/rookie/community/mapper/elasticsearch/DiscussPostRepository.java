package com.rookie.community.mapper.elasticsearch;

import com.rookie.community.pojo.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository  extends ElasticsearchRepository<DiscussPost,Integer> {

}
