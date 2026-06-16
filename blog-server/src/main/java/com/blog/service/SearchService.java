package com.blog.service;

import com.blog.common.Ret;
import com.blog.dto.PageResult;
import com.blog.dto.SearchRequest;
import com.blog.vo.ArticleVO;
import com.blog.vo.SearchResultVO;
import com.blog.mapper.ArticleMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 搜索服务 — MySQL FULLTEXT 全文搜索 + 搜索热词统计
 * 使用 MATCH(title, content) AGAINST(keyword IN BOOLEAN MODE)
 * ngram parser 支持中文分词（MySQL 8.0 内置）
 */
@Slf4j
@Service
public class SearchService {

    private final ArticleMapper articleMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public SearchService(ArticleMapper articleMapper, RedisTemplate<String, Object> redisTemplate) {
        this.articleMapper = articleMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 全文搜索 — 返回匹配文章列表 + 关键词高亮 + 相关度分数
     */
    public Ret<SearchResultVO> search(SearchRequest request) {
        String keyword = request.getKeyword();

        // MySQL FULLTEXT 搜索
        Page<?> page = new Page<>(request.getPage(), request.getSize());
        IPage<ArticleVO> result = articleMapper.selectSearchPage(page, keyword);

        // 关键词高亮处理：在 summary 中将关键词用 <mark> 标签包裹
        for (ArticleVO vo : result.getRecords()) {
            if (vo.getSummary() != null) {
                vo.setSummary(highlightKeyword(vo.getSummary(), keyword));
            }
        }

        SearchResultVO searchResult = new SearchResultVO();
        searchResult.setRecords(result.getRecords());
        searchResult.setTotal(result.getTotal());
        searchResult.setPage(request.getPage());
        searchResult.setSize(request.getSize());
        searchResult.setPages((int) Math.ceil((double) result.getTotal() / request.getSize()));
        searchResult.setKeyword(keyword);

        // 记录搜索热词到 Redis ZSet
        try {
            redisTemplate.opsForZSet().incrementScore("bls:search:hot", keyword, 1);
            // 设置24h过期
            redisTemplate.expire("bls:search:hot", 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("搜索热词记录失败: {}", e.getMessage());
        }

        log.info("全文搜索: keyword={}, total={}, page={}", keyword, result.getTotal(), request.getPage());
        return Ret.ok(searchResult);
    }

    /**
     * 获取搜索热词 Top N
     */
    @SuppressWarnings("unchecked")
    public Ret<java.util.Set<String>> hotKeywords(int topN) {
        try {
            // BUG-008修复: RedisTemplate<String,Object> 的 reverseRange 返回 Set<Object>，需强制转换
            java.util.Set<Object> raw = redisTemplate.opsForZSet()
                    .reverseRange("bls:search:hot", 0, topN - 1);
            java.util.Set<String> hotWords = new java.util.LinkedHashSet<>();
            if (raw != null) {
                for (Object obj : raw) {
                    hotWords.add(String.valueOf(obj));
                }
            }
            return Ret.ok(hotWords.isEmpty() ? java.util.Set.of() : hotWords);
        } catch (Exception e) {
            log.warn("获取搜索热词失败: {}", e.getMessage());
            return Ret.ok(java.util.Set.of());
        }
    }

    /**
     * 关键词高亮 — 将关键词用 <mark> 标签包裹
     * 搜索结果 summary 中匹配的关键词部分进行标记
     */
    private String highlightKeyword(String text, String keyword) {
        if (text == null || keyword == null) {
            return text;
        }
        // 对关键词中的每个词进行高亮
        String[] words = keyword.split("\\s+");
        String result = text;
        for (String word : words) {
            if (word.length() > 0) {
                result = result.replaceAll(word, "<mark>" + word + "</mark>");
            }
        }
        return result;
    }
}