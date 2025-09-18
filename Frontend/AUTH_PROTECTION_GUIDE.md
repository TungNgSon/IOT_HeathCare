# Hướng dẫn Test Auth Protection

## Tính năng đã implement

### 1. AuthGuard
- ✅ Bảo vệ các routes yêu cầu đăng nhập
- ✅ Tự động redirect đến `/login` nếu chưa đăng nhập

### 2. GuestGuard  
- ✅ Bảo vệ trang login/register
- ✅ Tự động redirect đến `/dashboard` nếu đã đăng nhập

### 3. Header Component
- ✅ Hiển thị thông tin user đã đăng nhập
- ✅ Nút logout để đăng xuất

### 4. Route Protection
- ✅ Tất cả routes được bảo vệ bằng guards
- ✅ Navigation chỉ hiển thị khi đã đăng nhập

## Cách test

### 1. Test khi chưa đăng nhập
```bash
# Start frontend
ng serve

# Truy cập các URL sau (sẽ redirect đến /login):
http://localhost:4200/dashboard
http://localhost:4200/sensors  
http://localhost:4200/device-actions
http://localhost:4200/profile
http://localhost:4200/home
```

### 2. Test đăng nhập
1. Truy cập: `http://localhost:4200/login`
2. Nhập username/password
3. Sau khi đăng nhập thành công → redirect đến `/dashboard`
4. Kiểm tra header hiển thị username và nút logout

### 3. Test đăng xuất
1. Click nút "Logout" trong header
2. Kiểm tra redirect đến `/login`
3. Thử truy cập `/dashboard` → sẽ redirect về `/login`

### 4. Test GuestGuard
1. Khi đã đăng nhập, truy cập `/login` hoặc `/register`
2. Sẽ tự động redirect đến `/dashboard`

## Routes được bảo vệ

### Protected Routes (cần đăng nhập)
- `/dashboard` - Dashboard chính
- `/home` - Trang home
- `/sensors` - Danh sách sensors
- `/device-actions` - Device actions
- `/profile` - Profile user

### Guest Routes (chỉ cho guest)
- `/login` - Trang đăng nhập
- `/register` - Trang đăng ký

## UI Changes

### Header
- Hiển thị logo IoT Healthcare
- Welcome message với username
- Nút logout với icon

### Navigation
- Chỉ hiển thị khi đã đăng nhập
- Ẩn hoàn toàn khi chưa đăng nhập

## Security Features

1. **Route Protection**: Không thể truy cập protected routes khi chưa đăng nhập
2. **Auto Redirect**: Tự động chuyển hướng phù hợp
3. **Session Management**: Lưu trữ user info trong localStorage
4. **Logout Functionality**: Xóa session và redirect về login

## Troubleshooting

### Nếu không redirect đúng:
1. Kiểm tra browser console có lỗi gì không
2. Kiểm tra localStorage có userInfo và token không
3. Restart frontend: `ng serve`

### Nếu header không hiển thị:
1. Kiểm tra AuthService.isLoggedIn() trả về true
2. Kiểm tra getUserInfo() có data không
3. Kiểm tra app.component.ts có import AuthService không
