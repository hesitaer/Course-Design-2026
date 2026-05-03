<template>
          <el-descriptions title="个人信息" direction="vertical" :column="4" border>
            <el-descriptions-item label="用户名">{{userInfo.user_name}}</el-descriptions-item>
            <el-descriptions-item label="性别" :span="2">{{userInfo.user_sex}}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userInfo.user_tel }}</el-descriptions-item>
          </el-descriptions>
</template>

<script>
import axios from 'axios'
var storage = window.localStorage
export default {
  data () {
    return {
      userInfo: {
      }
    }
  },
  methods: {
    pageInit () {
      this.userInfo.id = storage.getItem('username')
      axios.post('http://localhost:8085/users/get_detail', this.userInfo
      ).then((response) => {
        console.log(response.data.data)
        this.userInfo = response.data.data
        console.log(this.userInfo)
      })
        .catch(function (error) {
          console.log(error)
        })
    }
  },
  created () {
    this.pageInit()
  }
}
</script>

<style scoped>
.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}
.alink{

  text-decoration: none;
}
</style>
