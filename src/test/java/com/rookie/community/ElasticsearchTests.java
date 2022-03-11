package com.rookie.community;

import com.rookie.community.mapper.DiscussPostMapper;
import com.rookie.community.mapper.elasticsearch.DiscussPostRepository;
import com.rookie.community.pojo.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;

@SpringBootTest
public class ElasticsearchTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

   @Test
    public void testInsert(){
       discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
       discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
       discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
   }
   @Test
    public void testInsertList() {
       discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0,100,0));
       discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0,100,0));
       discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0,100,0));
       discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0,100,0));
       discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0,100,0));
       discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0,100,0));
       discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0,100,0));
       discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0,100,0));
       discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0,100,0));
   }

   @Test
    public void testUpdate() {
       DiscussPost post = discussPostMapper.selectDiscussPostById(231);
       post.setContent("我是新人，使劲灌水");
       discussPostRepository.save(post);

   }
   @Test void testDelete(){
//       discussPostRepository.deleteById(231);
       discussPostRepository.deleteAll();
   }

   @Test
    public void testSearchByRepository(){
       NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
               .withQuery(QueryBuilders.multiMatchQuery("互联网", "title","content"))
               .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC))
               .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
               .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
               .withPageable(PageRequest.of(0,10))
               .withHighlightFields(
                       new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                       new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
               ).build();
       SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
       System.out.println(searchHits.getTotalHits());
       List<SearchHit<DiscussPost>> searchHits1 = searchHits.getSearchHits();
       for (SearchHit<DiscussPost> discussPostSearchHit : searchHits1) {
           System.out.println(discussPostSearchHit);
           System.out.println("===================="+discussPostSearchHit.getScore());
           System.out.println("======+++++++++====="+discussPostSearchHit.getHighlightFields());
           System.out.println("======-------------===="+discussPostSearchHit.getId());
           System.out.println("======------^^^^^^^^^-------===="+discussPostSearchHit.getContent());
           System.out.println("======------^^^^^^$$^^^-------===="+discussPostSearchHit.getHighlightFields().get("title"));
           System.out.println("======------^^^^^^$$^^^-------===="+discussPostSearchHit.getHighlightFields().get("title").get(0));
       }
   }

}
