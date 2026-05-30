<template>
  <div class="statistics-container">
    <MainHeader></MainHeader>
    
    <el-container>
      <el-main>
        <div class="stats-overview">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card class="stat-card stat-card-blue">
                <div class="stat-icon-wrapper">
                  <i class="el-icon-trophy"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ totalAntiques }}</div>
                  <div class="stat-desc">文物总数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card stat-card-green">
                <div class="stat-icon-wrapper">
                  <i class="el-icon-collection"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ categoryCount }}</div>
                  <div class="stat-desc">文物类型</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card stat-card-orange">
                <div class="stat-icon-wrapper">
                  <i class="el-icon-office-building"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ museumCount }}</div>
                  <div class="stat-desc">馆藏机构</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card stat-card-purple">
                <div class="stat-icon-wrapper">
                  <i class="el-icon-date"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ dynastyCount }}</div>
                  <div class="stat-desc">历史朝代</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <div class="charts-grid">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-card class="chart-card">
                <template #header>
                  <div class="card-header">
                    <span class="card-title">朝代分布统计</span>
                    <el-select v-model="dynastyChartType" size="small" style="width: 110px;">
                      <el-option label="柱状图" value="bar"></el-option>
                      <el-option label="横向柱状" value="horizontalBar"></el-option>
                      <el-option label="饼图" value="pie"></el-option>
                      <el-option label="环形图" value="doughnut"></el-option>
                      <el-option label="玫瑰图" value="rose"></el-option>
                    </el-select>
                  </div>
                </template>
                <div ref="dynastyChart" class="chart-container chart-container-lg"></div>
              </el-card>
            </el-col>

            <el-col :span="12">
              <el-card class="chart-card">
                <template #header>
                  <div class="card-header">
                    <span class="card-title">博物馆藏量排行</span>
                    <el-select v-model="museumChartType" size="small" style="width: 110px;">
                      <el-option label="横向柱状" value="horizontalBar"></el-option>
                      <el-option label="柱状图" value="bar"></el-option>
                      <el-option label="饼图" value="pie"></el-option>
                      <el-option label="环形图" value="doughnut"></el-option>
                      <el-option label="玫瑰图" value="rose"></el-option>
                    </el-select>
                  </div>
                </template>
                <div ref="museumChart" class="chart-container chart-container-lg"></div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="24">
              <el-card class="chart-card">
                <template #header>
                  <div class="card-header">
                    <span class="card-title">文物类型占比</span>
                    <el-select v-model="categoryChartType" size="small" style="width: 110px;">
                      <el-option label="饼图" value="pie"></el-option>
                      <el-option label="环形图" value="doughnut"></el-option>
                      <el-option label="玫瑰图" value="rose"></el-option>
                      <el-option label="柱状图" value="bar"></el-option>
                      <el-option label="横向柱状" value="horizontalBar"></el-option>
                    </el-select>
                  </div>
                </template>
                <div ref="categoryChart" class="chart-container chart-container-wide"></div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="24">
              <el-card class="chart-card">
                <template #header>
                  <div class="card-header">
                    <span class="card-title">文物材质分布</span>
                    <el-select v-model="materialChartType" size="small" style="width: 110px;">
                      <el-option label="饼图" value="pie"></el-option>
                      <el-option label="环形图" value="doughnut"></el-option>
                      <el-option label="玫瑰图" value="rose"></el-option>
                      <el-option label="柱状图" value="bar"></el-option>
                      <el-option label="横向柱状" value="horizontalBar"></el-option>
                    </el-select>
                  </div>
                </template>
                <div ref="materialChart" class="chart-container chart-container-wide"></div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="12">
              <el-card class="chart-card">
                <template #header>
                  <div class="card-header">
                    <span class="card-title">海外藏地区域分布</span>
                    <el-select v-model="regionChartType" size="small" style="width: 110px;">
                      <el-option label="饼图" value="pie"></el-option>
                      <el-option label="环形图" value="doughnut"></el-option>
                      <el-option label="玫瑰图" value="rose"></el-option>
                      <el-option label="柱状图" value="bar"></el-option>
                      <el-option label="横向柱状" value="horizontalBar"></el-option>
                    </el-select>
                  </div>
                </template>
                <div ref="regionChart" class="chart-container chart-container-lg"></div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>
    
    <MainFooter></MainFooter>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import axios from 'axios'
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'

const COLORS = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#9b59b6', '#1abc9c', '#3498db', '#e74c3c', '#2ecc71', '#f39c12', '#8e44ad', '#16a085', '#d35400', '#2980b9', '#95a5a6']

export default {
  name: 'StatisticsView',
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      totalAntiques: 0,
      categoryCount: 0,
      museumCount: 0,
      dynastyCount: 0,
      dynastyChartType: 'bar',
      museumChartType: 'horizontalBar',
      categoryChartType: 'pie',
      materialChartType: 'pie',
      regionChartType: 'pie',
      charts: {},
      statData: null
    }
  },
  
  watch: {
    dynastyChartType () { this.renderDynastyChart() },
    museumChartType () { this.renderMuseumChart() },
    categoryChartType () { this.renderCategoryChart() },
    materialChartType () { this.renderMaterialChart() },
    regionChartType () { this.renderRegionChart() }
  },
  
  mounted () {
    this.loadStatistics()
    window.addEventListener('resize', this.handleResize)
  },
  
  beforeDestroy () {
    window.removeEventListener('resize', this.handleResize)
    Object.values(this.charts).forEach(chart => chart && chart.dispose())
  },
  
  methods: {
    async loadStatistics () {
      try {
        const response = await axios.get('http://localhost:8085/dataviz/statistics')
        if (response.data.state === 200) {
          this.statData = response.data.data
          this.totalAntiques = this.statData.totalAntiques || 0
          this.categoryCount = this.statData.categoryCount || 0
          this.museumCount = this.statData.museumCount || 0
          this.dynastyCount = this.statData.dynastyCount || 0
        } else {
          this.$message.error(response.data.message || '获取统计数据失败')
          this.statData = null
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
        this.$message.error('无法连接后端服务，请确保服务已启动')
        this.statData = null
      }
      await this.$nextTick()
      this.initAllCharts()
    },

    initAllCharts () {
      this.renderDynastyChart()
      this.renderMuseumChart()
      this.renderCategoryChart()
      this.renderMaterialChart()
      this.renderRegionChart()
    },
    
    handleResize () {
      Object.values(this.charts).forEach(chart => chart && chart.resize())
    },

    preparePieData (raw, topN) {
      let data = (raw || []).map(d => ({ value: d.value, name: d.name }))
      if (data.length === 0) data.push({ value: 1, name: '暂无数据' })
      let displayData = data.slice(0, topN)
      const otherValue = data.slice(topN).reduce((sum, d) => sum + d.value, 0)
      if (otherValue > 0) {
        displayData.push({ value: otherValue, name: '其他' })
      }
      return displayData
    },

    buildPieOption (data, chartType) {
      const base = {
        tooltip: { trigger: 'item', formatter: '{b}: {c}件 ({d}%)' },
        legend: { type: 'scroll', orient: 'vertical', right: '5%', top: 'middle', height: '80%' },
        color: COLORS
      }
      if (chartType === 'pie') {
        return {
          ...base,
          series: [{
            type: 'pie', radius: ['30%', '60%'], center: ['35%', '50%'],
            avoidLabelOverlap: true,
            itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
            label: { show: false },
            emphasis: { label: { show: true, fontSize: 13, fontWeight: 'bold' } },
            data: data
          }]
        }
      } else if (chartType === 'doughnut') {
        return {
          ...base,
          series: [{
            type: 'pie', radius: ['45%', '65%'], center: ['35%', '50%'],
            itemStyle: { borderRadius: 5, borderColor: '#fff', borderWidth: 2 },
            label: { show: false },
            emphasis: { label: { show: true, fontSize: 13, fontWeight: 'bold' } },
            data: data
          }]
        }
      } else {
        return {
          ...base,
          series: [{
            type: 'pie', radius: [20, 100], center: ['35%', '50%'], roseType: 'area',
            itemStyle: { borderRadius: 8 },
            label: { show: false },
            emphasis: { label: { show: true, fontSize: 13, fontWeight: 'bold' } },
            data: [...data].sort((a, b) => a.value - b.value)
          }]
        }
      }
    },

    buildBarOption (data, chartType, nameField, fontSize) {
      const fs = fontSize || 12
      if (chartType === 'bar') {
        return {
          tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          xAxis: {
            type: 'category', data: data.map(d => d.name),
            axisLabel: { rotate: 30, color: '#606266', fontSize: fs },
            axisLine: { lineStyle: { color: '#dcdfe6' } }
          },
          yAxis: {
            type: 'value', name: '数量',
            axisLine: { lineStyle: { color: '#dcdfe6' } },
            splitLine: { lineStyle: { color: '#ebeef5', type: 'dashed' } }
          },
          series: [{
            type: 'bar', data: data.map(d => d.value), barWidth: '60%',
            itemStyle: {
              borderRadius: [4, 4, 0, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#409eff' }, { offset: 1, color: '#79bbff' }
              ])
            },
            label: { show: true, position: 'top', color: '#606266', fontSize: fs }
          }]
        }
      } else {
        return {
          tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          xAxis: {
            type: 'value',
            axisLine: { lineStyle: { color: '#dcdfe6' } },
            splitLine: { lineStyle: { color: '#ebeef5', type: 'dashed' } }
          },
          yAxis: {
            type: 'category', data: data.map(d => d.name).reverse(),
            axisLabel: { color: '#606266', fontSize: fs },
            axisLine: { lineStyle: { color: '#dcdfe6' } }
          },
          series: [{
            type: 'bar', data: data.map(d => d.value).reverse(), barWidth: '60%',
            itemStyle: {
              borderRadius: [0, 4, 4, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#409eff' }, { offset: 1, color: '#79bbff' }
              ])
            },
            label: { show: true, position: 'right', color: '#606266', fontSize: fs }
          }]
        }
      }
    },

    isPieType (type) {
      return ['pie', 'doughnut', 'rose'].includes(type)
    },

    getChart (refName, key) {
      const dom = this.$refs[refName]
      if (!dom) return null
      if (this.charts[key]) this.charts[key].dispose()
      const chart = echarts.init(dom)
      this.charts[key] = chart
      return chart
    },
    
    renderCategoryChart () {
      const chart = this.getChart('categoryChart', 'category')
      if (!chart) return
      const raw = this.statData && this.statData.categoryDistribution ? this.statData.categoryDistribution : []
      const data = this.preparePieData(raw, 15)
      if (this.isPieType(this.categoryChartType)) {
        chart.setOption(this.buildPieOption(data, this.categoryChartType))
      } else {
        chart.setOption(this.buildBarOption(data, this.categoryChartType))
      }
    },
    
    renderDynastyChart () {
      const chart = this.getChart('dynastyChart', 'dynasty')
      if (!chart) return
      const data = (this.statData && this.statData.dynastyDistribution ? this.statData.dynastyDistribution : [])
      if (data.length === 0) data.push({ name: '暂无数据', value: 0 })
      if (this.isPieType(this.dynastyChartType)) {
        chart.setOption(this.buildPieOption(data, this.dynastyChartType))
      } else {
        chart.setOption(this.buildBarOption(data, this.dynastyChartType))
      }
    },
    
    renderMuseumChart () {
      const chart = this.getChart('museumChart', 'museum')
      if (!chart) return
      const data = (this.statData && this.statData.museumRanking ? this.statData.museumRanking : [])
      if (data.length === 0) data.push({ name: '暂无数据', value: 0 })
      if (this.isPieType(this.museumChartType)) {
        chart.setOption(this.buildPieOption(data, this.museumChartType))
      } else {
        chart.setOption(this.buildBarOption(data, this.museumChartType, 'name', 11))
      }
    },
    
    renderMaterialChart () {
      const chart = this.getChart('materialChart', 'material')
      if (!chart) return
      const raw = this.statData && this.statData.materialDistribution ? this.statData.materialDistribution : []
      const data = this.preparePieData(raw, 15)
      if (this.isPieType(this.materialChartType)) {
        chart.setOption(this.buildPieOption(data, this.materialChartType))
      } else {
        chart.setOption(this.buildBarOption(data, this.materialChartType))
      }
    },
    
    renderRegionChart () {
      const chart = this.getChart('regionChart', 'region')
      if (!chart) return
      const data = (this.statData && this.statData.regionDistribution ? this.statData.regionDistribution : [])
      if (data.length === 0) data.push({ name: '暂无数据', value: 0 })
      if (this.isPieType(this.regionChartType)) {
        chart.setOption(this.buildPieOption(data, this.regionChartType))
      } else {
        chart.setOption(this.buildBarOption(data, this.regionChartType))
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.statistics-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.stats-overview {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
}

.stat-card-blue .stat-icon-wrapper { background: linear-gradient(135deg, #409eff 0%, #337ecc 100%); }
.stat-card-green .stat-icon-wrapper { background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%); }
.stat-card-orange .stat-icon-wrapper { background: linear-gradient(135deg, #e6a23c 0%, #b88230 100%); }
.stat-card-purple .stat-icon-wrapper { background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%); }

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;

  .stat-icon-wrapper {
    width: 56px; height: 56px; border-radius: 12px;
    display: flex; align-items: center; justify-content: center;
    margin-right: 16px; flex-shrink: 0;
    i { font-size: 28px; color: #fff; }
  }

  .stat-content {
    .stat-number { font-size: 28px; font-weight: 700; color: #303133; line-height: 1.2; }
    .stat-desc { font-size: 13px; color: #909399; margin-top: 4px; }
  }
}

.charts-grid { padding-bottom: 40px; }

.chart-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;

  .card-header { display: flex; justify-content: space-between; align-items: center; }
  .card-title { font-size: 15px; font-weight: 600; color: #303133; }
  .chart-container { height: 340px; }
  .chart-container-lg { height: 380px; }
  .chart-container-wide { height: 450px; }
}
</style>
