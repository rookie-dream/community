package com.rookie.community.service;

import com.rookie.community.pojo.DiscussPost;
import org.springframework.data.domain.Page;

public interface ElasticsearchService {

    void saveDiscussPost(DiscussPost post);

    void deleteDiscussPost(int id);

    Page<DiscussPost> searchDiscussPost(String keyword,int current,int limit);

    int searchDiscussPostRows(String keyword);

}
