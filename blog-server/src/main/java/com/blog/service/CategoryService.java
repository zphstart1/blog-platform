package com.blog.service;

import com.blog.common.ErrorCode;
import com.blog.common.Ret;
import com.blog.dto.CategoryRequest;
import com.blog.entity.Category;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.vo.CategoryVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 分类服务 — CRUD/树形结构/文章计数/缓存
 */
@Slf4j
@Service
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public CategoryService(CategoryMapper categoryMapper, ArticleMapper articleMapper,
                           RedisTemplate<String, Object> redisTemplate) {
        this.categoryMapper = categoryMapper;
        this.articleMapper = articleMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取全部分类列表（树形结构） — 缓存1h
     */
    public Ret<List<CategoryVO>> categoryList() {
        String cacheKey = "blg:category:all";
        List<CategoryVO> cached = (List<CategoryVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return Ret.ok(cached);
        }

        List<Category> allCategories = categoryMapper.selectList(null);
        List<CategoryVO> voList = buildCategoryTree(allCategories);
        redisTemplate.opsForValue().set(cacheKey, voList, 3600 + (long)(Math.random() * 600), TimeUnit.SECONDS);
        return Ret.ok(voList);
    }

    /**
     * 创建分类
     */
    public Ret<CategoryVO> createCategory(CategoryRequest request) {
        // 名称唯一性校验
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, request.getName());
        if (categoryMapper.selectCount(wrapper) > 0) {
            return Ret.fail(ErrorCode.CATEGORY_EXISTS, "分类名称已存在");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(StringUtils.hasText(request.getSlug()) ? request.getSlug() : request.getName().toLowerCase().replaceAll("\\s+", "-"));
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        categoryMapper.insert(category);

        // 删除分类缓存
        redisTemplate.delete("blg:category:all");
        log.info("创建分类: id={}, name={}", category.getId(), category.getName());

        CategoryVO vo = toCategoryVO(category);
        return Ret.ok("创建成功", vo);
    }

    /**
     * 更新分类
     */
    public Ret<CategoryVO> updateCategory(Long id, CategoryRequest request) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Ret.notFound("分类不存在");
        }

        if (StringUtils.hasText(request.getName())) {
            // 名称唯一性校验（排除自身）
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getName, request.getName());
            wrapper.ne(Category::getId, id);
            if (categoryMapper.selectCount(wrapper) > 0) {
                return Ret.fail(ErrorCode.CATEGORY_EXISTS, "分类名称已存在");
            }
            category.setName(request.getName());
        }
        if (StringUtils.hasText(request.getSlug())) {
            category.setSlug(request.getSlug());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getParentId() != null) {
            category.setParentId(request.getParentId());
        }
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }

        categoryMapper.updateById(category);
        redisTemplate.delete("blg:category:all");
        log.info("更新分类: id={}", id);

        CategoryVO vo = toCategoryVO(category);
        return Ret.ok("更新成功", vo);
    }

    /**
     * 删除分类
     */
    public Ret<Void> deleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Ret.notFound("分类不存在");
        }

        // 检查是否有文章关联
        int articleCount = categoryMapper.countArticlesByCategoryId(id);
        if (articleCount > 0) {
            return Ret.badRequest("该分类下有" + articleCount + "篇文章，无法删除");
        }

        categoryMapper.deleteById(id);
        redisTemplate.delete("blg:category:all");
        log.info("删除分类: id={}", id);
        return Ret.ok("删除成功", null);
    }

    // ========== 私有方法 ==========

    private List<CategoryVO> buildCategoryTree(List<Category> allCategories) {
        // 构建VO列表
        List<CategoryVO> voList = allCategories.stream().map(c -> {
            CategoryVO vo = toCategoryVO(c);
            vo.setArticleCount(categoryMapper.countArticlesByCategoryId(c.getId()));
            return vo;
        }).collect(Collectors.toList());

        // 构建树形结构
        Map<Long, CategoryVO> voMap = new HashMap<>();
        for (CategoryVO vo : voList) {
            voMap.put(vo.getId(), vo);
        }

        List<CategoryVO> roots = new ArrayList<>();
        for (CategoryVO vo : voList) {
            if (vo.getId() == null) continue;
            // parentId为null或0的是根节点
            Category parent = allCategories.stream()
                    .filter(c -> c.getId().equals(vo.getId()))
                    .findFirst().orElse(null);
            if (parent != null && parent.getParentId() == null) {
                vo.setChildren(new ArrayList<>());
                roots.add(vo);
            } else if (parent != null && parent.getParentId() != null) {
                CategoryVO parentVo = voMap.get(parent.getParentId());
                if (parentVo != null) {
                    if (parentVo.getChildren() == null) {
                        parentVo.setChildren(new ArrayList<>());
                    }
                    parentVo.getChildren().add(vo);
                }
            }
        }
        return roots;
    }

    private CategoryVO toCategoryVO(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setSlug(category.getSlug());
        vo.setDescription(category.getDescription());
        return vo;
    }
}