<template>
  <div>
    <MainHeader></MainHeader>
    <div>
      <div v-if="flag">
        <div class="main-explore">
          <div class="explore-badge">
            <el-card class="box-card">
              搜索
            </el-card>

            <el-input v-model="in_form.keyword" placeholder="请输入内容">
              <template #append>
                <el-button class="search-button" @click="res_res">搜索</el-button>
              </template>
            </el-input>

            <el-row :gutter="12">
              <el-col v-for="(item, index) in classifyList" :key="index" class="classify-layout" :span="6">
                <el-card shadow="hover" class="badge" :style="{ backgroundImage: 'url(' + item.bgUrl + ')' }">
                </el-card>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
      <div v-else>
        <div style="font-size: 14px;">
          <div style="background-color: #eeeeee;color: gray;">
            <div style="margin: 0 200px;">
              <div style="display: flex;margin-bottom: 20px;">
                <el-row>
                  <el-col v-for="(item,index) in res_form" :key="index" :span="6">
                    <div :style="'flex: 1;border: 1px solid #dfdfdf;'+(index>0?'margin-left: 20px;':'')">
                      <router-link :to="{path: '/antiqueDetail', query: {id:item.id}}">
                        <el-card :body-style="{ padding: '0px',width:'300px',height:'400px' }" class="vert">
                          <img :src="item.img_url" class="image" alt="">
                          <div style="padding: 14px;">
                            <span>{{item.object_name}}</span>
                            <div class="bottom clearfix">
                              <time class="time">{{ item.cat2 }}</time>
                              <el-button type="text" class="button">详情</el-button>
                            </div>
                          </div>
                        </el-card>
                      </router-link>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <br>
    <MainFooter></MainFooter>
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import axios from 'axios'

export default {
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      classifyList: [
        {
          key: 'dynasty',
          title: '朝代',
          bgUrl: 'https://tse1-mm.cn.bing.net/th/id/OIP-C.8cF-Es_gVFYtmMwzaHoKIwHaFt?pid=ImgDet&rs=1'
        },
        {
          key: 'material',
          title: '材质',
          bgUrl: 'https://tse1-mm.cn.bing.net/th/id/R-C.03057cce39380cf934605e8d4bfb65ef?rik=BP3kzTvHjqD6ZA&riu=http%3a%2f%2fimg01.baimao.com%2fM00%2f54%2f88%2fwKgAFFuHOgGAD6ofAAC8a81zVbQ551.jpg&ehk=%2bCiN%2brqmeEMrr2CGAsO9thQUEsDB3Y0izk7piXsNLTw%3d&risl=&pid=ImgRaw&r=0'
        },
        {
          key: 'museum',
          title: '博物馆',
          bgUrl: 'https://tse1-mm.cn.bing.net/th/id/R-C.eeefca935019f71260e6a9fcb1df0f63?rik=MoqnjhKYBCvfgg&riu=http%3a%2f%2fimg.pconline.com.cn%2fimages%2fupload%2fupc%2ftx%2fphotoblog%2f1309%2f20%2fc87%2f25937376_25937376_1136074994421_mthumb.jpg&ehk=8vdy1RIbqowuPXHaMdMI283TboT8kZ1A3vQRlVMH14E%3d&risl=&pid=ImgRaw&r=0'
        },
        {
          key: 'usage',
          title: '用途',
          bgUrl: 'https://tse4-mm.cn.bing.net/th/id/OIP-C.5YOZG6psKqpAA89M7o3oaAHaGY?pid=ImgDet&rs=1'
        }
      ],
      res_form: [
        {
          object_name: 'f7',
          cat2: '111',
          img_url: 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201410%2F10%2F20141010165320_kEUVj.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1654431956&t=79f3266521c73d40f9da38b933cdb188',
          id: 0
        },
        {
          object_name: 'eb6',
          cat2: '111',
          img_url: 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201410%2F10%2F20141010165320_kEUVj.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1654431956&t=79f3266521c73d40f9da38b933cdb188',
          id: 0
        },
        {
          object_name: 'ea5',
          cat2: '111',
          img_url: 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201410%2F10%2F20141010165320_kEUVj.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1654431956&t=79f3266521c73d40f9da38b933cdb188',
          id: 0
        },
        {
          object_name: 'd4',
          cat2: '111',
          img_url: 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201410%2F10%2F20141010165320_kEUVj.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1654431956&t=79f3266521c73d40f9da38b933cdb188',
          id: 0
        },
        {
          object_name: 'c3',
          cat2: '111',
          img_url: 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201410%2F10%2F20141010165320_kEUVj.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1654431956&t=79f3266521c73d40f9da38b933cdb188',
          id: 0
        },
        {
          object_name: 'b2',
          cat2: '111',
          img_url: 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201410%2F10%2F20141010165320_kEUVj.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1654431956&t=79f3266521c73d40f9da38b933cdb188',
          id: 0
        },
        {
          object_name: 'a1',
          cat2: '111',
          img_url: 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201410%2F10%2F20141010165320_kEUVj.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1654431956&t=79f3266521c73d40f9da38b933cdb188',
          id: 0
        }
      ],
      in_form: {
        keyword: ''
      },
      flag: false
    }
  },
  methods: {
    handle_dyn () {
      this.$router.push('/classify_dynasty')
    },
    handle_mate () {
      this.$router.push('/classify_mart')
    },
    handle_muse () {
      this.$router.push('/classify_muse')
    },
    handle_res () {
      this.$router.push('/result')
    },
    handle_usea () {
      this.$router.push('/classify_usea')
    },
    res_res () {
      axios.post('http://localhost:8085/search/obscure', this.in_form).then((response) => {
        console.log(response.data)
        if (response.data.state === 200) {
          this.res_form = response.data.data
          this.flag = false
        } else {
          alert(response.data)
        }
      }).catch(function (error) { console.log(error) })
      console.log('Received values of form: ', this.in_form)
    },
    up () {
      let arr = this.res_form.sort(function (x, y) {
        if (x.object_name < y.object_name) {
          return -1
        } else if (x.object_name > y.object_name) {
          return 1
        } else {
          return 0
        }
      })
    },
    ars () {
      let arr = this.res_form.sort(function (x, y) {
        return y.object_name.localeCompare(x.object_name)
      })
      this.res_form = arr
    }
  }
}
</script>

<style lang="scss" scoped>
.vert {
  padding: 0px;
  width: 300px;
  height: 300px;
  justify-content: space-around;
}

.image {
  width: 150px;
  height: 150px
}

.classify-layout, .hotheme-layout {
  margin: 0 0 12px 0;
}

.explore-main-pic img {
  width: 100%;
  padding: 25px;
}

.explore-badge {
  margin-top: 40px;
  margin-left: auto;
  margin-right: auto;
  width: 60%;
  height: 400px;
}

.box-card {
  width: 100%;
  text-align: center;
}

.badge {
  display: -moz-box;
  display: -webkit-box;
  -moz-box-align: center;
  -webkit-box-align: center;
  -moz-box-pack: center;
  -webkit-box-pack: center;
  height: 150px;
}

a {
  text-decoration: none;
}

.time {
  font-size: 13px;
  color: #999;
}

.bottom {
  margin-top: 10px;
  line-height: 12px;
}

.button {
  padding: 0;
  float: right;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}
</style>
