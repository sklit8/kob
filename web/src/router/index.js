import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '../views/pk/PkIndexView'
import RecordIndexView from '../views/record/RecordIndexView'
import RecordContentView from '../views/record/RecordContentView'
import RanklistIndexView from '../views/ranklist/RanklistIndexView'
import UserBotIndexView from '../views/user/bots/UserBotIndexView'
import NotFound from '../views/error/NotFound'
import UserAccountLoginView from '../views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '../views/user/account/UserAccountRegisterView'
import HomeView from '../views/home/HomeView'
import DisscussView from '../views/disscussion/DisscussView'
import store from '@/store'
const routes = [
  {
    path:"/",
    name:"home",
    redirect:"/home/",
    meta:{
      requestAuth:true
    }
  },
  {
    path:"/home/",
    name:"home_index",
    component:HomeView,
    meta:{
      requestAuth:true
    }
  },
  {
    path:"/disscussion/",
    name:"disscuss_index",
    component:DisscussView,
    meta:{
      requestAuth:true
    }
  },
  {
    path:"/pk/",
    name:"pk_index",
    component:PkIndexView,
    meta:{
      requestAuth:true
    }
  },
  {
    path:"/record/",
    name:"record_index",
    component:RecordIndexView,
    meta:{
      requestAuth:true
    }
  },
  {
    path:"/record/:recordId",
    name:"record_content",
    component:RecordContentView,
    meta:{
      requestAuth:true
    }
  },
  {
    path:"/ranklist/",
    name:"ranklist_index",
    component:RanklistIndexView,
    meta:{
      requestAuth:true
    }
  },
  {
    path:"/user/bot/",
    name:"user_bot_index",
    component:UserBotIndexView,
    meta:{
      requestAuth:true
    }
  },
  {
    path:"/user/account/login/",
    name:"user_login_index",
    component:UserAccountLoginView,
    meta:{
      requestAuth:false
    }
  },
  {
    path:"/user/account/register/",
    name:"user_register_index",
    component:UserAccountRegisterView,
    meta:{
      requestAuth:false
    }
  },
  {
    path:"/404/",
    name:"notfound_index",
    component:NotFound
  },
  {
    path:"/:catchAll(.*)",
    redirect:"/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to,from,next) => {
  if(to.meta.requestAuth &&  !store.state.user.is_login){
    next({name:'user_login_index'});
  }else {
    next();
  }
})
export default router
