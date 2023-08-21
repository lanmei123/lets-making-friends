<template>
  <div id="teamAddPage">
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="addTeamData.name"
            name="name"
            label="队伍名"
            placeholder="请输入更新的队伍名"
            :rules="[{ required: true, message: '请输入更新的队伍名' }]"
        />
        <van-field
            v-model="addTeamData.description"
            rows="4"
            autosize
            label="队伍描述"
            type="textarea"
            placeholder="请输入队伍描述"
        />
        <van-field
            is-link
            readonly
            name="calendar"
            label="截止时间"
            :placeholder="formatDate"
            @click="showCalendar = true"
        />
        <van-calendar
            v-model:show="showCalendar"
            @confirm="onConfirm"
            title="请选择过期时间"/>

        <van-field name="stepper" label="最大人数">
          <template #input>
            <van-stepper v-model="addTeamData.maxNum" max="10" min="3"/>
          </template>
        </van-field>
        <van-field name="radio" label="队伍状态">
          <template #input>
            <van-radio-group v-model="addTeamData.status" direction="horizontal">
              <van-radio name="0">公开</van-radio>
              <van-radio name="1">私有</van-radio>
              <van-radio name="2">加密</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
            v-if="Number(addTeamData.status) === 2"
            v-model="addTeamData.password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入队伍密码"
            :rules="[{ required: true, message: '请填写密码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {showFailToast, showSuccessToast} from "vant";
import myAxios from "../../plugins/myAxios.js";
import {TeamType} from "../models/team";

const router = useRouter();
const route = useRoute();

const id = route.query.id;
//展示时期选择
const showCalendar = ref(false);
const addTeamData = ref({});

// 获取之前的用户信息
onMounted(async () =>{
  if (id<=0){
    showFailToast("加载队伍失败")
    return;
  }
  const res = await myAxios.get("/team/get",{
    params:{
      id
    }
  });
  console.log(res)
  if (res?.data.code === 0){
    addTeamData.value = res.data.data;
  }else {
    showFailToast("加载队伍失败，请重试");
  }
})

// const initFormData = {
//   "name": "",
//   "description": "",
//   "expireTime": "",
//   "maxNum": 0,
//   "password": "",
//   "status": 0,
//   "userId": 0
// }

// 需要用户填写的表单数据

let formatDate;

const onConfirm = (date) => {
  formatDate = `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
  date.setHours(23);
  date.setMinutes(59);
  date.setSeconds(59);
  addTeamData.value.expireTime = date;
  showCalendar.value = false;
};

const onSubmit = async () =>{

  const postData = {
    ...addTeamData.value,
    status: Number(addTeamData.value.status)
  }
  // todo 前端参数校验
  const res = await myAxios.post("/team/update",postData);
  if (res.data.code === 0 && res.data){
    showSuccessToast('更新成功');
    await router.push({
      path: '/team',
      replace: true,
    });
  }else {
    showFailToast("更新失败");
  }
}

</script>

<style scoped>

</style>