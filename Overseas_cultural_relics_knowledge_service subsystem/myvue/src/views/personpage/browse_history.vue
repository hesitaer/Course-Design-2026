<template>
  <div>
    <div class="history-header">
      <h2>浏览记录</h2>
      <el-button v-if="historyList.length > 0" type="danger" size="small" @click="clearAll">
        一键清除
      </el-button>
    </div>
    <br>
    <el-empty v-if="historyList.length === 0" description="暂无浏览记录"></el-empty>
    <el-table
      v-else
      :data="historyList"
      style="width: 100%"
      @row-click="goToDetail">
      <el-table-column
        label="文物图片"
        width="100">
        <template slot-scope="scope">
          <img :src="scope.row.img_url || '/src/assets/timg.jpeg'" class="history-img" />
        </template>
      </el-table-column>
      <el-table-column
        label="文物名称"
        prop="object_name"
        width="200">
      </el-table-column>
      <el-table-column
        label="文物时期"
        prop="time_period"
        width="150">
      </el-table-column>
      <el-table-column
        label="浏览时间"
        prop="browse_time"
        width="180">
      </el-table-column>
      <el-table-column
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click.stop="removeItem(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
var storage = window.localStorage
export default {
  name: 'browse_history',
  data () {
    return {
      historyList: []
    }
  },
  created () {
    this.loadHistory()
  },
  methods: {
    loadHistory () {
      const history = storage.getItem('browse_history')
      if (history) {
        try {
          this.historyList = JSON.parse(history)
        } catch (e) {
          this.historyList = []
        }
      }
    },
    clearAll () {
      this.$confirm('确定要清除所有浏览记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        storage.removeItem('browse_history')
        this.historyList = []
        this.$message({
          type: 'success',
          message: '已清除所有浏览记录'
        })
      }).catch(() => {})
    },
    removeItem (row) {
      this.$confirm('确定要删除这条浏览记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const history = storage.getItem('browse_history')
        if (history) {
          let list = JSON.parse(history)
          list = list.filter(item => item.object_id !== row.object_id || item.browse_time !== row.browse_time)
          storage.setItem('browse_history', JSON.stringify(list))
          this.historyList = list
          this.$message({
            type: 'success',
            message: '已删除'
          })
        }
      }).catch(() => {})
    },
    goToDetail (row) {
      this.$router.push({
        path: '/antiqueDetail',
        query: { id: row.object_id, museum_id: row.museum_id }
      })
    }
  }
}
</script>

<style scoped>
.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}
</style>
