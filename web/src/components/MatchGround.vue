<template>
    <div class="matchground">
        <div class="row">
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user-name">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-4">
                <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                        <option selected c value="-1">亲自上阵</option>
                        <option v-for="bot in bots" :key="bot.id" :value="bot.id">{{bot.title}}</option>
                    </select>
                </div>
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo" alt="">
                </div>
                <div class="user-name">
                    {{ $store.state.pk.opponent_username }}
                </div>
            </div>
            <div style="text-align:center;">
                <i class="iconfont" v-if="match_btn_info === '取消' || match_btn_info_ai === '取消'">正在匹配，请稍等...</i>
                <i class="iconfont" else-if>&nbsp;</i>
            </div>
            <div class="col-12" style="text-align:center;">
                <div class="matching-waiting">
                </div>
                <div>
                    <button @click="click_match_btn_ai" type="button" class="btn btn-primary btn-lg">{{ match_btn_info_ai }}</button>
                </div>
                <div style="margin-top:5vh;">
                    <button @click="click_match_btn" type="button" class="btn btn-primary btn-lg">{{ match_btn_info }}</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue'
import { useStore } from 'vuex'
import $ from 'jquery'
export default {
    setup(){
        const store = useStore();
        let bots = ref([]);
        let match_btn_info = ref("双人对战");
        let match_btn_info_ai = ref("人机对战");
        let select_bot = ref("-1");

        const click_match_btn = () => {
            if(match_btn_info.value === '双人对战'){
                match_btn_info.value = "取消";
                console.log(select_bot.value);
                store.state.pk.socket.send(JSON.stringify({
                    event:"start-matching",
                    bot_id:select_bot.value,
                }));
            }else {
                match_btn_info.value = "双人对战";
                store.state.pk.socket.send(JSON.stringify({
                    event:"stop-matching",
                }));
            }
        }

        const click_match_btn_ai = () => {
            if(match_btn_info_ai.value === '人机对战'){
                match_btn_info_ai.value = "取消";
                console.log(select_bot.value);
                store.state.pk.socket.send(JSON.stringify({
                    event:"start-matching-ai",
                    bot_id:select_bot.value,
                }));
            }else {
                match_btn_info_ai.value = "人机对战";
                store.state.pk.socket.send(JSON.stringify({
                    event:"stop-matching",
                }));
            }
        }
                
        const refresh_bots = () =>{
            $.ajax({
                url:store.state.productUrl+"/api/user/bot/getList",
                type:'get',
                headers:{
                    Authorization:"Bearer " + store.state.user.token,
                },
                success(res){
                    bots.value = res;
                }
            })
        }

        refresh_bots();//从云端获取bots

        return {
            match_btn_info,
            match_btn_info_ai,
            click_match_btn,
            click_match_btn_ai,
            bots,
            select_bot,
        }
    }
}
</script>

<style scoped>
    div.matchground{
        width: 55vw;
        height: 65vh;
        margin: 40px auto;
        background-color: rgba(8, 193, 250, 0.1);
        border-radius: 5%;
    }
    div.user-photo{
        text-align: center;
        padding-top: 10vh;
    }
    div.user-photo > img{
        border-radius: 50%;
        width: 20vh;
    }
    div.user-name{
        text-align: center;
        padding-top: 2vh;
        color: black;
    }
    div.user-select-bot{
        padding-top: 20vh;
    }
    div.user-select-bot > select{
        width: 60%;
        margin: 0 auto
    }
    div.matching-waiting{
        margin-bottom: 5vh;
    }
    /* CDN 服务仅供平台体验和调试使用，平台不承诺服务的稳定性，企业客户需下载字体包自行发布使用并做好备份。 */
    @font-face {
    font-family: "iconfont Bold";font-weight: 700;src: url("//at.alicdn.com/wf/webfont/dl8N4da2qMRK/IBledjoVMQ3n8c9C0vgbV.woff2") format("woff2"),
    url("//at.alicdn.com/wf/webfont/dl8N4da2qMRK/Gnu6JI3YMAv398p_ZQbo3.woff") format("woff");
    font-display: swap;
    }
    .iconfont{
    font-family:"iconfont Bold" !important;color: #051889;
    font-size:37px;font-style:normal;
    -webkit-font-smoothing: antialiased;
    -webkit-text-stroke-width: 0.2px;
    -moz-osx-font-smoothing: grayscale;}
</style>