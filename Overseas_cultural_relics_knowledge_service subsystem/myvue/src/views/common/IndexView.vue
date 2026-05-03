<template>
  <div class="main-index">
    <MainHeader></MainHeader>
    <div class="carousel">
      <el-carousel :interval="4000" type="card" height="400px">
        <el-carousel-item v-for="(item, index) in carouselImages" :key="index">
          <img :src="item" class="carousel-img" alt="">
        </el-carousel-item>
      </el-carousel>
    </div>

    <MainFooter></MainFooter>
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import carouselImg1 from '@/assets/index/1.png'
import carouselImg2 from '@/assets/index/2.png'
import carouselImg3 from '@/assets/index/3.png'
import carouselImg4 from '@/assets/index/4.png'
import carouselImg5 from '@/assets/index/5.png'

// import { getAntique, getMuseumList } from '@/api/index'
// import { getImg, getAudio, getModel } from '@/api/file'
export default {
  components: {
    MainHeader,
    MainFooter
    // ModelObj
  },
  data () {
    return {
      carouselImages: [
        carouselImg1,
        carouselImg2,
        carouselImg3,
        carouselImg4,
        carouselImg5
      ],
      height: '28.5em',
      interval: 5000,
      screenWidth: '',
      testQuery: {
        offset: 1,
        rows: 20
      },
      dialogVisible: false,
      fileInfo: null,
      fileName: '',
      fileDesc: '',
      imgSrc: '',
      audioSrc: '',
      modelSrc: '',
      modelPosition: {
        x: 0,
        y: 30,
        z: 0
      }
    }
  },
  computed: {
    insetMuseum () {
      return '$@/inset-museum/index.html'
    }
  },
  watch: {
    // 监听浏览器窗口宽度
    screenWidth (val) {
      const x = val / 2.8 / 16
      this.height = x + 'em'
    }
  },
  created () {
    // this.fetchAntique()
    // this.fetchMuseum()
  },
  mounted () {
    window.onload = () => {
      return (() => {
        window.screenWidth = window.innerWidth
        this.screenWidth = window.screenWidth
      })()
    }
    window.onresize = () => {
      return (() => {
        window.screenWidth = window.innerWidth
        this.screenWidth = window.screenWidth
      })()
    }
  },
  methods: {
    /**
     * @description 请求精选文物列表
     * @returns
     */
    fetchAntique () {
      /* getAntique().then(res => {
        // console.log(res.data[0], '精选文物列表')
        this.fileInfo = res.data[0]
      }).then(_ => {
        this.fileName = `${this.fileInfo['antiqueName']}`
        this.fileDesc = `${this.fileInfo['antiqueDesc']}`
        this.imgSrc = `${process.env.VUE_APP_BASE_API}/museum-antique/res${this.fileInfo['antiqueImg']}`
        this.audioSrc = `${process.env.VUE_APP_BASE_API}/museum-antique/res${this.fileInfo['antiqueAudio']}`
        this.modelSrc = `${process.env.VUE_APP_BASE_API}/museum-antique/res${this.fileInfo['antiqueModel']}`
      }).catch(err => {
        console.log(err)
      }) */
    },
    /**
     * @description 请求博物馆列表
     * @returns
     */
    fetchMuseum () {
      /*  getMuseumList(this.testQuery).then(res => {
        console.log(res.data, '博物馆列表')
      }).catch(err => {
        console.log(err)
      }) */
    }
  }
}
</script>

<style lang="scss" scoped>
.carousel { // 轮播图
  margin-top: 10px;
  padding: 0 5%;

  :deep(.el-carousel__container) {
    height: 400px;
  }
  
  :deep(.el-carousel__item) {
    // 中间卡片的缩放效果
    transform: scale(0.5);
  
    transition: transform 0.3s ease;
    
    &.is-active {
      transform: scale(1);  // 中间活跃卡片放大到正常大小
    }
  }

  .carousel-img {
    width: 100%;
    height: 400px;
    object-fit: cover;
    border-radius: 5px;
  }
}

// for the file view
.el-dialog__body {
  padding: 10px;
}

.file-media {
  display: flex;
  width: 100%;

  .file-left-i-a {
    width: 30%;
  }

  .file-right-m {
    width: 70%;

    .file-model {
      height: 100%;
    }
  }
}

.featured-antique-img { // 精选文物 博物馆 精选主题
  margin-top: 3%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.view-all {
  position: absolute;
  margin-top: -15px;
  width: 100%;
}
.view-all a {
  text-decoration: none;
  float: right;
  padding-right: 18.7%;
  color: black;
  &:hover {
    color: #2752c7;
  }
}
.featured-antique {
  margin-top: 1%;
  width: 65%;
  margin-left: 16.3%;
  display: flex;
  justify-content: center;
  align-items: center;
}
.antiques {
  margin-left: 3.5%;
}
.antique {
  height: 16.3em;
  overflow: hidden;
}
.antique img {
  width: 100%;
  height: auto;
}
.featured-museum {
  margin-top: 15px;
  width: 70%;
  margin-left: 14.2%;
  display: flex;
  justify-content: center;
  align-items: center;
}
.museums {
  margin-left: 2.4%;
}
.museum {
  overflow: hidden;
  color: black;
  text-decoration: none;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.museum img {
  width: 100%;
  height: auto;
}
.museum span{
  display: inline-block;
}
.under {
  margin-bottom: 5%;
}
.featured-theme {
  width: 65%;
  margin-left: 16.5%;
  margin-bottom: 35px;
  display: flex;
  justify-content: center;
  align-content: center;
}
.themes{
  margin-left: 3%;
}
.theme {
  margin-top: 15px;
  overflow: hidden;
  color: black;
  display: flex;
  justify-content: center;
  align-items: center;
}
.theme img {
  width: 100%;
  height: auto;
}
</style>
