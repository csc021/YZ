package com.tf.sc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tf.sc.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * 行政区划三级联调接口
 * GET /api/regions              → 返回全部省份（含市级子节点）
 * GET /api/regions?pid=130000   → 返回河北省下所有城市
 * GET /api/regions?pid=130100   → 返回石家庄市下所有区县
 */
@RestController
@RequestMapping("/api/regions")
public class RegionController {
    private static final Logger log = LoggerFactory.getLogger(RegionController.class);

    private List<Map<String, Object>> regionTree;

    @PostConstruct
    public void loadRegions() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("regions.json");
            try (InputStream is = resource.getInputStream()) {
                JsonNode root = mapper.readTree(is);
                regionTree = new ArrayList<>();
                for (JsonNode node : root) {
                    regionTree.add(toMap(node));
                }
            }
            log.info("行政区划数据加载完成，共 {} 个省级区域", regionTree.size());
        } catch (Exception e) {
            log.error("加载行政区划数据失败", e);
            regionTree = new ArrayList<>();
        }
    }

    /**
     * 查询行政区划
     * @param pid 父级编码，为空则返回所有省份
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getRegions(@RequestParam(required = false) String pid) {
        if (pid == null || pid.isEmpty()) {
            // 返回所有省份（不含子节点以减小响应体积，前端按需加载）
            List<Map<String, Object>> provinces = regionTree.stream()
                    .map(province -> {
                        Map<String, Object> summary = new LinkedHashMap<>();
                        summary.put("code", province.get("code"));
                        summary.put("name", province.get("name"));
                        summary.put("hasChildren", true);
                        return summary;
                    })
                    .collect(Collectors.toList());
            return Result.success(provinces);
        }

        // 查找指定父级下的子区域
        List<Map<String, Object>> children = findChildrenByParent(pid);
        if (children.isEmpty()) {
            return Result.error("未找到该区域的子级数据");
        }
        return Result.success(children);
    }

    /**
     * 返回完整树结构（用于需要一次性加载所有数据的场景）
     */
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> getTree() {
        return Result.success(regionTree);
    }

    /**
     * 根据父级编码查找子节点
     */
    private List<Map<String, Object>> findChildrenByParent(String parentCode) {
        // 遍历省份
        for (Map<String, Object> province : regionTree) {
            if (parentCode.equals(province.get("code"))) {
                return toSimpleList(province);
            }
            // 遍历城市
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cities = (List<Map<String, Object>>) province.get("children");
            if (cities != null) {
                for (Map<String, Object> city : cities) {
                    if (parentCode.equals(city.get("code"))) {
                        return toSimpleList(city);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * 提取某个节点的子节点列表（简单格式，不含孙节点）
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> toSimpleList(Map<String, Object> node) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
        if (children == null) return new ArrayList<>();
        return children.stream()
                .map(child -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("code", child.get("code"));
                    m.put("name", child.get("name"));
                    m.put("hasChildren", child.containsKey("children") && child.get("children") != null
                            && !((List<?>) child.get("children")).isEmpty());
                    return m;
                })
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(JsonNode node) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", node.get("code").asText());
        map.put("name", node.get("name").asText());
        if (node.has("children") && node.get("children").isArray()) {
            List<Map<String, Object>> children = new ArrayList<>();
            for (JsonNode child : node.get("children")) {
                children.add(toMap(child));
            }
            map.put("children", children);
        }
        return map;
    }
}
