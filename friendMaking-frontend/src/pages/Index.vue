<template>
  <user-card-list :user-list="userList" />
  <van-empty v-if="!userList || userList.length <1" description="数据为空"></van-empty>
</template>

<script setup >
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../../plugins/myAxios.ts";
import {showToast} from "vant";

const route = useRoute();
const userList = ref([]);

onMounted(async () => {
  const userListData = await myAxios.get('/user/recommend',{
    params:{
      pageSize: 8,
      pageNum: 1,
    },
  })
      .then(function (response) {
        // console.log("/user/search/tags success",response);
        showToast("请求成功")
        return response.data?.data?.records;
      })
      .catch(function (error){
        // console.log("/user/search/tags error",error);
        showToast("请求失败")
      })
  if (userList){
    userListData.forEach(user => {
      if (user.tags){
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
  }
})

</script>

<style scoped>

</style>