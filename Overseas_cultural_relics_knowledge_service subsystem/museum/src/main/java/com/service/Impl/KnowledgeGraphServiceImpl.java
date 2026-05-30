package com.service.Impl;

import com.entity.KnowledgeGraphDTO;
import com.entity.Product;
import com.mapper.ProductMapper;
import com.service.IKnowledgeGraphService;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KnowledgeGraphServiceImpl implements IKnowledgeGraphService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeGraphServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private Driver neo4jDriver;

    private volatile boolean neo4jAvailable = true;
    private long lastNeo4jCheckTime = 0;
    private static final long NEO4J_RETRY_INTERVAL = 30000;

    @Override
    public KnowledgeGraphDTO getKnowledgeGraph(String keyword) {
        KnowledgeGraphDTO graphDTO = new KnowledgeGraphDTO();
        List<KnowledgeGraphDTO.GraphNode> nodes = new ArrayList<>();
        List<KnowledgeGraphDTO.GraphEdge> edges = new ArrayList<>();

        try {
            if (keyword != null && !keyword.trim().isEmpty()) {
                if (isNeo4jAvailable()) {
                    buildNeo4jSearchGraph(keyword, nodes, edges);
                } else {
                    buildSearchGraph(keyword, nodes, edges);
                }
            } else {
                if (isNeo4jAvailable()) {
                    buildNeo4jEntityGraph(nodes, edges);
                } else {
                    buildEntityGraph(nodes, edges);
                }
            }
        } catch (Exception e) {
            log.error("获取知识图谱失败", e);
            if (isNeo4jAvailable()) {
                markNeo4jUnavailable();
                nodes.clear();
                edges.clear();
                if (keyword != null && !keyword.trim().isEmpty()) {
                    buildSearchGraph(keyword, nodes, edges);
                } else {
                    buildEntityGraph(nodes, edges);
                }
            }
        }

        graphDTO.setNodes(nodes);
        graphDTO.setEdges(edges);
        return graphDTO;
    }

    @Override
    public KnowledgeGraphDTO getRelicKnowledgeGraph(String relicName) {
        return getKnowledgeGraph(relicName);
    }

    private boolean isNeo4jAvailable() {
        if (!neo4jAvailable) {
            long now = System.currentTimeMillis();
            if (now - lastNeo4jCheckTime > NEO4J_RETRY_INTERVAL) {
                try (Session session = neo4jDriver.session()) {
                    session.run("RETURN 1").consume();
                    neo4jAvailable = true;
                    log.info("Neo4j连接恢复");
                } catch (Exception e) {
                    lastNeo4jCheckTime = now;
                    log.warn("Neo4j不可用，降级到MySQL: {}", e.getMessage());
                }
            }
        }
        return neo4jAvailable;
    }

    private void markNeo4jUnavailable() {
        neo4jAvailable = false;
        lastNeo4jCheckTime = System.currentTimeMillis();
        log.warn("Neo4j查询失败，标记为不可用，降级到MySQL");
    }

    private void buildNeo4jEntityGraph(List<KnowledgeGraphDTO.GraphNode> nodes, List<KnowledgeGraphDTO.GraphEdge> edges) {
        try (Session session = neo4jDriver.session()) {
            Set<String> addedNodes = new HashSet<>();

            Result labelResult = session.run("CALL db.labels()");
            List<String> labels = new ArrayList<>();
            while (labelResult.hasNext()) {
                labels.add(labelResult.next().get(0).asString());
            }

            for (String label : labels) {
                Result nodeResult = session.run(
                    "MATCH (n:`" + label + "`) RETURN n.name AS name, n.count AS count, n.objectId AS objectId LIMIT 500"
                );
                while (nodeResult.hasNext()) {
                    Record record = nodeResult.next();
                    String name = record.get("name").isNull() ? label : record.get("name").asString();
                    String nodeId = label.toLowerCase() + "_" + name;
                    if (addedNodes.add(nodeId)) {
                        int count = record.get("count").isNull() ? 0 : record.get("count").asInt();
                        nodes.add(new KnowledgeGraphDTO.GraphNode(nodeId, name, label.toLowerCase(), count));
                    }
                }
            }

            Result relResult = session.run("CALL db.relationshipTypes()");
            List<String> relTypes = new ArrayList<>();
            while (relResult.hasNext()) {
                relTypes.add(relResult.next().get(0).asString());
            }

            for (String relType : relTypes) {
                Result edgeResult = session.run(
                    "MATCH (a)-[r:`" + relType + "`]->(b) " +
                    "WITH labels(a)[0] AS aLabel, a.name AS aName, labels(b)[0] AS bLabel, b.name AS bName, count(r) AS cnt " +
                    "RETURN aLabel, aName, bLabel, bName, cnt LIMIT 2000"
                );
                while (edgeResult.hasNext()) {
                    Record record = edgeResult.next();
                    String aLabel = record.get("aLabel").asString();
                    String aName = record.get("aName").asString();
                    String bLabel = record.get("bLabel").asString();
                    String bName = record.get("bName").asString();
                    int cnt = record.get("cnt").asInt();

                    String sourceId = aLabel.toLowerCase() + "_" + aName;
                    String targetId = bLabel.toLowerCase() + "_" + bName;

                    if (addedNodes.contains(sourceId) && addedNodes.contains(targetId)) {
                        edges.add(new KnowledgeGraphDTO.GraphEdge(sourceId, targetId, relType, cnt));
                    } else {
                        if (addedNodes.add(sourceId)) {
                            nodes.add(new KnowledgeGraphDTO.GraphNode(sourceId, aName, aLabel.toLowerCase()));
                        }
                        if (addedNodes.add(targetId)) {
                            nodes.add(new KnowledgeGraphDTO.GraphNode(targetId, bName, bLabel.toLowerCase()));
                        }
                        edges.add(new KnowledgeGraphDTO.GraphEdge(sourceId, targetId, relType, cnt));
                    }
                }
            }
        }
    }

    private void buildNeo4jSearchGraph(String keyword, List<KnowledgeGraphDTO.GraphNode> nodes, List<KnowledgeGraphDTO.GraphEdge> edges) {
        try (Session session = neo4jDriver.session()) {
            Set<String> addedNodes = new HashSet<>();

            Result searchResult = session.run(
                "MATCH (n) WHERE n.name CONTAINS $keyword OR n.title CONTAINS $keyword OR n.objectId CONTAINS $keyword " +
                "RETURN n, labels(n)[0] AS nodeLabel LIMIT 100",
                Values.parameters("keyword", keyword)
            );

            List<Record> searchRecords = searchResult.list();
            if (searchRecords.isEmpty()) {
                buildNeo4jEntityGraph(nodes, edges);
                return;
            }

            for (Record record : searchRecords) {
                Node node = record.get("n").asNode();
                String label = record.get("nodeLabel").asString();
                String name = node.get("name").isNull() ? (node.get("title").isNull() ? label : node.get("title").asString()) : node.get("name").asString();
                String nodeId = label.toLowerCase() + "_" + name;

                if (addedNodes.add(nodeId)) {
                    KnowledgeGraphDTO.GraphNode graphNode = new KnowledgeGraphDTO.GraphNode(nodeId, name, label.toLowerCase());
                    nodes.add(graphNode);
                }

                Result relResult = session.run(
                    "MATCH (m)-[r]-(other) WHERE id(m) = $nodeId RETURN type(r) AS relType, labels(other)[0] AS otherLabel, other.name AS otherName, other.title AS otherTitle",
                    Values.parameters("nodeId", node.id())
                );
                while (relResult.hasNext()) {
                    Record relRecord = relResult.next();
                    String relType = relRecord.get("relType").asString();
                    String otherLabel = relRecord.get("otherLabel").asString();
                    String otherName = relRecord.get("otherName").isNull()
                        ? (relRecord.get("otherTitle").isNull() ? otherLabel : relRecord.get("otherTitle").asString())
                        : relRecord.get("otherName").asString();
                    String otherId = otherLabel.toLowerCase() + "_" + otherName;

                    if (addedNodes.add(otherId)) {
                        nodes.add(new KnowledgeGraphDTO.GraphNode(otherId, otherName, otherLabel.toLowerCase()));
                    }
                    edges.add(new KnowledgeGraphDTO.GraphEdge(nodeId, otherId, relType));
                }
            }
        }
    }

    private void buildEntityGraph(List<KnowledgeGraphDTO.GraphNode> nodes, List<KnowledgeGraphDTO.GraphEdge> edges) {
        Set<String> addedNodes = new HashSet<>();
        Map<String, Integer> nodeCounts = new HashMap<>();

        List<Map<String, Object>> dynastyCounts = productMapper.countByDynasty();
        for (Map<String, Object> entry : dynastyCounts) {
            String name = String.valueOf(entry.get("name"));
            int count = ((Number) entry.get("value")).intValue();
            String id = "dynasty_" + name;
            if (addedNodes.add(id)) {
                nodes.add(new KnowledgeGraphDTO.GraphNode(id, name, "dynasty", count));
            }
            nodeCounts.put(id, count);
        }

        List<Map<String, Object>> museumCounts = productMapper.countByMuseum();
        for (Map<String, Object> entry : museumCounts) {
            String name = String.valueOf(entry.get("name"));
            int count = ((Number) entry.get("value")).intValue();
            String id = "museum_" + name;
            if (addedNodes.add(id)) {
                nodes.add(new KnowledgeGraphDTO.GraphNode(id, name, "museum", count));
            }
            nodeCounts.put(id, count);
        }

        List<Map<String, Object>> materialCounts = productMapper.countByMaterial();
        for (Map<String, Object> entry : materialCounts) {
            String name = String.valueOf(entry.get("name"));
            int count = ((Number) entry.get("value")).intValue();
            String id = "material_" + name;
            if (addedNodes.add(id)) {
                nodes.add(new KnowledgeGraphDTO.GraphNode(id, name, "material", count));
            }
            nodeCounts.put(id, count);
        }

        List<Map<String, Object>> typeCounts = productMapper.countByType();
        for (Map<String, Object> entry : typeCounts) {
            String name = String.valueOf(entry.get("name"));
            int count = ((Number) entry.get("value")).intValue();
            String id = "type_" + name;
            if (addedNodes.add(id)) {
                nodes.add(new KnowledgeGraphDTO.GraphNode(id, name, "type", count));
            }
            nodeCounts.put(id, count);
        }

        addCooccurrenceEdges(edges, productMapper.countDynastyMuseum(), "dynasty_", "museum_", "馆藏", addedNodes);
        addCooccurrenceEdges(edges, productMapper.countDynastyMaterial(), "dynasty_", "material_", "使用材质", addedNodes);
        addCooccurrenceEdges(edges, productMapper.countDynastyType(), "dynasty_", "type_", "文物类型", addedNodes);
        addCooccurrenceEdges(edges, productMapper.countMuseumType(), "museum_", "type_", "收藏类型", addedNodes);
        addCooccurrenceEdges(edges, productMapper.countMuseumMaterial(), "museum_", "material_", "收藏材质", addedNodes);
        addCooccurrenceEdges(edges, productMapper.countTypeMaterial(), "type_", "material_", "材质构成", addedNodes);
    }

    private void addCooccurrenceEdges(List<KnowledgeGraphDTO.GraphEdge> edges,
                                       List<Map<String, Object>> cooccurrenceData,
                                       String sourcePrefix, String targetPrefix,
                                       String relationType,
                                       Set<String> validNodes) {
        for (Map<String, Object> entry : cooccurrenceData) {
            String sourceId = sourcePrefix + String.valueOf(entry.get("source"));
            String targetId = targetPrefix + String.valueOf(entry.get("target"));
            int count = ((Number) entry.get("value")).intValue();
            if (validNodes.contains(sourceId) && validNodes.contains(targetId)) {
                edges.add(new KnowledgeGraphDTO.GraphEdge(sourceId, targetId, relationType, count));
            }
        }
    }

    private void buildSearchGraph(String keyword, List<KnowledgeGraphDTO.GraphNode> nodes, List<KnowledgeGraphDTO.GraphEdge> edges) {
        Set<String> addedNodes = new HashSet<>();

        List<Product> products = productMapper.searchProducts(keyword);

        for (Product product : products) {
            String relicId = "relic_" + product.getMuseumId() + "_" + product.getObjectId();
            if (addedNodes.add(relicId)) {
                KnowledgeGraphDTO.GraphNode relicNode = new KnowledgeGraphDTO.GraphNode(
                    relicId,
                    product.getTitle() != null ? product.getTitle() : "未知文物",
                    "relic"
                );
                relicNode.setValue(product.getDynasty() != null ? product.getDynasty() : "");
                nodes.add(relicNode);
            }

            if (product.getArtist() != null && !product.getArtist().trim().isEmpty()) {
                String artistId = "artist_" + product.getArtist();
                if (addedNodes.add(artistId)) {
                    KnowledgeGraphDTO.GraphNode artistNode = new KnowledgeGraphDTO.GraphNode(
                        artistId, product.getArtist(), "artist"
                    );
                    artistNode.setValue(product.getArtistBio() != null ? product.getArtistBio() : "");
                    nodes.add(artistNode);
                }
                edges.add(new KnowledgeGraphDTO.GraphEdge(relicId, artistId, "作者"));
            }

            if (product.getDynasty() != null && !product.getDynasty().trim().isEmpty()) {
                String dynastyId = "dynasty_" + product.getDynasty();
                if (addedNodes.add(dynastyId)) {
                    nodes.add(new KnowledgeGraphDTO.GraphNode(dynastyId, product.getDynasty(), "dynasty"));
                }
                edges.add(new KnowledgeGraphDTO.GraphEdge(relicId, dynastyId, "朝代"));
            }

            if (product.getMuseum() != null && !product.getMuseum().trim().isEmpty()) {
                String museumId = "museum_" + product.getMuseum();
                if (addedNodes.add(museumId)) {
                    nodes.add(new KnowledgeGraphDTO.GraphNode(museumId, product.getMuseum(), "museum"));
                }
                edges.add(new KnowledgeGraphDTO.GraphEdge(relicId, museumId, "收藏于"));
            }

            if (product.getMaterial() != null && !product.getMaterial().trim().isEmpty()) {
                String materialId = "material_" + product.getMaterial();
                if (addedNodes.add(materialId)) {
                    nodes.add(new KnowledgeGraphDTO.GraphNode(materialId, product.getMaterial(), "material"));
                }
                edges.add(new KnowledgeGraphDTO.GraphEdge(relicId, materialId, "材质"));
            }

            if (product.getType() != null && !product.getType().trim().isEmpty()) {
                String typeId = "type_" + product.getType();
                if (addedNodes.add(typeId)) {
                    nodes.add(new KnowledgeGraphDTO.GraphNode(typeId, product.getType(), "type"));
                }
                edges.add(new KnowledgeGraphDTO.GraphEdge(relicId, typeId, "类型"));
            }
        }

        if (products.isEmpty()) {
            buildEntityGraph(nodes, edges);
        }
    }
}
