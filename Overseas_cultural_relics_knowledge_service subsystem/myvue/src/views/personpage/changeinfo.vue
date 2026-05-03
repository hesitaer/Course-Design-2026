<template>
  <div style="background-color: white" >
    <el-form ref="form" :model="form" label-width="80px" :rules="rules">
      <el-form-item label="用户名">
        <el-input v-model="form.name" v-bind:placeholder="userInfo.user_name"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-radio v-model="form.sex" value="0" label="0">男</el-radio>
        <el-radio v-model="form.sex" value="1" label="1">女</el-radio>
      </el-form-item>
      <el-form-item label="联系电话">
        <el-input v-model="form.tel" v-bind:placeholder="userInfo.user_tel"></el-input>
      </el-form-item>
      <el-form-item label="旧密码">
        <el-input v-model="form.oldPassword" v-bind:placeholder="userInfo.oldPassword" prop="oldPassword"></el-input>
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="form.newPassword" v-bind:placeholder="userInfo.newPassword" prop="newPassword"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">修改</el-button>
        <el-button>取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import axios from 'axios'
var storage = window.localStorage
export default {
  data () {
    return {
      form: {
        id: storage.getItem('username'),
        name: '',
        sex: '',
        tel: '',
        oldPassword: '',
        newPassword: ''
      },
      userInfo: {
        id: ''
      },
      rules: {
        oldPassword: [
          {required: true, message: '请输入旧密码', trigger: 'blur'}
        ],
        newPassword: [
          {required: true, message: '请输入新密码', trigger: 'blur'}
        ]
      }
    }
  },
  mounted () {
    this.pageInit()
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
    },
    async onSubmit () {
      axios.post('http://localhost:8085/user_admin/update', this.form// 注意数据是直接保存到json对象
      ).then((response) => {
        if (response.data.state === 200) {
          alert('修改成功!')
          this.$router.go(0)
        } else {
          alert(response.data.message)
        }
        console.log(response.data)
      }).catch(function (error) {
        console.log(error)
      })
    }
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
