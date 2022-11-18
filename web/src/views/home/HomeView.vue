<template>
    <div class="container">
        <div class="row">
            <div class="col">
                
            </div>
            <div class="col">
                <div class="game-logo top">
                </div>
                <div style="text-align:center;">
                    <router-link role="button" class="btn btn-outline-primary" :to="{name:'pk_index'}">开始游戏</router-link>
                </div>
                <div style="text-align:center;margin-top:5vh;">
                    <button type="button" class="btn btn-outline-success" data-bs-toggle="modal" data-bs-target="#gameIntroduce">玩法介绍</button>
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
                            通过移动你的小蛇来尽可能的堵住对面的小蛇！<br>
                            当蛇头撞墙或者触碰到双方的身体后游戏结束！<br>
                            <br>
                            请通过W,A,S,D来控制移动<br>
                            你也可以在我的Bot里添加一个Bot代码来代替你控制小蛇的移动!
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-success" data-bs-dismiss="modal">OK</button>
                        </div>
                        </div>
                    </div>
                </div>
            <div class="col">
                
            </div>
        </div>
    </div>
    <BottomBar />
</template>

<script>
import BottomBar from '../../components/BottomBar.vue'
export default {
    components:{
        BottomBar,
    }
}
</script>

<style scoped>
    div.game-logo{
        width: 10vw;
        height: 10vh;
    }
    div.top{
        height: 30vh;
    }
</style>