import axios from "axios";

const fileAPI = axios.create({
  baseURL: "http://localhost:8082/api",
});

fileAPI.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default fileAPI;