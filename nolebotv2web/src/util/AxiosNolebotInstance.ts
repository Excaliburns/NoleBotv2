import axios from "axios";

const axiosNolebotInstance = axios.create({
    baseURL: 'http://localhost:8080/',
    withCredentials: true,
    timeout: 1000
})

const axiosDiscordInstance = axios.create({
    baseURL: 'https://discord.com/api/',
    timeout: 1000
})

export { axiosNolebotInstance, axiosDiscordInstance};