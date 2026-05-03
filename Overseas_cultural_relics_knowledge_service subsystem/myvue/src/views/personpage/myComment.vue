<template>
  <div>
    <h2>我的收藏</h2>
    <br>
    <el-table
      :data="tableData"
      style="width: 100%">
      <el-table-column
        prop="rid"
        label="文物id"
        width="180">
      </el-table-column>
      <el-table-column
        prop="cid"
        label="评论id"
        width="180">
      </el-table-column>
      <el-table-column
        prop="content"
        label="评论内容"
        width="180">
      </el-table-column>
      <el-table-column
        fixed="right"
        label="操作"
        width="100">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="danger"
            @click="shanchu(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import axios from 'axios'
import * as json from 'autoprefixer'
var storage = window.localStorage
export default {
  data () {
    return {
      tableData: [
        {
          rid: '1',
          cid: '2',
          content: 'qqq'
        },
        {
          rid: '1',
          cid: '2',
          content: 'qqq'
        }
      ],
      form: {
        username: '',
        cid: ''
      }
    }
  },
  methods: {
    pageInit () {
      this.form.username = storage.getItem('username')
      console.log(typeof (this.form.username))
      axios.post('http://localhost:8085/user_admin/comment', this.form
      ).then((response) => {
        console.log(response.data.data)
        this.tableData = response.data.data
        console.log(this.tableData)
      })
        .catch(function (error) {
          console.log(error)
        })
    },
    shanchu (row) {
      this.form.cid = row.cid.toString()
      axios.post('http://localhost:8085/user_admin/deleteComment', this.form// 注意数据是直接保存到json对象
      ).then((response) => {
        alert(response.data.message)
        console.log(response.data)
      }).catch(function (error) {
        console.log(error)
      })
      console.log(typeof (row.cid))
      this.$router.go(0)
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
