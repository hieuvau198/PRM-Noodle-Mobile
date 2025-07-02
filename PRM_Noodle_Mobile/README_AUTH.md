# Hệ thống Đăng nhập và Đăng ký - PRM Noodle Mobile

## Tổng quan
Đã thêm hệ thống đăng nhập và đăng ký hoàn chỉnh cho ứng dụng PRM Noodle Mobile với các tính năng:

### Tính năng chính:
1. **Splash Screen** - Hiển thị logo app với animation đẹp
2. **Đăng nhập** - Giao diện đăng nhập hiện đại với validation
3. **Đăng ký** - Form đăng ký tài khoản mới
4. **Lưu trạng thái đăng nhập** - Sử dụng SharedPreferences
5. **Đăng xuất** - Menu option để đăng xuất

## Cấu trúc file đã thêm:

### Java Classes:
- `SplashActivity.java` - Màn hình khởi động với logo
- `auth/LoginActivity.java` - Màn hình đăng nhập
- `auth/RegisterActivity.java` - Màn hình đăng ký
- Cập nhật `MainActivity.java` - Thêm chức năng đăng xuất

### Layout Files:
- `activity_splash.xml` - Layout splash screen
- `activity_login.xml` - Layout đăng nhập
- `activity_register.xml` - Layout đăng ký

### Animation Files:
- `anim/fade_in_scale.xml` - Animation cho logo
- `anim/slide_up.xml` - Animation cho text

### Menu & Icons:
- `menu/main_menu.xml` - Menu với option đăng xuất
- `drawable/ic_logout.xml` - Icon đăng xuất

### Themes:
- Cập nhật `themes.xml` với theme NoActionBar

## Cách hoạt động:

### 1. Khởi động ứng dụng:
- App bắt đầu với `SplashActivity`
- Hiển thị logo với animation trong 3 giây
- Kiểm tra trạng thái đăng nhập trong SharedPreferences

### 2. Đăng nhập:
- Nếu chưa đăng nhập → Chuyển đến `LoginActivity`
- Validate email và mật khẩu
- Lưu trạng thái đăng nhập vào SharedPreferences
- Chuyển đến `MainActivity` (giao diện chính)

### 3. Đăng ký:
- Từ màn hình đăng nhập → Chuyển đến `RegisterActivity`
- Validate đầy đủ thông tin: họ tên, email, số điện thoại, mật khẩu
- Sau khi đăng ký thành công → Quay lại màn hình đăng nhập

### 4. Sử dụng ứng dụng:
- Sau khi đăng nhập → Vào giao diện chính với bottom navigation
- Có thể đăng xuất từ menu (3 chấm) ở góc trên bên phải

### 5. Đăng xuất:
- Xóa dữ liệu đăng nhập trong SharedPreferences
- Chuyển về màn hình đăng nhập

## Validation Rules:

### Đăng nhập:
- Email phải đúng định dạng
- Mật khẩu tối thiểu 6 ký tự
- Demo: chấp nhận bất kỳ email hợp lệ nào

### Đăng ký:
- Họ tên: bắt buộc
- Email: đúng định dạng
- Số điện thoại: tối thiểu 10 số
- Mật khẩu: tối thiểu 6 ký tự
- Xác nhận mật khẩu: phải khớp

## Lưu ý:
- Hiện tại sử dụng demo data, cần tích hợp với API thực tế
- SharedPreferences được sử dụng để lưu trạng thái đăng nhập
- Tất cả giao diện đều responsive và hỗ trợ scroll
- Animation mượt mà và chuyên nghiệp

## Để tích hợp API thực:
1. Thay thế logic demo trong `performLogin()` và `performRegister()`
2. Thêm API calls sử dụng Retrofit đã có sẵn
3. Xử lý response từ server
4. Thêm error handling cho các trường hợp lỗi mạng 