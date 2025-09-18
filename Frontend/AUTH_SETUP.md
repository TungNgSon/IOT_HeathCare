# Hướng dẫn sử dụng Authentication

## Tổng quan
Đã tạo thành công 2 trang đăng nhập và đăng ký cho ứng dụng IoT Healthcare với UI giống hệt với thiết kế được cung cấp.

## Các tính năng đã implement

### 1. AuthService (`src/app/services/auth.service.ts`)
- Gọi API đăng nhập và đăng ký
- Quản lý token trong localStorage
- Kiểm tra trạng thái đăng nhập

### 2. LoginComponent (`src/app/components/login/`)
- UI giống hệt ảnh đăng nhập
- Form validation
- Toggle hiển thị password
- Remember me checkbox
- Error handling
- Loading state

### 3. RegisterComponent (`src/app/components/register/`)
- UI giống hệt ảnh đăng ký
- Form validation với confirm password
- Toggle hiển thị password cho cả 2 field
- Kiểm tra password match
- Error handling
- Loading state

### 4. Routing
- `/login` - Trang đăng nhập
- `/register` - Trang đăng ký
- Mặc định redirect đến `/login`

## Cách sử dụng

### 1. Chạy ứng dụng
```bash
cd Frontend
npm install
ng serve
```

### 2. Truy cập
- Đăng nhập: `http://localhost:4200/login`
- Đăng ký: `http://localhost:4200/register`

### 3. API Endpoints
Đảm bảo backend đang chạy trên `http://localhost:8080` với các endpoints:
- `POST /api/users/login`
- `POST /api/users/register`

## Cấu trúc dữ liệu

### Login Request
```typescript
{
  username: string;
  password: string;
}
```

### Register Request
```typescript
{
  username: string;
  password: string;
  confirmPassword: string;
}
```

### Auth Response
```typescript
{
  token: string;
  username: string;
}
```

## Tính năng UI

### Login Page
- Logo IoT Healthcare với icon trái tim
- Form đăng nhập với username/password
- Toggle hiển thị password
- Remember me checkbox
- Forgot password link
- Sign up link
- Security information footer

### Register Page
- Logo IoT Healthcare với icon trái tim
- Form đăng ký với username/password/confirm password
- Toggle hiển thị password cho cả 2 field
- Validation password match
- Security information footer

## Responsive Design
- Tối ưu cho mobile và desktop
- Breakpoint tại 480px

## Security Features
- 256-bit SSL encryption (hiển thị)
- Secure healthcare data management
- Token-based authentication
