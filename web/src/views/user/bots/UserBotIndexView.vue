<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top:20px">  
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width:100%">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top:20px">
                    <div class="card-header" style="font-size:130%">
                        我的bot
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn"> 
                            创建Bot
                        </button>

                        <!-- Modal -->
                        <div class="modal fade" id="add-bot-btn" tabindex="-1">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="exampleModalLabel">创建Bot</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label for="add-bot-title" class="form-label">名称</label>
                                        <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                    </div>
                                    <div class="mb-3">
                                        <label for="add-bot-dexcription" class="form-label">Bot简介</label>
                                        <textarea v-model="botadd.description" class="form-control" id="add-bot-dexcription" rows="3" placeholder="请输入Bot简介"></textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label for="add-bot-code" class="form-label">代码</label>
                                        <VAceEditor
                                            v-model:value="botadd.content"
                                            @init="editorInit"
                                            lang="c_cpp"
                                            theme="textmate"
                                            style="height: 300px" />
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <div class="error_message">{{ botadd.error_message }}</div>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="button" class="btn btn-primary" @click="add_bot">创建</button>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>名称</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>
                                        <button type="button" class="btn btn-success" style="margin-right:10px" @click="update_bot(bot)" data-bs-toggle="modal" :data-bs-target="'#update-bot-btn'+bot.id">修改</button>
                                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">删除</button>

                                        <!-- Modal -->
                                        <div class="modal fade" :id="'update-bot-btn' + bot.id" tabindex="-1">
                                            <div class="modal-dialog modal-xl">
                                                <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title" id="exampleModalLabel">修改Bot</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="mb-3">
                                                        <label for="add-bot-title" class="form-label">名称</label>
                                                        <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-dexcription" class="form-label">Bot简介</label>
                                                        <textarea v-model="bot.description" class="form-control" id="add-bot-dexcription" rows="3" placeholder="请输入Bot简介"></textarea>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-code" class="form-label">代码</label>
                                                        <VAceEditor
                                                            v-model:value="bot.content"
                                                            @init="editorInit"
                                                            lang="c_cpp"
                                                            theme="textmate"
                                                            style="height: 300px" />
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <div class="error_message">{{ bot.error_message }}</div>
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                                    <button type="button" class="btn btn-primary" @click="update_bot(bot)">保存修改</button>
                                                </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <BottomBar />
</template>

<script>
import BottomBar from '../../../components/BottomBar.vue'
import { ref , reactive} from 'vue'
import $ from 'jquery'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from 'vue3-ace-editor'
import ace from 'ace-builds'
export default{
    components:{
        VAceEditor,
        BottomBar,
    },
    setup(){
        ace.config.set(
            "basePath", 
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")

        const store = useStore();

        const botadd = reactive({
            title:"",
            description:"",
            content:"",
            error_message:"",
        })

        let bots = ref([]);
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

        refresh_bots();

        const add_bot = () =>{
            botadd.error_message  = "";
            $.ajax({
                url:store.state.productUrl+"/api/user/bot/add",
                type:'POST',
                data:{
                    title:botadd.title,
                    description:botadd.description,
                    content:botadd.content,
                },
                headers:{
                    Authorization:"Bearer " + store.state.user.token,
                },
                success(res){
                    if(res.error_message === 'success'){
                        botadd.title="";
                        botadd.description="";
                        botadd.content="";
                        Modal.getInstance("#add-bot-btn").hide();
                        refresh_bots();
                    }else {
                        botadd.error_message = res.error_message;
                    }
                }
            })
        };

        const remove_bot = (bot) => {
            $.ajax({
                url:store.state.productUrl+"/api/user/bot/remove",
                type:'POST',
                data:{
                    bot_id:bot.id,
                },
                headers:{
                    Authorization:"Bearer " + store.state.user.token,
                },
                success(res){
                    if(res.error_message === 'success'){
                        refresh_bots();
                    }
                }
            })
        };

        const update_bot = (bot) =>{
            botadd.error_message  = "";
            $.ajax({
                url:store.state.productUrl+"/api/user/bot/update",
                type:'POST',
                data:{
                    bot_id:bot.id,
                    title:bot.title,
                    description:bot.description,
                    content:bot.content,
                },
                headers:{
                    Authorization:"Bearer " + store.state.user.token,
                },
                success(res){
                    if(res.error_message === 'success'){
                        Modal.getInstance("#update-bot-btn" + bot.id).hide();
                        refresh_bots();
                    }else {
                        botadd.error_message = res.error_message;
                    }
                }
            })
        };

        return{
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
        }
    }
}
</script>

<style scoped>
div.error_message{
    color: red;
}
</style>