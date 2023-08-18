<template>
  <van-form @submit="onSubmit">
      <van-field
          v-model="editUser.currentValue"
          :name="editUser.editKey"
          :label="editUser.editName"
          :placeholder="'请输入${editUser.editName}'"
      />
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>
</template>

<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {ref} from "vue";
import myAxios from "../../plugins/myAxios";
import {showFailToast, showSuccessToast, showToast} from "vant";
import {getCurrentUser} from "../services/user";
import {onMounted} from "vue/dist/vue";

const route = useRoute()
const router = useRouter()
const editUser = ref({
  editKey: route.query.editKey,
  currentValue: route.query.currentValue,
  editName: route.query.editName
})


const onSubmit = async () => {
  const currentUser = await getCurrentUser();

  if (!currentUser){
    showToast("用户未登陆")
    return;
  }
  const res = await myAxios.post('user/update',{
    'id': currentUser.data.id,
    [editUser.value.editKey]: editUser.value.currentValue,
  })
  if (res.data.code == 0 && res.data){
    showSuccessToast('修改成功');
    router.back();
  }else {
    showFailToast('修改失败');
  }
};
</script>

<style scoped>

</style>