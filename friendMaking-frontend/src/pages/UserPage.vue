<template>
  <template v-if="user">
    <van-cell title="当前用户" :value="user?.username"/>
    <van-cell title="修改信息" is-link to="/user/update"/>
    <van-cell title="我创建的队伍" is-link to="/user/team/join"/>
    <van-cell title="我加入的队伍" is-link to="/user/team/create"/>
  </template>
</template>

<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../../plugins/myAxios";
import {showFailToast, showSuccessToast} from "vant";
import {getCurrentUser} from "../services/user";

// const user = {
//   id: 1,
//   username: '蓝莓',
//   userAccount: 'lanMei',
//   avatarUrl: 'src/img/yln.jpg',
//   gender: '男',
//   phone: '123456',
//   email: '123456@qq.com',
//   planetCode: '1',
//   createTime: new Date(),
// }
const user = ref();
onMounted(async () =>{
  const res= await getCurrentUser();
  user.value = res.data;
})


</script>

<style scoped>

</style>