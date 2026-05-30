<template>
  <div class="timeline-container">
    <MainHeader></MainHeader>
    
    <el-container>
      <el-main>
        <div class="filter-section">
          <el-card class="filter-card">
            <div class="filter-header">
              <span class="filter-title">文物时间轴筛选</span>
            </div>
            
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="filter-item">
                  <span class="filter-label">按朝代筛选：</span>
                  <el-select 
                    v-model="selectedDynasty" 
                    placeholder="请选择朝代"
                    class="filter-select"
                  >
                    <el-option label="全部朝代" value="all"></el-option>
                    <el-option 
                      v-for="dynasty in dynasties" 
                      :key="dynasty.value" 
                      :label="dynasty.label" 
                      :value="dynasty.value"
                    ></el-option>
                  </el-select>
                </div>
              </el-col>
              
              <el-col :span="16">
                <div class="filter-item">
                  <span class="filter-label">自定义年份：</span>
                  <el-input-number 
                    v-model="startYear" 
                    :min="-3000" 
                    :max="2025" 
                    placeholder="起始年份"
                    controls-position="right"
                    class="year-input"
                  ></el-input-number>
                  <span class="year-separator">至</span>
                  <el-input-number 
                    v-model="endYear" 
                    :min="-3000" 
                    :max="2025" 
                    placeholder="结束年份"
                    controls-position="right"
                    class="year-input"
                  ></el-input-number>
                  <el-button type="primary" @click="applyTimeFilter" class="apply-btn">
                    应用筛选
                  </el-button>
                  <el-button @click="resetFilter" class="reset-btn">
                    重置
                  </el-button>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </div>

        <div class="stats-section">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card class="stat-card">
                <div class="stat-icon stat-icon-blue">
                  <i class="el-icon-trophy"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ filteredTotal }}</div>
                  <div class="stat-label">筛选文物数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="stat-card">
                <div class="stat-icon stat-icon-green">
                  <i class="el-icon-date"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ filteredPeriods }}</div>
                  <div class="stat-label">历史时期</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="stat-card">
                <div class="stat-icon stat-icon-orange">
                  <i class="el-icon-office-building"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ museumsCount }}</div>
                  <div class="stat-label">馆藏机构</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <div class="timeline-section">
          <el-card class="timeline-card">
            <div class="timeline-header">
              <span class="timeline-title">海外文物历史时间轴</span>
              <span class="timeline-subtitle">点击时间节点查看对应文物</span>
            </div>

            <div class="horizontal-timeline" v-if="filteredTimelineData.length > 0">
              <div class="timeline-segments" ref="timelineTrack">
                <div
                  v-for="(node, index) in displayTimelineData"
                  :key="node.id"
                  class="timeline-segment"
                  :class="{ active: selectedNode === node.id }"
                  :style="{ width: node.segmentWidth + '%', backgroundColor: getEraColor(node.startYear) }"
                  @click="handleNodeClick(node)"
                >
                  <el-tooltip placement="top" effect="dark" :open-delay="200">
                    <template #content>
                      <div class="tooltip-content">
                        <div class="tooltip-title">{{ node.dynastyCn }}</div>
                        <div class="tooltip-year">{{ formatYear(node.startYear) }} ~ {{ formatYear(node.endYear) }}</div>
                        <div class="tooltip-count">{{ node.count }} 件文物</div>
                        <div class="tooltip-hint">点击查看文物列表</div>
                      </div>
                    </template>
                    <div class="segment-inner">
                      <div class="segment-dot" :class="{ 'dot-large': node.count > avgCount }">
                        <span class="dot-count" v-if="node.count > 0">{{ node.count }}</span>
                      </div>
                      <div class="segment-label">
                        <div class="segment-name">{{ node.dynastyCn }}</div>
                        <div class="segment-year" v-if="node.segmentWidth > 4">{{ formatYear(node.startYear) }}~{{ formatYear(node.endYear) }}</div>
                      </div>
                    </div>
                  </el-tooltip>
                </div>
              </div>
              <div class="timeline-legend">
                <span class="legend-item"><span class="legend-dot" style="background:#5B8FF9"></span>远古~先秦</span>
                <span class="legend-item"><span class="legend-dot" style="background:#5AD8A6"></span>秦汉</span>
                <span class="legend-item"><span class="legend-dot" style="background:#F6BD16"></span>魏晋南北朝</span>
                <span class="legend-item"><span class="legend-dot" style="background:#E86452"></span>隋唐五代</span>
                <span class="legend-item"><span class="legend-dot" style="background:#6DC8EC"></span>宋元</span>
                <span class="legend-item"><span class="legend-dot" style="background:#945FB9"></span>明清~近代</span>
                <span class="legend-item"><span class="legend-dot" style="background:#C0C4CC"></span>未知朝代</span>
              </div>
            </div>

            <div class="no-data" v-else>
              <i class="el-icon-warning-outline"></i>
              <span>没有符合条件的文物数据</span>
            </div>

            <div class="timeline-detail-list" v-if="filteredTimelineData.length > 0">
              <div 
                v-for="(node, index) in filteredTimelineData" 
                :key="'detail-' + node.id"
                class="detail-item"
                :class="{ active: selectedNode === node.id }"
                @click="handleNodeClick(node)"
              >
                <div class="detail-line-wrapper">
                  <div class="detail-dot" :class="{ 'dot-highlight': node.count > avgCount }"></div>
                  <div class="detail-connector" v-if="index < filteredTimelineData.length - 1"></div>
                </div>
                <div class="detail-card">
                  <div class="detail-card-header">
                    <span class="detail-dynasty">{{ node.dynastyCn }}</span>
                    <span class="detail-year">{{ formatYear(node.startYear) }} ~ {{ formatYear(node.endYear) }}</span>
                  </div>
                  <div class="detail-card-body">
                    <div class="detail-count">
                      <i class="el-icon-trophy"></i>
                      <span>{{ node.count }} 件文物</span>
                    </div>
                    <div class="detail-thumbnail" v-if="node.thumbnail">
                      <img :src="node.thumbnail" :alt="node.dynastyCn">
                    </div>
                  </div>
                  <div class="detail-card-action">
                    <span>查看文物列表 <i class="el-icon-arrow-right"></i></span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <el-dialog 
          :title="'文物列表 - ' + currentPeriod" 
          :visible.sync="showAntiqueList"
          width="80%"
          top="5vh"
        >
          <div class="antique-list-header">
            <span class="antique-list-count">共 {{ currentAntiques.length }} 件文物</span>
          </div>
          <div class="antique-list">
            <el-row :gutter="16">
              <el-col 
                v-for="antique in currentAntiques" 
                :key="antique.objectId" 
                :span="6"
              >
                <router-link :to="{ path: '/antiqueDetail', query: { id: antique.objectId } }" class="antique-link">
                  <el-card class="antique-card" :body-style="{ padding: '0' }">
                    <div class="antique-img-wrapper">
                      <img :src="antique.imageUrl" :alt="antique.title" class="antique-img" v-if="antique.imageUrl">
                      <div class="antique-img-placeholder" v-else>
                        <i class="el-icon-picture-outline"></i>
                      </div>
                    </div>
                    <div class="antique-info">
                      <div class="antique-name" :title="antique.title">{{ antique.title }}</div>
                      <div class="antique-museum">{{ antique.museum }}</div>
                    </div>
                  </el-card>
                </router-link>
              </el-col>
            </el-row>
          </div>
        </el-dialog>
      </el-main>
    </el-container>
    
    <MainFooter></MainFooter>
  </div>
</template>

<script>
import axios from 'axios'
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'

export default {
  name: 'TimelineView',
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      dynasties: [],
      selectedDynasty: 'all',
      startYear: null,
      endYear: null,
      timelineData: [],
      selectedNode: null,
      showAntiqueList: false,
      currentPeriod: '',
      currentAntiques: [],
      totalAntiques: 0,
      filteredPeriods: 0,
      museumsCount: 0
    }
  },
  
  computed: {
    filteredTimelineData () {
      let data = this.timelineData
      if (this.selectedDynasty !== 'all') {
        data = data.filter(item => {
          if (this.selectedDynasty === '') {
            return item.dynastyEn === '' || !item.dynastyEn
          }
          return item.dynastyEn === this.selectedDynasty
        })
      }
      if (this.startYear !== null && this.startYear !== undefined) {
        data = data.filter(item => item.endYear >= this.startYear || item.startYear === 0)
      }
      if (this.endYear !== null && this.endYear !== undefined) {
        data = data.filter(item => item.startYear <= this.endYear || item.startYear === 0)
      }
      return data
    },
    filteredTotal () {
      if (this.selectedDynasty === 'all' && this.startYear === null && this.endYear === null) {
        return this.totalAntiques
      }
      return this.filteredTimelineData.reduce((sum, item) => sum + item.count, 0)
    },
    displayTimelineData () {
      const data = this.filteredTimelineData.filter(d => d.startYear !== 0 || d.endYear !== 0)
      const unknownItem = this.filteredTimelineData.find(d => d.startYear === 0 && d.endYear === 0)
      if (data.length === 0) {
        if (unknownItem) {
          unknownItem.segmentWidth = 100
          return [unknownItem]
        }
        return []
      }
      const MIN_WIDTH = 3.5
      const SQRT_SCALE = true
      let items = data.map(d => {
        const duration = Math.max(d.endYear - d.startYear, 1)
        const scaledDuration = SQRT_SCALE ? Math.sqrt(duration) : duration
        return { ...d, duration, scaledDuration }
      })
      let totalScaled = items.reduce((sum, d) => sum + d.scaledDuration, 0)
      let rawWidths = items.map(d => (d.scaledDuration / totalScaled) * 100)
      let widths = rawWidths.map(w => Math.max(w, MIN_WIDTH))
      let totalWidth = widths.reduce((a, b) => a + b, 0)
      widths = widths.map(w => (w / totalWidth) * 100)
      if (unknownItem) {
        const unknownWidth = 5
        widths = widths.map(w => (w / 100) * (100 - unknownWidth))
        items.forEach((d, i) => { d.segmentWidth = widths[i] })
        unknownItem.segmentWidth = unknownWidth
        return [...items, unknownItem]
      }
      items.forEach((d, i) => { d.segmentWidth = widths[i] })
      return items
    },
    filteredPeriods () {
      return this.filteredTimelineData.length
    },
    globalMinYear () {
      if (this.filteredTimelineData.length === 0) return -1600
      return Math.min(...this.filteredTimelineData.map(d => d.startYear))
    },
    globalMaxYear () {
      if (this.filteredTimelineData.length === 0) return 1912
      return Math.max(...this.filteredTimelineData.map(d => d.endYear))
    },
    avgCount () {
      if (this.filteredTimelineData.length === 0) return 0
      return Math.round(this.filteredTotal / this.filteredTimelineData.length)
    }
  },
  
  mounted () {
    this.loadTimelineData()
  },
  
  methods: {
    async loadTimelineData () {
      try {
        const response = await axios.get('http://localhost:8085/dataviz/timeline')
        if (response.data.state === 200) {
          const data = response.data.data
          this.timelineData = this.convertToTimelineFormat(data)
          this.totalAntiques = data.totalAntiques || 0
          this.museumsCount = data.museumCount || 0
          this.buildDynastyOptions()
        } else {
          this.$message.error(response.data.message || '获取时间轴数据失败')
          this.timelineData = []
        }
      } catch (error) {
        console.error('获取时间轴数据失败:', error)
        this.$message.error('无法连接后端服务，请确保服务已启动')
        this.timelineData = []
      }
    },

    buildDynastyOptions () {
      const seen = new Set()
      this.dynasties = []
      for (const item of this.timelineData) {
        const key = item.dynastyEn || '__unknown__'
        if (!seen.has(key)) {
          seen.add(key)
          this.dynasties.push({
            value: item.dynastyEn || '',
            label: item.dynastyCn || item.dynastyEn || '未知朝代'
          })
        }
      }
    },

    convertToTimelineFormat (data) {
      if (!data.items || !Array.isArray(data.items)) return []
      return data.items.map((item, index) => {
        const isUnknown = !item.dynastyEn || item.dynastyEn === ''
        return {
          id: index + 1,
          period: isUnknown ? '未知朝代时期' : (item.dynasty || '') + '时期',
          dynasty: item.dynasty || '',
          dynastyCn: item.dynasty || '未知朝代',
          dynastyEn: item.dynastyEn || '',
          startYear: item.startYear || 0,
          endYear: item.endYear || 0,
          count: item.count || 0,
          thumbnail: item.antiques && item.antiques.length > 0
            ? (item.antiques[0].imageUrl || '')
            : '',
          antiques: item.antiques || []
        }
      })
    },

    getEraColor (startYear) {
      if (startYear === 0) return '#C0C4CC'
      if (startYear < -206) return '#5B8FF9'
      if (startYear < 220) return '#5AD8A6'
      if (startYear < 581) return '#F6BD16'
      if (startYear < 960) return '#E86452'
      if (startYear < 1368) return '#6DC8EC'
      return '#945FB9'
    },

    formatYear (year) {
      if (year === null || year === undefined) return ''
      if (year < 0) return '公元前' + Math.abs(year) + '年'
      return year + '年'
    },

    applyTimeFilter () {
      if (this.startYear !== null && this.endYear !== null && this.startYear > this.endYear) {
        this.$message.warning('起始年份不能大于结束年份')
        return
      }
      this.selectedNode = null
    },

    resetFilter () {
      this.selectedDynasty = 'all'
      this.startYear = null
      this.endYear = null
      this.selectedNode = null
    },
    
    handleNodeClick (node) {
      this.selectedNode = node.id
      this.currentPeriod = node.period
      this.currentAntiques = node.antiques
      this.showAntiqueList = true
    }
  }
}
</script>

<style lang="scss" scoped>
.timeline-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-card {
  padding: 20px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border-radius: 8px;
}

.filter-header {
  margin-bottom: 20px;
}

.filter-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.filter-select {
  width: 200px;
}

.year-input {
  width: 140px;
}

.year-separator {
  font-size: 14px;
  color: #909399;
}

.apply-btn {
  padding: 0 24px;
}

.reset-btn {
  padding: 0 24px;
}

.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;
}

.stat-icon-blue { background: #ecf5ff; color: #409eff; }
.stat-icon-green { background: #f0f9eb; color: #67c23a; }
.stat-icon-orange { background: #fdf6ec; color: #e6a23c; }

.stat-info { color: #303133; }
.stat-value { font-size: 24px; font-weight: 600; }
.stat-label { font-size: 12px; color: #909399; margin-top: 4px; }

.timeline-section {
  margin-bottom: 20px;
}

.timeline-card {
  padding: 24px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border-radius: 8px;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.timeline-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.timeline-subtitle {
  font-size: 12px;
  color: #909399;
}

.horizontal-timeline {
  padding: 20px 0 10px;
  margin-bottom: 30px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 20px;
  overflow-x: auto;
}

.timeline-segments {
  display: flex;
  width: 100%;
  min-width: 900px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.timeline-segment {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  border-right: 1px solid rgba(255, 255, 255, 0.3);
  overflow: hidden;

  &:last-child {
    border-right: none;
  }

  &:hover {
    filter: brightness(1.1);
    z-index: 2;

    .segment-inner {
      transform: scale(1.05);
    }
  }

  &.active {
    filter: brightness(1.15);
    z-index: 3;
    box-shadow: inset 0 0 0 3px rgba(255, 255, 255, 0.8);
  }
}

.segment-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4px 2px;
  transition: transform 0.2s ease;
  width: 100%;
}

.segment-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4px;
  flex-shrink: 0;

  &.dot-large {
    width: 30px;
    height: 30px;
  }
}

.dot-count {
  font-size: 8px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
}

.segment-label {
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
}

.segment-name {
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
}

.segment-year {
  font-size: 9px;
  color: rgba(255, 255, 255, 0.85);
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.2);
  line-height: 1.2;
  margin-top: 1px;
}

.timeline-legend {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 12px;
  padding: 8px 0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #606266;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 3px;
  flex-shrink: 0;
}

.tooltip-content {
  text-align: center;
  line-height: 1.6;
}

.tooltip-title {
  font-size: 14px;
  font-weight: 600;
}

.tooltip-year {
  font-size: 12px;
  color: #c0c4cc;
}

.tooltip-count {
  font-size: 13px;
  color: #67c23a;
  font-weight: 600;
}

.tooltip-hint {
  font-size: 11px;
  color: #409eff;
  margin-top: 2px;
}

.timeline-detail-list {
  display: flex;
  flex-direction: column;
  padding-left: 20px;
  margin-top: 10px;
}

.detail-item {
  display: flex;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover .detail-card {
    border-color: #409eff;
    box-shadow: 0 4px 16px rgba(64, 158, 255, 0.15);
  }

  &.active .detail-card {
    border-color: #409eff;
    background: #ecf5ff;
  }
}

.detail-line-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 24px;
  flex-shrink: 0;
}

.detail-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #409eff;
  border: 3px solid #d9ecff;
  flex-shrink: 0;
  z-index: 1;

  &.dot-highlight {
    background: #e6a23c;
    border-color: #faecd8;
  }
}

.detail-connector {
  width: 2px;
  flex: 1;
  background: #dcdfe6;
  min-height: 20px;
}

.detail-card {
  flex: 1;
  margin: 0 0 16px 16px;
  padding: 16px 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s ease;
}

.detail-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.detail-dynasty {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.detail-year {
  font-size: 13px;
  color: #909399;
}

.detail-card-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.detail-count {
  font-size: 14px;
  color: #606266;

  i {
    color: #e6a23c;
    margin-right: 6px;
  }
}

.detail-thumbnail {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.detail-card-action {
  margin-top: 10px;
  font-size: 13px;
  color: #409eff;

  i {
    margin-left: 4px;
    transition: transform 0.2s ease;
  }

  &:hover i {
    transform: translateX(4px);
  }
}

.no-data {
  text-align: center;
  padding: 60px 0;
  color: #909399;
  font-size: 14px;

  i {
    font-size: 40px;
    display: block;
    margin-bottom: 12px;
  }
}

.antique-list-header {
  padding: 0 10px 16px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

.antique-list-count {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.antique-list {
  max-height: 550px;
  overflow-y: auto;
  padding: 0 10px;
}

.antique-link {
  text-decoration: none;
  display: block;
}

.antique-card {
  margin-bottom: 16px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 8px;
  overflow: hidden;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  }
}

.antique-img-wrapper {
  width: 100%;
  height: 150px;
  overflow: hidden;
  background: #f5f7fa;
}

.antique-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.antique-img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 36px;
}

.antique-info {
  padding: 10px 12px;
}

.antique-name {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.antique-museum {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
