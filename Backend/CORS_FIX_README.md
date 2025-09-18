# Hướng dẫn fix lỗi CORS

## Vấn đề
Lỗi CORS khi frontend Angular (port 4200) gọi API đến backend Spring Boot (port 8080).

## Giải pháp đã áp dụng

### 1. Cập nhật SecurityConfig.java
- Thêm `@EnableWebSecurity`
- Cấu hình `SecurityFilterChain` với:
  - Disable CSRF
  - Enable CORS
  - Permit all cho `/api/**` endpoints
  - Stateless session management

### 2. Cấu hình CORS
- Allow origin: `http://localhost:4200`
- Allow methods: GET, POST, PUT, DELETE, OPTIONS
- Allow headers: *
- Allow credentials: true

### 3. Cập nhật AuthService
- Thêm error handling tốt hơn
- Xử lý các loại lỗi khác nhau (network, server, client)

## Cách test

### 1. Restart Backend
```bash
cd Backend
mvn clean compile
mvn spring-boot:run
```

### 2. Restart Frontend
```bash
cd Frontend
ng serve
```

### 3. Test API
- Truy cập: `http://localhost:4200/login`
- Thử đăng ký tài khoản mới
- Thử đăng nhập

## Kiểm tra logs
- Backend logs sẽ hiển thị CORS headers
- Frontend console sẽ không còn lỗi CORS

## Nếu vẫn có lỗi
1. Kiểm tra backend có chạy trên port 8080
2. Kiểm tra frontend có chạy trên port 4200
3. Xem browser developer tools Network tab
4. Kiểm tra backend logs
