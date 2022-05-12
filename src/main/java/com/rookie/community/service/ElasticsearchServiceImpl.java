package com.rookie.community.service;

import com.rookie.community.mapper.elasticsearch.DiscussPostRepository;
import com.rookie.community.pojo.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void saveDiscussPost(DiscussPost post) {
        discussPostRepository.save(post);
    }

    @Override
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    @Override
    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title","content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current,limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);

        if (searchHits.getTotalHits() <= 0) {
            return null;
        }

        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            DiscussPost post = new DiscussPost();
            post.setId(hit.getContent().getId());
            post.setUserId(hit.getContent().getUserId());
            post.setTitle(hit.getContent().getTitle());
            post.setContent(hit.getContent().getContent());
            post.setStatus(hit.getContent().getStatus());
            post.setScore(hit.getContent().getScore());
            post.setCreateTime(hit.getContent().getCreateTime());
            post.setCommentCount(hit.getContent().getCommentCount());
            // 处理高亮显示的结果
            List<String> title = hit.getHighlightFields().get("title");
            if (title != null) {
                post.setTitle(title.get(0));
            }
            List<String> content = hit.getHighlightFields().get("content");
            if (content != null) {
                post.setContent(content.get(0));

            }
            list.add(post);
        }

        return new PageImpl<>(list);
    }

    @Override
    public int searchDiscussPostRows(String keyword) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title","content"))
                .build();
        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);

       return (int)searchHits.getTotalHits();
    }
}
