<template>
  <div class="map-page">
    <h2>文物地理分布图</h2>
    <p>展示博物馆馆藏文物的地理分布情况。</p>

    <div ref="chartRef" class="map-chart"></div>
    <el-dialog
  v-model="dialogVisible"
  title="博物馆详情"
  width="500px"
>
  <p><strong>名称：</strong>{{ selectedMuseum.name }}</p>
  <p><strong>国家：</strong>{{ selectedMuseum.country }}</p>
  <p><strong>文物总数：</strong>{{ selectedMuseum.value ? selectedMuseum.value[2] : 0 }} 件</p>

  <hr>

  <p>青铜器：{{ selectedMuseum.bronze }} 件</p>
  <p>瓷器：{{ selectedMuseum.porcelain }} 件</p>
  <p>书画：{{ selectedMuseum.painting }} 件</p>
</el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import worldJson from '@/assets/map/world.json'

export default {
  name: 'MuseumMap',
  data () {
  return {
    dialogVisible: false,
    selectedMuseum: {
      name: '',
      count: 0
    }
    }
  },
  mounted () {
    this.drawMap()
  },
  methods: {
    drawMap () {
     echarts.registerMap('world', worldJson)

      const chart = echarts.init(this.$refs.chartRef)

     const museumData = [
  {
    name: '大英博物馆',
    value: [-0.126, 51.519, 230],
    country: '英国',
    bronze: 90,
    porcelain: 80,
    painting: 60
  },
  {
    name: '卢浮宫',
    value: [2.336, 48.861, 120],
    country: '法国',
    bronze: 40,
    porcelain: 50,
    painting: 30
  },
  {
    name: '大都会艺术博物馆',
    value: [-73.963, 40.779, 180],
    country: '美国',
    bronze: 70,
    porcelain: 60,
    painting: 50
  },
  {
    name: '东京国立博物馆',
    value: [139.776, 35.718, 90],
    country: '日本',
    bronze: 30,
    porcelain: 40,
    painting: 20
  }
]

      chart.setOption({
        title: {
          text: '文物地理分布图',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter (params) {
            return `
              博物馆：${params.name}<br/>
              文物数量：${params.value ? params.value[2] : '暂无数据'}
            `
          }
        },
        geo: {
          map: 'world',
          roam: true,
          zoom: 1.2,
          itemStyle: {
            areaColor: '#eef3f8',
            borderColor: '#999'
          },
          emphasis: {
            itemStyle: {
              areaColor: '#d6e9ff'
            }
          }
        },
        series: [
          {
            label: 
            {
              show: true,
              formatter: '{b}',
              position: 'right'
            },
            name: '博物馆',
            type: 'scatter',
            coordinateSystem: 'geo',
            data: museumData,
            symbolSize (val) {
              return Math.sqrt(val[2]) * 2
            }
          }
        ]
      })
          chart.on('click', (params) => {
  if (params.componentType === 'series') {
    this.selectedMuseum = params.data
    this.dialogVisible = true
  }
})
    }
  }
}
</script>

<style scoped>
.map-page {
  padding: 30px;
}

.map-page h2 {
  margin-bottom: 8px;
}

.map-page p {
  color: #666;
  margin-bottom: 20px;
}

.map-chart {
  width: 100%;
  height: 650px;
  background: #f8fafc;
  border-radius: 12px;
}
</style>