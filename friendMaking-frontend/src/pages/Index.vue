<template>
  <van-cell center title="心动模式">
    <template #right-icon>
      <van-switch v-model="isMatchMode" size="24" @change="handleSwitchChange(isMatchMode)"/>
    </template>
  </van-cell>
  <user-card-list :user-list="userList" :loading="loading"/>
  <van-empty v-if="!userList || userList.length <1" description="数据为空"></van-empty>
</template>

<script setup>
import {useRoute} from "vue-router";
import {onMounted, ref, watchEffect} from "vue";
import myAxios from "../../plugins/myAxios.ts";
import {showSuccessToast, showToast} from "vant";

const route = useRoute();
const userList = ref([]);
const isMatchMode = ref(false);
const loading = ref(false);


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
/**
 * 默认匹配用户
 */
const doDefault = async () =>{
  loading.value = true;
  await delay(1000);
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
  loading.value = false;
}

/**
 * 精确匹配用户
 */
const doMatch = async () =>{
  loading.value = true;
  await delay(1000);
  const num = 10;
  const userListData = await myAxios.get('/user/match',{
    params:{
      num,
    },
  })
      .then(function (response) {
        showToast("请求成功")
        return response.data?.data;
      })
      .catch(function (error){
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
  loading.value = false;
}
const handleSwitchChange = (value) =>{
  if (value){
    doMatch();
  }else {
    doDefault();
  }
}
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

</script>

<style scoped>

</style>