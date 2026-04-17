package com.xsj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.dto.response.CategoryGamesVO;
import com.xsj.dto.response.GameBriefVO;
import com.xsj.entity.Category;
import com.xsj.entity.Game;
import com.xsj.entity.GameCategory;
import com.xsj.mapper.CategoryMapper;
import com.xsj.mapper.GameCategoryMapper;
import com.xsj.service.CategoryService;
import com.xsj.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final GameService gameService;
    private final GameCategoryMapper gameCategoryMapper;

    @Transactional
    @Override
    public int initCategoriesFromGames() {
        log.info("开始从 game 表提取分类数据...");

        // 1. 获取所有游戏
        List<Game> allGames = gameService.list();
        if (allGames.isEmpty()) {
            log.warn("game 表为空，无法初始化分类");
            return 0;
        }

        // 2. 提取所有类型并去重
        Set<String> uniqueTypes = new HashSet<>();
        for (Game game : allGames) {
            if (game.getGameType() != null && !game.getGameType().trim().isEmpty()) {
                // 按逗号分割，支持 "Role-playing (RPG), Adventure" 格式
                String[] types = game.getGameType().split(",");
                for (String type : types) {
                    String trimmed = type.trim();
                    if (!trimmed.isEmpty()) {
                        uniqueTypes.add(trimmed);
                    }
                }
            }
        }

        log.info("共提取到 {} 个唯一分类: {}", uniqueTypes.size(), uniqueTypes);

        // 3. 批量插入分类
        List<Category> categoriesToInsert = new ArrayList<>();
        Map<String, Long> typeNameToId = new HashMap<>();

        for (String typeName : uniqueTypes) {
            // 检查是否已存在
            Category existing = this.getOne(new LambdaQueryWrapper<Category>().eq(Category::getName, typeName));
            if (existing == null) {
                Category cat = new Category();
                cat.setName(typeName);
                categoriesToInsert.add(cat);
            } else {
                typeNameToId.put(typeName, existing.getId());
            }
        }

        if (!categoriesToInsert.isEmpty()) {
            this.saveBatch(categoriesToInsert);
            log.info("成功插入 {} 个新分类", categoriesToInsert.size());

            // 获取新插入分类的 ID
            for (Category cat : categoriesToInsert) {
                Category inserted = this.getOne(new LambdaQueryWrapper<Category>().eq(Category::getName, cat.getName()));
                if (inserted != null) {
                    typeNameToId.put(cat.getName(), inserted.getId());
                }
            }
        }

        // 4. 建立游戏与分类的关联
        List<GameCategory> relationsToInsert = new ArrayList<>();
        int relationCount = 0;

        for (Game game : allGames) {
            if (game.getGameType() == null) continue;

            String[] types = game.getGameType().split(",");
            for (String type : types) {
                String trimmed = type.trim();
                Long categoryId = typeNameToId.get(trimmed);

                if (categoryId != null) {
                    // 检查关联是否已存在
                    Long existingCount = gameCategoryMapper.selectCount(
                            new LambdaQueryWrapper<GameCategory>()
                                    .eq(GameCategory::getGameId, game.getId())
                                    .eq(GameCategory::getCategoryId, categoryId)
                    );

                    if (existingCount == 0) {
                        GameCategory relation = new GameCategory();
                        relation.setGameId(game.getId());
                        relation.setCategoryId(categoryId);
                        relationsToInsert.add(relation);
                        relationCount++;
                    }
                }
            }
        }

        if (!relationsToInsert.isEmpty()) {
            // 分批插入，避免单次插入过多
            int batchSize = 500;
            for (int i = 0; i < relationsToInsert.size(); i += batchSize) {
                int end = Math.min(i + batchSize, relationsToInsert.size());
                List<GameCategory> batch = relationsToInsert.subList(i, end);

                for (GameCategory rel : batch) {
                    gameCategoryMapper.insert(rel);
                }
            }
            log.info("成功建立 {} 条游戏-分类关联", relationCount);
        }

        log.info("分类初始化完成！");
        return uniqueTypes.size();
    }


    @Override
    public List<CategoryGamesVO> getCategoryGames() {
        // 1. 获取所有分类（现在 list() 可以正常使用了）
        List<Category> categories = this.list();
        if (categories.isEmpty()) return Collections.emptyList();

        // 2. 获取所有分类关联关系
        List<GameCategory> relations = gameCategoryMapper.selectList(null);
        Map<Long, List<Long>> categoryToGameIds = new HashMap<>();
        for (GameCategory rel : relations) {
            categoryToGameIds.computeIfAbsent(rel.getCategoryId(), k -> new ArrayList<>()).add(rel.getGameId());
        }

        // 3. 收集所有游戏 ID 并批量查询
        Set<Long> allGameIds = relations.stream().map(GameCategory::getGameId).collect(Collectors.toSet());
        List<Game> games = allGameIds.isEmpty() ? Collections.emptyList() : gameService.listByIds(allGameIds);
        Map<Long, Game> gameMap = games.stream().collect(Collectors.toMap(Game::getId, g -> g));

        // 4. 组装数据
        List<CategoryGamesVO> result = new ArrayList<>();
        for (Category cat : categories) {
            CategoryGamesVO vo = new CategoryGamesVO();
            vo.setCategoryId(cat.getId());
            vo.setCategoryName(cat.getName());

            List<Long> gameIds = categoryToGameIds.getOrDefault(cat.getId(), Collections.emptyList());
            List<GameBriefVO> briefGames = new ArrayList<>();

            for (int i = 0; i < Math.min(gameIds.size(), 10); i++) {
                Game g = gameMap.get(gameIds.get(i));
                if (g != null) {
                    GameBriefVO brief = new GameBriefVO();
                    BeanUtils.copyProperties(g, brief);
                    briefGames.add(brief);
                }
            }
            vo.setGames(briefGames);
            result.add(vo);
        }

        return result;
    }

    @Override
    public List<GameBriefVO> getGamesByCategoryId(Long categoryId, Integer limit, Integer offset) {
        List<GameCategory> relations = gameCategoryMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<GameCategory>()
                        .eq(GameCategory::getCategoryId, categoryId)
        );

        List<Long> gameIds = relations.stream().map(GameCategory::getGameId).collect(Collectors.toList());
        if (gameIds.isEmpty()) return Collections.emptyList();

        List<Game> games = gameService.listByIds(gameIds);
        Map<Long, Game> gameMap = games.stream().collect(Collectors.toMap(Game::getId, g -> g));

        List<GameBriefVO> result = new ArrayList<>();
        for (Long gid : gameIds) {
            Game g = gameMap.get(gid);
            if (g != null) {
                GameBriefVO brief = new GameBriefVO();
                BeanUtils.copyProperties(g, brief);
                result.add(brief);
            }
        }

        int fromIndex = offset;
        int toIndex = Math.min(fromIndex + limit, result.size());

        return fromIndex < result.size() ? result.subList(fromIndex, toIndex) : Collections.emptyList();
    }
}
