import axios from "axios";

const myAxios = axios.create({
    baseURL: 'http://localhost:8080/api'
})
myAxios.defaults.withCredentials = true;
export default myAxios;