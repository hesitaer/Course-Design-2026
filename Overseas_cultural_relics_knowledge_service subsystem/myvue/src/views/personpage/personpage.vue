<template>
  <div style="background-color: white">
    <MainHeader></MainHeader>
    <el-container style="height: 800px; border: 1px solid #eee;">
      <el-aside width="200px" style="margin-top: 1%" >
        <div style="width: 190px;background-color: white;padding: 5px;margin: auto">
          <img :src="user.userpic" width="150px" height="150px" style="border-radius: 300px;margin-left: 8%">
          <br>
          <br>
          <p style="margin:0 auto;text-align: center">{{user.username}}</p>
        </div>

        <br>
        <br>
        <el-menu :default-openeds="['1','2']">
          <el-submenu index="1">
            <template slot="title"><i class="el-icon-message"></i>用户中心</template>
            <el-menu-item-group>

              <router-link class="alink" to="/personpage/info"><el-menu-item index="1-1">个人信息</el-menu-item></router-link>
<!--              <router-link class="alink" to="/personpage/changeimg"><el-menu-item index="1-2">修改头像</el-menu-item></router-link>-->
              <router-link class="alink" to="/personpage/changeinfo"><el-menu-item index="1-3">修改信息</el-menu-item></router-link>
            </el-menu-item-group>

          </el-submenu>
          <router-link class="alink" to="/personpage/favourite">
            <el-menu-item index="2">
              <i class="el-icon-star-on"></i>
              <span slot="title" >
                  我的收藏
              </span>
            </el-menu-item>
          </router-link>
          <router-link class="alink" to="/personpage/myComment">
            <el-menu-item index="3">
              <i class="el-icon-document"></i>
              <span slot="title">我的评论</span>
            </el-menu-item>
          </router-link>
        </el-menu>
      </el-aside>

      <el-container style="background-color: white">
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
    <MainFooter></MainFooter>
  </div>
</template>

<script>
  import MainHeader from '../../components/MainHeader/MainHeader'
  import MainFooter from '../../components/MainFooter/MainFooter'
  var storage = window.localStorage
  export default {
    name: 'personpage',
    created () {
      this.pageInit()
    },
    data () {
      return {
        user: {
          userpic: '',
          username: ''
        }
      }
    },
    components: {
      MainFooter,
      MainHeader
    },
    methods: {
      pageInit () {
        // 从 localStorage 读取用户信息（与登录页面存储的键名一致）
        const isLogin = storage.getItem('islogin')
        const username = storage.getItem('user_username')
        const avatar = storage.getItem('user_avatar')
        
        if (isLogin === '1' && username) {
          this.user.username = username
          // 处理头像URL：如果是相对路径则拼接完整地址
          if (avatar) {
            this.user.userpic = avatar.startsWith('http') 
              ? avatar 
              : 'http://localhost:8085' + avatar
          } else {
            this.user.userpic = '/src/assets/timg.jpeg'
          }
        } else {
          // 如果没有登录信息，显示默认值
          this.user.username = '游客'
          this.user.userpic = '/src/assets/timg.jpeg'
        }
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
