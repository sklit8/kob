<template>
    <div class="top"></div>
    <div class="text-center">
      <main class="form-signin">
        <form @submit.prevent="login">
          <img class="mb-4" src="@/assets/images/logo.png" alt="" width="150" height="150" />
          <h1 class="h3 mb-3 fw-normal">请登录</h1>
          <div class="form-floating">
            <input v-model="username" type="text" class="form-control" id="username" placeholder="account" />
            <label for="username">Account</label>
          </div>
          <div class="form-floating">
            <input v-model="password" type="password" class="form-control" id="password" placeholder="Password" />
            <label for="password">Password</label>
          </div>
          <div type="error" class="error_message">{{error_message}}</div>
          <div class="checkbox mb-3">
            <label>
              <input type="checkbox" value="remember-me" /> 记住用户名和密码
            </label>
          </div>
          <button class="w-100 btn btn-lg btn-primary" type="submit" >登录</button>
          <p class="mt-5 mb-3 text-muted">&copy; sklit</p>
        </form>
      </main>
    </div>


  <BottomBar />
</template>

<script>
import BottomBar from '../../../components/BottomBar.vue'
import { useStore } from 'vuex';
import { ref } from 'vue'
import router from '../../../router/index'
export default {
  components: {
    BottomBar,
  },
  setup(){
      const store = useStore();
      let username = ref('');
      let password = ref('');
      let error_message = ref('');

      const jwt_token = localStorage.getItem("jwt_token");
      if(jwt_token){
        store.commit("updateToken",jwt_token);
        store.dispatch("getInfo",{
          success(){
            router.push({name:'home'});
            store.commit("updatePulling",false);
          },
          error(){
          }
        })
      }else{
        store.commit("updatePulling",false);
      }

      const login = () =>{
          error_message.value = '';
          store.dispatch("login",{
              username:username.value,
              password:password.value,
              success(){
                  store.dispatch("getInfo",{
                      success(){
                          router.push({name:'home'});
                          store.commit("updatePulling",false);
                      }
                  })
                  router.push({name:'home'});
              },
              error(res){
                  console.log(res);
                  error_message.value = '用户名或密码错误';
              }
          })
      }

      return{
          username,
          password,
          error_message,
          login,
      }
  }
};
</script>

<style scoped>
div.top{
    height: 5vh;
}
.bd-placeholder-img {
  font-size: 1.125rem;
  text-anchor: middle;
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
}

@media (min-width: 768px) {
  .bd-placeholder-img-lg {
    font-size: 3.5rem;
  }
}
body {
  height: 100%;
  display: flex;
  align-items: center;
  padding-top: 40px;
  padding-bottom: 40px;
  background-color: #f5f5f5;
}

.form-signin {
  width: 100%;
  max-width: 330px;
  padding: 15px;
  margin: auto;
}

.form-signin .checkbox {
  font-weight: 400;
}

.form-signin .form-floating:focus-within {
  z-index: 2;
}

.form-signin input[type="email"] {
  margin-bottom: -1px;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0;
}

.form-signin input[type="password"] {
  margin-bottom: 10px;
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}
div.error_message{
    color: red;
}
.register-link{
  color: blue;
}

</style>