<template>
  <user-card-list :user-list="userList" />
  <van-empty v-if="!userList || userList.length <1" description="搜索结果为空"></van-empty>
</template>

<script setup >
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../../plugins/myAxios.ts";
import {showToast} from "vant";
import qs from 'qs';
import UserCardList from "../components/userCardList.vue";

const route = useRoute();
const { tags } = route.query;

const userList = ref([]);

onMounted(async () => {
  const userListData = await myAxios.get('/user/search/tags',{
    params:{
      tagNameList: tags
    },
    paramsSerializer: params => {
      return qs.stringify(params,{indices: false})
    }
  })
      .then(function (response) {
        // console.log("/user/search/tags success",response);
        showToast("请求成功")
        return response.data?.data;
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

const  mockUser = {
  id: 1,
  username: '蓝莓',
  userAccount: 'lanMei',
  avatarUrl: 'src/img/yln.jpg',
  profile: '你好，我叫蓝莓果酱',
  gender: '男',
  phone: '123456',
  email: '123456@qq.com',
  planetCode: '1',
  tags:['java','伊雷娜','python','学习好累'],
  createTime: new Date(),
}



</script>

<style scoped>

</style>