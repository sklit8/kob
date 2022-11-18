import $ from 'jquery'
import store from '.';

export default({
  state: {
      id:"",
      username:"",
      photo:"",
      token:"",
      is_login:"",
      pulling_info:false,
  },
  getters: {
  },
  mutations: {
      updateUser(state,user){
          state.id = user.id;
          state.username = user.username;
          state.photo = user.photo;
          state.is_login = user.is_login;
      },
      updateToken(state,token){
          state.token = token;
      },
      logout(state){
            state.id="";
            state.username="";
            state.photo="";
            state.token="";
            state.is_login=false;   
      },
      updatePulling(state,pulling_info){
            state.pulling_info=pulling_info;
      }
  },
  actions: {
      login(context,data){
        $.ajax({
            url:store.state.productUrl+"/api/user/account/token",
            type:'post',
            data:{
              username:data.username,
              password:data.password
            },
            success(res){
                if(res.error_message === "success"){
                    localStorage.setItem("jwt_token",res.token);
                    context.commit("updateToken",res.token);
                    data.success(res);
                }else {
                    data.error(res);
                }
            },
            error(res){
                data.error(res);
            }
          });
      },
      getInfo(context,data){
        $.ajax({
            url:store.state.productUrl+"/api/user/account/info",
            type:'get',
            headers:{
              Authorization:"Bearer " + context.state.token,
            },
            success(res){
                if(res.error_message === "success"){
                    context.commit("updateUser",{
                        ...res,
                        is_login:true
                    });
                    data.success(res);
                }else {
                    data.error(res);
                }
                
            },
            error(res){
              data.error(res);
            }
          });
      },
      logout(context){
        localStorage.removeItem("jwt_token");
        context.commit("logout");
      }
  },
  modules: {
  }
})
