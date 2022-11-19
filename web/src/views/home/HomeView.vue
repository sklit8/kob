<template>
    <div class="container">
        <div class="row">
            <div class="col">
                <div class="top">
                    <i class="title" >King Of Snakes</i>
                </div>
                <div class="game-logo">
                    <img class="mb-4" src="@/assets/images/logo.png" alt="" width="150" height="150" />
                </div>
                <div style="text-align:center;">
                    <router-link role="button" class="btn btn-outline-primary btn-lg" :to="{name:'pk_index'}">开始游戏</router-link>
                </div>
                <div style="text-align:center;margin-top:4vh;">
                    <button type="button" class="btn btn-outline-success btn-lg" data-bs-toggle="modal" data-bs-target="#gameIntroduce">玩法介绍</button>
                </div>
                <div style="text-align:center;margin-top:4vh;">
                    <button type="button" class="btn btn-outline-primary btn-lg" @click="logout">退出游戏</button>
                </div>
            </div>
                <!-- Modal -->
                <div class="modal fade" id="gameIntroduce" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="exampleModalLabel">玩法介绍</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            此游戏为回合移动制，玩家须在7秒内移动，否则判定为输<br>
                            通过移动你的小蛇来尽可能的堵住对面的小蛇！<br>
                            当蛇头撞墙或者触碰到双方的身体后游戏结束！<br>
                            <br>
                            请通过W,A,S,D来控制移动<br>
                            你也可以在我的Bot里添加一个智能的Bot代码来代替你控制小蛇的移动!
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-success" data-bs-dismiss="modal">OK</button>
                        </div>
                        </div>
                    </div>
                </div>
        </div>
    </div>
    <BottomBar />
</template>

<script>
import BottomBar from '../../components/BottomBar.vue'
import router from '../../router/index'
import { useStore } from 'vuex'

export default {
    components:{
        BottomBar,
    },
    setup(){
        const store = useStore();
        const logout = () => {
            store.dispatch("logout");
            router.push({name:"user_login_index"})
        }
        return {
            logout
        }
    }
}
</script>

<style scoped>
    @font-face {
    font-family: "站酷高端黑 Regular";font-weight: 400;src: url("//at.alicdn.com/wf/webfont/dl8N4da2qMRK/ZISgVMrUC7jaAKUKXTv0D.woff2") format("woff2"),
    url("//at.alicdn.com/wf/webfont/dl8N4da2qMRK/lA6by_EP6UlRHJb6kpShF.woff") format("woff");
    font-display: swap;
    }
    .title{
    font-family:"站酷高端黑 Regular" !important;color: #111111;
    font-size:70px;font-style:normal;
    -webkit-font-smoothing: antialiased;
    -webkit-text-stroke-width: 0.2px;
    -moz-osx-font-smoothing: grayscale;
    }
    div.game-logo{
        margin-top: 3vh;
        text-align: center;      
    }
    div.top{
        text-align: center;
        margin-top: 6vh;
        height: 10vh;
    }
</style>