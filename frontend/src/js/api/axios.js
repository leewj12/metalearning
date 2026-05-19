import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://localhost:8091", // 백엔드 서버 주소
  withCredentials: true, // 쿠키 포함
});

const getCsrfToken = () => {
  const cookies = document.cookie.split("; ");
  const csrfCookie = cookies.find((cookie) => cookie.startsWith("XSRF-TOKEN="));
  return csrfCookie ? csrfCookie.split("=")[1] : null;
};

// 요청 인터셉터 설정
apiClient.interceptors.request.use(
  (config) => {
    const csrfToken = getCsrfToken(); // CSRF 토큰 가져오기
    console.log(csrfToken)
    if (csrfToken) {
      config.headers["X-XSRF-TOKEN"] = csrfToken; // 요청 헤더에 추가
    }
    console.log(config);
    return config;
  },
  (error) => {
    return Promise.reject(error); // 요청 오류 처리
  }
);



export default apiClient;