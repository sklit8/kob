<template>
  <div class="dis">待开放...</div>

  <BottomBar />
</template>

<script>
import BottomBar from "../../components/BottomBar.vue"
export default {
  components: {
    BottomBar,
  },
  setup(){
  }
};
</script>

<style scoped>
  div.dis{
    text-align: center;
    padding-top: 30vh;
    font-size: 30px;
    font-weight: 600;
  }
</style>