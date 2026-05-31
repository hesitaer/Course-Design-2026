package com.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class KnowledgeGraphDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<GraphNode> nodes;

    private List<GraphEdge> edges;

    private Map<String, Long> categoryCounts;

    @Data
    public static class GraphNode implements Serializable {
        private String id;
        private String name;
        private String category;
        private String value;
        private Integer symbolSize;
        private String color;

        public GraphNode() {}

        public GraphNode(String id, String name, String category) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.symbolSize = calculateSize(category);
            this.color = calculateColor(category);
        }

        public GraphNode(String id, String name, String category, int count) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.symbolSize = calculateSizeByCount(category, count);
            this.color = calculateColor(category);
            this.value = String.valueOf(count);
        }

        private Integer calculateSize(String category) {
            switch (category) {
                case "relic": return 50;
                case "artist": return 40;
                case "dynasty": return 38;
                case "museum": return 36;
                case "material": return 30;
                case "type": return 30;
                case "location": return 28;
                case "culture": return 28;
                default: return 22;
            }
        }

        private Integer calculateSizeByCount(String category, int count) {
            int base;
            switch (category) {
                case "museum": base = 30; break;
                case "dynasty": base = 28; break;
                case "artist": base = 26; break;
                case "material": base = 22; break;
                case "type": base = 22; break;
                case "location": base = 20; break;
                case "culture": base = 20; break;
                case "relic": base = 35; break;
                default: base = 18; break;
            }
            return base + Math.min(count / 20, 50);
        }

        private String calculateColor(String category) {
            switch (category) {
                case "relic": return "#FF6B6B";
                case "artist": return "#4ECDC4";
                case "dynasty": return "#45B7D1";
                case "museum": return "#FFA07A";
                case "material": return "#98D8C8";
                case "type": return "#C39BD3";
                case "culture": return "#F7DC6F";
                case "location": return "#85C1E9";
                default: return "#BBBBBB";
            }
        }
    }

    @Data
    public static class GraphEdge implements Serializable {
        private String source;
        private String target;
        private String relationType;
        private String label;
        private Integer value;

        public GraphEdge() {}

        public GraphEdge(String source, String target, String relationType) {
            this.source = source;
            this.target = target;
            this.relationType = relationType;
            this.label = relationType;
        }

        public GraphEdge(String source, String target, String relationType, int value) {
            this.source = source;
            this.target = target;
            this.relationType = relationType;
            this.label = relationType;
            this.value = value;
        }
    }
}
