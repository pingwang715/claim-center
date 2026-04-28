import axios from "axios";
import type { AxiosInstance } from "axios";

const baseURL = import.meta.env.VITE_API_BASE_URL;
if (!baseURL) throw new Error("VITE_API_BASE_URL is not defined");

const apiClient: AxiosInstance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  timeout: 10000,
  withCredentials: true,
});

export default apiClient;
