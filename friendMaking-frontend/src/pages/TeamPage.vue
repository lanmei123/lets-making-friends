<template>
  <div id="teamPage">
    <van-search v-model="searchText" placeholder="搜索队伍" @search="onSearch"></van-search>
    <van-button type="primary" @click="doJoinTeam">创建队伍</van-button>
    <team-card-list :team-list="teamList" ></team-card-list>
    <van-empty v-if="teamList?.length<1" description="数据为空"/>
  </div>
 
</template>

<script setup>
import {useRouter} from "vue-router";
import TeamCardList from "../components/TeamCardList.vue";
import {onMounted, ref} from "vue";
import myAxios from "../../plugins/myAxios";
import {showFailToast} from "vant";

const router = useRouter();
const searchText = ref('');
// 跳转到加入队伍页
const doJoinTeam = () => {
  router.push({
    path: "/team/add"
  })
}
const teamList = ref([]);

// 挂载组队的列表

const listTeam = async (val = '') =>{
  const res = await myAxios.get("/team/list",{
    params:{
      searchText: val,
      pageNum: 1,
    }
  });
  if (res?.data.code === 0 && res.data){
    teamList.value = res.data.data;
  }else {
    showFailToast("加载队伍失败");
  }
}
onMounted(() =>{
  listTeam();
})
const onSearch =  (val) =>{
  listTeam(val);
}
</script>

<style scoped>

</style>