<template>
  <div class="knowledge-graph-page">
    <MainHeader />

    <div class="main-content">
      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文物、作者、朝代..."
            clearable
            style="width: 280px; margin-right: 10px"
            @clear="handleSearch"
            @keyup.enter.native="handleSearch"
          >
            <i slot="prefix" class="el-icon-search"></i>
          </el-input>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetGraph">重置</el-button>
        </div>
        <div class="toolbar-right">
          <span class="stats-text">
            节点: <b>{{ nodeCount }}</b> | 关系: <b>{{ edgeCount }}</b>
          </span>
          <el-divider direction="vertical"></el-divider>
          <el-button-group>
            <el-button size="small" :type="showAllCategories ? 'primary' : ''" @click="toggleAllCategories(true)">全选</el-button>
            <el-button size="small" :type="!showAllCategories ? 'primary' : ''" @click="toggleAllCategories(false)">清除</el-button>
          </el-button-group>
        </div>
      </div>

      <!-- 类别筛选标签 -->
      <div class="category-filter">
        <el-tag
          v-for="cat in categories"
          :key="cat.key"
          :type="activeCategories.includes(cat.key) ? '' : 'info'"
          :effect="activeCategories.includes(cat.key) ? 'dark' : 'plain'"
          class="category-tag"
          @click="toggleCategory(cat.key)"
        >
          <span class="tag-dot" :style="{ background: cat.color }"></span>
          {{ cat.label }}
        </el-tag>
      </div>

      <!-- 图谱主体区域 -->
      <div class="graph-body">
        <!-- 图谱容器 -->
        <div ref="graphContainer" class="graph-container"></div>

        <!-- 右侧详情面板 -->
        <transition name="slide">
          <div v-if="selectedNode" class="detail-panel">
            <div class="panel-header">
              <span class="panel-title">节点详情</span>
              <i class="el-icon-close panel-close" @click="closeDetail"></i>
            </div>
            <div class="panel-body">
              <div class="detail-type">
                <span class="type-dot" :style="{ background: getCategoryColor(selectedNode.category) }"></span>
                <span>{{ getCategoryLabel(selectedNode.category) }}</span>
              </div>
              <div class="detail-name">{{ selectedNode.name }}</div>
              <el-divider></el-divider>
              <div class="detail-attrs" v-if="selectedNode.attrs">
                <div class="attr-item" v-for="(val, key) in selectedNode.attrs" :key="key">
                  <span class="attr-label">{{ key }}</span>
                  <span class="attr-value">{{ val }}</span>
                </div>
              </div>
              <div class="detail-relations" v-if="relatedNodes.length > 0">
                <el-divider></el-divider>
                <div class="relations-title">关联节点 ({{ relatedNodes.length }})</div>
                <div class="relations-list">
                  <div
                    v-for="rel in relatedNodes"
                    :key="rel.node.id"
                    class="relation-item"
                    @click="focusNode(rel.node)"
                  >
                    <span class="tag-dot" :style="{ background: getCategoryColor(rel.node.category) }"></span>
                    <span class="rel-name">{{ rel.node.name }}</span>
                    <span class="rel-type">{{ rel.relation }}</span>
                    <span class="rel-weight" v-if="rel.weight">({{ rel.weight }})</span>
                  </div>
                </div>
              </div>
              <div class="detail-actions">
                <el-button type="primary" size="small" @click="expandNode" :disabled="!canExpand">
                  展开关联
                </el-button>
                <el-button size="small" @click="collapseNode">
                  收起关联
                </el-button>
              </div>
            </div>
          </div>
        </transition>
      </div>

      <!-- 悬停提示 -->
      <div
        v-if="hoverNode"
        class="hover-tooltip"
        :style="{ left: tooltipPos.x + 'px', top: tooltipPos.y + 'px' }"
      >
        <div class="tooltip-header">
          <span class="tooltip-dot" :style="{ background: getCategoryColor(hoverNode.category) }"></span>
          <span class="tooltip-name">{{ hoverNode.name }}</span>
        </div>
        <div class="tooltip-category">{{ getCategoryLabel(hoverNode.category) }}</div>
        <div class="tooltip-count" v-if="hoverNode.count">文物数量: {{ hoverNode.count }}</div>
        <div class="tooltip-attrs" v-if="hoverNode.attrs">
          <div v-for="(val, key) in hoverNode.attrs" :key="key" class="tooltip-attr">
            <span>{{ key }}:</span> {{ val }}
          </div>
        </div>
        <div class="tooltip-hint">点击查看详情 / 展开关联</div>
      </div>
    </div>

    <MainFooter />
  </div>
</template>

<script>
import * as echarts from 'echarts'
import axios from 'axios'
import MainHeader from '@/components/MainHeader/MainHeader'
import MainFooter from '@/components/MainFooter/MainFooter'

export default {
  name: 'KnowledgeGraph',
  components: { MainHeader, MainFooter },
  data () {
    return {
      searchKeyword: '',
      loading: false,
      chartInstance: null,
      
      // 图谱数据
      allNodes: [],
      allEdges: [],
      displayedNodes: [],
      displayedEdges: [],
      expandedNodeIds: new Set(),
      
      // 类别
      categories: [
        { key: 'relic', label: '文物', color: '#409eff' },
        { key: 'artist', label: '作者', color: '#67c23a' },
        { key: 'dynasty', label: '朝代', color: '#e6a23c' },
        { key: 'museum', label: '博物馆', color: '#f56c6c' },
        { key: 'material', label: '材质', color: '#909399' },
        { key: 'type', label: '类型', color: '#9b59b6' }
      ],
      activeCategories: ['relic', 'artist', 'dynasty', 'museum', 'material', 'type'],
      showAllCategories: true,
      
      // 选中节点
      selectedNode: null,
      relatedNodes: [],
      canExpand: false,
      
      // 悬停
      hoverNode: null,
      tooltipPos: { x: 0, y: 0 },
      
      // 统计
      nodeCount: 0,
      edgeCount: 0
    }
  },
  
  mounted () {
    this.loadGraphData()
    window.addEventListener('resize', this.handleResize)
  },
  
  beforeDestroy () {
    window.removeEventListener('resize', this.handleResize)
    if (this.chartInstance) {
      this.chartInstance.dispose()
    }
  },
  
  methods: {
    async loadGraphData () {
      this.loading = true
      try {
        const response = await axios.post('http://localhost:8085/knowledge/graph', {
          keyword: this.searchKeyword
        })
        if (response.data.state === 200) {
          const graphData = response.data.data
          this.processGraphData(graphData.nodes, graphData.edges)
        } else {
          this.$message.error(response.data.message || '获取知识图谱数据失败')
          this.allNodes = []
          this.allEdges = []
          this.displayedNodes = []
          this.displayedEdges = []
          this.updateStats()
          this.renderGraph()
        }
      } catch (error) {
        console.error('获取知识图谱数据失败:', error)
        this.$message.error('无法连接后端服务，请确保服务已启动')
        this.allNodes = []
        this.allEdges = []
        this.displayedNodes = []
        this.displayedEdges = []
        this.updateStats()
        this.renderGraph()
      } finally {
        this.loading = false
      }
    },
    
    processGraphData (nodes, edges) {
      this.allNodes = nodes.map(n => ({
        id: n.id,
        name: n.name,
        category: n.category,
        attrs: this.buildAttrs(n),
        symbolSize: n.symbolSize || this.calcSymbolSize(n.category),
        color: n.color || this.getCategoryColor(n.category),
        count: n.value ? parseInt(n.value) : 0
      }))
      this.allEdges = edges.map(e => ({
        source: e.source,
        target: e.target,
        relation: e.relationType || e.label || '',
        weight: e.value || 1
      }))
      this.displayedNodes = [...this.allNodes]
      this.displayedEdges = [...this.allEdges]
      this.updateStats()
      this.renderGraph()
    },
    
    buildAttrs (node) {
      const attrs = {}
      if (node.category !== 'relic' && node.value) {
        attrs['文物数量'] = node.value
      } else if (node.category === 'relic' && node.value) {
        attrs['描述'] = node.value
      }
      return attrs
    },
    
    calcSymbolSize (category) {
      const sizes = { relic: 55, artist: 42, dynasty: 38, museum: 35, material: 30, culture: 30 }
      return sizes[category] || 25
    },
    
    getCategoryColor (category) {
      const colors = {
        relic: '#409eff',
        artist: '#67c23a',
        dynasty: '#e6a23c',
        museum: '#f56c6c',
        material: '#909399',
        type: '#9b59b6'
      }
      return colors[category] || '#bbbbbb'
    },
    
    getCategoryLabel (category) {
      const labels = {
        relic: '文物',
        artist: '作者',
        dynasty: '朝代',
        museum: '博物馆',
        material: '材质',
        type: '类型'
      }
      return labels[category] || category
    },
    
    updateStats () {
      this.nodeCount = this.displayedNodes.length
      this.edgeCount = this.displayedEdges.length
    },
    
    renderGraph () {
      const dom = this.$refs.graphContainer
      if (!dom) return
      
      if (this.chartInstance) this.chartInstance.dispose()
      this.chartInstance = echarts.init(dom)
      
      const filteredNodes = this.displayedNodes.filter(n => this.activeCategories.includes(n.category))
      const filteredNodeIds = new Set(filteredNodes.map(n => n.id))
      const filteredEdges = this.displayedEdges.filter(
        e => filteredNodeIds.has(e.source) && filteredNodeIds.has(e.target)
      )
      
      const categoryData = this.categories.map(c => ({
        name: c.label,
        itemStyle: { color: c.color }
      }))
      
      const echartsNodes = filteredNodes.map(n => ({
        id: n.id,
        name: n.name,
        value: n.attrs,
        category: this.categories.findIndex(c => c.key === n.category),
        symbolSize: n.symbolSize,
        itemStyle: { color: n.color, borderColor: '#fff', borderWidth: 2 },
        label: {
          show: true,
          fontSize: n.category === 'relic' ? 13 : 11,
          color: '#303133',
          position: 'bottom',
          distance: 5
        }
      }))
      
      const echartsEdges = filteredEdges.map(e => {
        const weight = e.weight || 1
        const lineWidth = Math.max(1, Math.min(weight / 30, 8))
        return {
          source: e.source,
          target: e.target,
          value: weight,
          label: {
            show: weight > 50,
            formatter: e.relation + '(' + weight + ')',
            fontSize: 9,
            color: '#909399'
          },
          lineStyle: {
            color: '#c0c4cc',
            width: lineWidth,
            curveness: 0.15,
            opacity: Math.max(0.3, Math.min(weight / 100, 0.9))
          }
        }
      })
      
      const option = {
        tooltip: { show: false },
        legend: {
          data: categoryData.map(c => c.name),
          orient: 'horizontal',
          bottom: 10,
          left: 'center',
          textStyle: { color: '#606266' }
        },
        series: [{
          type: 'graph',
          layout: 'force',
          data: echartsNodes,
          links: echartsEdges,
          categories: categoryData,
          roam: true,
          draggable: true,
          force: {
            repulsion: 500,
            gravity: 0.06,
            edgeLength: [60, 250],
            layoutAnimation: true,
            friction: 0.6
          },
          label: {
            position: 'bottom',
            formatter: '{b}'
          },
          lineStyle: {
            color: 'source',
            curveness: 0.15,
            opacity: 0.6
          },
          emphasis: {
            focus: 'adjacency',
            label: { fontSize: 15, fontWeight: 'bold' },
            lineStyle: { width: 3, opacity: 1 }
          },
          animation: true,
          animationDuration: 800,
          animationEasingUpdate: 'cubicOut'
        }]
      }
      
      this.chartInstance.setOption(option)
      
      this.chartInstance.on('click', (params) => {
        if (params.dataType === 'node') {
          this.onNodeClick(params)
        }
      })
      
      this.chartInstance.on('mouseover', (params) => {
        if (params.dataType === 'node') {
          this.onNodeHover(params)
        }
      })
      
      this.chartInstance.on('mouseout', () => {
        this.hoverNode = null
      })
    },
    
    onNodeClick (params) {
      const node = this.allNodes.find(n => n.id === params.data.id)
      if (!node) return
      
      this.selectedNode = node
      this.canExpand = !this.expandedNodeIds.has(node.id)
      
      const nodeIds = new Set()
      const related = []
      this.allEdges.forEach(e => {
        if (e.source === node.id) {
          const target = this.allNodes.find(n => n.id === e.target)
          if (target) {
            nodeIds.add(target.id)
            related.push({ node: target, relation: e.relation, weight: e.weight })
          }
        }
        if (e.target === node.id) {
          const source = this.allNodes.find(n => n.id === e.source)
          if (source) {
            nodeIds.add(source.id)
            related.push({ node: source, relation: e.relation, weight: e.weight })
          }
        }
      })
      this.relatedNodes = related
    },
    
    onNodeHover (params) {
      const node = this.allNodes.find(n => n.id === params.data.id)
      if (!node) return
      
      const event = params.event.event
      this.hoverNode = node
      this.tooltipPos = {
        x: event.offsetX + 15,
        y: event.offsetY + 15
      }
    },
    
    expandNode () {
      if (!this.selectedNode) return
      const nodeId = this.selectedNode.id
      this.expandedNodeIds.add(nodeId)
      this.canExpand = false
      
      const connectedNodeIds = new Set()
      this.allEdges.forEach(e => {
        if (e.source === nodeId) connectedNodeIds.add(e.target)
        if (e.target === nodeId) connectedNodeIds.add(e.source)
      })
      
      const existingIds = new Set(this.displayedNodes.map(n => n.id))
      const newNodes = this.allNodes.filter(n => connectedNodeIds.has(n.id) && !existingIds.has(n.id))
      
      if (newNodes.length > 0) {
        this.displayedNodes = [...this.displayedNodes, ...newNodes]
        const newNodeIds = new Set(this.displayedNodes.map(n => n.id))
        this.displayedEdges = this.allEdges.filter(e => newNodeIds.has(e.source) && newNodeIds.has(e.target))
        this.updateStats()
        this.renderGraph()
        this.$message.success(`展开了 ${newNodes.length} 个关联节点`)
      } else {
        this.$message.info('所有关联节点已展示')
      }
    },
    
    collapseNode () {
      if (!this.selectedNode) return
      const nodeId = this.selectedNode.id
      this.expandedNodeIds.delete(nodeId)
      
      this.displayedNodes = [...this.allNodes]
      this.displayedEdges = [...this.allEdges]
      this.updateStats()
      this.renderGraph()
      this.$message.success('已重置图谱')
    },
    
    focusNode (node) {
      if (!this.chartInstance) return
      this.chartInstance.dispatchAction({
        type: 'highlight',
        name: node.name
      })
      this.onNodeClick({ data: node })
    },
    
    closeDetail () {
      this.selectedNode = null
      this.relatedNodes = []
    },
    
    toggleCategory (key) {
      const idx = this.activeCategories.indexOf(key)
      if (idx > -1) {
        this.activeCategories.splice(idx, 1)
      } else {
        this.activeCategories.push(key)
      }
      this.showAllCategories = this.activeCategories.length === this.categories.length
      this.renderGraph()
    },
    
    toggleAllCategories (show) {
      if (show) {
        this.activeCategories = this.categories.map(c => c.key)
      } else {
        this.activeCategories = []
      }
      this.showAllCategories = show
      this.renderGraph()
    },
    
    handleSearch () {
      this.loadGraphData()
    },
    
    resetGraph () {
      this.searchKeyword = ''
      this.selectedNode = null
      this.relatedNodes = []
      this.expandedNodeIds.clear()
      this.activeCategories = this.categories.map(c => c.key)
      this.showAllCategories = true
      this.loadGraphData()
    },
    
    handleResize () {
      this.chartInstance && this.chartInstance.resize()
    }
  }
}
</script>

<style lang="scss" scoped>
.knowledge-graph-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.toolbar {
  padding: 12px 20px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 10;
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stats-text {
  font-size: 13px;
  color: #909399;
  
  b {
    color: #409eff;
    font-weight: 600;
  }
}

.category-filter {
  padding: 10px 20px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  gap: 8px;
}

.category-tag {
  cursor: pointer;
  user-select: none;
  transition: all 0.2s;
}

.tag-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 4px;
  vertical-align: middle;
}

.graph-body {
  flex: 1;
  position: relative;
  display: flex;
}

.graph-container {
  flex: 1;
  min-height: calc(100vh - 260px);
  background: #fff;
  margin: 16px;
  margin-right: 0;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.detail-panel {
  width: 320px;
  background: #fff;
  margin: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex-shrink: 0;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.panel-close {
  font-size: 18px;
  color: #909399;
  cursor: pointer;
  transition: color 0.2s;
  
  &:hover {
    color: #409eff;
  }
}

.panel-body {
  padding: 16px 20px;
  overflow-y: auto;
  flex: 1;
}

.detail-type {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.type-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.detail-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.detail-attrs {
  .attr-item {
    display: flex;
    padding: 6px 0;
    border-bottom: 1px solid #f5f7fa;
  }
  
  .attr-label {
    color: #909399;
    font-size: 13px;
    width: 60px;
    flex-shrink: 0;
  }
  
  .attr-value {
    color: #303133;
    font-size: 13px;
    flex: 1;
  }
}

.relations-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.relation-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
  margin-bottom: 4px;
  
  &:hover {
    background: #f5f7fa;
  }
}

.rel-name {
  flex: 1;
  font-size: 13px;
  color: #303133;
}

.rel-type {
  font-size: 11px;
  color: #909399;
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
}

.rel-weight {
  font-size: 11px;
  color: #409eff;
  font-weight: 500;
}

.detail-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

.hover-tooltip {
  position: fixed;
  z-index: 9999;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  padding: 12px 16px;
  pointer-events: none;
  max-width: 260px;
  border: 1px solid #ebeef5;
}

.tooltip-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.tooltip-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.tooltip-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.tooltip-category {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.tooltip-count {
  font-size: 12px;
  color: #409eff;
  font-weight: 600;
  margin-bottom: 4px;
}

.tooltip-attrs {
  .tooltip-attr {
    font-size: 12px;
    color: #606266;
    line-height: 1.6;
    
    span {
      color: #909399;
    }
  }
}

.tooltip-hint {
  margin-top: 8px;
  font-size: 11px;
  color: #c0c4cc;
  border-top: 1px solid #f5f7fa;
  padding-top: 6px;
}

.slide-enter-active, .slide-leave-active {
  transition: all 0.3s ease;
}

.slide-enter, .slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style>