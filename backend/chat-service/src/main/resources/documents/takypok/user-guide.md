# Hướng dẫn sử dụng hệ thống TAKYPOK

Tài liệu này mô tả các tính năng chính của ứng dụng TAKYPOK (nền tảng quản lý ticket, giao tiếp nội bộ và nhân sự) dành cho người dùng cuối. Dùng tài liệu này để trả lời các câu hỏi hỗ trợ của người dùng về cách sử dụng ứng dụng.

## Đăng nhập và tài khoản

![Màn hình đăng nhập — Sign in to your workspace](http://localhost:8080/media-service/images/e6b22d85-40f7-40fa-a99e-b631b3f96bc1.jpg)

Người dùng đăng nhập bằng tài khoản thư mục nội bộ (LDAP) thông qua màn hình **Sign in to your workspace**. Có hai lựa chọn:

- **Continue with SSO**: đăng nhập bằng tài khoản LDAP hiện có, thông qua luồng OAuth2 Authorization Code kèm PKCE do `auth-service` cung cấp. Nếu `auth-service` đang gặp sự cố (không "UP"), nút này sẽ bị vô hiệu hóa tạm thời.
- **Request access**: dùng khi chưa có tài khoản, mở form đăng ký để yêu cầu cấp quyền truy cập.

Sau khi xác thực LDAP thành công lần đầu, hệ thống tự động tạo (provision) tài khoản tương ứng. Phiên đăng nhập được mã hóa ("Session encrypted").

## Điều hướng chung

Sau khi đăng nhập, thanh điều hướng bên trái gồm các mục:

- **Home** — trang tổng quan (dashboard tổng hợp).
- **Dashboard** — màn hình quản lý hàng đợi ticket chi tiết.
- **Messages** — nhắn tin nội bộ.
- **My Team's Leave** — duyệt đơn nghỉ phép của nhóm (dành cho quản lý).
- **Settings** — cấu hình cá nhân và cấu hình quy trình làm việc.

Phía trên cùng còn có: nút **Create** để tạo ticket nhanh, chuông thông báo, công tắc chuyển giao diện sáng/tối, thanh trạng thái hệ thống (hiển thị tình trạng UP/DOWN của từng dịch vụ backend: auth, media, chat, workflow), và menu người dùng — menu này chỉ hiển thị mục **Admin Portal** nếu tài khoản có quyền quản trị.

## Trang chủ (Home)

![Trang chủ Home — tổng quan vận hành](http://localhost:8080/media-service/images/2fc3f651-c7e5-42c6-b05d-b2f0968503e1.jpg)

Trang Home hiển thị các chỉ số vận hành theo thời gian thực (tự cập nhật qua kết nối trực tiếp khi có sự kiện SLA mới):

- **Total Queue**: tổng số ticket đang mở.
- **Critical Now**: số ticket đang mở ở mức ưu tiên cao nhất hiện có, kèm thời hạn phản hồi (response time) theo SLA của mức đó.
- **SLA Breached**: tổng số lần vi phạm SLA trong kỳ (gồm cả vi phạm thời hạn phản hồi lẫn thời hạn xử lý).
- **Compliance**: tỷ lệ tuân thủ SLA.

Ngoài ra còn có biểu đồ tình trạng ứng dụng (Application Health), biểu đồ tuân thủ SLA, bảng phân bổ ticket theo trạng thái/loại/dự án, thời gian xử lý trung bình, và nhật ký hoạt động trực tiếp (Live Activity Feed).

## Quản lý Ticket (Dashboard)

![Màn hình Dashboard — hàng đợi ticket dạng bảng (Console)](http://localhost:8080/media-service/images/d31ecbef-694d-4fd3-9c5f-9b5024bf3336.jpg)

Đây là màn hình chính để xử lý công việc. Có hai kiểu hiển thị: dạng bảng (**Console**) hoặc dạng bảng Kanban kéo-thả (**Board**).

**Tìm kiếm và lọc**: theo nội dung tóm tắt, trạng thái, mức ưu tiên, người được giao (có gợi ý tự động). Có thể sắp xếp theo tỷ lệ hoàn thành và xuất danh sách ra CSV.

**Tạo ticket mới**: nhấn nút Create, chọn Dự án (Project) và Loại công việc (Issue Type) — mỗi dự án có bộ loại công việc riêng — sau đó nhập mức ưu tiên, tóm tắt, mô tả (định dạng rich-text), tệp đính kèm và các ticket liên quan. Dự án "Internal Application" (mã IA) có thêm các trường riêng: ứng dụng liên quan, phòng ban, khu vực, vị trí, số điện thoại.

**Chi tiết ticket**: nhấn vào một dòng để mở ngăn xem nhanh (Inspector), hoặc mở trang chi tiết đầy đủ. Tại đây có thể:

- Chuyển trạng thái theo quy trình (workflow) đã cấu hình cho loại ticket đó.
- Tạm dừng SLA (Pause SLA) với lý do cụ thể: đang chờ người dùng phản hồi, đang chờ bên thứ ba, đang chờ thay đổi (change), đang chờ vật tư/mua sắm, hoặc đang bảo trì theo lịch — và tiếp tục tính SLA (Resume SLA) khi lý do chờ đã kết thúc.
- Thêm bình luận, đính kèm tệp.
- Liên kết ticket khác: có liên quan (Related), ticket gây ra sự cố này (Caused By), hoặc ticket trùng lặp (Duplicates).
- Xem nhật ký thay đổi (audit log): ai tạo ticket, đổi trạng thái, đổi người xử lý, tạm dừng/tiếp tục SLA, kèm thời điểm.

**Trạng thái ticket**: mỗi ticket đi qua các trạng thái theo quy trình (workflow) được cấu hình, thường theo nhóm: Cần làm (To Do) → Đang xử lý (In Progress) → Đã xử lý xong (Resolved), ngoài ra còn có các trạng thái tạm dừng chờ (Pending), đóng (Closed), hoặc mở lại (Reopen).

**Mức ưu tiên và SLA**: mỗi mức ưu tiên quy định thời hạn phản hồi (response time) và thời hạn xử lý (resolution time) tính theo giờ, cùng một cấp độ (level) dùng để so sánh mức độ khẩn cấp giữa các ưu tiên. SLA của mỗi ticket tính theo giờ làm việc đã cấu hình (múi giờ, giờ làm việc, giờ nghỉ trưa, ngày nghỉ cuối tuần) và có thể bị tạm dừng khi ticket đang chờ một điều kiện bên ngoài.

## Nhắn tin (Messages)

![Màn hình Messages — trò chuyện trực tiếp](http://localhost:8080/media-service/images/2235ae23-c0aa-4b8b-9e39-826709d0b715.jpg)

Tính năng chat nội bộ hỗ trợ:

- Trò chuyện riêng (Direct) hoặc theo nhóm (Group); có thể đổi tên nhóm, thêm/xóa thành viên.
- Kênh công khai (Public Channel) mà người dùng có thể tự tham gia, và kênh riêng tư/nhóm chỉ vào theo lời mời.
- Gửi tin nhắn dạng văn bản, hình ảnh, video hoặc kết hợp; tệp đính kèm hình/video sẽ được xử lý (trạng thái: đang xử lý / sẵn sàng / lỗi) trước khi hiển thị đầy đủ.
- Thả biểu cảm (reaction) vào tin nhắn, xem danh sách người đã thả biểu cảm.
- Trả lời theo luồng (thread reply) cho một tin nhắn cụ thể.
- Tìm kiếm tin nhắn theo nội dung trong một cuộc trò chuyện.
- Trạng thái online/lần hoạt động gần nhất của người dùng khác, chỉ báo đang gõ (typing indicator).
- Đánh dấu đã đọc, hiển thị mốc tin nhắn đối phương đã đọc/đã nhận.

## Nghỉ phép của nhóm (My Team's Leave)

![Màn hình My Team's Leave — danh sách đơn nghỉ phép](http://localhost:8080/media-service/images/d14cd844-2ea3-4d42-a0da-0a59770cf889.jpg)

Đơn nghỉ phép trong hệ thống thực chất là một loại ticket đặc biệt (thuộc dự án nội bộ dành cho nhân sự), không phải một màn hình riêng biệt về mặt dữ liệu. Màn hình "My Team's Leave" dành cho quản lý để:

- Xem danh sách đơn nghỉ phép của các nhân viên **trực thuộc quyền quản lý trực tiếp** của mình (không thấy đơn của nhân viên ngoài nhóm).
- Lọc theo trạng thái (đang chờ duyệt / đã duyệt / đã từ chối) và tìm theo tên nhân viên.
- **Approve** (duyệt) hoặc **Reject** (từ chối, bắt buộc nhập lý do từ chối).

Mỗi đơn nghỉ phép gồm: loại nghỉ phép (phép năm, ốm, việc riêng, không lương, thai sản), ngày bắt đầu/kết thúc, số ngày nghỉ, lý do, và tệp đính kèm (nếu có, ví dụ giấy khám bệnh).

## Cài đặt (Settings)

![Màn hình Settings — hồ sơ, giao diện và cấu hình workflow](http://localhost:8080/media-service/images/bb9705d7-31e2-4804-98f2-d7f07f86db20.jpg)

Ở cấp người dùng: đổi ảnh đại diện (có tự động cắt/resize), chuyển giao diện sáng/tối.

Ngoài ra Settings còn có các tab cấu hình quy trình làm việc dùng chung cho cả hệ thống: Workflows (sơ đồ và luồng chuyển trạng thái), Statuses (danh sách trạng thái), Priorities (mức ưu tiên), Projects (dự án), Issue Types (loại công việc), và Team Org (cơ cấu đội hỗ trợ).

## Cổng quản trị (Admin Portal)

![Màn hình Admin Portal — Dashboard quản trị](http://localhost:8080/media-service/images/da89bd8c-3e62-4f07-9b58-903175da997b.jpg)

Chỉ hiển thị và truy cập được với tài khoản có quyền quản trị. Các mục đã kết nối dữ liệu thực tế gồm:

- **Employee Directory** (Danh bạ nhân viên): tìm kiếm/lọc theo phòng ban, trạng thái làm việc. Có thể chỉnh sửa thông tin công việc (chức danh, phòng ban/đơn vị, địa điểm, ngày vào làm, ca làm, quản lý trực tiếp) và thông tin cá nhân (số điện thoại, ngày sinh, giới tính, số CMND/CCCD, địa chỉ, người liên hệ khẩn cấp), cũng như đổi trạng thái làm việc (Đang làm việc / Đang nghỉ phép / Tạm đình chỉ / Đã nghỉ việc). Có thể thêm nhân viên mới.
- **Org Structure** (Cơ cấu tổ chức): thêm/sửa/xóa phòng ban và đơn vị (tên, trưởng đơn vị, địa điểm), xem số lượng nhân sự theo từng phòng ban/đơn vị.
- Quản lý Thông báo/Tin tức nội bộ, Tài liệu & Chính sách, Tài liệu đào tạo — đã kết nối dữ liệu thực tế.

Một số mục khác trong Admin Portal (Dashboard quản trị, Yêu cầu tăng ca, Quản lý khảo sát, IT Helpdesk, Thực đơn căng tin, Lịch ca làm việc, Chấm công, Quản lý KPI sản xuất) hiện đang hiển thị **dữ liệu minh họa** và chưa kết nối với dữ liệu vận hành thực tế — nếu người dùng hỏi về các mục này, cần lưu ý đây là tính năng đang phát triển, số liệu hiển thị chỉ mang tính minh họa.

## Ứng dụng di động

Có ứng dụng di động riêng (Android/iOS) với các màn hình: đăng nhập, trang chủ (có banner có thể vuốt chuyển), tin tức, nhắn tin (đầy đủ tính năng như bản web: danh sách chat, luồng trả lời, thành viên nhóm, thả biểu cảm), và menu Hồ sơ cá nhân với các mục: sơ đồ tổ chức, nghỉ phép, lịch làm việc, phiếu lương, đánh giá hiệu suất, an toàn lao động, căng tin, tăng ca. Mục "Thêm" (More) có: Helpdesk, đào tạo, chính sách, khảo sát, cài đặt, và trình xem tài liệu PDF ngay trong ứng dụng.

## Thông báo

Hệ thống gửi thông báo đẩy (push notification) trên cả web và di động qua Firebase Cloud Messaging, hiển thị qua biểu tượng chuông trên thanh điều hướng.

## Trợ lý AI (AI Assistant)

Góc dưới bên phải màn hình có biểu tượng trợ lý AI hình robot — có thể kéo thả để đổi vị trí, nhấn để mở/đóng khung chat. Trợ lý hỗ trợ nhiều cuộc trò chuyện riêng biệt (giống danh sách hội thoại của ChatGPT):

- Nhấn dấu **+** để bắt đầu cuộc trò chuyện mới.
- Nhấn biểu tượng lịch sử để xem/chuyển giữa các cuộc trò chuyện trước đó, hoặc xóa một cuộc trò chuyện.
- Cuộc trò chuyện đang mở được ghi nhớ ngay cả khi tải lại trang.
- Trợ lý có khả năng nhớ ngữ cảnh trong cùng một cuộc trò chuyện, nên có thể hỏi tiếp các câu liên quan đến câu hỏi trước đó.

## Câu hỏi thường gặp

**Vì sao tôi không đăng nhập được?** Kiểm tra thanh trạng thái hệ thống ở đầu trang — nếu auth-service đang DOWN, nút đăng nhập sẽ bị khóa tạm thời, vui lòng thử lại sau ít phút. Nếu chưa có tài khoản, dùng "Request access".

**Vì sao SLA của ticket bị tạm dừng?** Ticket có thể đang ở trạng thái chờ (Pending) với một trong các lý do: chờ người dùng phản hồi, chờ bên thứ ba, chờ thay đổi, chờ vật tư/mua sắm, hoặc đang trong thời gian bảo trì theo lịch. Đồng hồ SLA sẽ chạy lại khi ticket được chuyển tiếp (Resume).

**Tôi có thể xem đơn nghỉ phép của nhân viên không thuộc nhóm mình không?** Không, màn hình "My Team's Leave" chỉ hiển thị đơn của nhân viên trực thuộc quyền quản lý trực tiếp của tài khoản đang đăng nhập.

**Làm sao để biết ai đã đọc tin nhắn của tôi?** Trong cuộc trò chuyện, hệ thống hiển thị mốc tin nhắn cuối cùng mà người nhận đã đọc/đã nhận.
