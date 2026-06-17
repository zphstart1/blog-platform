package com.blog.article.application;

import com.blog.article.application.command.CreateCategoryCommand;
import com.blog.article.domain.*;
import com.blog.shared.AuthContext;
import com.blog.shared.Result;
import com.blog.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 分类应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryApplicationService {

    private final CategoryRepository categoryRepository;

    public Result<List<CategoryVO>> listAll() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryVO> vos = categories.stream()
                .map(this::toCategoryVO)
                .collect(Collectors.toList());

        // 构建树形结构
        List<CategoryVO> tree = buildTree(vos);
        return Result.ok(tree);
    }

    @Transactional
    public Result<CategoryVO> create(CreateCategoryCommand cmd) {
        AuthContext.LoginUser user = AuthContext.get();

        // 名称唯一性校验
        if (categoryRepository.existsByName(cmd.getName(), null)) {
            return Result.fail(400, "分类名称已存在");
        }

        Category category = Category.create(
                cmd.getName(),
                Slug.of(cmd.getSlug()),
                cmd.getDescription(),
                cmd.getParentId() != null ? CategoryId.of(cmd.getParentId()) : null,
                cmd.getSortOrder() != null ? cmd.getSortOrder() : 0
        );
        category = categoryRepository.save(category);

        log.info("创建分类: id={}, name={}", category.getId(), category.getName());
        CategoryVO vo = toCategoryVO(category);
        vo.setArticleCount(0);
        return Result.ok(vo);
    }

    @Transactional
    public Result<CategoryVO> update(Long id, CreateCategoryCommand cmd) {
        Optional<Category> opt = categoryRepository.findById(CategoryId.of(id));
        if (opt.isEmpty()) {
            return Result.fail(404, "分类不存在");
        }

        Category category = opt.get();

        // 名称唯一性校验
        if (!category.getName().equals(cmd.getName()) && categoryRepository.existsByName(cmd.getName(), CategoryId.of(id))) {
            return Result.fail(400, "分类名称已存在");
        }

        category.rename(cmd.getName());
        category.changeSlug(Slug.of(cmd.getSlug()));
        category.changeDescription(cmd.getDescription());
        category.changeSortOrder(cmd.getSortOrder() != null ? cmd.getSortOrder() : 0);
        categoryRepository.update(category);

        log.info("更新分类: id={}, name={}", id, category.getName());
        return Result.ok(toCategoryVO(category));
    }

    @Transactional
    public Result<Void> delete(Long id) {
        CategoryId categoryId = CategoryId.of(id);
        if (categoryRepository.findById(categoryId).isEmpty()) {
            return Result.fail(404, "分类不存在");
        }

        // 检查是否有关联文章
        if (categoryRepository.hasArticles(categoryId)) {
            return Result.fail(400, "该分类下有文章，无法删除");
        }

        categoryRepository.delete(categoryId);

        log.info("删除分类: id={}", id);
        return Result.ok();
    }

    private CategoryVO toCategoryVO(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setSlug(category.getSlug().value());
        vo.setDescription(category.getDescription());
        vo.setArticleCount(categoryRepository.countArticles(CategoryId.of(category.getId())));
        vo.setChildren(new ArrayList<>());
        return vo;
    }

    private List<CategoryVO> buildTree(List<CategoryVO> flatList) {
        Map<Long, CategoryVO> map = flatList.stream()
                .collect(Collectors.toMap(CategoryVO::getId, vo -> vo, (a, b) -> a, LinkedHashMap::new));

        // The Category domain doesn't have parentId exposed cleanly, so we build flat for now
        // Future: add parentId field to CategoryVO if tree is needed
        return new ArrayList<>(map.values());
    }
}
