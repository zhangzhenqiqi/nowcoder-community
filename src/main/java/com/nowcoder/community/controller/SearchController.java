package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {
        //搜索帖子
        SearchHits<DiscussPost> searchHits = elasticsearchService
                .searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchHits != null) {
            for (SearchHit<DiscussPost> hit : searchHits) {
                Map<String, Object> map = new HashMap<>();
                DiscussPost post = hit.getContent();
                //高亮化搜索结果
                List<String> titleHighlights = hit.getHighlightField("title");
                if (!CollectionUtils.isEmpty(titleHighlights)) {
                    post.setTitle(titleHighlights.get(0));
                }
                List<String> contentHighlights = hit.getHighlightField("content");
                if (!CollectionUtils.isEmpty(contentHighlights)) {
                    post.setContent(contentHighlights.get(0));
                }

                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchHits == null ? 0 : (int) searchHits.getTotalHits());
        return "/site/search";
    }
}
