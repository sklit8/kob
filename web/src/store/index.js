import { createStore } from 'vuex'
import ModuleUser from './user'
import ModulePk from './pk'
import ModuleRecord from './record'

export default createStore({
  state: {
    productUrl:"http://127.0.0.1:3000",
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user:ModuleUser,
    pk:ModulePk,
    record:ModuleRecord,
  }
})
