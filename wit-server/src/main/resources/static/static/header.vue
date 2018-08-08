<template>
  <div class="header">
    <div class="navbar clearfix">
      <div class="com-logo">
        <img src="@/assets/img/1_icon.png"  class="img-align" alt="menu icon">
        <span class="navbar-text-root">智慧校园系统</span><span style="color:#eeeeee">|</span><span class="navbar-text-child">泸州老窖天府中学</span>
      </div>
      <el-menu
        :default-active="$route.path"
        class="narbar-menu"
        mode="horizontal"
        @select="handleSelect"
        background-color="#477e66"
        text-color="#b8d6cd"
        active-text-color="#fff">
        <el-menu-item index="/index"><router-link to="/index">首页</router-link></el-menu-item>
        <el-menu-item index="/personnel" ><router-link  :to="{path:'personnel'}">人事</router-link> </el-menu-item>
        <el-submenu index="/classinfo">
          <template slot="title">基础信息</template>
          <el-menu-item  index="/classinfo/?clsInfoAct=first">
            <router-link :to="{path:'/classinfo',query: {clsInfoAct: 'first'}}">
              <p class="headerIcon headerIcon1"></p>班级信息
            </router-link>
          </el-menu-item>
          <el-menu-item index="/classinfo/?clsInfoAct=second">
            <router-link :to="{path:'/classinfo',query: {clsInfoAct: 'second'}}">
              <p class="headerIcon headerIcon2"></p>课程管理
            </router-link>
          </el-menu-item>
          <el-menu-item index="/classinfo/?clsInfoAct=third">
            <router-link :to="{path:'/classinfo',query: {clsInfoAct: 'third'}}">
              <p class="headerIcon headerIcon3"></p>学生信息
            </router-link>
          </el-menu-item>
          <el-menu-item index="/classinfo/?clsInfoAct=fourth">
            <router-link :to="{path:'/classinfo',query: {clsInfoAct: 'fourth'}}">
              <p class="headerIcon headerIcon4"></p>学段管理
            </router-link>
          </el-menu-item>
        </el-submenu>
        <el-menu-item index="/examList" ><router-link  :to="{path:'examList'}">考试</router-link> </el-menu-item>

        <el-menu-item index="/achievement"><router-link to="/achievement">成绩管理</router-link></el-menu-item>
        <el-menu-item index="/authority"><router-link to="/authority">权限</router-link></el-menu-item>
        <el-submenu index="7" style="margin-left: 38px">
          <template slot="title">
            <img :src="portraitUrl" alt="handerimg" class="headerImg">
             {{userName}}
          </template>
          <el-menu-item index="7-1">
              <div @click="passwordEdit">
                <p class="headerIcon headerIcon5"></p>修改密码
              </div>
          </el-menu-item>
          <el-menu-item index="7-2">
              <div @click="loginOut">
                <p class="headerIcon headerIcon6"></p>退出登录
              </div>
          </el-menu-item>
        </el-submenu>
      </el-menu>
    </div>


    <!-- 修改密码弹窗 -->
     <div class="pwordMidfyBox" v-show="pwordsessios.pwordPopUpFlag">
      <div class="pwordMidfy">
        <div class="pwordPopTitBox">
          <div class="pwordPopTit">
            <img src="@/assets/left.png" alt="">

            <span>修改密码</span>
            <img src="@/assets/right.png" alt="">
          </div>
          <div class="pwordMidfyCont">
            <el-form ref="pwordObj" v-model="login" label-width="80px">
              <el-form-item label="用户名">
                <el-input v-model="login.adminName" :key="login.adminName" placeholder="用户名"></el-input>
              </el-form-item>
              <el-form-item label="旧密码">
                <el-input v-model="login.oldpassWord" :key="login.oldpassWord" type="password"></el-input>
              </el-form-item>
              <el-form-item label="新密码">
                <el-input v-model="login.newpassWord1" type="password" :key="login.newpassWord1" @blur="passwordValidate"></el-input>
              </el-form-item>
              <el-form-item label="确认密码">
                <el-input v-model="login.newpassWord2" type="password" :key="login.newpassWord2" @blur="passwordCheckValidate"></el-input>
              </el-form-item>
              <div class="pwordMidfyBtnBox">
                <button @click="pwordBoxShowClick">
                  确认
                </button>
                <button @click="pwordBoxHideClick">
                  返回
                </button>
              </div>
            </el-form>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  data () {
    return {
      pwordsessios:{
        pwordPopUpFlag:false//控制弹窗显示
      },
      login:{
        adminName:'',
        oldpassWord:'',
        newpassWord1:'',
        newpassWord2:''
      },
      // adminName:'',
      // oldpassWord:'',
      // newpassWord1:'',
      // newpassWord2:''
    }
  },
  methods: {
    handleSelect (key, keyPath) {
      console.log(key, keyPath)
    },
    //判断两次密码是否一致
    passwordEdit(){
      this.pwordsessios.pwordPopUpFlag = true
    },
    passwordValidate() {
      if(!(/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$/.test(this.login.newpassWord1))) {
          this.$message.error('密码设置为6-16位的数字和字母组合')
      }
    },
    passwordCheckValidate() {
      console.log('nihao');
      if(!(/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$/.test(this.login.newpassWord2))) {
          this.$message.error('密码设置为6-16位的数字和字母组合')
      }else if(this.login.newpassWord2 !==this.login.newpassWord1 ){
          this.$message.error('两次密码不匹配')
      }
    },
    //确认更改
    pwordBoxShowClick () {
      let updata={
        oldPass:this.login.oldpassWord,
        newPass:this.login.newpassWord2
      }
      this.$http.put('/api/v1/admin/updatePass',updata)
      .then(res=>{
        if (res.status === 200) {
            if (res.data.code === 1) {
              this.$notify({
                title: '提示信息',
                message: '操作成功',
                type: 'success'
              })
              this.$router.push({path: '/login'})
            }else {
              // this.fullscreenLoading = false

              this.$notify({
                title: '提示信息',
                message: res.data.msg,
                type: 'error'
              })
            }
        }
      })
      .catch(err => {
          console.log(err)
        })
      this.pwordsessios.pwordPopUpFlag = false
    },
    pwordBoxHideClick () {
      this.pwordsessios.pwordPopUpFlag = false
    },
    loginOut () {
      this.$http
        .delete(
          '/api/v1/admin/ifLogout'
        )
        .then(res => {
          if (res.status === 200) {
            if (res.data.code === 1) {
              window.localStorage.removeItem('userInfo')
              this.$router.push({path: '/'})
            } else {
              console.log(res.data.msg)
              this.$message({
                //  饿了么的消息弹窗组件,类似toast
                showClose: true,
                message: res.data.msg,
                type: 'error'
              })
            }
          }
        })
        .catch(err => {
          console.log(err)
        })
    }
  },
  computed: {
    userInfo () {
      // console.log('localStorge', JSON.parse(window.localStorage.getItem('userInfo')))
      return JSON.parse(window.localStorage.getItem('userInfo'))
    },
    userName () {
      return this.userInfo.fullName
    },
    portraitUrl () {
      return this.userInfo.photoUrl
    }
}

}
</script>

<style lang="scss">
.el-menu--horizontal>.el-menu-item a, .el-menu--horizontal>.el-menu-item a:hover {
  color: inherit;
  width: 100%;
  height: 100%;
  display: block;
  padding: 0 20px;
}
.el-menu--popup {
  width: 113px !important;
  min-width: 113px !important;
}
.el-menu-item > a {
  width: 100%;
  height: 100%;
  display: block;
  color: inherit;
}
.header {
  background: #477e66;
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  z-index: 10;
}
.navbar {
  width: 960px;
  margin: 0 auto;
  ul{
    border:none;
    li:hover{
      background-color: transparent !important;
      border:none;
    }
    li .el-submenu__title:hover{
      background-color: transparent !important;
    }
  }

}
.el-header {
  padding:0;
}
.com-logo {
  width: 250px;
  line-height: 60px;
  float:left;
}
.img-align {
  vertical-align: middle;
}

.navbar-text-root {
  font-size: 14px;
  color:white;
  margin-left: 15px;
  margin-right: 11px;

}
.navbar-text-child {
  margin-left: 11px;
  font-size: 12px;
  color:white;

}
.narbar-menu {
  float: right;

}
.clearfix:after {
  content: ".";
  display: block;
  height: 0;
  clear: both;
  visibility: hidden;
}
.el-menu--collapse .el-menu .el-submenu, .el-menu--popup {
  min-width: 113px;
}
.el-menu.item {
  padding: 0 15px;
}
.el-icon-arrow-down:before {
  content: "\E603";
  color:white;
}
.el-submenu__title {
  padding: 0 10px;
}
.el-menu--popup-bottom-start{
  margin-top: 0;
}
.headerIcon{
  float: left;
  width: 26px;
  height: 26px;
  margin: 6px 4px 0 0;
  background: url("../assets/headericon.png") no-repeat;
}
.headerIcon1{
  background-position: -2px -79px;
}
.headerIcon2{
  background-position: -1px -183px;
}
.headerIcon3{
  background-position: -1px -287px;
}
.headerIcon4{
  background-position: -1px -235px;
}
.headerIcon5{
  background-position: -1px -131px;
}
.headerIcon6{
  background-position: -1px -339px;
}

.el-menu--horizontal .el-menu .el-menu-item, .el-menu--horizontal .el-menu .el-submenu__title{
  background-color: #ffffff ;
  line-height: 38px;
  height: 38px;
}
.el-menu--horizontal{
  background-color: #ffffff;
  ul{
    background-color: #ffffff !important;
    padding: 0;
    li{
      background-color: #ffffff !important;
    }
    li:hover{
      color: #fff !important;
      background-color: #68bd8b !important;
      .headerIcon1{
        background-position: -2px -53px;
      }
      .headerIcon2{
        background-position: -1px -157px;
      }
      .headerIcon3{
        background-position: -1px -261px;
      }
      .headerIcon4{
        background-position: -1px -209px;
      }
      .headerIcon5{
        background-position: -1px -104px;
      }
      .headerIcon6{
        background-position: -1px -313px;
      }
    }
    .is-active{
      color: #fff;
      background-color: #68bd8b !important;
      .headerIcon1{
        background-position: -2px -53px;
      }
      .headerIcon2{
        background-position: -1px -157px;
      }
      .headerIcon3{
        background-position: -1px -261px;
      }
      .headerIcon4{
        background-position: -1px -209px;
      }
      .headerIcon5{
        background-position: -1px -104px;
      }
      .headerIcon6{
        background-position: -1px -313px;
      }
    }

  }
}
.headerImg{
  position: absolute;
  left: -26px;
  top: 50%;
  margin-top: -16px;
  margin-bottom: -16px;
  width: 32px;
  height: 32px;
  border-radius: 50%;

}
  /*.el-menu--horizontal>.el-submenu.is-active .el-submenu__title{*/
    /*border-bottom: none !important;*/
  /*}*/
.el-menu--horizontal .el-menu-item{
  padding: 0;
}

//弹窗样式
.pwordPopTitBox {
      height: 50px;
      line-height: 50px;
      background-color: #6bc08b;
      text-align: center;
      color: #fff;
      font-size: 18px;
      border-top-left-radius: 10px;
      border-top-right-radius: 10px;
      img {
        vertical-align: middle;

      }
    }
.pwordMidfyBox{
      background-color: rgba(0, 0, 0, .5);
      position: fixed;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      z-index: 10;
      /*display: none;*/
      .pwordMidfy{
        width: 400px;
        height: 400px;
        position: absolute;
        left: 50%;
        top: 50%;
        margin: -175px -200px;
        background-color: #fff;
        border-radius: 10px;
        box-shadow: 0px 0px 6px #c4c8ca;
        .pwordMidfyCont{
          padding: 20px;
        }
      }
    }
    .pwordMidfyBtnBox{
      width: 100%;
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      padding-bottom: 30px;
      button{
        width: 100px;
        height: 30px;
        background-color: #6bc08b;
        border-radius: 25px;
        color: #fff;
        font-size: 14px;
        cursor: pointer;
      }

    }
</style>
