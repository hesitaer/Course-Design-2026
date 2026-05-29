<template>
  <div>
    <h2>我的收藏</h2>
    <br>
    <el-table
      :data="tableData"
      style="width: 100%">
      <el-table-column
        label="文物id"
        prop="rid"
        width="auto">
      </el-table-column>
      <el-table-column
        label="文物名称"
        prop="relicname"
        width="auto">
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  data () {
    return {
      tableData: [
        {
          rid: '',
          relicname: ''
        }
      ],
      form: {
        id: ''
      }
    }
  },
  methods: {
    pageInit () {
      this.form.id = localStorage.getItem('username')
      axios.post('http://localhost:8085/user_admin/collect', this.form
      ).then((response) => {
        console.log(response.data.data)
        this.tableData = response.data.data
        console.log(this.tableData)
      })
        .catch(function (error) {
          console.log(error)
        })
    },
    handleSizeChange (val) {
      console.log(`每页 ${val} 条`)
    },
    /* 分页后台要写好相关代码 */
    handleCurrentChange (currentPage) {
      alert(currentPage)
    },
    handleClick (tab, event) {
      console.log(tab, event)
    },
    jumpto () {
      // this.$router.push('/')
      this.$router.push('/')
    }
  },
  created () {
    this.pageInit()
  }
}
</script>

<style scoped>
.product:hover{
  cursor: pointer;
  transform: scale(1.05);
  border-top: 1px solid red!important;
}
</style>
