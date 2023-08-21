<template>
  <van-card
      v-for="team in props.teamList"
      :desc="team.description"
      :thumb="maomao"
      :title="team.name"
  >
    <template #tags>
      <van-tag plain type="danger"  style="margin-left:8px; margin-top: 8px" >
        {{teamStatusEnum[team.status] }}
      </van-tag>
    </template>
    <template #bottom>
      <div>
        {{'最大人数: ' + team.maxNum}}
      </div>
      <div v-if="team.expireTime" >
        {{'过期时间: ' + formattedDate(team.expireTime)}}
      </div>
      <div v-if="team.createTime">
        {{'发布时间: ' + formattedDate(team.createTime)}}
      </div>
    </template>
    <template #footer>
      <van-button v-if="team.createUser.id === currentUser?.data.id" size="small" type="success" plain
                  @click="doUpdateTeam(team.id)">解散队伍</van-button>
      <van-button v-if="team.createUser.id === currentUser?.data.id" size="small" type="success" plain
                  @click="doUpdateTeam(team.id)">更新队伍</van-button>
      <van-button v-if="team.createUser.id === currentUser?.data.id" size="small" type="success" plain
                  @click="doUpdateTeam(team.id)">退出队伍</van-button>
      <van-button size="small" type="primary" plain @click="doJoinTeam(team.id)">加入队伍</van-button>
    </template>
  </van-card>
</template>

<script setup lang="ts">
import {defineProps, onMounted, ref} from "vue";
import {TeamType} from "../models/team";
import {teamStatusEnum} from "../constants/team";
import maomao from "../img/maomao.jpg"
import myAxios from "../../plugins/myAxios";
import {showFailToast, showSuccessToast} from "vant";
import {getCurrentUser} from "../services/user";
import {useRouter} from "vue-router";
interface TeamCardListProps{
  teamList: TeamType[];
}

const router = useRouter()
const props = defineProps<TeamCardListProps>();

const formattedDate = (timestamp: number) => {
  const date = new Date(timestamp);
  return date.toLocaleDateString()
};

const currentUser = ref();

onMounted(async () =>{
  currentUser.value = await getCurrentUser();
})
/**
 * 加入队伍
 */
const doJoinTeam = async (id:number) =>{
  const res = await myAxios.post('/team/join',{
    teamId: id
  })
  if (res?.data.code === 0){
    showSuccessToast('加入成功');
  }else {
    showFailToast('加入失败' + (res.data.description ? `，${res.data.description}` : ''));
  }
}
const doUpdateTeam = (id: number) =>{
  router.push({
    path:'/team/update',
    query:{
      id,
    }
  })
}
</script>


<style scoped>

</style>