<template>
  <div class="changeinfo-container">
    <el-form ref="form" :model="form" label-width="100px" :rules="rules">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" placeholder="请输入昵称"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-radio v-model="form.sex" :label="0">未知</el-radio>
        <el-radio v-model="form.sex" :label="1">男</el-radio>
        <el-radio v-model="form.sex" :label="2">女</el-radio>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
      </el-form-item>
      <el-form-item label="头像URL" prop="avatar_url">
        <el-input v-model="form.avatar_url" placeholder="请输入头像URL"></el-input>
      </el-form-item>
      <el-form-item label="原密码" prop="old_password">
        <el-input type="password" v-model="form.old_password" placeholder="请输入原密码"></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="password">
        <el-input type="password" v-model="form.password" placeholder="请输入新密码（不修改请留空）"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">保存修改</el-button>
        <el-button @click="resetForm">重置</el-button>
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
        username: '',
        nickname: '',
        sex: 0,
        phone: '',
        email: '',
        avatar_url: '',
        old_password: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        old_password: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      }
    }
  },
  mounted () {
    this.pageInit()
  },
  methods: {
    pageInit () {
      const userId = storage.getItem('username')
      axios.post('http://localhost:8085/users/get_detail', { id: userId })
        .then((response) => {
          const user = response.data.data
          this.form.username = user.username || ''
          this.form.nickname = user.nickname || ''
          this.form.sex = user.sex || 0
          this.form.phone = user.phone || ''
          this.form.email = user.email || ''
          this.form.avatar_url = user.avatar_url || ''
        })
        .catch(function (error) {
          console.log(error)
        })
    },
    resetForm () {
      this.pageInit()
      this.$refs.form.resetFields()
    },
    onSubmit () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          axios.post('http://localhost:8085/users/update', this.form)
            .then((response) => {
              if (response.data.state === 200) {
                this.$message({
                  type: 'success',
                  message: '修改成功！'
                })
                this.$router.push('/personpage/info')
              } else {
                this.$message({
                  type: 'error',
                  message: response.data.message
                })
              }
            }).catch(function (error) {
              console.log(error)
              this.$message({
                type: 'error',
                message: '修改失败，请稍后重试'
              })
            })
        }
      })
    }
  }
}
</script>

<style scoped>
.changeinfo-container {
  padding: 20px;
}
</style>