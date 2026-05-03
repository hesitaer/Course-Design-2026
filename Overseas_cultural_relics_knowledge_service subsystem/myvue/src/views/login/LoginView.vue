<template>
  <div >
    <MainHeader></MainHeader>
  <div>
    <el-form ref="loginForm" :model="form" :rules="rules" label-width="80px" class="login-box">
      <h3 class="login-title">登录</h3>
      <el-form-item label="账号" prop="username">
        <el-input type="text" placeholder="请输入账号" v-model="form.username"/>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" placeholder="请输入密码" v-model="form.password"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" v-on:click="onSubmit()">登录</el-button>
        <el-button type="primary" v-on:click="turn_to_register()">注册</el-button>
      </el-form-item>
    </el-form>

    <el-dialog
      title="温馨提示"
      :visible.sync="dialogVisible"
      width="30%"
      :before-close="handleClose">
      <span>请输入账号和密码</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
    <MainFooter></MainFooter>
  </div>
</template>

<script>
import MainFooter from '../../components/MainFooter/MainFooter'
import MainHeader from '../../components/MainHeader/MainHeader'
import axios from 'axios'
import * as json from 'autoprefixer'
export default {
  name: 'Login',
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      form: {
        username: '',
        password: ''
      },
      info: {
        flag: true
      },
      list: {
        user_id: '',
        user_password: '',
        user_name: '',
        user_sex: '',
        user_tel: '',
        user_comment: '',
        user_login: ''
      },
      // 表单验证，需要在 el-form-item 元素中增加 prop 属性
      rules: {
        username: [
          {required: true, message: '账号不可为空', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '密码不可为空', trigger: 'blur'}
        ]
      },

      // 对话框显示和隐藏
      dialogVisible: false
    }
  },
  methods: {
    handleClose () {
      this.dialogVisible = false
    },
    onSubmit () {
      axios.post('http://localhost:8085/users/login', this.form// 注意数据是直接保存到json对象
      ).then((response) => {
        console.log(response.data)
        if (response.data.state === 200) {
          // eslint-disable-next-line no-sequences,no-unused-expressions
          if (response.data.data.user_login === 1) {
            var storage = window.localStorage
            // eslint-disable-next-line no-sequences,no-unused-expressions
            storage.setItem('username', response.data.data.user_id),
            storage.setItem('userpassword', response.data.data.user_password),
            storage.setItem('user_comment', response.data.data.user_comment),
            storage.setItem('user_login', response.data.data.user_login),
            storage.setItem('islogin', 1),
            console.log(storage.getItem('username'))
            // eslint-disable-next-line no-sequences,no-unused-expressions
            this.list === response.data,
            alert('welcome to overseas museum!')
            this.$router.push('/index')
          } else {
            // eslint-disable-next-line no-sequences,no-unused-expressions
            alert('账户登录受限'),
            this.$router.push('/index')
          }
          localStorage.setItem('User_infrom', json.stringify(this.list))
          console.log(this.list)
        } else {
          alert(response.data.message)
        }
      }).catch(function (error) {
        console.log(error)
      })
      console.log('Received values of form: ', this.form)
    },
    turn_to_register () {
      this.$router.push('register')
    }
  }
}
</script>

<style lang="scss" scoped>
  .login-box {
    border: 1px solid #DCDFE6;
    width: 350px;
    margin: 180px auto;
    padding: 35px 35px 15px 35px;
    border-radius: 5px;
    -webkit-border-radius: 5px;
    -moz-border-radius: 5px;
    box-shadow: 0 0 25px #909399;
    background-image: url("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.pptjia.com%2Fimage%2F20180321%2F388ab9a0b82be807d876ac2aa0271a2e.png&refer=http%3A%2F%2Fimg.pptjia.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1654419512&t=ec62ac8ce9c4edf0ec2e9b18c0223873");
  }

  .login-title {
    text-align: center;
    margin: 0 auto 40px auto;
    color: #303133;
  }
</style>
