# AMS Dashboard — Combined Knowledge Ba

## Table of Contents

1. [Allocation validation 1 (Rule validate PAKD-Project).pptx](#1-allocation-validation-1--rule-validate-pakd-project--pptx)
2. [CMCG_AMS_Dashboard-System_Architecture_v0.2.docx](#2-cmcg-ams-dashboard-system-architecture-v0-2-docx)
3. [CMCG_AMS_Dashboard_Matrix_permission.xlsx](#3-cmcg-ams-dashboard-matrix-permission-xlsx)
4. [CMCG_AMS_Dashboard_Projects_User guide v1.0.docx](#4-cmcg-ams-dashboard-projects-user-guide-v1-0-docx)
5. [CMCG_AMS_Dashboard_Slide Training_Module KPI.pptx](#5-cmcg-ams-dashboard-slide-training-module-kpi-pptx)
6. [CMCG_AMS_Dashboard_User_Guide_QTMDDA_v202082023.pptx](#6-cmcg-ams-dashboard-user-guide-qtmdda-v202082023-pptx)
7. [CMCG_AMS_TMS - Dashboard 051022.pptx](#7-cmcg-ams-tms---dashboard-051022-pptx)
8. [CMCG_AMS_[Dashboard] User Guide Menubar.docx](#8-cmcg-ams--dashboard--user-guide-menubar-docx)
9. [Close project request (Đóng dự án).pptx](#9-close-project-request--đóng-dự-án--pptx)
10. [Phase 11 Tích hợp C-Code-Gitlab, Project Overview.pptx](#10-phase-11-tích-hợp-c-code-gitlab--project-overview-pptx)
11. [Phase 15 Remove Member 1.pptx](#11-phase-15-remove-member-1-pptx)
12. [Phase 6_ Phương án sản xuất.pptx](#12-phase-6--phương-án-sản-xuất-pptx)
13. [Runbook.xlsx](#13-runbook-xlsx)
14. [Ràng buộc edit, delete CI và Relationship.xlsx](#14-ràng-buộc-edit--delete-ci-và-relationship-xlsx)
15. [TDX_Dashboard_Phase11_FI_25052025.xlsx](#15-tdx-dashboard-phase11-fi-25052025-xlsx)
16. [Tài liệu học tập_Billing Plan 1.pdf](#16-tài-liệu-học-tập-billing-plan-1-pdf)
17. [Tài liệu vận hành Dashboard_01102024.docx.xlsx](#17-tài-liệu-vận-hành-dashboard-01102024-docx-xlsx)
18. [Dashboard - Menubar_User guide_V1.0.docx](#18-dashboard---menubar-user-guide-v1-0-docx) — _User Guide/_
19. [Dashboard - TMS_User guide_V1.0.pptx](#19-dashboard---tms-user-guide-v1-0-pptx) — _User Guide/_
20. [Dashboard_Module Business plan_User guide_V1.0.pptx](#20-dashboard-module-business-plan-user-guide-v1-0-pptx) — _User Guide/_
21. [Dashboard_Module KPI_User guide_V1.0.pptx](#21-dashboard-module-kpi-user-guide-v1-0-pptx) — _User Guide/_
22. [Dashboard_Module Open&Update project_User guide_V1.0.pptx](#22-dashboard-module-open-update-project-user-guide-v1-0-pptx) — _User Guide/_
23. [Dashboard_Projects_User guide_V1.0.docx](#23-dashboard-projects-user-guide-v1-0-docx) — _User Guide/_
24. [Dashboard_QTMDDA_User guide_V1.0.pptx](#24-dashboard-qtmdda-user-guide-v1-0-pptx) — _User Guide/_
25. [Dashboard_User Guide_Module KPI's Operation.pptx](#25-dashboard-user-guide-module-kpi-s-operation-pptx) — _User Guide/_
26. [[Dashboard Phase 11] C-codex & GIT-v1-20250526_155505.pdf](#26--dashboard-phase-11--c-codex---git-v1-20250526-155505-pdf)
27. [[Dashboard phase 6] PAKD-v1-20250625_154028.pdf](#27--dashboard-phase-6--pakd-v1-20250625-154028-pdf)
28. [[Guide for AMS] Dashboard - OT Validation (1).pptx](#28--guide-for-ams--dashboard---ot-validation--1--pptx)
29. [[Guide] Dashboard - OT Validation.pptx](#29--guide--dashboard---ot-validation-pptx)
30. [[User Guide] P19_ Số Hóa CM Plan (1) 1.pptx](#30--user-guide--p19--số-hóa-cm-plan--1--1-pptx)
31. [[User Guide] P21_ Số Hóa CM Plan (1).pptx](#31--user-guide--p21--số-hóa-cm-plan--1--pptx)
32. [[User Guide] P9_ Business Plan Enhancement.pptx](#32--user-guide--p9--business-plan-enhancement-pptx)
33. [[User Guide] Phase 16 Ranking Member (2).pptx](#33--user-guide--phase-16-ranking-member--2--pptx)
34. [[User Guide] Phase 17 Milestone & Workload chart (1).pptx](#34--user-guide--phase-17-milestone---workload-chart--1--pptx)
---

<a id="1-allocation-validation-1--rule-validate-pakd-project--pptx"></a>
## 1. Allocation validation 1 (Rule validate PAKD-Project).pptx

## Slide 1

Dashboard – Module Allocation validation

SEP 2025

User Guide_ PM/DUL

## Slide 2

Nội dung

02

01

Tổng quan module Allocation validation

Logic validate khi allocate

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE ALLOCATION VALIDATION

1.2. Đối tượng

1.3. Tính năng

## Slide 4

1.1

MỤC ĐÍCH

Mục đích: PM đang allocate cho các member theo kế hoạch sản xuất. Tuy nhiên, việc allocate như vậy có thể không tương ứng với kế hoạch sản xuất trong Phương án kinh doanh. Vì vậy, hệ thống sẽ kiểm tra theo kế hoạch để đảm bảo được sản xuất theo đúng kế hoạch đã đề ra như sau
Số giờ allocate cho từng member phải thoả mãn đồng thời: tổng số MM allocated không vượt quá tổng số MM plan, số MM allocate cho từng member theo tháng không vượt quá remaining MM theo từng tháng
Remaining MM = (MM plan + MM plan * x %) – Current MM allocated

MM plan: số MM được lên kế hoạch trong Phương án kinh doanh
x %: số % được extend theo từng loại project type
Currrent MM allocated: số MM allocate đã được sử dụng

## Slide 5

1.2

ĐỐI TƯỢNG

PM: những user thực hiện hành động allocate cho các member trong dự án
**Khi PM bị chặn allocate -> PM phải thông báo cho AM/DUL/DM revise Business plan
AM: những user tạo Business plan
DUL, GL, FC, BOM, CEO: những user thực hiện phê duyệt Business plan
CDO: user thực hiện setup Allocation validation

## Slide 6

1.3

Tính năng

MM plan = tổng số giờ của tất cả các MVV thuộc dự án đã được lên kế hoạch ở Phương án kinh doanh. 
Cụ thể user có thể thấy trong Business plan > Delivery plan > trường MM effort (đổi sang đơn vị giờ)
Current MM allocated = tổng MM đã allocated của tất cả các MVV thuộc dự án
The remaining hours for allocation = (MM plan + MM plan * x %) – Current MM allocated
x %: số % được extend theo từng loại project type
-> User click <View details> để xem cụ thể thông tin từng mã
***User sẽ không thể allocate cho nhân sự khi The remaining hours là số âm
-> User phải hoàn thiện hết Business plan hoặc thay đổi MM allocated để làm tăng The maining hours

## Slide 7

02

Logic validate khi thực hiện allocate

1. Đối với các dự án không allocate theo kế hoạch
Những dự án không allocate theo kế hoạch -> PM phải xin ngoại lệ và được phê duyệt từ phía CDO để có thể allocate mà không bị chặn theo Business plan
2. Đối với các dự án allocate theo kế hoạch
2.1 Ngày allocate nằm trong khoảng (ngày bắt đầu của MVV + x working days) 
-> Cho phép allocate theo rule hiện tại
2.2 Ngày allocate nằm ngoài khoảng (ngày bắt đầu của MVV + x working days) 
-> User không thể allocate nếu rơi vào các trường hợp sau:
Chưa có bất kỳ Business plan nào đã được approved
Đã tồn tại Business plan với version được approved nhưng số giờ allocate > số giờ The remaining theo từng tháng trong khoảng Intervals mà user chọn
->  User phải tạo Business plan với version mới (MM plan nhiều hơn version cũ, PM thông báo cho AM để revise Business plan)
HOẶC thay đổi MM allocated để làm tăng The maining hours
Note: x working days là 1 ngày do CDO setup (VD: x = 15, x = 20)

---

<a id="2-cmcg-ams-dashboard-system-architecture-v0-2-docx"></a>
## 2. CMCG_AMS_Dashboard-System_Architecture_v0.2.docx

|  | **POA**** ****System ****Architect** |  |
| --- | --- | --- |

			

|  |  |
| --- | --- |

*DASHBOARD*

*SYSTEM A**RCHITECTURE*

**Hanoi, *****12/******19******/20******23***

Table of Contents

1.	High Level Architecture	3

2.	Low-Level Design	4

2.1	Architectural Diagrams	4

2.2	Database	5

3.	Applications (Client Apps)	6

4.	Backend Rest-API	7

5.	Backend Layer	7

6.	External Services	8

7.	Server Topology	8

## High Level Architecture

This platform a simplified architecture with Spring boot and Reactjs 15.

| **No** | **Layer** | **Description** |
| --- | --- | --- |
| 1 | Client App | Bao gồm Web App tương tác với người dùng |
| 2 | Web-API | Bao gồm 6 service Backend xử lý logic và dữ liệu. |
| 3 | Database | Sử dụng MySQL database dể lưu trữ dữ liệu. |
| 4 | RabbitMQ | Hỗ trợ quản lý hàng đợi cho một số chức năng bất đồng bộ. |
| 5 | POA | Đồng bộ data nhân sự và phòng ban |
| 6 | HRMS | Đẩy thông tin về dự án của các nhân sự cho HRMS |
| 7 | Skillset | Đồng bộ thông tin về Skill của nhân sự |
| 8 | PMS | Đồng bộ data về issues của các dự án trên jira |

## Low-Level Design

### Architectural Diagrams

### Database

.....		

## Layer

### Applications (Client Web Apps)

| **No.** | **Application Type** | **Technology** | **Description** |
| --- | --- | --- | --- |
| 1 | Web Application | HTML5/CSS3 |  |
|  |  | ant design 3 | UI components library. |
|  |  | Reactjs 15 | Web Front-end framework. |
|  |  | Scss 1.69 | Css preprocessor |
|  |  | Redux/flux 8 | State management library |

## Package Layer

| **No.** | **Group** | **Component** | **Purpose** |
| --- | --- | --- | --- |
| 1 | Common | Annotation | Là thư mục chứa các custom Annotation |
|  |  | Enums | Là thư mục chứa các common enums |
|  |  | Exception | Là thư mục chứa các common Exception |
|  |  | Handler | Là thư mục chứa các lớp handler exception |
| 2 |  | Controller | Là lớp định nghĩa các API của hệ thống |
| 3 |  | Utils | Là thư mục chứa các lớp định nghĩa Constant, common function |
| 4 |  | Services | Là thư mục chứa các lớp, interface chung |
| 5 |  | Repository | Là thư mục chứa các lớp thực hiện các query dữ liệu, kết nối với database |
| 6 |  | Config | Là thư mục chứa các lớp định nghĩa các config của hệ thống |
| 7 |  | Dto | This layer is data transfer object layer. Data that used to exchange between layers and other systems is defined in here |
| 8 |  | Logic | Lớp này định nghĩa các xử lý logic common của hệ thống. |
| 9 |  | Listener | Là thư mục chứa các lớp listener |
| 10 |  | Mapper | Là thư mục chứa các lớp định nghĩa Mapper giữa Entity và Dto |
| 11 |  | Model | Là thư mục chứa các lớp định nghĩa các các base Entity |

## Deploy overview

## External Services

Dashboard using some external service to improve the efficiency of its functions.

| **No.** | **External System** | **Purpose** |
| --- | --- | --- |
| 1 | Nexus | Lưu trữ thư viện cho các service tải về khi build. |
| 2 | POA | Đồng bộ data nhân sự và phòng ban |
| 3 | HRMS | Đẩy thông tin về dự án của các nhân sự cho HRMS |
| 4 | Skillset | Đồng bộ thông tin về Skill của nhân sự |
| 5 | CRM | Đồng bộ data về issues của các dự án trên jira |

##  Server Topology

**Physical Topology**

| **No.** | **Server** | **Configuration** | **Description** |
| --- | --- | --- | --- |
| 1 | Web Server | Dell Intel / 20 GB RAM  OS: CentOS 7 | For hosting web app. |
| 2 | Application Server | Dell Intel / 20GB RAM OS: CentOS 7 | For hosting Server app |
| 3 | Database Server | Dell Intel / 12 GB RAM OS: CentOS 7 | For install MySQL |

## Source code

- Frontend: [https://source.cmcglobal.com.vn/it-tools/dashboard-phase2-frontend](https://source.cmcglobal.com.vn/it-tools/dashboard-phase2-frontend)

- Backend: [https://source.cmcglobal.com.vn/it-tools/dashboard-phase2-backend](https://source.cmcglobal.com.vn/it-tools/dashboard-phase2-backend)

- Delivery: [https://source.cmcglobal.com.vn/tdx/delivery-project](https://source.cmcglobal.com.vn/tdx/delivery-project)

- Refactor: [https://source.cmcglobal.com.vn/it-tools/refactor-dashboard](https://source.cmcglobal.com.vn/it-tools/refactor-dashboard)

- Including Group, Master-data, Timesheet

- Sale: [https://source.cmcglobal.com.vn/tdx/sale-project](https://source.cmcglobal.com.vn/tdx/sale-project)

- Common: [https://source.cmcglobal.com.vn/tdx/glbnb2300501-dashboard/dashboard-common](https://source.cmcglobal.com.vn/tdx/glbnb2300501-dashboard/dashboard-common)

## List API

- Danh sách all API của hệ thống:

- Backend: [https://dashboard-backend-uat.cmcglobal.vn/swagger-ui/index.html](https://dashboard-backend-uat.cmcglobal.vn/swagger-ui/index.html)

- Delivery: [https://dashboard-delivery-uat.cmcglobal.vn/swagger-ui/index.html](https://dashboard-delivery-uat.cmcglobal.vn/swagger-ui/index.html)

- Group: [https://dashboard-group-uat.cmcglobal.vn/swagger-ui/index.html](https://dashboard-group-uat.cmcglobal.vn/swagger-ui/index.html)

- Master data: [https://dashboard-masterdata-uat.cmcglobal.vn/swagger-ui/index.html](https://dashboard-masterdata-uat.cmcglobal.vn/swagger-ui/index.html)

- Timesheet: [https://dashboard-timesheet-uat.cmcglobal.vn/swagger-ui/index.html](https://dashboard-timesheet-uat.cmcglobal.vn/swagger-ui/index.html)

- Sale: [https://dashboard-sale-uat.cmcglobal.vn/swagger-ui/index.html](https://dashboard-sale-uat.cmcglobal.vn/swagger-ui/index.html)

- Danh sách các API publish cho hệ thống khác:

##  Cảnh báo rủi ro

- Cần chạy service group trước khi chạy các service còn lại

- FE khi build cần sửa config profile trong file webpack.config.docker.js

3

			

1

---

<a id="3-cmcg-ams-dashboard-matrix-permission-xlsx"></a>
## 3. CMCG_AMS_Dashboard_Matrix_permission.xlsx

## Sheet: Sheet2

		role	Admin	QAL	QA	PM	SEPG	SALE	BOM	PMO	DUL
	Activity
PCV rate	Add PCV rate		o	o	x	x	x	x	x
	Edit PCV rate		o	o	o*	x	x	x	x
	View		o	o	o	o	o	x	o
	Delete PCV rate		o	o	o*	x	x	x	x
Bug rate input	View bug rate		o	o	o	o	o	x	o
Leakage rate input	View leakage rate		o	o	o	o	o	x	o
Timeliness input	view timeliness rate		o	o	o	o	o	x	o
CSS	View CSS		o	o	o	o	o	x	o
	Add CSS		o	o	o*	x	x	x	x
	Edit CSS		o	o	o*	x	x	x	x
	Delete CSS		o	o	o*	x	x	x	x
KPI taloring	View tailotring		o	o	o	o	o	x	o
	Edit tailoring		o	o	o	x	x	x	x
WO list	View		o	o	o	x	o	o*	o	o
Actual timesheet	View		o	o	o	o	o	x	o	o	o
	Edit		o	x	x	o*	x	x	x	x	o*
	Approve		o	x	x	o*	x	x	x	x	o*
Actual billable	view		o	x	x	x	x	o	x	o	x
	edit		o	x	x	x	x	o	x	o	x
	add		o	x	x	x	x	o	x	o	x
	delete		o	x	x	x	x	o	x	o	x

## Sheet: Project request old

Function/Role		Group Division
		BOM	Admin	G lead	PMO	QA	SEPG	Sale	DU
									DU Lead	PM
View	Tất cả các loại request Open for new project/Open for existing project/Update/Close đã được tạo	x	x		x	x	x
	Tất cả các loại request Open for new project/Open for existing project/Update/Close thuộc G host quản lý	x	x	x	x	x	x
	Tất cả các loại request Open for new project/Open for existing project/Update/Close thuộc DU host phụ trách			x	x	x	x		x
	Các loại request Open for new project/Open for existing project/Update mà Sale đó đã tạo 				x	x	x	x	x
	Các loại request Open for new project/Open for existing project/Update của khách hàng mà Sale đó quản lý				x	x	x	x	x
	Các Open for new project/Open for existing project/Update đang trong quá trình phê duyệt mà người đó là PM với Status Pending approval by PM				x	x	x		x	x
	Close request đã tạo mà người đó là PM				x	x	x		x	x
	View details	x	x	x	x	x	x	x	x	x
Create 	Open for new project/Open for existing project/Update							x
	Close 									x
Edit	Open for new project ( Tên dự án + Jira + Sharepoint link + GIT)						x
Approve	Các loại Open for new project/Open for existing project/Update form mà người đó là PM									x
	Các loại Open for new project/Open for existing project/Update/Close form có trạng thái Pending approval by SEPG						x
	Close form mà người đó là QA					x
Reject	Các loại Open for new project/Open for existing project/Update form mà người đó là PM									x
	Các loại Open for new project/Open for existing project/Update/Close form có trạng thái Pending approval by SEPG						x
	Close form mà người đó là QA					x

## Sheet: Project request p2

Function/Role		Group Division
		BOM	Admin	G lead	PMO	QA	QAL	SEPG	DU
									DU Lead	PM
View	Tất cả các loại request Open for new project/Update/Close đã được tạo	x	x		x	x	x	x
	Tất cả các loại request Open for new project/Update/Close thuộc G host quản lý	x	x	x	x	x	x	x
	Tất cả các loại request Open for new project/Update/Close thuộc DU host phụ trách			x	x	x	x	x	x
	Các Open for new project/Update đang trong quá trình phê duyệt mà người đó là PM với Status Pending approval 				x	x	x	x		x
	Close request đã tạo mà người đó là PM				x	x	x	x		x
	View details	x	x	x	x	x	x	x	x	x
Create	Close 									x
Edit	Open for new project/Update								x	x
Save	Open for new project/Update								x	x
Approve	Các loại Open for new project/Update form mà người đó là PM									x
	Close form mà người đó là QA với trạng thái Pending approval by QA					x
	Close form mà người đó là QAL với trạng thái Pending approval by QAL						x
Reject	Các loại Open for new project/Update form mà người đó là PM									x
	Close form mà người đó là QA					x
	Close form mà người đó là QAL						x

## Sheet: project list

Function/Role		Group Division
		BOM	Admin	G lead	PMO	QA	SEPG	Sale	DU
									DU Lead	PM
View 	Tất cả các dự án đã tạo thành công trên DB	x	x		x	x	x
	Tất cả các dự án đã tạo thành công trên DB thuộc G host đó quản lý	x	x	x	x	x	x
	Tất cả các dự án đã tạo thành công trên DB thuộc DU host phụ trách	x	x	x	x	x	x		x
	Tất cả các dự án đã tạo thành công trên DB	x	x			x	x
	Các dự án mà người đó là PM									x

## Sheet: Project information

Function/Role		BOM	Admin	Group Division
				G lead	PMO	QA Lead	QA	SEPG	Sale	DU
										DU Lead	PM
View	Tất cả thông tin trên màn hình project information	x	x	x	x	x	x	x		x	x
Edit	General information (Các trường enable cho QA và QAL)		x			x	x
	General information (Các trường enable cho SEPG)		x					x
	QA package		x			x	x

## Sheet: Project zone

	Các role khác ngoài PMO thì BA sẽ cần confirm với stakeholder
Function/Role		Group Division
		G Lead	PMO	QA	DU
					DU Lead	PM	BU Manager	BU Lead	BU Staff
View 	Project zone của dự án thuộc DU
	Project zone của dự án thuộc G		x
	Project zone của dự án không thuộc DU
	Project zone của dự án không thuộc G
Add zone	Project zone của dự án thuộc DU
	Project zone của dự án thuộc G		x
	Project zone của dự án không thuộc DU
	Project zone của dự án không thuộc G
Edit zone	Project zone của dự án thuộc DU
	Project zone của dự án thuộc G		x
	Project zone của dự án không thuộc DU
	Project zone của dự án không thuộc G

---

<a id="4-cmcg-ams-dashboard-projects-user-guide-v1-0-docx"></a>
## 4. CMCG_AMS_Dashboard_Projects_User guide v1.0.docx

<DashBoard>

User Guide

Dashboard - Projects

Version: <1.0>

| Issued Status: |  |
| --- | --- |
| Issued Date: |  |
| Owner: | Nguyễn Hà Anh |
| Author: | Nguyễn Hà Anh |
| Location: | [https://dashboard.cmcglobal.com.vn/project/list](https://dashboard.cmcglobal.com.vn/project/list) |
| Confidential Class: | Confidential |

| Date: | 07/2022 |
| --- | --- |
| Approved by: | HTAnh6 |
| Signature: |  |

Approval Information

| Approver Name | Role | Date (mm-dd-yyyy) | Revision | Comment |
| --- | --- | --- | --- | --- |
|  |  |  |  |  |

Revision History

| Revision | Date (mm-dd-yyyy) | Description | Revised by | Reviewer | Date (mm-dd-yyyy) |
| --- | --- | --- | --- | --- | --- |
|  |  |  |  |  |  |
|  |  |  |  |  |  |

RECORD OF CHANGE

| No | Effective Date | Version | Change Description | Reason | Reviewer | Approver |
| --- | --- | --- | --- | --- | --- | --- |
| 1 |  |  |  |  |  |  |
| 2 |  |  |  |  |  |  |
| 3 |  |  |  |  |  |  |
| 4 |  |  |  |  |  |  |
| 5 |  |  |  |  |  |  |

Mục Lục

Table of Contents

	Giới thiệu	4

	Định nghĩa từ viết tắt	4

	System requirements	4

	Hướng dẫn cài đặt	4

	Hướng dẫn sử dụng	4

			Tổng quan	4

			Đăng nhập hệ thống	5

			Tổng quan (Overview)	5

	Chức năng lọc thông tin (Filter)	6

	Chức năng giải trình KPI (Explain)	9

			Danh sách dự án (Project List) và Đăng kí KPI	10

		I.	Danh sách dự án (Project List)	10

	Chức năng lọc thông tin (Filter)	11

	Chức năng xem chi tiết KPI của 1 dự án	12

		II.	Đăng kí KPI (KPI Register)	12

	Chức năng xem KPI đã đăng kí	15

	Chức năng chỉnh sửa KPI đã đăng kí	16

			Update KPI (KPI Tracker)	18

	Chức năng Xem KPI Tracker	18

	Chức năng Update/edit KPI Tracker	19

	# Giới thiệu

*Bản** **hướng** **dẫn** **dành** **cho** **người** **sử** **dụng** **khi** **truy** **cập** **vào** “**DashBoard** - Projects”*

	# Định nghĩa từ viết tắt

*[Chi **tiết** **về** **các** **thuật** **ngữ** **và** **định** **nghĩa** **được** **sử** **dụng** **trong** **bản** **hướng** **dẫn** **này**.]*

	# System requirements

	# Hướng dẫn cài đặt

*Vui** **lòng** **tham** **khảo** **Hướng** **dẫn** **triển** **khai*

	# Hướng dẫn sử dụng

		## Tổng quan

Phần “Projects” là phần tính năng trên hệ thống Dashboard để theo dõi tổng quan thông tin các dự án của công ty CMC Global

Module “Projects” cho phép QA, SEPG và các cấp quản lý đăng kí, update việc thực hiện KPI của dự án.

		## Đăng nhập hệ thống

		- Bước 1: Truy cập giao diện đăng nhập hệ thống

- Bước 2: Nhập Ldap và mật khẩu Ldap

- Bước 3: Chọn Login hoặc ấn Enter để đăng nhập vào hệ thống

## Tổng quan (Overview)

Yêu cầu: Đăng nhập thành công vào hệ thống

Nhấn: Projects/ Overview trên thanh công cụ

Màn hình hiển thị tất cả các nội dung:

### Chức năng lọc thông tin (Filter)

- Bước 1: Chọn 1 hoặc đồng thời nhiều Filter: 

+ Thời gian: MM/YYYY

+ Division: Chọn Group

+ Department: Sau khi chọn Group, chọn 1 DU trong group để xem chi tiết ở cấp độ DU

+ Location: Xem thông tin theo vị trí công ty Global (HN, ĐN, HCM,…)

- Thông tin đầu ra: 

#### Filter Group/Company: 

- Biểu đồ “**Project Type**” - Biểu đồ (% và số lượng) phân loại dự án của công ty CMC Global (HN, ĐN,…) theo loại: ODC, Project based, Onsite, Testing, Training

- Biểu đồ “**Total projects**” -  Biển đồ (% và số lượng) phân loại dự án theo trạng thái (Status): New, Open (Due this month, Running, Overdue), Close (Ontime, Overdue)

- Bảng:

- Bảng chi tiết số lượng dự án theo trạng thái ở cấp độ: Công ty – Group – DU 

- Bảng chi tiết Effort của Công ty – Group - DU

#### Filter DU hoặc click vào tên của 1 DU trong bảng G/Company:

** **

- Biểu đồ “**Project Type**” - Biểu đồ (% và số lượng) phân loại dự án của DU theo loại: ODC, Project based, Onsite, Testing, Training

- Biểu đồ “**Total projects**” -  Biển đồ (% và số lượng) phân loại dự án theo trạng thái (Status): New, Open (Due this month, Running, Overdue), Close (Ontime, Overdue)

- Biểu đồ "**KPI ****Overview**” 12 tháng/năm và giải trình 

- Bảng:

- Bảng chi tiết số lượng dự án theo trạng thái ở cấp độ DU

- Bảng Efforts của DU  

- Bảng KPI của DU

- Note: Nếu không chọn Filter, hệ thống hiện mặc định thông tin ở cấp độ Company: Global HN; Thời gian: Hiện tại 

### Chức năng giải trình KPI (Explain)

    Yêu cầu: Chọn xem thông tin ở cấp độ DU

- Bước 1: Ở biểu đồ "**KPI Overview**”, chọn nút “Explain”. Hệ thống hiện popup như hình:

-  Bước 2: Chọn tháng cần nhập giải trình/cần xem ở thanh chọn thời gian

- Bước 3: Nhập nội dung

- Bước 4: Nhấn “Save” để lưu/ “Cancel” để hủy và quay về màn hình Overview ban đầu

## Danh sách dự án (Project List) và Đăng kí KPI

### Danh sách dự án (Project List)

Yêu cầu: Đăng nhập thành công vào hệ thống

Nhấn: Projects/Projects trên thanh công cụ

Màn hình hiển thị tất cả các nội dung:

       

### Chức năng lọc thông tin (Filter)

- Bước 1: Chọn 1 hoặc đồng thời nhiều Filter: 

+ Thời gian: MM/YYYY

+ Division: Chọn Group

+ Department: Sau khi chọn Group, chọn 1 DU trong group để xem chi tiết ở cấp độ DU

+ Status: Chọn từ list dropdown để xem dự án theo trạng thái

+ Type: Chọn từ list dropdown để xem dự án theo loại

+ PM: Chọn từ list dropdown để xem dự án theo tên PM

- Thông tin đầu ra: Hiển thị danh sách các dự án cùng thông tin chung như hình:

    

### Chức năng xem chi tiết KPI của 1 dự án

Yêu cầu:

- Dự án đã được đăng kí KPI trong năm

- Bước 1: Chọn 1 dự án, click vào tên dự án. 

- Bước 2: Màn hình chuyển sang tab “KPI Tracker” 

### Đăng kí KPI (KPI Register)

Yêu cầu:

- Dự án chưa từng được đăng kí KPI trong năm

- Người dùng có role PM/PMO

- Bước 1: Click nút “KPI Register” ở góc phải màn hình

- Bước 2: Hệ thống hiển thị màn hình đăng kí như hình:

                                

- Bước 3: Nhập thông tin dự án “A. General Information”

+ Project name: Click để chọn tên dự án từ list (bắt buộc)

+ Project code: Chọn Project code (bắt buộc)

+ Approved Jira ticket link: Nhập Link Jira của dự án (bắt buộc)

+ Các thông tin còn lại hệ thống tự động điền theo các thông tin nhập bởi người dung

- Bước 4: Đăng kí chỉ tiêu KPI cho các tiêu chí chung ở mục “Standard Objectives”: 

+ Chỉnh sửa số ở phần Target theo giới hạn tiêu chuẩn (bắt buộc); 

+ Chọn 1 từ list Frequency: Chu kì thực hiện chỉ tiêu  (bắt buộc) 

+ Note (không bắt buộc)

+ Click icon cục tẩy ở cuối dòng để xóa toàn bộ thông tin vừa thêm

- Bước 5 (Optional) (Dành cho các dự án có tiêu chí riêng): Click nút “Special Objectives” và Nhập/thêm các tiêu chí riêng  

+  Nhập tên tiêu chí; Đơn vị tính (Unit); Công thức tính (Formular); Target; Chu kì thực hiện đo (Frequency); Note.

+ Click icon thùng rác ở cuối dòng để xóa

- Bước 6: Click “Register” để lưu đăng kí/ “Cancel” hoặc “Back” icon để hủy

### Chức năng xem KPI đã đăng kí

Yêu cầu: Dự án đã được đăng kí KPI trong năm

- Bước 1: Ở màn danh sách dự án, chọn 1 dự án bất kì, click nút “con mắt” ở cuối để xem.

- Bước 2: Click nút “Close”/ “Back” để đóng bảng thông tin đã đăng kí và quay lại màn hình danh sách dự án như hình dưới.

### Chức năng chỉnh sửa KPI đã đăng kí

Yêu cầu: 

- Dự án đã được đăng kí KPI trong năm

- User có role là member SEPG

- Bước 1: Ở màn danh sách dự án, chọn 1 dự án bất kì, click biểu tượng “bút chì” ở cuối để mở form chỉnh sửa.

- Bước 2: Click nút “Edit”

- Bước 3: Chỉnh sửa nội dung; thêm note ở dòng vừa chỉnh sửa (Bắt buộc)

- Bước 4: Click “Save” để lưu thay đổi/ “Cancel”/”Back” để hủy thay đổi như hình:

## Update KPI (KPI Tracker)

### Chức năng Xem KPI Tracker

Yêu cầu: 

- Dự án đã có Resource Allocate

- Dự án đã có đăng kí KPI

- User có role là member SEPG, QA, PM, PMO của dự án và các dự án khác trong cùng 1 DU

- Bước 1: Ở màn hình project list, click vào tên 1 dự án/click vào biểu tượng bút chì ở cuối dòng tên dự án/Click vào tab “KPI Tracker”. Màn hình hiển thị:

- Bước 2: Click vào 1 trong các tab: Quality, Process, Productivity, Billable Rate, Timeliness, Specific Objectives để xem các bảng tương ứng

- Bước 3: Để quay lại màn hình danh sách dự án, click vào tab “Projects” trong module “Projects”

### Chức năng Update/edit KPI Tracker

Yêu cầu: 

- Dự án đã có Resource Allocate

- Dự án đã có đăng kí KPI

- User có role là member SEPG, QA, PMO của dự án

- Bước 1:  Chọn 1 bảng KPI cần thao tác

- Bước 2: 

Hệ thống tự động hiện bảng theo 12 tháng của năm hiện tại

Tại dòng thời gian cần update/edit KPI, click biểu tượng “Bút chì” ở cuối để thực hiện update/edit số liệu ở những phần ô trắng, có thể edit. Nhập các trường bắt buộc.

Sau đó chọn 1 giá trị từ list Quality Assessment (đánh giá số actual so với target) (bắt buộc). 

Sau khi chọn “Quality Assessment”, màu của dòng thay đổi theo giá trị đó:

+ Good: Xanh lá

+ Warning: Vàng

+ Bad: Đỏ

+ Tháng hiện tại: Xanh nước biển

- Bước 3: Sau khi edit, click biểu tượng dấu “tích” để lưu/ biểu tượng cục tẩy để xóa toàn bộ thông tin vừa nhập/ biểu tượng dấu “x” để bỏ lưu. 

- Bước 4: Riêng đối với bảng “Billable”

Yêu cầu: Người dùng có role: PMO

Các bước thực hiện update như các bảng khác

Có thể click “Add row” để thêm dòng mới phía dưới 

## Technical Support 

[Further information about technical contact.]

---

<a id="5-cmcg-ams-dashboard-slide-training-module-kpi-pptx"></a>
## 5. CMCG_AMS_Dashboard_Slide Training_Module KPI.pptx

## Slide 1

Dashboard – Module chỉ số KPI chất lượng dự án

February 2024

User Guide_ QA/ PM

## Slide 2

Nội dung

03

02

01

Tổng quan module chỉ số KPI chất lượng dự án

Hướng dẫn thao tác trên hệ thống Jira và Dashboard

Q&A

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE CHỈ SỐ KPI CHẤT LƯỢNG DỰ ÁN

1.2. Quy trình nhập data input cho chỉ số KPI chất lượng dự án

## Slide 4

1.1

MỤC ĐÍCH

Tạo quy trình và số hóa quá trình liên quan đến các chỉ số chất lượng dự án
Giúp giảm tải công việc tổng hợp data KPI chất lượng dự án và các công việc phối hợp giữa QA và PM. Users dễ dàng nắm bắt thông tin chỉ số chất lượng dự án.
Quản lý tập trung dữ liệu KPI Dự án trên hệ thống Dashboard

## Slide 5

1.1

Quy trình nhập data input chỉ số KPI chất lượng 

## Slide 6

1.1

Quy trình nhập data input chỉ số KPI chất lượng 

Project Jira: Sau khi Jira và Dashboard của dự án được tạo ra, phía SEPG sẽ điền Jira key vào dự án trên Dashboard.  
Data Jira của dự án sẽ được đồng bộ sang dự án trên Dashboard tương ứng

## Slide 7

1.1

Quy trình nhập data input chỉ số KPI chất lượng 

Đối với data bug, leakage, deliverables: User nhập data bên Jira, hệ thống Dashboard sẽ đồng bộ và tính toán chỉ số KPI chất lượng tương ứng 

Đối với data PCV và CSS: QA dự án nhập trên hệ thống Dashboard 

## Slide 8

1.2

Đối tượng người dùng và nhiệm vụ chính

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được thao tác toàn bộ trên hệ thống
SEPG
Tạo Jira của dự án và gắn Jira của dự án lên Dashboard
PM
Tổng hợp data, kiểm tra thông tin về chỉ số chất lượng dự án
QA
Đo lường chỉ số chất lượng dự án và nhập data
DU Lead
Tiếp nhận thông tin và quản lý thông tin chỉ số chất lượng dự án

## Slide 9

Deliverables

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

Bugs

PCV

CSS

Leakages

Tailoring chỉ số KPI

## Slide 10

2.1

Nhập data về Deliverables của dự án

Dự án sử dụng Jira của CMC Global – Release theo ticket

Users tạo ticket có issue type = Bug/Leakage/Story/Task
User sẽ phải chọn trường Deliverables để xác định ticket có liên quan đến công việc deliver cho khách hàng hay không
Các ticket được xem là delivered deliverables cần đáp ứng điều kiện sau: 
Resolution = Done 
Deliverables? = Yes 
Resolved date khác blank 
Xác định ticket delivered trong tháng nào dựa trên tháng của Resolved date 
Các ticket được xem là On time deliverables cần đáp ứng điều kiện sau: 
Resolution = Done 
Deliverables? = Yes
Resolve Date =< Due Date 
Xác định ticket delivered trong tháng nào dựa trên tháng của Resolved date 

## Slide 11

2.1

Nhập data về Deliverables của dự án

Dự án sử dụng Jira của CMC Global – Release theo package

Users tạo ticket có issue type = Delivery
QA sẽ chọn trường Delivery result để xác định package dự án deliver cho khách hàng là On time hoặc Late
Package được xem là delivered deliverables cần đáp ứng điều kiện sau: 
Status = Package Released
Due date khác blank
Delivery result khác None 
Xác định package delivered trong tháng nào dựa trên tháng của Resolved date 
Package được xem là On time deliverables cần đáp ứng điều kiện sau: 
Status = Closed (QA dự án chuyển trạng thái từ Package Released sang Closed)
Due date khác blank
Delivery result = On time
Resolved date <= Due date
Xác định package delivered trong tháng nào dựa trên tháng của Resolved date 

## Slide 12

2.1

Nhập data về Deliverables của dự án

Dự án sử dụng Jira của khách hàng

Nếu dự án sử dụng Jira khách hàng, phía SEPG sẽ tạo Jira nội bộ CMC tương ứng để quản lý nội bộ
QA/PM dự án tạo ticket có issue type = Number of deliverables
Form ticket yêu cầu điền thông tin sau
Summary
Baseline date: Thời điểm chốt số liệu
Số lượng deliverable đã delivered trong tháng
Số lượng deliverable được delivered On time trong tháng 

## Slide 13

2.1

Nhập data về Deliverables của dự án

Dự án sử dụng Jira của khách hàng

Chỉ có QA, QAL và PM được hoạt động trong luồng flow này 
Chỉ role QA/PM được khởi tạo và edit issue type Number of Deliverables
Người khởi tạo chuyển trạng thái sang review 
Chỉ PM/QA chuyển trạng thái Review sang Resolved. Nếu QA là ng tạo ticket thì chỉ PM đc chuyển, nếu PM tạo ticket thì chỉ QA đc chuyển  
Chỉ Reporter mới có quyền chuyển từ Resolved sang Baselined 
Tại Status = Baselined thì không thể edit. Nếu cần edit thì phải chuyển về trạng thái khác Cancel 
Chỉ có QAL mới có quyền chuyển từ Baselined sang các status khác 

## Slide 14

2.1

Nhập data về Deliverables của dự án

Data về deliverables bên Jira sẽ hiển thị trên Dashboard của dự án

Lưu ý: 
Đối với issue type = Number of Deliverables, hệ thống sẽ chỉ tính timeliness cho những ticket có status = Baselined sau ngày cuối cùng của tháng

## Slide 15

2.2

Nhập data về Bug của dự án

Dự án sử dụng Jira của CMC Global 

Users tạo ticket có issue type = Bug
Hệ thống Dashboard sẽ đếm số lượng bug luỹ kế 
Không tính các bug có status = Cancel

## Slide 16

2.2

Nhập data về Bug của dự án

Dự án sử dụng Jira của khách hàng

Nếu dự án sử dụng Jira khách hàng, phía SEPG sẽ tạo Jira nội bộ CMC tương ứng để quản lý nội bộ
QA/PM dự án tạo ticket có issue type = Number of Bugs để nhập số lượng bug luỹ kế của dự án
Form ticket yêu cầu điền thông tin sau
Summary
Number of items: Số lượng bug luỹ kế
Related activity has not happened?: Chỉ tick chọn khi hoạt động testing của dự án chưa diễn ra
Baseline date: Thời điểm chốt số liệu
Severity: Mức độ nghiêm trọng của bug

## Slide 17

2.2

Nhập data về Bug của dự án

Dự án sử dụng Jira của khách hàng

Chỉ có QA, QAL và PM được hoạt động trong luồng flow này 
Chỉ role QA/PM được khởi tạo và edit issue type Number of Bugs
Người khởi tạo chuyển trạng thái sang review 
Chỉ PM/QA chuyển trạng thái Review sang Resolved. Nếu QA là ng tạo ticket thì chỉ PM đc chuyển, nếu PM tạo ticket thì chỉ QA đc chuyển  
Chỉ Reporter mới có quyền chuyển từ Resolved sang Baselined 
Tại Status = Baselined thì không thể edit. Nếu cần edit thì phải chuyển về trạng thái khác Cancel 
Chỉ có QAL mới có quyền chuyển từ Baselined sang các status khác 

## Slide 18

2.2

Nhập data về Bug của dự án

Data về số lượng bug luỹ kế bên Jira sẽ hiển thị trên Dashboard của dự án

Lưu ý: 
Hệ thống Dashboard sẽ baseline số lượng bug luỹ kế sau ngày cuối cùng của tháng
Đối với issue type = Number of Bugs, hệ thống sẽ chỉ tính số liệu cho những ticket có status = Baselined

## Slide 19

2.3

Nhập data về Leakage của dự án

Dự án sử dụng Jira của CMC Global 

Users tạo ticket có issue type = Leakage
Hệ thống Dashboard sẽ đếm số lượng leakage luỹ kế từng tháng
Không tính các leakage có status = Cancel

## Slide 20

2.3

Nhập data về Leakage của dự án

Dự án sử dụng Jira của khách hàng

Nếu dự án sử dụng Jira khách hàng, phía SEPG sẽ tạo Jira nội bộ CMC tương ứng để quản lý nội bộ
QA/PM dự án tạo ticket có issue type = Number of Leakages để nhập số lượng leakage luỹ kế của dự án
Form ticket yêu cầu điền thông tin sau
Summary
Number of items: Số lượng leakage luỹ kế
Related activity has not happened?: Chỉ tick chọn khi hoạt động UAT của dự án chưa diễn ra
Baseline date: Thời điểm chốt số liệu
Severity: Mức độ nghiêm trọng của bug

## Slide 21

2.3

Nhập data về Leakage của dự án

Dự án sử dụng Jira của khách hàng

Chỉ có QA, QAL và PM được hoạt động trong luồng flow này 
Chỉ role QA/PM được khởi tạo và edit issue type Number of Leakages
Người khởi tạo chuyển trạng thái sang review 
Chỉ PM/QA chuyển trạng thái Review sang Resolved. Nếu QA là ng tạo ticket thì chỉ PM đc chuyển, nếu PM tạo ticket thì chỉ QA đc chuyển  
Chỉ Reporter mới có quyền chuyển từ Resolved sang Baselined 
Tại Status = Baselined thì không thể edit. Nếu cần edit thì phải chuyển về trạng thái khác Cancel 
Chỉ có QAL mới có quyền chuyển từ Baselined sang các status khác 

## Slide 22

2.3

Nhập data về Leakage của dự án

Data về số lượng leakage luỹ kế bên Jira sẽ hiển thị trên Dashboard của dự án

Lưu ý: 
Hệ thống Dashboard sẽ baseline số lượng leakage luỹ kế sau ngày cuối cùng của tháng
Đối với issue type = Number of Leakages, hệ thống sẽ chỉ tính số liệu cho những ticket có status = Baselined

## Slide 23

2.4

Nhập data về CSS của dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Input -> Section CSS
Click button Add

## Slide 24

2.4

Nhập data về CSS của dự án

Form Add CSS bao gồm các thông tin sau
Project Jira: Jira của dự án
Baseline: Thời gian phía CMC nhận CSS từ khách hàng
Duration: Phía CMC xin CSS khách hàng cho giai đoạn nào của dự án
Value: Điểm CSS của dự án 
Default point: Điểm CSS này là điểm khách hàng cho hay là điểm măc định
Comment: Comment của khách hàng (nếu có)

## Slide 25

2.5

Nhập data về PCV của dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Input -> Section PCV rate
Click button Add để tạo lần đo PCV

## Slide 26

2.5

Nhập data về PCV của dự án

User chọn lần đo PCV thuộc stage nào

## Slide 27

2.5

Nhập data về PCV của dự án

QA nhập thông tin sau 
Summary: Summary về lần đo
Baseline month of Check result: Thời điểm chốt số liệu của lần đo

QA nhập kết quả của từng items và finding items trong trường hợp dự án chưa tuân thủ quy trình

Lưu ý: User không thể edit/delete lần đo PCV nếu tháng hiện tại > Baseline month of Check result

## Slide 28

2.6

Tailoring chỉ số

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Information -> Project KPI Tailoring
Click toggle button của chỉ số KPI tương ứng để tailor 

## Slide 29

2.6

Tailoring chỉ số

QA nhập link ticket Jira approve Tailoring chỉ số
Click Save
Khi chỉ số bị tailor, Số liệu của chỉ số đó sẽ hiển thị “Not Applicable”

## Slide 30

2.7

Hiển thị báo cáo cấp dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Overview -> Hệ thống hiển thị các chỉ số KPI chất lượng dự án, luỹ kế đến tháng hiện tại

## Slide 31

2.7

Hiển thị báo cáo cấp dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project KPI -> Hệ thống hiển thị chart các chỉ số

## Slide 32

2.8

Hiển thị báo cáo cấp công ty

Chọn Tab Dashboard -> Project Statistics 
Hệ thống hiển thị chart các chỉ số 

## Slide 33

03

Q&A

## Slide 34

---

<a id="6-cmcg-ams-dashboard-user-guide-qtmdda-v202082023-pptx"></a>
## 6. CMCG_AMS_Dashboard_User_Guide_QTMDDA_v202082023.pptx

## Slide 1

Dashboard – Quy trình mở và cập nhật dự án

July 2023

User Guide_ Sale/ PM

## Slide 2

Nội dung

03

02

01

Tổng quan quy trình mở và cập nhật dự án

Hướng dẫn thao tác trên hệ thống với các quy trình

Q&A

## Slide 3

1.1. Mục đích

01

TỔNG QUAN QUY TRÌNH MỞ, CẬP NHẬT DỰ ÁN TRÊN DASHBOARD

1.2. Quy trình mở và cập nhật dự án 

## Slide 4

1.1

MỤC ĐÍCH

Tạo quy trình khép kín & Số hóa toàn bộ quá trình liên quan đến tạo, thay đổi, đóng Dự án trên hệ thống
Giúp giảm tải công việc thực hiện trực tiếp và các công việc phối hợp giữa các bên. Thông tin chi tiết và tình trạng công việc được quản lý rõ ràng
Quản lý tập trung dữ liệu Dự án trên hệ thống Dashboard theo cấu trúc: Customer ID – Dự án – MVV

CRM

Dashboard

Sale hoặc sale support khởi tạo PC trên hệ thống CRM (JIRA)
(Sale cần làm việc với DUL/PM để xác định việc add mã mới vào dự án đã tồn tại hay khởi tạo dự án mới)

DUL/PM nhận notification/ vào project list check các request cần phê duyệt mở/cập nhật dự án
PM cần kiểm tra thông tin mã mới đã chính xác chưa theo quy định đã ban hành về dự án
Phê duyệt khởi tạo/cập nhật mã vào dự án

## Slide 5

1.1

Thay đổi trong luồng mở dự án

Hiện tại

Version mới

## Slide 6

QUY ĐỊNH MỚI

DỰ ÁN MỚI:

Là Dự án của Khách hàng mới (Khách hàng lần đầu hợp tác, chưa phát sinh Hợp đồng nào trước đó)
Của Khách hàng cũ (Khách hàng đã có phát sinh Hợp đồng trước đó) nhưng triển khai với category khác (VD: Hợp đồng trước là development, Hợp đồng sau là Maintenance) 
Trường hợp Dự án có nhiều line, vận hành thực tế khác nhau có thể xem xét tách thành các Dự án

 Được phép tạo mới Dự án trên hệ thống Dashboard             

DỰ ÁN CŨ:

Là Dự án đã được tạo trên hệ thống và đã/ đang triển khai
Có phát sinh thêm các Work Order trong quá trình thực hiện DA (ví dụ: 03 tháng/ lần, 06 tháng/ lần…)
Dự án đang triển khai nhưng Hợp đồng được tách biệt theo từng năm, thường đóng vào cuối năm và mở lại vào đầu năm (ví dụ: Maintenance năm 2022, Maintenance năm 2023)

## Slide 7

1.2

ĐỐI TƯỢNG NGƯỜI DÙNG VÀ NHIỆM VỤ CHÍNH

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được thao tác toàn bộ trên hệ thống
SEPG
Tiếp nhận thông tin và quản lý thông tin dự án của CMC Global
PM
Kiểm tra, phê duyệt tạo dự án và cập nhật dự án
QA
Tiếp nhận thông tin mở và cập nhật dự án
DU Lead
Tiếp nhận thông tin và quản lý thông tin dự án 
AM/Sale support
Khởi tạo yêu cầu mở và cập nhật từ hệ thống CRM

## Slide 8

Màn Project Request

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

Open request form

Màn Project List

Section Project Information 

Update request form

## Slide 9

2.1

  Quy trình mở dự án 

Đối tượng liên quan: Sale, FIN, PM

## Slide 10

2.2.1

  Update trên mã vụ việc tại CRM project code

Role Sale:

Sau khi khởi tạo đầy đủ pipeline (KT1) tiến hành tạo mã request bên tài chính phê duyệt

PC delivery Unit: AM cần làm việc với DU liên quan để xác định DU host phụ trách thực hiện dự án

Delivery Project: AM cần làm việc với DUL hoặc PM phụ trách để xác định Mã vụ việc trên là thuộc dự án đang chạy hay khởi tạo ra dự án mới.
Đối với dự án đang chạy AM cần chọn mã vv của dự án đang chạy đó (previous MVV)

Sau khi PC được active sẽ tự động tạo thành request dự án trên hệ thống dashboard màn request list

## Slide 11

2.1.2

  Màn hình Project Request

Mục đích: 
+ Quản lý tất cả các request type (Open/Update/Close) 
+ Cho phép PM phê duyệt các Open/Update request mà người đó là PM và tạo Close request của dự án.
+ Cho phép DUL view tất cả các loại request type mà có DU host thuộc DUL quản lý
+ Cho phép QA/QAL/SEPG view tất cả các loại request type 

## Slide 12

2.1.3

  Màn hình Project Request 

Role PM

Thao tác: 
1. Login vào hệ thống dashboard => hiển thị màn Project Request
2. Click button “View details” tại request có trạng thái “Pending approval by PM”  => hiển thị form Open for new project_detail để review và nhập thêm thông tin có trong form
 

## Slide 13

2.1.3

  Màn hình Project Request 

## Slide 14

2.1.3

  Màn hình Project Request 

Role PM

Thao tác: 
3. Review các thông tin trên form và nhập đầy đủ thông tin:
- Click “Approve” => Hệ thống check DA có end date > 31/3 năm tiếp theo k và => Hiển thị popup confirm. User chọn “Yes” => hiển thị message “Approved Open project request successfully“, back lại màn Project Request. Request status trong màn Project Request sẽ chuyển trạng thái sang “Approved”. Nếu click “No” => popup biến mất.
- Click “Reject” => hiển thị popup “Do you confirm to reject this request? User chọn “Yes” => hiển thị message “Rejected Open project request“, back lại màn Project Request. Request status trong màn Project Request sẽ chuyển trạng thái sang “Rejected”. Nếu click “No” => popup biến mất.

## Slide 15

2.1.3

  Màn hình Project List 

Mục đích: + Cho phép PM xem danh sách dự án mà họ là PM
                  + Cho phép DU lead xem danh sách các dự án đã tạo thành công có DU host mà DU lead đó quản lý 
                  + Cho phép QA/SEPG/PMO xem danh sách dự án đã được tạo thành công 

## Slide 16

2.1.4

  Section Project Information 

Mục đích: 
+ Cho phép PM view/edit thông tin dự án mà họ là PM, xóa MVV trong MVV list nếu MVV đó chưa được allocate và chưa được add billable. Sau khi edit thông tin -> click Save changes để lưu thay đổi
+ Cho phép DU lead xem danh sách các dự án đã tạo thành công có DU host mà DU lead đó quản lý 
+ Cho phép QA/QAL view/edit trường QA service package. Sau khi edit thông tin -> click Save changes để lưu thay đổi
+ Cho phép SEPG/PMO xem danh sách dự án đã được tạo thành công trên DB. 

## Slide 17

2.2

  Quy trình cập nhật dự án 

Đối tượng liên quan: Sale, PM

## Slide 18

2.1.1

  Màn hình tạo MVV (Project code) 

Role Sale:

TH1: với dự án điền sai thông tin về MVV vào PC đã active comment yêu cầu thay đổi thông tin với bên tài chính. Phía FIN sẽ kiểm tra và cập nhật. Khi cập nhật sẽ tạo request phía hệ thống Dashboard để PM kiểm tra và xác nhận

TH2: Với việc add thêm mã vào dự án đã tồn tại. Vẫn làm thủ tục xin cấp mã như mở mới tại trường
Delivery project: lựa chọn existing
Previous project code: lưa chọn MVV trước đó cần add thêm

## Slide 19

2.2.3

  Màn hình Project Request 

Role PM

Thao tác: 
1. Login vào hệ thống dashboard => hiển thị màn Project Request
2. Click button “View details” tại Update request có trạng thái “Pending approval by PM”  => hiển thị form Update_detail để review thông tin có trong form
 

## Slide 20

2.2.3

  Màn hình Project Request 

Role PM

Thao tác: 
1. Login vào hệ thống dashboard => hiển thị màn Project Request
2. Click button “View details” tại Update request có trạng thái “Pending approval by PM”  => hiển thị form Update_detail để review thông tin có trong form
 

## Slide 21

2.2.3

  Section Project Information 

Lưu ý:

+ Sau khi Update request được approve từ PM => các thông tin ở Update form sẽ được cập nhật vào màn Project Information 
+ PM view/edit thông tin dự án mà họ là PM, xóa MVV trong MVV list nếu MVV đó chưa được allocate và chưa được add billable. Sau khi edit thông tin -> click Save changes để lưu thay đổi
+ QA/QAL: view/edit trường QA service package. Sau khi edit thông tin -> click Save changes để lưu thay đổi

## Slide 22

04

Q&A

## Slide 23

---

<a id="7-cmcg-ams-tms---dashboard-051022-pptx"></a>
## 7. CMCG_AMS_TMS - Dashboard 051022.pptx

## Slide 1

Dashboard System - Actual Timesheet module
User Guide

September 2023

## Slide 2

Content

3.   Tính năng Actual Timesheet

4.   Report Actual Timesheet

2.   Tổng quan về Timesheet

5.   Những ảnh hưởng của chức năng timesheet tới việc quản lý dự án

1.   Hiện trạng

6.   Q&A

## Slide 3

1. Hiện trạng

## Slide 4

1.1   Hiện trạng

Hiện tại việc quản lý nhân sự và thời gian nhân sự tham gia vào dựa án chỉ dựa trên Resources Allocation được DU lead allocate đầu tháng (dựa trên planning)
Điều này dẫn đến không có con số chính xác về nhân sự thực tế tham gia vào dự án và thời gian làm việc thực tế của họ
 Sai lệch về chi phí dự án
 Sai lệch về timesheet và hóa đơn xuất cho khách hàng hàng tháng

1.2. Mục đích

Chức năng timesheet hỗ trợ theo dõi và quản lý thời gian thực tế của nhân sự tham gia vào dự án
Là cơ sở để tính toán chi phí dự án => KPI của dự án
Là cơ sở để xuất hóa đơn doanh thu cho dự án gửi khách hàng

## Slide 5

2. Tổng quan về Timesheet

## Slide 6

2.2. Tổng quan màn Timesheet

2. Tổng quan về Timesheet

2.1. Mục tiêu khoá training

## Slide 7

2.1. Mục tiêu khóa training

Hiểu và sử dụng được chức năng timesheet phục vụ trong công việc quản lý dự án hàng ngày
Hiểu ý nghĩa, mục đích và tầm quan trọng của tính năng trong quy trình quản lý dự án

## Slide 8

2.2. Tổng quan màn timesheet

Resources allocation

Actual timesheet

Actual billable

Từ những dự án được khởi tạo phân bổ nguồn lực dự kiến vào các dự án

Dựa trên thời gian của các nhân sự được phân bổ vào dự án và thời gian chấm công phê duyệt thời gian tham gia dự án của nhân sự

Dự trên số liệu actual timesheet đã được phê duyệt, chỉnh sửa và xuất timesheet cho khách hàng phê duyệt xuất hóa đơn

## Slide 9

2.2. Tổng quan màn timesheet

Chọn Timesheet

Tính năng của Timesheet

Body Timesheet

## Slide 10

3. Tính năng Actual Timesheet

## Slide 11

3.3. Edit actual timesheet

3. Tính năng Actual Timesheet

3.4. Approve actual timesheet

3.2. View và filter actual timesheet

3.1. Giới thiệu tính năng actual timesheet

3.5. Delete actual timesheet

## Slide 12

3.1. Giới thiệu tính năng actual timesheet

Filter tìm kiếm

Điều kiện cần: User phải add resource plan ở trong function Resource – tab Delivery

Resource được add

Lưu ý: 
Nhân sự được add resource phải ở trạng thái allocated thì mới đồng bộ sang màn Actual TS
Sau ngày 28 hàng tháng user không được thêm mới, sửa, xoá resource plan của tháng hiện tại

## Slide 13

3.1. Giới thiệu tính năng actual timesheet

Actor vào được tab Timesheet: BOM/DUL/PM/PMO/QA/SEPG (Phải được set quyền trên POA)

Chọn Timesheet

Tính năng của Timesheet

Body Timesheet

## Slide 14

3.2. View và filter actual timesheet

Chức năng Filter by users

Chọn tháng xem Timesheet

Chọn Filter by user

Chọn tên user

Chọn mvv

Kết quả filter

## Slide 15

3.2. View và filter actual timesheet

Tên nhân sự

Tổng thời gian làm việc trong tuần/tháng

Dự án nhân sự tham gia

Dữ liệu từ HRMS

Dữ liệu từ resource plan

Chức năng Filter by users

## Slide 16

3.2. View và filter actual timesheet

Chức năng Filter by users

Màu hiển thị trạng thái hiện tại
Cam: Timesheet data chưa được approve
Xanh: Timesheet data đã được approve

Màu dot cảnh báo chênh lệch về Hours per day Actual Timesheet dự án và giờ HRMS
Xanh: Số giờ trên HRMS >= Actual Timesheet
Vàng: 0 < HRMS < Actual Timesheet
Đỏ: Số giờ trên HRMS = 0 và Actual Timesheet >0

## Slide 17

3.2. View và filter actual timesheet

Chức năng Filter by projects

Tên dự án

Nhân sự của dự án

Resource được add từ Delivery

Dữ liệu từ HRMS

Dữ liệu từ resource plan

## Slide 18

3.2. View và filter actual timesheet

Chức năng Filter by projects

Màu hiển thị trạng thái hiện tại
Cam: Timesheet data chưa được approve
Xanh: Timesheet data đã được approve

Màu dot cảnh báo chênh lệch về Hours per day Actual Timesheet dự án và giờ HRMS
Xanh: Số giờ trên HRMS >= Actual Timesheet
Vàng: 0 < HRMS < Actual Timesheet
Đỏ: Số giờ trên HRMS = 0 và Actual Timesheet >0

## Slide 19

3.3. Edit actual timesheet

Bước 1: Trỏ chuột vào ô muốn sửa dữ liệu
Bước 2: Click chuột để hiển thị popup Edit

## Slide 20

3.3. Edit actual timesheet

Bước 3: Sửa thông tin actual timesheet và ấn Save

Lưu ý: 
Trường hợp user edit bản ghi Resource allocation của nhân sự, hệ thống sẽ update trường hours per day của bản ghi timesheet và chuyển status bản ghi từ approved thành pending (Nếu bản ghi đã được approve từ trước

## Slide 21

3.4. Approve actual timesheet – Cách 1

Bước 1: Nhấn chọn vào ô muốn approve hoặc kéo thả chuột vào các ô muốn approve 

## Slide 22

3.4. Approve actual timesheet – Cách 1

Bước 2: Khi hiển thị popup confirm, ấn chọn OK để approve
Data đã được approve sẽ có dot xanh để phân biệt

## Slide 23

3.4. Approve actual timesheet – Cách 2

Bước 1: Tick chọn timesheet của các nhân sự/dự án muốn approve. Có thể tick chọn timesheet đầu tiên để tick chọn tất cả nhân sự/dự án
Bước 2: Ấn nút Approve

## Slide 24

3.4. Approve actual timesheet – Cách 2

Bước 3: Khi hiển thị popup confirm, ấn chọn OK để approve
Data đã được approve sẽ có dot xanh để phân biệt

## Slide 25

3.4. Approve actual timesheet – Cách 3

Rule approve tự động của hệ thống:
Vào ngày 28 hàng tháng hệ thống sẽ tự động phê duyệt toàn bộ các timesheet ở trạng thái pending từ ngày 26 tháng trước đến ngày 25 của tháng hiện tại.
Thời gian được approve sẽ là số giờ Actual Timesheet của nhân sự tại thời điểm hệ thống tự động phê duyệt

## Slide 26

3.4. Delete actual timesheet

Trong trường hợp user tạo sai bản ghi actual timesheet:
User: Xoá bản ghi resource allocation của nhân sự
System: Hệ thống sẽ dựa trên các thông tin của data allocation được xoá, để xoá bản ghi actual timesheet tương ứng
Thông tin data allocation hệ thống dựa vào để xoá bản ghi actual timesheet tương ứng bao gồm:
Project nhân sự được allocate
Mã vụ việc nhân sự được allocate
Ngày nhân sự được allocate

## Slide 27

4. Report Actual Timesheet

## Slide 28

5. Những ảnh hưởng của chức năng timesheet tới việc quản lý dự án

## Slide 29

5. Những ảnh hưởng của chức năng timesheet tới việc quản lý dự án

5.1. Kiểm soát chi phí dự án
Việc tracking timesheet của các nhân sự trong dự án sẽ là cơ sở để tính chi phí cho dự án và ảnh hưởng trực tiếp đến đánh giá KPI tại các giai đoạn của dự án.
Việc kiểm soát timesheet sẽ giúp các cấp quản lý nhận ra sự chênh lệch về hiệu quả dự án giữa planning (resources allocation) và thực tế (actual timesheet) để có thể đưa ra các giải pháp kịp thời.
5.2. Xuất hóa đơn cho khách hàng
Dữ liệu phê duyệt timesheet là cơ sở để sale làm timesheet và xuất hóa đơn doanh thu dự án cho khách hàng 
Tiết kiệm thời gian cho sale trong việc đi xin timesheet để phê duyệt với khách hàng từ các DU
=> Vì vậy việc phê duyệt thời gian làm việc sẽ ảnh hưởng lớn đến giá trị doanh thu xuất hóa đơn cho khách hàng hàng tháng.

## Slide 30

6. Q&A

## Slide 31

Lưu ý:
Các thông tin thắc mắc khi sử dụng hệ thống vui lòng tạo Ticket lên Service Center

Steps tạo Ticket
1. Truy cập link: FIS - CMC Global JIRA
2. Chọn “Create”
3. Chọn “Project”: Service Center(SC)
     Issue Type: Dashboard
4. Điền các thông tin bắt buộc
5. Chọn “Save” để tạo ticket

## Slide 32

---

<a id="8-cmcg-ams--dashboard--user-guide-menubar-docx"></a>
## 8. CMCG_AMS_[Dashboard] User Guide Menubar.docx

|  | **<****Dashboard****>** |
| --- | --- |

| <Dashboard> |  |  |
| --- | --- | --- |

User Guide 

Menu bar Dashboard

**Version****: ****0****1**

| **Issue****d**** Status:** | Issued |
| --- | --- |
| **Issued**** Date:** | 01-23-2024 |
| **Owner:** | Bộ phận TDX |
| **Author:** | ltphuong6 |
| **Location:** | <Project Repository> |
| **Confidential Class:** | Internal Use |

| **Date****:** | <30-01-2024> |
| --- | --- |
| **Approved by:** | <ntson4> |
| **Signature:** |  |

**Review Information**

| **Role** | **Required / Suggested** | **Comment** |
| --- | --- | --- |
|  |  |  |
|  |  |  |

**Approval Information**

| **Approver Name** | **Role** | **Date** (mm-dd-yyyy) | **Revision** | **Comment** |
| --- | --- | --- | --- | --- |
|  |  |  |  |  |
|  |  |  |  |  |

**Revision History**

| **Revision** | **Date** (mm-dd-yyyy) | **Description** | **Revised by** | **Reviewer** | **Date** (mm-dd-yyyy) |
| --- | --- | --- | --- | --- | --- |
| *[**0.1**]* | *[**23**-01-2024**]* | *[First **Version**]* |  |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |

**T****able of Content**

1	Introduction	2

2	Menu bar mới	2

2.1	Tab Dashboard	2

2.2	Tab Delivery	2

2.3	Tab Task	3

2.4	Tab Setting	4

Introduction

Với việc đẩy mạnh phát triển các tính năng trên hệ thống Dashboard, phục vụ cho các hoạt động báo cáo và số hóa quy trình nghiệp vụ các phòng ban, menubar (thanh điều hướng) hiện tại của hệ thống Dashboard chưa đáp ứng được điều này. 

Vì vậy, dự án phát triển thiết kế menu bar mới cho hệ thống để việc phát triển các tính năng dễ dàng hơn đồng thời tối ưu trải nghiệm người dùng khi sử dụng hệ thống.

Mục đích của tài liệu này nhằm giới thiệu và hướng dẫn người dùng sử dụng menubar mới trên hệ thống Dashboard.

Menu bar mới 

Tab Dashboard

Tab Dashboard tổng hợp các module báo cáo bao gồm các module Global, Sales, Delivery và 1 module Dashboard settings để thiết lập cài đặt liên quan đến báo cáo.

*Với modules Global, Sales, Delivery:*

Người dùng click vào icon Expand “>” ở các tab trên cột menu. Khi đó, hệ thống hiển thị menu các sub-tab  như sau:

*Với module** Dashboard Setting:*

Admin truy cập vào module Dashboard Setting > Hệ thống hiện thị các section như bên dưới

Tab Delivery

Tab Delivery: Tổng hợp các module về vận hành Delivery bao gồm các modules như mục 2 trong ảnh.

*Với **Mod**ule Project List:*

- Người dùng chọn tab Project List trên thanh side menu bar 

- Hệ thống hiển thị bảng danh sách các dự án 

- Người dùng click vào hyperlink tên dự án muốn xem chi tiết ở bảng.

- Hệ thống hiển thị màn Project details gồm các các sub-tab như ảnh:

Trong đó:

- Phần Project Inputs gồm các section như sau:

- Phần Project Information gồm các section như sau:

Tab Tasks

Tab Tasks: Tập trung các module liên quan đến request và phê duyệt (VD: Phê duyệt mở/cập nhật dự án)

Tab Setting

Tab Setting: Tập trung các module setting hệ thống Dashboard

					

| CMC Global | Internal Use | Page 1/1 |
| --- | --- | --- |

| CMC Global | Internal Use | Page ii |
| --- | --- | --- |

---

<a id="9-close-project-request--đóng-dự-án--pptx"></a>
## 9. Close project request (Đóng dự án).pptx

## Slide 1

Dashboard – Module Close project request

July 2025

User Guide_PM / QA / SEPG

## Slide 2

Nội dung

03

02

01

Tổng quan module Close project request

Hướng dẫn thao tác trên hệ thống Dashboard

Q&A

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE CLOSE PROJECT REQUEST

1.2. Đối tượng người dùng

## Slide 4

1.1

MỤC ĐÍCH

Hệ thống Dashboard đã số hoá và đưa vào hoạt động Module Mở dự án, Cập nhật dự án 
Việc số hoá form Đóng dự án giúp tối ưu effort của PM và các bộ phận liên quan trong quy trình đóng dự án

## Slide 5

1.2

Tổng quan workflow phê duyệt Đóng dự án

Flow hiện trạng (PM)

Flow thay đổi (PM)

## Slide 6

1.2

Tổng quan workflow phê duyệt Đóng dự án

Flow thay đổi

## Slide 7

1.3

Đối tượng người dùng & phân quyền

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được thao tác toàn bộ trên hệ thống
PM
Tạo request đóng dự án cho các dự án mà họ là PM
View toàn bộ request đóng dự án cho các dự án mà họ là PM
QA
Phê duyệt việc đóng dự án của các dự án mà họ là QA và được assign để phê duyệt
SEPG
Quản lý các dự án cần đóng quyền Jira, Sharepoint khi request đóng dự án đã được approved

## Slide 8

Tạo request đóng dự án

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

View request đóng dự án trên hệ thống Dashboard

Checklist các dự án cần đóng quyền sharepoint, jira của SEPG

## Slide 9

2.1

Tạo request đóng dự án

Khi dự án đã hoàn thành, bàn giao sản phẩm cho khách hàng, hoặc, dự án buộc phải kết thúc giữa chừng
-> User (PM) vào hệ thống Dashboard để khởi tạo yêu cầu đóng dự án
User chọn Task > Project request > Close request

## Slide 10

2.1

Nhập data vào form Close project request

Có 3 phần chính trong form Close project request:
Overview
Detail
Case study
Đầu tiên, user (PM) phải chọn Project cần tạo request đóng dự án
Danh sách Project mà user có thể chọn là danh sách Project user đó là PM

## Slide 11

2.1

Nhập data vào form Close project request

Project information, MVV list là toàn bộ thông tin liên quan đến dự án và MVV được gán vào dự án
User phải chọn Close Type và QA (thông tin này sẽ quyết định được user phê duyệt Close project request)
-  Close type:
   + Terminated
   + Completed contract
-  QA: 
    Droplist QA là danh sách QA thuộc dự án mà user đã chọn

## Slide 12

2.1

Nhập data vào form Close project request

KPI result là kết quả và % hoàn thành của các chỉ số KPI
Proportion của các chỉ số có kết quả là N/A sẽ được phân bổ cho các chỉ số còn lại
Validate: Result của bất kỳ chỉ số nào là Data is not available thì Close project request sẽ không hợp lệ để PM submit
Complete Planning and Opening procedure được sync từ Project information
Hoặc user có thể điền khi khởi tạo Close project request

## Slide 13

2.1

Nhập data vào form Close project request

Project > Project information > Project bonus penalty
User (QA) có thể input data vào Complete planning and opening procedure -> <Save changes>

## Slide 14

2.1

Nhập data vào form Close project request

Tab Detail:
Milestone & Deliverables:  những thông tin được đồng bộ từ Jira của những ticket có issue type = Delivery & status khác Cancel
NCs Report: thông tin được đồng bộ từ Jira của những ticket có issue type = NC & status = Done
*Lưu ý: Để hoàn thiện việc đóng dự án, các ticket phải ở các trạng thái hoàn thành. 
NC sẽ chỉ tính điểm cho những ticket ở trạng thái Done

## Slide 15

2.1

Nhập data vào form Close project request

Tab Case study:
Customer information:  những thông tin liên quan đến khách hàng của dự án
Case study description:
     + The customer
     + Services & Domain
     + Business needs
     + Challenges
     + Solutions
     + Benefits
     + Technology used
User phải điền đủ vào tất cả trường này

## Slide 16

2.1

Nhập data vào form Close project request

Sau khi user điền đầy đủ thông tin vào form Close project request:
Save: Lưu lại toàn bộ thông tin đã điền
Cancel: User không muốn lưu thay đổi / thông tin đã điền
-> Hệ thống trả lại data cũ
Submit: Chuyển request sang trạng thái phê duyệt
Delete: Xoá request

## Slide 17

2.2

Phê duyệt Close project request - QA

User (QA) vào Task > Project request > View details task phê duyệt của mình với Request type = Close

## Slide 18

2.2

Phê duyệt Close project request - QA

***QA chỉ có thể thay đổi data của Penalty project bonus
Approve: user phê duyệt thành công
-> Dự án chuyển status sang Closed
Reassign: assign phê duyệt cho 1 user khác
Reject: user comment trước khi reject
-> Request chuyển về trạng thái Draft (PM nhận được thông báo để điều chỉnh request)

## Slide 19

2.3

Checklist danh sách dự án cần đóng quyền của SEPG

User (SEPG) lọc theo Request type = Close và permission status để tìm được dự án:
Permission status = Accessible: dự án được đánh dấu là chưa cắt quyền Jira, Sharepoint
Permission status = Access denied: dự án được đánh dấu là đã cắt quyền Jira, Sharepoint

## Slide 20

2.3

Checklist danh sách dự án cần đóng quyền của SEPG

User (SEPG) tick vào box để đánh dấu dự án đã được cắt quyền Jira, Sharepoint
***Box này sẽ chỉ xuất hiện trên request đóng dự án đã được approved

## Slide 21

03

Q&A

## Slide 22

---

<a id="10-phase-11-tích-hợp-c-code-gitlab--project-overview-pptx"></a>
## 10. Phase 11 Tích hợp C-Code-Gitlab, Project Overview.pptx

## Slide 1

Dashboard – Module enhancement

May 2025

User Guide_ QA/ PM/SALE

## Slide 2

Nội dung

02

01

Tích hợp C-codeX

Acceptance milestones

KPI’s Operation

03

Feedback & assessment

04

## Slide 3

1.1. Mục đích

01

Tích hợp C-codeX

1.2. Các màn hình ảnh hưởng

1.2.1 Project information

1.2.2 Project overview

1.2.3 Project statistics

## Slide 4

1.1

MỤC ĐÍCH

Với sự phát triển của C-codeX, cho phép Developer được sử dụng AI (C-codex) để hỗ trợ trong việc phát triển phần mềm -> C-codeX ghi nhận data cho việc sử dụng AI trong việc Coding của Developer
Tích hợp C-codeX nhằm mục đích đưa những data về việc sử dụng AI lên các màn tổng quan dưới dạng view theo Project, DU, G 

## Slide 5

1.2

Các màn hình ảnh hưởng

1.2.1 Project information

GIT: Sau khi GIT và Dashboard của dự án được tạo ra, phía PM sẽ sang GIT lấy id của GIT và điền vào field GIT. 
Lưu ý:
-     1 GIT được gắn            với nhiều Project
1 Project cũng có thể chứa nhiều GIT
PM, QA phải verify GIT trước khi gắn vào Project
Nếu xoá GIT cũ & thay bằng GIT mới -> data của GIT cũ sẽ không hiển thị ở dự án đó nữa
Data C-codeX của dự án sẽ được đồng bộ sang dự án trên Dashboard tương ứng

## Slide 6

1.2

Các màn hình ảnh hưởng

1.2.2 Project overview

Hiện trạng: 
Các chỉ số được hiển thị data in month & accumulated trong cùng 1 box
Thay đổi:
User sử dụng button switch tab để xem data in months HOẶC accumulated
Hiển thị 2 chỉ số AI index in & AI supporting (được đồng bộ từ C-codeX)

## Slide 7

1.2

Các màn hình ảnh hưởng

Các chỉ số được đồng bộ từ C-codeX về Dashboard:
Line of code generated
Line of code accepted
AI index = Line of code generated / (200*22)
Line of code generate: số dòng code được AI tạo ra dựa theo câu hỏi của Dev
Sau khi quy đổi: AI index sẽ hiển thị dưới đơn vị MM

AI supporting = Line of code accepted / (200*22)
Line of code accepted: số dòng code được Dev sử dụng trong việc coding
Sau khi quy đổi: AI supporting sẽ hiển thị dưới đơn vị MM
NOTE: 200 line of code = 1 MD
            22 là số ngày công trung bình trong tháng

## Slide 8

1.2

Các màn hình ảnh hưởng

1.2.3 Project statistics

Hiện trạng: PENDING đợi phần Enhance Project statistics

## Slide 9

02

Acceptance milestones

## Slide 10

03

Feedback & assessment

## Slide 11

04

KPI’s Operation

## Slide 12

04

Q&A

## Slide 13

---

<a id="11-phase-15-remove-member-1-pptx"></a>
## 11. Phase 15 Remove Member 1.pptx

## Slide 1

Dashboard – Module Remove Member ra khỏi dự án

October 2025

User Guide_PM / AMS

## Slide 2

Nội dung

04

02

01

Tổng quan module Remove Member

Hướng dẫn thao tác trên hệ thống Dashboard

Q&A

03

Các Module khác được enhance trong phase 15

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE REMOVE MEMBER

1.2. Tổng quan workflow module Remove Member

## Slide 4

1.1

MỤC ĐÍCH

Hệ thống hỗ trợ PM remove member ra khỏi dự án và đánh dấu ngày remove nhân sự
Hỗ trợ gửi email report nhân sự đã out dự án cho phía IT để IT cắt quyền vào source code, tài liệu,…

## Slide 5

1.2

Tổng quan workflow Remove Member

## Slide 6

1.3

Đối tượng người dùng & phân quyền

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được thao tác toàn bộ trên hệ thống
PM
Remove member ra khỏi dự án
QA
Xuất báo cáo danh sách project member status
IT
Nhận email danh sách nhân sự out khỏi dự án trong tháng và danh sách nhân sự có dấu hiệu out dự án

## Slide 7

Remove member ra khỏi dự án

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

Project Member Status report

Hiển thị màn hình financial margin của dự án tại màn hình Project Overview

Thêm chart financial margin tại màn hình Project statistics

Phần phát triển mới:

## Slide 8

2.1

Remove member ra khỏi dự án

Đối tượng sử dụng
PM
ADMIN
Mục đích:
Cho phép PM remove nhân sự ra khỏi dự án

## Slide 9

2.1

Remove member ra khỏi dự án

B1: User truy cập vào Dashboard > Delivery > Project > Chọn project​
B2: Chọn nhân sự cần remove > Click check-box Removed
B3: Chọn remove date và click Save Changes để mark nhân sự remove và ngày remove

## Slide 10

2.1

Remove member ra khỏi dự án

Unremove Member: 
B1: Uncheck tại checkbox Removed
B2: Click Save Changes

## Slide 11

2.1

Remove member ra khỏi dự án

Log member history: 
B1: Click Member Log History
B2: Lọc theo time range/ Editor/ Action/ Member

## Slide 12

2.2

Export Project Member Status

Đối tượng sử dụng
QA
ADMIN
Mục đích:
Cho phép QA, Admin xuất báo cáo danh sách member in/out dự án trong khoảng thời gian đã chọn

## Slide 13

2.2

Export Project Member Status

B1: User truy cập vào Dashboard > Delivery > Delivery Reports > Project Member Status
B2: User chọn bộ lọc cần kiểm tra
B3: Click export ​
 

## Slide 14

2.2

Export Project Member Status

Chọn time-range cần lọc

Lọc project

Export

Lọc member

Lọc theo member status

## Slide 15

2.2

Export Project Member Status

File export chứa các thông tin như sau: 
Member name
Project Name
Group
Role
Effort Actual (Actual Timesheet)
Effort Calendar (Resource Allocation)
Removed
Removed Date
Active Status

## Slide 16

2.3

Email gửi IT danh sách nhân sự rời dự án hoặc có dấu hiệu rời dự án

Mục đích: Gửi danh sách nhân sự đã rời khỏi dự án trong tháng cho bộ phận IT để IT cắt quyền source code, tài liệu,… của dự án
To Address: 
Tần suất: Gửi ngày 25 hàng tháng
Nội dung file đính kèm bao gồm các danh sách:
Danh sách nhân sự đã xác nhận rời khỏi dự án trong tháng
Danh sách nhân sự có dấu hiệu rời khỏi dự án nhưng chưa được xác nhân chính thức. Bao gồm các trường hợp sau:
Đã mark remove nhưng vẫn có allocate
Chưa mark remove nhưng không có allocate trong vòng 30 ngày
Status là inactive nhưng chưa mark remove

## Slide 17

2.3

Email gửi IT danh sách nhân sự rời dự án hoặc có dấu hiệu rời dự án

Sheet Remove Member: Danh sách nhân sự rời dự án. Bao gồm các thông tin sau
Name
Project Name
Group
Effort Actual (Actual Timesheet)
Effort Calendar (Resource Allocation)
Allocated Start Date
Allocated End Date
Remove Date

## Slide 18

2.3

Email gửi IT danh sách nhân sự rời dự án hoặc có dấu hiệu rời dự án

Sheet Suspicion: Danh sách nhân sự có dấu hiệu đã rời khỏi dự án nhưng chưa được xác nhận chính thức
Bao gồm các trường hợp sau:
Đã mark remove nhưng vẫn có allocate
Chưa mark remove nhưng không có allocate trong vòng 30 ngày
Status là inactive nhưng chưa mark remove

## Slide 19

2.1

Remove member ra khỏi dự án

Đối tượng sử dụng
PM
ADMIN
Mục đích:
Cho phép PM remove nhân sự ra khỏi dự án

## Slide 20

2.1

Remove member ra khỏi dự án

B1: User truy cập vào Dashboard > Delivery > Project > Chọn project​
B2: Chọn nhân sự cần remove > Click check-box Removed
B3: Chọn remove date và click Save Changes để mark nhân sự remove và ngày remove

## Slide 21

2.1

Remove member ra khỏi dự án

Unremove Member: 
B1: Uncheck tại checkbox Removed
B2: Click Save Changes

## Slide 22

2.1

Remove member ra khỏi dự án

Log member history: 
B1: Click Member Log History
B2: Lọc 

## Slide 23

2.2

Export Project Member Status

Đối tượng sử dụng
QA
ADMIN
Mục đích:
Cho phép QA, Admin xuất báo cáo danh sách member in/out dự án trong khoảng thời gian đã chọn

## Slide 24

2.2

Export Project Member Status

B1: User truy cập vào Dashboard > Delivery > Delivery Reports > Project Member Status
B2: User chọn bộ lọc cần kiểm tra
B3: Click export ​
 

## Slide 25

2.2

Export Project Member Status

Chọn time-range cần lọc

Chọn project

Export theo bộ lọc
Effort Actual = 

## Slide 26

2.2

Export Project Member Status

File export chứa các thông tin như sau: 
Member name
Project Name
Group
Role
Effort Actual (Actual Timesheet)
Effort Calendar (Resource Allocation)
Removed
Removed Date
Active Status

## Slide 27

2.3

Project Overview Enhancement

Đối tượng sử dụng
Sales
PM
Admin
Mục đích:
Cho phép PM xem được margin tài chính của dự án theo mã dưới dạng %.

## Slide 28

2.3

Project Overview Enhancement

B1: User truy cập vào Dashboard > Delivery > Project > Chọn project ​
B2: Chọn Project Overview
B3: Chọn View Financial Margin

Actual: Lợi nhuận thực tế của MVV thuộc dự án dưới dạng %
Plan: Lợi nhuận theo PAKD của MVV dưới dạng %
Percentage of completion: Actual - Plan

## Slide 29

2.4

Project Statistics Enhancement

B1: User truy cập vào Dashboard > Project Statistics 
B2: Chọn project statistics

## Slide 30

Allocation Validation Settings – Enhance bảng Project Exception

03

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

Project Resources/ Delivery Resources

Enhance tính năng cũ

## Slide 31

3.1

Allocation Validation Settings

Đối tượng sử dụng
CDO
ADMIN
Mục đích:
Thêm thông tin % buffer cho dự án được exception
Thêm thông tin ngày buffer cho dự án được exception

## Slide 32

3.1

Allocation Validation Settings

B1: User truy cập vào Dashboard > Setting > Allocation Validation > Project Exception 
B2: Chọn Add/ Edit
B3: Thêm thông tin dự án exception và Percentage và Number of Extend dates

## Slide 33

3.2

Project Resources Enhancement

B1: User truy cập vào Dashboard > Delivery > Project List > Chọn Project đã được add vào Exception
B2: Chọn Project Resource
B3: Check remaining hours for allocation tính toán lại theo Percentage đã add tại màn Allocation Validation

## Slide 34

3.2

Project Resources Enhancement

B1: User truy cập vào Dashboard > Delivery > Resource Allocation > Add Plan
B2: Chọn dự án đã được add là exception
B3: Với số giờ allocate vượt quá số giờ plan theo tháng tại PAKD, kiểm tra Remaining (h) đã được tính theo % đã thêm exception

---

<a id="12-phase-6--phương-án-sản-xuất-pptx"></a>
## 12. Phase 6_ Phương án sản xuất.pptx

## Slide 1

Dashboard – Business Plan new version

Jun 2025

User Guide

## Slide 2

Nội dung

03

02

01

Tổng quan quy trình

Hướng dẫn thao tác trên hệ thống Dashboard

Một số lưu ý trong quá trình sử dụng

04

Q&A

## Slide 3

1.1

Mục đích

Số hoá thông tin Phương án sản xuất (Delivery Plan), để phục vụ cho việc quản trị thông tin effort và chi phí kế hoạch theo từng đơn vị
Số hoá thông tin Phương án doanh thu (Revenue Plan), phục vụ việc quản trị thông tin doanh thu và MM Bill kế hoạch theo từng đơn vị
Màn hình Business Plan sẽ tự động lấy số liệu doanh thu từ Billing Plan và Delivery Plan để tính toán, giảm thao tác nhập liệu cho user
Cơ sở để xây dựng dashboard quản trị hiệu quả tài chính dự án 

## Slide 4

1.2

Đối tượng người dùng

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được thao tác toàn bộ trên hệ thống
Sales
Nhập thông tin về doanh thu trên Revenue Plan và request phê duyệt
PMO
Hỗ trợ Sales và phân bổ Billing Plan
FC 
Kiểm soát thông tin điền trên Business Plan và phê duyệt
DUL
Nhập thông tin về chi phí trên Delivery Plan, kiểm tra thông tin doanh thu, chi phí, lợi nhuận kế hoạch của DU

## Slide 5

1.3

Workflow

## Slide 6

2.5

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan

Sau khi tạo Sub WO trên CRM, PMO sẽ vào hệ thống Dashboard Dashboard -> Billing Plan để nhập thông tin DU thực hiện Sub WO 

## Slide 7

2.5

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan

1. User chọn button Edit
2. User click button (+) để add DU thực hiện cho Sub WO Sale
3. User input thông tin Company Code – DU – Ratecard và dải MM Billable
4. User click Save để lưu thông tin đã nhập

Lưu ý: 
Nếu user chọn DU cùng G với BU trên sub WO -> hệ thống disable trường ratecard, data sẽ hiển thị theo dữ liệu setting ratecard trên Dashboard
Hệ thống sẽ không cho phép edit MM Billable cho tháng quá khứ
Hệ thống không cho phép edit MM Billable cho tháng hiện tại nếu tháng hiện tại quá ngày 28
Nếu user đã nhập MM Billable trong 2 trường hợp trên, hệ thống sẽ không cho phép xoá thông tin Company code – DU - Ratecard
 

Sales/PMO dựa trên WO có type = Sales, add DU thực hiện tương ứng với các vị trí mà DU có khả năng cung cấp

## Slide 8

2.5

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan

Khi nhập quá MM so với MM của Sub WO Sale, hệ thống không cho user Save thông tin và yêu cầu điều chỉnh lại
User có thể filter các data bị điền sai để chỉnh sửa lại

## Slide 9

2.5

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan

Hệ thống sẽ validate nếu user để trống thông tin Entity Key – DU – Giá bán ratecard khi ấn Save

## Slide 10

2.5

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan

User có thể switch sang Sales Revenue hoặc Delivery Revenue để xem doanh thu dự kiến 
Sales revenue = Unit Price x MM Billable x ratio của Pipeline
Delivery Revenue = Ratecard x MM Billable x ratio của Pipeline
Average Price của Sales = Tổng Sales Revenue / Tổng MM Billable
Average Price của Delivery = Tổng Delivery Revenue / Tổng MM Billable

Lưu ý: Hệ thống không tính toán data Revenue và Average Price của Sub WO Sales status = Cancel

## Slide 11

2.5

Hướng dẫn thao tác trên hệ thống Dashboard – Business Plan

User chọn Tab Delivery > Sub Tab Business Plan để xem danh sách các Business Plan
User ấn View Detail để xem thông tin chi tiết của Business Plan

Sau khi phía FC activate Project Code trên CRM -> Phía Dashboard sẽ tự động tạo ra 1 Business Plan version 1 và status = Draft

## Slide 12

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Business Plan

Thông tin chung của Business Plan

Thông tin doanh thu/chi phí/lợi nhuận kế hoạch của Business Plan

Thông tin tài liệu upload

Thông tin comment và lịch sử edit Business Plan

Export PDF

Submit phê duyệt

## Slide 13

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Revenue Plan

## Slide 14

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Revenue Plan

MM Bill: Tổng MM Bill của đơn vị trong mã vụ việc
Software production revenues: Tổng doanh thu sản xuất phần mềm của đơn vị trong mã vụ việc
Deduction: Giảm trừ doanh thu (chỉ hiển thị với đơn vị BU)
Onsite fee: Tổng doanh thu onsite của đơn vị trong mã vụ việc
Revenues from Equipment, Internet, Server,…: Tổng doanh thu thiết bị của đơn vị trong mã vụ việc
Other revenues: Tổng doanh thu khác của đơn vị trong mã vụ việc
Agency expenses: Chi phí thuê agency để kiếm hợp đồng (Chỉ hiển thị với đơn vị BU)

## Slide 15

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Revenue Plan

User có thể switch sang Revenue để xem doanh thu kế hoạch
Sales revenue = Unit Price x MM Billable x ratio của Pipeline
Delivery Revenue = Ratecard x MM Billable x ratio của Pipeline

Section Software Production Revenue Information lấy dữ liệu từ Billing Plan của Mã vụ việc tương ứng để hiển thị data

## Slide 16

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Revenue Plan

User ấn (+) để add dòng, sau đó điền thông tin tên doanh thu và số tiền theo từng tháng -> ấn Save để lưu thông tin

Other Revenues

## Slide 17

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Delivery Plan

## Slide 18

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Delivery Plan

MM Effort: Tổng MM Effort của đơn vị trong mã vụ việc
Direct Labor Cost: Tổng chi phí trực tiếp sản xuất phần mềm của đơn vị trong mã vụ việc (Chỉ tính nhân sự có type = In-housed)
Outsourcing Cost: Tổng chi phí thuê ngoài sản xuất phần mềm của đơn vị trong mã vụ việc (Chỉ tính nhân sự có type = In-housed)
Onsite expense: Tổng chi phí onsite của đơn vị trong mã vụ việc
Equipment, Internet, Server cost : Tổng chi phí thiết bị của đơn vị trong mã vụ việc
Overtime: Tổng chi phí OT của đơn vị trong mã vụ việc
Non-deductible input VAT: Tổng chi phí thuế VAT không được khấu trừ của đơn vị trong mã vụ việc
Other expenses: Tổng chi phí khác của đơn vị trong mã vụ việc

## Slide 19

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Delivery Plan

Tại section Resource Information, user ấn button (+) để add bản ghi, điền thông tin nhân sự kế hoạch trong dự án và effort kế hoạch của nhân sự đó

Thông tin nhân sự kế hoạch trong dự án bao gồm
Resource Type: Là nhân sự trong công ty hay nhân sự ảo
Resource Full Name: Tên nhân sự, nếu là nhân sự ảo thì có thể nhập tay
Location: Nơi nhân sự đó ký hợp đồng với công ty
Employee Type: Nhân sự trong công ty hay CTV thuê ngoài
Original Gross Salary: Lương nguyên tệ theo location của nhân sự
Gross Salary (VND): Lương theo VND của nhân sự
Position: Position được khách hàng thuê
Role: Role của nhân sự trong dự án 

Sau khi điền thông tin xong, user ấn Save để lưu thông tin

## Slide 20

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Delivery Plan

User có thể switch view sang Labor cost để xem chi phí kế hoạch của từng nhân sự trong delivery plan

Công thức tính labor cost
Labor Cost = Labor Rate x MM Effort
Nếu nhân sự có employee type = In-housed  
Labor rate = Salary index x Gross Salary (VND) + Expense Index
Giá trị của Salary index và Expense index dựa theo location của nhân sự đó
Nếu nhân sự có employee type = Outsourced
Labor rate = Gross Salary (VND)

## Slide 21

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Delivery Plan

Nếu mã vụ việc đã được mở thành dự án trên Dashboard và nhân sự đã được allocate/có actual timesheet cho mã vụ việc -> Khi làm delivery plan, user có thể ấn load data from -> hệ thống tự động load data allocate/actual timesheet của nhân sự lên trên màn delivery plan

Tính năng load data from có các giá trị sau
Resource Allocation: Load theo data allocation (Không bao gồm OT) của nhân sự trong mã vụ việc
Actual Timesheet: Load theo data effort Actual Timesheet đã được approved của nhân sự trong mã vụ việc
Available: Load theo data available của nhân sự = Tổng số h làm việc tiêu chuẩn trong tháng – Tổng số h allocate của nhân sự trong tháng
Book: Load theo data book của nhân sự trong mã vụ việc

## Slide 22

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Delivery Plan

User ấn (+) để add dòng, sau đó điền thông tin tên chi phí và số tiền theo từng tháng -> ấn Save để lưu thông tin

## Slide 23

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Delivery Plan

Thông tin exchange rate theo location được dùng để tính Gross Salary (VND) của nhân sự

Lưu ý: User có thể edit thông tin Exchange Rate theo từng location, tuy nhiên việc này sẽ thay đổi data gross salary (VND) các nhân sự thuộc tất cả các delivery plan trong business plan tương ứng

## Slide 24

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Business Plan

Với version mới, trên màn hình Business Plan, AM chỉ cần phải edit các thông tin sau
CIT and VAT (%)
Incentive rate (%)
Project bonus/MM
Billable rate norm (%)

Các thông tin còn lại hệ thống sẽ tự động tính toán theo data user đã nhập trên màn hình Revenue Plan và Delivery Plan

## Slide 25

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Business Plan

Section
Chỉ số
Công thức tính
Unit price & MM Bill
Unit Price
Unit Price Total = Unit Price BU
Unit Price BU = Tổng doanh thu sản xuất phần mềm của BU / Tổng MM Bill của BU
Unit Price DU = Tổng doanh thu sản xuất phần mềm của DU / Tổng MM Bill của DU
Trong đó
Doanh thu sản xuất phần mềm của BU = Σ(Đơn giá BU bán cho khách hàng x MM Bill x Pipeline Status Ratio)
Doanh thu sản xuất phần mềm của DU = Σ(Đơn giá DU bán cho BU x MM Bill x Pipeline Status Ratio)
Unit price & MM Bill
MM Effort
MM Effort Total = Σ MM Effort DU
MM Effort BU = Σ MM Bill BU
MM Effort DU = Σ MM Effort của DU theo data user nhập trong Delivery Plan – Resource Information
Unit price & MM Bill
MM Bill
MM Bill Total = MM Bill BU 
MM Bill BU = Σ MM Bill DU
MM Bill DU = Σ MM Bill DU theo revenue plan của DU
Revenues
Revenues from work delivered
Total revenues from work delivered = Σ (BU + Internal + DU) revenues from work delivered
(BU) Revenues from work delivered = Exchange rate * software from development
Internal revenues from work delivered = - Σ (DU) revenues from work delivered
(DU) Revenues from work delivered = Σ(Đơn giá DU bán cho BU x MM Bill x Pipeline Status Ratio)
Revenues
Deduction
Total deduction = Σ (BU+ Internal + DU) deduction
(BU) Deduction = Σ(Đơn giá BU bán cho khách hàng x MM Bill x Pipeline Status Ratio) - (BU) Revenues from work delivered
Revenues
Onsite Fee
Total onsite fee = Σ (BU + Internal + DU) onsite fee
(BU)/(DU) onsite fee = Data onsite fee user nhập trong revenue plan của đơn vị tương ứng
(BU)/(DU) onsite fee là hoạt động onsite được Khách hàng đồng ý thành toán. Onsite fee có thể bao gồm vé máy bay, perdiem, phí lưu trú, đi lại...
Revenues
Revenues from Equipment, Internet, Server
Total Revenues from Equipment, Internet, Server = Σ (BU + Internal + DU) revenues from Equipment, Internet, Server,...
(BU)/(DU) Revenues from Equipment, Internet, Server = Data Revenues from Equipment, Internet, Server user nhập trong revenue plan của đơn vị tương ứng
(BU)/(DU) Revenues from Equipment, Internet, Server là doanh thu từ cho thuê thiết bị, cung ứng dịch vụ đường truyển, xây dựng ODC
Revenues
Other revenues
Total other revenues = Σ (BU + Internal + DU) other revenues
(BU)/(DU) other revenues = Data other revenues user nhập trong revenue plan của đơn vị tương ứng
(BU)/(DU) other revenues là các doanh thu khác (nếu có) (ví dụ, BOT)

## Slide 26

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Business Plan

Section
Chỉ số
Công thức tính
Cost of sales
Cost of sales 
Total cost of sales = Σ (BU + Internal + DU) cost of sales
(BU) cost of sales = (BU) cost of sales (ratecard DU)
Internal cost of sales = internal cost of sales (ratecard DU)
Cost of sales
Cost of sales (Ratecard DU)
(BU) cost of sales (ratecard DU) = Σ (DU) revenues from work delivered
Internal cost of sales (ratecard DU) = internal revenues from work delivered
Selling expenses
Selling expenses
Total selling expenses = total incentives + total agency expenses
(BU) selling expenses = (BU) incentives + (BU) agency expenses
Selling expenses
Incentives
Total incentives = Σ (BU + Internal+ DU) incentives
(BU) incentives = (BU) revenues from work delivered * (BU) incentives rate
Selling expenses
Agency expenses
Total agency expenses = Σ (BU + Internal + DU) agency expenses
(BU) agency expenses là chi phí hoa hồng chi trả cho môi giới kinh doanh (agency, advisor)
Delivery expenses
Direct Labor cost
Total direct labor cost = Σ (BU + Internal + DU) direct labor cost
(DU) direct labor cost là chi phí nhân sự sản xuất trực tiếp cho dự án, dựa theo data user input trong Delivery Plan – section Resource Information
Delivery expenses
Outsourcing cost
Total outsourcing cost = Σ (BU + Internal + DU) outsourcing cost
(BU)/(DU) outsourcing cost là chi phí nhân sự thuê ngoài trọn gói, dựa theo data user input trong Delivery Plan – section Resource Information
Delivery expenses
Equipment, Internet, Server cost
Total Equipment, Internet, Server cost = Σ (BU + Internal + DU) Equipment, Internet, Server Cost
(BU)/(DU) Equipment, Internet, Server cost = Data Equipment, Internet, Server cost user nhập trong delivery plan của đơn vị tương ứng
(BU)/(DU) Equipment, Internet, Server cost là Chi phí mua thiết bị, internet, server, license, phần mềm, ... phục vụ dự án

## Slide 27

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Business Plan

Section
Chỉ số
Công thức tính
Delivery expenses
Onsite expenses (Onsite allowance, perdiem, travelling, accommodation, etc.)
Onsite development cost (Onsite allowance, perdiem, travelling, accommodation, etc.): Chi phí onsite, công tác của nhân sự thực hiện dự án.
Chi phí công tác có thể bao gồm vé máy bay, perdiem, phí lưu trú, đi lại, ... và cần bổ sung bảng tính chi tiết.
Chi phí onsite bao gồm phụ cấp onsite, gửi xe, ...
Total onsite development cost = Σ (BU + Internal + DU) Onsite development cost
(BU)/(DU) onsite development cost = Data onsite development cost user nhập trong delivery plan của đơn vị tương ứng
Delivery expenses
Project bonus
Total project bonus = Σ (BU + Internal + DU) project bonus
(DU) project bonus = (DU) MM bill * (DU) project bonus/MM
Delivery expenses
Overtime
Overtime: Chi phí làm thêm giờ (nếu có)
Total overtime = Σ (BU + Internal + DU) overtime
Delivery expenses
Non-deductible input VAT
Non-deductible input VAT: Chi phí thuế Giá trị gia tăng/thuế tiêu thụ đầu vào không được khấu trừ - áp dụng các dự án với khách hàng tại Việt Nam
Total non-deductible input VAT = Σ (BU + Internal + DU) non-deductible input VAT
(BU)/(DU) Non-deductible input VAT = Data Non-deductible input VAT user nhập trong delivery plan của đơn vị tương ứng
Delivery expenses
Other expenses
Total other revenues = Σ (BU + Internal + DU) other expenses
(BU)/(DU) other expenses = Data other expenses user nhập trong revenue plan của đơn vị tương ứng
Tax expenses
Tax expenses
Total tax expenses = total revenues * total CIT and VAT
(BU) tax expenses = (BU) revenues * total CIT and VAT
(DU) tax expenses = (Du) revenues * total CIT and VAT
Tax expenses
CIT and VAT (%)
CIT and VAT (if any) (%): Thuế Thu nhập doanh nghiệp hoặc thuế Giá trị gia tăng phải chịu (áp dụng với các Hợp đồng có rủi ro về thuế do không ký theo mẫu của CMC Global)

## Slide 28

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Business Plan

Section
Chỉ số
Công thức tính
Margin
Direct Margin
Direct margin = Revenues - Cost of sales - selling expenses - delivery expenses - tax expenses
Margin
Direct Margin before Incentives and Project bonus
Direct margin before incentive and project bonus = direct margin + incentives + project bonus
Margin
Allocation of pool and unbillable
Total allocation of pool and unbillable = Σ (BU + Internal + DU) allocation of pool and unbillable
(DU) allocation of pool and unbillable = ((DU) Direct labor cost / (DU) billable rate norm) - (DU) Direct labor cost
Margin
Indirect margin
Indirect margin = Direct margin - allocation of pool and unbillable
Margin
Direct margin %
Margin
Direct Margin before Incentives and Project bonus %
Margin
Indirect margin %

## Slide 29

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Business Plan

Section
Chỉ số
Công thức tính
Reference
Average delivery expense
Reference
Average direct labor cost/MM
Reference
Billable rate(%)
Reference
Productivity
Reference
Efficiency
Reference
Incentives rate (%)
Incentives rate (%): Tỷ lệ incentive của dự án
Reference
Project bonus/MM
Hệ số thưởng sản xuất cho 1 MM Bill tương ứng
Reference
Billable rate norm (%)
Bill rate norm (%) là định mức billable rate theo Kế hoạch ngân sách

## Slide 30

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Business Plan

Sau khi thông tin được điền đầy đủ, AM ấn button Submit -> Hệ thống tạo các step và task phê duyệt 
Tất cả task phê duyệt trong 1 step cần được approve mới có thể chuyển sang step tiếp theo

## Slide 31

2.6

Hướng dẫn thao tác trên hệ thống Dashboard – Business Plan

User cần replan Business Plan trong trường hợp có sự thay đổi về statement of work của mã vụ việc hoặc dự án phát sinh chi phí nhiều so với kế hoạch

## Slide 32

04

Q&A

## Slide 33

---

<a id="13-runbook-xlsx"></a>
## 13. Runbook.xlsx

## Sheet: Ruling

	Module	Tab	Sub Tab	Content	Tag
1	General Issue			Job đồng bộ của Dashboard thường là 1 tiếng 1 lần
			Gắn link Jira vào Dashboard	1 dự án trên Dashboard có thể add nhiều link Jira, nhưng nếu link Jira đã được gán 1 dự án trên Dashbooard thì không thể gán sang dự án khác trên Dashboard. Với link Jira thứ 2 được add vào Dashboard thì sẽ không có nút đồng bộ ngay như link 1, mà được gán thẳng vào Save Change	Jira

			Không submit được tờ trình	Do trường ẩn, bật F12 check. Nhờ HNO config lại
			Check group trên PMS	Đăng nhập tài khoản crm-system -> chọn icon bánh răng -> chọn user management -> search

2	Dashboard 	Delivery overview *	General
2			Project Billable	Hiển thị các Project mà DU đó đang host. Hiển thị bill rate in month theo số %.
			Project Statistics	3 chỉ số chính Acc Timeliness/Bugrate/Leakage rate. 

3		Delivery report	Đối tượng	DUL,PMO,HR,FIN,QA
			Resource allocation, Actual Timesheet by Effort, Actual Timesheet Effort by Role	HR dùng để tính KPI cho nhân sự. -> Compare với màn Actual Timesheet, Delivery Resource
			Project Allocation, Project Billable, MVV List in Project, Project KPI	QA tính KPI cho dự án, export dữ liệu. Hiện CMC có 3 loại dự án: dev, operation,migration. KPI dự án migration ăn theo dự án dev, còn dự án operation tính riêng

			Resource allocation (phân bổ nhân sự)	- Có các trường thông tin của từng Employee theo tháng. Startdate,enddate, role, dự án , % allocation  - Có thể Export
			Project Allocation (Phân bổ dự án)	 - Có các thông tin về Project, loại hình project, billable, bill rate, allocation, actualtimesheet theo dự án. Có thể lọc theo MVV, DU, month  - Có thể export
			Resource Project (tỷ lệ lấp đầy nhân sự vào dự án)
			Project Billable	- Thông tin Billable của dự án theo từng tháng, effort, bill rate  - Export được theo dự án
			Billable Summary	- Thông tin Billable của toàn bộ các DU,G của các loại dự án (Investment, Profit,...) của tháng được export
			Actual Timesheet Effort	Thông tin effort của nhân sự, tách record nếu ở nhiều project
			Resource Allocation New	Giôngs kiểu màn Resource Allocation

			Actual Timesheet Effort by Role	- Có thông tin về Effort (số giờ làm việc thực tế), WT, OT, ManPower (tổng giờ làm việc theo planning của team) của từng nhân sự theo Role. Ví dụ 1 nhân sự tham gia 2 dự án khác nhau thì sẽ tách 2 record riêng  - Có thể export
			MVV List in Project	- Danh sách MVV
			Business Plan Report	 Chủ yếu xem doanh thu, margin của các dự án
			List Project (single search)	 Xem thông tin PM,DU, start-endate của dự án được tìm kiếm
			Delivery Timesheet Report	Effort theo plan và thực tế của các dự án, lọc theo tuần
			Project Dev Kpi Reports	Các chỉ số KPI của tất cả các dự án dev (leakage rate, bugrate,...)
			Project Op Kpi Reports	Các chỉ số KPI của tất cả các dự án Op (leakage rate, bugrate,...)
			Performance Score	Performance score của từng nhân sự, theo dự án, search theo period time
			Performance Score By Day	Performance score của từng nhân sự, theo dự án,từng ngày   Ví dụ search 1-5/10/2025 thì sẽ ra 5 cái record

		Available resource		FIN,SEPG có quyền view nhưng không được export
		Dashboard setting		Admin có quyền view còn SEPG có quyền setting
4
	Delivery

		Work Order (Sale điền WO bên CRM)		Tự động sync khi AM tạo WO bên CRM, có trạng thái Draft/Issue/Cancel. Có thông tin WO bố, AM, DU host, G host
				Khi type là Issue: Ghi nhận doanh thu nếu sub WO đã được issue  Type là Draft/Cancel: Không ghi nhận doanh thu  ▪WO không thể chuyển issue nếu như chưa tạo Sub WO.  ▪Sub WO không thể issue nếu WO cha chưa được issue  ▪WO bên đầu CRM chỉ có cho Sale, còn sub WO cho DU là bên PMO điền ở Billing Plan/ Dashboard

5		Billing plan (PMO vào điền sub WO)		- Có thể edit thông số.  - Không thể edit MM billable quá khứ và sau ngày 28  - Nếu phân bổ quá MM so với MM của sub WO, hệ thống sẽ không cho Save  - Nếu để trống thông tin trường Entity Key – DU – Ratecard, hệ thống sẽ không cho Save và yêu cầu bổ sung
				AM tạo Pipeline trên CRM -> tự động gen 1 WO, AM có thể thêm sub WO để chia dự án thành nhiều phần -> tự động sync sang màn WO bên Dash board -> AM có thể vào màn billing plan để phân bổ DU tham gia và MM, điền Rate Card. (mỗi dự án chỉ có 1 DU host)
				Billing Plan - G1_AMS - CMC Global Wiki

6
			Quyền rate cho PM,QA	Chỉ có DUL mới được rate	rate điểm PM, QA

7
		Delivery resource	Do DUL allocate từ đầu tháng (là Timesheet planning)
		Actual timesheet	(Là Timesheet thực tế mà nhân sự làm, PM approve Timesheet bên này)
			Quyền view DUL	DUL chỉ nhìn được dự án/ nhân sự mà DU đó host
8			Rule	- Màn này lấy data từ Project Resource. Khóa rate điểm và approve theo kỳ công sau ngày 28. E.g: Sau ngày 28, khóa rate điểm và approve cho kỳ công 25/5-26/6  - Time sheet được approve sẽ đẩy sang màn Actual Billable  - Chỉ được approve timesheet <= ngày hiện tại  - Chỉ cho phép approve theo member thuộc dự án  - Approve thành công thì chuyển từ cam sang xanh

		Actual billable (Role sale)		Các Timesheet được approve bên Actual Timesheet sẽ đẩy sang bên Actual Billble. Đến ngày 28, rule hệ thống tự động approve

9		Business plan (Role sale) -PAKD	AM tạo Ticket mở Project bên CRM -> FC approve -> Có mã Project code -> hệ thống tự động gen 1 cái PAKD có MVV = Project code  ở màn Business Plan	Preparator: Backup, DU lead đang host muốn xem dự án -> add vào để có quyền xem/edit  Chỉ add DUL vào phần này để điền thông tin DU tham gia. Do module Delivery plan chứa thông tin lương nhân viên nên PM cũng không được vào điền
				- Chỉ có AM host hoặc được add vào AM information mới có quyền edit và tạo version mới

				- PMO phải chia Blling Plan cho DU thì DU đó mới hiển thị cột tại Business Plan
				- Các trường chỉ FIN được nhập: Section Delivery expense, trừ trường Project Bonus và Other Expense  - Trường CIT and VAT lấy từ Setting, nhưng user có thể tự thay đổi  - Các trường Incentives rate (%), Project bonus/MM, Billable rate norm (%) thuộc Section Reference đều lấy từ Master Data setting, FIN có thể điền số thay đổi giá trị 
				- Khi bị reject, Biz Plan sẽ về lại trạng thái Draft để AM edit và submit lại
	Project List	General issue	Project Category	Màn Project list thêm 1 trường mới là Project Category bao gồm 3 loại Operation, Development và Migration (đang chạy theo KPI của Development) => thông tin loại dự án sẽ lấy khi mở dự án, trường này AM/ PM sẽ chọn khi mở dự án chứ không lấy thông tin đồng bộ từ CRM về
			Nếu chuyển category dự án đang chạy từ Development sang Operation/Nếu dự án đang là Type Development, mà KH không muốn end dự án mà muốn chuyển sang type Operation để tiếp tục chạy dự án 	Về quy trình, team phải tạo 1 dự án mới với category mới. Bên cạnh đó, (PM, Admin) có quyền thay đổi data trong màn Project information, và nếu dự án vẫn chuyển category từ Development sang Operation thì những data trước đây sẽ được match và hiển thị với KPI type là Operation.  VD: Dự án là Development, đã có đủ data của 5 chỉ số KPI (CSS, PCV, Bug rate, Leakage rate, Timeliness). Sau đó, dự án bị đổi sang category là Operation, hệ thống sẽ ghi nhận CSS, PCV đã được ghi nhận trước đây, còn lại Bug rate, Leakage rate, Timeliness vẫn tồn tại trong database của dự án đó nhưng không được hiển thị trên UI	Chuyển category dự án, chuyển type dự án

10
		Project Overview
			Quyền PM	1 dự án chỉ có 1 PM, ăn theo PM cuối cùng được add vào dự án. PM cũ vẫn có quyền view dự án. Nhưng chỉ PM mới có quyền thao tác.	quyền PM
11		Project member	Logic	- Start date và End date ghi nhận theo thời gian được allocate. Quyền PM cũng sẽ ăn theo PM được allocate lâu nhất 

		Project resource	Ruling	Hiển thị resource theo dự án. Data ở màn này là Inputs cho màn Actual Timesheet. Màn này khóa allocate theo tháng tại 23h ngày 28. E.g: Tại ngày 28 khóa allocate từ ngày 1-30 cùng tháng
12				Phải hoàn thành PAKD để có MM plan. Nếu không remaining hours sẽ âm và không allocate được thêm     Nếu có MVV mà chưa có PAKD thì chỉ có thể allocate trong 20 working days kể từ start date của MVV, còn có PAKD đã chạy hết luồng approve rồi thì allocate như bình thường. Ngoài ra ko thể allocate vượt MM của PAKD     Nếu có nhiều version ăn theo version cuối cùng được approve  e.g: Nếu version 2 chưa hoàn thành thì hệ thống vẫn nhận version 1

		Project KPI
13		Project input	Business Rule	PCV, CSS do QA nhập, các chỉ số khác đồng bộ từ Jira. Job đồng bộ 4h sáng hàng ngày
14

	Task	Project request		- Các status:  Draft -> Verification (PM approve) -> Peer review (CDO approve)  - Logic hiển thị chung trong màn Task: Ticket đến chân ai thì mới hiển thị, sau khi approve người đó vẫn được xem Ticket. Các DU tham gia thường nằm trong luồng phê duyệt nên cũng có quyền xem Ticket
		Open request (AM, PMO Open, PM approve)		Tài liệu luồng update/open dự án - G1_AMS - CMC Global Wiki
				Sale tạo Project code -> Fin approve -> có mvv-> sale sang Dashboard Open request mới có Project tại màn Project list
				- Sale có quyền Open và submit để PM duyệt  - PM có quyền edit khi ở trạng thái Verification và có quyền submit
			Luồng tạo Open request	Update trên CRM, phải được FIN approve, sau đó mới sang Dashboard update request được. Sale submit xong -> PM approve -> anh bách CDO approve. 

			Luồng Update request 	Sau khi request được submit, luồng phê duyệt sẽ phụ thuộc vào từng trường hợp:  Add MVV cho dự án mà MVV này đã tồn tại trong một dự án (yêu cầu)  -> Yêu cầu mở dự án này sẽ cần sự phê duyệt của PM & CDO  Add MVV cho dự án mà MVV này chưa tồn tại trong một dự án (yêu cầu) hoặc thay đổi Project MM của MVV sẵn có  -> Yêu cầu mở dự án này chỉ cần sự phê duyệt của PM  Thay đổi Du Host cho dự án  -> Yêu cầu cập nhật này sẽ cần sự phê duyệt của PM & CDO
		Update request (Do client cắt HD, gia hạn HD, or add MM, đã update trên PC CRM)	Luồng phê duyệt mở dự án/ update request

15		Busniness plan request
16
17

		Group

		General Setting

		Delivery Setting

## Sheet: Trouble Shooting

	Module	Trigger	Common Issue	Resolution	Tag
1	General Issue		Dashboard khóa sửa dữ liệu vào ngày 28, nhân sự OB từ 28-31 của tháng sẽ không allocate vào dự án được	Rule do anh Bách yêu cầu	Không allocate được, khóa dữ liệu, khóa chỉnh sửa
			Dữ liệu export sai dù đã Tailoring	Check update user Tailoring từ thời điểm nào. Tailoring chỉ active bắt đầu từ khi Enable chỉ số, các ngày trước đó sẽ không áp dụng.	Tailoring, export sai, dữ liệu export sai, check chỉ số
			Issue cần can thiệp Database, khi nào xử lý xong	Phụ thuộc vào lịch hotfix của IT	Lịch hotfix, lịch IT
			Support gộp MVV	Cần email xin approve từ anh Bách, keep cc HR, FIN, SEPG, QA (playbook, tìm mục billing/allocate). Sau đó forward cho L2 can thiệp vào Database.  HR: ntnanh2, FIN: TTMY, QA của dự án	Gộp MVV, gộp dự án, dự án extend
			Support chuyển bộ phận	Tạo Ticket cho HNO với nội dung như ảnh, sau đó check với HNO anh tntu2 xem đã config nhân sự vào bộ phận mới chưa	Chuyển bộ phận
			Check role user	Call user, thực hiện log in đồng thời check F12. Tại Network, mở preview như hình, tìm role -> check	check role, F12
			Tại sao nhân sự là Intern/intern không lương/nhân sự nghỉ không lương lại hiển thị trên Data information của đơn vị	Check với HNO (tntu2) check type của LDAP. Eg: type của intern không lương là type 4, nếu check không đúng type 4 -> yêu cầu update type. Thời gian sync từ POA lên sever là 12h trưa, sync từ sever lên Dashboard là 12h đêm.	intern, data information
			KPI của project Migration 	Đang ăn theo project Development	KPI, dự án migrate, migration
			Thông tin không sync từ CRM sang DB	- Ấn Resend to DB tại Project Code/PMS để sync sang DB, vì DB lấy data từ Project code, bấm ở chỗ khác sẽ không sync (Pipeline,...)  Eg: Dự án đã edit lên MM ở CRM nhưng Dashboard vẫn chưa lên	sync CRM, Resend to DB
			Phân quyền xong không có data (DUL)	DUL được phân quyền xong, truy cập được Resource/Actual Timesheet nhưng không có data gì  -> Do org chart mới được cập nhật theo thông tin nhân sự, chờ qua ngày hôm sau check lại	Không có data, phân quyền, DUL
			Đổi PM dự án 	- Trên hệ thống CRM : AM báo FIN update thông tin PM trên Project Code thành PM mới trước  - Trên Dashboard: sau khi FIN Update thông tin AM trên Project Code (phải update xong) -> PMO/AM của dự án tạo update request để update thông tin PM dự án  - Sau khi Update request được approve => thì thêm thông tin PM mới vào Project Member và process như các dự án bình thường khác
			Thủ tục đóng dự án	- Để đóng dự án thì cần làm các task của dự án với SEPG và QA yêu cầu, anh xin thêm cho anh quyền PM vào sharepoint và JIra của dự án nhé ạ. Anh tạo ticket trên PMS với luồng dưới đây  -> Update Phase mới, giờ tạo Close Request cho dự án, chỉ có thể tạo với Project có stt khác Cancel
			Cần vào dự án làm thủ tục đóng dự án, nhưng PM cũ đã nghỉ	- Báo PMO thêm vào dự án với role PM ở Project member  - Đóng dự án theo thủ tục SEPG và QA yêu cầu, xin thêm quyền PM vào sharepoint và Jirra của dự án
			Fresher bị load lên Available resources của đơn vị	Fresher employee type = 4, check thấy sai thì báo lại HNO
			Thông tin nhân sự hiển thị sai trên DB	1. Lấy confirm từ HR/sf4c, check xem HNO đã update AD đúng chưa  -> Nếu đúng, chờ sync, nếu qua ngày chưa sync, báo IT update thủ công lại trên History Department/POA  -> Update xong chạy API https://backend-dashboard.cmcglobal.com.vn/api/api/user để sync sang DB, lấy Bearer Token từ acc admin để auth
			Mở dự án nhầm, cần Cancel dự án	Check thủ tục cancel dự án với SEPG nếu đã mở Jira  Check xem nguyên nhân cần Cancel là gì -> Nếu cần gộp MVV, xin approve anh Bách

			Template xin cấp quyền cho DUL	Dear IT  Theo như email nhân sự của HR gửi vào 12/06/2025 (đình kèm tại C ticket), anh lxngoc là DUL của DE3  Nhờ phía IT hỗ trợ cấp quyền DUL cho anh lxngoc ở các hệ thống sau:  Dashboard: DB-DUL  CRM/DPM: CRM-DU-Lead  PMS: Chuyển luồng approve của đơn vị DE3 từ anh phbinh sang anh lxngoc  RTS: RTS-DIVISION-HEAD  HRMS: TMS_DL_1  GAMS: GAMS_DUL  Skillset: SKILSHEET_DULEAD  Esign : Esign-DUL  Bidding: DU lead  OFFICE: OFFICE-DULEAD  Anh phbinh là G lead nên các quyền sẽ giữ nguyên.  Update trên orgchart của các con hệ thống có org chart nữa (CNOW,RTS)	Cấp quyền DUL
			Xin cấp quyền PM, QA, Sale, ... Trên dashboard	2 trường hợp:  + Xin quyền đúng role (eg: PM xin quyền PM, Sale xin quyền sale): Tạo Ticket cho IT như bình thường  + Xin quyền khác role (eg: HR xin quyền PMO): Cần approval từ anh Bách CDO, sau đó tạo Ticket cho IT như bình thường  - Flow tạo Ticket: Lên pms tạo ticket cho IT, type User permission, involve member vào và gắn link 2 đầu	Cấp quyền
	Dashboard 	Delivery overview
			Mới edit xong, số không hiện đúng	Job sync chạy 1 tiếng 1 lần -> Đợi
2			Số trên màn overview bị sai	Check lại số liệu xem đúng hay k. Nếu số không đúng sau khi tính lại theo công thức trên DB thì chạy lại API để hệ thống update lại thông tin	Check số liệu, Overview
2		Delivery report	QA không export được report	Check lại số liệu xem đúng hay k. Nếu số không đúng sau khi tính lại theo công thức trên DB thì chạy lại API để hệ thống update lại thông tin	Lỗi export, không export được, report
			Xin quyền để export được các loại report 	Cần có xác nhận của appowner cho việc cấp quyền, vì chỉ có PMO và DUL có quyền export hết các loại report	Cấp quyền, quyền export
			Số liệu trên KPI report bị sai 	Cross-check lại xem số bị sai mà QA raise lên trên report và số input trên dự án có đúng hay không, có một số chỉ số cần lấy thông tin từ dự án, áp vào công thức và tính lại = tay để xem số trên report là đúng hay sai	Check số liệu, công thức
			Số liệu trên Allocate/Actual timesheet bị sai	Check lại số trên actual timesheet và Project resource để xem số đúng hay sai	Actual Timesheet/Allocate, số liệu
			Bị đổi position (từ DM thành PM,...)	Data ăn theo HRMS<SF4C. Báo HR check sf4c, Du1.20 là chị dttanh1@cmcglobal.vn
3		Available resource	Thông tin nhân sự đã nghỉ việc/ CTV không lương/Intern không lương vẫn hiển thị	Check lại thông tin config với HNO, nếu HNO báo đúng thông tin cần check lại thông tin trên POA	Nhân sự đã nghỉ việc, CTV không lương, Intern
		Dashboard setting	Các setting chỉ số khác	Do FIN config. Thường là Fin và Admin vào setting
			Cập nhật AM lead	AM cần active các client được tạo trên CRM. Khi có AM lead mới, cần vào setting để config teamlead mới. -> Teamlead mới cần xem được các client của AM dưới quyền, nhưng AM chỉ active được các client được tạo sau ngày AM lead mới được cấp quyền -> để AM lead active được client thì AM phải assign lại client cho AM lead active, sau đó AM lead lại assign cho AM dưới quyền	AM lead, client, không tìm thấy client

4
	Delivery	Work Order (Sale điền WO bên CRM)	Điền hết WO bên CRM mà sang Dash board không hiển thị gì	Đang lỗi hệ thống	WO

		Billing plan (PMO vào điền sub WO)	Add bill báo lỗi DPM headcount vượt sub WO	- Do rải MM quá MM order  -> Check PMO có rải bill quá MM order không, vì PMO họ rải bill theo data pipeline bên CRM -> Nếu Pipeline add quá MM cho phép, PMO rải theo thì lỗi   -> Rootcause có thể do sale nếu PMO không lỗi thao tác

			Đã rải bill nhưng không load sang PAKD	Check rải bill tháng mấy và end date PAKD tháng mấy. Rải bill vượt End date PAKD hệ thống sẽ không nhận
5			Không tìm, không thêm được thông tin WO	Check lại quyền của user, hiện tại chỉ có PMO có quyền edit thông tin, DUL/BUL và AM chỉ có quyền view	WO, Work Order

6		Delivery resource	Không tìm thấy nhân sự để allocate vào dự án	Check thông tin nhân sự đó có thuộc DU mà đang được yêu cầu hay không ở màn Setting -> user. Nếu user k thuộc DU đó, check lại vs HNO xem config department của nhân sự trên LDAP server => đã config đúng => check lại trên POA => nếu POA đúng, đùng Postman để call API update thông tin từ POA về Dashboard	Không tìm thấy nhân sự, allocate, allocate vào dự án
		Actual timesheet	Quyền rate PM,QA	Chỉ có DUL mới được rate	rate điểm PM, QA
			Không xem được màn Actual Timesheet	User phải có quyền PM trở lên mới xem được	Actual timesheet, quyền PM
7			PM không tìm thấy dự án để đánh giá và rate điểm/ PM không tìm thấy nhân sự nào trong Actual Timesheet	Check 2 điều kiện:   - Nhân sự PM đã được allocate đúng role PM (sai role sẽ không hiển thị) ở màn Project resource trong dự án  - PM đã được cấp quyền PM trên POA  - Nếu nhân sự kiêm 2 role: Có thể chia ra để allocate, eg: 1h PM, 7h Techlead. Phải allocate đúng role nếu không sẽ ảnh hưởng đến KPI HR đánh giá. Ít nhất phải đc allocate 1h/tháng mới có quyền rate điểm.	PM, không tìm thấy dự án, phân quyền PM
			Nhân sự được allocate trong dự án nhưng không hiển thị trên màn actual timesheet	Check xem thông tin nhân sự được allocate trong dự án => xoá đi => allocate lại	Allocate, actual timesheet, không hiển thị
			Allocate backdate do user quên allocate	Đánh Label Lỗiuser và yêu cầu approval từ anh Bách, cc hr,fin,qa -> L2process
			Nhân sự không được allocate/ đã remove trong dự án nhưng vẫn hiển thị	Dùng devtool => lấy API xoá thông tin allocate trên màn actual timesheet của user trên dự án-> dùng postman để chạy API => thay thông số ngày giờ, project-id, user-id để chạy  Lưu ý, nếu nhân sự được allocate đến cuối năm thì không thể dùng cách này, do chỉ có thể remove được thông tin tối đa 2 tháng kể từ ngày được allocate. Cần chạy query để xoá trên database	Allocate, actual timesheet, vẫn hiển thị
			Mail nhận về thiếu giờ Actual Timesheet	- Check allocation trên Project Resource có khớp với Actual Timesheet không, nếu không thì là lỗi hệ thống, nếu có thì theo step dưới  -> Check ngày nhận được mail, vì mail chỉ gửi các allocate đã được PM approve. Rootcause có thể do tại ngày đó PM chưa approve hết toàn bộ Timesheet	Actual Timesheet, allocate, thiếu giờ
			Lỗi Only newest PM	Do PM cũ được xóa đi được allocate lâu hơn PM mới hiện tại, dù xóa PM cũ đi, data vẫn được lưu trên hệ thống.  -> Solution: Báo PM mới tự allocate vào ngày muộn hơn (allcate luôn tại ngày cuối kỳ công của dự án cho chắc), hoặc xóa allocate của PM cũ đi allocate lại.

			Mở backdate để rate điểm	Vào màn setting => performacne score mở allocate backdate => user rate xong cần khoá lại	Backdate, performance score, đánh giá
		Actual billable (Role sale)	Không thấy sub WO cho DU	Báo user liên hệ PMO add thêm
			WO ở sai trạng thái so với CRM (Kt1, KT2,...)	Sang CRM tìm theo Project Code. Resend to Dashboard tại Project, nếu không được thì ấn lần nữa ở Pipeline

8			Lỗi khi AM edit thông tin
		Business plan (Role sale) -PAKD	Lỗi không approve được PAKD
			Lỗi sai thông số
			Lỗi chưa có WO	Check bên CRM xem có WO cho sale chưa	WO, CRM
			Lỗi there is no delivery unit in this business plan	Chưa có sub WO tại màn Billing plan, chưa có thông tin DU host. -> sẽ không submit được business plan	there is no delivery unit in this business plan, WO, DU host
9			PAKD hiện lỗi ko có DU sản xuất, nhưng pipeline và WO đã có thông tin DU sx rồi, và Preparator ko edit được	Check billing plan đã có thông tin WO của DU chưa, nếu chưa có thì báo user nhờ PMO add WO	WO, bussiness plan, billing plan
			Không Submit được PAKD	- Check tài liệu upload, tối thiểu upload 1 tài liệu mới submit được. Trường hợp PAKD bị reject về draft thì ko cần rule này
			Cần đẩy margin lên trên 30%	- Đẩy margin thì tăng revenue hoặc cắt expense. Do user tự điền, out of scope
			Đổi AM xong AM mới không xem được PAKD cũ 	- Check đã đổi AM host bên CRM chưa  -> Nếu done, check xem trạng thái PAKD là gì.  -> Nếu đang trong giai đoạn review, báo Reject về trạng thái Draft   -> Nếu đã được approve, dùng acc admin tạo Version mới -> auto ăn theo AM host mới
			Số Unit Price trên PAKD không giống với Pipeline, Avr Ratecard (Billing Plan)	Sô Unit Price trên PAKD ăn theo WO trên CRM, tự tính theo công thức riêng, avr Ratecard đang dùng công thức khác

			Không tìm thấy cột của DU	Check xem đã add DU trên Billing Plan chưa
			Project code trên CRM đã được active nhưng không tìm được PAKD tương ứng	Vào hệ thống CRM để check thông tin Project code mà AM gửi, sử dụng MVV để check lại trên hệ thống Dashboard, có thể chạy API để sync thông tin từ CRM về Dashboard	PAKD, CRM, Project Code, PC
10	Project Detail (khi chọn 1 project tại Project list)	Project Overview	Số Accumulated bị sai	Check lại thông tin áp vào công thức để xem tính đúng hay không, nếu không đúng cần check lại số allocate sai hay trên actual timesheet sai để set lại
			Số Billable bị sai	Check tại Project Inputs/Project Billable, đếm tổng số Billable của tháng cần tìm
			Project Health bị hiển thị sai	Check lại xem request của user và số đượ add ở màn project input xem chính xác hay không
		Project member	Chuyển thông tin PM	Báo PMO của bộ phận add user vào Project Member của dự án với role PM

			Quyền PM	1 dự án chỉ có 1 PM, ăn theo PM cuối cùng được add vào dự án. PM cũ vẫn có quyền view dự án. Nhưng chỉ PM mới có quyền thao tác.	quyền PM
11			Không add được member vào danh sách
		Project resource

			Không tìm thấy nhân sự để allocate vào dự án	Member phải được Add vào tại màn Project Member trước mới hiển thị sang màn Project Resource.	Project resource, không tìm thấy dữ liệu
12			Không allocate được nhân sự, báo quá số giờ tại Add Plan	Check trên màn actual timesheet xem nhân sự đó đã được allocate vào MVV nào khác hay k. Do logic của nhân sự chỉ được allocate maximum 8h với Working Time. Nếu muốn add hơn 8 tiếng phải tích vào OT	Add plan, allocate
		Project KPI	Tại sao chỉ số sai (QA)	Check Inputs
		Project input	Sửa số Billable	Do PMO rải, số trong kỳ quá khứ không sửa được	Billable

			Không Add được billable cho nhân sự mới	Check billable của dự án, nếu đã rải hết MM thì sẽ không add thêm được. Nếu cần add cho nhân sự mới -> tạo hoặc dùng 1 MVV khác cho 2 bạn này, báo lại sale để làm việc rõ hơn	Billable, MM, nhân sự mới
13			Số KPI bị sai	Xem thông tin tính KPI của dự án trong Slide module KPI để xem số ra đúng hoặc sai => nếu số ra đúng, mà QA báo sai thì xem phải lỗi của QA hay k, nếu lỗi của QA thì dự án cần xin approve của anh Bách để được sửa. Nếu lỗi do hệ thống thì AMS phải hỗ trợ.	Input bị sai, bug rate, leakage rate, Timeline
14		Project request	Tạo Open/Update request	Chi tiết trong slide Module Open/Update request
	Task	Open request (AM Open, PM approve)	Không thấy MVV trong droplist	Droplist chỉ hiển thị tối đa 10 MVV. Báo user thử copy paste thay vì tìm trong droplist.	MVV
			Project MM báo lỗi, không add được	Tối đa chỉ được phép 12 tháng, ko được add hơn	Project MM
			Project MM nhập dưới 12 tháng nhưng vẫn không được	Có thể do user khác dùng MVV để nhập MM từ trước rồi. 1 MVV có thể nằm trong nhiều dự án, nhưng tổng MM không thể quá 12 tháng.  -> Sang Project list search theo MVV để check.	Project MM, trùng MVV
			Không search được client/ Search client ra no data	Search client trên crm, check BU of AM host. Nếu khác BU sẽ không xem được. Thường xảy ra khi AM chuyển BU hoặc client được BU này bán cho BU kia.   -> Phải contact FIN chuyển BU client về đúng cho AM	không tìm thấy client
			PM không tìm thấy nút Submit, Save, Cancel dự án	AM điền thiếu trường Project Category, Scope	Không submit được, không thấy nút submit
			Luồng tạo Open request	Sale tạo Project code -> Fin approve -> có mvv-> sale sang Dashboard Open req
			Lỗi "Please Select Project Category"	Vẫn phải điền trường Project Category và 1 số trường không bắt buộc thì PM mới submit được dự án
		Update request (Do client cắt HD, gia hạn HD, or add MM, đã update trên PC CRM)	Nhập LDAP để reassign không trả kết quả	Nhập tìm kiếm theo tên, không nhập LDAP
			Luồng Update request 	Update trên CRM, phải được FIN approve, sau đó mới sang Dashboard update request được. Sale submit xong -> PM approve -> anh bách CDO approve. 
			PC đổi AM host, BU host -> AM và BU hiện tại không tìm được dự án đó nữa
			Project Information, phải điền 	Các trường không bắt buộc nhưng cũng phải điền. Lỗi chỉ gặp ở Open Request do ở Update Request nó là trường bắt buộc
			Submit Update request không cảnh báo lỗi nhưng không submit được	F12. Thường là do trường Project MM, lỗi do java script. Workaround: Sửa lên xuống cho tròn 2 chữ số thập phân
			Lỗi 'Selected project start date..."	Do dính Billable/Data allocate backdate so với Start date của Project  -> Với data allocate-> check Resources  -> Với Billable, check Start date các MVV tại project input/billable  Nếu PIC không update được do khóa data -> user tự xin approval  Tương tự với End date
			Sale không search được Project	Sale chỉ search được Project của Client nào -> Lên CRM/CLIENT/ check AM host, check BU host trong tab Additional Information
			Lỗi trắng trang, submit xong không thấy thông tin gì	Do Thiếu field tại Project Information. Các trường General Information trong Request phải có đủ
	Busniness plan	Busniness plan	PAKD chưa được submit (draft)	Chỉ xem được trên business plan, bấm xem chi tiết để check luồng approve
			Project code được FC approve nhưng chưa thấy MVV bên Dashboard
			Lỗi chưa có WO cho Sale, lỗi không thấy cột của bộ phận	Check trên CRM có WO/Sub WO đủ chưa cho Sale chưa	Error: there is no delivery unit in this business plan.
			Lỗi chưa có WO cho DU	Check trên Billing Plan/Dashboard có Sub WO cho DU chưa
			Lỗi DUL, BUL, CEO,.. Không approve được
			Lỗi Sale không thấy Revenue Plan	Do chưa có sub WO cho DU ở billing plan. Báo user liên hệ PMO
			Không thấy Billable rate ở cột Total	Billable rate ở các cột thành phần của DU/BU phải hiện đủ. Check thông tin DU/BU đã điền đủ chưa
			Lỗi there are more than one sale work order	Do PC bên CRM có 2 WO type Sale, báo user cancel đi 1 cái	Error: there are more than one sale work order

15			Lỗi không approve được PAKD
16		Group	Config đơn vị mới
17
	Setting	General Setting	Check thông tin user active/inactive	Tab User	Nhân sự inactive,active
			Check Endate của nhân sự	Tab user, trường history	Nhân sự nghỉ việc, remove nhân sự
			Nhân sự chuyển DU sao chưa thấy	Job đồng bộ phải qua ngày mới cập nhật. Nếu đã chuyển nhân sự lâu rồi mà chưa thấy, thì alo HNO lấy description xem config đúng chưa	Nhân sự chuyển DU

		Delivery Setting	enable backdate rate performance	Lấy thông tin từ user: Kỳ công muốn reopen. Rồi vào tab Performance score enable
			Email báo sai điểm, báo sai giờ allocate, timesheet không tự động approve, rate 85	Sau ngày 28 system tự động approve time sheet và rate điểm 85 cho nhân sự. Nếu job sai phải check lại job
			Không thấy setting Project norm cho period nào đó	Norm tự động ăn theo period gần nhất. Norm do SEPG & QA ban hành	project norm

## Sheet: Permission Matrix

ROLES AND PERMISSIONS MATRIX			ADMIN	BOD	SEPG	FC	Sale	DUL	PM	PMO	HR	QAL	QA	Toàn bộ CBNV (Employee)
Dashboard Module 															Ghi chú
	Screens	Global
		Global Overview
		View	x	x						x
		Global Sales
		View	x	x						x
		Global Delivery
		View	x	x						x
		Global HR
		View	x	x						x
		Sales
		Sales Overview
		View	x	x
		Sales DPM
		View	x	x
		Sales Productivity
		View	x	x
		Sales Progress
		View	x	x
		Sales KPI
		View	x	x
		Delivery
		Delivery Overview
		View all	x	x	x	x				x	x	x	x
		View by DU						x
		Project Statistics
		View all	x	x	x	x				x	x	x	x
		View by DU						x
		Delivery Monitoring
		View	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A
		Delivery Issue
		View	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A
		Delivery Risk
		View	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A	N/A
		Delivery Report
		View report resource allocation	x	x		x				x
		View report project allocation	x	x		x		x		x
		View report resource project	x	x		x				x
		View report project billable	x	x		x				x
		View report billable summary	x	x		x				x
		View report actual timesheet effort	x	x		x		x		x		x	x
		View report resource allocation new	x	x		x		x		x
		View report actual timesheet effort by role	x	x		x		x		x	x
		View report MVV list in project	x	x		x		x		x
		View report list project	x	x
		View report Delivery timesheet report	x	x
		View report Project KPI	x	x								x	x
		View report Performance score	x	x							x
		View report Performance score by day	x	x							x
		Available Resource
		View	x	x	x	x	x	x		x
		Dashboard setting
		View	x
	Activity
		Delivery Report
		Export resource allocation	x	x		x				x
		Export project allocation	x	x		x		x		x
		Export resource project	x	x		x				x
		Export project billable	x	x		x				x
		Export billable summary	x	x		x				x
		Export actual timesheet effort	x	x		x		x		x		x	x
		Export resource allocation new	x	x		x		x		x
		Export actual timesheet effort by role	x	x		x		x		x
		Export MVV list in project	x	x		x		x		x	x
		Export list project	x	x
		Export Delivery timesheet report	x	x
		Export Project KPI	x	x								x	x
		Export Performance score	x	x							x
		Export Performance score by day	x	x							x
		Available Resource
		Export 	x	x			x	x		x
		Dashboard setting			x
		CRUD
Delivery Module
	Screens	Project 
		View	x	x	x	x
		View by project DU host						x		x	x	x	x
		View by PM							x
		Project overview
		View	x	x	x	x		x	x	x	x	x	x
		Project member
		View	x	x	x	x		x	x	x	x	x	x
		Project resources
		View	x	x	x	x		x	x	x	x	x	x
		Project KPI
		View	x	x	x	x		x	x	x	x	x	x
		Project Inputs
		View	x	x	x	x		x	x	x	x	x	x
		Project Information
		View	x	x	x	x		x	x	x	x	x	x
		Project Issues
		View	x	x	x	x		x	x	x	x	x	x
		Project Risk
		View	x	x	x	x		x	x	x	x	x	x
		Project Monitoring
		View	x	x	x	x		x	x	x	x	x	x
		Project Information
		View	x	x	x	x		x	x	x	x	x	x
	Activity
		Project member
		Add	x	x				x	x	x
		Edit	x	x				x	x	x
		Delete	x	x				x	x	x
		Project resources
		Add	x	x				x	x	x
		Edit	x	x				x	x	x
		Delete	x	x				x	x	x
		Project inputs
		Add	x	x						x
		Edit	x	x						x
		Delete	x	x						x
		Project Information
		Edit	x		x			x	x
	Screens
		Resource allocation
		View all	x	x	x	x				x	x	x	x
		View by DU						x
		Actual timesheets
		View all	x	x	x	x		x	x	x	x	x	x
		Actual billable
		View all	x	x		x	x			x
		Business plan
		View all	x	x		x					x
		View by DU						x
		View by BU
		View by assignee
		View by Sales					x
		View by collaborator							x	x
		Workorder
		View all	x	x		x				x	x
		View by DU						x
		View by BU						x
		View by Sales					x
		Billing Plan
		View all	x	x		x				x
		View by DU					x	x
	Activity
		Resource allocation
		Add	x	x				x	x	x
		Edit	x	x				x	x	x
		Delete	x	x				x	x	x
		Actual timesheets
		Edit all	x	x
		Edit by DUL						x
		Edit by PM							x						PM không được rate PM/QA
		Approve all	x	x
		Approve by DUL						x
		Approve by PM							x
		Business plan
		Edit business plan detail					x	x	x	x					Chỉ AM có quyền Submit
		Edit business plan detail all	x	x		x
		Upload document all	x	x		x	x
		Upload document DU						x
		Create new version	x				x
		Export	x			x
		Actual billable
		Add	x	x		x	x			x
		Edit	x	x		x	x			x
		Delete	x	x		x	x			x
		Billing Plan
		Add	x	x						x
		Edit	x	x						x
		Delete	x	x						x
Task Module
	Screens
		Project request
		View all	x	x	x	x				x		x	x
		View by PM							x
		View by DUL						x
		View by Sales					x
		View by assignee												x
		Business plan request
		View all	x
		View by assignee		x	x	x	x	x	x	x	x	x	x	x
	Activity
		Project request			x			x
		Creeate open request BU	x				x							x
		Create open request all	x											x
		Create open request G	x							x
		Create update request BU	x				x							x
		Create update request all	x
		Create update request G	x							x
		Submit open request	x				x			x
		Edit open request BU	x				x
		Edit open request G	x							x
		Edit open request all	x
		Edit open request PM	x						x
		Approve reject open request PM	x						x
		Submit update request	x
		Edit update request BU	x				x
		Edit update request G	x
		Edit update request all	x
		Edit update request PM	x						x
		Approve reject open request PM	x						x
Setting module
	Screens	General setting
		Users
		View all	x			x
		Groups
		View all	x			x
		View by assignee	x
		Delivery setting	x
		Project types
		View all	x
		Setting projects
		View all	x
		Project norms
		View all	x		x
		Timesheet setting
		View all	x
		Holiday setting
		View all	x
		Skill setting
		View all	x
		Performance score setting
		View all	x								x
		System support	x
		Update manpower
		View all	x
		Update DU Statistics
		View all	x
		Logs
		View all	x
		Workflow setting	x
		Workflow
		View	x
		Workflow groups
		View	x
		Permission
		View	x
		Email
		View	x

## Sheet: Role Account

Role		Acount UAT
ADMIN		dvdat4
BOD
SEPG		pthuyen8
FC		pvthang6
Sale		ldhoang3
DUL		dtmanh1
PM		blanh2
PMO	Quyền của PMO giới hạn bởi G trên PMO đó	nvqcuong
HR		dthieu6
QAL
QA		nvcquoc
IT 	Cấp quyền trên POA
HNO	Tạo user, config user
Member		hthang1

---

<a id="14-ràng-buộc-edit--delete-ci-và-relationship-xlsx"></a>
## 14. Ràng buộc edit, delete CI và Relationship.xlsx

## Sheet: Sheet1

Mapping Status
	Trạng thái hiện tại của relationship				Hành động gần nhất của relationship
project_ci_relationship	Status (Relationship Status)	Mô tả 	cột status trong màn list relationship		Latest Action (Latest Action Status)	Mô tả 	cột status trong màn list relationship
	Draft	Relationship mới tạo hoặc chỉnh sửa mà chưa được approve thành công 	Relationship chưa có hành động approve/reject gì từ IT -> sinh ra 1 bản ghi mới sub-ticket có approval status = draft		New	Ticket gắn liền relationship chưa được approve thành công lần nào
	Active	Relationship đang hoạt động (còn hiệu lực) sau khi IT approve ở Relationship Request	Relationship được approved và đang hoạt động		Updated 	Relationship được chỉnh sửa sau khi có ít nhất 1 bản ghi project_ci_jira_mapping approved và chưa expired	Relationship đã được approved, chưa hết hạn nhưng User cần sửa relationship (apply hoặc expire date) -> sinh ra bản ghi mới bên Sub-ticket có approval status = draft
	Expired	Relationship đã hết hạn	Relationship chưa được approve hay reject nhưng hết hạn. nếu 		Renew 	Relationship được chỉnh sửa sau khi có ít nhất 1 bản ghi project_ci_jira_mapping approved và đã expired	Relationship đã được approved/rejected và đã hết hạn và User cần sửa relationship (apply hoặc expire date) -> sinh ra bản ghi mới bên sub-ticket có activity status = renew và approval status = draft

	Trạng thái phê duyệt
project_ci_sub_ticket_ 	Status (Approval Status)	Mô tả 	Cột trạng thái ticket gần đây  list relationship		Action / Access Type	Mô tả 	Cột Access type màn list relationship
	Approved	Ticket con dựa trên relationship đã được chấp thuận	Relationship request được approved bởi IT,		Allow	Relationship được cho phép
	Rejected	Ticket con dựa trên relationship bị từ chối	Relationship request bị rejected từ IT		Deny	Relationship không được cho phép
	Pending 	Ticket con dựa trên relationship đang chờ duyệt	Relationship request đang chờ để được approve		N/A	Mặc định khi tạo mới relationship
	Draft	Ticket chưa được gửi thành công lần nào	Relationship request đang chờ để được PM request và mới hiện sang màn view của IT

project_ci_jira_parent_mapping 
	Ticket status	Mô tả 		Ticket resolution	Mô tả 		Sync Status	Mô tả 
	Các status dựa trên data mà jira trả về			Resolved	Resolution jira trả về khác unresolved		Failed	Tạo ticket Jira thất bại
				Unresolved	Resolution jira trả về = unresolved		Success	Tạo ticket Jira thành công
							Sending	Đang tạo ticket Jira
Mapping Relationship to Jira
	Ticket cha	Failed  hoặc chưa tạo							project_ci_jira_mapping > sync_status = Sending
	project_ci_sub_ticket_Status( sub-ticket mới nhất)		Draft và chỉ có 1 ticket	Draft và có nhiều hơn 1 sub-ticket  (sau khi đã đi hết 1 luồng và bị rejected/approved và user có update ở lần thứ 2 và tạo ticket Jira bị failed hoặc chưa tạo)	Success		Rejected	Approval Pending 
	project_ci_relationship Status( relatiionship)	CI chưa gắn relationship			Active	Expired
	CI	Sửa  Xóa	Sửa  Xóa	Sửa (Không được sửa Primary và Info ) 	Sửa (Không được sửa Primary và Info )  Tự động tạo mới sub-ticket với status = draft  Cập nhật relationship.latest_action hiện tại => Updated    	Sửa (Không được sửa Primary và Info )  Sửa CI không có ý nghĩa về mặt thay đổi với ticket cũ nên sẽ ko cần tạo mới ticket	Sửa (Không được sửa Primary và Info)  Tự động tạo mới sub-ticket với status = draft	X  Nếu không được sửa báo đỏ message "Action disabled because CIs has at least one established relationship being procesed on Jira	X  Nếu không được sửa báo đỏ message "Action disabled because CIs has at least one established relationship being procesed on Jira
	Relationship		Sửa (chỉ sửa applied date và expired date )  Xóa	Sửa (chỉ sửa applied date và expired date )   Tại case sau khi relationship được approved và update -> Approved, Active, New. User có chỉnh sửa relationship -> Draft, Active, Updated thì FE cũng sẽ hiển thị message "*All changes require an approval process and will be applied only after IT review and approval."	Sửa (chỉ sửa applied date và expired date )  Tự động tạo mới sub-ticket với status = draft và set applied date và expired date bằng giá trị trên  Cập nhật relationship.Latest_Action hiện tại=> Updated      FE hiển thị message trên modal "*All changes require an approval process and will be applied only after IT review and approval.    	Sửa (chỉ sửa applied date và expired date )  Tự động tạo mới sub-ticket với status = draft và set applied date và expired date bằng giá trị trên  Cập nhật relationship.Latest_action hiện tại => Renew    FE hiển thị message trên modal "*All changes require an approval process and will be applied only after IT review and approval.    	Sửa (chỉ sửa applied date và expired date )  Tự động tạo mới sub-ticket với status = draft	X  Nếu không được sửa báo đỏ message "Action disabled because CIs has at least one established relationship being procesed on Jira	X  Nếu không được sửa báo đỏ message "Action disabled because CIs has at least one established relationship being procesed on Jira
	CI thuộc rất nhiều relationsho nên khi kiểm tra được sửa hay không thì phải kiểm tra thêm điều kiện nữa
						0
	 1.Trường hợp Sửa CI  -  Không cho phép sửa nếu tồn tại sub-ticket con (hệ thống dashboard - màn Ticket system) có trạng thái là Approval Pending hoặc ticket cha (ticket Jira) có sync_status là sending  - Cho phép sửa bình thường khi tất cả các relationship mà CI tham gia đều chỉ có duy nhất 1 sub-ticket và ở trạng thái draft  - Cho phép sửa bình thường khi CI chưa gắn bất kì relationship nào  - Cho phép sửa ngoài primary và key info với tất cả trường hợp còn lại    2. Trường hợp Xóa CI  - Cho phép xóa  khi tất cả các relationship mà CI tham gia đều chỉ có duy nhất 1 sub-ticket và đang ở trạng thái draft  - Cho phép xóa khi CI chưa gắn bất kì relationship nào  - Các trường hợp còn lại không cho xóa    3. Trường hợp Sửa Relationship  - Không cho phép sửa nếu tồn tại sub-ticket con có trạng thái là Approval Pending hoặc ticket cha có sync_status là sending  - Chỉ cho phép sửa applied date và expired date với tất cả trường hợp còn lại    4. Trường hợp Xóa Relationship  - Chỉ được xóa khi mà sub-ticket con duy  nhất có trạng thái là Draft  - Các trường hợp còn lại không cho xóa    5. Nếu sub-ticket con mới nhất có trạng thái là Approved hoặc Rejected  - Khi sửa CI hoặc Relationship thì sẽ: Tự động tạo mới sub-ticket với status = draft    6. Hành động bổ sung khi sửa CI/ Relationship  Nếu sub-ticket con mới nhất có trạng thái là Approved thì:  + Nếu Relationship hiện tại là Active thì Cập nhật relationship hiện tại từ Active => Updated khi sửa CI hoặc Relationship  + Nếu Relationship hiện tại là Expired thì Cập nhật relationship hiện tại từ Expired => Renew khi sửa Relationship  
Sơ đồ luồng status relationship
Ràng buộc giữa Source CI, Desstination CI

	COMPUTER	PERIPHERAL_DEVICES	HARDWARE	CONNECTS_TO
	COMPUTER	NETWORK	IP	CONNECTS_TO
	COMPUTER	VPN_TO_CUSTOMER	VPN	CONNECTS_TO
	MEMBER	EMAIL	EMAIL	CONNECTS_TO
	MEMBER	CUSTOMER_ASSET	HARDWARE	CONNECTS_TO
	SERVER	NETWORK	IP	CONNECTS_TO
	MEMBER	CUSTOMER_ACCESS	IP	CONNECTS_TO
	CUSTOMER_ACCOUNT	NETWORK	IP	CONNECTS_TO
	VPN_TO_CMC	NETWORK	VPN	CONNECTS_TO
	VPN_TO_CUSTOMER	COMPUTER	VPN	CONNECTS_TO
	EMAIL	MEMBER	EMAIL	CONNECTS_TO
	CUSTOMER_ASSET	MEMBER	HARDWARE	CONNECTS_TO
	NETWORK	SERVER	IP	CONNECTS_TO
	CUSTOMER_ACCESS	MEMBER	IP	CONNECTS_TO
	NETWORK	CUSTOMER_ACCOUNT	IP	CONNECTS_TO
	EMAIL	EMAIL	EMAIL	CONNECTS_TO
	COMPUTER	SOFTWARE	SOFTWARE	CONNECTS_TO
	MEMBER	DOCUMENT	IP	CONNECTS_TO
	CUSTOMER_ASSET	NETWORK	IP	CONNECTS_TO
	CUSTOMER_ASSET	SOFTWARE	IP	CONNECTS_TO
	MEMBER	COMPUTER	HARDWARE	CONNECTS_TO
	COMPUTER	CMC_DEFAULT_SOFTWARE	SOFTWARE	CONNECTS_TO

---

<a id="15-tdx-dashboard-phase11-fi-25052025-xlsx"></a>
## 15. TDX_Dashboard_Phase11_FI_25052025.xlsx

## Sheet: Summary

				SUMMARY REPORT

	Project Name:			TDX Dashboard					Module Name:		Phase 11
									Prepared By: Tran Thi Hoai Thuong		CMC
	Created date:			45354
	Updated date:

	Sheet Name			Passed	Failed	Untested	N/A	Total	Coverage	Successful coverage			High	Medium	Low
	C-code_Project_Information			0	0	22	0	22	0	0			4	16	2
	C-Ticket Overview			0	0	35	0	35	0	0			15	28	8
	C-ticket_Project_Information			0	0	19	0	19	0	0			1	15	3
	Project_Overview			0	0	22	0	22	0	0			4	17	1
	TOTAL			0	0	98	0	98	0	0			24	76	14

	TEST RESULT LEGEND
	PASSED	This Test Case's Actual Result is aligned with Expected Result, behaving as intended
	FAILED	This Test Case's Actual Result is not aligned with Expected Result, not behaving as intended. Please provide screenshots of the issue that cause the Failed result. 
	UNTESTED	This Test Case has not been tested
	N/A	This Test Case cannot be tested because it is not required at that point of time, or dependent on others. Please provide the reasons when selecting this option. 

## Sheet: C-Code_Project Information

3	Total	Passed	Failed	Untested	N/A	% Coverage	% Success			 	 	 		 	 
C-code_Project_Information	22	0	0	22	0	0	0			 	 	 		 	 
Chrome	22	0	0	22	0	0	0			 	 	 		 	 
Screen	TC ID	Summary	Pre-Condition	Test Data	Test Steps	Expected Result	Type	Priority	Test Result	Remarks	R1 Date	R1 Date	R1 Result 	R2 Date	R2 Result	R3 Date	R3 Result
	C-code_Project_Information_1	[Project Information_Authorization]  Check quyền truy cập Project Information	Set up role và permission trên POA  - Permission ADMIN_EDIT_PROJECT_INFO 		1. Log in vào Dashboard với role ADMIN_EDIT_PROJECT_INFO   2. Truy cập vào Deliver > Project > ấn vào Project Name của project (thuộc Operation) muốn chọn > vào Project Information	1. Trường Git enable, có thể nhập giá trị vào trường Git  	Function	Medium	UNTESTED			45792	UNTESTED
	C-code_Project_Information_2	[Project Information_Authorization] Check quyền truy cập Project Information	Set up role và permission trên POA  - Permission ADMIN_EDIT_PROJECT_INFO 		1. Truy cập vào Project Information > Setting Number   2. Nhập number vào Git , ấn Save Changes	1. Number được Save  2. Button Reload hiển thị ở textbox của trường Project C-ticket & Projet Jira	Function	Medium	UNTESTED			45792	UNTESTED
	C-code_Project_Information_3	[Project Information_Authorization] Check quyền truy cập Project Information	Set up role và permission trên POA  - Permission PM_EDIT_PROJECT_INFO		1. Log in vào Dashboard với role ADMIN_EDIT_PROJECT_INFO   2. Truy cập vào Deliver > Project > ấn vào Project Name của project (thuộc Operation) muốn chọn > vào Project Information	1. Trường Git enable, có thể nhập giá trị vào trường Git  	Function	Medium	UNTESTED			45800	UNTESTED
	C-code_Project_Information_4	[Project Information_Authorization] Check quyền truy cập Project Information	Set up role và permission trên POA  - Permission   PM_EDIT_PROJECT_INFO		1. Truy cập vào Project Information > Setting Number   2. Nhập number vào Git , ấn Save Changes	1. Number được Save  2. Button Reload hiển thị ở textbox của trường Git	Function	Medium	UNTESTED			45800	UNTESTED
	C-code_Project_Information_5	[Project Information_Authorization] Check quyền truy cập Project Information	Set up role và permission trên POA  - Permission   PM_VIEW_PROJECT_INFO/ADMIN_VIEW_PROJECT_INFO		1. Log in vào Dashboard với role PM_VIEW_PROJECT_INFO/ADMIN_VIEW_PROJECT_INFO  2. Truy cập vào Deliver > Project > ấn vào Project Name của project (thuộc Operation) muốn chọn > vào Project Information	1. Trường Git disabled, không nhập giá trị vào textbox được	Function	Medium	UNTESTED			45800	UNTESTED
	C-code_Project_Information_6	[Project Information_UI] Check UI Project information/Git	Set up role và permission trên POA  - Permission ADMIN_EDIT_PROJECT_INFO/ PM_EDIT_PROJECT_INFO		1. Truy cập vào Deliver > Project > ấn vào Project Name của project (thuộc Operation) muốn chọn > vào Project Information	1. Field mới "Git" nằm dưới "Tool" bên cạnh "Sharepoint link"  2. Dạng textbox  3. Hiển thị trống với project chưa có GitID/ Hiển thị number và button reload với project đã save GitID	UI	Low	UNTESTED			45792	UNTESTED		UNTESTED		UNTESTED
	C-code_Project_Information_7	[Project Information_UI] Check UI Project information/Git	Set up role và permission trên POA  - Permission ADMIN_EDIT_PROJECT_INFO/ PM_EDIT_PROJECT_INFO		1. Truy cập vào Deliver > Project > ấn vào Project Name của project (thuộc Operation) muốn chọn > vào Project Information	1. Hover đến icon ? bên cạnh Git: hiển thị ra pop-up:  "This field value can be acquired from Gitlab. From the GitLab of the project please go to Setting > General > Section Naming, topics, avatar > Project ID"  2. Có thể nhập số vào textbox   3. Nhập ký tự String/đặc biệt, hiển thị warning pop-up: "Please input Number type"  4. Nhập Number có 11 chứ số, hiển thị  Warning pop-u: "Git-ids is not valid"	UI	Low	UNTESTED			45792	UNTESTED
	C-code_Project_Information_8	[Project Information_Funtion] Check khi thêm 1 Git number hợp lệ			1. Truy câp vào 1 Project Information  2. Tại Git, nhập 1 giá trị Git 	1. Id hiển thị  dưới dạng drop list  2. Sau khi chọn, id hiển thị dạng task  3. Save changes thành công, button reload hiển thị cạnh id	Function	Medium	UNTESTED			45792	UNTESTED
	C-code_Project_Information_9	[Project Information_Funtion] Check khi thêm add nhiều Git-ids/ 1 project có nhiều gitID ( tất cả các gitID đều chưa được dùng cho project khác)			1. Truy câp vào 1 Project Information  2. Tại Git, nhập nhiều giá trị Git không có trong DB  3. Ấn Save changes	1. Save thành công nhiều Git-ids  2. Hiển thị button reload bên cạnh tất cả các Git-ids được thêm vào	Function	Medium	UNTESTED			45792	UNTESTED
	C-code_Project_Information_10	[Project Information_Funtion] Check khi thêm GitId bị trùng/ 1 gitID chỉ được dùng cho 1 project	1. Truy cập vào 1 project/ Project Information  2. Nhập và save 1 GitId 		1. Truy câp vào 1 project khác/Project Information  2. Nhập vào GitId của tại PreCondition	1. hiển thị  ra error mess: "Git ids: [GitID] already assigned to project [ProjectID]"	Function	Medium	UNTESTED			45792	UNTESTED
	C-code_Project_Information_11	[Project Information_Funtion] Chon gitID A và B cho 1 project  - gitID A chưa được sử dụng cho project nào  - gitID B đã được sử dụng cho Project khác (Done)			1. Truy câp vào project khác/Project Information  2. Nhập vào gitID A và gitID B cho project đó  	1. gitID B và sử dụng lại cho Project khác   => Hệ thống hiển thị warning message  2. Sau khi sync, trong project_git_information chỉ hiện thị 1 bảng data duy nhất cho các dự án	Function	Medium	UNTESTED	https://pms.cmcglobal.com.vn/browse/TDXDAS-2417		45799	UNTESTED
	C-code_Project_Information_12	[Project Information_Funtion] Delete a gitID			1. Truy câp vào 1 project khác/Project Information  2. Nhập và Save Changes  1 gitID   3. Delete gitID đó   4. Save Changes  	1. Delete thành công gitID  2. Hiển thị  mess: "Save Successfully!"	Function	Medium	UNTESTED	https://pms.cmcglobal.com.vn/browse/TDXDAS-2310		45797	UNTESTED
	C-code_Project_Information_13	[Project Information_Funtion] Deleted gitID có thể sử dụng cho project khác	1. Truy cập vào 1 project/ Project Information và lưu thành công 1 GitID		1. Delete gitID đó và save changes thành công  2. Truy cập vào 1 project khác/Project Information > Nhập deleted gitID 	1.Có thể dử dụng lại gitID đã xoá  2. Không hiển thị warning mess	Function	Medium	UNTESTED			45797	UNTESTED
	C-code_Project_Information_14	Check data after save and not sync yet	Open data in project_git và query ra bảng project_id A tương ứng		1. Select project A/ project Information  2. Chọn 1 gitID   3. Click on save change  4. Check data on DB  	Data save on table project_git.project_id và status =new	Function	Medium	UNTESTED			45793	UNTESTED
	C-code_Project_Information_15	Check data after save and after Sync by manual	Open data in project_git và query ra bảng project_id A tương ứng		1. Select project A/ project Information  2. Chọn 1 gitID  3. Click on save change  4. Click on reload button on gitID  5. Check data on DB 	1. Data save on table project_git.project_id và status =sync  2. Hệ thống đồng bộ data vào bảng project_git_information    	Function	Medium	UNTESTED			45793	UNTESTED
	C-code_Project_Information_16	Check data after save and sync data by auto on the first time	Open data in project_git và query ra bảng project_id A tương ứng		1. Select project A/ project Information  2. Chọn 1 gitID  3. Click on save change  4. Check data on DB at 4am next day	1. Data save on table project_git.project_id và status =sync  2. Hệ thống đồng bộ data vào bảng project_git_information lần đầu	Function	Medium	UNTESTED			45798	UNTESTED
	C-code_Project_Information_17	Check data after save and sync data by auto on the second time	Open data in project_git và query ra bảng project_id A tương ứng		1. Select project A/ project Information  2. Chọn 1 gitID  3. Click on save change  4. Click on reload button on gitID  5. Check data on DB at 4am next day	1. Data save on table project_git.project_id và status =sync  2. Hệ thống đồng bộ data vào bảng project_git_information và chỉ sync data ngày trước đó	Function	Medium	UNTESTED			45798	UNTESTED
	C-code_Project_Information_18	Check sync data before save 	Open data in project_git và query ra bảng project_id A tương ứng		1. Select project A/ project Information  2. Chọn 1 gitID (có trong DB git_id)  3. Click on save change  4. Click on reload button on gitID ( không click Save changes)	1.Can sync data without clicking button save changes	Function	Medium	UNTESTED			45798	UNTESTED
	C-code_Project_Information_19	Check edit data on Project information	Open Project detail with Category = Operation		1. Edit data on Project Information  2. Click on Save button  	1. Can edit data  2. Data Save successfully	Function	High	UNTESTED			45797	UNTESTED
	C-code_Project_Information_20	Check sync data project có start-date là tháng hiện tại 	project_git_information.gitId A chỉ có data quá khứ/ không có data		1. Truy cập vào Deliver > Project > ấn vào Project Name của project (thuộc Operation) muốn chọn > vào Project Information  2. project  nhập gitID A ở pre-condition và ấn Save Changes   3. Click on reload button on gitID	1. Trong DB project_id, project  được gán gitID A và ở status sync  2. project_git_information chỉ sync data của tháng hiện tại  2. Chỉ số "AI index in months" & " AI supporting in months" được hiển thị cho tháng hiện tại	Function	High	UNTESTED				UNTESTED
	C-code_Project_Information_21	Check sync data có end_date là tháng của quá khứ	project_git_information.gitId A chỉ có data quá khứ/ không có data		1. Truy cập vào Deliver > Project > ấn vào Project Name của project (thuộc Operation) muốn chọn > vào Project Information  2. project  nhập gitID A ở pre-condition và ấn Save Changes   3. Click on reload button on gitID	1. Trong DB project_id, project  được gán gitID A và ở status sync  2. project_git_information chỉ sync data đến tháng end-date  2. Chỉ số "AI index in months" & " AI supporting in months" được hiển thị cho tháng end-date	Function	High	UNTESTED				UNTESTED
	C-code_Project_Information_22	Check sync data project vào ngày 01 	project_git_information.gitId A có data đến cuối tháng trước		1. Truy cập vào Deliver > Project > ấn vào Project Name của project (thuộc Operation) muốn chọn > vào Project Information  2. project  nhập gitID A ở pre-condition và ấn Save Changes   3. Click on reload button on gitID	  1. project_git_information được sync data của tháng hiện tại và update data tháng ngay trước nó  2. Chỉ số "AI index in months" & " AI supporting in months" được hiển thị cho tháng hiện tại và update tháng ngay trước đó	Function	High	UNTESTED				UNTESTED

## Sheet: Project Overview

3	Total	Passed	Failed	Untested	N/A	% Coverage	% Success			 	 		 	 	 
Project_Overview	22	0	0	22	0	0	0			 	 		 	 	 
Chrome	22	0	0	22	0	0	0			 	 		 	 	 
Screen	TC ID	Summary	Pre-Condition	Test Data	Test Steps	Expected Result	Type	Priority	Test Result	Remarks	R1 Date	R1 Date	R1 Result	R2 Date	R2 Result	R3 Date	R3 Result	Notes
	Project_Overview_1	[Project Overview_UI] Check UI Project Overview			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Overview 	1. Hiển thị Project Overview Page  2. Check GUI  - Project Information:    + Project ID    + Project Name/ DU    + Start Date/ End Date    + Catelogy    + MVV    + Type    + Status  - Filter Date  - Section 1: IN MONTH  - Section 2: ACCUMULATED  - Project Heath  - CMC Global JIRA Link  - CMC Global C-ticket Link  - Issue  - Risk  	UI	Low	UNTESTED				UNTESTED		UNTESTED		UNTESTED
	Project_Overview_2	[Project Overview_Function] Check Project Overview/ Filter date			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Overview 	1. Format XX-YYYY, default là tháng hiện tại  2. Không thể chọn date từ tháng start date trở về quá khứ  3. From (date) phải nhỏ hơn To (date)	Function	Medium	UNTESTED				UNTESTED	45799	UNTESTED		UNTESTED
	Project_Overview_3	[Project Overview_UI] Check [In month] section UI			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Overview 	1. Section  [In Month]  - Bill in month   - Allocation in month  - Planning bill rate in month  - Actual bill rate in month  - Actual timesheet in month  - AI index in months  - AI supporting in months  	UI	Medium	UNTESTED				UNTESTED		UNTESTED		UNTESTED
	Project_Overview_4	[Project Overview_Function] Check [In month]/ Bill in months			1. Select from project_billable table where Project_id tương ứng  2. Lấy ra các bill tương ứng tính toán	1. Bill in month = Tổng project_billable.billable value theo filter Date (UI/UX) = project_billable.start_date -> project_billable.end_date  2. Làm tròn 2 chữ số sau dấu thập phân	Function	Medium	UNTESTED				UNTESTED		UNTESTED		UNTESTED
	Project_Overview_5	[Project Overview_Function] Check [In month]/ Allocation in months			1. Select from user_plan table where Project_id tương ứng  2. Lấy thông tin user tương ứng 	1. Allocation của 1 tháng = Tổng số h nhân sự đươc allocate vào dự án trong tháng / (Số ngày công trong tháng x 8h)   - Nếu user không đủ working time in month → hours working theo working đây thực tế  2. Làm tròn 2 chữ số sau dấu thập phân	Function	Medium	UNTESTED				UNTESTED		UNTESTED		UNTESTED
	Project_Overview_6	[Project Overview_Function] Check [In month]/ Planning bill rate in months			1. Checking hiển thị UI 	1. Allocation bill rate in month = (Bill in month / Allocation in month) x100%  - Bill rate < AVG → Hiện màu đỏ  - AVG <= Bill rate < USL → Hiện màu cam  - Bill rate >= USL → Hiện màu xanh  2. Làm tròn 2 chữ số sau dấu thập phân	Function	Medium	UNTESTED				UNTESTED		UNTESTED		UNTESTED
	Project_Overview_7	[Project Overview_Function] Check [In month]/ Actual bill rate in months			1. Checking hiển thị UI 	1. Allocation bill rate in month = (Bill in month /  Actual timesheet in month) x100%  - Bill rate < AVG → Hiện màu đỏ  - AVG <= Bill rate < USL → Hiện màu cam  - Bill rate >= USL → Hiện màu xanh  2. Làm tròn 2 chữ số sau dấu thập phân	Function	Medium	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_8	[Project Overview_Function] Check [In month]/ Actual timesheet in months			1. Select from ctable where Project_id tương ứng and status = 2  2. Lấy ra value tương ứng	1. Actual timesheet in month = Tổng số h timesheet của nhân sự trong tháng / (Số ngày công trong tháng x 8h)  - Tổng số h timesheet của nhân sự trong tháng = Hours per day  - Nếu user không đủ working time in month → hours working theo working đây thực tế  2. Làm tròn 2 chữ số sau dấu thập phân	Function	Medium	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_9	[Project Overview_Function] Check [In month]/ AI index in months			1. Select from mater_data.setting table where setting_config_key = LOC_GENERATED , get value  2. Từ project_id = project_git.project_id = project_git_information.project_id → lấy ra project_git_information.LoC_generated  theo điều kiện Date trên UIUX = project_git_information.month&year    3. AI index in month = value* LoC_generated	1. AI index in month  hiển thị dúng 	Function	High	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_10	[Project Overview_Function] Check [In month]/ AI supporting in month			1. Select from mater_data.setting table where setting_config_key= LOC_ACCEPTED , get value  2. Từ project_id = project_git.project_id = project_git_information.project_id → lấy ra project_git_information.LoC_accepted  theo điều kiện Date trên UIUX = project_git_information.month&year    3. AI index in month = value* LoC_accepted	1. AI supporting in month hiển thị dúng 	Function	High	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_11	[Project Overview_Function] Check [In month]/ Budget billable			1. Từ project_id → query bảng mvv_project → sum cột divided man_month    Budget billable = Sum divided man_month	1. Budget billable hiện thị đúng  2. Làm tròn 2 chữ số sau dấu thập phân	Function	Medium	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_12	[Project Overview_UI] Check [Accumulated] section UI			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Overview 	1. Section  [Accumulated]  - Accumulated Billable  - Accumulated Allocation  - Accumulated Planning bill rate  - Accumulated Actual bill rate  - Accumulated Actual timesheet  - Accumulated AI index  - Accumulated AI supporting  - Budget billable	UI	Medium	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_13	[Project Overview_Function] Check [In month]/ Accumulated Billable			1. Select from project_billable table where Project_id tương ứng  2. Lấy ra các bill tương ứng tính toán	1. Accumulated billable = Tổng project_billable.billable value luỹ kế từ đầu dự án → Date trên UIUX  2. Làm tròn 2 chữ số sau dấu thập phân	Function	Medium	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_14	[Project Overview_Function] Check [In month]/ Accumulated Allocation			1. Select from user_plan table where Project_id tương ứng  2. Lấy thông tin user tương ứng 	1. Allocation của 1 tháng = Tổng số h nhân sự đươc allocate vào dự án trong tháng / (Số ngày công trong tháng x 8h)   - Nếu user không đủ working time in month → hours working theo working đây thực tế  -> Accumulated allocation = Tổng MM allocation luỹ kế từ đầu dự án → Date trên UIUX  2. Làm tròn 2 chữ số sau dấu thập phân	Function	Medium	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_15	[Project Overview_Function] Check [In month]/ Accumulated Planning bill rate			1. Checking hiển thị UI 	1. Accumulated allocation bill rate = Accumulated billable /  Accumulated allocation    - Bill rate < AVG → Hiện màu đỏ  - AVG <= Bill rate < USL → Hiện màu cam  - Bill rate >= USL → Hiện màu xanh	Function	Medium	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_16	[Project Overview_Function] Check [In month]/ Accumulated Actual bill rate			1. Checking hiển thị UI 	1. Accumulated actual timesheet bill rate = Accumulated billable /  Accumulated actual timesheet    - Bill rate < AVG → Hiện màu đỏ  - AVG <= Bill rate < USL → Hiện màu cam  - Bill rate >= USL → Hiện màu xanh	Function	Medium	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_17	[Project Overview_Function] Check [In month]/ Accumulated Actual timesheet			1. Select from project_task_spent_time table where Project_id tương ứng and status = 2  2. Lấy ra value tương ứng	1. Actual timesheet in month = Tổng số h timesheet của nhân sự trong tháng / (Số ngày công trong tháng x 8h)  - Tổng số h timesheet của nhân sự trong tháng = Hours per day  - Nếu user không đủ working time in month → hours working theo working đây thực tế  Accumulated actual timesheet = Tổng MM actual timesheet luỹ kế từ đầu dự án → Date trên UIUX  2. Làm tròn 2 chữ số sau dấu thập phân	Function	Medium	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_18	[Project Overview_Function] Check [In month]/ Accumulated AI index			1. Select from mater_data.setting table where setting_config_key = LOC_GENERATED, get value  2. Từ project_id = project_git.project_id = project_git_information.project_id → lấy ra project_git_information.LoC_generated  thoả mãn project_git_information.date&month <= date trên UIUX    3. Accumulated AI index = value* LoC_generated	1. Accumulated AI index hiển thị dúng 	Function	High	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_19	[Project Overview_Function] Check [In month]/ Accumulated AI supporting			1. Select from mater_data.setting table where setting_config_key= LOC_ACCEPTED , get value  2. Từ project_id = project_git.project_id = project_git_information.project_id → lấy ra project_git_information.LoC_accepted  thoả mãn project_git_information.date&month <= date trên UIUX    3. Accumulated AI index = value* LoC_generated	1. Accumulated AI supporting	Function	High	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_20	[Project Overview_Function] Check [In month]/ Budget billable			1. Từ project_id → query bảng mvv_project → sum cột divided man_month    Budget billable = Sum divided man_month	1. Budget billable hiện thị đúng  2. Làm tròn 2 chữ số sau dấu thập phân	Function	Medium	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_21	[Project Overview_Function] Check projects using same git_id			1. Check hiển thị UI	1.  Accumulated AI và Accumulated AI supporting hiện thị thông số giống nhau cho cả 2 project	Function	Medium	UNTESTED				UNTESTED		UNTESTED
	Project_Overview_22	[Project Overview_Function] Check 1 projects using multiple git_id			1. Check hiển thị UI	1.  Checking  project_git_information  lấy ra thông tin project_git_information.LoC_generated và project_git_information.LoC_accepted của các git_id  2. Accumulated AI và Accumulated AI bầng tổng các git sử dụng cho dự án	Function	Medium	UNTESTED				UNTESTED		UNTESTED

## Sheet: C-Ticket Overview

2	Total	Passed	Failed	Untested	N/A	% Coverage	% Success			 	 	 	 	 
C-Ticket Overview	35	0	0	35	0	0	0			 	 	 	 	 
Chrome	35	0	0	35	0	0	0			 	 	 	 	 
Screen	TC ID	Summary	Pre-Condition	Test Data	Test Steps	Expected Result	Type	Priority	Test Result	Remarks	R1 Date	R1 Result	R2 Date	R2 Result	R3 Date	R3 Result
Project Health table
	C-Ticket Overview_1	 [Project health]  Check chỉ số Norm Project Health	1. Đăng nhập thành công Dashboard  2. Click Delivery > Project   3. Tại cột Project Name > click 1 Project bất kỳ		1. Check các chỉ số Norm của project health	1. Hiển thị đúng các chỉ số (LSL, USL, AVG)  từ màn Project Norms- phần Operation    - TH1: Nếu có 1 bản ghi project norms thoả mãn điều kiện norms start date <= ngày hiện tại <= norms end date  > Lấy bản ghi có start date lớn nhất     2. TH2: Nếu ngày hiện tại nằm ngoài khoảng (norms start date; norms end date)    > Lấy bản ghi có end date lớn nhất  	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_2	[Project health]  Check chỉ số Score với data bị Tailoring	1. Đăng nhập thành công Dashboard  2. Click Delivery > Project   3. Tại cột Project Name > click 1 Project bất kỳ		1. Check các chỉ số Score của Project Health 	1. Hiển thị text và màu  xám : [Not Applicable]			UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_3	 [Project health]  Check hiển thị Score của On-time response (%) và On-time resolution khi Number of ticket On-time = 0 và number_of_ticket_received > 0	 CTicket :      On-time response (%) và On-time resolution thoả mãn điều kiện:     Number of ticket On-time = 0 và number_of_ticket_received >=0	    CTICKET:    SELECT * FROM project_cticket where project_id =  'id dự án'-- > lấy ra project_cticket_id    SELECT * FROM project_cticket_ontime_baseline   where project_cticketid in ( 'project_cticket_id vừa lấy bên trên')  and (year <='năm hiện tại' and month <='tháng hiện tại') and issue_type ='On-time resolution'  	1. Check hiển thị score On-time response = 0    2. Check hiển thị score On-time resolution = 0  	2. Score On-time response/resolution = 0 khi Number of ticket On-time = 0 và number_of_ticket_received >= 0 (Jira + Cticket)	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_4	 [Project health]  Check hiển thị Score của On-time resolution (%) 	Jira + CTicket :      On-time response (%) và On-time resolution thoả mãn điều kiện:     Number of ticket On-time # 0 và number_of_ticket_received # 0	JIRA:    SELECT * FROM project_jira where project_id = 'id dự án' -- > lấy ra project_jira_id    SELECT * FROM project_jira_ontime_baseline where project_jira_id in ( 'nhập project_jira_id' )  and (year <='năm hiện tại' and month <='tháng hiện tại') and issue_type ='Number of On-time resolution'    CTICKET:    SELECT * FROM project_cticket where project_id =  'id dự án'-- > lấy ra project_cticket_id    SELECT * FROM project_cticket_ontime_baseline   where project_cticketid in ( 'project_cticket_id vừa lấy bên trên')  and (year <='năm hiện tại' and month <='tháng hiện tại') and issue_type ='On-time resolution'	1. Check hiển thị score On-time resolution trên Project Health	1.   - Hiển thị total Score của On-time resolution từ Cticket + Jira  -Làm tròn 2 chữ số sau dấu thập phân     Dựa theo công thức:     *JIRA    On-time resolution lũy kế đến tháng hiện tại (%) = (Tổng project_jira_ontime_baseline.number_of_on_time / Tổng project_jira_ontime_baseline.number_of_ticket_received) x 100     *CTICKET  On-time resolution lũy kế đến tháng hiện tại = (Tổng project_cticket_ontime_baseline.number_of_on_time / Tổng project_cticket_ontime_baseline.number_of_ticket_received) x 100    	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_5	 [Project health]  Check hiển thị màu sắc dựa trên Score của On-time resolution (%) và Response			1. Check hiển thị màu của Indicator button  của On-time resolution (%) khi Score On-time resolution < chỉ số AVG     2. Check hiển thị màu của Indicator button  của On-time resolution (%) khi Score On-time resolution < chỉ số AVG     3. Check hiển thị màu của Indicator button  của On-time resolution (%) khi Score On-time resolution < chỉ số AVG 	1. Button hiện màu đỏ khi  Score On-time  < chỉ số AVG - Norm     2. Button hiện màu cam khi   chỉ số AVG - Norm On-time <= Score On-time  < chỉ số USL → button Hiện màu cam    3. Button hiện màu xanh khi   -Score On-time  >= USL → button Hiện màu xanh	Function	Low	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_6	 [Project health]  Check hiển thị Score của Process Compliance Rate		  SELECT * FROM project_jira where project_id = 'id dự án' -- > lấy ra project_jira_id    SELECT * FROM project_pcv_baseline where project_jira_id in ( 'nhập project_jira_id' )  and (year <='năm hiện tại' and month <='tháng hiện tại') 	2. Check hiển thị score Process Compliance Rate = Data is not available   <2025>	2. Score của On-time resolution hiển thị "Data is not available"     Nếu tất cả bản ghi được query ra có  Không có bất kỳ bản ghi nào  -> Hiển thị Data is not available 	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_7	  [Project health] Check hiển thị Score của Process Compliance Rate		SELECT * FROM project_jira where project_id = 'id dự án' -- > lấy ra project_jira_id    SELECT * FROM project_pcv_baseline where project_jira_id in ( 'nhập project_jira_id' )  and (year <='năm hiện tại' and month <='tháng hiện tại')   	1. Check hiển thị score Process Compliance Rate các trường hợp còn lại  <2218> 	1. Hiển thị đúng Score, BE tính toán công thức như sau   -Làm tròn 2 chữ số sau dấu thập phân    PCV rate = Tổng project_pcv_baseline.total_value/ Tổng Project_pcv_baseline.total_complete_items  	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_8	 [Project health]  Check hiển thị màu dựa trên Score của Process Compliance Rate		PCV rate < AVG  AVG <= PCV rate < USL  PCV rate >= USL	1. Check hiển thị màu của Indicator button  <2218>	1. Logic hiển thị màu    PCV rate < AVG → Hiện màu đỏ  AVG <= PCV rate < USL → Hiện màu cam  PCV rate >= USL → Hiện màu xanh  + AVG = PCV = USL	Function	Low	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_9	 [Project health]  Check hiển thị Score của Customer Satisfaction Score khi không có bản ghi nào		  SELECT * FROM project_jira where project_id = 'id dự án' -- > lấy ra project_jira_id    SELECT * FROM project_css   where project_jira_id in ( 'nhập project_jira_id' )  and (year <='năm hiện tại' and month <='tháng hiện tại')   	2. Check hiển thị score Customer Satisfaction Score = Data is not available	2. Sau khi query, nếu không có bản ghi nào, score của Customer Satisfaction Score hiển thị Data is not available 	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_10	 [Project health]  Check hiển thị Score của Customer Satisfaction Score		  SELECT * FROM project_jira where project_id = 'id dự án' -- > lấy ra project_jira_id    SELECT * FROM project_css   where project_jira_id in ( 'nhập project_jira_id' )  and (year <='năm hiện tại' and month <='tháng hiện tại')   	1. Check hiển thị score Customer Satisfaction Score các trường hợp còn lại  <2221>	1. Sau khi query, nếu có bản ghi thì hiển thị score theo công thức sau:    CSS = Tổng CSS project_css.value/Số lần đo CSS    -Làm tròn 2 chữ số sau dấu thập phân    	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_11	[Project health]   Check hiển thị màu sắc dựa trên Score của Customer Satisfaction Score			1. Check hiển thị màu của Indicator button	1. Logic hiển thị màu    CSS < AVG → Hiện màu đỏ  AVG <= CSS < USL → Hiện màu cam  CSS >= USL → Hiện màu xanh  	Function	Low	UNTESTED			UNTESTED		UNTESTED		UNTESTED
Pie Chart Application Support
	C-Ticket Overview_12	[Project health]Check hiển thị Pie Chart On Time Response	1. Đăng nhập thành công Dashboard  2. Tại cột Project Name > click 1 Project bất kỳ với các dự án sử dụng C-ticket 		1. Check hiển thị Pie Chart On Time Response	1. Pie chart On Time Response hiển thị 3 chỉ số (số thập phân) tương ứng theo màu sắc   - On time: xanh lá cây  - Late: đỏ  -Unassessed: vàng  - Tooltip: Pie chart này chỉ áp dụng cho những dự án sử dụng C-ticket nội bộ CMC  - Hiển thị filter type: default value = Application Support  Dropdown hiển thị 3 giá trị   - Application Support  - Incident   - Complaint   Hiển thị đúng màu các chỉ số On Time Response- Application Support với 3 chỉ số On time, Late, Unassessed theo giá trị được filter  	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_13	[Project health] Check hiển thị Pie Chart số	1. Đăng nhập thành công Dashboard  2. Tại cột Project Name > click 1 Project bất kỳ với các dự án sử dụng C-ticket 	SELECT *   FROM project_cticket_issue   WHERE (ontime_response IS NULL OR ontime_response IN ('yes', 'no'))   AND status <> 'Cancel'   AND request_type = 'Application Support';  → Tổng số ticket received (1)    SELECT *   FROM project_cticket_issue   WHERE ontime_response IN ('yes')  AND status <> 'Cancel'   AND request_type = 'Application Support';  (2)	1. Tại filter > chọn Application Support> Check hiển thị chỉ số On -time	1. Pie chart hiển thị % On time   BE query bảng project_cticket_issue → đếm tất cả bản ghi thoả mãn theo từng request type:  - status != cancel  - ontime_response IN ("null","yes","no")  → Tổng số ticket received (1)  Trong tổng số ticket (1), BE đếm tất cả bản ghi thoả mãn: ontime_response? = Yes (2) theo từng issue type  Trong tổng số ticket (1), BE đếm tất cả bản ghi thoả mãn: ontime_response? = No (3) theo từng issue type  Trong tổng số ticket (1), BE đếm tất cả bản ghi thoả mãn: ontime_response? = null (4) theo từng issue type  % on-time ticket = (2) / (1) *100 → màu xanh lá cây    % late ticket = (3) / (1) *100 → màu đỏ    % unassessed = (4) / (1) *100 → màu xám	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_14	[Project health]Check hiển thị Pie Chart On Time Resolution	1. Đăng nhập thành công Dashboard  2. Tại cột Project Name > click 1 Project bất kỳ với các dự án sử dụng C-ticket 		1. Check hiển thị Pie Chart On Time Resolution	1. Pie chart On Time Resolution hiển thị 3 chỉ số (số thập phân) tương ứng theo màu sắc   - On time: xanh lá cây  - Late: đỏ  -Unassessed: vàng  - Tooltip: Pie chart này chỉ áp dụng cho những dự án sử dụng C-ticket nội bộ CMC  - Hiển thị filter type: default value = Application Support  Dropdown hiển thị 3 giá trị   - Application Support  - Incident   - Complaint   Hiển thị đúng màu các chỉ số On Time Resolution- Application Support với 3 chỉ số On time, Late, Unassessed theo giá trị được filter  	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_15	[Project health]Check hiển thị Pie Chart số	1. Đăng nhập thành công Dashboard  2. Tại cột Project Name > click 1 Project bất kỳ với các dự án sử dụng C-ticket 	SELECT *   FROM project_cticket_issue   WHERE (ontime_Resolution IS NULL OR ontime_Resolution IN ('yes', 'no'))   AND status <> 'Cancel'   AND request_type = 'Application Support';  → Tổng số ticket received (1)    SELECT *   FROM project_cticket_issue   WHERE ontime_Resolution IN ('yes')  AND status <> 'Cancel'   AND request_type = 'Application Support';  (2)	1. Tại filter > chọn Application Support> Check hiển thị chỉ số On -time	1. Pie chart hiển thị % On time   BE query bảng project_cticket_issue → đếm tất cả bản ghi thoả mãn theo từng request type:  - status != cancel  - ontime_Resolution IN ("null","yes","no")  → Tổng số ticket received (1)  Trong tổng số ticket (1), BE đếm tất cả bản ghi thoả mãn: ontime_Resolution? = Yes (2) theo từng issue type  Trong tổng số ticket (1), BE đếm tất cả bản ghi thoả mãn: ontime_Resolution? = No (3) theo từng issue type  Trong tổng số ticket (1), BE đếm tất cả bản ghi thoả mãn: ontime_Resolution? = null (4) theo từng issue type  % on-time ticket = (2) / (1) *100 → màu xanh lá cây    % late ticket = (3) / (1) *100 → màu đỏ    % unassessed = (4) / (1) *100 → màu xám	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_16	[Project health]Check hiển thị số total ticket On Time Response khi chọn type = Incident (1)	1. Đăng nhập thành công Dashboard  2. Tại cột Project Name > click 1 Project bất kỳ với các dự án sử dụng C-ticket 	SELECT *   FROM project_cticket_issue   WHERE (ontime_response IS NULL OR ontime_response IN ('yes', 'no'))   AND status <> 'Cancel'   AND request_type ='Incident'  → Tổng số ticket received (1)	1. filter type = Incident > check hiển thị tổng số ticket On Time Response 	3. Khi user filter type = Incident   > hiển thị tổng số ticket On Time Response (số nguyên dương) ở bên tay phải title On-time Response  Công thức: tổng số ticket On Time Response = số ticket ontime + số ticket late + số ticket Unassessed	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_17	[Project health]Check hiển thị số total ticket On Time Response khi chọn type = Complaint	1. Đăng nhập thành công Dashboard  2. Tại cột Project Name > click 1 Project Operation bất kỳ	  SELECT *   FROM project_cticket_issue   WHERE (ontime_response IS NULL OR ontime_response IN ('yes', 'no'))   AND status <> 'Cancel' and request_type ='Complaint'   	1. filter type = Complaint > check hiển thị tổng số ticket On Time Response 	3. Khi user filter type = Complaint   > hiển thị tổng số ticket On Time Response (số nguyên dương) ở bên tay phải title On-time Response  Công thức: tổng số ticket On Time Response = số ticket ontime + số ticket late + số ticket Unassessed	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_18	[Project health]Check hiển thị số total ticket On Time Resolution khi chọn type = Incident	1. Đăng nhập thành công Dashboard  2. Tại cột Project Name > click 1 Project Operation bất kỳ	Select * from project_cticket_issue  WHERE (ontime_resolution IS NULL OR ontime_resolution IN ('yes', 'no'))  AND status <> 'Cancel'and request_type = 'Incident';	1. filter type = Incident > check hiển thị tổng số ticket On Time Resolution 	3. Khi user filter type = Incident   > hiển thị tổng số ticket On Time Resolution (số nguyên dương) ở bên tay phải title On-time Resolution  Công thức: tổng số ticket On Time Resolution = số ticket ontime + số ticket late + số ticket Unassessed	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_19	[Project health]Check hiển thị số total ticket On Time Resolution khi chọn type = Complaint	1. Đăng nhập thành công Dashboard  2. Tại cột Project Name > click 1 Project Operation bất kỳ	  Select * from project_cticket_issue  where ontime_Resolution in ('Null', 'yes', 'no')   and status <> 'Cancel' and request_type ='Complaint'   	1. filter type = Complaint > check hiển thị tổng số ticket On Time Resolution 	3. Khi user filter type = Complaint   > hiển thị tổng số ticket On Time Resolution (số nguyên dương) ở bên tay phải title On-time Resolution  Công thức: tổng số ticket On Time Resolution = số ticket ontime + số ticket late + số ticket Unassessed	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_20	[Project health] Check hiển thị Pie Chart khi chọn filter type = Complaint	1. Đăng nhập thành công Dashboard  2. Tại cột Project Name > click 1 Project bất kỳ	Select * from project_cticket_issue  WHERE (ontime_resolution IS NULL OR ontime_resolution IN ('yes', 'no'))  AND status <> 'Cancel' and request_type ='Complaint' ;  → Tổng số ticket received (1)    Select * from project_cticket_issue  where ontime_Resolution ='yes' and status <> 'Cancel' and request_type ='Complaint' (2)  	1. Tại filter > chọn Complaint> Check hiển thị chỉ số On -time	1. Pie chart hiển thị % On time của Complaint theo công thức sau:    % on-time ticket = (2) / (1) *100 → màu xanh lá cây    - Số % làm tròn đến số thứ 2 sau dấu thập phân	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_21	[Project health]Check hiển thị Pie Chart khi chọn filter type = Complaint		Select * from project_cticket_issue  WHERE (ontime_resolution IS NULL OR ontime_resolution IN ('yes', 'no'))  AND status <> 'Cancel' and request_type ='Complaint' ;  → Tổng số ticket received (1)    Select * from project_cticket_issue  where ontime_Resolution ='No' and status <> 'Cancel' and request_type ='Complaint' (3)  	2. Tại filter > chọn Complaint> Check hiển thị chỉ số Late	2. Pie chart hiển thị % Late của Complaint theo công thức sau:    % late ticket = (3) / (1) *100 → màu đỏ    - Số % làm tròn đến số thứ 2 sau dấu thập phân	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_22	[Project health]Check hiển thị Pie Chart khi chọn filter type = Complaint		Select * from project_cticket_issue  WHERE (ontime_resolution IS NULL OR ontime_resolution IN ('yes', 'no'))  AND status <> 'Cancel' and request_type ='Complaint' ;  → Tổng số ticket received (1)    Select * from project_cticket_issue  where ontime_Resolution IS NULL and status <> 'Cancel' and request_type ='Complaint' (4)  	3. Tại filter > chọn Complaint> Check hiển thị chỉ số Unassessed	3. Pie chart hiển thị % Unassessed của Complaint theo công thức sau:    % unassessed = (4) / (1) *100 → màu xám    - Số % làm tròn đến số thứ 2 sau dấu thập phân	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_23	[Project health]Check hover  Pie Chart khi chọn type = Complaint	1. Đăng nhập thành công Dashboard  2. Tại cột Project Name > click 1 Project bất kỳ	  Select * from project_cticket_issue  where ontime_Resolution ='yes' and status <> 'Cancel' and request_type ='Complaint'	1. Hover vào chỉ số On -time trên Pie Chart > check hiển thị	1. Khi user hover vào chart sẽ hiển thị số ticket ontime (số nguyên dương)	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_24	[Project health]Check hover  Pie Chart khi chọn type = Complaint		Select * from project_cticket_issue  where ontime_Resolution ='No' and status <> 'Cancel' and request_type ='Complaint'	2. Hover vào chỉ số Late trên Pie Chart > check hiển thị	2. Khi user hover vào chart sẽ hiển thị số ticket late (số nguyên dương)	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_25	[Project health]Check hover  Pie Chart khi chọn type = Complaint		  Select * from project_cticket_issue  where ontime_Resolution IS NULL and status <> 'Cancel' and request_type ='Complaint'	3. Hover vào chỉ số Unassessed trên Pie Chart > check hiển thị	3. Khi user hover vào chart sẽ hiển thị số của ticket Unassessed (số nguyên dương)	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-Ticket Overview_26	[Project health]Check click text On time, Late, Unassessed khi chọn type = Complaint	1. Đăng nhập thành công Dashboard  2. Tại cột Project Name > click 1 Project Operation bất kỳ		1. Click vào text On- time > check hiển thị  2. Click lần nữa vào text On- time  3. Click vào button tròn cạnh text On - time  4. Click lại vào button tròn cạnh text On-Time	1.   -Đổ màu xám chữ On-time   -Data On-time ở Pie Chart biến mất  2. Pie Chart hiển thị lại data On-time, chữ On-time hiển thị màu đen   3. Pie Chart chỉ hiển thị On-time, các chỉ số khác màu xám.  4.  Pie Chart hiển thị đầy đủ như ban đầu  	UI	Low	UNTESTED			UNTESTED
	C-Ticket Overview_27	[Project health]Check click text On time, Late, Unassessed khi chọn type = Complaint			1. Click vào text Late > check hiển thị  2. Click lần nữa vào text Late  3. Click vào button tròn cạnh text Late  4. Click lại vào button tròn cạnh text Late	1.   -Đổ màu xám chữ Late   -Data Late ở Pie Chart biến mất  2. Pie Chart hiển thị lại data Late, chữ Late hiển thị màu đen  3. Pie Chart chỉ hiển thị Late, các chỉ số khác màu xám.  4.  Pie Chart hiển thị đầy đủ như ban đầu	UI	Low	UNTESTED			UNTESTED
	C-Ticket Overview_28	[Project health]Check click text On time, Late, Unassessed khi chọn type = Complaint			1. Click vào text Unassessed> check hiển thị  2. Click lần nữa vào text Unassessed  3. Click vào button tròn cạnh text Unassessed  4. Click lại vào button tròn cạnh text Unassessed	1.   -Đổ màu xám chữ Unassessed   -Data Unassessed ở Pie Chart biến mất  2. Pie Chart hiển thị lại data Unassessed, chữ Unassessed hiển thị màu đen  3. Pie Chart chỉ hiển thị Unassessed, các chỉ số khác màu xám.  4.  Pie Chart hiển thị đầy đủ như ban đầu  	UI	Low	UNTESTED			UNTESTED
Project KPI
	C-Ticket Overview_29	[Project KPI_UI] Check UI của phần header			1. Log in vào hệ thống Dashboard    2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project KPI	- Category "Operation" xuất hiện ở header, cạnh start date and end date của project	UI	Low	UNTESTED			UNTESTED
	C-Ticket Overview_30	[Project KPI_UI] Check phần text của thẻ On-time Response	Chưa liên kết Project C-ticket ở Project Information		1. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project KPI. Check hiển thị text ở label On-time Response  2. Click expand [On-Time Response]	1. Text hiển thị theo format On-time Response: [Data is not available] màu xám  2. Hiển thị icon và text No data	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_31	[Project KPI_UI] Check phần text của thẻ On-time Response	Liên kết Project C-ticket ở Project Information.  Data On-Time Response = 0		1. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project KPI. Check hiển thị text ở label On-time Response  2. Click expand [On-Time Response]	1. Text hiển thị theo format On-time Response: [Data is not available] màu xám  2. Hiển thị chart với Text = Data is not available	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_32	[Project KPI_UI] Check phần text của thẻ On-time Response	Liên kết Project C-ticket ở Project Information.  On-Time Response bị Tailoring		1. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project KPI. Check hiển thị text ở label On-time Response  2. Click expand [On-Time Response]	1. Text hiển thị theo format On-time Response: [Not Applicable] màu xám  2. Hiển thị chart với Text = Not Applicable	UI	High	UNTESTED			UNTESTED
	C-Ticket Overview_33	[Project KPI_UI] Check phần số của thẻ On-time Response	Liên kết Project C-ticket ở Project Information.  On-Time Response không bị Tailoring		1. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project KPI. Check hiển thị  ở On-time Response	1. Số hiển thị các chỉ số LSL, AVG, USL theo setting được setting trong project norm theo query sau trong cột value, giá trị "target_ontime_response_operation"  2. Màu của label:  - On-time response < AVG: Màu đỏ  - AVG ≤ On-time response < USL : Màu Vàng  - On-time response  ≥ USL: Màu xanh  3. Hiển thị trong chart:  - Text ""On-time Response"" ở chính giữa  - Số phần trăm hiển thị: 0, 25, 50, 75, 100%  - Tháng năm hiển thị theo format ""mm/yyyy"", tăng dần từ trái qua phải   Ở dưới cùng hiện text lần lượt: Accumulated On-time Response, AVG, LSL, USL cùng màu tương ứng "	UI	High	UNTESTED			UNTESTED
	C-Ticket Overview_34	[Project KPI_UI] Check UI của phần header			1. Log in vào hệ thống Dashboard    2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project KPI	- Category "Operation" xuất hiện ở header, cạnh start date and end date của project	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_35	[Project KPI_UI] Check phần text của thẻ On-time Resolution	Chưa liên kết Project C-ticket ở Project Information		1. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project KPI. Check hiển thị text ở label On-time Resolution  2. Click expand [On-Time Resolution]	1. Text hiển thị theo format On-time Resolution: [Data is not available] màu xám  2. Hiển thị icon và text No data	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_36	[Project KPI_UI] Check phần text của thẻ On-time Resolution	Liên kết Project C-Ticket ở Project Information.  Data On-Time Resolution = 0		1. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project KPI. Check hiển thị text ở label On-time Resolution  2. Click expand [On-Time Resolution]	1. Text hiển thị theo format On-time Resolution: [Data is not available] màu xám  2. Hiển thị chart với Text = Data is not available	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_37	[Project KPI_UI] Check phần text của thẻ On-time Resolution	Liên kết Project C-Ticket ở Project Information.  On-Time Resolution bị Tailoring		1. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project KPI. Check hiển thị text ở label On-time Resolution  2. Click expand [On-Time Resolution]	1. Text hiển thị theo format On-time Resolution: [Not Applicable] màu xám  2. Hiển thị chart với Text = Not Applicable	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_38	[Project KPI_UI] Check phần số của thẻ On-time Resolution	Liên kết Project C-Ticket ở Project Information.  On-Time Resolution không bị Tailoring		1. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project KPI. Check hiển thị  ở On-time Resolution	1. Số hiển thị các chỉ số LSL, AVG, USL theo setting được setting trong project norm theo query sau trong cột value, giá trị "target_ontime_Resolution_operation"  2. Màu của label:  - On-time Resolution < AVG: Màu đỏ  - AVG ≤ On-time Resolution < USL : Màu Vàng  - On-time Resolution  ≥ USL: Màu xanh  3. Hiển thị trong chart:  - Text ""On-time Resolution"" ở chính giữa  - Số phần trăm hiển thị: 0, 25, 50, 75, 100%  - Tháng năm hiển thị theo format ""mm/yyyy"", tăng dần từ trái qua phải   Ở dưới cùng hiện text lần lượt: Accumulated On-time Resolution, AVG, LSL, USL cùng màu tương ứng "	Function	Medium	UNTESTED			UNTESTED
Project Input
	C-Ticket Overview_39	[Project Input_UI] Check hiển thị màn section On-time Response	1. Click Delivery > Project   2. Tại trường Project Name > click 1 project bất kỳ    3. Click Project Input   		1. Click On-time Response   2. check hiển thị	2. Hiển thị các trường sau   -  filter "Search By Baseline"  - Table: Default hiển thị toàn bộ các bản ghi On-time Response của các trường sau:  +No  +Baseline  +Number of ticket On-time response in month  +Number of ticket response received in month  + Accumulated ticket On-time response  + Accumulated ticket response received  	UI	Low	UNTESTED			UNTESTED
	C-Ticket Overview_40	[Project Input_On time response_filter] Check hiển thị filter Search by Baseline	Open project detail/ Project Input		1. Click on [On time response]   2. Check datetime picker	 1. Hiển thị Date Picker.   - Format: AAA - yyyy  AAA: Ba chữ cái đầu bằng tiếng anh của tháng filter  yyyy: Năm filter    - Enable các tháng theo timeline dự án  → Những tháng nằm ngoài timeline sẽ bị disable   	Function	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_41	[Project Input_On time response_filter_Seach] Check Data default	Open project detail/ Project Input		1. Click on [On time response]   2. Check data default	2. Hiển thị tất cả các data baseline của dự án đã có	Function	High	UNTESTED			UNTESTED
	C-Ticket Overview_42	[Project Input_On time response_filter_Seach] Check kết quả search filter Search by Baseline	Open project detail/ Project Input		1. Click on [On time response]   2. Select From - TO  3. Check data display	3. Hiển thị data theo tháng đã filter	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_43	[Project Input_On time response_filter_Seach] Check kết quả search filter Search by Baseline với is_tailoring = 1	Open project detail/ Project Input	Data tháng baseline bị tailoring	1. Click on [On time response]   2. Select From - TO  3. Check data display	3. Table On time response hiển thị các bản ghi tương ứng theo filter:  No. : thứ tự number của các tháng tương ứng DESC  Baseline: Show all thông tin các tháng có dữ liệu Baseline của dự án  Number of ticket On-time response in month: Not Applicable  Number of ticket response received in month: Not Applicable  Accumulated ticket On-time response: Not Applicable  Accumulated ticket response received: Not Applicable	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_44	[Project Input_On time response_filter_Seach] Check kết quả search filter Search by Baseline với is_tailoring = 0	Open project detail/ Project Input	Data tháng không bị tailoring      CTICKET  SELECT * FROM project_cticket where project_id =  'id dự án'-- → lấy ra project_cticket_id    SELECT * FROM project_cticket_ontime_baseline   where project_cticketid in ( 'project_cticket_id vừa lấy bên trên')  and (year ='năm filter' and month ='tháng filter') and issue_type ='On-time response'"	1. Click on [On time response]   2. Select From - TO  3. Check data display	3. Table On time response hiển thị các bản ghi tương ứng theo filter:  No. : thứ tự number của các tháng tương ứng DESC  Baseline: Show all thông tin các tháng có dữ liệu Baseline của dự án  Number of ticket On-time response in month: Show dữ liệu tương ứng trong DB  Number of ticket response received in month: Show dữ liệu tương ứng trong DB  Accumulated ticket On-time response: Show dữ liệu tương ứng trong DB  Accumulated ticket response received: Show dữ liệu tương ứng trong DB	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_45	[Project Input_UI] Check hiển thị màn section On-time Resolution	1. Click Delivery > Project   2. Tại trường Project Name > click 1 project bất kỳ    3. Click Project Input    4. Click On-time Resolution → check hiển thị		1. Check hiển thị màn section On-time Resolution	1. Hiển thị các trường sau   -  filter "Search By Baseline"  - Table: Default hiển thị toàn bộ các bản ghi On-time Resolution của các trường sau:  +No  +Baseline  +Number of ticket On-time resolution in month  +Number of ticket resolution received in month  + Accumulated ticket On-time resolution  + Accumulated ticket resolution received  	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_46	[Project Input_UI] Check hiển thị màn section On-time Resolution	1. Click Delivery > Project   2. Tại trường Project Name > click 1 project bất kỳ    3. Click Project Input   		1. Click On-time Resolution   2. check hiển thị	2. Hiển thị các trường sau   -  filter "Search By Baseline"  - Table: Default hiển thị toàn bộ các bản ghi On-time Resolution của các trường sau:  +No  +Baseline  +Number of ticket On-time Resolution in month  +Number of ticket Resolution received in month  + Accumulated ticket On-time Resolution  + Accumulated ticket Resolution received  	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_47	[Project Input_On time Resolution_filter] Check hiển thị filter Search by Baseline	Open project detail/ Project Input		1. Click on [On time Resolution]   2. Check datetime picker	 1. Hiển thị Date Picker.   - Format: AAA - yyyy  AAA: Ba chữ cái đầu bằng tiếng anh của tháng filter  yyyy: Năm filter    - Enable các tháng theo timeline dự án  → Những tháng nằm ngoài timeline sẽ bị disable   	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_48	[Project Input_On time Resolution_filter_Seach] Check Data default	Open project detail/ Project Input		1. Click on [On time Resolution]   2. Check data default	2. Hiển thị tất cả các data baseline của dự án đã có	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_49	[Project Input_On time Resolution_filter_Seach] Check kết quả search filter Search by Baseline	Open project detail/ Project Input		1. Click on [On time Resolution]   2. Select From - TO  3. Check data display	3. Hiển thị data theo tháng đã filter	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_50	[Project Input_On time Resolution_filter_Seach] Check kết quả search filter Search by Baseline với is_tailoring = 1	Open project detail/ Project Input	Data tháng baseline bị tailoring	1. Click on [On time Resolution]   2. Select From - TO  3. Check data display	3. Table On time Resolution hiển thị các bản ghi tương ứng theo filter:  No. : thứ tự number của các tháng tương ứng DESC  Baseline: Show all thông tin các tháng có dữ liệu Baseline của dự án  Number of ticket On-time Resolution in month: Not Applicable  Number of ticket Resolution received in month: Not Applicable  Accumulated ticket On-time Resolution: Not Applicable  Accumulated ticket Resolution received: Not Applicable  	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_51	[Project Input_On time Resolution_filter_Seach] Check kết quả search filter Search by Baseline với is_tailoring = 0	Open project detail/ Project Input	Data tháng không bị tailoring      CTICKET  SELECT * FROM project_cticket where project_id =  'id dự án'-- → lấy ra project_cticket_id    SELECT * FROM project_cticket_ontime_baseline   where project_cticketid in ( 'project_cticket_id vừa lấy bên trên')  and (year ='năm filter' and month ='tháng filter') and issue_type ='On-time Resolution'"	1. Click on [On time Resolution]   2. Select From - TO  3. Check data display	3. Table On time Resolution hiển thị các bản ghi tương ứng theo filter:  No. : thứ tự number của các tháng tương ứng DESC  Baseline: Show all thông tin các tháng có dữ liệu Baseline của dự án  Number of ticket On-time Resolution in month: Show dữ liệu tương ứng trong DB  Number of ticket Resolution received in month: Show dữ liệu tương ứng trong DB  Accumulated ticket On-time Resolution: Show dữ liệu tương ứng trong DB  Accumulated ticket Resolution received: Show dữ liệu tương ứng trong DB	UI	Medium	UNTESTED			UNTESTED
	C-Ticket Overview_52	[Project Input_UI] Check hiển thị màn section On-time Resolution	1. Click Delivery > Project   2. Tại trường Project Name > click 1 project bất kỳ    3. Click Project Input    4. Click On-time Resolution → check hiển thị		1. Check hiển thị màn section On-time Resolution	1. Hiển thị các trường sau   -  filter "Search By Baseline"  - Table: Default hiển thị toàn bộ các bản ghi On-time Resolution của các trường sau:  +No  +Baseline  +Number of ticket On-time resolution in month  +Number of ticket resolution received in month  + Accumulated ticket On-time resolution  + Accumulated ticket resolution received  	UI	Medium	UNTESTED			UNTESTED

## Sheet: C-Ticket - Project Information

2	Total	Passed	Failed	Untested	N/A	% Coverage	% Success			 	 	 	 	 
C-ticket_Project_Information	19	0	0	19	0	0	0			 	 	 	 	 
Chrome	19	0	0	19	0	0	0			 	 	 	 	 
Screen	TC ID	Summary	Pre-Condition	Test Data	Test Steps	Expected Result	Type	Priority	Test Result	Remarks	R1 Date	R1 Result	R2 Date	R2 Result	R3 Date	R3 Result
Manual Report
	C-ticket_Project_Information_1	[Project Information_UI] Check category được hiện ra ở màn Project Information			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project (thuộc Operation) muốn chọn > vào Project Information	- Category "Operation" hiện ở bên cạnh MVV trên header	UI	Low	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_2	[Project Information_UI] Check field mới được thêm "Project C-ticket"			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Information  3. Expand phần Project Information	- Field mới "Project C-ticket" nằm dưới "Project Jira"  - Dạng multiselect dropdown  - Hiện 10 bản ghi đầu, user có thể search manually  - Giá trị mặc định: Customer name (Customer key) - Project name 	UI	Low	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_3	[Project Information_UI] Check field mới được thêm "Link" của Project C-ticket			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Information  3. Expand phần Project Information	- Dạng hyperlink > user click vào để dẫn sang hệ thống C-ticket	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED	https://pms.cmcglobal.com.vn/browse/TDXDAS-2309
	C-ticket_Project_Information_4	[Project Information_Function] Check field "Project C-ticket" khi thêm giá trị và lưu thay đổi			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Information  3. Expand phần Project Information  4. Chọn giá trị cho field "Project C-ticket" > click Save changes	- Thông tin vừa điền ở Project C-ticket được lưu lại	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_5	[Project Information_Function] Check Hyperink của field C-Ticket khi thêm giá trị và lưu thay đổi			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Information  3. Expand phần Project Information  4. Chọn giá trị cho field C-Ticket > click Save changes	- Thông tin  hyperlink vừa điền được lưu lại	Function	Medium	UNTESTED			UNTESTED		UNTESTED
	C-ticket_Project_Information_6	[Project Information_Function] Check field "Project C-ticket" khi thêm giá trị và không lưu thay đổi			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Information  3. Expand phần Project Information  4. Chọn giá trị cho field "Project C-ticket" nhưng không click Save changes và refresh màn	- Thông tin vừa điền ở Project C-ticket không được lưu lại	Function	Medium	UNTESTED			UNTESTED		UNTESTED
	C-ticket_Project_Information_7	[Project Information_Function] Check field C-Ticket khi thêm nhiều giá trị và không lưu thay đổi			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Information  3. Expand phần Project Information  4. Chọn nhiều link cho field "C-Ticket" nhưng không click Save changes và refresh màn	- Thông tin hyperlink vừa chọn ở C-ticket không được lưu lại	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_8	[Project Information_Function] Check field "Project C-ticket" khi thêm nhiều giá trị và lưu thay đổi			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Information  3. Expand phần Project Information  4. Chọn 2 giá trị cho field  > click Save changes	- Thông tin hyperlink vừa chọn ở C-ticket được lưu lại	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_9	[Project Information_Function] Checkfield "Project C-ticket" khi thêm giá trị và refresh			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Information  3. Expand phần Project Information  4. Chọn giá trị cho field C-Ticket nhưng không click Save changes và refresh màn  	- Thông tin vừa điền ở 2 field không được lưu lại	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_10	[Project Information_Function] Chọn 1 Project C-ticket đã được link cho 1 project khác 			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Information  3. Expand phần Project Information  4. Chọn 1 c-ticket đã được sử dụng cho project khác 	- Error message "This project C-Ticket has been linked to project ___"  - Không được gán c-ticket đó (đã dùng trước đó) cho project đó (này)	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_11	[Project Information_Function] Chọn nhiều Project C-ticket cho 1 project, tất cả project đều chưa được dùng cho project khác			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Information  3. Expand phần Project Information	- C-ticket được gắn thành công 	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_12	[Project Information_Function] Chọn c-ticket A và B cho 1 project  - C-ticket A chưa được sử dụng cho project nào khác  - C-ticket B đã được sử dụng cho project khác			1. Log in vào hệ thống Dashboard  2. Truy cập vào Deliver > Project > ấn vào Project Name của project muốn chọn > vào Project Information  3. Expand phần Project Information	- Error message "This project C-Ticket B has been linked to project ___"  - - Không gán được c-ticket B cho project đó	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_13	Check data after save and not sync yet	Open Project detail with Category = Operation		1. Select project A on list C-ticket  2. Click on save change.  3. Check data on DB	Data save on table: project.cticket successfully and status = new	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_14	Check data after save and after Sync by manual	Open Project detail with Category = Operation		1. Select project A on list C-ticket  2. Click on save change.  3. Click on Sync data  4. Check data on DB	Data save on table: project.cticket successfully and status = sync	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_15	Check data after save and sync data by auto on the first time	Open Project detail with Category = Operation		1. Select project A on list C-ticket  2. Click on save change.  3. Check data on DB at 4am next day	Data save on table: project.cticket successfully and status = sync  all data is sync on first time	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_16	check data after save and sync data by auto on the second time	Open Project detail with Category = Operation		1. Select project A on list C-ticket  2. Click on save change.  3. Click on sync data  4. Check data on DB on at 4 AM next day	Data save on table: project.cticket successfully and status = sync  just sync data of previous day	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_17	Check sync data before save 	Open Project detail with Category = Operation		1. Select project A on list C-ticket  2. Click on Sync data  3. Check data on DB	cannot be sync data before save	Function	Medium	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_18	Check search data	Open Project detail with Category = Operation		1. click on C-ticket  2. Enter data = PD [random_data]  3. Select C-Ticket data	2. Search data Project C-ticket list follow entered data  3. Data can be select and show on C-Ticket value	UI	Low	UNTESTED			UNTESTED		UNTESTED		UNTESTED
	C-ticket_Project_Information_19	Check edit data on Project information	Open Project detail with Category = Operation		1. Edit data on Project Information  2. Click on Save button  	2. Canbe Edit data  3. Data Save successfully	Function	High	UNTESTED			UNTESTED		UNTESTED		UNTESTED

---

<a id="16-tài-liệu-học-tập-billing-plan-1-pdf"></a>
## 16. Tài liệu học tập_Billing Plan 1.pdf

Dashboard – Module Billing Plan
User Guide_Billing Plan
Oct 2024

Nội dung

01

Tổng quan quy trình
ghi nhận DTSL/DTXHD

02

Hướng dẫn thao tác trên
hệ thống CRM và Dashboard

1.1. Mục đích

➢ Giảm tải việc nhập liệu của người
dùng và hỗ trợ cho công việc ghi
nhận doanh thu sản lượng và
doanh thu xuất hoá đơn

03
04

Một số lưu ý trong
quá trình sử dụng
Q&A

➢ Là nền tảng để xây dựng
module PAKD version tiếp
theo

2

1.2

Đối tượng người dùng

Đối tượng

Nhiệm vụ chính

Admin

Admin của hệ thống, được thao tác toàn bộ trên hệ thống

Sales

Điền thông tin WO và Tạo Payment/Invoice

PMO

Hỗ trợ Sales và phân bổ Billing Plan trên Dashboard

FC & ACC

Kiểm soát việc ghi nhận thông tin và hạch toán doanh thu sản
lượng / doanh thu xuất hoá đơn

BUL/DUL

Xem thông tin trên hệ thống

3

1.3

Workflow

4

2.1

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan

User chọn Tab Delivery >
Sub Tab Billing Plan

5

2.1

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan
1. User chọn button Edit
2. User click button (+) để add DU
thực hiện cho Sub WO Sale
3. User input thông tin Company
Code – DU – Ratecard và dải MM
Billable
4. User click Save để lưu thông tin
đã nhập

Lưu ý:
• Nếu user chọn DU cùng G với BU trên sub WO -> hệ thống disable trường ratecard, data sẽ hiển thị theo dữ liệu setting ratecard trên
Dashboard
• Hệ thống sẽ không cho phép edit MM Billable cho tháng quá khứ
• Hệ thống không cho phép edit MM Billable cho tháng hiện tại nếu tháng hiện tại quá ngày 28
• Nếu user đã nhập MM Billable trong 2 trường hợp trên, hệ thống sẽ không cho phép xoá thông tin Company code – DU - Ratecard
6

2.1

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan

Khi nhập quá MM so với MM của
Sub WO Sale, hệ thống không cho
user Save thông tin và yêu cầu điều
chỉnh lại
User có thể filter các data bị điền
sai để chỉnh sửa lại

7

2.1

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan

Hệ thống sẽ validate nếu user để
trống thông tin Entity Key – DU –
Giá bán ratecard khi ấn Save

8

2.1

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan

User có thể switch sang Sales
Revenue hoặc Delivery Revenue
để xem doanh thu dự kiến
Sales revenue = Unit Price x MM
Billable x ratio của Pipeline
Delivery Revenue = Ratecard x MM
Billable x ratio của Pipeline
Average Price của Sales = Tổng
Sales Revenue / Tổng MM Billable
Average Price của Delivery = Tổng
Delivery Revenue / Tổng MM
Billable

Lưu ý: Hệ thống không tính toán data Revenue và Average Price của Sub WO Sales status = Cancel
9

2.1

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan

User có thể switch giao diện Billing
Plan theo các chiều view khác nhau
• Theo Sub WO Sales
• Theo MVV
• Theo Delivery Unit
Hệ thống sẽ hiển thị thông tin đơn
giá ratecard trung bình và đơn giá
bán cho KH trung bình tuỳ theo
filter của user

10

2.1

Hướng dẫn thao tác trên hệ thống Dashboard – Billing Plan

User tick chọn History > History
Billing Plan để xem lịch sử thay
đổi của data trên Billing Plan

11

2.2

Hướng dẫn thao tác trên hệ thống CRM - Payment

Trên hệ thống PMS, user ấn nút
Create > Chọn Project = Payment
(PAY) và input thông tin

12

2.2

Hướng dẫn thao tác trên hệ thống CRM - Payment

Tại ticket Payment, user chọn type
payment và duration

13

2.2

Hướng dẫn thao tác trên hệ thống CRM - Payment

Hệ thống hiển thị data, user nhập
thông tin> ấn Calculate > Save

Khi user tạo payment có from date < tháng go live module Billing Plan -> trường giá trị SWO – DU – Position sẽ xổ ra giá trị WO key onsite/offshore
Khi user tạo payment có from date >= tháng go live module Billing Plan -> trường giá trị SWO – DU – Position sẽ xổ ra giá trị key mới

14

2.2

Hướng dẫn thao tác trên hệ thống CRM - Payment

Format Key mới của internal payment: Sub Sale WO
Key/Key của Billing Plan (VD: WO-7210/61)

15

2.2

Hướng dẫn thao tác trên hệ thống CRM - Invoice

User tạo ticket Invoice > Add
Payment vào Invoice và Request
Approval

16

2.3

Một số lưu ý trong quá trình sử dụng

1. CRM sẽ không cho phép edit WO tại trạng
thái Issue
2. Nếu WO có MM billable trên Dashboard
ngoài khoảng WO start date – end date -> hệ
thống sẽ không cho user issue WO
3. Để đảm bảo logic hạch toán DTXHD được
chính xác, khi module Billing Plan mới golive
• Team FC và ACC reject các Payment được
tạo theo luồng cũ có start date tại tháng go
live (Có WO onsite/offshore)
• Yêu cầu user tạo payment theo bộ data mới
nếu payment có start date từ tháng go live
trở đi

17

2.3

Một số lưu ý trong quá trình sử dụng

3. Sau khi go live, user sẽ không được tạo WO
Onsite/Offshore trên hệ thống CRM

4. Sau khi go live, phía Admin sẽ ngắt quyền edit trên hệ
thống DPM cũ. User sẽ cần phải chuyển sang hệ thống
Billing Plan để dải MM Billable

18

2.3

Một số lưu ý trong quá trình sử dụng

5. Do với module mới, user sẽ không tạo WO
onsite/offshore nên để hiển thị các cột DU trên
business plan, user phải input thông tin DU cho
mã vụ việc tương ứng tại màn hình Billing Plan

19

03

Q&A

20

---

<a id="17-tài-liệu-vận-hành-dashboard-01102024-docx-xlsx"></a>
## 17. Tài liệu vận hành Dashboard_01102024.docx.xlsx

## Sheet: Architecture overview

	System Overview
																	This platform a simplified architecture with Spring boot and Reactjs 15. 
																	No 	Layer 	Description 
																	1 	Client App 	Bao gồm Web App tương tác với người dùng 
																	2 	Web-API 	Bao gồm 6 service Backend xử lý logic và dữ liệu. 
																	3 	Database 	Sử dụng MySQL database dể lưu trữ dữ liệu. 
																	4 	RabbitMQ 	Hỗ trợ quản lý hàng đợi cho một số chức năng bất đồng bộ. 
																	5 	POA 	Đồng bộ data nhân sự và phòng ban 
																	6 	HRMS 	Đẩy thông tin về dự án của các nhân sự cho HRMS 
																	7 	Skillset 	Đồng bộ thông tin về Skill của nhân sự 
																	8 	PMS 	Đồng bộ data về issues của các dự án trên jira 
																	3. Applications (Client Web Apps) 
																	No. 	Application Type 	Technology 	Description 
																	1 	Web Application 	HTML5/CSS3 
																			ant design 3 	UI components library. 
																			Reactjs 15 	Web Front-end framework. 
																			Scss 1.69 	Css preprocessor 
																			Redux/flux 8 	State management library 
	Low - Level Design

	3. Layer

	4. Package layer
																No. 	Group 	Component 	Purpose 
																1 	Common 	Annotation 	Là thư mục chứa các custom Annotation 
																		Enums 	Là thư mục chứa các common enums 
																		Exception 	Là thư mục chứa các common Exception 
																		Handler 	Là thư mục chứa các lớp handler exception 
																2 		Controller 	Là lớp định nghĩa các API của hệ thống 
																3 		Utils 	Là thư mục chứa các lớp định nghĩa Constant, common function 
																4 		Services 	Là thư mục chứa các lớp, interface chung 
																5 		Repository 	Là thư mục chứa các lớp thực hiện các query dữ liệu, kết nối với database 
																6 		Config 	Là thư mục chứa các lớp định nghĩa các config của hệ thống 
																7 		Dto 	This layer is data transfer object layer. Data that used to exchange between layers and other systems is defined in here 
																8 		Logic 	Lớp này định nghĩa các xử lý logic common của hệ thống. 
																9 		Listener 	Là thư mục chứa các lớp listener 
																10 		Mapper 	Là thư mục chứa các lớp định nghĩa Mapper giữa Entity và Dto 
																11 		Model 	Là thư mục chứa các lớp định nghĩa các các base Entity 
	Deploy overview

## Sheet: Thông tin server

	1 	Web Server 	Dell Intel / 20 GB RAM 	For hosting web app. 
			OS: CentOS 7 
	2 	Application Server  	Dell Intel / 20GB RAM 	For hosting Server app 
			OS: CentOS 7 
	3 	Database Server 	Dell Intel / 12 GB RAM 	For install MySQL  
			OS: CentOS 7 
Source	Description	Environment	Target	Description	Environment	Ports	Way	Thời hạn rule FW	Note
10.62.5.5	Dashboard	prod	https://auth-api.cmcglobal.com.vn	POA	prod	443	1 chiều	Never Expired	Kết nối qua Ha Internal	Ha Internal =>10.62.5.5
192.168.72.70	Dashboard	uat	https://api-auth-aut.cmcglobal.com.vn	POA	uat		1 chiều	Never Expired	Kết nối qua Ha Internal	Ha Internal =>192.168.72.70
192.168.66.16	Dashboard	dev/test	192.168.66.17	POA	dev/test	5000	1 chiều	Never Expired	Cùng VLAN => K có rule Firewall
10.62.5.5	Dashboard	prod	https://pms.cmcglobal.com.vn	PMS	prod		1 chiều	Never Expired	Kết nối qua Ha Internal	Ha Internal =>10.62.5.5
192.168.72.70	Dashboard	uat	https://pms-uat.cmcglobal.com.vn	PMS	uat		1 chiều	Never Expired	Kết nối qua Ha Internal	Ha Internal =>192.168.72.70
192.168.66.16	Dashboard	dev/test	192.168.66.154	PMS	dev/test	8080	1 chiều	Never Expired	Cùng VLAN => K có rule Firewall
10.62.5.5	Dashboard	prod	https://hrms-be.cmcglobal.com.vn	HRMS	prod		1 chiều	Never Expired	Kết nối qua Ha Internal	Ha Internal =>10.62.5.5
192.168.72.70	Dashboard	uat	https://hrms-uat-backend.cmcglobal.com.vn	HRMS	uat		1 chiều	Never Expired	Kết nối qua Ha Internal
192.168.66.16	Dashboard	dev/test	192.168.66.38	HRMS	dev/test	8762	1 chiều	Never Expired	Cùng VLAN => K có rule Firewall
10.62.5.5	Dashboard	prod	https://skills.cmcglobal.com.vn	Skillset	prod		1 chiều	Never Expired	Kết nối qua Ha Internal	Ha Internal =>10.62.5.5
192.168.72.70	Dashboard	uat	https://skill-uat-api.cmcglobal.vn	Skillset	uat		1 chiều	Never Expired	Kết nối qua Ha Internal	Ha Internal =>192.168.72.70
192.168.66.16	Dashboard	dev/test	192.168.66.91	Skillset	dev/test	8083	1 chiều	Never Expired	Cùng VLAN => K có rule Firewall
10.62.5.5	Dashboard	prod	https://db-rabbitmq.cmcglobal.com.vn/	RabbitMQ-DB	prod		1 chiều	Never Expired	Kết nối qua Ha Internal	Ha Internal =>10.62.5.5
192.168.72.70	Dashboard	uat	192.168.72.70	RabbitMQ-DB	uat	5672	1 chiều	Never Expired	Cùng VLAN => K có rule Firewall
192.168.66.16	Dashboard	dev/test	192.168.66.16	RabbitMQ-DB	dev/test	5672	1 chiều	Never Expired	Cùng VLAN => K có rule Firewall

## Sheet: source code

	Frontend: https://source.cmcglobal.com.vn/it-tools/dashboard-phase2-frontend 
	Backend: https://source.cmcglobal.com.vn/it-tools/dashboard-phase2-backend 
	Delivery: https://source.cmcglobal.com.vn/tdx/delivery-project 
	Refactor: https://source.cmcglobal.com.vn/it-tools/refactor-dashboard 							Including Group, Master-data, Timesheet 

	Sale: https://source.cmcglobal.com.vn/tdx/sale-project 
	Common: https://source.cmcglobal.com.vn/tdx/glbnb2300501-dashboard/dashboard-common 

## Sheet: Danh sách API

	Backend: https://dashboard-backend-uat.cmcglobal.vn/swagger-ui/index.html 
	Delivery: https://dashboard-delivery-uat.cmcglobal.vn/swagger-ui/index.html 
	Group: https://dashboard-group-uat.cmcglobal.vn/swagger-ui/index.html 
	Master data: https://dashboard-masterdata-uat.cmcglobal.vn/swagger-ui/index.html 
	Timesheet: https://dashboard-timesheet-uat.cmcglobal.vn/swagger-ui/index.html 
	Sale: https://dashboard-sale-uat.cmcglobal.vn/swagger-ui/index.html 

## Sheet: Run book

	Lưu ý
	Cần chạy service group trước khi chạy các service còn lại 
	FE khi build cần sửa config profile trong file webpack.config.docker.js

---

<a id="18-dashboard---menubar-user-guide-v1-0-docx"></a>
## 18. Dashboard - Menubar_User guide_V1.0.docx (from `User Guide/`)

|  | **<****Dashboard****>** |
| --- | --- |

| <Dashboard> |  |  |
| --- | --- | --- |

User Guide 

Menu bar Dashboard

**Version****: ****0****1**

| **Issue****d**** Status:** | Issued |
| --- | --- |
| **Issued**** Date:** | 01-23-2024 |
| **Owner:** | Bộ phận TDX |
| **Author:** | ltphuong6 |
| **Location:** | <Project Repository> |
| **Confidential Class:** | Internal Use |

| **Date****:** | <30-01-2024> |
| --- | --- |
| **Approved by:** | <ntson4> |
| **Signature:** |  |

**Review Information**

| **Role** | **Required / Suggested** | **Comment** |
| --- | --- | --- |
|  |  |  |
|  |  |  |

**Approval Information**

| **Approver Name** | **Role** | **Date** (mm-dd-yyyy) | **Revision** | **Comment** |
| --- | --- | --- | --- | --- |
|  |  |  |  |  |
|  |  |  |  |  |

**Revision History**

| **Revision** | **Date** (mm-dd-yyyy) | **Description** | **Revised by** | **Reviewer** | **Date** (mm-dd-yyyy) |
| --- | --- | --- | --- | --- | --- |
| *[**0.1**]* | *[**23**-01-2024**]* | *[First **Version**]* |  |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |

**T****able of Content**

1	Introduction	2

2	Menu bar mới	2

2.1	Tab Dashboard	2

2.2	Tab Delivery	2

2.3	Tab Task	3

2.4	Tab Setting	4

Introduction

Với việc đẩy mạnh phát triển các tính năng trên hệ thống Dashboard, phục vụ cho các hoạt động báo cáo và số hóa quy trình nghiệp vụ các phòng ban, menubar (thanh điều hướng) hiện tại của hệ thống Dashboard chưa đáp ứng được điều này. 

Vì vậy, dự án phát triển thiết kế menu bar mới cho hệ thống để việc phát triển các tính năng dễ dàng hơn đồng thời tối ưu trải nghiệm người dùng khi sử dụng hệ thống.

Mục đích của tài liệu này nhằm giới thiệu và hướng dẫn người dùng sử dụng menubar mới trên hệ thống Dashboard.

Menu bar mới 

Tab Dashboard

Tab Dashboard tổng hợp các module báo cáo bao gồm các module Global, Sales, Delivery và 1 module Dashboard settings để thiết lập cài đặt liên quan đến báo cáo.

*Với modules Global, Sales, Delivery:*

Người dùng click vào icon Expand “>” ở các tab trên cột menu. Khi đó, hệ thống hiển thị menu các sub-tab  như sau:

*Với module** Dashboard Setting:*

Admin truy cập vào module Dashboard Setting > Hệ thống hiện thị các section như bên dưới

Tab Delivery

Tab Delivery: Tổng hợp các module về vận hành Delivery bao gồm các modules như mục 2 trong ảnh.

*Với **Mod**ule Project List:*

- Người dùng chọn tab Project List trên thanh side menu bar 

- Hệ thống hiển thị bảng danh sách các dự án 

- Người dùng click vào hyperlink tên dự án muốn xem chi tiết ở bảng.

- Hệ thống hiển thị màn Project details gồm các các sub-tab như ảnh:

Trong đó:

- Phần Project Inputs gồm các section như sau:

- Phần Project Information gồm các section như sau:

Tab Tasks

Tab Tasks: Tập trung các module liên quan đến request và phê duyệt (VD: Phê duyệt mở/cập nhật dự án)

Tab Setting

Tab Setting: Tập trung các module setting hệ thống Dashboard

					

| CMC Global | Internal Use | Page 1/1 |
| --- | --- | --- |

| CMC Global | Internal Use | Page ii |
| --- | --- | --- |

---

<a id="19-dashboard---tms-user-guide-v1-0-pptx"></a>
## 19. Dashboard - TMS_User guide_V1.0.pptx (from `User Guide/`)

## Slide 1

Dashboard System - Actual Timesheet module
User Guide

September 2023

## Slide 2

Content

3.   Tính năng Actual Timesheet

4.   Report Actual Timesheet

2.   Tổng quan về Timesheet

5.   Những ảnh hưởng của chức năng timesheet tới việc quản lý dự án

1.   Hiện trạng

6.   Q&A

## Slide 3

1. Hiện trạng

## Slide 4

1.1   Hiện trạng

Hiện tại việc quản lý nhân sự và thời gian nhân sự tham gia vào dựa án chỉ dựa trên Resources Allocation được DU lead allocate đầu tháng (dựa trên planning)
Điều này dẫn đến không có con số chính xác về nhân sự thực tế tham gia vào dự án và thời gian làm việc thực tế của họ
 Sai lệch về chi phí dự án
 Sai lệch về timesheet và hóa đơn xuất cho khách hàng hàng tháng

1.2. Mục đích

Chức năng timesheet hỗ trợ theo dõi và quản lý thời gian thực tế của nhân sự tham gia vào dự án
Là cơ sở để tính toán chi phí dự án => KPI của dự án
Là cơ sở để xuất hóa đơn doanh thu cho dự án gửi khách hàng

## Slide 5

2. Tổng quan về Timesheet

## Slide 6

2.2. Tổng quan màn Timesheet

2. Tổng quan về Timesheet

2.1. Mục tiêu khoá training

## Slide 7

2.1. Mục tiêu khóa training

Hiểu và sử dụng được chức năng timesheet phục vụ trong công việc quản lý dự án hàng ngày
Hiểu ý nghĩa, mục đích và tầm quan trọng của tính năng trong quy trình quản lý dự án

## Slide 8

2.2. Tổng quan màn timesheet

Resources allocation

Actual timesheet

Actual billable

Từ những dự án được khởi tạo phân bổ nguồn lực dự kiến vào các dự án

Dựa trên thời gian của các nhân sự được phân bổ vào dự án và thời gian chấm công phê duyệt thời gian tham gia dự án của nhân sự

Dự trên số liệu actual timesheet đã được phê duyệt, chỉnh sửa và xuất timesheet cho khách hàng phê duyệt xuất hóa đơn

## Slide 9

2.2. Tổng quan màn timesheet

Chọn Timesheet

Tính năng của Timesheet

Body Timesheet

## Slide 10

3. Tính năng Actual Timesheet

## Slide 11

3.3. Edit actual timesheet

3. Tính năng Actual Timesheet

3.4. Approve actual timesheet

3.2. View và filter actual timesheet

3.1. Giới thiệu tính năng actual timesheet

3.5. Delete actual timesheet

## Slide 12

3.1. Giới thiệu tính năng actual timesheet

Filter tìm kiếm

Điều kiện cần: User phải add resource plan ở trong function Resource – tab Delivery

Resource được add

Lưu ý: 
Nhân sự được add resource phải ở trạng thái allocated thì mới đồng bộ sang màn Actual TS
Sau ngày 28 hàng tháng user không được thêm mới, sửa, xoá resource plan của tháng hiện tại

## Slide 13

3.1. Giới thiệu tính năng actual timesheet

Actor vào được tab Timesheet: BOM/DUL/PM/PMO/QA/SEPG (Phải được set quyền trên POA)

Chọn Timesheet

Tính năng của Timesheet

Body Timesheet

## Slide 14

3.2. View và filter actual timesheet

Chức năng Filter by users

Chọn tháng xem Timesheet

Chọn Filter by user

Chọn tên user

Chọn mvv

Kết quả filter

## Slide 15

3.2. View và filter actual timesheet

Tên nhân sự

Tổng thời gian làm việc trong tuần/tháng

Dự án nhân sự tham gia

Dữ liệu từ HRMS

Dữ liệu từ resource plan

Chức năng Filter by users

## Slide 16

3.2. View và filter actual timesheet

Chức năng Filter by users

Màu hiển thị trạng thái hiện tại
Cam: Timesheet data chưa được approve
Xanh: Timesheet data đã được approve

Màu dot cảnh báo chênh lệch về Hours per day Actual Timesheet dự án và giờ HRMS
Xanh: Số giờ trên HRMS >= Actual Timesheet
Vàng: 0 < HRMS < Actual Timesheet
Đỏ: Số giờ trên HRMS = 0 và Actual Timesheet >0

## Slide 17

3.2. View và filter actual timesheet

Chức năng Filter by projects

Tên dự án

Nhân sự của dự án

Resource được add từ Delivery

Dữ liệu từ HRMS

Dữ liệu từ resource plan

## Slide 18

3.2. View và filter actual timesheet

Chức năng Filter by projects

Màu hiển thị trạng thái hiện tại
Cam: Timesheet data chưa được approve
Xanh: Timesheet data đã được approve

Màu dot cảnh báo chênh lệch về Hours per day Actual Timesheet dự án và giờ HRMS
Xanh: Số giờ trên HRMS >= Actual Timesheet
Vàng: 0 < HRMS < Actual Timesheet
Đỏ: Số giờ trên HRMS = 0 và Actual Timesheet >0

## Slide 19

3.3. Edit actual timesheet

Bước 1: Trỏ chuột vào ô muốn sửa dữ liệu
Bước 2: Click chuột để hiển thị popup Edit

## Slide 20

3.3. Edit actual timesheet

Bước 3: Sửa thông tin actual timesheet và ấn Save

Lưu ý: 
Trường hợp user edit bản ghi Resource allocation của nhân sự, hệ thống sẽ update trường hours per day của bản ghi timesheet và chuyển status bản ghi từ approved thành pending (Nếu bản ghi đã được approve từ trước

## Slide 21

3.4. Approve actual timesheet – Cách 1

Bước 1: Nhấn chọn vào ô muốn approve hoặc kéo thả chuột vào các ô muốn approve 

## Slide 22

3.4. Approve actual timesheet – Cách 1

Bước 2: Khi hiển thị popup confirm, ấn chọn OK để approve
Data đã được approve sẽ có dot xanh để phân biệt

## Slide 23

3.4. Approve actual timesheet – Cách 2

Bước 1: Tick chọn timesheet của các nhân sự/dự án muốn approve. Có thể tick chọn timesheet đầu tiên để tick chọn tất cả nhân sự/dự án
Bước 2: Ấn nút Approve

## Slide 24

3.4. Approve actual timesheet – Cách 2

Bước 3: Khi hiển thị popup confirm, ấn chọn OK để approve
Data đã được approve sẽ có dot xanh để phân biệt

## Slide 25

3.4. Approve actual timesheet – Cách 3

Rule approve tự động của hệ thống:
Vào ngày 28 hàng tháng hệ thống sẽ tự động phê duyệt toàn bộ các timesheet ở trạng thái pending từ ngày 26 tháng trước đến ngày 25 của tháng hiện tại.
Thời gian được approve sẽ là số giờ Actual Timesheet của nhân sự tại thời điểm hệ thống tự động phê duyệt

## Slide 26

3.4. Delete actual timesheet

Trong trường hợp user tạo sai bản ghi actual timesheet:
User: Xoá bản ghi resource allocation của nhân sự
System: Hệ thống sẽ dựa trên các thông tin của data allocation được xoá, để xoá bản ghi actual timesheet tương ứng
Thông tin data allocation hệ thống dựa vào để xoá bản ghi actual timesheet tương ứng bao gồm:
Project nhân sự được allocate
Mã vụ việc nhân sự được allocate
Ngày nhân sự được allocate

## Slide 27

4. Report Actual Timesheet

## Slide 28

5. Những ảnh hưởng của chức năng timesheet tới việc quản lý dự án

## Slide 29

5. Những ảnh hưởng của chức năng timesheet tới việc quản lý dự án

5.1. Kiểm soát chi phí dự án
Việc tracking timesheet của các nhân sự trong dự án sẽ là cơ sở để tính chi phí cho dự án và ảnh hưởng trực tiếp đến đánh giá KPI tại các giai đoạn của dự án.
Việc kiểm soát timesheet sẽ giúp các cấp quản lý nhận ra sự chênh lệch về hiệu quả dự án giữa planning (resources allocation) và thực tế (actual timesheet) để có thể đưa ra các giải pháp kịp thời.
5.2. Xuất hóa đơn cho khách hàng
Dữ liệu phê duyệt timesheet là cơ sở để sale làm timesheet và xuất hóa đơn doanh thu dự án cho khách hàng 
Tiết kiệm thời gian cho sale trong việc đi xin timesheet để phê duyệt với khách hàng từ các DU
=> Vì vậy việc phê duyệt thời gian làm việc sẽ ảnh hưởng lớn đến giá trị doanh thu xuất hóa đơn cho khách hàng hàng tháng.

## Slide 30

6. Q&A

## Slide 31

Lưu ý:
Các thông tin thắc mắc khi sử dụng hệ thống vui lòng tạo Ticket lên Service Center

Steps tạo Ticket
1. Truy cập link: FIS - CMC Global JIRA
2. Chọn “Create”
3. Chọn “Project”: Service Center(SC)
     Issue Type: Dashboard
4. Điền các thông tin bắt buộc
5. Chọn “Save” để tạo ticket

## Slide 32

---

<a id="20-dashboard-module-business-plan-user-guide-v1-0-pptx"></a>
## 20. Dashboard_Module Business plan_User guide_V1.0.pptx (from `User Guide/`)

## Slide 1

Dashboard – Module business plan

July 2024

User Guide_BOM/ G lead/ DU lead/ BU lead/ FC/ Sale

## Slide 2

Nội dung

03

02

01

Tổng quan module phương án kinh doanh

Hướng dẫn thao tác trên hệ thống CRM và Dashboard

Q&A

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE PHƯƠNG ÁN KINH DOANH

1.2. Tổng quan workflow của phê duyệt phương án kinh doanh

1.3. Đối tượng người dùng

## Slide 4

1.1

MỤC ĐÍCH

Giúp giảm tải công việc trình ký phê duyệt phương án kinh doanh của bộ phận Sales. 
Users dễ dàng nắm bắt quá trình phê duyệt của bộ phận phê duyệt phương án kinh doanh.
Quản lý tập trung dữ liệu về phương án kinh doanh (dự án) trên Dashboard

## Slide 5

1.2

Tổng quan workflow phê duyệt phương án kinh doanh

## Slide 6

1.3

Đối tượng người dùng

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được thao tác toàn bộ trên hệ thống
AM
- Tạo PC ở CRM
- Nhập data tại màn Business plan detail
- Submit PAKD để chuyển sang giai đoạn phê duyệt PAKD
DU
- DU làm PASX qua file excel và điền số lên hệ thống
FC
- Activate PC để tạo thành PAKD trên hệ thống dashboard
- Nhập/ chỉnh sửa data tại màn Business plan detail (optional)
DU Lead/ BU Lead/ G Lead/ FC/ BOM
Kiểm tra và phê duyệt PAKD

## Slide 7

Activate PC trên hệ thống CRM

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

View PAKD, danh sách PAKD trên hệ thống Dashboard

So sánh các version của PAKD

Phê duyệt PAKD

Nhập data vào PAKD

## Slide 8

2.1

Active PC trên hệ thống CRM và tạo PAKD trên Dashboard

Quy trình để có phương án kinh doanh (PAKD) tại màn business plan list:
AM tạo Client trên hệ thống CRM
AM tạo Pipeline trên hệ thống CRM
AM tạo Project code (PC) trên hệ thống CRM
FIN activate PC trên hệ thống CRM
View PAKD vừa được tạo ratrên hệ thống Dashboard
***PC project code: GLBPB2400067

## Slide 9

2.2

View PAKD trên hệ thống Dashboard

Sau khi FIN activate PC trên hệ thống CRM
-> PC chuyển status sang Active và từ PC đó chuyển thành PAKD trên hệ thống Dashboard
PAKD được activate sang DB gần nhất sẽ hiển thị ở đầu tiên trong list PAKD
Sau khi đăng nhập vào hệ thống Dashboard: user vào
Delivery > Business plan list > Tìm MVV vừa activate > view detail
User có thể search PAKD, lọc PAKD theo bộ lọc, và view detail PAKD

## Slide 10

2.3

Nhập data vào PAKD

AM là người input data trong phần General informtaion
Có 4 trường thông tin bắt buộc trong Collaborator
AM information, PM information sẽ luôn có 1 ldap được đồng bộ từ PC
-> User không sửa, xoá được AM, PM default này
Exchange rate, software development fee được đồng bộ từ CRM và user được sửa data

## Slide 11

2.3

Nhập data vào PAKD

Có 6 phần chính trong Business plan
-    Unit price & MM bill
-    Revenues
Cost
Expenses
Summary margin
Reference 

## Slide 12

2.3

Nhập data vào PAKD

Trường CIT and VAT, Project bonus/MM và Billable rate norm là những trường đã có giá trị default, cho phép các bộ phận sửa data khi status = draft
Cột internal của phần Delivery expenses cho phép FIN được điền data
Mỗi đề mục trong phương án kinh doanh đều có tooltip giải thích về định nghĩa và công thức
Trong quá trình phê duyệt, FIN được sửa data ở 3 trường CIT and VAT, Project bonus/MM và Billable rate norm 

## Slide 13

2.3

Nhập data vào PAKD

- Sau khi chọn file cần upload -> user chọn category của file và DU mà có thể download file đó
- Chỉ user mới được xoá document do chính user đó upload 

- AM gửi Delivery plan template này cho DU -> DU điền data
- DU gửi lại template đã điền và AM gắn file đã điền ở mục Documents
-> Phải có ít nhất 1 file được upload -> PAKD mới được submit
- Maximun file size: 50MB – Chỉ nhận documents dạng PNG, JPG, JPEG, PDF, word, excel

## Slide 14

2.3

Nhập data vào PAKD

Những user được quyền view detail PAKD sẽ có quyền add comments
Hiển thị comment mới nhất ở vị trí đầu tiên trong comment list

## Slide 15

2.3

Nhập data vào PAKD

History lưu lại sự thay đổi liên quan đến PAKD
Sự thay đổi về số liệu trong PAKD
User đã add comments
User đã upload documents, delete documents
User đã approve/reject/delegate PAKD

## Slide 16

2.3

Nhập data vào PAKD

Save: lưu data đã thay đổi

Sau khi điền hết thông tin vào các mục
-> AM submit PAKD để chuyển sang luồng phê duyệt

## Slide 17

2.4

Phê duyệt PAKD

Sau khi submit PAKD, AM muốn theo dõi quy trình phê duyệt thì AM vào Delivery > Business plan list > View detail
User không dược assign trong luồng phê duyệt -> sẽ không thấy button phê duyệt

## Slide 18

2.4

Phê duyệt PAKD

User được assign trong luồng phê duyệt:
- User vào Tasks > business plan request
- User thấy danh sách những task được assign -> tìm task cần phê duyệt > View detail -> phê duyệt

## Slide 19

2.4

Phê duyệt PAKD

Sau khi kết thúc luồng phê duyệt (tất cả user đã approve)
-> AM có thể create new version

## Slide 20

2.5

So sánh các version của PAKD

Tại version mới, data được clone từ version trước đó
User thay đổi data ở version 2 -> đổi PAKD về version 1 để thấy sự thay đổi (tăng giảm)

## Slide 21

03

Q&A

## Slide 22

---

<a id="21-dashboard-module-kpi-user-guide-v1-0-pptx"></a>
## 21. Dashboard_Module KPI_User guide_V1.0.pptx (from `User Guide/`)

## Slide 1

Dashboard – Module chỉ số KPI chất lượng dự án

February 2024

User Guide_ QA/ PM

## Slide 2

Nội dung

03

02

01

Tổng quan module chỉ số KPI chất lượng dự án

Hướng dẫn thao tác trên hệ thống Jira và Dashboard

Q&A

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE CHỈ SỐ KPI CHẤT LƯỢNG DỰ ÁN

1.2. Quy trình nhập data input cho chỉ số KPI chất lượng dự án

## Slide 4

1.1

MỤC ĐÍCH

Tạo quy trình và số hóa quá trình liên quan đến các chỉ số chất lượng dự án
Giúp giảm tải công việc tổng hợp data KPI chất lượng dự án và các công việc phối hợp giữa QA và PM. Users dễ dàng nắm bắt thông tin chỉ số chất lượng dự án.
Quản lý tập trung dữ liệu KPI Dự án trên hệ thống Dashboard

## Slide 5

1.1

Quy trình nhập data input chỉ số KPI chất lượng 

## Slide 6

1.1

Quy trình nhập data input chỉ số KPI chất lượng 

Project Jira: Sau khi Jira và Dashboard của dự án được tạo ra, phía SEPG sẽ điền Jira key vào dự án trên Dashboard.  
Data Jira của dự án sẽ được đồng bộ sang dự án trên Dashboard tương ứng

## Slide 7

1.1

Quy trình nhập data input chỉ số KPI chất lượng 

Đối với data bug, leakage, deliverables: User nhập data bên Jira, hệ thống Dashboard sẽ đồng bộ và tính toán chỉ số KPI chất lượng tương ứng 

Đối với data PCV và CSS: QA dự án nhập trên hệ thống Dashboard 

## Slide 8

1.2

Đối tượng người dùng và nhiệm vụ chính

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được thao tác toàn bộ trên hệ thống
SEPG
Tạo Jira của dự án và gắn Jira của dự án lên Dashboard
PM
Tổng hợp data, kiểm tra thông tin về chỉ số chất lượng dự án
QA
Đo lường chỉ số chất lượng dự án và nhập data
DU Lead
Tiếp nhận thông tin và quản lý thông tin chỉ số chất lượng dự án

## Slide 9

Deliverables

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

Bugs

PCV

CSS

Leakages

Tailoring chỉ số KPI

## Slide 10

2.1

Nhập data về Deliverables của dự án

Dự án sử dụng Jira của CMC Global – Release theo ticket

Users tạo ticket có issue type = Bug/Leakage/Story/Task
User sẽ phải chọn trường Deliverables để xác định ticket có liên quan đến công việc deliver cho khách hàng hay không
Các ticket được xem là delivered deliverables cần đáp ứng điều kiện sau: 
Resolution = Done 
Deliverables? = Yes 
Resolved date khác blank 
Xác định ticket delivered trong tháng nào dựa trên tháng của Resolved date 
Các ticket được xem là On time deliverables cần đáp ứng điều kiện sau: 
Resolution = Done 
Deliverables? = Yes
Resolve Date =< Due Date 
Xác định ticket delivered trong tháng nào dựa trên tháng của Resolved date 

## Slide 11

2.1

Nhập data về Deliverables của dự án

Dự án sử dụng Jira của CMC Global – Release theo package

Users tạo ticket có issue type = Delivery
QA sẽ chọn trường Delivery result để xác định package dự án deliver cho khách hàng là On time hoặc Late
Package được xem là delivered deliverables cần đáp ứng điều kiện sau: 
Status = Package Released
Due date khác blank
Delivery result khác None 
Xác định package delivered trong tháng nào dựa trên tháng của Resolved date 
Package được xem là On time deliverables cần đáp ứng điều kiện sau: 
Status = Closed (QA dự án chuyển trạng thái từ Package Released sang Closed)
Due date khác blank
Delivery result = On time
Resolved date <= Due date
Xác định package delivered trong tháng nào dựa trên tháng của Resolved date 

## Slide 12

2.1

Nhập data về Deliverables của dự án

Dự án sử dụng Jira của khách hàng

Nếu dự án sử dụng Jira khách hàng, phía SEPG sẽ tạo Jira nội bộ CMC tương ứng để quản lý nội bộ
QA/PM dự án tạo ticket có issue type = Number of deliverables
Form ticket yêu cầu điền thông tin sau
Summary
Baseline date: Thời điểm chốt số liệu
Số lượng deliverable đã delivered trong tháng
Số lượng deliverable được delivered On time trong tháng 

## Slide 13

2.1

Nhập data về Deliverables của dự án

Dự án sử dụng Jira của khách hàng

Chỉ có QA, QAL và PM được hoạt động trong luồng flow này 
Chỉ role QA/PM được khởi tạo và edit issue type Number of Deliverables
Người khởi tạo chuyển trạng thái sang review 
Chỉ PM/QA chuyển trạng thái Review sang Resolved. Nếu QA là ng tạo ticket thì chỉ PM đc chuyển, nếu PM tạo ticket thì chỉ QA đc chuyển  
Chỉ Reporter mới có quyền chuyển từ Resolved sang Baselined 
Tại Status = Baselined thì không thể edit. Nếu cần edit thì phải chuyển về trạng thái khác Cancel 
Chỉ có QAL mới có quyền chuyển từ Baselined sang các status khác 

## Slide 14

2.1

Nhập data về Deliverables của dự án

Data về deliverables bên Jira sẽ hiển thị trên Dashboard của dự án

Lưu ý: 
Đối với issue type = Number of Deliverables, hệ thống sẽ chỉ tính timeliness cho những ticket có status = Baselined sau ngày cuối cùng của tháng

## Slide 15

2.2

Nhập data về Bug của dự án

Dự án sử dụng Jira của CMC Global 

Users tạo ticket có issue type = Bug
Hệ thống Dashboard sẽ đếm số lượng bug luỹ kế 
Không tính các bug có status = Cancel

## Slide 16

2.2

Nhập data về Bug của dự án

Dự án sử dụng Jira của khách hàng

Nếu dự án sử dụng Jira khách hàng, phía SEPG sẽ tạo Jira nội bộ CMC tương ứng để quản lý nội bộ
QA/PM dự án tạo ticket có issue type = Number of Bugs để nhập số lượng bug luỹ kế của dự án
Form ticket yêu cầu điền thông tin sau
Summary
Number of items: Số lượng bug luỹ kế
Related activity has not happened?: Chỉ tick chọn khi hoạt động testing của dự án chưa diễn ra
Baseline date: Thời điểm chốt số liệu
Severity: Mức độ nghiêm trọng của bug

## Slide 17

2.2

Nhập data về Bug của dự án

Dự án sử dụng Jira của khách hàng

Chỉ có QA, QAL và PM được hoạt động trong luồng flow này 
Chỉ role QA/PM được khởi tạo và edit issue type Number of Bugs
Người khởi tạo chuyển trạng thái sang review 
Chỉ PM/QA chuyển trạng thái Review sang Resolved. Nếu QA là ng tạo ticket thì chỉ PM đc chuyển, nếu PM tạo ticket thì chỉ QA đc chuyển  
Chỉ Reporter mới có quyền chuyển từ Resolved sang Baselined 
Tại Status = Baselined thì không thể edit. Nếu cần edit thì phải chuyển về trạng thái khác Cancel 
Chỉ có QAL mới có quyền chuyển từ Baselined sang các status khác 

## Slide 18

2.2

Nhập data về Bug của dự án

Data về số lượng bug luỹ kế bên Jira sẽ hiển thị trên Dashboard của dự án

Lưu ý: 
Hệ thống Dashboard sẽ baseline số lượng bug luỹ kế sau ngày cuối cùng của tháng
Đối với issue type = Number of Bugs, hệ thống sẽ chỉ tính số liệu cho những ticket có status = Baselined

## Slide 19

2.3

Nhập data về Leakage của dự án

Dự án sử dụng Jira của CMC Global 

Users tạo ticket có issue type = Leakage
Hệ thống Dashboard sẽ đếm số lượng leakage luỹ kế từng tháng
Không tính các leakage có status = Cancel

## Slide 20

2.3

Nhập data về Leakage của dự án

Dự án sử dụng Jira của khách hàng

Nếu dự án sử dụng Jira khách hàng, phía SEPG sẽ tạo Jira nội bộ CMC tương ứng để quản lý nội bộ
QA/PM dự án tạo ticket có issue type = Number of Leakages để nhập số lượng leakage luỹ kế của dự án
Form ticket yêu cầu điền thông tin sau
Summary
Number of items: Số lượng leakage luỹ kế
Related activity has not happened?: Chỉ tick chọn khi hoạt động UAT của dự án chưa diễn ra
Baseline date: Thời điểm chốt số liệu
Severity: Mức độ nghiêm trọng của bug

## Slide 21

2.3

Nhập data về Leakage của dự án

Dự án sử dụng Jira của khách hàng

Chỉ có QA, QAL và PM được hoạt động trong luồng flow này 
Chỉ role QA/PM được khởi tạo và edit issue type Number of Leakages
Người khởi tạo chuyển trạng thái sang review 
Chỉ PM/QA chuyển trạng thái Review sang Resolved. Nếu QA là ng tạo ticket thì chỉ PM đc chuyển, nếu PM tạo ticket thì chỉ QA đc chuyển  
Chỉ Reporter mới có quyền chuyển từ Resolved sang Baselined 
Tại Status = Baselined thì không thể edit. Nếu cần edit thì phải chuyển về trạng thái khác Cancel 
Chỉ có QAL mới có quyền chuyển từ Baselined sang các status khác 

## Slide 22

2.3

Nhập data về Leakage của dự án

Data về số lượng leakage luỹ kế bên Jira sẽ hiển thị trên Dashboard của dự án

Lưu ý: 
Hệ thống Dashboard sẽ baseline số lượng leakage luỹ kế sau ngày cuối cùng của tháng
Đối với issue type = Number of Leakages, hệ thống sẽ chỉ tính số liệu cho những ticket có status = Baselined

## Slide 23

2.4

Nhập data về CSS của dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Input -> Section CSS
Click button Add

## Slide 24

2.4

Nhập data về CSS của dự án

Form Add CSS bao gồm các thông tin sau
Project Jira: Jira của dự án
Baseline: Thời gian phía CMC nhận CSS từ khách hàng
Duration: Phía CMC xin CSS khách hàng cho giai đoạn nào của dự án
Value: Điểm CSS của dự án 
Default point: Điểm CSS này là điểm khách hàng cho hay là điểm măc định
Comment: Comment của khách hàng (nếu có)

## Slide 25

2.5

Nhập data về PCV của dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Input -> Section PCV rate
Click button Add để tạo lần đo PCV

## Slide 26

2.5

Nhập data về PCV của dự án

User chọn lần đo PCV thuộc stage nào

## Slide 27

2.5

Nhập data về PCV của dự án

QA nhập thông tin sau 
Summary: Summary về lần đo
Baseline month of Check result: Thời điểm chốt số liệu của lần đo

QA nhập kết quả của từng items và finding items trong trường hợp dự án chưa tuân thủ quy trình

Lưu ý: User không thể edit/delete lần đo PCV nếu tháng hiện tại > Baseline month of Check result

## Slide 28

2.6

Tailoring chỉ số

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Information -> Project KPI Tailoring
Click toggle button của chỉ số KPI tương ứng để tailor 

## Slide 29

2.6

Tailoring chỉ số

QA nhập link ticket Jira approve Tailoring chỉ số
Click Save
Khi chỉ số bị tailor, Số liệu của chỉ số đó sẽ hiển thị “Not Applicable”

## Slide 30

2.7

Hiển thị báo cáo cấp dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Overview -> Hệ thống hiển thị các chỉ số KPI chất lượng dự án, luỹ kế đến tháng hiện tại

## Slide 31

2.7

Hiển thị báo cáo cấp dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project KPI -> Hệ thống hiển thị chart các chỉ số

## Slide 32

2.8

Hiển thị báo cáo cấp công ty

Chọn Tab Dashboard -> Project Statistics 
Hệ thống hiển thị chart các chỉ số 

## Slide 33

03

Q&A

## Slide 34

---

<a id="22-dashboard-module-open-update-project-user-guide-v1-0-pptx"></a>
## 22. Dashboard_Module Open&Update project_User guide_V1.0.pptx (from `User Guide/`)

## Slide 1

Dashboard – Module Project Request

July 2024

User Guide_CDO/ G Lead/ Du Lead/ PM/ Sale

## Slide 2

Nội dung

03

02

01

Tổng quan module mở, cập nhật dự án

Hướng dẫn thao tác trên hệ thống CRM và Dashboard

Q&A

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE MỞ, CẬP NHẬT DỰ ÁN

1.2. Tổng quan workflow của phê duyệt mở, cập nhật dự án

1.3. Đối tượng người dùng

## Slide 4

1.1

MỤC ĐÍCH

Thay đổi module mở dự án để đáp ứng yêu cầu 1 MVV có nhiều dự án thực hiện
Giúp giảm tải công việc trình ký phê duyệt mở, cập nhật dự án của bộ phận Sales. 
Users dễ dàng nắm bắt quá trình phê duyệt của bộ phận phê duyệt mở, cập nhật dự án.
Quản lý tập trung dữ liệu về việc mở, cập nhật dự án trên Dashboard

## Slide 5

1.2

Tổng quan workflow phê duyệt Mở dự án

## Slide 6

1.2

Tổng quan workflow phê duyệt Cập nhật dự án

## Slide 7

1.3

Đối tượng người dùng

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được thao tác toàn bộ trên hệ thống
AM
- Tạo Client, Pipeline, Project Code ở CRM
- Nhập data tại màn Open for new project request / Update project request
- Submit Request để chuyển sang giai đoạn phê duyệt
FC
Activate PC để AM tạo Request dựa trên PC đó
PM / CDO
Kiểm tra và phê duyệt Request

## Slide 8

Activate PC trên hệ thống CRM

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

View request mở dự án, cập nhật dự án trên hệ thống Dashboard

Tạo request cập nhật dự án

Tạo request mở dự án

## Slide 9

2.1

Active PC trên hệ thống CRM và tạo Request mở dự án trên Dashboard

Quy trình để có Project code :
AM tạo Client trên hệ thống CRM (Status = New)
AM tạo Contact cho Client đó trên hệ thống CRM -> Active contact
AM chọn active Client (Status = Active)
AM tạo Pipeline trên hệ thống CRM
AM tạo Project code (PC) trên hệ thống CRM
FIN activate PC trên hệ thống CRM
Tạo request mở dự án
***Client: CLIENT - 3999
***PC project code: GLBTM2400143

## Slide 10

2.1

Active PC trên hệ thống CRM và tạo Request mở dự án trên Dashboard

Sơ lược về quy trình phê duyệt mở, cập nhật dự án mới
Có 4 status thuộc quy trình tạo và phê duyệt: Draft, verification, peer review, approved
Draft: Là trạng thái trước khi phê duyệt, AM điền thông tin vào form mở, cập nhật dự án -> AM submit chuyển request sang quá trình phê duyệt
Verification & Peer review (Optional): Là trạng thái request đang trong quá trình phê duyệt (Pending approval by PM, Pending approval by CDO)
Approved: Là trạng thái request đã phê duyệt thành công

## Slide 11

2.2

Open for new project request – Create Client

Bước 1: Tạo Client trong hệ thống CRM 
Để client này được active -> user phải tạo contact gắn với client đó

## Slide 12

2.2

Open for new project request – Create Contact

Bước 2: Tạo Contact cho khách hàng (Bước 1) trong hệ thống CRM 
User chọn Active contact -> User quay lại màn tạo Client để chọn Ative client

## Slide 13

2.2

Open for new project request – Create Client

Đây là trạng thái Active của client

## Slide 14

2.2

Open for new project request – Create Pipeline

Bước 3: Tạo Pipeline của khách hàng
Pipiline có status = TT -> User chọn Workflow > To KT1

## Slide 15

2.2

Open for new project request – Create Project Code

Bước 4: Tạo Project code (Mã vụ việc – MVV)
Tạo PC -> PC có status = Draft -> User chọn Request và chờ bộ phận FIN phê duyệt

## Slide 16

2.2

Open for new project request

Sau khi PC được active, dựa trên thông tin đã làm việc với PM dự án
-> User (Sale) vào hệ thống Dashboard để khởi tạo yêu cầu mở dự án
User (Sale) vào Task > Project Request > Button (+) > Open Request

## Slide 17

2.2

Nhập data vào form Open for new project request

Có 6 phần chính trong Open for new project request
-    Customer information
-    MVV list
General information
Scope & Timeline
Interfaces
Note

Đầu tiên, user phải chọn Customer name:
Danh sách customer name mà user có thể chọn là danh sách Customer thuộc BU của user

## Slide 18

2.2

Nhập data vào form Open for new project request

Droplist MVV là danh sách MVV được gắn với customer mà user đã chọn
Budget MM là số man-month của MVV
Project MM là số man-month mà MVV này cung cấp cho dự án

Customer information, MVV list là toàn bộ thông tin liên quan đến khách hàng và MVV được gắn vào dự án

## Slide 19

2.2

Nhập data vào form Open for new project request

General information, Scope & Timeline, Interfaces, Note là toàn bộ thông tin liên quan đến dự án
Save: user lưu lại thông tin đã điền
Cancel: user không muốn lưu thông tin đã điền (thay đổi)
-> Hệ thống trả lại data cũ
Submit: chuyển request sang trạng thái phê duyệt

## Slide 20

2.2

Nhập data vào form Open for new project request

User (AM) điền hết các trường bắt buộc sau đó submit để request chuyển sang trạng thái chờ phê duyệt
Khi request có status khác draft, AM không có quyền edit request
Sau khi request được submit, luồng phê duyệt sẽ phụ thuộc vào từng trường hợp:
Mở dự án gắn MVV mà MVV này đã tồn tại trong một dự án (yêu cầu)
-> Yêu cầu mở dự án này sẽ cần sự phê duyệt của PM & CDO
Mở dự án gắn MVV mà MVV này chưa tồn tại trong một dự án (yêu cầu)
-> Yêu cầu mở dự án này chỉ cần sự phê duyệt của PM
 

## Slide 21

2.2

Phê duyệt Open for new project request - PM

User (PM) vào Task > Project Request > Chọn View details task phê duyệt của mình với Request type = Open

## Slide 22

2.2

Phê duyệt Open for new project request - PM

*Lưu ý: PM không sửa Project MM của từng MVV, không add thêm MVV
PM có thể thay đổi thông tin trong phần General information, Scope & Timeline, Interfaces, Note 

## Slide 23

2.2

Phê duyệt Open for new project request - PM

User (PM) phải điền đầy đủ thông tin bắt buộc trước khi Approve
*Lưu ý:
User (PM) muốn đổi PM của dự án 
-> User chọn Reassign (chỉ PM của dự án mới được phép phê duyệt request)
Save: user lưu lại thông tin đã điền
Approve: user phê duyệt thành công và request chuyển sang bước phê duyệt tiếp theo (nếu có)
Reject: user comment trước khi reject
-> Request chuyển về trạng thái Draft (AM nhận được thông báo để chỉnh sửa request)

## Slide 24

2.2

Phê duyệt Open for new project request - CDO

User (CDO) vào Task > Project Request > Chọn View details task phê duyệt của mình 
Khi user (CDO) phê duyệt, user không edit bất kỳ thông tin gì
*Lưu ý: User có thể reassign phê duyệt cho user khác

## Slide 25

2.3

Update project request

Dự án đang chạy cần cập nhật thông tin
-> User (Sale) vào hệ thống Dashboard để khởi tạo yêu cầu cập nhật dự án
User (Sale) vào Task > Project Request > Button (+) > Update Request

## Slide 26

2.3

Update project request

Đầu tiên, user phải chọn Project cần được cập nhật:
Danh sách project mà user (AM) có thể chọn là danh sách Project có khách hàng trực thuộc BU của AM đó

## Slide 27

2.3

Update project request

Giống với Open for new project request, Update project request cũng có 6 phần chính
-    Customer information
-    MVV list
General information
Scope & Timeline
Interfaces
Note
-> Tất cả thông tin đều được sync theo dự án đã chọn
*User (AM) không xoá MVV sẵn có của dự án, và không thay đổi PM đang quản lý dự án
*User (AM) được quyền thêm MVV, thay đổi project MM của MVV sẵn có, thay đổi DU Host

## Slide 28

2.3

Update project request

User (AM) điền hết các trường bắt buộc sau đó submit để request chuyển sang trạng thái chờ phê duyệt
Khi request có status khác draft, AM không có quyền edit request
Sau khi request được submit, luồng phê duyệt sẽ phụ thuộc vào từng trường hợp:
Add MVV cho dự án mà MVV này đã tồn tại trong một dự án (yêu cầu)
-> Yêu cầu mở dự án này sẽ cần sự phê duyệt của PM & CDO
Add MVV cho dự án mà MVV này chưa tồn tại trong một dự án (yêu cầu) hoặc thay đổi Project MM của MVV sẵn có
-> Yêu cầu mở dự án này chỉ cần sự phê duyệt của PM
Thay đổi Du Host cho dự án
-> Yêu cầu cập nhật này sẽ cần sự phê duyệt của PM & CDO

## Slide 29

2.3

Update project request - PM

User (PM) vào Task > Project Request > Chọn View details task phê duyệt của mình với Request type = Update

## Slide 30

2.3

Update project request - PM

User (PM) muốn đổi PM của dự án
User chọn Delivery > Project list > Project             member và thay đổi PM tại đây
Khác với reassign tại màn Open for new project request, form Update project request, user (PM) muốn reassign phê duyệt không đồng nghĩa với việc đổi PM của dự án

## Slide 31

2.3

Update project request - CDO

User (CDO) vào Task > Project Request > Chọn View details task phê duyệt của mình 
Khi user (CDO) phê duyệt, user không edit bất kỳ thông tin gì
*Lưu ý: User có thể reassign phê duyệt cho user khác

## Slide 32

2.4

View Request trên hệ thống Dashboard

ADMIN, CDO, QA, PMO, FC, SEPG được xem tất cả request
PM có thể xem được những request mà PM đó phụ trách quản lý
Du Lead có thể xem được những request mà Du Host là Du của họ
Sale có thể xem được những request cho khách hàng thuộc BU của họ

## Slide 33

2.4

View Request đã phê duyệt thành công trên hệ thống Dashboard

Sau khi yêu cầu mở dự án được phê duyệt thành công
-> User vào Delivery > Project list 
Lúc nào dự án mới được khởi tạo đã xuất hiện trong Project list với Status của dự án = Running
Sau khi yêu cầu cập nhật dự án được phê duyệt thành công
-> User vào Delivery > Project list > Project information 
Lúc nào dự án đã được cập nhật thông tin thay đổi

## Slide 34

03

Q&A

## Slide 35

---

<a id="23-dashboard-projects-user-guide-v1-0-docx"></a>
## 23. Dashboard_Projects_User guide_V1.0.docx (from `User Guide/`)

<DashBoard>

User Guide

Dashboard - Projects

Version: <1.0>

| Issued Status: |  |
| --- | --- |
| Issued Date: |  |
| Owner: | Nguyễn Hà Anh |
| Author: | Nguyễn Hà Anh |
| Location: | [https://dashboard.cmcglobal.com.vn/project/list](https://dashboard.cmcglobal.com.vn/project/list) |
| Confidential Class: | Confidential |

| Date: | 07/2022 |
| --- | --- |
| Approved by: | HTAnh6 |
| Signature: |  |

Approval Information

| Approver Name | Role | Date (mm-dd-yyyy) | Revision | Comment |
| --- | --- | --- | --- | --- |
|  |  |  |  |  |

Revision History

| Revision | Date (mm-dd-yyyy) | Description | Revised by | Reviewer | Date (mm-dd-yyyy) |
| --- | --- | --- | --- | --- | --- |
|  |  |  |  |  |  |
|  |  |  |  |  |  |

RECORD OF CHANGE

| No | Effective Date | Version | Change Description | Reason | Reviewer | Approver |
| --- | --- | --- | --- | --- | --- | --- |
| 1 |  |  |  |  |  |  |
| 2 |  |  |  |  |  |  |
| 3 |  |  |  |  |  |  |
| 4 |  |  |  |  |  |  |
| 5 |  |  |  |  |  |  |

Mục Lục

Table of Contents

	Giới thiệu	4

	Định nghĩa từ viết tắt	4

	System requirements	4

	Hướng dẫn cài đặt	4

	Hướng dẫn sử dụng	4

			Tổng quan	4

			Đăng nhập hệ thống	5

			Tổng quan (Overview)	5

	Chức năng lọc thông tin (Filter)	6

	Chức năng giải trình KPI (Explain)	9

			Danh sách dự án (Project List) và Đăng kí KPI	10

		I.	Danh sách dự án (Project List)	10

	Chức năng lọc thông tin (Filter)	11

	Chức năng xem chi tiết KPI của 1 dự án	12

		II.	Đăng kí KPI (KPI Register)	12

	Chức năng xem KPI đã đăng kí	15

	Chức năng chỉnh sửa KPI đã đăng kí	16

			Update KPI (KPI Tracker)	18

	Chức năng Xem KPI Tracker	18

	Chức năng Update/edit KPI Tracker	19

	# Giới thiệu

*Bản** **hướng** **dẫn** **dành** **cho** **người** **sử** **dụng** **khi** **truy** **cập** **vào** “**DashBoard** - Projects”*

	# Định nghĩa từ viết tắt

*[Chi **tiết** **về** **các** **thuật** **ngữ** **và** **định** **nghĩa** **được** **sử** **dụng** **trong** **bản** **hướng** **dẫn** **này**.]*

	# System requirements

	# Hướng dẫn cài đặt

*Vui** **lòng** **tham** **khảo** **Hướng** **dẫn** **triển** **khai*

	# Hướng dẫn sử dụng

		## Tổng quan

Phần “Projects” là phần tính năng trên hệ thống Dashboard để theo dõi tổng quan thông tin các dự án của công ty CMC Global

Module “Projects” cho phép QA, SEPG và các cấp quản lý đăng kí, update việc thực hiện KPI của dự án.

		## Đăng nhập hệ thống

		- Bước 1: Truy cập giao diện đăng nhập hệ thống

- Bước 2: Nhập Ldap và mật khẩu Ldap

- Bước 3: Chọn Login hoặc ấn Enter để đăng nhập vào hệ thống

## Tổng quan (Overview)

Yêu cầu: Đăng nhập thành công vào hệ thống

Nhấn: Projects/ Overview trên thanh công cụ

Màn hình hiển thị tất cả các nội dung:

### Chức năng lọc thông tin (Filter)

- Bước 1: Chọn 1 hoặc đồng thời nhiều Filter: 

+ Thời gian: MM/YYYY

+ Division: Chọn Group

+ Department: Sau khi chọn Group, chọn 1 DU trong group để xem chi tiết ở cấp độ DU

+ Location: Xem thông tin theo vị trí công ty Global (HN, ĐN, HCM,…)

- Thông tin đầu ra: 

#### Filter Group/Company: 

- Biểu đồ “**Project Type**” - Biểu đồ (% và số lượng) phân loại dự án của công ty CMC Global (HN, ĐN,…) theo loại: ODC, Project based, Onsite, Testing, Training

- Biểu đồ “**Total projects**” -  Biển đồ (% và số lượng) phân loại dự án theo trạng thái (Status): New, Open (Due this month, Running, Overdue), Close (Ontime, Overdue)

- Bảng:

- Bảng chi tiết số lượng dự án theo trạng thái ở cấp độ: Công ty – Group – DU 

- Bảng chi tiết Effort của Công ty – Group - DU

#### Filter DU hoặc click vào tên của 1 DU trong bảng G/Company:

** **

- Biểu đồ “**Project Type**” - Biểu đồ (% và số lượng) phân loại dự án của DU theo loại: ODC, Project based, Onsite, Testing, Training

- Biểu đồ “**Total projects**” -  Biển đồ (% và số lượng) phân loại dự án theo trạng thái (Status): New, Open (Due this month, Running, Overdue), Close (Ontime, Overdue)

- Biểu đồ "**KPI ****Overview**” 12 tháng/năm và giải trình 

- Bảng:

- Bảng chi tiết số lượng dự án theo trạng thái ở cấp độ DU

- Bảng Efforts của DU  

- Bảng KPI của DU

- Note: Nếu không chọn Filter, hệ thống hiện mặc định thông tin ở cấp độ Company: Global HN; Thời gian: Hiện tại 

### Chức năng giải trình KPI (Explain)

    Yêu cầu: Chọn xem thông tin ở cấp độ DU

- Bước 1: Ở biểu đồ "**KPI Overview**”, chọn nút “Explain”. Hệ thống hiện popup như hình:

-  Bước 2: Chọn tháng cần nhập giải trình/cần xem ở thanh chọn thời gian

- Bước 3: Nhập nội dung

- Bước 4: Nhấn “Save” để lưu/ “Cancel” để hủy và quay về màn hình Overview ban đầu

## Danh sách dự án (Project List) và Đăng kí KPI

### Danh sách dự án (Project List)

Yêu cầu: Đăng nhập thành công vào hệ thống

Nhấn: Projects/Projects trên thanh công cụ

Màn hình hiển thị tất cả các nội dung:

       

### Chức năng lọc thông tin (Filter)

- Bước 1: Chọn 1 hoặc đồng thời nhiều Filter: 

+ Thời gian: MM/YYYY

+ Division: Chọn Group

+ Department: Sau khi chọn Group, chọn 1 DU trong group để xem chi tiết ở cấp độ DU

+ Status: Chọn từ list dropdown để xem dự án theo trạng thái

+ Type: Chọn từ list dropdown để xem dự án theo loại

+ PM: Chọn từ list dropdown để xem dự án theo tên PM

- Thông tin đầu ra: Hiển thị danh sách các dự án cùng thông tin chung như hình:

    

### Chức năng xem chi tiết KPI của 1 dự án

Yêu cầu:

- Dự án đã được đăng kí KPI trong năm

- Bước 1: Chọn 1 dự án, click vào tên dự án. 

- Bước 2: Màn hình chuyển sang tab “KPI Tracker” 

### Đăng kí KPI (KPI Register)

Yêu cầu:

- Dự án chưa từng được đăng kí KPI trong năm

- Người dùng có role PM/PMO

- Bước 1: Click nút “KPI Register” ở góc phải màn hình

- Bước 2: Hệ thống hiển thị màn hình đăng kí như hình:

                                

- Bước 3: Nhập thông tin dự án “A. General Information”

+ Project name: Click để chọn tên dự án từ list (bắt buộc)

+ Project code: Chọn Project code (bắt buộc)

+ Approved Jira ticket link: Nhập Link Jira của dự án (bắt buộc)

+ Các thông tin còn lại hệ thống tự động điền theo các thông tin nhập bởi người dung

- Bước 4: Đăng kí chỉ tiêu KPI cho các tiêu chí chung ở mục “Standard Objectives”: 

+ Chỉnh sửa số ở phần Target theo giới hạn tiêu chuẩn (bắt buộc); 

+ Chọn 1 từ list Frequency: Chu kì thực hiện chỉ tiêu  (bắt buộc) 

+ Note (không bắt buộc)

+ Click icon cục tẩy ở cuối dòng để xóa toàn bộ thông tin vừa thêm

- Bước 5 (Optional) (Dành cho các dự án có tiêu chí riêng): Click nút “Special Objectives” và Nhập/thêm các tiêu chí riêng  

+  Nhập tên tiêu chí; Đơn vị tính (Unit); Công thức tính (Formular); Target; Chu kì thực hiện đo (Frequency); Note.

+ Click icon thùng rác ở cuối dòng để xóa

- Bước 6: Click “Register” để lưu đăng kí/ “Cancel” hoặc “Back” icon để hủy

### Chức năng xem KPI đã đăng kí

Yêu cầu: Dự án đã được đăng kí KPI trong năm

- Bước 1: Ở màn danh sách dự án, chọn 1 dự án bất kì, click nút “con mắt” ở cuối để xem.

- Bước 2: Click nút “Close”/ “Back” để đóng bảng thông tin đã đăng kí và quay lại màn hình danh sách dự án như hình dưới.

### Chức năng chỉnh sửa KPI đã đăng kí

Yêu cầu: 

- Dự án đã được đăng kí KPI trong năm

- User có role là member SEPG

- Bước 1: Ở màn danh sách dự án, chọn 1 dự án bất kì, click biểu tượng “bút chì” ở cuối để mở form chỉnh sửa.

- Bước 2: Click nút “Edit”

- Bước 3: Chỉnh sửa nội dung; thêm note ở dòng vừa chỉnh sửa (Bắt buộc)

- Bước 4: Click “Save” để lưu thay đổi/ “Cancel”/”Back” để hủy thay đổi như hình:

## Update KPI (KPI Tracker)

### Chức năng Xem KPI Tracker

Yêu cầu: 

- Dự án đã có Resource Allocate

- Dự án đã có đăng kí KPI

- User có role là member SEPG, QA, PM, PMO của dự án và các dự án khác trong cùng 1 DU

- Bước 1: Ở màn hình project list, click vào tên 1 dự án/click vào biểu tượng bút chì ở cuối dòng tên dự án/Click vào tab “KPI Tracker”. Màn hình hiển thị:

- Bước 2: Click vào 1 trong các tab: Quality, Process, Productivity, Billable Rate, Timeliness, Specific Objectives để xem các bảng tương ứng

- Bước 3: Để quay lại màn hình danh sách dự án, click vào tab “Projects” trong module “Projects”

### Chức năng Update/edit KPI Tracker

Yêu cầu: 

- Dự án đã có Resource Allocate

- Dự án đã có đăng kí KPI

- User có role là member SEPG, QA, PMO của dự án

- Bước 1:  Chọn 1 bảng KPI cần thao tác

- Bước 2: 

Hệ thống tự động hiện bảng theo 12 tháng của năm hiện tại

Tại dòng thời gian cần update/edit KPI, click biểu tượng “Bút chì” ở cuối để thực hiện update/edit số liệu ở những phần ô trắng, có thể edit. Nhập các trường bắt buộc.

Sau đó chọn 1 giá trị từ list Quality Assessment (đánh giá số actual so với target) (bắt buộc). 

Sau khi chọn “Quality Assessment”, màu của dòng thay đổi theo giá trị đó:

+ Good: Xanh lá

+ Warning: Vàng

+ Bad: Đỏ

+ Tháng hiện tại: Xanh nước biển

- Bước 3: Sau khi edit, click biểu tượng dấu “tích” để lưu/ biểu tượng cục tẩy để xóa toàn bộ thông tin vừa nhập/ biểu tượng dấu “x” để bỏ lưu. 

- Bước 4: Riêng đối với bảng “Billable”

Yêu cầu: Người dùng có role: PMO

Các bước thực hiện update như các bảng khác

Có thể click “Add row” để thêm dòng mới phía dưới 

## Technical Support 

[Further information about technical contact.]

---

<a id="24-dashboard-qtmdda-user-guide-v1-0-pptx"></a>
## 24. Dashboard_QTMDDA_User guide_V1.0.pptx (from `User Guide/`)

## Slide 1

Dashboard – Quy trình mở và cập nhật dự án

July 2023

User Guide_ Sale/ PM

## Slide 2

Nội dung

03

02

01

Tổng quan quy trình mở và cập nhật dự án

Hướng dẫn thao tác trên hệ thống với các quy trình

Q&A

## Slide 3

1.1. Mục đích

01

TỔNG QUAN QUY TRÌNH MỞ, CẬP NHẬT DỰ ÁN TRÊN DASHBOARD

1.2. Quy trình mở và cập nhật dự án 

## Slide 4

1.1

MỤC ĐÍCH

Tạo quy trình khép kín & Số hóa toàn bộ quá trình liên quan đến tạo, thay đổi, đóng Dự án trên hệ thống
Giúp giảm tải công việc thực hiện trực tiếp và các công việc phối hợp giữa các bên. Thông tin chi tiết và tình trạng công việc được quản lý rõ ràng
Quản lý tập trung dữ liệu Dự án trên hệ thống Dashboard theo cấu trúc: Customer ID – Dự án – MVV

CRM

Dashboard

Sale hoặc sale support khởi tạo PC trên hệ thống CRM (JIRA)
(Sale cần làm việc với DUL/PM để xác định việc add mã mới vào dự án đã tồn tại hay khởi tạo dự án mới)

DUL/PM nhận notification/ vào project list check các request cần phê duyệt mở/cập nhật dự án
PM cần kiểm tra thông tin mã mới đã chính xác chưa theo quy định đã ban hành về dự án
Phê duyệt khởi tạo/cập nhật mã vào dự án

## Slide 5

1.1

Thay đổi trong luồng mở dự án

Hiện tại

Version mới

## Slide 6

QUY ĐỊNH MỚI

DỰ ÁN MỚI:

Là Dự án của Khách hàng mới (Khách hàng lần đầu hợp tác, chưa phát sinh Hợp đồng nào trước đó)
Của Khách hàng cũ (Khách hàng đã có phát sinh Hợp đồng trước đó) nhưng triển khai với category khác (VD: Hợp đồng trước là development, Hợp đồng sau là Maintenance) 
Trường hợp Dự án có nhiều line, vận hành thực tế khác nhau có thể xem xét tách thành các Dự án

 Được phép tạo mới Dự án trên hệ thống Dashboard             

DỰ ÁN CŨ:

Là Dự án đã được tạo trên hệ thống và đã/ đang triển khai
Có phát sinh thêm các Work Order trong quá trình thực hiện DA (ví dụ: 03 tháng/ lần, 06 tháng/ lần…)
Dự án đang triển khai nhưng Hợp đồng được tách biệt theo từng năm, thường đóng vào cuối năm và mở lại vào đầu năm (ví dụ: Maintenance năm 2022, Maintenance năm 2023)

## Slide 7

1.2

ĐỐI TƯỢNG NGƯỜI DÙNG VÀ NHIỆM VỤ CHÍNH

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được thao tác toàn bộ trên hệ thống
SEPG
Tiếp nhận thông tin và quản lý thông tin dự án của CMC Global
PM
Kiểm tra, phê duyệt tạo dự án và cập nhật dự án
QA
Tiếp nhận thông tin mở và cập nhật dự án
DU Lead
Tiếp nhận thông tin và quản lý thông tin dự án 
AM/Sale support
Khởi tạo yêu cầu mở và cập nhật từ hệ thống CRM

## Slide 8

Màn Project Request

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

Open request form

Màn Project List

Section Project Information 

Update request form

## Slide 9

2.1

  Quy trình mở dự án 

Đối tượng liên quan: Sale, FIN, PM

## Slide 10

2.2.1

  Update trên mã vụ việc tại CRM project code

Role Sale:

Sau khi khởi tạo đầy đủ pipeline (KT1) tiến hành tạo mã request bên tài chính phê duyệt

PC delivery Unit: AM cần làm việc với DU liên quan để xác định DU host phụ trách thực hiện dự án

Delivery Project: AM cần làm việc với DUL hoặc PM phụ trách để xác định Mã vụ việc trên là thuộc dự án đang chạy hay khởi tạo ra dự án mới.
Đối với dự án đang chạy AM cần chọn mã vv của dự án đang chạy đó (previous MVV)

Sau khi PC được active sẽ tự động tạo thành request dự án trên hệ thống dashboard màn request list

## Slide 11

2.1.2

  Màn hình Project Request

Mục đích: 
+ Quản lý tất cả các request type (Open/Update/Close) 
+ Cho phép PM phê duyệt các Open/Update request mà người đó là PM và tạo Close request của dự án.
+ Cho phép DUL view tất cả các loại request type mà có DU host thuộc DUL quản lý
+ Cho phép QA/QAL/SEPG view tất cả các loại request type 

## Slide 12

2.1.3

  Màn hình Project Request 

Role PM

Thao tác: 
1. Login vào hệ thống dashboard => hiển thị màn Project Request
2. Click button “View details” tại request có trạng thái “Pending approval by PM”  => hiển thị form Open for new project_detail để review và nhập thêm thông tin có trong form
 

## Slide 13

2.1.3

  Màn hình Project Request 

## Slide 14

2.1.3

  Màn hình Project Request 

Role PM

Thao tác: 
3. Review các thông tin trên form và nhập đầy đủ thông tin:
- Click “Approve” => Hệ thống check DA có end date > 31/3 năm tiếp theo k và => Hiển thị popup confirm. User chọn “Yes” => hiển thị message “Approved Open project request successfully“, back lại màn Project Request. Request status trong màn Project Request sẽ chuyển trạng thái sang “Approved”. Nếu click “No” => popup biến mất.
- Click “Reject” => hiển thị popup “Do you confirm to reject this request? User chọn “Yes” => hiển thị message “Rejected Open project request“, back lại màn Project Request. Request status trong màn Project Request sẽ chuyển trạng thái sang “Rejected”. Nếu click “No” => popup biến mất.

## Slide 15

2.1.3

  Màn hình Project List 

Mục đích: + Cho phép PM xem danh sách dự án mà họ là PM
                  + Cho phép DU lead xem danh sách các dự án đã tạo thành công có DU host mà DU lead đó quản lý 
                  + Cho phép QA/SEPG/PMO xem danh sách dự án đã được tạo thành công 

## Slide 16

2.1.4

  Section Project Information 

Mục đích: 
+ Cho phép PM view/edit thông tin dự án mà họ là PM, xóa MVV trong MVV list nếu MVV đó chưa được allocate và chưa được add billable. Sau khi edit thông tin -> click Save changes để lưu thay đổi
+ Cho phép DU lead xem danh sách các dự án đã tạo thành công có DU host mà DU lead đó quản lý 
+ Cho phép QA/QAL view/edit trường QA service package. Sau khi edit thông tin -> click Save changes để lưu thay đổi
+ Cho phép SEPG/PMO xem danh sách dự án đã được tạo thành công trên DB. 

## Slide 17

2.2

  Quy trình cập nhật dự án 

Đối tượng liên quan: Sale, PM

## Slide 18

2.1.1

  Màn hình tạo MVV (Project code) 

Role Sale:

TH1: với dự án điền sai thông tin về MVV vào PC đã active comment yêu cầu thay đổi thông tin với bên tài chính. Phía FIN sẽ kiểm tra và cập nhật. Khi cập nhật sẽ tạo request phía hệ thống Dashboard để PM kiểm tra và xác nhận

TH2: Với việc add thêm mã vào dự án đã tồn tại. Vẫn làm thủ tục xin cấp mã như mở mới tại trường
Delivery project: lựa chọn existing
Previous project code: lưa chọn MVV trước đó cần add thêm

## Slide 19

2.2.3

  Màn hình Project Request 

Role PM

Thao tác: 
1. Login vào hệ thống dashboard => hiển thị màn Project Request
2. Click button “View details” tại Update request có trạng thái “Pending approval by PM”  => hiển thị form Update_detail để review thông tin có trong form
 

## Slide 20

2.2.3

  Màn hình Project Request 

Role PM

Thao tác: 
1. Login vào hệ thống dashboard => hiển thị màn Project Request
2. Click button “View details” tại Update request có trạng thái “Pending approval by PM”  => hiển thị form Update_detail để review thông tin có trong form
 

## Slide 21

2.2.3

  Section Project Information 

Lưu ý:

+ Sau khi Update request được approve từ PM => các thông tin ở Update form sẽ được cập nhật vào màn Project Information 
+ PM view/edit thông tin dự án mà họ là PM, xóa MVV trong MVV list nếu MVV đó chưa được allocate và chưa được add billable. Sau khi edit thông tin -> click Save changes để lưu thay đổi
+ QA/QAL: view/edit trường QA service package. Sau khi edit thông tin -> click Save changes để lưu thay đổi

## Slide 22

04

Q&A

## Slide 23

---

<a id="25-dashboard-user-guide-module-kpi-s-operation-pptx"></a>
## 25. Dashboard_User Guide_Module KPI's Operation.pptx (from `User Guide/`)

## Slide 1

Dashboard – Module chỉ số KPI chất lượng dự án Operation

February 2025

User Guide_ QA/ PM

## Slide 2

Nội dung

02

01

Tổng quan module chỉ số KPI chất lượng dự án Operation

Hướng dẫn thao tác trên hệ thống Jira và Dashboard

Q&A

03

Hiển thị báo cáo cấp dự án

04

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE CHỈ SỐ KPI CHẤT LƯỢNG DỰ ÁN

1.2. Quy trình nhập data input cho chỉ số KPI chất lượng dự án Operation

## Slide 4

1.1

MỤC ĐÍCH

Tạo quy trình và số hóa quá trình liên quan đến các chỉ số chất lượng dự án Operation
Giúp giảm tải công việc tổng hợp data KPI chất lượng dự án Operation và các công việc phối hợp giữa QA và PM. Users dễ dàng nắm bắt thông tin chỉ số KPI chất lượng dự án.
Quản lý tập trung dữ liệu KPI của Dự án Operation trên hệ thống Dashboard

## Slide 5

1.1

Quy trình nhập data input chỉ số KPI chất lượng 

## Slide 6

1.1

Quy trình nhập data input chỉ số KPI chất lượng 

Project Jira: Sau khi Jira và Dashboard của dự án được tạo ra, phía SEPG sẽ điền Jira key vào dự án trên Dashboard.  
Data Jira của dự án sẽ được đồng bộ sang dự án trên Dashboard tương ứng

## Slide 7

1.1

Quy trình nhập data input chỉ số KPI chất lượng 

Đối với data Number of On-time response, Number of On-time resolution: User nhập data bên Jira, hệ thống Dashboard sẽ đồng bộ và tính toán chỉ số KPI chất lượng tương ứng 

Đối với data PCV và CSS: QA dự án nhập trên hệ thống Dashboard 

## Slide 8

1.2

Đối tượng người dùng và nhiệm vụ chính

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được thao tác toàn bộ trên hệ thống
SEPG
Tạo Jira của dự án và gắn Jira của dự án lên Dashboard
PM
Tổng hợp data, kiểm tra thông tin về chỉ số chất lượng dự án
QA
Đo lường chỉ số chất lượng dự án và nhập data
DU Lead
Tiếp nhận thông tin và quản lý thông tin chỉ số chất lượng dự án

## Slide 9

Number of On-time response

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

Number of On-time resolution

PCV

CSS

Tailoring chỉ số KPI và Item PCV

## Slide 10

2.1

Nhập data về On-time response của dự án

Dự án sử dụng Jira của khách hàng

Nếu dự án sử dụng Jira khách hàng, phía SEPG sẽ tạo Jira nội bộ CMC tương ứng để quản lý nội bộ
QA/PM dự án tạo ticket có issue type = Number of On-time response
Form ticket yêu cầu điền thông tin sau
Summary
Baseline date: Thời điểm chốt số liệu
Số lượng ticket đã nhận trong tháng
Số lượng ticket được phản hồi On time trong tháng 

## Slide 11

2.1

Nhập data về On-time response của dự án

Dự án sử dụng Jira của khách hàng

Chỉ có QA, QAL và PM được hoạt động trong luồng flow này 
Chỉ role QA/PM được khởi tạo và edit issue type Number of On-time response
Reporter chuyển trạng thái sang Review 
Chỉ PM/QA chuyển trạng thái Review sang Resolved. Nếu QA là người tạo ticket thì chỉ PM được chuyển, nếu PM tạo ticket thì chỉ QA được chuyển  
Chỉ Reporter mới có quyền chuyển từ Resolved sang Baselined 
Tại Status = Baselined thì không thể edit. Nếu cần edit thì phải chuyển về trạng thái khác Cancel 
Chỉ có QAL mới có quyền chuyển từ Baselined sang các status khác 

## Slide 12

2.1

Nhập data về On-time response của dự án

Data về On-time response bên Jira sẽ hiển thị trên Dashboard của dự án

Lưu ý: 
Đối với issue type = Number of On-time response, khi baseline, hệ thống sẽ chỉ tính số liệu cho những ticket có 
status = Baselined sau ngày cuối cùng của tháng

## Slide 13

2.2

Nhập data về On-time resolution của dự án

Dự án sử dụng Jira của khách hàng

Nếu dự án sử dụng Jira khách hàng, phía SEPG sẽ tạo Jira nội bộ CMC tương ứng để quản lý nội bộ
QA/PM dự án tạo ticket có issue type = Number of On-time resolution
Form ticket yêu cầu điền thông tin sau
Summary
Baseline date: Thời điểm chốt số liệu
Số lượng ticket đã nhận trong tháng
Số lượng ticket được Resolve on time trong tháng 

## Slide 14

2.2

Nhập data về On-time resolution của dự án

Dự án sử dụng Jira của khách hàng

Chỉ có QA, QAL và PM được hoạt động trong luồng flow này 
Chỉ role QA/PM được khởi tạo và edit issue type Number of On-time resolution
Reporter chuyển trạng thái sang Review 
Chỉ PM/QA chuyển trạng thái Review sang Resolved. Nếu QA là người tạo ticket thì chỉ PM được chuyển, nếu PM tạo ticket thì chỉ QA được chuyển  
Chỉ Reporter mới có quyền chuyển từ Resolved sang Baselined 
Tại Status = Baselined thì không thể edit. Nếu cần edit thì phải chuyển về trạng thái khác Cancel 
Chỉ có QAL mới có quyền chuyển từ Baselined sang các status khác 

## Slide 15

2.2

Nhập data về On-time resolution của dự án

Data về On-time resolution bên Jira sẽ hiển thị trên Dashboard của dự án

Lưu ý: 
Đối với issue type = Number of On-time resolution, khi baseline, hệ thống sẽ chỉ tính số liệu cho những ticket có 
status = Baselined sau ngày cuối cùng của tháng

## Slide 16

2.3

Nhập data về CSS của dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Input -> Section CSS
Click button Add

## Slide 17

2.3

Nhập data về CSS của dự án

Form Add CSS bao gồm các thông tin sau:
Project Jira: Jira của dự án
Baseline: Thời gian phía CMC nhận CSS từ khách hàng
Duration: Phía CMC xin CSS khách hàng cho giai đoạn nào của dự án
Value: Điểm CSS của dự án 
Default point: Điểm CSS này là điểm khách hàng cho hay là điểm măc định
Comment: Comment của khách hàng (nếu có)

## Slide 18

2.4

Nhập data về PCV của dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Input -> Section PCV rate
Click button Add để tạo lần đo PCV

## Slide 19

2.4

Nhập data về PCV của dự án

User chọn lần đo PCV thuộc stage nào

## Slide 20

2.4

Nhập data về PCV của dự án

QA nhập thông tin sau 
Summary: Summary về lần đo
Baseline month of Check result: Thời điểm chốt số liệu của lần đo

QA nhập kết quả của từng items và finding items trong trường hợp dự án chưa tuân thủ quy trình

Lưu ý: 
- User không thể edit/delete lần đo PCV nếu tháng hiện tại > Baseline month of Check result
- Hệ thống sẽ baseline kết quả các lần đo PCV sau ngày cuối cùng của tháng

## Slide 21

2.5

Tailoring chỉ số

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Information -> Project KPI Tailoring
Click toggle button của chỉ số KPI tương ứng để tailor 

## Slide 22

2.5

Tailoring chỉ số

QA nhập link ticket Jira approve Tailoring chỉ số
Click Save
Khi chỉ số bị tailor, Số liệu của chỉ số đó sẽ hiển thị “Not Applicable”

## Slide 23

2.6

Tailoring item đo PCV

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Information -> PCV Template
Click nút Edit để tailoring item PCV theo từng Stage

## Slide 24

2.6

Tailoring item đo PCV

QA click vào toggle button để tailor items và điền Project Specification để nhập lý do cho việc tailoring
Click Save
Sau khi click Save, khi user tạo 1 lần đo PCV mới, item PCV sẽ mặc định chọn là Not Applicable

## Slide 25

Hiển thị báo cáo cấp dự án

03

Hiển thị báo cáo cấp dự án

## Slide 26

03

Hiển thị báo cáo cấp dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project Overview -> Hệ thống hiển thị các chỉ số KPI chất lượng dự án, luỹ kế đến tháng hiện tại

## Slide 27

3

Hiển thị báo cáo cấp dự án

Tại màn Project List, user click chọn xem chi tiết 1 dư án
Chọn Tab Project KPI -> Hệ thống hiển thị chart các chỉ số

## Slide 28

04

Q&A

## Slide 29

---

<a id="26--dashboard-phase-11--c-codex---git-v1-20250526-155505-pdf"></a>
## 26. [Dashboard Phase 11] C-codex & GIT-v1-20250526_155505.pdf

[Dashboard Phase 11] C-codex & GIT
TDX Delivery

Exported on 05/26/2025

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Table of Contents
1 1'. Project information...............................................................................................3

2 2'. Đồng bộ từ C-codeX sang Dashboard..................................................................8
3 3'. Enhance Open/Update for new project ...............................................................9
4 4'. Project input ........................................................................................................19
5 5'. Project overview..................................................................................................30
6 6'. Project statistics ..................................................................................................45

– 2

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

1 1'. Project information
1, Feature / Function 1: Thêm trường thông tin Project C-ticket trong Project information
1.1.1 Access control

[Contain all the Access control requirements of function linked to POA]
Page

Position

Activity

Permission

Role

Description

Project
information
(projectsetting)

ADMIN_EDIT_P
ROJECT_INFO

ADMIN_EDIT_P
ROJECT_INFO

User được gán activity này sẽ
được enable trường GIT và có
quyền thấy button Reload

Project
information
(projectsetting)

PM_EDIT_PROJ
ECT_INFO

PM_EDIT_PROJ
ECT_INFO

User được gán activity này sẽ
được enable trường GIT và có
quyền thấy button Reload

1.1.2 Graphic User Interface
1.1.3 Workflow - User case
Use case

Gắn git_id vào dự án Dashboard

Precondition
Thiết bị user sử dụng phải có kết nối
internet
User có role là PM,
permission PM_EDIT_PROJECT_INFO
ADMIN,
permission ADMIN_EDIT_PROJECT_INFO

Description

Post condition
User vào được màn Project information

Main Flow

User: Tại màn Delivery > Chọn
project > Project information
User nhập gitID tại trường GIT →
Save changes
System: Validate nếu gitID đã gắn
trong project khác

Business Rule
Không validate 1 gidID chỉ đi với 1
project → mối quan hệ nhiều nhiều (1
project có nhiều git, 1 git được gắn
trên nhiều project)

Nếu không → Save changes thành
công

1'. Project information – 3

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Exception Flow:

User: Tại màn Delivery > Chọn
project > Project information

Add thêm gidID vào dự
án

BE tạo bản ghi trong bảng project_git theo
project_id <Rule đồng bộ C-codeX về
Dashboard>

User nhập thêm gitID tại trường
GIT → Save changes

→ Save changes thành công
Exception Flow:
Xoá gidID khỏi dự án

User: Tại màn Delivery > Chọn
project > Project information

BE query bảng project_git → tìm bản ghi
có project_id tương ứng

User xoá gitID tại trường GIT →
Save changes

→ BE xoá value của trường project_id
Mọi data đã được sync theo (PENDING)

System: Save changes thành công
1.1.4 GUI Element
N
o.

Field
Name

Type

1

Project
GIT

Num
ber

2

Reload

Butto
n

Le
ngt
h

Man
dato
ry

Descriptio
n

N/A

User nhập
id của
gitlab
muốn gán
cho dự án
Dashboard

Placeholder: Select options

→ id sẽ
được hiển
thị dưới
dạng

→ id được chọn sẽ hiển thị ở dạng Tag

User click
để đồng
bộ data
của GIT

Button Reload chỉ hiển thị đối với những link đã
gắn thành công vào project

N/A

Defa
ult
Valu
e

Rule

Khi user nhập id của gitlab vào textfield → id sẽ
được hiển thị dưới droplist
User nhập id → <enter>
User chọn id ở droplist

User có thể chọn nhiều bản ghi
Sau khi user chọn được id trong droplist →
<Save changes> → BE lưu vào trường
project_git.git_id theo project_id tương ứng

Từ project_id, mapping với
project_git.project_id → lấy ra git_id
BE gọi lại API call list data của C-codeX theo
git_id
→ (Hệ thống đồng bộ data vào bảng
project_git_information)

1'. Project information – 4

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

3

Save
change
s

Butto
n

N/A

User click
để lưu lại
data của
Project
informatio
n

User chọn git_id trong droplist
→ BE query check project_git.git_id user vừa
gắn → Nếu tìm thấy bản ghi project_git.git_id
đã tồn tại <Validate: 1 git_id chỉ gắn với 1
project>
→ BE show message: "GIT id [abc(see page 3)] has
been linked to project + (project.project_name)*
*(project.project_name) = Từ project_git.git_id
user vừa gắn → BE query bảng projec → lấy ra
project_git.project_id
project_git.project_id = project.project_id → lấy
ra project.project_name
Hệ thống tạo bản ghi trong project_git

2, Feature / Function 2: Thêm bảng thông tin Acceptance milestone
2.1.1 Access control
[Contain all the Access control requirements of function linked to POA]
Page

Position

Activity

Permission

Role

Description

Project
information
(projectsetting)

ADMIN_EDIT_P
ROJECT_INFO

ADMIN_EDIT_P
ROJECT_INFO

User được gán activity này sẽ
được enable trường GIT và có
quyền thấy button Reload

Project
information
(projectsetting)

PM_EDIT_PROJ
ECT_INFO

PM_EDIT_PROJ
ECT_INFO

User được gán activity này sẽ
được enable trường GIT và có
quyền thấy button Reload

2.1.2 Graphic User Interface

2.1.3 GUI Element
N
o.

Field
Name

Type

Le
ngt
h

Man
dato
ry

Descriptio
n

Defa
ult
Valu
e

Rule

1'. Project information – 5

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Project
phase

Từ project.project_id → lấy ra
project.project_detail_id = project_detail.id1 →
lấy ra trường project_phase_id

Mapping với setting_config.id2 → lấy ra trường
name
Sectio
n:
Accept
ance
milest
one

Colla
pse/
Expa
nd

Add

N/A

No

Khi user lick add, no sẽ được tăng thêm theo
format n = n + 1

Accept
ance
descrip
tion

textfi
eld

Từ project_id → BE query bảng
acceptance_milestone thoả mãn
project.project_id =
acceptance_milestone.project_id
→ Lấy
ra acceptance_milestone.acceptance_descriptio
n_name
User có thể nhập nội dung của Acceptance
description vào bản ghi mới
HOẶC edit bản ghi cũ

1 http://project_detail.id
2 http://setting_config.id

1'. Project information – 6

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Deliver
able

Drop
dow
n

1. API get data deliverable trong droplist (User
chọn deliverable cho Aceptance milestone)
Từ project_id → BE query bảng project_jira, lấy
ra project_jira_id
→ Mapping với project_task.project_task_id →
Lấy ra project_task_id của các bản ghi thoả mãn
→ Lấy ra project_task_id của các bản ghi thoả
mãn issue_type = Delivery
→ Ticket update project request được chạy phê
duyệt → BE lưu vào
acceptance_milestone_request_detail.deliverab
le_id
2. API get data trong table acceptant milestone
Từ project_id → BE query bảng
acceptance_milestone → lấy ra
accetance_milestone.deliverable_id
BE query in list
acceptance_milestone.deliverable_id =
project_task.project_task_id → lấy ra các bản
ghi thoả mãn project_task.status != Cancel
BE trả project_task.subject để FE map thành tên
của Deliverable ticket

Due
date

Date
picke
r

Từ project_id → BE query bảng
acceptance_milestone → lấy ra
accetance_milestone.due_date với
acceptance_description_id tương ứng
User có thể edit trường này
Chỉ cho phép user chọn due date lớn hơn
startDate của dự án
→ Chặn chọn thời gian <= startDate của dự án

Action
Delete

Click để
xoá toàn
bộ dữ liệu
trong cell

Trường hợp đang có 5 record đã được add, xoá
record thứ 5 → mất dòng đó, chỉ còn lại 4 record
Trường hợp đang có 1 record đã được add, xoá
record thứ nhất đó → Mất thông tin đã add
(nhưng vẫn còn box dropdown để user điền
acceptance milestone, hay còn gọi là giữ nguyên
line đầu tiên những trống thông tin)

1'. Project information – 7

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

2 2'. Đồng bộ từ C-codeX sang Dashboard
A. Rule đồng bộ
Thời gian tính: hàng ngày (4h sáng)

Khi user thực hiện hành động gắn id của gitlab vào dự án bên Dashboard (Project information)
→ Tạo 1 bản ghi trong bảng project_git tương ứng với project_id của dự án bên Dashboard

BE query trong bảng project → lấy ra list project_id thoả mãn status != 2 & closing_date < ngày hiện tại - 1 HOẶC
status != 3 status = 2 & closing_date = ngày hiện tại - 1 HOẶC status in (0,1,4)
→ Đây là list project_id được truyền trong API gọi sang C-codeX
Hê thống vào bảng project_git, trong list tất cả bản ghi có project_id vừa tìm được, nếu:
• project_git.status = NEW → lấy ra git_id → đồng bộ 4 chỉ số thuộc git_id đó vào bảng project_git_information
(truyền list git_id thoả mãn project_git.status fromDate = start_date của dự án, endDat= end_date của dự án )
• status = SYNCHRONIZED → lấy ra git_id → đồng bộ 4 chỉ số có update_at vào ngày trước đó (thuộc git_id đó)
vào bảng project_git_information (truyền git_id, chỉ truyền fromDate, toDate của tháng chạy job)
Nếu ngày chạy JOB là ngày đầu tiên của tháng → BE truyền fromDate = tháng hiện tại - 1, toDate = tháng hiện tại - 1)
và update vào bản ghi của tháng hiện - 1
***Khi user sử dụng button sync data → BE truyền git_id, fromDate = start_date của dự án, endDat= end_date của dự
án truyền git_id, chạy all (không quan tâm đến thời gian)

2'. Đồng bộ từ C-codeX sang Dashboard – 8

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

3 3'. Enhance Open/Update for new project

<Project abbreviation> - <Project code>

System Requirement Specification
<Code>
Version: <Open for new project request>

Issued Status:

<Draft / >

Issued Date:

<mm-dd-yyyy>

Owner:

<Responsible Manager, who issues this document>

Author:

<blanh2>

Location:

<Project Repository>

Confidential Class:

Internal Use

Date:

<mm-dd-yyyy>

Approved by:

<Name>

Signature:

3'. Enhance Open/Update for new project – 9

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Review Information
Role

Required / Suggested

PO

ntson4

Comment

Approval Information
Approver Name

Role

Date

Revision

Comment

(mm-dd-yyyy)
Nguyễn Thái Sơn

PO

Revision History
Revision

Date

Description

Revised by

Reviewer

(mm-dd-yyyy)

Date
(mm-dd-yyyy)

Table of Content
[Press F9 to edit or update table of content. Please remove this text when applying.]
1

Introduction. 13

1.1

Purpose. 14

1.2

Scope. 15

3 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320167
4 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320168
5 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320169

3'. Enhance Open/Update for new project – 10

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

1.3

Definitions, Acronyms and Abbreviations. 16

1.4

References. 17

2

Overall Description. 18

2.1

High level requirement 19

2.2

Product overview. 110

2.3

Product functions. 111

2.4

User characteristics. 112

2.5

Constraints. 113

2.6

Assumptions, dependencies and impact analysis. 114

3

Functional Requirements. 115

3.1

Feature/Function 1. 216

3.1.1

Access control 217

3.1.2

Graphic User Interface. 218

3.1.3

GUI Element 219

3.1.4

Use case. 220

4

Non-functional requirements. 221

4.1

Usability. 222

4.1.1

<Usability Requirement One>. 323

4.2

Security. 324

4.3

Reliability. 325

4.3.1

<Reliability Requirement One>. 326

6 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320170
7 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320171
8 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320172
9 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320173
10 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320174
11 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320175
12 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320176
13 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320177
14 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320178
15 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320179
16 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320180
17 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320181
18 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320182
19 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320183
20 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320184
21 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320185
22 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320186
23 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320187
24 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320188
25 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320189
26 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320190

3'. Enhance Open/Update for new project – 11

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

4.4
4.4.1
4.5
4.5.1

4.6
4.6.1

Performance. 327
<Performance Requirement One>. 328
Supportability. 429
<Supportability Requirement One>. 430
Design Constraints. 431
<Design Constraint One>. 432

4.7

On-line User Documentation and Help System Requirements. 433

4.8

Purchased Components. 434

4.9

Interfaces. 435

4.9.1

User Interfaces. 436

4.9.2

Hardware Interfaces. 437

4.9.3

Software Interfaces. 438

4.9.4

Communications Interfaces. 439

4.10 Environment 440
4.10.1 Target Environment 441
4.10.2 Development Environment 442
4.10.3 Database. 543
4.11 Licensing Requirements. 544
4.12 Legal, Copyright, and Other Notices. 545
4.13 Applicable Standards. 546
5

Appendix. 547

27 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320191
28 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320192
29 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320193
30 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320194
31 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320195
32 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320196
33 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320197
34 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320198
35 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320199
36 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320200
37 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320201
38 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320202
39 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320203
40 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320204
41 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320205
42 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320206
43 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320207
44 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320208
45 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320209
46 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320210
47 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320211

3'. Enhance Open/Update for new project – 12

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

2

Introduction

• Purpose
[Specify the purpose of this SRS. The SRS should fully describe the external behavior of the application or subsystem
identified. It also describes nonfunctional requirements, design constraints and other factors necessary to provide a
complete and comprehensive description of the requirements for the software.]
• Scope
[A brief description of the software application that the SRS applies to; the feature or other subsystem grouping; what
Use-Case model(s) it is associated with; and anything else that is affected or influenced by this document.]
• Definitions, Acronyms and Abbreviations
[Provide the definitions of all terms, acronyms, and abbreviations required to properly interpret the SRS. This
information may be provided by reference to the project Glossary.]
• References
[This subsection should:
• Provide a complete list of all documents referenced elsewhere in the SRS.
• Identify each document by title, report number (if applicable), date and publishing organization.
• Specify the sources from which the references can be obtained.
This information may be provided by reference to an appendix or to another document.]
3

Overall Description

[Describe the general factors that affect the product and its requirements. This section does not state specific
requirements. Instead, it provides a background for those requirements, which are defined in detail in Section 3, and
makes them easier to understand. Include such items as:
• Product overview.
• High level requirement
Với sự phát triển của loại hình dự án (Normal / Proof of concept) → Hệ thống Dashboard cần thêm trường thông
tin mới để user setup 1 dự án là Normal/Proof of concept tại form mở, cập nhật dự án.
Bên cạnh đó, tại form mở, cập nhật dự án cũng có thêm section mới để nhập data cho Acceptance Milestone
1. User cần chọn thông tin cho trường Project phase
2. User cần nhập thông tin Acceptance Milestone
• Product functions: Enhance Project phase, Acceptance milestone

4

Functional Requirements

[Contain all the software requirements to a level of detail sufficient to enable designers to design a system to satisfy
those requirements, and testers to test that the system satisfies those requirements. When using use-case modeling,
these requirements are captured in the Use-Cases and the applicable supplementary specifications. If use-case
modeling is not used, the outline for supplementary specifications may be inserted directly into this section, as shown
below.]
4.1 Feature / Function 1: Enhance Project phase, Acceptance milestone
4.1.1 Access control
[Contain all the Access control requirements of function linked to POA]

3'. Enhance Open/Update for new project – 13

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Page
Position
Activity
Permission
Role
Description

4.1.2 Graphic User Interface

3'. Enhance Open/Update for new project – 14

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

4.1.3 GUI Element

N
o.
Field
Nam
e

I
Open
for
new
proje
ct
requ
est
Typ
e
Le
ng
th
Ma
nda
tor
y
Descri
ption
Def
ault
Val
ue
Rule

3'. Enhance Open/Update for new project – 15

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Proje
ct
phas
e

BE tạo 2 bản ghi mới trong bảng setting_config:
i
d

type_i
d

c
o
d
e

name

level
_sett
ing

desc
ripti
on

st
at
us

pro
prit
y

..
.

Nhờ
BE set
id mới

n
ul
l

Norm
al

0

null

1

0

..
.

Nhờ
BE set
id mới

n
ul
l

Proof
of
conce
pt

0

null

1

0

BE query bảng setting_config, lấy ra những bản ghi name
của
• type_id =[...]
• status = 1
User chọn trong danh sách dropdown
Khi AM submit open for new project request → lưu tại
project_request_detail.project_phase_id
***BE thêm trường project_phase_id trong bảng
project_detail, mapping qua project_request_detail.id &
project_request_detail.project_detail_id
Secti
on:
Acce
ptan
ce
miles
tone
Add

Enable button add đối với AM khi open for new project
request có status là draft

No

Khi user lick add, no sẽ được tăng thêm theo format n = n +
1

3'. Enhance Open/Update for new project – 16

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Acce
ptan
ce
descr
iptio
n

text
fiel
d

User nhập nội dung của Acceptance description

Deliv
erabl
e

Dro
pdo
wn

User click vào dropdown → hiển thị No data

Due
date

Dat
e
pick
er

Chỉ cho phép user chọn due date lớn hơn startDate của dự
án

Actio
nDelet
e

II

→ Chặn chọn thời gian <= startDate của dự án

Click
để xoá
toàn bộ
dữ liệu
trong
cell

Trường hợp đang có 5 record đã được add, xoá record thứ
5 → mất dòng đó, chỉ còn lại 4 record
Trường hợp đang có 1 record đã được add, xoá record thứ
nhất đó → Mất thông tin đã add (nhưng vẫn còn box
dropdown để user điền acceptance milestone, hay còn gọi
là giữ nguyên line đầu tiên những trống thông tin)

Upda
te for
proje
ct
requ
est
Proje
ct
phas
e

Từ project.project_id → lấy ra project.project_detail_id =
project_detail.id → lấy ra trường project_phase_id
Mapping với setting_config.id → lấy ra trường name

Secti
on:
Acce
ptan
ce
miles
tone
Add

Enable button add đối với AM khi update project request có
status là draft

3'. Enhance Open/Update for new project – 17

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

No

Acce
ptan
ce
descr
iptio
n

Khi user lick add, no sẽ được tăng thêm theo format n = n +
1
text
fiel
d

Từ project_id → BE query bảng acceptance_milestone
thoả mãn project.project_id =
acceptance_milestone.project_id
→ Lấy
ra acceptance_milestone.acceptance_description_name
User có thể nhập nội dung của Acceptance description vào
bản ghi mới
HOẶC edit bản ghi cũ

Deliv
erabl
e

Dro
pdo
wn

1. API get data deliverable trong droplist (User chọn
deliverable cho Aceptance milestone)
Từ project_id → BE query bảng project_jira, lấy ra
project_jira_id
→ Mapping với project_task.project_task_id → Lấy ra
project_task_id của các bản ghi thoả mãn
→ Lấy ra project_task_id của các bản ghi thoả
mãn issue_type = Delivery
→ Ticket update project request được chạy phê duyệt →
BE lưu vào
acceptance_milestone_request_detail.deliverable_id
2. API get data trong table acceptant milestone
Từ project_id → BE query bảng acceptance_milestone →
lấy ra accetance_milestone.deliverable_id

Due
date

Dat
e
pick
er

Từ project_id → BE query bảng acceptance_milestone →
lấy ra accetance_milestone.due_date với
acceptance_description_id tương ứng
User có thể edit trường này
Chỉ cho phép user chọn due date lớn hơn startDate của dự
án
→ Chặn chọn thời gian <= startDate của dự án

Actio
nDelet
e

Click
để xoá
toàn bộ
dữ liệu
trong
cell

Trường hợp đang có 5 record đã được add, xoá record thứ
5 → mất dòng đó, chỉ còn lại 4 record
Trường hợp đang có 1 record đã được add, xoá record thứ
nhất đó → Mất thông tin đã add (nhưng vẫn còn box
dropdown để user điền acceptance milestone, hay còn gọi
là giữ nguyên line đầu tiên những trống thông tin)

3'. Enhance Open/Update for new project – 18

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

4 4'. Project input

<Project abbreviation> - <Project code>

System Requirement Specification
<Code>
Version: <Project input>

Issued Status:

<Draft / >

Issued Date:

<mm-dd-yyyy>

Owner:

<Responsible Manager, who issues this document>

Author:

<blanh2>

Location:

<Project Repository>

Confidential Class:

Internal Use

Date:

<mm-dd-yyyy>

Approved by:

<Name>

Signature:

4'. Project input – 19

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Review Information
Role

Required / Suggested

PO

ntson4

Comment

Approval Information
Approver Name

Role

Date

Revision

Comment

(mm-dd-yyyy)
Nguyễn Thái Sơn

PO

Revision History
Revision

Date

Description

Revised by

Reviewer

(mm-dd-yyyy)

Date
(mm-dd-yyyy)

Table of Content
[Press F9 to edit or update table of content. Please remove this text when applying.]
1

Introduction. 148

1.1

Purpose. 149

1.2

Scope. 150

48 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320167
49 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320168
50 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320169

4'. Project input – 20

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

1.3

Definitions, Acronyms and Abbreviations. 151

1.4

References. 152

2

Overall Description. 153

2.1

High level requirement 154

2.2

Product overview. 155

2.3

Product functions. 156

2.4

User characteristics. 157

2.5

Constraints. 158

2.6

Assumptions, dependencies and impact analysis. 159

3

Functional Requirements. 160

3.1

Feature/Function 1. 261

3.1.1

Access control 262

3.1.2

Graphic User Interface. 263

3.1.3

GUI Element 264

3.1.4

Use case. 265

4

Non-functional requirements. 266

4.1

Usability. 267

4.1.1

<Usability Requirement One>. 368

4.2

Security. 369

4.3

Reliability. 370

4.3.1

<Reliability Requirement One>. 371

51 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320170
52 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320171
53 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320172
54 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320173
55 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320174
56 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320175
57 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320176
58 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320177
59 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320178
60 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320179
61 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320180
62 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320181
63 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320182
64 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320183
65 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320184
66 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320185
67 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320186
68 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320187
69 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320188
70 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320189
71 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320190

4'. Project input – 21

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

4.4
4.4.1
4.5
4.5.1

4.6
4.6.1

Performance. 372
<Performance Requirement One>. 373
Supportability. 474
<Supportability Requirement One>. 475
Design Constraints. 476
<Design Constraint One>. 477

4.7

On-line User Documentation and Help System Requirements. 478

4.8

Purchased Components. 479

4.9

Interfaces. 480

4.9.1

User Interfaces. 481

4.9.2

Hardware Interfaces. 482

4.9.3

Software Interfaces. 483

4.9.4

Communications Interfaces. 484

4.10 Environment 485
4.10.1 Target Environment 486
4.10.2 Development Environment 487
4.10.3 Database. 588
4.11 Licensing Requirements. 589
4.12 Legal, Copyright, and Other Notices. 590
4.13 Applicable Standards. 591
5

Appendix. 592

72 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320191
73 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320192
74 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320193
75 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320194
76 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320195
77 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320196
78 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320197
79 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320198
80 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320199
81 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320200
82 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320201
83 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320202
84 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320203
85 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320204
86 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320205
87 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320206
88 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320207
89 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320208
90 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320209
91 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320210
92 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320211

4'. Project input – 22

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

2

Introduction

• Purpose
[Specify the purpose of this SRS. The SRS should fully describe the external behavior of the application or subsystem
identified. It also describes nonfunctional requirements, design constraints and other factors necessary to provide a
complete and comprehensive description of the requirements for the software.]
• Scope
[A brief description of the software application that the SRS applies to; the feature or other subsystem grouping; what
Use-Case model(s) it is associated with; and anything else that is affected or influenced by this document.]
• Definitions, Acronyms and Abbreviations
[Provide the definitions of all terms, acronyms, and abbreviations required to properly interpret the SRS. This
information may be provided by reference to the project Glossary.]
• References
[This subsection should:
• Provide a complete list of all documents referenced elsewhere in the SRS.
• Identify each document by title, report number (if applicable), date and publishing organization.
• Specify the sources from which the references can be obtained.
This information may be provided by reference to an appendix or to another document.]
3

Overall Description

[Describe the general factors that affect the product and its requirements. This section does not state specific
requirements. Instead, it provides a background for those requirements, which are defined in detail in Section 3, and
makes them easier to understand. Include such items as:
• Product overview.
• High level requirement
Với sự phát triển của loại hình dự án (Normal / Proof of concept) → Hệ thống Dashboard cần hiển thị data Criteria
của dự án
1. User nhập thông tin criterial (Feedback, assessment) của dự án Proof of concept
• Product functions:
1. Enhance Project input → Criterial
• User characteristics.
• Assumptions, dependencies and impact analysis.

4

Functional Requirements

[Contain all the software requirements to a level of detail sufficient to enable designers to design a system to satisfy
those requirements, and testers to test that the system satisfies those requirements. When using use-case modeling,
these requirements are captured in the Use-Cases and the applicable supplementary specifications. If use-case
modeling is not used, the outline for supplementary specifications may be inserted directly into this section, as shown
below.]
4.1 Feature / Function 1: Project input
4.1.1 Access control

4'. Project input – 23

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

[Contain all the Access control requirements of function linked to POA]

Page

Position

Activity

Permission

Role

Description

4.1.2 Graphic User Interface

4.1.3 GUI Element
N
o.

I

II

Field
Name

Type

Le
ngt
h

Man
dato
ry

Descriptio
n

Defa
ult
Value

Rule

Criteria
l

Colla
pse/
Expa
nd

Section này sẽ có, khi và chỉ khi project phase =
proof of concept

No

Num
eric

FE đánh số đếm thứ tự cho các dòng trong
bảng

Criteria

Alpha
nume
ric

Nguồn: project_feedback.criteria

Feedba
ck
detail

Alpha
nume
ric

Nguồn: project_feedback.feedback_detail

Expect
ed

Num
eric

Nguồn: project_feedback.expected

Result

Num
eric

Nguồn: project_feedback.result

Table:
Feedba
ck

Add
Criteria

User click ADD → hệ thống
textFi
eld

Đây là trường bắt buộc
Cho phép user nhập thông tin. Tối đa 500 ký tự

4'. Project input – 24

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Feedba
ck
detail

textFi
eld

Đây là trường bắt buộc

Expect
ed (%)

textFi
eld

Đây là trường bắt buộc

Cho phép user nhập thông tin. Tối đa 500 ký tự

Chỉ nhập số: tối đa 3 chữ số nguyên và 2 chữ số
thập phân
Min - Max: 0 - 100
User bỏ trống trường này → submit → "Please
input a number from 0 to 100"

Result
(%)

textFi
eld

Đây là trường bắt buộc
Chỉ nhập số: tối đa 3 chữ số nguyên và 2 chữ số
thập phân
Min - Max: 0 - 100
User bỏ trống trường này → submit → "Please
input a number from 0 to 100"

Button
Cancel

Butto
n

Cancel → Đóng popup

Button
Add

Butto
n

Validate user điền đủ thông tin các trường bắt
buộc → "Create successfully"
Message khi chưa điền đủ thông tin "Please
inout required field"

III

Action
- Edit
Criteria

textFi
eld

BE query bảng project_feedback → lấy ra
project_feedback.criteria theo project_id,
project_feedback_id tương ứng
Đây là trường bắt buộc
Cho phép user nhập thông tin. Tối đa 500 ký tự

Feedba
ck
detail

textFi
eld

BE query bảng project_feedback → lấy ra
project_feedback.feedback_detail theo
project_id, project_feedback_id tương ứng
Đây là trường bắt buộc
Cho phép user nhập thông tin. Tối đa 500 ký tự

4'. Project input – 25

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Expect
ed (%)

textFi
eld

BE query bảng project_feedback → lấy ra
project_feedback.expected theo project_id,
project_feedback_id tương ứng

Đây là trường bắt buộc
Chỉ nhập số: tối đa 3 chữ số nguyên và 2 chữ số
thập phân
Min - Max: 0 - 100
User bỏ trống trường này → submit → "Please
input a number from 0 to 100"
Result
(%)

textFi
eld

BE query bảng project_feedback → lấy ra
project_feedback.result theo project_id,
project_feedback_id tương ứng
Đây là trường bắt buộc
Chỉ nhập số: tối đa 3 chữ số nguyên và 2 chữ số
thập phân
Min - Max: 0 - 100
User bỏ trống trường này → submit → "Please
input a number from 0 to 100"

Button
Cancel

Butto
n

Cancel → Đóng popup

Button
Add

Butto
n

Validate user điền đủ thông tin các trường bắt
buộc → "Update successfully"
Message khi chưa điền đủ thông tin "Please
inout required field"

N
o.

I

Field
Name

Type

Criteria
l

Colla
pse/
Expa
nd

Le
ngt
h

Man
dato
ry

Descriptio
n

Defa
ult
Value

Rule

Section này sẽ có, khi và chỉ khi project phase =
proof of concept

Table:
Assess
ment

4'. Project input – 26

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

II

No

Num
eric

FE đánh số đếm thứ tự cho các dòng trong
bảng

Criteria

Alpha
nume
ric

Nguồn: project_assessment.criteria

Summa
ry

Alpha
nume
ric

Nguồn: project_assessment.summary

Expect
ed

Num
eric

Nguồn: project_assessment.expected

Result

Num
eric

Nguồn: project_assessment.result

Add
Criteria

User click ADD → hệ thống
textFi
eld

Đây là trường bắt buộc

Summa
ry

textFi
eld

Đây là trường bắt buộc

Expect
ed (%)

textFi
eld

Đây là trường bắt buộc

Cho phép user nhập thông tin. Tối đa 500 ký tự

Cho phép user nhập thông tin. Tối đa 500 ký tự

Chỉ nhập số: tối đa 3 chữ số nguyên và 2 chữ số
thập phân
Min - Max: 0 - 100
User bỏ trống trường này → submit → "Please
input a number from 0 to 100"

Result
(%)

textFi
eld

Đây là trường bắt buộc
Chỉ nhập số: tối đa 3 chữ số nguyên và 2 chữ số
thập phân
Min - Max: 0 - 100
User bỏ trống trường này → submit → "Please
input a number from 0 to 100"

Button
Cancel

Butto
n

Cancel → Đóng popup

4'. Project input – 27

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Button
Add

Butto
n

Validate user điền đủ thông tin các trường bắt
buộc

Message khi chưa điền đủ thông tin "Please
inout required field"
III

Action
- Edit
Criteria

textFi
eld

BE query bảng project_assessment → lấy ra
project_assessment.criteria theo project_id,
project_assessment_id tương ứng
Đây là trường bắt buộc
Cho phép user nhập thông tin. Tối đa 500 ký tự

Summa
ry

textFi
eld

BE query bảng project_assessment → lấy ra
project_assessment.summary theo project_id,
project_assessment_id tương ứng
Đây là trường bắt buộc
Cho phép user nhập thông tin. Tối đa 500 ký tự

Expect
ed (%)

textFi
eld

BE query bảng project_assessment → lấy ra
project_assessment.expected theo project_id,
project_assessment_id tương ứng
Đây là trường bắt buộc
Chỉ nhập số: tối đa 3 chữ số nguyên và 2 chữ số
thập phân
Min - Max: 0 - 100
User bỏ trống trường này → submit → "Please
input a number from 0 to 100"

Result
(%)

textFi
eld

BE query bảng project_assessment → lấy ra
project_assessment.result theo project_id,
project_assessment_id tương ứng
Đây là trường bắt buộc
Chỉ nhập số: tối đa 3 chữ số nguyên và 2 chữ số
thập phân
Min - Max: 0 - 100
User bỏ trống trường này → submit → "Please
input a number from 0 to 100"

Button
Cancel

Butto
n

Cancel → Đóng popup

4'. Project input – 28

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Button
Add
Butto
n
Validate user điền đủ thông tin các trường bắt
buộc → "Create successfully"

Message khi chưa điền đủ thông tin "Please
inout required field"

4'. Project input – 29

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

5 5'. Project overview

<Project abbreviation> - <Project code>

System Requirement Specification
<Code>
Version: <Project overview>

Issued Status:

<Draft / >

Issued Date:

<mm-dd-yyyy>

Owner:

<Responsible Manager, who issues this document>

Author:

<blanh2>

Location:

<Project Repository>

Confidential Class:

Internal Use

Date:

<mm-dd-yyyy>

Approved by:

<Name>

Signature:

5'. Project overview – 30

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Review Information
Role

Required / Suggested

PO

ntson4

Comment

Approval Information
Approver Name

Role

Date

Revision

Comment

(mm-dd-yyyy)
Nguyễn Thái Sơn

PO

Revision History
Revision

Date

Description

Revised by

Reviewer

Date

(mm-dd-yyyy)

(mm-dd-yyyy)

Table of Content
[Press F9 to edit or update table of content. Please remove this text when applying.]
1

Introduction. 193

1.1

Purpose. 194

1.2

Scope. 195

93 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320167
94 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320168
95 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320169

5'. Project overview – 31

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

1.3

Definitions, Acronyms and Abbreviations. 196

1.4

References. 197

2

Overall Description. 198

2.1

High level requirement 199

2.2

Product overview. 1100

2.3

Product functions. 1101

2.4

User characteristics. 1102

2.5

Constraints. 1103

2.6

Assumptions, dependencies and impact analysis. 1104

3

Functional Requirements. 1105

3.1

Feature/Function 1. 2106

3.1.1

Access control 2107

3.1.2

Graphic User Interface. 2108

3.1.3

GUI Element 2109

3.1.4

Use case. 2110

4

Non-functional requirements. 2111

4.1

Usability. 2112

4.1.1

<Usability Requirement One>. 3113

4.2

Security. 3114

4.3

Reliability. 3115

4.3.1

<Reliability Requirement One>. 3116

96 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320170
97 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320171
98 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320172
99 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320173
100 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320174
101 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320175
102 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320176
103 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320177
104 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320178
105 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320179
106 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320180
107 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320181
108 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320182
109 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320183
110 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320184
111 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320185
112 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320186
113 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320187
114 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320188
115 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320189
116 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320190

5'. Project overview – 32

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

4.4
4.4.1
4.5
4.5.1

4.6
4.6.1

Performance. 3117
<Performance Requirement One>. 3118
Supportability. 4119
<Supportability Requirement One>. 4120
Design Constraints. 4121
<Design Constraint One>. 4122

4.7

On-line User Documentation and Help System Requirements. 4123

4.8

Purchased Components. 4124

4.9

Interfaces. 4125

4.9.1

User Interfaces. 4126

4.9.2

Hardware Interfaces. 4127

4.9.3

Software Interfaces. 4128

4.9.4

Communications Interfaces. 4129

4.10 Environment 4130
4.10.1 Target Environment 4131
4.10.2 Development Environment 4132
4.10.3 Database. 5133
4.11 Licensing Requirements. 5134
4.12 Legal, Copyright, and Other Notices. 5135
4.13 Applicable Standards. 5136
5

Appendix. 5137

117 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320191
118 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320192
119 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320193
120 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320194
121 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320195
122 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320196
123 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320197
124 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320198
125 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320199
126 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320200
127 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320201
128 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320202
129 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320203
130 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320204
131 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320205
132 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320206
133 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320207
134 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320208
135 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320209
136 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320210
137 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320211

5'. Project overview – 33

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

2

Introduction

• Purpose
[Specify the purpose of this SRS. The SRS should fully describe the external behavior of the application or subsystem
identified. It also describes nonfunctional requirements, design constraints and other factors necessary to provide a
complete and comprehensive description of the requirements for the software.]
• Scope
[A brief description of the software application that the SRS applies to; the feature or other subsystem grouping; what
Use-Case model(s) it is associated with; and anything else that is affected or influenced by this document.]
• Definitions, Acronyms and Abbreviations
[Provide the definitions of all terms, acronyms, and abbreviations required to properly interpret the SRS. This
information may be provided by reference to the project Glossary.]
• References
[This subsection should:
• Provide a complete list of all documents referenced elsewhere in the SRS.
• Identify each document by title, report number (if applicable), date and publishing organization.
• Specify the sources from which the references can be obtained.
This information may be provided by reference to an appendix or to another document.]
3

Overall Description

[Describe the general factors that affect the product and its requirements. This section does not state specific
requirements. Instead, it provides a background for those requirements, which are defined in detail in Section 3, and
makes them easier to understand. Include such items as:
• Product overview.
• High level requirement
Với sự phát triển của việc tích hợp C-codeX vào hệ thống Dashboard, sự phát triển của loại hình dự án (Normal /
Proof of concept) → Hệ thống Dashboard cần hiển thị thêm những data mới & tái cấu trúc giao diện đối với những
data sẵn có
1. User cần view data của việc deliverable dưới dạng Gantt chart thể hiện progress của dự án
2. User cần view data của các chỉ số theo button bar (In month/ Accumulated) và hiển thị 2 chỉ số mới
• Product functions:
1. Gantt chart
2. Project feedback and assessment
3. Enhance UI/UX và hiển thị 2 chỉ số mới tích hợp từ C-CodeX
• User characteristics.
• Assumptions, dependencies and impact analysis.

4

Functional Requirements

[Contain all the software requirements to a level of detail sufficient to enable designers to design a system to satisfy
those requirements, and testers to test that the system satisfies those requirements. When using use-case modeling,
these requirements are captured in the Use-Cases and the applicable supplementary specifications. If use-case

5'. Project overview – 34

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

modeling is not used, the outline for supplementary specifications may be inserted directly into this section, as shown
below.]
4.1 Feature / Function 1: Gantt chart
4.1.1 Access control

[Contain all the Access control requirements of function linked to POA]
Page

Position

Activity

Permission

Role

Description

4.1.2 Graphic User Interface

4.1.3 GUI Element
N
o.

Field
Name

Type

Le
ngt
h

Man
dato
ry

Descriptio
n

Defa
ult
Valu
e

Rule

Section
: Chart

5'. Project overview – 35

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Accept
ance
descrip
tion

Cột mốc sẽ được đổ màu:

Từ project_id, BE query lấy ra
acceptance_milestone.acceptance_description_i
d & acceptance_milestone_name tương ứng
T
ừ
acceptance_milestone.acceptance_description_
id → lấy ra list deliverable_id → mapping với
project_task.project_task_id
→ Lấy ra:
project_task.due_date
project_task.status
project_task.resolution_date
project_task.resolution
1. Acceptance được tính là on-time (Hệ thống
đổ màu xanh) nếu thoả mãn điều kiện
project_task.status = Package-Released hoặc
Close
project_task.resolution = Done
project_task.resolution_date
<= acceptance_milestone.due_date
2. Acceptance được tính là late (Hệ thống đổ
màu đỏ) nếu thoả mãn điều kiện
project_task.status = Package-Released hoặc
Close
project_task.resolution = Done
project_task.resolution_date >
acceptance_milestone.due_date
HOẶC
acceptance_milestone.due_date > ngày hiện
tại
project_task.status != Package-Released,
Close
3. Acceptance chưa thể xác định được Late/ontime (Hệ thống không đổ màu) → các trường
hợp còn lại

5'. Project overview – 36

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Deliver
able
ticket

Từ project_id, BE query lấy ra
acceptance_milestone.deliverable_id
BE query in list deliverable_id vào bảng
project_task thoả
mãn acceptance_milestone.deliverable_id =
project_task.project_task_id
→ lấy ra các bản ghi thoả mãn
project_task.status != cancel
BE trả project_task.subject → FE map thành tên
của Deliverable ticket
BE trả project_task.start_date &
project_task.due_date -> FE map lên Chart theo
range time

Deliver
able
ticket's
color

Từ project_id, BE query lấy ra
acceptance_milestone.deliverable_id
BE query in list deliverable_id vào bảng
project_task thoả
mãn acceptance_milestone.deliverable_id =
project_task.project_task_id
→ lấy ra các bản ghi thoả mãn
project_task.status != cancel
Những status còn lại, hiển thị theo quy ước sau
status = delivery planning → Màu xanh
dương nhạt (suggest: #66b2ff)
status = delivery finalized → Màu xanh
dương đậm ((suggest: #007bff)
status = package implemeting → Màu vàng
(suggest: #ffc107)
status = package resolved → Màu tím
(suggest: #9c27b0)
status = package released → Màu xanh lá cây
nhạt
status = Close → Màu xanh lá cây đậm

Section
: Time
interva
l bar

Khi người dùng giữ hoặc kéo thanh, biểu đồ
hiển thị theo khoảng thời gian đã kéo trong
thanh
BE trả range time 12 tháng → Tháng hiện tại
luôn ở giữa

4.2 Feature / Function 2: Project feedback & assessment
4.2.1 Access control
[Contain all the Access control requirements of function linked to POA]

5'. Project overview – 37

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Page

Position

Activity

Permission

Role

Description

4.2.2 Graphic User Interface

4.2.3 GUI Element
N
o
.

Field
Name

I

Table:
Feedb
ack

II

Ty
pe

L
e
n
g
t
h

Ma
nd
at
or
y

Descriptio
n

Def
ault
Val
ue

Rule

No

Nu
me
ric

FE đánh số đếm thứ tự cho các dòng trong bảng

Criteri
a

Alp
han
um
eric

Nguồn: project_feedback.criteria

Feedb
ack
detail

Alp
han
um
eric

Nguồn: project_feedback.feedback_detail

Expect
ed

Nu
me
ric

Nguồn: project_feedback.expected

Result

Nu
me
ric

Nguồn: project_feedback.result

Table:
Assess
ment

5'. Project overview – 38

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

No

Nu
me
ric

FE đánh số đếm thứ tự cho các dòng trong bảng

Criteri
a

Alp
han
um
eric

Nguồn: project_assessment.criteria

Summ
ary

Alp
han
um
eric

Nguồn: project_assessment.summary

Expect
ed

Nu
me
ric

Nguồn: project_assessment.expected

Result

Nu
me
ric

Nguồn: project_assessment.result

4.3 Feature / Function 3: Enhance UI/UX và hiển thị 2 chỉ số mới tích hợp từ C-CodeX
4.3.1 Access control
[Contain all the Access control requirements of function linked to POA]
Page

Position

Activity

Permission

Role

Description

4.3.2 Graphic User Interface

4.3.3 GUI Element

5'. Project overview – 39

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

N
o.

Field
Name

Type

Le
ngt
h

Man
dato
ry

Descriptio
n

Defa
ult
Valu
e

Rule

0

Sectio
n 0:
Filter

Nếu user để From date > To date → Hệ thống
báo lỗi From date < To date. Please select
another range

Filter
FROM

Format: XX-YYYY
xx: tháng
yyyy: năm

Filter
DATE

Format: XX-YYYY
xx: tháng
yyyy: năm

I

Sectio
n 1: In
month

Bill in
month
s

User chọn FROM-TO
Data của in months sẽ là data của tháng (những
tháng) từ PROM-TO
Num
ber

N/A

N/A

N/A

Làm tròn 2 chữ số sau dấu thập phân
Project_id mapping vào bảng project_billable,
lấy ra các bản ghi theo project_id tương ứng
BIll in month = Tổng project_billable.billable
value theo filter Date (UI/UX) =
project_billable.start_date
-> project_billable.end_date

Allocati
on in
month
s

Làm tròn 2 chữ số sau dấu thập phân
Project_id mapping vào bảng user_plan, lấy ra
các bản ghi theo project_id tương ứng
Allocation của 1 tháng = Tổng số h nhân sự
đươc allocate vào dự án trong tháng / (Số ngày
công trong tháng x 8h)
• Nếu nhân sự nghỉ việc trong tháng hoặc
onboard trong tháng → Tổng số h nhân sự
được allocate chỉ tính theo số ngày nhân sự
có thể làm việc

5'. Project overview – 40

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Planni
ng bill
rate in
month
s

Làm tròn 2 chữ số sau dấu thập phân
Allocation bill rate in month = (Bill in
month / Allocation in month) x100%
Nếu
• Bill rate < AVG → Hiện màu đỏ
• AVG <= Bill rate < USL → Hiện màu cam
• Bill rate >= USL → Hiện màu xanh

Actual
bill
rate in
month
s

Làm tròn 2 chữ số sau dấu thập phân
Allocation bill rate in month = (Bill in month /
Actual timesheet in month) x100%
Nếu
• Bill rate < AVG → Hiện màu đỏ
• AVG <= Bill rate < USL → Hiện màu cam
• Bill rate >= USL → Hiện màu xanh

Actual
timesh
eet in
month
s

Làm tròn 2 chữ số sau dấu thập phân
Project_id mapping vào bảng
project_task_spent_time, lấy ra các bản ghi
theo project_id tương ứng có status = 2
Actual timesheet in month = Tổng số h timesheet
của nhân sự trong tháng / (Số ngày công trong
tháng x 8h)
• Tổng số h timesheet của nhân sự trong
tháng = Hours per day
• Nếu nhân sự nghỉ việc trong tháng hoặc
onboard trong tháng → Tổng số h nhân sự
được allocate chỉ tính theo số ngày nhân sự
có thể làm việc

AI
index
in
month
s

Config data trong bảng mater_data.setting với
setting_config_key = LOC_GENERATED → Lấy ra
data của trường value
Từ project_id = project_git.project_id =
project_git_information.project_id → lấy ra
project_git_information.LoC_generated
theo điều kiện Date trên UIUX
= project_git_information.month&year
AI index in month = value* LoC_generated

5'. Project overview – 41

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

AI
suppor
ting in
month

Config data trong bảng mater_data.setting với
setting_config_key = LOC_ACCEPTED → Lấy ra
data của trường value
Từ project_id = project_git.project_id =
project_git_information.project_id → lấy ra
project_git_information.LoC_accepted
theo điều kiện Date trên UIUX
= project_git_information.month&year
AI index in month = value* LoC_accepted

Budget
billable

Làm tròn 2 chữ số sau dấu thập phân
Từ project_id → query bảng mvv_project →
sum cột divided man_month
Budget billable = Sum divided man_month

II

Sectio
n 2:
Accum
ulated

User chọn FROM-TO

Accum
ulated
Billabl
e

Làm tròn 2 chữ số sau dấu thập phân

Data của accumulated sẽ là data của tháng
(những tháng) từ đầu dự án → TO

Project_id mapping vào bảng project_billable,
lấy ra các bản ghi theo project_id tương ứng
Accumulated billable = Tổng
project_billable.billable value luỹ kế từ đầu dự
án → Date trên UIUX

Accum
ulated
Allocati
on

Làm tròn 2 chữ số sau dấu thập phân
Project_id mapping vào bảng user_plan, lấy ra
các bản ghi theo project_id tương ứng
Allocation của 1 tháng = Tổng số h nhân sự
đươc allocate vào dự án trong tháng / (Số ngày
công trong tháng x 8h)
• Nếu nhân sự nghỉ việc trong tháng hoặc
onboard trong tháng → Tổng số h nhân sự
được allocate chỉ tính theo số ngày nhân sự
có thể làm việc
Accumulated allocation = Tổng MM allocation
luỹ kế từ đầu dự án → Date trên UIUX

5'. Project overview – 42

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Accum
ulated
Planni
ng bill
rate

Accumulated allocation bill rate = Accumulated
billable / Accumulated allocation

Accum
ulated
Actual
bill
rate

Accumulated actual timesheet bill
rate = Accumulated billable / Accumulated
actual timesheet

Nếu
• Bill rate < AVG → Hiện màu đỏ
• AVG <= Bill rate < USL → Hiện màu cam
• Bill rate >= USL → Hiện màu xanh

Nếu
• Bill rate < AVG → Hiện màu đỏ
• AVG <= Bill rate < USL → Hiện màu cam
• Bill rate >= USL → Hiện màu xanh

Accum
ulated
Actual
timesh
eet

Làm tròn 2 chữ số sau dấu thập phân
Project_id mapping vào bảng
project_task_spent_time, lấy ra các bản ghi
theo project_id tương ứng có status = 2
Actual timesheet in month = Tổng số h timesheet
của nhân sự trong tháng / (Số ngày công trong
tháng x 8h)
• Tổng số h timesheet của nhân sự trong
tháng = Hours per day
• Nếu nhân sự nghỉ việc trong tháng hoặc
onboard trong tháng → Tổng số h nhân sự
được allocate chỉ tính theo số ngày nhân sự
có thể làm việc
Accumulated actual timesheet = Tổng MM actual
timesheet luỹ kế từ đầu dự án → Date trên UIUX

Accum
ulated
AI
index

Config data trong bảng mater_data.setting với
setting_config_key = LOC_GENERATED → Lấy ra
data của trường value
Từ project_id = project_git.project_id =
project_git_information.project_id → lấy ra
project_git_information.LoC_generated
thoả mãn project_git_information.date&month
<= date trên UIUX
Accumulated AI index = value* LoC_generated

5'. Project overview – 43

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Accum
ulated
AI
suppor
ting

Config data trong bảng mater_data.setting với
setting_config_key = LOC_ACCEPTED → Lấy ra
data của trường value
Từ project_id = project_git.project_id =
project_git_information.project_id → lấy ra
project_git_information.LoC_accepted
thoả mãn project_git_information.date&month
<= date trên UIUX
Accumulated AI index = value* LoC_accepted

Budget
billable

Làm tròn 2 chữ số sau dấu thập phân
Từ project_id → query bảng mvv_project →
sum cột divided man_month
Budget billable = Sum divided man_month

5'. Project overview – 44

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

6 6'. Project statistics

<Project abbreviation> - <Project code>

System Requirement Specification
<Code>
Version: <Project Statistics>

Issued Status:

<Draft / >

Issued Date:

<mm-dd-yyyy>

Owner:

<Responsible Manager, who issues this document>

Author:

<blanh2>

Location:

<Project Repository>

Confidential Class:

Internal Use

Date:

<mm-dd-yyyy>

Approved by:

<Name>

Signature:

6'. Project statistics – 45

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Review Information
Role

Required / Suggested

PO

ntson4

Comment

Approval Information
Approver Name

Role

Date

Revision

Comment

(mm-dd-yyyy)
Nguyễn Thái Sơn

PO

Revision History
Revision

Date

Description

Revised by

Reviewer

Date

(mm-dd-yyyy)

(mm-dd-yyyy)

Table of Content
[Press F9 to edit or update table of content. Please remove this text when applying.]
1

Introduction. 1138

1.1

Purpose. 1139

1.2

Scope. 1140

138 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320167
139 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320168
140 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320169

6'. Project statistics – 46

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

1.3

Definitions, Acronyms and Abbreviations. 1141

1.4

References. 1142

2

Overall Description. 1143

2.1

High level requirement 1144

2.2

Product overview. 1145

2.3

Product functions. 1146

2.4

User characteristics. 1147

2.5

Constraints. 1148

2.6

Assumptions, dependencies and impact analysis. 1149

3

Functional Requirements. 1150

3.1

Feature/Function 1. 2151

3.1.1

Access control 2152

3.1.2

Graphic User Interface. 2153

3.1.3

GUI Element 2154

3.1.4

Use case. 2155

4

Non-functional requirements. 2156

4.1

Usability. 2157

4.1.1

<Usability Requirement One>. 3158

4.2

Security. 3159

4.3

Reliability. 3160

4.3.1

<Reliability Requirement One>. 3161

141 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320170
142 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320171
143 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320172
144 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320173
145 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320174
146 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320175
147 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320176
148 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320177
149 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320178
150 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320179
151 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320180
152 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320181
153 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320182
154 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320183
155 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320184
156 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320185
157 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320186
158 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320187
159 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320188
160 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320189
161 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320190

6'. Project statistics – 47

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

4.4
4.4.1
4.5
4.5.1

4.6
4.6.1

Performance. 3162
<Performance Requirement One>. 3163
Supportability. 4164
<Supportability Requirement One>. 4165
Design Constraints. 4166
<Design Constraint One>. 4167

4.7

On-line User Documentation and Help System Requirements. 4168

4.8

Purchased Components. 4169

4.9

Interfaces. 4170

4.9.1

User Interfaces. 4171

4.9.2

Hardware Interfaces. 4172

4.9.3

Software Interfaces. 4173

4.9.4

Communications Interfaces. 4174

4.10 Environment 4175
4.10.1 Target Environment 4176
4.10.2 Development Environment 4177
4.10.3 Database. 5178
4.11 Licensing Requirements. 5179
4.12 Legal, Copyright, and Other Notices. 5180
4.13 Applicable Standards. 5181
5

Appendix. 5182

162 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320191
163 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320192
164 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320193
165 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320194
166 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320195
167 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320196
168 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320197
169 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320198
170 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320199
171 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320200
172 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320201
173 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320202
174 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320203
175 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320204
176 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320205
177 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320206
178 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320207
179 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320208
180 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320209
181 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320210
182 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320211

6'. Project statistics – 48

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

2

Introduction

• Purpose
[Specify the purpose of this SRS. The SRS should fully describe the external behavior of the application or subsystem
identified. It also describes nonfunctional requirements, design constraints and other factors necessary to provide a
complete and comprehensive description of the requirements for the software.]
• Scope
[A brief description of the software application that the SRS applies to; the feature or other subsystem grouping; what
Use-Case model(s) it is associated with; and anything else that is affected or influenced by this document.]
• Definitions, Acronyms and Abbreviations
[Provide the definitions of all terms, acronyms, and abbreviations required to properly interpret the SRS. This
information may be provided by reference to the project Glossary.]
• References
[This subsection should:
• Provide a complete list of all documents referenced elsewhere in the SRS.
• Identify each document by title, report number (if applicable), date and publishing organization.
• Specify the sources from which the references can be obtained.
This information may be provided by reference to an appendix or to another document.]
3

Overall Description

[Describe the general factors that affect the product and its requirements. This section does not state specific
requirements. Instead, it provides a background for those requirements, which are defined in detail in Section 3, and
makes them easier to understand. Include such items as:
• Product overview.
• High level requirement
Với sự phát triển của việc tích hợp C-codeX vào hệ thống Dashboard, sự phát triển của loại hình dự án (Normal /
Proof of concept), và sự phát triển của các chỉ số KPI cho dự án Operation
→ Hệ thống Dashboard cần hiển thị thêm những data mới & tái cấu trúc giao diện đối với những data sẵn có
1. User cần view data của các chỉ số KPI thuộc dự án Operation
2. User cần view data của các chỉ số theo button bar (Profit/ Investment/ Total) và hiển thị 1 chỉ số mới (CcodeX) AI Supporting
3. User cần view bảng Milestone
• Product functions:
1. KPI của dự án Operation
2. Enhance UI/UX và hiển thị 1 chỉ số mới tích hợp từ C-CodeX
• User characteristics.
• Assumptions, dependencies and impact analysis.

4

Functional Requirements

[Contain all the software requirements to a level of detail sufficient to enable designers to design a system to satisfy
those requirements, and testers to test that the system satisfies those requirements. When using use-case modeling,
these requirements are captured in the Use-Cases and the applicable supplementary specifications. If use-case

6'. Project statistics – 49

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

modeling is not used, the outline for supplementary specifications may be inserted directly into this section, as shown
below.]
4.1 Feature / Function 1: KPI của dự án Operation
4.1.1 Access control

[Contain all the Access control requirements of function linked to POA]
Page

Position

Activity

Permission

Role

Description

Project
Statistics

11

VIEW

VIEW_PROJECT
_STATISTIC

ADMIN/
BOM/QA/
QAL/
SEPG/
DUL

Các role có activity này
(Enabled) có quyền view màn
hình project statistics
Với role DUL chỉ được xem
màn hình project statistics của
DU người đó

4.1.2 Graphic User Interface

4.1.3 GUI Element
N
o.

Field
Name

Typ
e

Le
ng
th

Ma
nda
tor
y

Descript
ion

Def
ault
Valu
e

Rule

6'. Project statistics – 50

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Secti
on 1:
Filter

Filter
time

Butt
on
bar

N/
A

N/A

User lựa
chọn
xem
data
theo
điều
kiện

End
date

End date - Closing date
1. User chọn End date → BE query bảng project →lấy
ra tất cả bản ghi project_id thoả mãn:
- end_date nằm trong tháng user chọn
- Từ project_id, lấy ra
project.project_detail_id
→ project.project_detail_id = project_detail.id, lấy
ra project_category_id → project_category_id =
setting_config.id → BE chỉ lấy ra những bản ghi
có setting_config.id = 44
2. User chọn Closing date → BE query bảng project →
lấy ra tất cả bản ghi project_id có closing_date
nằm trong tháng user chọn
- closing_date nằm trong tháng user chọn
- Từ project_id, lấy ra
project.project_detail_id
→ project.project_detail_id = project_detail.id, lấy
ra project_category_id → project_category_id =
setting_config.id → BE chỉ lấy ra những bản ghi
có setting_config.id = 44

Hiển thị đầy đủ Button End date, Closing date. User
switch button để select end date
• Khi kết hợp Filter time và Filter end date
TH1: Chọn End date → BE query các dự án có:
+ start date <= ngày cuối cùng của tháng filter
+ End date >= ngày đầu tiên của tháng filter
+ Status = 0/1/2/4
TH2: Chọn Closing date → BE query các dự án có:
+ start date <= ngày cuối cùng của tháng filter
+ Closing date >= ngày đầu tiên của tháng filter hoặc
closing_date = null
+ Status = 0/1/2/4

6'. Project statistics – 51

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Filter
Grou
p&
DU

Sing
le
sele
ct
drop
dow
n

N/
A

N/A

Hiển thị
CMC
Global
và các
Group,
từng DU
của
từng
group

CMC
Glob
al

***Khi click vào, BE query b
ảng dashboard.history_group, tìm các bản ghi có
• depth = 0 hoặc depth = depth của parent_id + 1
• parent_id = 63 hoặc parent_id = 0
• Tháng của history_group.start_date <= Tháng filter
và (tháng của history.group.end_date >= tháng
filter hoặc history.end_date is null)
• Nếu group_id của bản ghi khi query bảng
history_group theo parent_id = group_id của bản
ghi thì phải có ít nhất 1 bản ghi
Sau khi lấy ra, BE cầm group_id để query bảng groups,
tìm group_name để FE hiển thị
Khi user filter, BE tìm các bản ghi có project.group_id =
giá trị user filter tương ứng
***Show DU khi user select Filter Group khác CMC
Global
Khi click vào, BE query bảng dashboard.history_group,
tìm các bản ghi có
• parent_id = FIlter group (#2) user chọn
• depth = depth của parent_id + 1
• Tháng của history_group.start_date <= Tháng filter
và (tháng của history.group.end_date >= tháng
filter hoặc history.end_date is null)
• groups.group_sale = 0
Khi user filter, BE tìm các bản ghi có project.g_id = giá
trị user filter tương ứng

Filter
Proje
ct
status

Khi click vào dropdown, xổ ra 4 giá trị
• All project status
• New
• Running
• Finished
• Closed
Khi filter All project status, BE query project.status =
0,1,4,2
Khi chọn vào 1 giá trị project status, BE query các bản
ghi project có project.status tương ứng
Nếu không có giá trị: Hiển thị No data

6'. Project statistics – 52

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Filter
Proje
ct
type

***Giữ nguyên so với hiện trạng
Nguồn: Bảng project_type
Hiển thị các giá trị từ trên xuống dưới thep
project_type_id tăng dần, có project_type.status = 1
• All project types
• ODC
• Project based
• Hybrid
• Result based
Khi filter All project status, BE query
project.project_type_id theo tất cả giá trị trong bảng
project_type
Khi click vào, query theo project.project_type_id
Nếu không có giá trị: Hiển thị No data

Filter
Proje
ct
level

***Giữ nguyên so với hiện trạng
Nguồn: Bảng setting_config
Hiển thị các giá trị có type_id = 3 và status = 1
• All project level
• Super light
• Light
• Small
• Normal
• Complicated
Thứ tự sắp xếp giá trị từ trên xuống dưới theo giá trị
setting_config.priority tăng dần
Nếu setting_config.priority bằng nhau → Sắp xếp theo
setting_config.id183
Khi click vào, query theo project.project_detail_id →
mapping với project_detail.project_level_id
Nếu không có giá trị: Hiển thị No data

Filter
proje
ct
phase

PENDING đợi dev set id mới trường này

183 http://setting_config.id

6'. Project statistics – 53

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Proje
ct
categ
ory

Dro
pdo
wn

N/
A

N/A

Hiển thị
loại
category
của dự
án

All
cate
gory

BE query bảng setting_config → lấy ra tất cả bản ghi
name thoả mãn:
• type_id = 6
• status = 1
Danh sách droplist của category:
- All category
- Development
- Operation
- Migration
***User chọn Category = Operation

Secti
on 2:
Data
cell
Total
proje
ct

Nu
mbe
r

N/
A

N/A

Hiển thị
tổng số
dự án có
category
=
Operatio
n

N/A

Số dự án có
• thoả mãn filter Time
• project.status = 0/1/4/2
• thoả mãn category = Operation
Từ project_id, lấy ra
project.project_detail_id → project.project_detail_id =
project_detail.id, lấy
ra project_category_id → project_category_id =
setting_config.id → BE chỉ lấy ra những bản ghi
có setting_config.id = 44
và thoả mãn theo điều kiện query của các filter
khác

Proje
ct
type

pie
Char
t

N/
A

N/A

***Giữ nguyên so với hiện trạng
Khi hover vào chart, hiển thị % project type đó chiếm
bao nhiêu so với total project
% = (Số lượng project có project type tương ứng / Total
project) x 100
Làm tròn 2 chữ số sau dấu thập phân

Proje
ct
level

pie
Char
t

N/
A

N/A

***Giữ nguyên so với hiện trạng
Khi hover vào chart, hiển thị % số project có project
level đó chiếm bao nhiêu so với total project
% = (Số lượng project có project level tương ứng /
Total project) x 100
Làm tròn 2 chữ số sau dấu thập phân

6'. Project statistics – 54

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

2.1
Produ
ctivity
statis
tics
Billab
le

PEN
DIN
G

Profit

Investment

Làm tròn 2 chữ số
sau dấu thập phân

Làm tròn 2 chữ số
sau dấu thập phân

BE query in list
project_id →
mapping với
project_type_log.pr
oject_id để lấy ra
những bản ghi có
trường
project_type_id = 1,
2

BE query in list
project_id →
mapping với
project_type_log.pr
oject_id để lấy ra
những bản ghi có
trường
project_type_id = 4

BE query in list
project_id vào bảng
mvv_project →
mapping
mvv_project.project
_id
→ lấy ra trường
mvv_code
***Từ
mvv_project.mvv_c
ode =
project_billable.issu
e_code
→ Lấy ra trường
billable_value
→ Accumulated
billable = Tổng
billable value các
bản ghi luỹ kế từ
đầu dự án → tháng
filter
*** BE query in list
project_id vào bảng
mvv_project → lấy

Tota
l

BE query in list
project_id vào bảng
mvv_project →
mapping
mvv_project.project
_id
→ lấy ra trường
mvv_code
***Từ
mvv_project.mvv_c
ode =
project_billable.issu
e_code
→ Lấy ra trường
billable_value
→ Accumulated
billable = Tổng
billable value các
bản ghi luỹ kế từ
đầu dự án → tháng
filter
*** BE query in list
project_id vào bảng
mvv_project → lấy

6'. Project statistics – 55

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Profit

Investment

ra trường
mvv_project.id184

ra trường
mvv_project.id186

Từ
mvv_project.id185 =
mvv_du_effort.mvv_
id

Từ
mvv_project.id187 =
mvv_du_effort.mvv_
id

→ Lấy ra trường
bill_in_month

→ Lấy ra trường
bill_in_month

→ Billable in month
= Tổng billable value
các bản ghi có start
date và end date
trong tháng filter

→ Billable in month
= Tổng billable value
các bản ghi có start
date và end date
trong tháng filter

Tota
l

184 http://mvv_project.id
185 http://mvv_project.id
186 http://mvv_project.id
187 http://mvv_project.id

6'. Project statistics – 56

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Alloca
tion

Profit

Investment

Total

Làm tròn 2 chữ
số sau dấu
thập phân

Làm tròn 2 chữ
số sau dấu
thập phân

Làm tròn 2 chữ
số sau dấu
thập phân

BE query in list
project_id →
mapping với
project_type_l
og.project_id
để lấy ra
những bản ghi
có trường
project_type_i
d = 1, 2

BE query in list
project_id →
mapping với
project_type_l
og.project_id
để lấy ra
những bản ghi
có trường
project_type_i
d=4

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng
user_plan để
lấy ra các bản
ghi có mvv
tương ứng

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng
user_plan để
lấy ra các bản
ghi có mvv
tương ứng

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng
user_plan để
lấy ra các bản
ghi có mvv
tương ứng

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng baseline
để lấy data
tương ứng

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng baseline
để lấy data
tương ứng

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng baseline
để lấy data
tương ứng

Allocation của
1 tháng = Tổng
số h nhân sự
đươc allocate
vào dự án
trong tháng /
(Số ngày công
trong tháng x
8h)

Allocation của
1 tháng = Tổng
số h nhân sự
đươc allocate
vào dự án
trong tháng /
(Số ngày công
trong tháng x
8h)

Allocation của
1 tháng = Tổng
số h nhân sự
đươc allocate
vào dự án
trong tháng /
(Số ngày công
trong tháng x
8h)
• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
nhân sự
được
allocate chỉ
tính theo
số ngày
nhân sự có
thể làm
việc

6'. Project statistics – 57

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

6'. Project statistics – 58

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Profit
• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
nhân sự
được
allocate chỉ
tính theo
số ngày
nhân sự có
thể làm
việc
Accumulated
allocation =
Tổng MM
allocation luỹ
kế từ đầu dự
án → tháng
filter

Investment

Total

• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
nhân sự
được
allocate chỉ
tính theo
số ngày
nhân sự có
thể làm
việc

Accumulated
allocation =
Tổng MM
allocation luỹ
kế từ đầu dự
án → tháng
filter

Accumulated
allocation =
Tổng MM
allocation luỹ
kế từ đầu dự
án → tháng
filter

6'. Project statistics – 59

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Actua
l
times
heet

Profit

Investment

Total

Làm tròn 2 chữ
số sau dấu
thập phân

Làm tròn 2 chữ
số sau dấu
thập phân

Làm tròn 2 chữ
số sau dấu
thập phân

BE query in list
project_id →
mapping với
project_type_l
og.project_id
để lấy ra
những bản ghi
có trường
project_type_i
d = 1, 2

BE query in list
project_id →
mapping với
project_type_l
og.project_id
để lấy ra
những bản ghi
có trường
project_type_i
d=4

Actual
timesheet in
month = Tổng
số h timesheet
của nhân sự
trong
tháng / (Số
ngày công
trong tháng x
8h)

Actual
timesheet in
month = Tổng
số h timesheet
của nhân sự
trong
tháng / (Số
ngày công
trong tháng x
8h)

Actual
timesheet in
month = Tổng
số h timesheet
của nhân sự
trong
tháng / (Số
ngày công
trong tháng x
8h)

• Tổng số h
timesheet
(có status =
2) của
nhân sự
trong
tháng =
Hours per
day

• Tổng số h
timesheet
(có status =
2) của
nhân sự
trong
tháng =
Hours per
day

• Tổng số h
timesheet
(có status =
2) của
nhân sự
trong
tháng =
Hours per
day
• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
timesheet
của nhân
sự chỉ tính
theo số
ngày nhân
sự có thể
làm việc
Accumulated
actual
timesheet =
Tổng MM
allocation luỹ
kế từ đầu dự
án → Filter
tháng

6'. Project statistics – 60

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Profit

Investment

Total

• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
timesheet
của nhân
sự chỉ tính
theo số
ngày nhân
sự có thể
làm việc

• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
timesheet
của nhân
sự chỉ tính
theo số
ngày nhân
sự có thể
làm việc

Accumulated
actual
timesheet =
Tổng MM
allocation luỹ
kế từ đầu dự
án → Filter
tháng

Accumulated
actual
timesheet =
Tổng MM
allocation luỹ
kế từ đầu dự
án → Filter
tháng

AI
SUPP
ORTI
NG

6'. Project statistics – 61

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Billab
le
rate
Planni
ng
billabl
e rate

Profit

Investment

Total

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

<PROFIT>
Planning
billable rate
in month =
(Billable in
month /
Allocation
in month) x
100

<INVESTME
NT>
Planning
billable rate
in month =
(Billable in
month /
Allocation
in month) x
100

<TOTAL>
Planning
billable rate
in month =
(Billable in
month /
Allocation
in month) x
100

<PROFIT>
Accumulate
d planning
billable rate
in month =
(Accumulat
ed billable /
Accumulate
d
allocation)
x 100

<INVESTME
NT>
Accumulate
d planning
billable rate
in month =
(Accumulat
ed billable /
Accumulate
d
allocation)
x 100

<TOTAL>
Accumulate
d planning
billable rate
in month =
(Accumulat
ed billable /
Accumulate
d
allocation)
x 100

6'. Project statistics – 62

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Actual
billabl
e rate

Profit

Investment

Total

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

<PROFIT>
Actual
billable rate
in month =
(Billable in
month /
Actual
timesheet
in month) x
100

<INVESTME
NT>
Actual billa
ble rate in
month =
(Billable in
month /
Actual
timesheet
in month) x
100

<TOTAL>
Actual billa
ble rate in
month =
(Billable in
month /
Actual
timesheet
in month) x
100

<PROFIT>
Accumulate
d
actual billa
ble rate in
month =
(Accumulat
ed billable /
Accumulate
d actual
timesheet)
x 100

<INVESTME
NT>
Accumulate
d
actual billa
ble rate in
month =
(Accumulat
ed billable /
Accumulate
d actual
timesheet)
x 100

<TOTAL>
Accumulate
d actual
billable rate
in month =
(Accumulat
ed billable /
Accumulate
d actual
timesheet)
x 100

2.2
Qualit
y
statis
tics

6'. Project statistics – 63

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Accu
mulat
ed
Ontime
respo
nse

1. Jira
Làm tròn 2 chữ số sau dấu thập phân
BE query in list project_id vào bảng project_jira → lấy
ra project_jira_id
BE query in list project_jira_id vào bảng
project_jira_ontime_baseline thoả mãn
project_jira_ontime_baseline.issue_type = Number
of On-time response
month, year <= month, year của filter
→ Lấy ra tổng value của
project_jira_ontime_baseline.number_of_on_time(1)
& project_jira_ontime_baseline.number_of_ticket_rece
ived(1')
2. C-ticket
Làm tròn 2 chữ số sau dấu thập phân
BE query in list project_id vào bảng project_cticket →
lấy ra project_cticket_id
BE query in list project_cticket_id vào bảng
project_cticket_ontime_baseline thoả mãn
• project_cticket_ontime_baseline.issue_type = Ontime response
• month, year <= month, year của filter
→ Lấy ra tổng value của
project_cticket_ontime_baseline.number_of_ontime(2)
& project_cticket_ontime_baseline.number_of_ticket_r
eceived(2')
Accumulated On-time response = [(1) + (2)] / [(1') + (2')]
* 100
***Tooltips: Công thức tính On-time response: Số ticket
on-time response / Tổng số ticket response received

6'. Project statistics – 64

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Accu
mulat
ed
Ontime
resol
ution

1. Jira
Làm tròn 2 chữ số sau dấu thập phân
BE query in list project_id vào bảng project_jira → lấy
ra project_jira_id
BE query in list project_jira_id vào bảng
project_jira_ontime_baseline thoả mãn
• project_jira_ontime_baseline.issue_type = Number
of On-time resolution
• month, year <= month, year của filter
→ Lấy ra tổng value của
project_jira_ontime_baseline.number_of_on_time(1)
& project_jira_ontime_baseline.number_of_ticket_rece
ived(1')
2. C-ticket
Làm tròn 2 chữ số sau dấu thập phân
BE query in list project_id vào bảng project_cticket →
lấy ra project_cticket_id
BE query in list project_cticket_id vào bảng
project_cticket_ontime_baseline thoả mãn
• project_cticket_ontime_baseline.issue_type = Ontime resolution
• month, year <= month, year của filter
→ Lấy ra tổng value của
project_cticket_ontime_baseline.number_of_ontime(2)
& project_cticket_ontime_baseline.number_of_ticket_r
eceived(2')
Accumulated On-time response = [(1) + (2)] / [(1') + (2')]
* 100
***Tooltips: Công thức tính On-time resolution: Số
ticket on-time resolution / Tổng số ticket resolution
received

6'. Project statistics – 65

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Accu
mulat
ed
PCV

Làm tròn 2 chữ số sau dấu thập phân
Từ list project_id → BE query bảng project_pcv_rate,
tìm tất cả bản ghi theo project_id có:
• Giá trị project_pcv_rate.accumulated (của 1 dự án)
của bản ghi có end time lớn nhất của tháng filter
• Nếu các bản ghi có end time giống nhau, lấy theo
bản ghi có start time lớn nhất
• Nếu các bản ghi có start time và end time bằng
nhau → Lấy bản ghi được tạo mới nhất
PCV luỹ kế= (Tổng project_pcv_rate.accumulated tháng
filter / Tổng số bản ghi project_pcv_rate đến tháng
filter)
Nếu không có data, hiển thị NA
***Tooltips: Trung bình cộng giá trị PCV lũ kế đến tháng
filter của các dự án

Accu
mulat
ed
CSS

Nguồn: project_css.score_value
Làm tròn 2 chữ số sau dấu thập phân
Từ list project_id, BE query bảng project_css để lấy
score_value
CSS luỹ kế = Tổng score_value CSS từ đầu dự án đến
tháng filter / Tổng số bản ghi CSS từ đầu dự án đến
tháng filter
Nếu không có data, hiển thị NA
***Tooltips: Trung bình cộng giá trị CSS luỹ kế đến
tháng filter của các dự án

Secti
on 3:
Chart
Filter

Plan
ning
billa
ble
rate

Planning billable rate
Actual billable rate
Accumulated On-time response
Accumulated On-time resolution
Accumulated PCV
Accumulated CSS
Billable - Allocation
Billable - Actual timesheet

6'. Project statistics – 66

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Chart

Khi chọn một trong các giá trị → hệ thống show ra biểu
đồ với giá trị tương ứng
Planning billable rate: hiển thị data in month của
tháng filter
Actual billable rate: hiển thị data in month của
tháng filter
Accumulated On-time response: Hiển thị data luỹ
kế đến tháng filter
Accumulated On-time resolution: Hiển thị data luỹ
kế đến tháng filter
Accumulated PCV: Hiển thị data luỹ kế đến tháng
filter
Accumulated CSS: Hiển thị data luỹ kế đến tháng
filter
Billable - Allocation: hiển thị data in month của
tháng filter
Billable - Actual timesheet: hiển thị data in month
của tháng filter

Time
interv
al bar

Khi người dùng giữ hoặc kéo thanh, biểu đồ hiển thị
theo khoảng thời gian đã kéo trong thanh
BE trả range time từ thời gian user filter theo tháng trên
search bar → time quá khứ (tháng hiện tại - 12 tháng)

<Project abbreviation> - <Project code>

System Requirement Specification
<Code>
Version: <Project Statistics>

Issued Status:

<Draft / >

Issued Date:

<mm-dd-yyyy>

Owner:

<Responsible Manager, who issues this document>

Author:

<blanh2>

6'. Project statistics – 67

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Location:
<Project Repository>

Confidential Class:
Internal Use

Date:
<mm-dd-yyyy>

Approved by:
<Name>

Signature:

Review Information

Role
Required / Suggested

PO
ntson4
Comment

6'. Project statistics – 68

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Approval Information
Approver Name

Role

Date

Revision

Comment

(mm-dd-yyyy)

Nguyễn Thái Sơn

PO

Revision History
Revision

Date
(mm-dd-yyyy)

Description

Revised by

Reviewer

Date
(mm-dd-yyyy)

Table of Content

6'. Project statistics – 69

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

[Press F9 to edit or update table of content. Please remove this text when applying.]
1

Introduction. 1188

1.1

Purpose. 1189

1.2

Scope. 1190

1.3

Definitions, Acronyms and Abbreviations. 1191

1.4

References. 1192

2

Overall Description. 1193

2.1

High level requirement 1194

2.2

Product overview. 1195

2.3

Product functions. 1196

2.4

User characteristics. 1197

2.5

Constraints. 1198

2.6

Assumptions, dependencies and impact analysis. 1199

3

Functional Requirements. 1200

3.1

Feature/Function 1. 2201

3.1.1

Access control 2202

3.1.2

Graphic User Interface. 2203

188 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320167
189 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320168
190 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320169
191 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320170
192 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320171
193 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320172
194 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320173
195 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320174
196 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320175
197 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320176
198 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320177
199 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320178
200 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320179
201 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320180
202 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320181
203 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320182

6'. Project statistics – 70

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

3.1.3

GUI Element 2204

3.1.4

Use case. 2205

4

Non-functional requirements. 2206

4.1

Usability. 2207

4.1.1

<Usability Requirement One>. 3208

4.2

Security. 3209

4.3

Reliability. 3210

4.3.1
4.4
4.4.1
4.5
4.5.1
4.6
4.6.1

<Reliability Requirement One>. 3211
Performance. 3212
<Performance Requirement One>. 3213
Supportability. 4214
<Supportability Requirement One>. 4215
Design Constraints. 4216
<Design Constraint One>. 4217

4.7

On-line User Documentation and Help System Requirements. 4218

4.8

Purchased Components. 4219

4.9

Interfaces. 4220

4.9.1

User Interfaces. 4221

4.9.2

Hardware Interfaces. 4222

4.9.3

Software Interfaces. 4223

4.9.4

Communications Interfaces. 4224

204 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320183
205 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320184
206 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320185
207 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320186
208 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320187
209 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320188
210 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320189
211 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320190
212 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320191
213 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320192
214 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320193
215 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320194
216 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320195
217 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320196
218 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320197
219 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320198
220 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320199
221 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320200
222 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320201
223 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320202
224 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320203

6'. Project statistics – 71

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

4.10 Environment 4225
4.10.1 Target Environment 4226
4.10.2 Development Environment 4227
4.10.3 Database. 5228

4.11 Licensing Requirements. 5229
4.12 Legal, Copyright, and Other Notices. 5230
4.13 Applicable Standards. 5231
5

Appendix. 5232

2

Introduction
• Purpose

[Specify the purpose of this SRS. The SRS should fully describe the external behavior of the application or subsystem
identified. It also describes nonfunctional requirements, design constraints and other factors necessary to provide a
complete and comprehensive description of the requirements for the software.]
• Scope
[A brief description of the software application that the SRS applies to; the feature or other subsystem grouping; what
Use-Case model(s) it is associated with; and anything else that is affected or influenced by this document.]
• Definitions, Acronyms and Abbreviations
[Provide the definitions of all terms, acronyms, and abbreviations required to properly interpret the SRS. This
information may be provided by reference to the project Glossary.]
• References
[This subsection should:
• Provide a complete list of all documents referenced elsewhere in the SRS.
• Identify each document by title, report number (if applicable), date and publishing organization.
• Specify the sources from which the references can be obtained.
This information may be provided by reference to an appendix or to another document.]
3

Overall Description

[Describe the general factors that affect the product and its requirements. This section does not state specific
requirements. Instead, it provides a background for those requirements, which are defined in detail in Section 3, and
makes them easier to understand. Include such items as:
• Product overview.
• High level requirement

225 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320204
226 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320205
227 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320206
228 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320207
229 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320208
230 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320209
231 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320210
232 https://wiki.cmcglobal.com.vn/pages/viewpage.action?pageId=117279112#_Toc175320211

6'. Project statistics – 72

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Với sự phát triển của việc tích hợp C-codeX vào hệ thống Dashboard, sự phát triển của loại hình dự án (Normal /
Proof of concept), và sự phát triển của các chỉ số KPI cho dự án Operation

→ Hệ thống Dashboard cần hiển thị thêm những data mới & tái cấu trúc giao diện đối với những data sẵn có
1. User cần view data của các chỉ số KPI thuộc dự án Operation
2. User cần view data của các chỉ số theo button bar (Profit/ Investment/ Total) và hiển thị 1 chỉ số mới (CcodeX) AI Supporting
3. User cần view bảng Milestone
• Product functions:
1. KPI của dự án Operation
2. Enhance UI/UX và hiển thị 1 chỉ số mới tích hợp từ C-CodeX
• User characteristics.
• Assumptions, dependencies and impact analysis.

4

Functional Requirements

[Contain all the software requirements to a level of detail sufficient to enable designers to design a system to satisfy
those requirements, and testers to test that the system satisfies those requirements. When using use-case modeling,
these requirements are captured in the Use-Cases and the applicable supplementary specifications. If use-case
modeling is not used, the outline for supplementary specifications may be inserted directly into this section, as shown
below.]
4.1 Feature / Function 1: KPI của dự án Operation
4.1.1 Access control
[Contain all the Access control requirements of function linked to POA]
Page
Projects

Position

Activity

Permission

Role

Description

VIEW_PROJECT
_STATISTICS

VIEW_PROJECT
_STATISTICS

ADMIN/
BOM/QA/
QAL/
SEPG/
DUL

Các role có activity này
(Enabled) có quyền view màn
hình project statistics
Với role DUL chỉ được xem
màn hình project statistics của
DU người đó

4.1.2 Graphic User Interface

6'. Project statistics – 73

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

4.1.3 GUI Element

N
o.
Field
Name
Typ
e
Le
ng
th
Ma
nda
tor
y
Descript
ion
Def
ault
Valu
e
Rule

Secti
on 1:
Filter

6'. Project statistics – 74

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Filter
time

Butt
on
bar

N/
A

N/A

User lựa
chọn
xem
data
theo
điều
kiện

End
date

End date - Closing date
1. User chọn End date → BE query bảng project →lấy
ra tất cả bản ghi project_id thoả mãn:
- end_date nằm trong tháng user chọn
- Từ project_id, lấy ra
project.project_detail_id
→ project.project_detail_id = project_detail.id, lấy
ra project_category_id → project_category_id =
setting_config.id → BE chỉ lấy ra những bản ghi
có setting_config.id = 44
2. User chọn Closing date → BE query bảng project →
lấy ra tất cả bản ghi project_id có closing_date
nằm trong tháng user chọn
- closing_date nằm trong tháng user chọn
- Từ project_id, lấy ra
project.project_detail_id
→ project.project_detail_id = project_detail.id, lấy
ra project_category_id → project_category_id =
setting_config.id → BE chỉ lấy ra những bản ghi
có setting_config.id = 44

Hiển thị đầy đủ Button End date, Closing date. User
switch button để select end date
• Khi kết hợp Filter time và Filter end date
TH1: Chọn End date → BE query các dự án có:
+ start date <= ngày cuối cùng của tháng filter
+ End date >= ngày đầu tiên của tháng filter
+ Status = 0/1/2/4
TH2: Chọn Closing date → BE query các dự án có:
+ start date <= ngày cuối cùng của tháng filter
+ Closing date >= ngày đầu tiên của tháng filter hoặc
closing_date = null
+ Status = 0/1/2/4

6'. Project statistics – 75

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Filter
Grou
p&
DU

Sing
le
sele
ct
drop
dow
n

N/
A

N/A

Hiển thị
CMC
Global
và các
Group,
từng DU
của
từng
group

CMC
Glob
al

***Khi click vào, BE query b
ảng dashboard.history_group, tìm các bản ghi có
• depth = 0 hoặc depth = depth của parent_id + 1
• parent_id = 63 hoặc parent_id = 0
• Tháng của history_group.start_date <= Tháng filter
và (tháng của history.group.end_date >= tháng
filter hoặc history.end_date is null)
• Nếu group_id của bản ghi khi query bảng
history_group theo parent_id = group_id của bản
ghi thì phải có ít nhất 1 bản ghi
Sau khi lấy ra, BE cầm group_id để query bảng groups,
tìm group_name để FE hiển thị
Khi user filter, BE tìm các bản ghi có project.group_id =
giá trị user filter tương ứng
***Show DU khi user select Filter Group khác CMC
Global
Khi click vào, BE query bảng dashboard.history_group,
tìm các bản ghi có
• parent_id = FIlter group (#2) user chọn
• depth = depth của parent_id + 1
• Tháng của history_group.start_date <= Tháng filter
và (tháng của history.group.end_date >= tháng
filter hoặc history.end_date is null)
• groups.group_sale = 0
Khi user filter, BE tìm các bản ghi có project.g_id = giá
trị user filter tương ứng

Filter
Proje
ct
status

Khi click vào dropdown, xổ ra 4 giá trị
• All project status
• New
• Running
• Finished
• Closed
Khi filter All project status, BE query project.status =
0,1,4,2
Khi chọn vào 1 giá trị project status, BE query các bản
ghi project có project.status tương ứng
Nếu không có giá trị: Hiển thị No data

6'. Project statistics – 76

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Filter
Proje
ct
type

***Giữ nguyên so với hiện trạng
Nguồn: Bảng project_type
Hiển thị các giá trị từ trên xuống dưới thep
project_type_id tăng dần, có project_type.status = 1
• All project types
• ODC
• Project based
• Hybrid
• Result based
Khi filter All project status, BE query
project.project_type_id theo tất cả giá trị trong bảng
project_type
Khi click vào, query theo project.project_type_id
Nếu không có giá trị: Hiển thị No data

Filter
Proje
ct
level

***Giữ nguyên so với hiện trạng
Nguồn: Bảng setting_config
Hiển thị các giá trị có type_id = 3 và status = 1
• All project level
• Super light
• Light
• Small
• Normal
• Complicated
Thứ tự sắp xếp giá trị từ trên xuống dưới theo giá trị
setting_config.priority tăng dần
Nếu setting_config.priority bằng nhau → Sắp xếp theo
setting_config.id233
Khi click vào, query theo project.project_detail_id →
mapping với project_detail.project_level_id
Nếu không có giá trị: Hiển thị No data

233 http://setting_config.id

6'. Project statistics – 77

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Proje
ct
categ
ory

Dro
pdo
wn

N/
A

N/A

Hiển thị
loại
category
của dự
án

All
cate
gory

BE query bảng setting_config → lấy ra tất cả bản ghi
name thoả mãn:
• type_id = 6
• status = 1
Danh sách droplist của category:
- All category
- Development
- Operation
- Migration
***User chọn Category = Operation

Secti
on 2:
Data
cell
Total
proje
ct

Nu
mbe
r

N/
A

N/A

Hiển thị
tổng số
dự án có
category
=
Operatio
n

N/A

Số dự án có
• thoả mãn filter Time
• project.status = 0/1/4/2
• thoả mãn category = Operation
Từ project_id, lấy ra
project.project_detail_id → project.project_detail_id =
project_detail.id, lấy
ra project_category_id → project_category_id =
setting_config.id → BE chỉ lấy ra những bản ghi
có setting_config.id = 44
và thoả mãn theo điều kiện query của các filter
khác

Proje
ct
type

pie
Char
t

N/
A

N/A

***Giữ nguyên so với hiện trạng
Khi hover vào chart, hiển thị % project type đó chiếm
bao nhiêu so với total project
% = (Số lượng project có project type tương ứng / Total
project) x 100
Làm tròn 2 chữ số sau dấu thập phân

Proje
ct
level

pie
Char
t

N/
A

N/A

***Giữ nguyên so với hiện trạng
Khi hover vào chart, hiển thị % số project có project
level đó chiếm bao nhiêu so với total project
% = (Số lượng project có project level tương ứng /
Total project) x 100
Làm tròn 2 chữ số sau dấu thập phân

6'. Project statistics – 78

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

2.1
Produ
ctivity
statis
tics
Billab
le

PEN
DIN
G

Profit

Investment

Làm tròn 2 chữ số
sau dấu thập phân

Làm tròn 2 chữ số
sau dấu thập phân

BE query in list
project_id →
mapping với
project_type_log.pr
oject_id để lấy ra
những bản ghi có
trường
project_type_id = 1

BE query in list
project_id →
mapping với
project_type_log.pr
oject_id để lấy ra
những bản ghi có
trường
project_type_id = 4

BE query in list
project_id vào bảng
mvv_project →
mapping
mvv_project.project
_id

BE query in list
project_id vào bảng
mvv_project →
mapping
mvv_project.project
_id

→ lấy ra trường
mvv_code

→ lấy ra trường
mvv_code

***Từ
mvv_project.mvv_c
ode =
project_billable.issu
e_code
→ Lấy ra trường
billable_value

***Từ
mvv_project.mvv_c
ode =
project_billable.issu
e_code
→ Lấy ra trường
billable_value

→ Accumulated
billable = Tổng
billable value các
bản ghi luỹ kế từ
đầu dự án → tháng
filter

→ Accumulated
billable = Tổng
billable value các
bản ghi luỹ kế từ
đầu dự án → tháng
filter

*** BE query in list
project_id vào bảng
mvv_project → lấy

*** BE query in list
project_id vào bảng
mvv_project → lấy

Tota
l

6'. Project statistics – 79

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Profit

Investment

ra trường
mvv_project.id234

ra trường
mvv_project.id236

Từ
mvv_project.id235 =
mvv_du_effort.mvv_
id

Từ
mvv_project.id237 =
mvv_du_effort.mvv_
id

→ Lấy ra trường
bill_in_month

→ Lấy ra trường
bill_in_month

→ Billable in month
= Tổng billable value
các bản ghi có start
date và end date
trong tháng filter

→ Billable in month
= Tổng billable value
các bản ghi có start
date và end date
trong tháng filter

Tota
l

234 http://mvv_project.id
235 http://mvv_project.id
236 http://mvv_project.id
237 http://mvv_project.id

6'. Project statistics – 80

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Alloca
tion

Profit

Investment

Total

Làm tròn 2 chữ
số sau dấu
thập phân

Làm tròn 2 chữ
số sau dấu
thập phân

Làm tròn 2 chữ
số sau dấu
thập phân

BE query in list
project_id →
mapping với
project_type_l
og.project_id
để lấy ra
những bản ghi
có trường
project_type_i
d=1

BE query in list
project_id →
mapping với
project_type_l
og.project_id
để lấy ra
những bản ghi
có trường
project_type_i
d=4

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng
user_plan để
lấy ra các bản
ghi có mvv
tương ứng

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng
user_plan để
lấy ra các bản
ghi có mvv
tương ứng

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng
user_plan để
lấy ra các bản
ghi có mvv
tương ứng

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng baseline
để lấy data
tương ứng

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng baseline
để lấy data
tương ứng

BE query in list
project_id vào
bảng
mvv_project →
mapping vào
bảng baseline
để lấy data
tương ứng

Allocation của
1 tháng = Tổng
số h nhân sự
đươc allocate
vào dự án
trong tháng /
(Số ngày công
trong tháng x
8h)

Allocation của
1 tháng = Tổng
số h nhân sự
đươc allocate
vào dự án
trong tháng /
(Số ngày công
trong tháng x
8h)

Allocation của
1 tháng = Tổng
số h nhân sự
đươc allocate
vào dự án
trong tháng /
(Số ngày công
trong tháng x
8h)
• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
nhân sự
được
allocate chỉ
tính theo
số ngày
nhân sự có
thể làm
việc

6'. Project statistics – 81

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Profit
• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
nhân sự
được
allocate chỉ
tính theo
số ngày
nhân sự có
thể làm
việc
Accumulated
allocation =
Tổng MM
allocation luỹ
kế từ đầu dự
án → tháng
filter

Investment

Total

• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
nhân sự
được
allocate chỉ
tính theo
số ngày
nhân sự có
thể làm
việc

Accumulated
allocation =
Tổng MM
allocation luỹ
kế từ đầu dự
án → tháng
filter

Accumulated
allocation =
Tổng MM
allocation luỹ
kế từ đầu dự
án → tháng
filter

6'. Project statistics – 82

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Actua
l
times
heet

Profit

Investment

Total

Làm tròn 2 chữ
số sau dấu
thập phân

Làm tròn 2 chữ
số sau dấu
thập phân

Làm tròn 2 chữ
số sau dấu
thập phân

BE query in list
project_id →
mapping với
project_type_l
og.project_id
để lấy ra
những bản ghi
có trường
project_type_i
d=1

BE query in list
project_id →
mapping với
project_type_l
og.project_id
để lấy ra
những bản ghi
có trường
project_type_i
d=4

Actual
timesheet in
month = Tổng
số h timesheet
của nhân sự
trong
tháng / (Số
ngày công
trong tháng x
8h)

Actual
timesheet in
month = Tổng
số h timesheet
của nhân sự
trong
tháng / (Số
ngày công
trong tháng x
8h)

Actual
timesheet in
month = Tổng
số h timesheet
của nhân sự
trong
tháng / (Số
ngày công
trong tháng x
8h)

• Tổng số h
timesheet
(có status =
2) của
nhân sự
trong
tháng =
Hours per
day

• Tổng số h
timesheet
(có status =
2) của
nhân sự
trong
tháng =
Hours per
day

• Tổng số h
timesheet
(có status =
2) của
nhân sự
trong
tháng =
Hours per
day
• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
timesheet
của nhân
sự chỉ tính
theo số
ngày nhân
sự có thể
làm việc
Accumulated
actual
timesheet =
Tổng MM
allocation luỹ
kế từ đầu dự
án → Filter
tháng

6'. Project statistics – 83

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Profit

Investment

Total

• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
timesheet
của nhân
sự chỉ tính
theo số
ngày nhân
sự có thể
làm việc

• Nếu nhân
sự nghỉ
việc trong
tháng hoặc
onboard
trong
tháng →
Tổng số h
timesheet
của nhân
sự chỉ tính
theo số
ngày nhân
sự có thể
làm việc

Accumulated
actual
timesheet =
Tổng MM
allocation luỹ
kế từ đầu dự
án → Filter
tháng

Accumulated
actual
timesheet =
Tổng MM
allocation luỹ
kế từ đầu dự
án → Filter
tháng

AI
SUPP
ORTI
NG

6'. Project statistics – 84

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Billab
le
rate
Planni
ng
billabl
e rate

Profit

Investment

Total

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

<PROFIT>
Planning
billable rate
in month =
(Billable in
month /
Allocation
in month) x
100

<INVESTME
NT>
Planning
billable rate
in month =
(Billable in
month /
Allocation
in month) x
100

<TOTAL>
Planning
billable rate
in month =
(Billable in
month /
Allocation
in month) x
100

<PROFIT>
Accumulate
d planning
billable rate
in month =
(Accumulat
ed billable /
Accumulate
d
allocation)
x 100

<INVESTME
NT>
Accumulate
d planning
billable rate
in month =
(Accumulat
ed billable /
Accumulate
d
allocation)
x 100

<TOTAL>
Accumulate
d planning
billable rate
in month =
(Accumulat
ed billable /
Accumulate
d
allocation)
x 100

6'. Project statistics – 85

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Actual
billabl
e rate

Profit

Investment

Total

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

BE tính toán
ra kết quả,
sau đó mới
làm tròn 2
chữ số sau
dấu thập
phân

<PROFIT>
Actual
billable rate
in month =
(Billable in
month /
Actual
timesheet
in month) x
100

<INVESTME
NT>
Actual billa
ble rate in
month =
(Billable in
month /
Actual
timesheet
in month) x
100

<TOTAL>
Actual billa
ble rate in
month =
(Billable in
month /
Actual
timesheet
in month) x
100

<PROFIT>
Accumulate
d
actual billa
ble rate in
month =
(Accumulat
ed billable /
Accumulate
d actual
timesheet)
x 100

<INVESTME
NT>
Accumulate
d
actual billa
ble rate in
month =
(Accumulat
ed billable /
Accumulate
d actual
timesheet)
x 100

<TOTAL>
Accumulate
d actual
billable rate
in month =
(Accumulat
ed billable /
Accumulate
d actual
timesheet)
x 100

2.2
Qualit
y
statis
tics

6'. Project statistics – 86

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Accu
mulat
ed
Ontime
respo
nse

1. Jira
Làm tròn 2 chữ số sau dấu thập phân
BE query in list project_id vào bảng project_jira → lấy
ra project_jira_id
BE query in list project_jira_id vào bảng
project_jira_ontime_baseline thoả mãn
project_jira_ontime_baseline.issue_type = Number
of On-time response
month, year <= month, year của filter
→ Lấy ra tổng value của
project_jira_ontime_baseline.number_of_on_time(1)
& project_jira_ontime_baseline.number_of_ticket_rece
ived(1')
2. C-ticket
Làm tròn 2 chữ số sau dấu thập phân
BE query in list project_id vào bảng project_cticket →
lấy ra project_cticket_id
BE query in list project_cticket_id vào bảng
project_cticket_ontime_baseline thoả mãn
• project_cticket_ontime_baseline.issue_type = Ontime response
• month, year <= month, year của filter
→ Lấy ra tổng value của
project_cticket_ontime_baseline.number_of_ontime(2)
& project_cticket_ontime_baseline.number_of_ticket_r
eceived(2')
Accumulated On-time response = [(1) + (2)] / [(1') + (2')]
* 100
***Tooltips: Công thức tính On-time response: Số ticket
on-time response / Tổng số ticket response received

6'. Project statistics – 87

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Accu
mulat
ed
Ontime
resol
ution

1. Jira
Làm tròn 2 chữ số sau dấu thập phân
BE query in list project_id vào bảng project_jira → lấy
ra project_jira_id
BE query in list project_jira_id vào bảng
project_jira_ontime_baseline thoả mãn
• project_jira_ontime_baseline.issue_type = Number
of On-time resolution
• month, year <= month, year của filter
→ Lấy ra tổng value của
project_jira_ontime_baseline.number_of_on_time(1)
& project_jira_ontime_baseline.number_of_ticket_rece
ived(1')
2. C-ticket
Làm tròn 2 chữ số sau dấu thập phân
BE query in list project_id vào bảng project_cticket →
lấy ra project_cticket_id
BE query in list project_cticket_id vào bảng
project_cticket_ontime_baseline thoả mãn
• project_cticket_ontime_baseline.issue_type = Ontime resolution
• month, year <= month, year của filter
→ Lấy ra tổng value của
project_cticket_ontime_baseline.number_of_ontime(2)
& project_cticket_ontime_baseline.number_of_ticket_r
eceived(2')
Accumulated On-time response = [(1) + (2)] / [(1') + (2')]
* 100
***Tooltips: Công thức tính On-time resolution: Số
ticket on-time resolution / Tổng số ticket resolution
received

6'. Project statistics – 88

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Accu
mulat
ed
PCV

Làm tròn 2 chữ số sau dấu thập phân
Từ list project_id → BE query bảng project_pcv_rate,
tìm tất cả bản ghi theo project_id có:
• Giá trị project_pcv_rate.accumulated (của 1 dự án)
của bản ghi có end time lớn nhất của tháng filter
• Nếu các bản ghi có end time giống nhau, lấy theo
bản ghi có start time lớn nhất
• Nếu các bản ghi có start time và end time bằng
nhau → Lấy bản ghi được tạo mới nhất
PCV luỹ kế= (Tổng project_pcv_rate.accumulated tháng
filter / Tổng số bản ghi project_pcv_rate đến tháng
filter)
Nếu không có data, hiển thị NA
***Tooltips: Trung bình cộng giá trị PCV lũ kế đến tháng
filter của các dự án

Accu
mulat
ed
CSS

Nguồn: project_css.score_value
Làm tròn 2 chữ số sau dấu thập phân
Từ list project_id, BE query bảng project_css để lấy
score_value
CSS luỹ kế = Tổng score_value CSS từ đầu dự án đến
tháng filter / Tổng số bản ghi CSS từ đầu dự án đến
tháng filter
Nếu không có data, hiển thị NA
***Tooltips: Trung bình cộng giá trị CSS luỹ kế đến
tháng filter của các dự án

Secti
on 3:
Chart
Filter

Plan
ning
billa
ble
rate

Planning billable rate
Actual billable rate
Accumulated On-time response
Accumulated On-time resolution
Accumulated PCV
Accumulated CSS
Billable - Allocation
Billable - Actual timesheet

6'. Project statistics – 89

TDX Delivery – [Dashboard Phase 11] C-codex & GIT

Chart

Khi chọn một trong các giá trị → hệ thống show ra biểu
đồ với giá trị tương ứng
Planning billable rate: hiển thị data in month của
tháng filter
Actual billable rate: hiển thị data in month của
tháng filter
Accumulated On-time response: Hiển thị data luỹ
kế đến tháng filter
Accumulated On-time resolution: Hiển thị data luỹ
kế đến tháng filter
Accumulated PCV: Hiển thị data luỹ kế đến tháng
filter
Accumulated CSS: Hiển thị data luỹ kế đến tháng
filter
Billable - Allocation: hiển thị data in month của
tháng filter
Billable - Actual timesheet: hiển thị data in month
của tháng filter

Time
interv
al bar

Khi người dùng giữ hoặc kéo thanh, biểu đồ hiển thị
theo khoảng thời gian đã kéo trong thanh
BE trả range time từ thời gian user filter theo tháng trên
search bar → time quá khứ (tháng hiện tại - 12 tháng)

6'. Project statistics – 90

---

<a id="27--dashboard-phase-6--pakd-v1-20250625-154028-pdf"></a>
## 27. [Dashboard phase 6] PAKD-v1-20250625_154028.pdf

[Dashboard phase 6] PAKD
TDX Delivery

Exported on 06/25/2025

TDX Delivery – [Dashboard phase 6] PAKD

Table of Contents
1

Business Flow......................................................................................................5

2

ERD Business Plan...............................................................................................6

3

Mockup ................................................................................................................7

3.1

Warning Allocate ...................................................................................................................... 7

3.2

Warning Billing Plan................................................................................................................. 7

3.3

Warning norm setting .............................................................................................................. 8

3.4

Labor rate setting..................................................................................................................... 8

3.5

History business plan............................................................................................................... 9

4

SRS .....................................................................................................................10

4.1

1 Introduction......................................................................................................................... 13

4.2

2 Overall Description.............................................................................................................. 13

4.3

3 Functional Requirements ................................................................................................... 14

4.3.1

3.1 Feature: View Delivery Plan ........................................................................................................................... 14

4.3.1.1 3.1.1 Access control.............................................................................................................................................. 14
4.3.1.2 3.1.2. Use case description .................................................................................................................................. 17
4.3.1.3 3.1.3. Graphic User Interface ............................................................................................................................... 23
4.3.1.4 3.1.4 GUI Element................................................................................................................................................. 24
4.3.2

3.2 Feature: Edit Delivery Plan............................................................................................................................. 40

4.3.2.1 3.2.1 Access control.............................................................................................................................................. 40
4.3.2.2 3.2.2. Use case description .................................................................................................................................. 40
4.3.2.3 3.2.3. Graphic user interface................................................................................................................................ 42
4.3.2.4 3.2.4. GUI Element................................................................................................................................................ 42
4.3.3

3.3 Feature: View Revenue Plan nếu Business Plan Status = Draft.................................................................... 58

4.3.3.1 3.3.1 Access control.............................................................................................................................................. 58
4.3.3.2 3.3.2. Use case description .................................................................................................................................. 61
4.3.3.3 3.3.3. Graphic user interface................................................................................................................................ 64
4.3.3.4 3.3.4. GUI Element................................................................................................................................................ 65
4.3.4

3.4 Feature: View Revenue Plan nếu Business Plan Status khác Draft ............................................................. 79

4.3.4.1 3.4.1 Access control.............................................................................................................................................. 79
4.3.4.2 3.4.2. Use case description .................................................................................................................................. 82
4.3.4.3 3.4.3. Graphic User Interface ............................................................................................................................... 87

– 2

TDX Delivery – [Dashboard phase 6] PAKD

4.3.4.4 3.4.4. GUI Element................................................................................................................................................ 87
4.3.5

3.5 Feature: Edit Revenue Plan.......................................................................................................................... 104

4.3.5.1 3.5.1 Access control............................................................................................................................................ 104
4.3.5.2 3.5.2. Use case description ................................................................................................................................ 104

4.3.5.3 3.5.3. Graphic User Interface ............................................................................................................................. 106
4.3.5.4 3.5.4. GUI Element.............................................................................................................................................. 106
4.3.6

3.6 Feature: View Business Plan tại Status = Draft ........................................................................................... 108

4.3.6.1 3.6.1 Access control............................................................................................................................................ 108
4.3.6.2 3.6.2. Use case description ................................................................................................................................ 109
4.3.6.3 3.6.3. Graphic User Interface ............................................................................................................................. 112
4.3.6.4 3.6.4. GUI Element.............................................................................................................................................. 115
4.3.6.5 3.6.5. Logic lưu data từ revenue plan và delivery plan bảng business_plan_version_detail........................ 142
4.3.7

3.7 Feature: Edit Business Plan ......................................................................................................................... 187

4.3.7.1 3.7.1 Access control............................................................................................................................................ 187
4.3.7.2 3.7.2. Use case description ................................................................................................................................ 189
4.3.7.3 3.7.3. Graphic User Interface ............................................................................................................................. 191
4.3.7.4 3.7.4. GUI Element.............................................................................................................................................. 192
4.3.8

3.8 Feature: Submit Business Plan.................................................................................................................... 202

4.3.8.1 3.8.1 Access control............................................................................................................................................ 202
4.3.8.2 3.8.2 Use case description ................................................................................................................................. 202
4.3.8.3 3.8.3. Graphic User Interface ............................................................................................................................. 204
4.3.8.4 3.8.4. Logic.......................................................................................................................................................... 204
4.3.9

3.9 Feature: Delivery Plan User Action History ................................................................................................. 281

4.3.9.1 3.9.1 Access control............................................................................................................................................ 281
4.3.9.2 3.9.2 Use case description ................................................................................................................................. 281
4.3.9.3 3.9.3 Graphic User Interface .............................................................................................................................. 282
4.3.9.4 3.9.4 GUI Element............................................................................................................................................... 282
4.3.10 Sample Request lưu old_value_string và new_value_string .......................................................................... 283
4.3.11 3.10 Feature: Revenue Plan User Action History .............................................................................................. 285
4.3.11.1 3.10.1 Access control.......................................................................................................................................... 285
4.3.11.2 3.10.2 Use case description ............................................................................................................................... 285
4.3.11.3 3.10.3 Graphic User Interface ............................................................................................................................ 286
4.3.11.4 3.10.4 GUI Element............................................................................................................................................. 286
4.3.12 Sample Request lưu old_value_string và new_value_string .......................................................................... 288
4.3.13 3.11 Feature: Create New Version ..................................................................................................................... 289

– 3

TDX Delivery – [Dashboard phase 6] PAKD

4.3.13.1 3.11.1 Access control.......................................................................................................................................... 289
4.3.13.2 3.11.2 Use case description ............................................................................................................................... 289
4.3.13.3 3.11.3 Graphic User Interface ............................................................................................................................ 291
4.3.13.4 3.11.4 GUI Element............................................................................................................................................. 293

4.3.14 3.12 Feature: View Labor Rate Setting .............................................................................................................. 308
4.3.14.1 3.12.1 Access control.......................................................................................................................................... 308
4.3.14.2 3.12.2 Use case description ............................................................................................................................... 310
4.3.14.3 3.12.3 Graphic user interface............................................................................................................................. 310
4.3.14.4 3.12.4. GUI Element............................................................................................................................................ 310
4.3.15 3.13 Feature: Add Labor Rate Setting ............................................................................................................... 312
4.3.15.1 3.13.1 Access control.......................................................................................................................................... 312
4.3.15.2 3.13.2 Use case description ............................................................................................................................... 314
4.3.15.3 3.13.3 Graphic user interface............................................................................................................................. 314
4.3.15.4 3.13.4. GUI Element............................................................................................................................................ 314
4.3.16 3.14 Feature: Edit Labor Rate Setting ............................................................................................................... 315
4.3.16.1 3.14.1 Access control.......................................................................................................................................... 315
4.3.16.2 3.14.2 Use case description ............................................................................................................................... 319
4.3.16.3 3.14.3 Graphic user interface............................................................................................................................. 319
4.3.16.4 3.14.4. GUI Element............................................................................................................................................ 318
4.3.17 3.15 Feature: Delete Labor Rate Setting ........................................................................................................... 319
4.3.17.1 3.15.1 Access control.......................................................................................................................................... 319
4.3.17.2 3.15.2 Use case description ............................................................................................................................... 324
4.3.17.3 3.15.3 Graphic user interface............................................................................................................................. 325
4.3.17.4 3.15.4. GUI Element............................................................................................................................................ 325

5

System Flow ....................................................................................................326

– 4

TDX Delivery – [Dashboard phase 6] PAKD

1 Business Flow

Business Flow – 5

TDX Delivery – [Dashboard phase 6] PAKD

2 ERD Business Plan

ERD Business Plan – 6

TDX Delivery – [Dashboard phase 6] PAKD

3 Mockup

3.1 Warning Allocate

3.2 Warning Billing Plan

Mockup – 7

TDX Delivery – [Dashboard phase 6] PAKD

3.3 Warning norm setting

3.4 Labor rate setting

Mockup – 8

TDX Delivery – [Dashboard phase 6] PAKD

3.5 History business plan

Mockup – 9

TDX Delivery – [Dashboard phase 6] PAKD

4 SRS
<Project abbreviation> - <Project code>
System Requirement Specification

<Code>
Version: <Customer>
Issued Status:

<Draft>

Issued Date:

<>

Owner:

<ntson4>

Author:

<nbtduy>

Location:

<Project Repository>

Confidential Class:

Internal Use

Date:

<mm-dd-yyyy>

Approved by:

<Name>

Signature:

Review Information
Role

Required / Suggested

PO

ntson4

Comment

Approval Information
Approver Name

Role

Date

Revision

Comment

(mm-dd-yyyy)

SRS – 10

TDX Delivery – [Dashboard phase 6] PAKD

Nguyễn Thái Sơn

PO

Revision History
Revision

Date

Description

Revised by

Reviewer

(mm-ddyyyy)

Date
(mm-ddyyyy)

Table of Content
• 1 Introduction(see page 13)
• 2 Overall Description(see page 13)
• 3 Functional Requirements(see page 14)
• 3.1 Feature: View Delivery Plan(see page 14)
• 3.1.1 Access control(see page 14)
• 3.1.2. Use case description(see page 17)
• 3.1.3. Graphic User Interface(see page 23)
• 3.1.4 GUI Element(see page 24)
• 3.2 Feature: Edit Delivery Plan(see page 40)
• 3.2.1 Access control(see page 40)
• 3.2.2. Use case description(see page 40)
• 3.2.3. Graphic user interface(see page 42)
• 3.2.4. GUI Element(see page 42)
• 3.3 Feature: View Revenue Plan nếu Business Plan Status = Draft(see page 58)
• 3.3.1 Access control(see page 58)
• 3.3.2. Use case description(see page 61)
• 3.3.3. Graphic user interface(see page 64)
• 3.3.4. GUI Element(see page 65)
• 3.4 Feature: View Revenue Plan nếu Business Plan Status khác Draft(see page 79)
• 3.4.1 Access control(see page 79)
• 3.4.2. Use case description(see page 82)
• 3.4.3. Graphic User Interface(see page 87)
• 3.4.4. GUI Element(see page 87)
• 3.5 Feature: Edit Revenue Plan(see page 104)
• 3.5.1 Access control(see page 104)
• 3.5.2. Use case description(see page 104)
• 3.5.3. Graphic User Interface(see page 106)

SRS – 11

TDX Delivery – [Dashboard phase 6] PAKD

• 3.5.4. GUI Element(see page 106)
• 3.6 Feature: View Business Plan tại Status = Draft(see page 108)
• 3.6.1 Access control(see page 108)
• 3.6.2. Use case description(see page 109)
• 3.6.3. Graphic User Interface(see page 112)
• 3.6.4. GUI Element(see page 115)
• 3.6.5. Logic lưu data từ revenue plan và delivery plan bảng business_plan_version_detail(see
page 142)

• 3.7 Feature: Edit Business Plan(see page 187)
• 3.7.1 Access control(see page 187)
• 3.7.2. Use case description(see page 189)
• 3.7.3. Graphic User Interface(see page 191)
• 3.7.4. GUI Element(see page 192)
• 3.8 Feature: Submit Business Plan(see page 202)
• 3.8.1 Access control(see page 202)
• 3.8.2 Use case description(see page 202)
• 3.8.3. Graphic User Interface(see page 204)
• 3.8.4. Logic(see page 204)
• 3.8.4.1. Business Plan(see page 204)
• 3.8.4.1. Revenue Plan(see page 278)
• 3.9 Feature: Delivery Plan User Action History(see page 281)
• 3.9.1 Access control(see page 281)
• 3.9.2 Use case description(see page 281)
• 3.9.3 Graphic User Interface(see page 282)
• 3.9.4 GUI Element(see page 282)
• Sample Request lưu old_value_string và new_value_string(see page 283)
• 3.10 Feature: Revenue Plan User Action History(see page 285)
• 3.10.1 Access control(see page 285)
• 3.10.2 Use case description(see page 285)
• 3.10.3 Graphic User Interface(see page 286)
• 3.10.4 GUI Element(see page 286)
• Sample Request lưu old_value_string và new_value_string(see page 288)
• 3.11 Feature: Create New Version(see page 289)
• 3.11.1 Access control(see page 289)
• 3.11.2 Use case description(see page 289)
• 3.11.3 Graphic User Interface(see page 291)
• 3.11.4 GUI Element(see page 293)
• 3.11.4.1. Delivery Plan(see page 293)
• 3.11.4.2. Revenue Plan(see page 303)
• 3.11.4.3. Business Plan(see page 307)
• 3.12 Feature: View Labor Rate Setting(see page 308)
• 3.12.1 Access control(see page 308)
• 3.12.2 Use case description(see page 310)
• 3.12.3 Graphic user interface(see page 310)
• 3.12.4. GUI Element(see page 310)
• 3.13 Feature: Add Labor Rate Setting(see page 312)
• 3.13.1 Access control(see page 312)
• 3.13.2 Use case description(see page 314)
• 3.13.3 Graphic user interface(see page 314)
• 3.13.4. GUI Element(see page 314)
• 3.14 Feature: Edit Labor Rate Setting(see page 315)
• 3.14.1 Access control(see page 315)
• 3.14.2 Use case description(see page 319)

SRS – 12

TDX Delivery – [Dashboard phase 6] PAKD

• 3.14.3 Graphic user interface(see page 319)
• 3.14.4. GUI Element(see page 318)
• 3.15 Feature: Delete Labor Rate Setting(see page 319)
• 3.15.1 Access control(see page 319)
• 3.15.2 Use case description(see page 324)
• 3.15.3 Graphic user interface(see page 325)
• 3.15.4. GUI Element(see page 325)

4.1 1 Introduction
• Purpose:
• Expectation:

4.2 2 Overall Description
• Product functions:
• Assumptions, dependencies and impact analysis

SRS – 13

TDX Delivery – [Dashboard phase 6] PAKD

4.3 3 Functional Requirements

4.3.1 3.1 Feature: View Delivery Plan
4.3.1.1 3.1.1 Access control
Page

Activity

Permission

Role

Descripti
on

Busine
ss plan
detail

VIEW_DELIVERY_PLAN_BY_M
EMBER_DU

VIEW_DELIVERY_PLAN_BY_M
EMBER_DU

DB-MEMBER, DBSALE, DB-PM

Từ
users.user
_id →
mapping
group_us
er lấy ra
bản ghi có
start_date
lớn
nhất →
lấy
group_id
mapping
groups lấy
group_na
me (1)
Khi user
chọn
button
Delivery
Plan Filter
Departme
nt, user
chỉ có thể
chọn vào
giá trị = (1)

SRS – 14

TDX Delivery – [Dashboard phase 6] PAKD

Page

Activity

Permission

Role

Descripti
on

Busine
ss plan
detail

VIEW_DELIVERY_PLAN_BY_M
EMBER_G

VIEW_DELIVERY_PLAN_BY_M
EMBER_G

DB-PMO

Từ
users.user
_id →
mapping
group_us
er lấy ra
bản ghi có
start_date
lớn
nhất →
lấy
group_id
mapping
history_gr
oup lấy
parent_id
của bản
ghi có
start_date
lớn
nhất →
mapping
groups lấy
group_na
me (2)
Khi user
chọn
button
Delivery
Plan Filter
Departme
nt, giá trị
xổ ra →
mapping
history_gr
oup theo
group_id
lấy
parent_id
→
mapping
groups
theo
group_id
lấy
group_na
me = (2)

SRS – 15

TDX Delivery – [Dashboard phase 6] PAKD

Page

Activity

Permission

Role

Descripti
on

Busine
ss plan
detail

VIEW_DELIVERY_PLAN_ALL

VIEW_DELIVERY_PLAN_ALL

DB-ADMIN, DB-FC,
DB-BOM

Trên
button
Delivery
Plan Filter
Departme
nt, user có
thể chọn
tất cả giá
trị

Busine
ss plan
detail

VIEW_DELIVERY_PLAN_BY_M
ANAGER_DU

VIEW_DELIVERY_PLAN_BY_M
ANAGER_DU

DB-DUL, DB-BUL

Khi user
vào màn,
BE truyền
user_id
vào bảng
group_ma
nager lấy
ra các
group_id
tương
ứng →
map vào
bảng
group lấy
group_na
me (3)
Khi user
chọn
button
Delivery
Plan Filter
Departme
nt, user
chỉ có thể
chọn vào
giá trị = (3)

SRS – 16

TDX Delivery – [Dashboard phase 6] PAKD

Page

Activity

Permission

Busine
ss plan
detail

VIEW_DELIVERY_PLAN_BY_M
ANAGER_G

VIEW_DELIVERY_PLAN_BY_M
ANAGER_G

Role

Descripti
on
Khi user
vào màn,
BE truyền
user_id
vào bảng
group_ma
nager lấy
ra các
group_id
tương
ứng →
map vào
bảng
group lấy
group_na
me (3)
Khi user
chọn
button
Delivery
Plan Filter
Departme
nt, giá trị
xổ ra →
mapping
history_gr
oup theo
group_id
lấy
parent_id
→
mapping
groups
theo
group_id
lấy
group_na
me = (2)

4.3.1.2 3.1.2. Use case description
Use case

View Delivery Plan

Precondition
Thiết bị user sử dụng phải có kết nối internet

SRS – 17

TDX Delivery – [Dashboard phase 6] PAKD

Description
Post condition

User xem được thông tin thành công

SRS – 18

TDX Delivery – [Dashboard phase 6] PAKD

Main Flow

User: Tại màn Business Plan
List, click button View Details

System: Mở màn hình
Business Plan Detail
User: Trong section Business
Plan, chọn Tab Delivery Plan
System: Hiển thị màn hình tab
Delivery Plan

Business Rule

GUI Reference

Nếu business_plan_version.status
khác Draft → Các ô nhập bị disable

G1

Section Resource Information
• freeze label row đầu, freeze
column Resource Type, Resource
Full Name, LDAP khi scroll
• Khi user scroll trong table
Resource Information, Lazy load
20 records/lần
• Cột tháng điền thông tin
headcount sẽ gen số lượng tháng
theo logic sau
• Nếu
business_plan_version.stat
us = Draft
• project_code_crm_i
nformation.order_t
ype = T&M → Số
lượng tháng sẽ theo
project_code_crm_i
nformation.start_da
te và
project_code_crm_i
nformation.end_dat
e
• project_code_crm_i
nformation.order_t
ype khác T&M → Số
lượng tháng sẽ theo
business_plan_versi
on.planning_start_d
ate và
business_plan_versi
on.planning_end_d
ate
• Nếu
business_pla
n_version.pl
anning_start
_date is
null → lấy
theo project
_code_crm_i
nformation.s
tart_date

SRS – 19

TDX Delivery – [Dashboard phase 6] PAKD

• Nếu
business_pla
n_version.pl
anning_end_
date is
null → lấy
theo project
_code_crm_i
nformation.e
nd_date
• Nếu
business_plan_version.stat
us khác Draft
• business_plan_versi
on.order_type =
T&M → Số lượng
tháng sẽ theo
business_plan_versi
on.start_date và
business_plan_versi
on.end_date
• business_plan_versi
on.order_type khác
T&M → Số lượng
tháng sẽ theo
business_plan_versi
on.planning_start_d
ate và
business_plan_versi
on.planning_end_d
ate
• Nếu
business_pla
n_version.pl
anning_start
_date is
null → lấy
theo
business_pla
n_version.st
art_date
• Nếu
business_pla
n_version.pl
anning_end_
date is
null → lấy
theo
business_pla
n_version.en
d_date
Table Other Expenses

SRS – 20

TDX Delivery – [Dashboard phase 6] PAKD

• Các expense category được hiển
thị ra từ setting_config có
• setting_config.status = 1
• type_id → mapping
setting_type có name =
Expense Category
• Sắp xếp expense category
trong table Other Expense
từ trên xuống dưới theo
setting_config.priority tăng
dần
• Cột tháng điền thông tin expenses
sẽ gen số lượng tháng theo logic
sau
• Nếu
business_plan_version.stat
us = Draft
• project_code_crm_i
nformation.order_t
ype = T&M → Số
lượng tháng sẽ theo
project_code_crm_i
nformation.start_da
te và
project_code_crm_i
nformation.end_dat
e
• project_code_crm_i
nformation.order_t
ype khác T&M → Số
lượng tháng sẽ theo
business_plan_versi
on.planning_start_d
ate và
business_plan_versi
on.planning_end_d
ate
• Nếu
business_pla
n_version.pl
anning_start
_date is
null → lấy
theo project
_code_crm_i
nformation.s
tart_date

SRS – 21

TDX Delivery – [Dashboard phase 6] PAKD

• Nếu
business_pla
n_version.pl
anning_end_
date is
null → lấy
theo project
_code_crm_i
nformation.e
nd_date
• Nếu
business_plan_version.stat
us khác Draft
• business_plan_versi
on.order_type =
T&M → Số lượng
tháng sẽ theo
business_plan_versi
on.start_date và
business_plan_versi
on.end_date
• business_plan_versi
on.order_type khác
T&M → Số lượng
tháng sẽ theo
business_plan_versi
on.planning_start_d
ate và
business_plan_versi
on.planning_end_d
ate
• Nếu
business_pla
n_version.pl
anning_start
_date is
null → lấy
theo
business_pla
n_version.st
art_date
• Nếu
business_pla
n_version.pl
anning_end_
date is
null → lấy
theo
business_pla
n_version.en
d_date

SRS – 22

TDX Delivery – [Dashboard phase 6] PAKD

4.3.1.3 3.1.3. Graphic User Interface

G1. View Delivery Plan

G2. Click Use filters to find specific resources

SRS – 23

TDX Delivery – [Dashboard phase 6] PAKD

G3. Click detail filter

4.3.1.4 3.1.4 GUI Element

#
Field
Description
Control Type
Data
Type
Default
Value
Editable?
Mandatory?

(Y/N/NA)
(Y/N/NA)

Section 1: Delivery
Plan Data View
Switch

SRS – 24

TDX Delivery – [Dashboard phase 6] PAKD

1
Button Filter
Department
Single select
dropdown
String
NA
NA
NA

Section 2: Summary

1 http://business_plan_version.id

SRS – 25

TDX Delivery – [Dashboard phase 6] PAKD

2

MM Effort

Label

String

NA

NA

NA

3

Direct Labor Cost

Label

String

NA

NA

NA

4

Outsourcing Cost

Label

String

NA

NA

NA

5

Equipment,
Internet, Server, etc.

Label

String

NA

NA

NA

2 http://business_plan_version.id
3 http://delivery_plan.id

SRS – 26

TDX Delivery – [Dashboard phase 6] PAKD

6

Onsite expense

Label

String

NA

NA

NA

Non-deductible
input VAT

Label

String

NA

NA

NA

7

Overtime

Label

String

NA

NA

NA

8

Other

Label

String

NA

NA

NA

Section 2: Resource
Information Filter

4 http://business_plan_version.id
5 http://delivery_plan.id
6 http://business_plan_version.id
7 http://delivery_plan.id
8 http://business_plan_version.id
9 http://delivery_plan.id
10 http://business_plan_version.id
11 http://delivery_plan.id

SRS – 27

TDX Delivery – [Dashboard phase 6] PAKD

9

Filter

Filter

String

NA

NA

NA

12 http://setting_config.name
13 http://setting_type.name
14 http://setting_config.name
15 http://setting_type.name
16 http://setting_config.name
17 http://setting_type.name
18 http://setting_config.name
19 http://setting_type.name

SRS – 28

TDX Delivery – [Dashboard phase 6] PAKD

10

Show/Hide column

Checkbox

NA

Check

NA

NA

11

Headcount/Labor
cost switch

Button bar

NA

Headcount

NA

NA

Section 3: Resource
Information
12

Collapse button

Collapse
button

NA

NA

NA

NA

13

Plus button

Button

NA

NA

NA

NA

14

Minus button

Button

NA

NA

NA

NA

15

Load data from

Dropdown

NA

NA

NA

NA

16

Resource Type

Dropdown

String

NA

NA

NA

SRS – 29

TDX Delivery – [Dashboard phase 6] PAKD

17

Resource Full Name

Dropdown

String

NA

NA

NA

18

LDAP

Label

String

NA

NA

NA

19

Location

Dropdown

String

NA

NA

NA

20

Employee Type

Dropdown

String

NA

NA

NA

20 http://business_plan_version.id
21 http://delivery_plan.id
22 http://setting_config.name
23 http://business_plan_version.id
24 http://delivery_plan.id
25 http://setting_config.name
26 http://setting_config.name
27 http://business_plan_version.id
28 http://delivery_plan.id
29 http://setting_config.name
30 http://business_plan_version.id
31 http://delivery_plan.id
32 http://setting_config.name

SRS – 30

TDX Delivery – [Dashboard phase 6] PAKD

21

Original Gross
Salary

Numberic field

Number

NA

NA

NA

22

Gross Salary

Label

String

NA

NA

NA

23

Position

Dropdown

String

NA

NA

NA

24

Role

Dropdown

String

NA

NA

NA

33 http://business_plan_version.id
34 http://delivery_plan.id
35 http://business_plan_version.id
36 http://delivery_plan.id
37 http://business_plan_version.id
38 http://delivery_plan.id
39 http://setting_config.name
40 http://business_plan_version.id
41 http://delivery_plan.id
42 http://setting_config.name

SRS – 31

TDX Delivery – [Dashboard phase 6] PAKD

25

Row Total Value

Labtael

String

NA

NA

NA

26

Month Value

Numberic field/
Label

Number

NA

NA

NA

43 http://business_plan_version.id
44 http://delivery_plan.id
45 http://business_plan_version.id
46 http://delivery_plan.id
47 http://delivery_planmember.id
48 http://business_plan_version.id
49 http://delivery_plan.id
50 http://delivery_planmember.id

SRS – 32

TDX Delivery – [Dashboard phase 6] PAKD

51 http://business_plan_version.id

52 http://delivery_plan.id

53 http://delivery_planmember.id

SRS – 33

TDX Delivery – [Dashboard phase 6] PAKD

27

Column Total Value

Label

String

NA

NA

NA

Section 4: Resource
Information
Reference

54 http://business_plan_version.id
55 http://delivery_plan.id
56 http://business_plan_version.id
57 http://delivery_plan.id
58 http://delivery_plan_member.id

SRS – 34

TDX Delivery – [Dashboard phase 6] PAKD

28
Allocated
Label
String
NA
NA
NA

SRS – 35

TDX Delivery – [Dashboard phase 6] PAKD

29
Actual TS
Label
String
NA
NA
NA

SRS – 36

TDX Delivery – [Dashboard phase 6] PAKD

30
Book
Label
String
NA
NA
NA

SRS – 37

TDX Delivery – [Dashboard phase 6] PAKD

31
Available
Label
String
NA
NA
NA

32

Set

Button

NA

NA

NA

NA

Section 5: Other
expenses

33

Collapse

Button

NA

NA

NA

NA

34

Plus Button

Button

NA

NA

NA

NA

SRS – 38

TDX Delivery – [Dashboard phase 6] PAKD

35

Minus Button

Button

NA

NA

NA

NA

36

Expense Category

Dropdown

String

NA

NA

NA

37

Monthly Expense
Value

Numberic field

Number

NA

NA

NA

38

Total Expense Value

Label

Number

NA

NA

NA

Section 6: Reference
- Location Exchange
Rate
39

Location

Label

String

NA

NA

NA

40

Exchange Rate

Numberic field

String

NA

NA

NA

Section 7: Reference
- Location Salary
and Expense Index
41

Location

Label

String

NA

NA

NA

42

Salary Index

Label

String

NA

NA

NA

43

Expense Index

Label

String

NA

NA

NA

59 http://business_plan_version.id

SRS – 39

TDX Delivery – [Dashboard phase 6] PAKD

4.3.2 3.2 Feature: Edit Delivery Plan

4.3.2.1 3.2.1 Access control
Pag
e

Activity

Permission

Role

Description

Busi
ness
plan
deta
il

EDIT_DELIVERY_PLAN_AL
L_STATUS

EDIT_DELIVERY_PLAN_AL
L_STATUS

DBFC

User có activity này sẽ có thể edit
Delivery Plan tại tất cả business
plan status
User không có activity này sẽ chỉ
edit Delivery Plan tại business plan
status = Draft

4.3.2.2 3.2.2. Use case description
Use case

Edit Delivery Plan

Precondition
Thiết bị user sử dụng phải có kết nối internet

Description

Post condition
User edit thông tin thành công

SRS – 40

TDX Delivery – [Dashboard phase 6] PAKD

Main Flow

User: Tại màn Delivery Plan,
user thay đổi thông tin
System: Hiển thị button Save/
Cancel

User: Ấn Save
System: Lưu thông tin và hiển
thị thông báo lưu thành công

Business Rule

GUI Reference

Khi user thay đổi thông tin, hệ thống
sẽ hiển thị button Save/Cancel

G1

Khi button Save/Cancel hiện ra, user
không được switch sang button bar
Business Plan/Revenue Plan (button
bar bị disable), khi hover vào button
bar sẽ hiện tooltip "Please save or
cancel saving data in this sheet before
switching to another sheet"
Khi user ấn save, BE chỉ create/update
các data user update trên giao diện,
data được lưu vào các bảng sau
Section Resources Information
• delivery_plan
• delivery_plan_member
• delivery_plan_budget_MM
Section Other expenses
• delivery_plan
• delivery_plan_other_expenses
• delivery_plan_other_expenses_de
tail
Section Reference:
Nếu user update 1 giá trị trong phần
exchange rate theo location, Khi
save→ lưu tất cả data trên giao diện
vào bảng business_plan_labor_rate

Exception Flow

User: Tại màn Delivery Plan,
user thay đổi thông tin nhưng
không điền required field
System: Hiển thị button Save/
Cancel
User: Ấn Save
System: Báo lỗi Please input
required fields

Business Rule

GUI Reference

Section Resources Information

G2

• User chưa điền data trường
Resource Type và Resource Full
Name mà ấn Save → Báo lỗi
Please input required fields (G2)
Section Other expenses
• User chưa điền data trường
Resource Type và Resource Full
Name mà ấn Save → Báo lỗi
Please input required fields (G2)

SRS – 41

TDX Delivery – [Dashboard phase 6] PAKD

4.3.2.3 3.2.3. Graphic user interface

G1.

G2

4.3.2.4 3.2.4. GUI Element

#

Field

Descri
ption

Contr
ol
Type

Data
Type

Defa
ult
Valu
e

Edita
ble?

Mand
atory?

(Y/N/
NA)

(Y/N/
NA)

Rule

Not
e

SRS – 42

TDX Delivery – [Dashboard phase 6] PAKD

Section
1:
Resourc
e
Informa
tion
1

Collaps
e button

Collap
se
button

NA

NA

NA

NA

Khi user click vào collapse ở
hàng đầu tiên → Đóng/Mở
tất cả collapse bên trong
bảng Resource Information
Khi user click vào collapse ở
bên trong bảng Resource
Information → Đóng/Mở
thông tin Allocated/Act

2

Plus
button

Button

NA

NA

NA

NA

Khi click vào, add thêm 1
dòng cho table Resource
Information, dòng được add
sẽ nằm dưới label của table

3

Minus
button

Button

NA

NA

NA

NA

Khi click vào, xoá dòng tương
ứng cho table Resource
Information

SRS – 43

TDX Delivery – [Dashboard phase 6] PAKD

4

Load
data
from

Dropd
own

NA

NA

NA

NA

Khi click vào, xổ ra các giá trị
sau
• Load data from Resource
Allocation
• Load data from Actual
Timesheet
• Load data from Available
• Load data from Book
Khi click vào giá trị, Load data
của layer tương ứng lên
section Resource Information
(Chưa lưu vào database)
Nếu user đó chưa được
add trên section Resource
Information → Tạo bản
ghi mới
Nếu user đó đã được add
trên section Resource
Information → FE fill số
MM tương ứng vào Month
Value (#14)
Logic load data: Tham khảo
field #17, #18, #19, #20

5

Resourc
e Type

Dropd
own

Strin
g

NA

NA

Y

Khi click vào
• Lấy ra list
setting_config.name60
thoả mãn điều kiện sau
• setting_config.typ
e_id → mapping
setting_type theo
id →
setting_type.name61
= Resource Type
• setting_config.stat
us = 1
• User có thể search trên
search dropdown
Khi ấn save, lưu data vào
delivery_plan_member.type_
resource_id

60 http://setting_config.name
61 http://setting_type.name

SRS – 44

TDX Delivery – [Dashboard phase 6] PAKD

6

Resourc
e Full
Name

Dropd
own

Strin
g

NA

NA

Y

Nếu user chọn Resource Type
= User
Khi click vào, hiển thị giá trị
theo logic sau

HIển thị 10
users.full_name có
users.user_id lớn nhất
Nếu user chọn Resource Type
= Generic Resource
Khi user click chọn, xổ ra các
giá trị theo logic sau
• Từ delivery_plan.id
mapping
delivery_plan_member
theo delivery_plan_id →
lấy ra user_name của các
bản ghi thoả mãn điều
kiện
• resource_type_id
của bản ghi→
mapping
setting_config
theo id → lấy
setting_config.typ
e_id mapping
setting_type → chỉ
lấy
setting_type.name62
= Resource Type
• resource_type_id
của bản ghi→
mapping
setting_config
theo id → chỉ lấy
setting_type.name
= Generic Resource
→ lấy list
delivery_plan_member.user_
name để hiển thị trên giá trị
dropdown
Khi user nhập tên người
không có trong list xổ ra →
hiển thị giá trị Create "Tên
user nhập" → User click chọn
giá trị đó → hệ thống tạo ra
nhân sự ảo mới

62 http://setting_type.name

SRS – 45

TDX Delivery – [Dashboard phase 6] PAKD

User có thể search trên
Dropdown theo
users.user_name và
users.full_name
Khi ấn save, lưu data vào
delivery_plan_member.user_i
d
7

LDAP

Label

Strin
g

NA

NA

Y

Khi user chọn trường
Resource Full Name
• Nếu Resource Type =
User → LDAP =
users.user_name tương
ứng
• Nếu Resource Type =
Generic Resource → LDAP
= Resource Full Name

8

Locatio
n

Dropd
own

Strin
g

NA

NA

NA

Khi user chọn Resource Full
Name mà Resource Type =
User → lấy user_id mapping
vào bảng users lấy
users.location_working →
mapping bảng
user_location_country_mapp
ing theo location → lấy ra
country để FE tự fill vào giá trị
trường location
Khi click vào, Lấy ra
list setting_config.name63
thoả mãn điều kiện sau
• setting_config.type_id →
mapping setting_type
theo id
→ setting_type.name64 =
Location
• setting_config.status = 1
Khi ấn save, lưu data vào
delivery_plan_member.locati
on

63 http://setting_config.name/
64 http://setting_type.name/

SRS – 46

TDX Delivery – [Dashboard phase 6] PAKD

9

Employ
ee Type

Dropd
own

Strin
g

NA

NA

NA

Khi click vào,
• Lấy ra
list setting_config.name65
thoả mãn điều kiện sau
• setting_config.typ
e_id → mapping
setting_type theo
id
→
setting_type.name66
= Employee Type
• setting_config.stat
us = 1
• User có thể search trên
search dropdown
Khi ấn save, lưu data vào
delivery_plan_member.type_i
d

10

11

Original
Gross
Salary

Numb
eric
field

Num
ber

Gross
Salary
(VND)

Label

Strin
g

NA

NA

NA

User chỉ được nhập số và dấu
"." thập phân
Khi ấn save, lưu data vào
delivery_plan_member.origin
al_gross_salary

NA

NA

NA

Khi user nhập giá trị trên
trường Original Gross
Salary → FE sẽ dùng giá trị
nhập trên trường đó và từ
location của nhân sự đó →
mapping ra exchange_rate
của location đó là bao
nhiêu → Tính được Gross
Salary (VND)
Gross Salary (VND) = Original
Gross Salary được nhập x
Exchange Rate của location
của nhân sự tương ứng
Khi ấn save, lưu data vào
delivery_plan_member.gross
_salary_vnd

65 http://setting_config.name/
66 http://setting_type.name/

SRS – 47

TDX Delivery – [Dashboard phase 6] PAKD

12

Position

Dropd
own

Strin
g

NA

NA

NA

Khi click vào, lấy giá trị
crm_master_Data_mapping.n
ame thoả mãn điều kiện
• master_Data_name =
Position
• Status = 1
• is_category = 0
Khi ấn save, lưu data vào
delivery_plan_member.positi
on_id

13

Role

Dropd
own

Strin
g

NA

NA

NA

Khi click vào, lấy giá trị từ
project_role.name
Khi ấn save, lưu data vào
delivery_plan_member.role_i
d

14

Row
Total
Value

Label

Strin
g

NA

NA

NA

Tham khảo use case View
Delivery Plan

15

Month
Value

Numb
eric
field/
Label

Num
ber

NA

NA

NA

User chỉ có thể edit nếu
Headcount/Labor cost switch
= Headcount

Label

Strin
g

16

Column
Total
Value

User chỉ có thể nhập giá trị số
hoặc dấu thập phân "."
NA

NA

NA

Tham khảo use case View
Delivery Plan

Section
4:
Resourc
e
Informa
tion
Referen
ce

SRS – 48

TDX Delivery – [Dashboard phase 6] PAKD

17

Allocate
d

Label

Strin
g

NA

NA

NA

Khi user chọn trường
Resource Full Name và
expand collapse trong table
Resources Information, BE
query theo logic sau

Truy vấn bảng user_plan theo
• user_id = user_id được
chọn
• project_code =
business_plan.project_co
de
• type = 0
• allocation_type = 0
→ Ra được list (1)
(1) → Inner join 2 bảng
group_user và user_plan theo
user_id với điều kiện
• group_user.start_date <=
up.to_date
• group_user.end_date >=
user_plan.from_date or
group_user.end_date is
null
• group_user.group_id =
group_id của Delivery
Plan đang được Filter ở
button Filter Department
Check các bản ghi
• Nếu
group_user.start_date <
user_plan.from_date
→ from_date mới của
bản ghi user_plan =
user_plan.from_date
• Nếu
group_user.start_date >=
user_plan.from_date →
from_date mới của bản
ghi user_plan =
group_user.start_date
• Nếu
group_user.end_date <=
user_plan.to_date →
to_date mới của bản ghi
user_plan =
group_user.end_date

SRS – 49

TDX Delivery – [Dashboard phase 6] PAKD

• Nếu
group_user.end_date >
user_plan.to_date hoặc
group_user.end_date is
null → to_date mới của
bản ghi user_plan =
user_plan.to_date
Lấy from_date và to_date
mới → tính ra số ngày công x
effort per day→ tính ra số h
trong khoảng được
allocate → tính tổng số h
trong tháng được allocate (2)
Tính MM allocated = Tổng số h
trong tháng được allocate /
Số ngày công trong tháng đó

SRS – 50

TDX Delivery – [Dashboard phase 6] PAKD

18

Actual
TS

Label

Strin
g

NA

NA

NA

Khi user chọn trường
Resource Full Name và
expand collapse trong table
Resources Information, BE
query theo logic sau
Truy vấn bảng
project_task_spent_time theo
• user_id = user_id được
chọn
• subject =
business_plan.project_co
de
• allocation_type = 0
Ra được list (1)
(1) → Inner join 2 bảng
group_user và
project_task_spent_time theo
user_id với điều kiện
• project_task_spent_time.
start_date >=
group_user.start_date
• project_task_spent_time.
start_date <=
group_user.end_date or
group_user.end_date is
null
• group_user.group_id =
group_id của Delivery
Plan đang được Filter ở
button Filter Department
→ lấy được list bản ghi
tương ứng (2)
TÍnh tổng số
project_task_spent_time.effo
rt_per_day trong từng tháng
dựa theo
project_tast_spent_time.start
_date
Tính MM Actual TS = tổng số
project_task_spent_time.effo
rt_per_day trong tháng / Số
ngày công trong tháng đó

SRS – 51

TDX Delivery – [Dashboard phase 6] PAKD

19

Book

Label

Strin
g

NA

NA

NA

Khi user chọn trường
Resource Full Name và
expand collapse trong table
Resources Information, BE
query theo logic sau

Truy vấn bảng user_plan theo
• user_id = user_id được
chọn
• project_code =
business_plan.project_co
de
• type = 1
• allocation_type = 0
→ Ra được list (1)
(1) → Inner join 2 bảng
group_user và user_plan theo
user_id với điều kiện
• group_user.start_date <=
up.to_date
• group_user.end_date >=
user_plan.from_date or
group_user.end_date is
null
• group_user.group_id =
group_id của Delivery
Plan đang được Filter ở
button Filter Department
Check các bản ghi
• Nếu
group_user.start_date <
user_plan.from_date
→ from_date mới của
bản ghi user_plan =
user_plan.from_date
• Nếu
group_user.start_date >=
user_plan.from_date →
from_date mới của bản
ghi user_plan =
group_user.start_date
• Nếu
group_user.end_date <=
user_plan.to_date →
to_date mới của bản ghi
user_plan =
group_user.end_date

SRS – 52

TDX Delivery – [Dashboard phase 6] PAKD

• Nếu
group_user.end_date >
user_plan.to_date hoặc
group_user.end_date is
null → to_date mới của
bản ghi user_plan =
user_plan.to_date
Lấy from_date và to_date
mới → tính ra số ngày công x
effort per day→ tính ra số h
trong khoảng được book →
tính tổng số h trong tháng
được book (2)
Tính MM book = Tổng số h
trong tháng được book / Số
ngày công trong tháng đó

SRS – 53

TDX Delivery – [Dashboard phase 6] PAKD

20

Availabl
e

Label

Strin
g

NA

NA

NA

Khi user chọn trường
Resource Full Name và
expand collapse trong table
Resources Information, BE
query theo logic sau

Truy vấn bảng user_plan theo
• user_id = user_id được
chọn
• project_code =
business_plan.project_co
de
• type = 0
• allocation_type = 0
→ Ra được list (1)
(1) → Inner join 2 bảng
group_user và user_plan theo
user_id với điều kiện
• group_user.start_date <=
up.to_date
• group_user.end_date >=
user_plan.from_date or
group_user.end_date is
null
• group_user.group_id =
group_id của Delivery
Plan đang được Filter ở
button Filter Department
Check các bản ghi
• Nếu
group_user.start_date <
user_plan.from_date
→ from_date mới của
bản ghi user_plan =
user_plan.from_date
• Nếu
group_user.start_date >=
user_plan.from_date →
from_date mới của bản
ghi user_plan =
group_user.start_date
• Nếu
group_user.end_date <=
user_plan.to_date →
to_date mới của bản ghi
user_plan =
group_user.end_date

SRS – 54

TDX Delivery – [Dashboard phase 6] PAKD

• Nếu
group_user.end_date >
user_plan.to_date hoặc
group_user.end_date is
null → to_date mới của
bản ghi user_plan =
user_plan.to_date
Lấy from_date và to_date
mới → tính ra số ngày công x
effort per day→ tính ra số h
trong khoảng được
allocate → tính tổng số h
trong tháng được allocate (2)
Lấy tổng số ngày công trong
tháng x 8h - (2) = Số h nhân sự
available trong tháng
Tính MM allocated = Tổng số h
nhân sự available trong
tháng / Số ngày công trong
tháng đó
21

Set

Button

NA

NA

NA

NA

Khi click vào, fill data tương
ứng vào ô nhập MM của nhân
sự đó

Section
5: Other
expense
s
22

Collaps
e

Button

NA

NA

NA

NA

23

Plus
Button

Button

NA

NA

NA

NA

Khi ấn vào, thêm 1 dòng cho
category đó

24

Minus
Button

Button

NA

NA

NA

NA

Khi ấn vào, bớt 1 dòng cho
category đó

25

Expense
Categor
y

Dropd
own

Strin
g

NA

NA

NA

Từ
delivery_plan_other_expense
s.cost_name hiển thị

26

Monthly
Expense
Value

Numb
eric
field

Num
ber

NA

NA

NA

Từ
delivery_plan_other_expense
s_detail.value

SRS – 55

TDX Delivery – [Dashboard phase 6] PAKD

27

Total
Expense
Value

Label

Num
ber

NA

NA

NA

Từ
delivery_plan_other_expense
s_detail.value của month year
tương ứng

Label

Strin
g

NA

NA

NA

Từ
business_plan_version.id67
mapping
business_plan_location_exch
ange_rate

Section
6:
Referen
ce Locatio
n
Exchang
e Rate
28

Locatio
n

Nếu có bản ghi → lấy data đó
ra hiển thị
Nếu không có bản ghi → vào
bảng
location_default_exchange_r
ate để hiển thị
29

Exchang
e Rate

Label

Strin
g

NA

NA

NA

Từ
business_plan_version.id68
mapping
business_plan_location_exch
ange_rate
Nếu có bản ghi → lấy data đó
ra hiển thị
Nếu không có bản ghi → vào
bảng
location_default_exchange_r
ate để hiển thị

67 http://business_plan_version.id
68 http://business_plan_version.id/

SRS – 56

TDX Delivery – [Dashboard phase 6] PAKD

Section
7:
Referen
ce Locatio
n Salary
and
Expense
Index
30

Locatio
n

Label

Strin
g

NA

NA

NA

labor_rate_config.location

31

Salary
Index

Label

Strin
g

NA

NA

NA

labor_rate_config..salary_ind
ex

32

Expense
Index

Label

Strin
g

NA

NA

NA

labor_rate_config.expense_in
dex

SRS – 57

TDX Delivery – [Dashboard phase 6] PAKD

4.3.3 3.3 Feature: View Revenue Plan nếu Business Plan Status = Draft

4.3.3.1 3.3.1 Access control
Page

Activity

Permission

Role

Descripti
on

Busine
ss plan
detail

VIEW_REVENUE_PLAN_BY_M
EMBER_DU

VIEW_REVENUE_PLAN_BY_M
EMBER_DU

DB-MEMBER, DBSALE, DB-PM

Từ
users.user
_id →
mapping
group_us
er lấy ra
bản ghi có
start_date
lớn
nhất →
lấy
group_id
mapping
groups lấy
group_na
me (1)
Khi user
chọn
button
Revenue
Plan Filter
Departme
nt, user
chỉ có thể
chọn vào
giá trị = (1)

SRS – 58

TDX Delivery – [Dashboard phase 6] PAKD

Page

Activity

Permission

Role

Descripti
on

Busine
ss plan
detail

VIEW_REVENUE_PLAN_BY_M
EMBER_G

VIEW_REVENUE_PLAN_BY_M
EMBER_G

DB-PMO

Từ
users.user
_id →
mapping
group_us
er lấy ra
bản ghi có
start_date
lớn
nhất →
lấy
group_id
mapping
history_gr
oup lấy
parent_id
của bản
ghi có
start_date
lớn
nhất →
mapping
groups lấy
group_na
me (2)
Khi user
chọn
button Re
venue
Plan Filter
Departme
nt, giá trị
xổ ra →
mapping
history_gr
oup theo
group_id
lấy
parent_id
→
mapping
groups
theo
group_id
lấy
group_na
me = (2)

SRS – 59

TDX Delivery – [Dashboard phase 6] PAKD

Page

Activity

Permission

Role

Descripti
on

Busine
ss plan
detail

VIEW_REVENUE_PLAN_ALL

VIEW_REVENUE_PLAN_ALL

DB-ADMIN, DB-FC,
DB-BOM

Trên
button Re
venue
Plan Filter
Departme
nt, user có
thể chọn
tất cả giá
trị

Busine
ss plan
detail

VIEW_REVENUE_PLAN_BY_M
ANAGER_DU

VIEW_REVENUE_PLAN_BY_M
ANAGER_DU

DB-DUL, DB-BUL

Khi user
vào màn,
BE truyền
user_id
vào bảng
group_ma
nager lấy
ra các
group_id
tương
ứng →
map vào
bảng
group lấy
group_na
me (3)
Khi user
chọn
button Re
venue
Plan Filter
Departme
nt, user
chỉ có thể
chọn vào
giá trị = (3)

SRS – 60

TDX Delivery – [Dashboard phase 6] PAKD

Page

Activity

Permission

Role

Busine
ss plan
detail

VIEW_REVENUE_PLAN_BY_M
ANAGER_G

VIEW_REVENUE_PLAN_BY_M
ANAGER_G

Descripti
on
Khi user
vào màn,
BE truyền
user_id
vào bảng
group_ma
nager lấy
ra các
group_id
tương
ứng →
map vào
bảng
group lấy
group_na
me (3)
Khi user
chọn
button Re
venue
Plan Filter
Departme
nt, giá trị
xổ ra →
mapping
history_gr
oup theo
group_id
lấy
parent_id
→
mapping
groups
theo
group_id
lấy
group_na
me = (2)

4.3.3.2 3.3.2. Use case description
Use case

View Revenue Plan

Precondition
Thiết bị của user phải có kết nối internet

SRS – 61

TDX Delivery – [Dashboard phase 6] PAKD

Description
Post condition

SRS – 62

TDX Delivery – [Dashboard phase 6] PAKD

Main Flow

User: Tại màn Business Plan List, click button
View Details
System: Mở màn hình Business Plan Detail

User: Trong section Business Plan, chọn Tab
Revenue Plan
System: Hiển thị màn hình tab Revenue Plan

Business Rule

Section Software production revenue information

• freeze label row đầu, freeze column Position, Unit Pri
• Khi user scroll trong table Software production reven
• Cột tháng điền thông tin headcount sẽ gen số lượng
• Nếu business_plan_version.status = Draft
• Số lượng tháng sẽ theo project_code_
project_code_crm_information.end_d

Logic lấy dữ liệu của Software production revenue inform

1. Nếu business_plan_version.status = Draft và Revenu
sale_wo_v2 theo điều kiện

• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng crm_master_data_ma
Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter Department user chọn
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)

Từ trường id của list (2) → query unit_fulfilling_order the

Từ (3) mapping bảng dpm_delivery theo unit_fulfilling_o

• tháng của sale_wo_v2.start_date của list (2) <= dpm_
• tháng của sale_wo_v2.end_date của list (2) >= dpm_d
Logic query dữ liệu ở trên ký hiệu là

2. Nếu business_plan_version.status = Draft và Revenu
sale_wo_v2 theo điều kiện

• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng crm_master_data_ma
Type
• parent_wo_key is null
• wo_status khác cancel
→ Ra được list (4)
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (4)
• wo_status khác Cancel
→ ra được list bản ghi (5)
Từ trường id của list (5) → query unit_fulfilling_order
• theo sale_wo_v2_id tương ứng của list (5)

SRS – 63

TDX Delivery – [Dashboard phase 6] PAKD

• unit_fulfilling_order.group_id = giá trị user filter trên
→ ra được list bản ghi (6)

Từ (3) mapping bảng dpm_delivery theo unit_fulfilling_o

• tháng của sale_wo_v2.start_date của list (5) <= dpm_
• tháng của sale_wo_v2.end_date của list (5) >= dpm_d
Logic query dữ liệu ở trên ký hiệu là

Table Other Revenues

• Các expense revenues được hiển thị ra từ setting_co
• setting_config.status = 1
• type_id → mapping setting_type có name = R
• Sắp xếp revenue category trong table Other R
dần
• Cột tháng điền thông tin revenue sẽ gen số lượng thá
• Nếu business_plan_version.status = Draft
• Số lượng tháng sẽ theo project_code_
project_code_crm_information.end_d

4.3.3.3 3.3.3. Graphic user interface

G1. Màn hình Revenue Plan nếu Button Filter Department = Delivery

SRS – 64

TDX Delivery – [Dashboard phase 6] PAKD

G2. Màn hình Revenue Plan nếu Button Filter Department = Sales

4.3.3.4 3.3.4. GUI Element
#

Field

Descr
iption

Contr
ol
Type

Dat
a
Typ
e

Defa
ult
Valu
e

Edita
ble?

Mand
atory?

(Y/N/
NA)

(Y/N/
NA)

Rule

No
te

Section
1:
Revenu
e Data
View
Switch

SRS – 65

TDX Delivery – [Dashboard phase 6] PAKD

1

Button
Filter
Depart
ment

Single
select
dropd
own

Strin
g

NA

NA

NA

Khi click vào
Nếu
business_plan_version.status =
Draft

→ Query bảng sale_wo_v2 theo
điều kiện:
• mvv =
business_plan_version.project_
code
• wo_type → mapping vào bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name = Pipeline
Type
• parent_wo_key is null
• wo_status khác cancel
→ ra được list bản ghi lấy được list
wo_key (1)
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) → query
unit_fulfilling_order theo
sale_wo_v2_id → ra được list (3)
Giá trị dropdown xổ ra như sau
• BU: từ trường du_id của list
(2) → distinct list → mapping
crm_master_data_mapping.na
me69 theo id, hiển thị theo
format tên BU - Sales (VD: BU1 Sales)
• DU: từ trường group_id của list
(3) → distinct list → mapping
groups.group_name theo
group_id, hiển thị theo format
tên DU - Delivery (DU1.12 Delivery)
Section
2:
Summa
ry

69 http://crm_master_data_mapping.name

SRS – 66

TDX Delivery – [Dashboard phase 6] PAKD

2

MM Bill

Label

Strin
g

NA

NA

NA

1. Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Sales→ Query bảng
sale_wo_v2 theo điều kiện
Từ
→ lấy ra list bản ghi → Tính
tổng của trường billable_value
2. Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Delivery→ Query
bảng sale_wo_v2 theo điều kiện
Từ
→ lấy ra list bản ghi →
Tính tổng của trường
billable_value
Chỉ tính tổng của các data được
planning trong khoảng tháng hiển
thị trên giao diện (Tham khảo
business rule)

3

Softwar
e
Produc
tion
Revenu
es

Label

Strin
g

NA

NA

NA

1. Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Sales
Software Production Revenue =
business_plan_version.exchange_r
ate*business_plan_version.softwar
e_development_fee

2. Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Delivery→ Query
bảng sale_wo_v2 theo điều kiện
Từ

→ Lấy ra list bản ghi

Software Production Revenue = T
ổng dpm_delivery.billable value x
unit_fulfilling_order.ratecatd x
sale_pipeline_v2.ratio
Chỉ tính tổng của các data được
planning trong khoảng tháng hiển
thị trên giao diện (Tham khảo
business rule)

SRS – 67

TDX Delivery – [Dashboard phase 6] PAKD

4

Deducti
on

Label

Strin
g

NA

NA

NA

Chỉ hiển thị nếu button revenue
plan filter department = Sales

Deduction =Revenue Planning (Exchange rate x total contract
price)

1. Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Sales→ Query bảng
sale_wo_v2 theo điều kiện
Từ

→ Lấy ra list bản ghi

Revenue planning= Tổng
dpm_delivery.billable value x
sale_wo_v2.unit_price x
sale_pipeline_v2.ratio
Chỉ tính tổng của các data được
planning trong khoảng tháng hiển
thị trên giao diện (Tham khảo
business rule)
5

Onsite
fee

Label

Strin
g

NA

NA

NA

Từ business_plan_version.id70 →
mapping revenue_plan_group theo
business_plan_version_id →
mapping revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue_detai
l → lấy ra các bản ghi thoả mãn
điều kiện
• revenue_type_id = id của onsite
fee
→ Ra được list bản ghi
Total revenue = Tổng của
revenue_plan_other_revenue_detai
l.value
Chỉ tính tổng của các data được
planning trong khoảng tháng hiển
thị trên giao diện (Tham khảo
business rule)

70 http://business_plan_version.id

SRS – 68

TDX Delivery – [Dashboard phase 6] PAKD

6

Revenu
es from
Equipm
ent,
Interne
t,
Server,.
..

Label

Strin
g

NA

NA

NA

Từ business_plan_version.id71 →
mapping revenue_plan_group theo
business_plan_version_id →
mapping revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue_detai
l → lấy ra các bản ghi thoả mãn
điều kiện
• revenue_type_id = id của
Equipment Revenue
→ Ra được list bản ghi
Total revenue = Tổng của
revenue_plan_other_revenue_detai
l.value
Chỉ tính tổng của các data được
planning trong khoảng tháng hiển
thị trên giao diện (Tham khảo
business rule)

7

Other
revenu
es

Label

Strin
g

NA

NA

NA

Từ business_plan_version.id →
mapping revenue_plan_group theo
business_plan_version_id →
mapping revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue_detai
l → lấy ra các bản ghi thoả mãn
điều kiện
• revenue_type_id khác id của
Onsite Fee và Equipment
Revenue
→ Ra được list bản ghi
Total revenue = Tổng của
revenue_plan_other_revenue_detai
l.value
Chỉ tính tổng của các data được
planning trong khoảng tháng hiển
thị trên giao diện (Tham khảo
business rule)

71 http://business_plan_version.id

SRS – 69

TDX Delivery – [Dashboard phase 6] PAKD

Section
3:
Softwar
e
produc
tion
revenu
e
inform
ation
filter
8

Filter

Filter

Strin
g

NA

NA

NA

Khi click vào button Filter, hiển thị
ra danh sách các trường có thể
filter (G2)
Click vào 1 trường cụ thể để filter,
hiển thị danh sách giá trị có thể
filter theo logic như sau
Position
• Lấy ra list
crm_master_data_mapping.na
me thoả mãn điều kiện sau:
• master_data_name =
Position
• is_category = 0
• User có thể search trên search
dropdown
Khi user chọn giá trị trên filter
• Nếu Button Filter Department =
Sales → Từ list (2) → lấy các
bản ghi thoả mãn
revenue_plan_sales_order.posi
tion = Giá trị user chọn
• Nếu Button Filter Department =
Delivery → Từ list (6) → lấy các
bản ghi thoả mãn
revenue_plan_sales_order.posi
tion = Giá trị user chọn

9

Go to
Billing
Plan

Hyper
link

NA

NA

NA

NA

Khi click vào, mở sang tab mới màn hình billing plan
• filter MVV = MVV của PAKD
• Duration Start Month =
business_plan_version.start_da
te
• Duration End Month =
busines_plan_version.end_date

SRS – 70

TDX Delivery – [Dashboard phase 6] PAKD

1
0

Headco
unt/
Revenu
e
switch

Butto
n bar

NA

Head
coun
t

NA

NA

Label

Strin
g

NA

NA

NA

Section
3:
Softwar
e
produc
tion
revenu
e
inform
ation
table
1
1

Avg
Unit
Price

Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Sales
Từ list (2) ( Trong phần Business
Rule) → tính trung bình cộng của
trường sale_wo_v2.unit_price
Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Delivery
Từ list (6) (Trong phần Business
Rule) → tính trung bình cộng của
trường
unit_fulfilling_order.ratecard

SRS – 71

TDX Delivery – [Dashboard phase 6] PAKD

1
2

Total
Row
Value

Label

Strin
g

NA

NA

NA

Nếu Headcount/Revenue switch =
Headcount
• Revenue Plan Filter Department
= Sales
• Từ
lấy tổng
dpm_delivery.billable_V
alue của month year
tương ứng
• Revenue Plan Filter Department
= Delivery
• Từ
lấy tổng
dpm_delivery.billable_V
alue của month year
tương ứng
Nếu Headcount/Revenue switch =
Revenue
• Revenue Plan Filter Department
= Sales
• Từ
tính Revenue
planning= Tổng
dpm_delivery.billable
value x
sale_wo_v2.unit_price x
sale_pipeline_v2.ratio
của month year tương
ứng
• Revenue Plan Filter Department
= Delivery
• Từ
tính Revenue
planning = Tổng
dpm_delivery.billable
value x
unit_fulfilling_order.rate
catd x
sale_pipeline_v2.ratio
của month year tương
ứng
Chỉ tính tổng của các data được
planning trong khoảng tháng hiển
thị trên giao diện (Tham khảo
business rule)

SRS – 72

TDX Delivery – [Dashboard phase 6] PAKD

1
3

Averag
e Price

Label

Strin
g

NA

NA

NA

Nếu Headcount/Revenue switch =
Headcount → Không hiển thị

Nếu Headcount/Revenue switch =
Revenue
• Revenue Plan Filter Department
= Sales
•
→ Average Price =
(Tổng
dpm_delivery.billable
value x
sale_wo_v2.unit_price x
sale_pipeline_v2.ratio) /
(Tổng billable_value)
• Revenue Plan Filter Department
= Sales
•
→ Average Price =
(Tổng
dpm_delivery.billable
value x
unit_fulfilling_order.rate
card x
sale_pipeline_v2.ratio) /
(Tổng billable_value)
Chỉ tính tổng của các data được
planning trong khoảng tháng hiển
thị trên giao diện (Tham khảo
business rule)
1
4

Positio
n

Label

Strin
g

NA

NA

NA

Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Sales
• Từ list (2) → lấy trường
position_id mapping vào
crm_master_data_mapping
theo id → lấy
crm_master_data_mapping.na
me để hiển thị
Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Delivery
• Từ list (6) → lấy trường
position_id mapping vào
crm_master_data_mapping
theo id → lấy

SRS – 73

TDX Delivery – [Dashboard phase 6] PAKD

crm_master_data_mapping.na
me72 để hiển thị

1
5

Unit
Price

Label

Strin
g

NA

NA

NA

Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Sales
Từ list (2) ( Trong phần Business
Rule) → trường
sale_wo_v2.unit_price
Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Delivery
Từ list (6) (Trong phần Business
Rule) → trường
unit_fulfilling_order.ratecard

1
6

Depart
ment

Label

Strin
g

NA

NA

NA

Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Sales
Từ list (1) ( Trong phần Business
Rule) → lấy trường
sale_wo_v2.du_id mapping vào
crm_master_data_mapping theo
id → lấy
crm_master_data_mapping.name73
để hiển thị
Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Delivery
Từ list (6) (Trong phần Business
Rule) → lấy trường
unit_fulfilling_order.group_id
mapping vào groups theo
group_id → lấy
groups.group_name74 để hiển thị

72 http://crm_master_data_mapping.name
73 http://crm_master_data_mapping.name
74 http://crm_master_data_mapping.name

SRS – 74

TDX Delivery – [Dashboard phase 6] PAKD

1
7

Exchan
ge Rate

Label

Strin
g

NA

NA

NA

Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Sales

Từ list (2) → lấy trường
sale_wo_v2.exchange_rate
Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Delivery
Từ list (6) → lấy trường
sale_wo_v2.exchange_rate
1
8

Pipelin
e Ratio

Label

Strin
g

NA

NA

NA

Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Sales
Từ list (2) → lấy trường
sale_wo_v2.pipeline_key mapping
sale_pipeline_v2 theo
pipeline_key → lấy trường
sale_pipeline_v2.status mapping
pipeline_status_ratio theo
pipeline_status → lấy trường ratio
hiển thị
Nếu
business_plan_version.status =
Draft và Revenue Plan Filter
Department = Delivery
Từ list (6) → lấy trường
sale_wo_v2.pipeline_key mapping
sale_pipeline_v2 theo
pipeline_key → lấy trường
sale_pipeline_v2.status mapping
pipeline_status_ratio theo
pipeline_status → lấy trường ratio
hiển thị

SRS – 75

TDX Delivery – [Dashboard phase 6] PAKD

1
9

Total

Label

Strin
g

NA

NA

NA

Nếu Headcount/Revenue switch =
Headcount
• Revenue Plan Filter Department
= Sales
• Từ
lấy tổng
dpm_delivery.billable_V
alue của row tương ứng
• Revenue Plan Filter Department
= Delivery
• Từ
lấy tổng
dpm_delivery.billable_V
alue của row tương ứng
Nếu Headcount/Revenue switch =
Revenue
• Revenue Plan Filter Department
= Sales
• Từ
tính Revenue
planning= Tổng
dpm_delivery.billable
value x
sale_wo_v2.unit_price x
sale_pipeline_v2.ratio
của row tương ứng
• Revenue Plan Filter Department
= Delivery
• Từ
tính Revenue
planning = Tổng
dpm_delivery.billable
value x
unit_fulfilling_order.rate
catd x
sale_pipeline_v2.ratio
của row tương ứng
Chỉ tính tổng của các data được
planning trong khoảng tháng hiển
thị trên giao diện (Tham khảo
business rule)

SRS – 76

TDX Delivery – [Dashboard phase 6] PAKD

2
0

Month
Value

Label

Strin
g

NA

NA

NA

Nếu Headcount/Revenue switch =
Headcount

• Revenue Plan Filter Department
= Sales
• Từ
lấy tổng
dpm_delivery.billable_V
alue của month year
tương ứng
• Revenue Plan Filter Department
= Delivery
• Từ
lấy tổng
dpm_delivery.billable_V
alue của month year
tương ứng
Nếu Headcount/Revenue switch =
Revenue
• Revenue Plan Filter Department
= Sales
• Từ
tính Revenue
planning= Tổng
dpm_delivery.billable
value x
sale_wo_v2.unit_price x
sale_pipeline_v2.ratio
của month year tương
ứng
• Revenue Plan Filter Department
= Delivery
• Từ
tính Revenue
planning = Tổng
dpm_delivery.billable
value x
unit_fulfilling_order.rate
catd x
sale_pipeline_v2.ratio
của month year tương
ứng
Section
4:
Other
Revenu
es
2
1

Collaps
e

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit Revenue
Plan

SRS – 77

TDX Delivery – [Dashboard phase 6] PAKD

2
2

Plus
Button

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit Revenue
Plan

2
3

Minus
Button

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit Revenue
Plan

2
4

Revenu
e
Categor
y

Dropd
own

Strin
g

NA

NA

NA

Từ
revenue_plan_other_revenues.reve
nue_name hiển thị

2
5

Monthl
y
Revenu
e Value

Numb
eric
field

Num
ber

NA

NA

NA

Từ
revenue_plan_other_revenues_det
ail.value

2
6

Total
Revenu
e Value

Label

Num
ber

NA

NA

NA

Từ
revenue_plan_other_revenues_det
ail.value của month year tương
ứng
Chỉ tính tổng của các data được
planning trong khoảng tháng hiển
thị trên giao diện (Tham khảo
business rule)

Section
5:
Selling
expens
es

Chỉ hiển thị nếu Button Filter
Department = Sales

2
7

Collaps
e

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit Revenue
Plan

2
8

Plus
Button

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit Revenue
Plan

2
9

Minus
Button

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit Revenue
Plan

3
0

Revenu
e
Categor
y

Dropd
own

Strin
g

NA

NA

NA

Từ
revenue_plan_other_revenues.reve
nue_name của Agency expense để
hiển thị

SRS – 78

TDX Delivery – [Dashboard phase 6] PAKD

3
1

Monthl
y
Revenu
e Value

Numb
eric
field

Num
ber

NA

NA

NA

Từ
revenue_plan_other_revenues_det
ail.value

3
2

Total
Revenu
e Value

Label

Num
ber

NA

NA

NA

Từ
revenue_plan_other_revenues_det
ail.value của month year tương
ứng
Chỉ tính tổng của các data được
planning trong khoảng tháng hiển
thị trên giao diện (Tham khảo
business rule)

4.3.4 3.4 Feature: View Revenue Plan nếu Business Plan Status khác Draft
4.3.4.1 3.4.1 Access control
Page

Activity

Permission

Role

Descripti
on

Busine
ss plan
detail

VIEW_REVENUE_PLAN_BY_M
EMBER_DU

VIEW_REVENUE_PLAN_BY_M
EMBER_DU

DB-MEMBER, DBSALE, DB-PM

Từ
users.user
_id →
mapping
group_us
er lấy ra
bản ghi có
start_date
lớn
nhất →
lấy
group_id
mapping
groups lấy
group_na
me (1)
Khi user
chọn
button
Revenue
Plan Filter
Departme
nt, user
chỉ có thể
chọn vào
giá trị = (1)

SRS – 79

TDX Delivery – [Dashboard phase 6] PAKD

Page

Activity

Permission

Role

Descripti
on

Busine
ss plan
detail

VIEW_REVENUE_PLAN_BY_M
EMBER_G

VIEW_REVENUE_PLAN_BY_M
EMBER_G

DB-PMO

Từ
users.user
_id →
mapping
group_us
er lấy ra
bản ghi có
start_date
lớn
nhất →
lấy
group_id
mapping
history_gr
oup lấy
parent_id
của bản
ghi có
start_date
lớn
nhất →
mapping
groups lấy
group_na
me (2)
Khi user
chọn
button Re
venue
Plan Filter
Departme
nt, giá trị
xổ ra →
mapping
history_gr
oup theo
group_id
lấy
parent_id
→
mapping
groups
theo
group_id
lấy
group_na
me = (2)

SRS – 80

TDX Delivery – [Dashboard phase 6] PAKD

Page

Activity

Permission

Role

Descripti
on

Busine
ss plan
detail

VIEW_REVENUE_PLAN_ALL

VIEW_REVENUE_PLAN_ALL

DB-ADMIN, DB-FC,
DB-BOM

Trên
button Re
venue
Plan Filter
Departme
nt, user có
thể chọn
tất cả giá
trị

Busine
ss plan
detail

VIEW_REVENUE_PLAN_BY_M
ANAGER_DU

VIEW_REVENUE_PLAN_BY_M
ANAGER_DU

DB-DUL, DB-BUL

Khi user
vào màn,
BE truyền
user_id
vào bảng
group_ma
nager lấy
ra các
group_id
tương
ứng →
map vào
bảng
group lấy
group_na
me (3)
Khi user
chọn
button Re
venue
Plan Filter
Departme
nt, user
chỉ có thể
chọn vào
giá trị = (3)

SRS – 81

TDX Delivery – [Dashboard phase 6] PAKD

Page

Activity

Permission

Busine
ss plan
detail

VIEW_REVENUE_PLAN_BY_M
ANAGER_G

VIEW_REVENUE_PLAN_BY_M
ANAGER_G

Role

Descripti
on
Khi user
vào màn,
BE truyền
user_id
vào bảng
group_ma
nager lấy
ra các
group_id
tương
ứng →
map vào
bảng
group lấy
group_na
me (3)
Khi user
chọn
button Re
venue
Plan Filter
Departme
nt, giá trị
xổ ra →
mapping
history_gr
oup theo
group_id
lấy
parent_id
→
mapping
groups
theo
group_id
lấy
group_na
me = (2)

4.3.4.2 3.4.2. Use case description
Use case

View Revenue Plan nếu
Business Plan Status khác Draft

Precondition
Thiết bị của user phải có kết nối internet

SRS – 82

TDX Delivery – [Dashboard phase 6] PAKD

Description
Post condition

SRS – 83

TDX Delivery – [Dashboard phase 6] PAKD

Main Flow

User: Tại màn Business Plan
List, click button View Details

System: Mở màn hình Business
Plan Detail
User: Trong section Business
Plan, chọn Tab Revenue Plan
System: Hiển thị màn hình tab
Revenue Plan

Business Rule

GUI Reference

Các ô nhập sẽ bị disable nếu status
khác draft
Section Software production
revenue information
• freeze label row đầu, freeze
column Position, Unit Price,
LDAP khi scroll ngang
• Khi user scroll trong table
Software production revenue
information, Lazy load 20
records/lần
• Cột tháng điền thông tin
headcount sẽ gen số lượng
tháng theo logic sau
• Nếu
business_plan_version.s
tatus khác Draft
• Số lượng tháng
sẽ theo
business_plan_ve
rsion.start_date
và
business_plan_ve
rsion.end_date
Logic lấy dữ liệu cho Section
Software production revenue
information
1. Nếu
business_plan_version.status
khác Draft và Revenue Plan Filter
Department = Sales
Query revenue_plan_group theo
điều kiện
• revenue_plan_group.business_
plan_version_id =
business_plan_version.id75
• revenue_plan_group.is_sale = 1
• revenue_plan_group. group_id
= Giá trị user chọn trên Revenue
Plan Filter Department
→ Ra được list (1)
Query revenue_plan_sales_order
theo điều kiện

75 http://business_plan_version.id

SRS – 84

TDX Delivery – [Dashboard phase 6] PAKD

• revenue_plan_group_id = id của
list (1)
→ Ra được list (2)

Query
revenue_plan_unit_fulfilling_order
theo điều kiện
• revenue_plan_sales_order_id =
id của list (2)
→ Ra được list (3)
Query
revenue_plan_MM_headcount theo
điều kiện
• revenue_plan_unit_fulfilling_or
der_id = id của list (3)
→ Ra được list (4)
→ Logic query dữ liệu ở trên ký
hiệu là
2. Nếu
business_plan_version.status
khác Draft và Revenue Plan Filter
Department = Delivery
Query revenue_plan_group theo
điều kiện
• revenue_plan_group.business_
plan_version_id =
business_plan_version.id76
• revenue_plan_group.is_sale = 1
→ Ra được list (5)
Query revenue_plan_sales_order
theo điều kiện
• revenue_plan_group_id = id của
list (5)
→ Ra được list (6)
Query
revenue_plan_unit_fulfilling_order
theo điều kiện
• revenue_plan_group_id = id của
list (6)
• department = Giá trị user chọn
trên Revenue Plan Filter
Department
→ Ra được list (7)
76 http://business_plan_version.id

SRS – 85

TDX Delivery – [Dashboard phase 6] PAKD

Query
revenue_plan_MM_headcount theo
điều kiện
revenue_plan_unit_fulfilling_order
_id = id của list (7)

→ Ra được list (8)
→ Logic query dữ liệu ở trên ký
hiệu là
Table Other Revenues
• Các expense revenues được
hiển thị ra từ setting_config có
• setting_config.status = 1
• type_id → mapping
setting_type có name =
Revenue Category
• Sắp xếp revenue
category trong table
Other Revenues từ trên
xuống dưới theo
setting_config.priority
tăng dần
• Cột tháng điền thông tin
revenue sẽ gen số lượng tháng
theo logic sau
• Nếu
business_plan_version.s
tatus khác Draft
• Số lượng tháng
sẽ theo
business_plan_ve
rsion.start_date
và
business_plan_ve
rsion.end_date

SRS – 86

TDX Delivery – [Dashboard phase 6] PAKD

4.3.4.3 3.4.3. Graphic User Interface

G1. Revenue Plan nếu Button Filter Department = Delivery

G2. Revenue Plan nếu Button Filter Department = Sales

4.3.4.4 3.4.4. GUI Element
#

Field

Descri
ption

Contr
ol
Type

Data
Type

Defa
ult
Value

Edita
ble?

Manda
tory?

(Y/N/
NA)

(Y/N/
NA)

Rule

Not
e

SRS – 87

TDX Delivery – [Dashboard phase 6] PAKD

Section
1:
Revenu
e Data
View
Switch
1

Button
Filter
Depart
ment

Single
select
dropd
own

Strin
g

NA

NA

NA

Nếu
business_plan_version.stat
us khác Draft
Giá trị dropdown xổ ra như
sau
• BU: từ
business_plan_version.id77
mapping
business_plan_version_g
roup theo
business_plan_version_i
d → lấy group_id của
bản ghi có is_sale = 1 →
mapping bảng
groups.group_name theo
group_id, hiển thị theo
format tên BU - Sales (VD:
BU1 - Sales)
• DU: từ
business_plan_version.id78
mapping
business_plan_version_g
roup theo
business_plan_version_i
d → lấy group_id của
bản ghi có is_sale = 0 →
mapping bảng
groups.group_name theo
group_id, hiển thị theo
format tên DU - Delivery
(DU1.12 - Delivery)

Section
2:
Summa
ry

77 http://business_plan_version.id
78 http://business_plan_version.id/

SRS – 88

TDX Delivery – [Dashboard phase 6] PAKD

2

MM Bill

Label

Strin
g

NA

NA

NA

1. Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Sales
Từ
→ Tính tổng của
trường monthly_headcount
2. Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Delivery
Từ
→ Tính tổng của
trường monthly_headcount
Chỉ tính tổng của các data
được planning trong khoảng
tháng hiển thị trên giao diện
(Tham khảo business rule)

SRS – 89

TDX Delivery – [Dashboard phase 6] PAKD

3

Softwar
e
Product
ion
Revenu
es

Label

Strin
g

NA

NA

NA

1. Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Sales
Software Production
Revenue =
business_plan_version.excha
nge_rate*business_plan_vers
ion.software_development_f
ee

2. Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Delivery→ Query bảng
sale_wo_v2 theo điều kiện
Từ
ghi

→ Lấy ra list bản

Software Production
Revenue = Tổng
revenue_plan_MM_headcoun
t.monthly_headcount x
revenue_plan_unit_fulfilling_
order.ratecard x
revenue_plan_sales_order.pi
peline_ratio
Chỉ tính tổng của các data
được planning trong khoảng
tháng hiển thị trên giao diện
(Tham khảo business rule)

SRS – 90

TDX Delivery – [Dashboard phase 6] PAKD

4

Deducti
on

Label

Strin
g

NA

NA

NA

Chỉ hiển thị nếu button
revenue plan filter
department = Sales

Deduction =Revenue
Planning - (Exchange rate x
total contract price)

1. Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Sales→ Query bảng
sale_wo_v2 theo điều kiện
Từ

→ Lấy ra list bản ghi

Revenue planning= Tổng
revenue_plan_MM_headcoun
t.monthly_headcount x
revenue_plan_sales_order.u
nit_price x
revenue_plan_sales_order.pi
peline_ratio
Chỉ tính tổng của các data
được planning trong khoảng
tháng hiển thị trên giao diện
(Tham khảo business rule)

SRS – 91

TDX Delivery – [Dashboard phase 6] PAKD

Onsite
fee

Label

5

Strin
g

NA

NA

NA

Từ
business_plan_version.id79
→ mapping
revenue_plan_group theo
business_plan_version_id →
mapping
revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue
_detail → lấy ra các bản ghi
thoả mãn điều kiện
• revenue_type_id = id của
onsite fee
→ Ra được list bản ghi
Total revenue = Tổng của
revenue_plan_other_revenue
_detail.value
Chỉ tính tổng của các data
được planning trong khoảng
tháng hiển thị trên giao diện
(Tham khảo business rule)

79 http://business_plan_version.id/

SRS – 92

TDX Delivery – [Dashboard phase 6] PAKD

6

Revenu
es from
Equipm
ent,
Internet
,
Server,..
.

Label

Strin
g

NA

NA

NA

Từ
business_plan_version.id80
→ mapping
revenue_plan_group theo
business_plan_version_id →
mapping
revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue
_detail → lấy ra các bản ghi
thoả mãn điều kiện
• revenue_type_id = id của
Equipment Revenue
→ Ra được list bản ghi
Total revenue = Tổng của
revenue_plan_other_revenue
_detail.value
Chỉ tính tổng của các data
được planning trong khoảng
tháng hiển thị trên giao diện
(Tham khảo business rule)

7

Other
revenue
s

Label

Strin
g

NA

NA

NA

Từ
business_plan_version.id81
→ mapping
revenue_plan_group theo
business_plan_version_id →
mapping
revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue
_detail → lấy ra các bản ghi
thoả mãn điều kiện
• revenue_type_id khác id
của Onsite Fee và
Equipment Revenue
→ Ra được list bản ghi
Total revenue = Tổng của
revenue_plan_other_revenue
_detail.value

80 http://business_plan_version.id/
81 http://business_plan_version.id

SRS – 93

TDX Delivery – [Dashboard phase 6] PAKD

Section
3:
Softwar
e
product
ion
revenue
informa
tion
filter
8

Filter

Filter

Strin
g

NA

NA

NA

Khi click vào button Filter,
hiển thị ra danh sách các
trường có thể filter (G2)
Click vào 1 trường cụ thể để
filter, hiển thị danh sách giá
trị có thể filter theo logic như
sau
Position
• Lấy ra list
crm_master_data_mappi
ng.name82 thoả mãn
điều kiện sau:
• master_data_nam
e = Position
• is_category = 0
• User có thể search trên
search dropdown
Khi user chọn giá trị trên
filter
• Nếu Button Filter
Department = Sales →
Từ list (2) → lấy các bản
ghi thoả mãn
revenue_plan_sales_ord
er.position = Giá trị user
chọn
• Nếu Button Filter
Department = Delivery →
Từ list (7) → lấy các bản
ghi thoả mãn
revenue_plan_sales_ord
er.position = Giá trị user
chọn

82 http://crm_master_data_mapping.name

SRS – 94

TDX Delivery – [Dashboard phase 6] PAKD

9

10

Go to
Billing
Plan

Hyper
link

NA

NA

NA

NA

Headco
unt/
Labor
cost
switch

Butto
n bar

NA

Head
count

NA

NA

Label

Strin
g

NA

NA

NA

Khi click vào, mở sang tab
mới - màn hình billing plan
• filter MVV = MVV của
PAKD
• Duration Start Month =
business_plan_version.st
art_date
• Duration End Month =
busines_plan_version.en
d_date

Section
3:
Softwar
e
product
ion
revenue
informa
tion
table
11

Avg Unit
Price

Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Sales
Từ list (2) ( Trong phần
Business Rule) → tính trung
bình cộng của trường
revenue_plan_sales_order.u
nit_price
Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Delivery
Từ list (7) (Trong phần
Business Rule) → tính trung
bình cộng của trường
revenue_plan_unit_fulfilling_
order.ratecard

SRS – 95

TDX Delivery – [Dashboard phase 6] PAKD

12

Total
Row
Value

Label

Strin
g

NA

NA

NA

Nếu Headcount/Revenue
switch = Headcount
• Revenue Plan Filter
Department = Sales
• Từ
lấy tổng
revenue_plan_MM
_headcount.mont
hly_headcount
của month year
tương ứng
• Revenue Plan Filter
Department = Delivery
• Từ
lấy
t
ổ
ng revenue_plan_
MM_headcount.m
onthly_headcount
của month year
tương ứng
Nếu Headcount/Revenue
switch = Revenue
• Revenue Plan Filter
Department = Sales
• Từ
tính
Revenue
planning= Tổng
revenue_plan_MM
_headcount.mont
hly_headcount x
revenue_plan_sal
es_order.unit_pric
e x
revenue_plan_sal
es_order.pipeline
_ratio
• Revenue Plan Filter
Department = Delivery
• Từ
tính
Revenue planning
= Tổng
revenue_plan_MM
_headcount.mont
hly_headcount x
revenue_plan_uni
t_fulfilling_order.r
atecard x
revenue_plan_sal
es_order.pipeline
_ratio

SRS – 96

TDX Delivery – [Dashboard phase 6] PAKD

Chỉ tính tổng của các data
được planning trong khoảng
tháng hiển thị trên giao diện
(Tham khảo business rule)
Average
Price

Label

Strin
g

NA

NA

NA

Nếu Headcount/Revenue
switch = Headcount → Không
hiển thị
Nếu Headcount/Revenue
switch = Revenue
• Revenue Plan Filter
Department = Sales
•
→ Average
Price =
(
revenue_plan_MM
_headcount.mont
hly_headcount x
revenue_plan_sal
es_order.unit_pric
e x
revenue_plan_sal
es_order.pipeline
_ratio) / (Tổng
revenue_plan_MM
_headcount.mont
hly_headcount)
• Revenue Plan Filter
Department = Sales
•
→ Average
Price = (Tổng
r
evenue_plan_MM
_headcount.mont
hly_headcount x
revenue_plan_uni
t_fulfilling_order.r
atecard x
revenue_plan_sal
es_order.pipeline
_ratio) / (Tổ
ng
revenue_plan_MM
_headcount.mont
hly_headcount)
Chỉ tính tổng của các data
được planning trong khoảng
tháng hiển thị trên giao diện
(Tham khảo business rule)

SRS – 97

TDX Delivery – [Dashboard phase 6] PAKD

13

Position

Label

Strin
g

NA

NA

NA

Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Sales
• Từ list (2) → lấy trường
position_id mapping vào
crm_master_data_mappi
ng theo id → lấy
crm_master_data_mappi
ng.name83 để hiển thị
Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Delivery
• Từ list (7) → lấy trường
position_id mapping vào
crm_master_data_mappi
ng theo id →
lấy
crm_master_data_mappi
ng.name84 để hiển thị

14

Unit
Price

Label

Strin
g

NA

NA

NA

Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Sales
• Từ list (2) → lấy trường
revenue_plan_sales_ord
er.unit_price
Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Delivery
• Từ list (7) → lấy trường
revenue_plan_unit_fulfill
ing_order.ratecard để
hiển thị

83 http://crm_master_data_mapping.name
84 http://crm_master_data_mapping.name/

SRS – 98

TDX Delivery – [Dashboard phase 6] PAKD

15

Depart
ment

Label

Strin
g

NA

NA

NA

Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Sales
• Từ list (2) → lấy trường
revenue_plan_group.gro
up_id → mapping vào
groups lấy group_name
để hiển thị
Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Delivery
• Từ list (7) → lấy trường
revenue_plan_unit_fulfill
ing_order.department →
mapping vào groups lấy
group_name để hiển thị

16

Pipeline
Ratio

Label

Strin
g

NA

NA

NA

Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Sales
• Từ list (2) → lấy trường
revenue_plan_sales_ord
er.pipeline_ratio để hiển
thị
Nếu
business_plan_version.stat
us khác Draft và Revenue
Plan Filter Department =
Delivery
• Từ list (7) → lấy trường
revenue_plan_sales_ord
er.pipeline_ratio để hiển
thị

SRS – 99

TDX Delivery – [Dashboard phase 6] PAKD

17

Total

Label

Strin
g

NA

NA

NA

Nếu Headcount/Revenue
switch = Headcount
• Revenue Plan Filter
Department = Sales
• Từ
lấy tổng
revenue_plan_MM
_headcount.mont
hly_headcount
của month year
tương ứng
• Revenue Plan Filter
Department = Delivery
• Từ
lấy
t
ổ
ng revenue_plan_
MM_headcount.m
onthly_headcount
của month year
tương ứng
Nếu Headcount/Revenue
switch = Revenue
• Revenue Plan Filter
Department = Sales
• Từ
tính
Revenue
planning= Tổng
revenue_plan_MM
_headcount.mont
hly_headcount x
revenue_plan_sal
es_order.unit_pric
e x
revenue_plan_sal
es_order.pipeline
_ratio
• Revenue Plan Filter
Department = Delivery
• Từ
tính
Revenue planning
= Tổng
revenue_plan_MM
_headcount.mont
hly_headcount x
revenue_plan_uni
t_fulfilling_order.r
atecard x
revenue_plan_sal
es_order.pipeline
_ratio

SRS – 100

TDX Delivery – [Dashboard phase 6] PAKD

Chỉ tính tổng của các data
được planning trong khoảng
tháng hiển thị trên giao diện
(Tham khảo business rule)

SRS – 101

TDX Delivery – [Dashboard phase 6] PAKD

18

Month
Value

Label

Strin
g

NA

NA

NA

Nếu Headcount/Revenue
switch = Headcount
• Revenue Plan Filter
Department = Sales
• Từ
lấy tổng
revenue_plan_MM
_headcount.mont
hly_headcount
của month year
tương ứng
• Revenue Plan Filter
Department = Delivery
• Từ
lấy
t
ổ
ng revenue_plan_
MM_headcount.m
onthly_headcount
của month year
tương ứng
Nếu Headcount/Revenue
switch = Revenue
• Revenue Plan Filter
Department = Sales
• Từ
tính
Revenue
planning= Tổng
revenue_plan_MM
_headcount.mont
hly_headcount x
revenue_plan_sal
es_order.unit_pric
e x
revenue_plan_sal
es_order.pipeline
_ratio
• Revenue Plan Filter
Department = Delivery
• Từ
tính
Revenue planning
= Tổng
revenue_plan_MM
_headcount.mont
hly_headcount x
revenue_plan_uni
t_fulfilling_order.r
atecard x
revenue_plan_sal
es_order.pipeline
_ratio

SRS – 102

TDX Delivery – [Dashboard phase 6] PAKD

Section
4: Other
Revenu
es
19

Collaps
e

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit
Revenue Plan

20

Plus
Button

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit
Revenue Plan

21

Minus
Button

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit
Revenue Plan

22

Revenu
e
Categor
y

Dropd
own

Strin
g

NA

NA

NA

Từ
revenue_plan_other_revenue
s.revenue_name hiển thị

23

Monthly
Revenu
e Value

Numb
eric
field

Num
ber

NA

NA

NA

Từ
revenue_plan_other_revenue
s_detail.value

24

Total
Revenu
e Value

Label

Num
ber

NA

NA

NA

Từ
revenue_plan_other_revenue
s_detail.value của month
year tương ứng
Chỉ tính tổng của các data
được planning trong khoảng
tháng hiển thị trên giao diện
(Tham khảo business rule)

Section
5:
Selling
expense
s

Chỉ hiển thị nếu Button Filter
Department = Sales

27

Collaps
e

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit
Revenue Plan

28

Plus
Button

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit
Revenue Plan

SRS – 103

TDX Delivery – [Dashboard phase 6] PAKD

29

Minus
Button

Butto
n

NA

NA

NA

NA

Tham khảo use case Edit
Revenue Plan

30

Revenu
e
Categor
y

Dropd
own

Strin
g

NA

NA

NA

Từ
revenue_plan_other_revenue
s.revenue_name của Agency
expense để hiển thị

31

Monthly
Revenu
e Value

Numb
eric
field

Num
ber

NA

NA

NA

Từ
revenue_plan_other_revenue
s_detail.value

32

Total
Revenu
e Value

Label

Num
ber

NA

NA

NA

Từ
revenue_plan_other_revenue
s_detail.value của month
year tương ứng
Chỉ tính tổng của các data
được planning trong khoảng
tháng hiển thị trên giao diện
(Tham khảo business rule)

4.3.5 3.5 Feature: Edit Revenue Plan
4.3.5.1 3.5.1 Access control
Pag
e

Activity

Permission

Role

Description

Busi
ness
plan
deta
il

EDIT_REVENUE_PLAN_AL
L_STATUS

EDIT_REVENUE_PLAN_AL
L_STATUS

DBFC

User có activity này sẽ có thể edit
Revenue Plan tại tất cả business
plan status
User không có activity này sẽ chỉ
edit Revenue Plan tại business plan
status = Draft

4.3.5.2 3.5.2. Use case description
Use case

Edit Revenue Plan

Precondition
Thiết bị của user phải có kết nối internet
Business Plan Status = Draft

SRS – 104

TDX Delivery – [Dashboard phase 6] PAKD

Description

Main Flow

Post condition

User: Tại màn hình Revenue Plan,
user edit
System: Hiển thị button Save/
Cancel
User: Ấn Save
System: Lưu thông tin

Business Rule

GUI Reference

Section Other revenues

G1

Khi ấn Save, lưu giá trị vào các
bảng sau
• revenue_plan_group
• revenue_plan_other_revenu
es
• revenue_plan_other_revenu
e_detail
Section Selling expenses (Chỉ
hiển thị nếu button filter
department = Sales)
Khi ấn Save, lưu giá trị vào các
bảng sau
• revenue_plan_group
• revenue_plan_other_revenu
es
• revenue_plan_other_revenu
e_detail

Exception Flow

User: Tại màn hình Revenue Plan,
user edit, không điền field
revenue name trong section
Other revenues
System: Hiển thị button Save/
Cancel
User: Ấn Save

Business Rule

GUI Reference

Section Other expenses
và Selling expenses

G2

• User chưa điền data trường
Revenue name mà ấn
Save → Báo lỗi Please input
required fields

System: Lưu thông tin

SRS – 105

TDX Delivery – [Dashboard phase 6] PAKD

4.3.5.3 3.5.3. Graphic User Interface

G1

G2

4.3.5.4 3.5.4. GUI Element

#

Field

Descri
ption

Contro
l Type

Data
Type

Defau
lt
Value

Editab
le?

Mandat
ory?

(Y/N/
NA)

(Y/N/
NA)

Rule

Not
e

SRS – 106

TDX Delivery – [Dashboard phase 6] PAKD

Section
1: Other
Revenue
s
1

Collapse

Button

NA

NA

NA

NA

Tham khảo use case
Edit Revenue Plan

2

Plus
Button

Button

NA

NA

NA

NA

Khi ấn vào, thêm 1
dòng cho category đó

3

Minus
Button

Button

NA

NA

NA

NA

Khi ấn vào, bớt 1 dòng
cho category đó

4

Revenue
Category

Dropd
own

String

NA

NA

NA

Từ
revenue_plan_other_r
evenues.revenue_na
me hiển thị

5

Period

Month
picker

NA

NA

NA

NA

Từ
revenue_plan_other_r
evenues_detail.month
và
revenue_plan_other_r
evenues_detail.year

6

Monthly
Revenue
Value

Numbe
ric field

Numb
er

NA

NA

NA

Từ
revenue_plan_other_r
evenues_detail.value

7

Total
Revenue
Value

Label

Numb
er

NA

NA

NA

Từ
revenue_plan_other_r
evenues_detail.value
của month year tương
ứng

Section
2: CTA
button
8

Save

Button

NA

NA

NA

NA

9

Cancel

Button

NA

NA

NA

NA

SRS – 107

TDX Delivery – [Dashboard phase 6] PAKD

4.3.6 3.6 Feature: View Business Plan tại Status = Draft

4.3.6.1 3.6.1 Access control
Page

Activity

Permission

Role

Description

Business
plan
detail

EDIT_BUSINESS_PLAN

EDIT_BUSINESS_PLAN

DB-ADMIN/
DB-SALE/DBFC

Khi
business_plan
_version.statu
s = Draft, user
có quyền này
sẽ có thể
input, edit,
upload các giá
trị của section
general
information,
business plan,
documents

Business
plan
detail

EDIT_BUSINESS_PLAN_ALL

EDIT_BUSINESS_PLAN_ALL

DB-FC

User có quyền
này sẽ có thể
input, edit,
upload tất cả
các trường
(tất cả các
trạng thái sau
khi submit
ngoại trừ
approved)

Business
plan
detail

EDIT_DOCUMENT_ALL

EDIT_DOCUMENT_ALL

DB-ADMIN/
DB-SALE/DBFC/DB-BOM

User có quyền
này sẽ có thể
UPLOAD,
DOWNLOAD
các document

SRS – 108

TDX Delivery – [Dashboard phase 6] PAKD

Page

Activity

Permission

Role

Description

Business
plan
detail

EDIT_DOCUMENT_DU

EDIT_DOCUMENT_DU

DB-DUL/DBBUL

Khi user vào
màn,
BE truyền
user_id vào
bảng
group_manag
er lấy ra các
group_id
tương ứng →
map vào bảng
group lấy
group_name
(3)
→ User có
quyền này sẽ
có thể
download các
document có
business_plan
+document.du
_name = (3)

Business
plan
detail

CREATE_NEW_VERSION

CREATE_NEW_VERSION

DB-SALE, DBADMIN

Hệ thống sẽ
hiển thị button
Create New
Version khi
user có quyền
này và
Business Plan
thoả mãn điều
kiện sau
• business_p
lan_versio
n có
version
cao nhất
đang ở
status =
APPROVED

4.3.6.2 3.6.2. Use case description
Use case

View
Business
Plan

Precondition
Thiết bị của user phải có kết nối internet
Business Plan Status = Draft

SRS – 109

TDX Delivery – [Dashboard phase 6] PAKD

Description
Post condition

User xem được thông tin business plan

SRS – 110

TDX Delivery – [Dashboard phase 6] PAKD

Main Flow

Business Rule

GUI Reference

(1) Kết quả được hiển thị theo quy chuẩn KT
Việt Nam

G1

• Dấu "," phân cách hàng nghìn
• Dấu "." phân cách thập phân
• Kết quả âm → hiển thị trong dấu ( )
• Kết quả bằng 0 → hiển thị "-"
Quy ước làm tròn:
• Lấy 2 chữ số sau dấu thập phân
• Phần được làm tròn (số thứ ba trong phần
thập phân) nhỏ hơn 5 thì làm tròn
xuống. Phần được làm tròn ở số thứ (số thứ
ba trong phần thập phân) lớn hơn hoặc
bằng 5 thì làm tròn lên
• VD: 25.583 → 25.58
(2)
• Cho phép nhập số tự nhiên và số thập phân
• Cho phép nhập max 15 chữ số nguyên (17
chữ số bao gồm 2 chữ số thập phân sau dấu
phẩy)
• Lấy sau dấu thập phân 2 chữ số (Không cho
phép user nhập quá 3 chữ số thập phân sau
dấu phẩy
• Theo quy chuẩn kế toán Việt Nam
VD:
- Nhập 20000000 → Hệ thống show
20,000,000 (Hai mươi triệu)
- Nhập 25.5 → Hệ thống show 25.5 (Hai
mươi lăm phẩy năm)
(3) Khi user view Business Plan tại status = Draft,
data của Revenue Plan và Delivery Plan sẽ được
create/update vào bảng
business_plan_version_detail (Tham khảo logic
3.6.5.)
(4) Các cột trước cột Internal sẽ được hiển thị ra
theo logic sau
Query bảng sale_wo_v2 theo điều kiện:
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng
crm_master_data_mapping, trường name =
Sale và master_data_name = Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ ra được list bản ghi lấy được list wo_key (1)
→ Query bảng sale_wo_v2 theo điều kiện:

SRS – 111

TDX Delivery – [Dashboard phase 6] PAKD

• parent_wo_key is in list (1)
• wo_status khác Cancel

→ ra được list bản ghi (2) → từ trường du_id
của list (2) → distinct list →
mapping crm_master_data_mapping.name85
theo id → lấy trường name hiển thị ra cột
(5) Các cột sau cột Internal sẽ được hiển thị theo
logic sau
Query bảng sale_wo_v2 theo điều kiện:
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng
crm_master_data_mapping, trường name =
Sale và master_data_name = Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ ra được list bản ghi lấy được list wo_key (1)
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) → query
unit_fulfilling_order theo sale_wo_v2_id → ra
được list (3) → từ trường group_id của list (3) →
distinct list → mapping groups.group_name
theo group_id → có bao nhiêu bản ghi thì sẽ
hiển thị bấy nhiêu cột

4.3.6.3 3.6.3. Graphic User Interface

85 http://crm_master_data_mapping.name/

SRS – 112

TDX Delivery – [Dashboard phase 6] PAKD

SRS – 113

TDX Delivery – [Dashboard phase 6] PAKD

G1

4.3.6.4 3.6.4. GUI Element

#

Field

Descri
ption

Contr
ol
Type

Dat
a
Typ
e

Defa
ult
Value

Edita
ble?

Manda
tory?

(Y/N/
NA)

(Y/N/
NA)

Rule

N
ot
e

Section
1:
General
Informati
on
1

Planning
Start
Date

Datep
icker

Stri
ng

NA

NA

NA

Chỉ hiển thị với
project_code_crm_information.
order_type khác T&M

2

Planning
End Date

Datep
icker

Stri
ng

NA

NA

NA

Chỉ hiển thị với
project_code_crm_information.
order_type khác T&M

3

Section
2: Unit
Price &
MM Bill

Hyper
link

NA

NA

NA

NA

Khi click vào, redirect user sang
tab Revenue Plan

SRS – 114

TDX Delivery – [Dashboard phase 6] PAKD

4

Unit
Price

Label

Nu
mbe
r

NA

NA

NA

Unit Price BU = Unit Price Total
= (Tổng dpm_delivery.billable
value x sale_wo_v2.unit_price
x sale_pipeline_v2.ratio) /
(Tổng billable_value)
Query bảng sale_wo_v2 theo
điều kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter
Department user chọn
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) →
query unit_fulfilling_order theo
sale_wo_v2_id tương ứng (3)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (2) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (2) >=
dpm_delivery.month và
year
Logic query dữ liệu ở trên ký
hiệu là

SRS – 115

TDX Delivery – [Dashboard phase 6] PAKD

•

→ Unit Price BU = (Tổng
dpm_delivery.billable value
x sale_wo_v2.unit_price x
sale_pipeline_v2.ratio) /
(Tổng billable_value)

Chỉ tính tổng của các data được
planning trong khoảng tháng
hiển thị trên giao diện (Tham
khảo business rule)
Unit Price DU = (Tổng
dpm_delivery.billable value x
unit_fulfilling_order.ratecard
x sale_pipeline_v2.ratio) /
(Tổng billable_value)
Query bảng sale_wo_v2 theo
điều kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ Ra được list (4)
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (4)
• wo_status khác Cancel
→ ra được list bản ghi (5)
Từ trường id của list (5) →
query unit_fulfilling_order
• theo sale_wo_v2_id tương
ứng của list (5)
• unit_fulfilling_order.group_i
d = giá trị user filter trên
Revenue Plan Filter
Department
→ ra được list bản ghi (6)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện

SRS – 116

TDX Delivery – [Dashboard phase 6] PAKD

• tháng của
sale_wo_v2.start_date của
list (5) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (5) >=
dpm_delivery.month và
year
Logic query dữ liệu ở trên ký
hiệu là
•

5

MM
Effort
(MM)

Label

Nu
mbe
r

NA

NA

NA

→ Average Price =
(Tổng dpm_delivery.billable
value x
unit_fulfilling_order.ratecar
d x sale_pipeline_v2.ratio) /
(Tổng billable_value)

MM Effort (MM) của DU
Từ business_plan_version.id
mapping delivery_plan theo
điều kiện sau
• theo business_version_id
tương ứng
• group_id = group_id tương
ứng
→ Ra list bản ghi → mapping
delivery_plan_member theo
delivery_plan_id của list trên →
mapping
delivery_plan_budget_MM theo
điều kiện sau
MM Effort (MM) của Total =
Tổng MM Effort (MM) của DU

SRS – 117

TDX Delivery – [Dashboard phase 6] PAKD

6

MM Bill
(MM)

Label

Nu
mbe
r

NA

NA

NA

MM Bill (MM) của BU
Query sale_wo_v2 theo điều
kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter
Department user chọn
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) →
query unit_fulfilling_order theo
sale_wo_v2_id tương ứng (3)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (2) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (2) >=
dpm_delivery.month và year
→ Lấy
dpm_delivery.billable_value →
Tính tổng
MM Bill (MM) của DU
Query sale_wo_v2 theo điều
kiện
• mvv =
business_plan_version.proje
ct_code

SRS – 118

TDX Delivery – [Dashboard phase 6] PAKD

• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ Ra được list (4)
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (4)
• wo_status khác Cancel
→ ra được list bản ghi (5)
Từ trường id của list (5) →
query unit_fulfilling_order
• theo sale_wo_v2_id tương
ứng của list (5)
• unit_fulfilling_order.group_i
d = giá trị user filter trên
Revenue Plan Filter
Department
→ ra được list bản ghi (6)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (5) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (5) >=
dpm_delivery.month và
year
→ Lấy
dpm_delivery.billable_value →
Tính tổng

SRS – 119

TDX Delivery – [Dashboard phase 6] PAKD

7

MM Bill
theo
Subservice

Label

Nu
mbe
r

NA

NA

NA

MM Bill (MM) của DU theo subservice
Query sale_wo_v2 theo điều
kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter
Department user chọn
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) →
query unit_fulfilling_order theo
sale_wo_v2_id tương ứng (3)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (2) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (2) >=
dpm_delivery.month và year
→ Lấy
dpm_delivery.billable_value →
Tính tổng
Xác định service và subservice → Query bảng
project_code_crm_informatio
n theo điều kiện

SRS – 120

TDX Delivery – [Dashboard phase 6] PAKD

• project_code =
business_plan_version.proje
ct_code
→ Ra bản ghi → lấy service và
sub_service mapping
crm_master_data_mapping
theo id → lấy trường name
MM Bill (MM) của Total theo
sub-service = Tổng MM Bill
(MM) của DU theo sub-service
8

Section
3:
Revenue
s

Label

Nu
mbe
r

NA

NA

NA

Revenues của BU = Tổng của
các giá trị thuộc section
Revenues thuộc côt BU
Revenues của DU = Tổng của
các giá trị thuộc section
Revenues thuộc cột DU
Revenues của Internal = Tổng
của các giá trị thuộc section
Revenues thuộc cột Internal
Revenues của Total = Tổng của
các giá trị thuộc section
Revenues thuộc cột Total

Khi user click vào hyperlink
section, redirect user sang tab
Revenue Plan

SRS – 121

TDX Delivery – [Dashboard phase 6] PAKD

9

Revenue
s from
work
delivered
(VND)

Label

Nu
mbe
r

NA

NA

NA

Revenues from work delivered
của BU
=
business_plan_version.exchang
e_rate*business_plan_version.s
oftware_development_fee

Revenues from work delivered
của DU
Query bảng sale_wo_v2 theo
điều kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ Ra được list (4)
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (4)
• wo_status khác Cancel
→ ra được list bản ghi (5)
Từ trường id của list (5) →
query unit_fulfilling_order
• theo sale_wo_v2_id tương
ứng của list (5)
• unit_fulfilling_order.group_i
d = giá trị user filter trên
Revenue Plan Filter
Department
→ ra được list bản ghi (6)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện

SRS – 122

TDX Delivery – [Dashboard phase 6] PAKD

• tháng của
sale_wo_v2.start_date của
list (5) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (5) >=
dpm_delivery.month và
year
Tổng dpm_delivery.billable
value x
unit_fulfilling_order.ratecatd x
sale_pipeline_v2.ratio

Revenues from work delivered
của Internal = Tổng Revenues
from work delivered của DU

Revenues from work delivered
của Total = Revenues from
work delivered của BU
+ Revenues from work
delivered của DU + Revenues
from work delivered của
Internal

SRS – 123

TDX Delivery – [Dashboard phase 6] PAKD

1
0

Deductio
n

Label

Nu
mbe
r

NA

NA

NA

Deduction của BU = Revenue
Planning - (Exchange rate x total
contract price)
Revenue Planning = Tổng
dpm_delivery.billable value x
sale_wo_v2.unit_price x
sale_pipeline_v2.ratio
Query sale_wo_v2 theo điều
kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter
Department user chọn
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) →
query unit_fulfilling_order theo
sale_wo_v2_id tương ứng (3)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (2) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (2) >=
dpm_delivery.month và year

SRS – 124

TDX Delivery – [Dashboard phase 6] PAKD

1
1

Onsite
fee

Label

Nu
mbe
r

NA

NA

NA

Từ business_plan_version.id86
→ mapping
revenue_plan_group theo
business_plan_version_id →
mapping revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue_d
etail → lấy ra các bản ghi thoả
mãn điều kiện
• revenue_type_id = id của
onsite fee
→ Ra được list bản ghi
Total revenue
=T
ổ
ng revenue_plan_other_revenue
_detail.value

1
2

Revenue
s from
Equipme
nt,
Internet,
Server,...

Label

Nu
mbe
r

NA

NA

NA

Từ business_plan_version.id87
→ mapping
revenue_plan_group theo
business_plan_version_id →
mapping revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue_d
etail → lấy ra các bản ghi thoả
mãn điều kiện
• revenue_type_id = id của
Equipment Revenue
→ Ra được list bản ghi
Total revenue
=T
ổ
ng revenue_plan_other_revenue
_detail.value

1
3

Other
revenues

Label

Nu
mbe
r

NA

NA

NA

Từ business_plan_version.id88
→ mapping
revenue_plan_group theo
business_plan_version_id →

86 http://business_plan_version.id/
87 http://business_plan_version.id/
88 http://business_plan_version.id

SRS – 125

TDX Delivery – [Dashboard phase 6] PAKD

mapping revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue_d
etail → lấy ra các bản ghi thoả
mãn điều kiện
• revenue_type_id khác id của
Onsite Fee và Equipment
Revenue
→ Ra được list bản ghi
Total revenue =
T
ổ
ng revenue_plan_other_revenue
_detail.value
1
4

Section
4: Cost of
sales

Label

Nu
mbe
r

NA

NA

NA

Cost of Sales Total = Tổng Cost
of Sales BU + Cost of Sales
Internal
Cost of Sales BU = Tổng giá trị
các trường thuộc cột BU thuộc
section Cost of Sales
Cost of Sales Internal = Tổng giá
trị các trường thuộc cột Internal
thuộc section Cost of Sales
Cost of Sales DU = Tổng giá trị
các trường thuộc cột DU thuộc
section Cost of Sales

1
5

Cost of
sales
(Ratecar
d DU)

Label

Nu
mbe
r

NA

NA

NA

Cost of Sales của BU = Tổng
Revenues from work delivered
của DU
Cost of Sales của Internal
= Tổng Revenues from work
delivered của DU

SRS – 126

TDX Delivery – [Dashboard phase 6] PAKD

1
6

Section
5: Selling
expenses

Label

Nu
mbe
r

NA

NA

NA

Selling expenses Total =
Tổng Selling expenses BU
+ Selling expenses Internal

Selling expenses BU = Tổng giá
trị các trường thuộc cột BU
thuộc section Selling expenses
Selling expenses Internal = Tổng
giá trị các trường thuộc cột
Internal thuộc section Selling
expenses
Selling expenses DU = Tổng giá
trị các trường thuộc cột DU
thuộc section Selling expenses
1
7

1
8

Incentive
s

Agency
expenses

Label

Label
/Text
field

Nu
mbe
r

NA

Nu
mbe
r

NA

NA

NA

Incentives Total = Tổng
Incentives (BU + DU + Internal)
Incentives BU = Revenues from
work delivered * Incentives Rate

NA

NA

Agency expenses Total = Tổng
Incentives (BU + DU + Internal)
Agency expense BU =
Từ business_plan_version.id89
→ mapping
revenue_plan_group theo
business_plan_version_id →
mapping revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue_d
etail → lấy ra các bản ghi thoả
mãn điều kiện
• revenue_type_id = id của
Agency expenses
→ Ra được list bản ghi
Agency
expenses
=T
ổ
ng revenue_plan_other_revenue
_detail.value

89 http://business_plan_version.id/

SRS – 127

TDX Delivery – [Dashboard phase 6] PAKD

1
9

Section
6:
Delivery
expenses

Label

Nu
mbe
r

NA

NA

NA

Delivery expenses của Total =
Tổng giá trị của các trường
thuộc section Delivery Expenses
thuộc cột Total
Delivery expenses của BU = Tổng
giá trị của các trường thuộc
section Delivery Expenses thuộc
cột BU
Delivery expenses của Internal =
Tổng giá trị của các trường
thuộc section Delivery Expenses
thuộc cột Internal
Delivery expenses của DU =
Tổng giá trị của các trường
thuộc section Delivery Expenses
thuộc cột DU

Khi user click vào hyperlink,
redirect sang tab Delivery Plan

SRS – 128

TDX Delivery – [Dashboard phase 6] PAKD

2
0

Direct
labor
cost

Label
/Text
field

Nu
mbe
r

NA

NA

NA

Direct labor cost cột DU
Công thức tính Direct labor cost
của 1 tháng

Direct labor cost của 1 tháng =
Labor rate của nhân sự có type =
In-house x MM effort của nhân
sự trong tháng đó
• Labor rate của nhân sự =
delivery_plan_member.gros
s_salary_vnd x
labor_rate_config.salary_in
dex (của country tương ứng
của user) +
labor_rate_config.expense_i
ndex (của country tương
ứng của user)
• MM effort = Từ
delivery_plan_member.id90
→ mapping
delivery_plan_budget_MM
theo delivery_plan_id → lấy
ra value theo month và year
tương ứng
Direct labor cost = Tổng Direct
labor cost của từng tháng thuộc
delivery plan của

Direct labor cost cột Total =
Tổng Direct labor cost (BU +
Internal + DU)
Direct labor cost cột Internal:
Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Direct labor cost cột BU: User
nhập

90 http://delivery_plan_member.id

SRS – 129

TDX Delivery – [Dashboard phase 6] PAKD

2
1

Outsourc
ing cost

Label
/Text
field

Nu
mbe
r

NA

NA

NA

Outsourcing cost cột DU
Công thức tính Outsourcing cost
của 1 tháng
Outsourcing Cost của 1 tháng =
Gross Salary (VND) của nhân sự
có type = Outsourced x MM
Effort của nhân sự trong tháng
đó
Outsourcing cost = Tổng
Outsourcing cost của từng
tháng thuộc delivery plan

Outsourcing cost cột Total =
Tổng Direct labor cost (BU +
Internal + DU)
Outsourcing cost cột Internal:
Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Outsourcing cost cột BU: User
nhập

SRS – 130

TDX Delivery – [Dashboard phase 6] PAKD

2
2

Equipme
nt,
Internet,
Server
cost

Label
/Text
field

Nu
mbe
r

NA

NA

NA

Equipment, Internet, Server
cost cột DU
Từ business_plan_version.id91
→
mapping delivery_plan.id92 theo
business_plan_version_id → →
mapping
delivery_plan_other_expenses
theo delivery_plan_id →
mapping
delivery_plan_other_expenses_
detail theo
delivery_plan_other_Expenses_i
d → lấy tổng
delivery_plan_other_expenses_
detail.value của tất cả bản ghi
có cost_type_id = id của
Equipment expense

Equipment, Internet, Server
cost cột Total = Tổng của (BU +
Internal + DU)
Equipment, Internet, Server
cost cột Internal: Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Equipment, Internet, Server
cost cột BU: User nhập
2
3

Onsite
expenses
(Onsite
allowanc
e,
perdiem,
travellin
g,
accomod
ation,
etc.)

Label
/Text
field

Nu
mbe
r

NA

NA

NA

Onsite expenses cost cột DU
Từ business_plan_version.id93
→
mapping delivery_plan.id94 theo
business_plan_version_id → →
mapping
delivery_plan_other_expenses
theo delivery_plan_id →
mapping
delivery_plan_other_expenses_
detail theo
delivery_plan_other_Expenses_i
d → lấy tổng
delivery_plan_other_expenses_
detail.value của tất cả bản ghi

91 http://business_plan_version.id/
92 http://delivery_plan.id/
93 http://business_plan_version.id/
94 http://delivery_plan.id/

SRS – 131

TDX Delivery – [Dashboard phase 6] PAKD

có cost_type_id = id của Onsite
expense

Onsite expenses cost cột Total
= Tổng của (BU + Internal + DU)
Onsite expenses cost cột
Internal: Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Onsite expenses cost cột BU:
User nhập
2
4

Project
bonus

Label

Nu
mbe
r

NA

NA

NA

Project bonus cost cột DU
= Project bonus/MM x MM Bill
của DU
Project bonus cost cột Total =
Tổng của (BU + Internal + DU)
Project bonus cost cột
Internal: NA
Project bonus cost cột BU: NA

SRS – 132

TDX Delivery – [Dashboard phase 6] PAKD

Overtime

Label
/Text
field

2
5

Nu
mbe
r

NA

NA

NA

Overtime cột DU
Từ business_plan_version.id95
→
mapping delivery_plan.id96 theo
business_plan_version_id → →
mapping
delivery_plan_other_expenses
theo delivery_plan_id →
mapping
delivery_plan_other_expenses_
detail theo
delivery_plan_other_Expenses_i
d → lấy tổng
delivery_plan_other_expenses_
detail.value của tất cả bản ghi
có cost_type_id = id của
Overtime

Overtime cột Total = Tổng của
(BU + Internal + DU)
Overtime cột Internal: Chỉ
quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Overtime cột BU: User nhập

95 http://business_plan_version.id/
96 http://delivery_plan.id/

SRS – 133

TDX Delivery – [Dashboard phase 6] PAKD

2
6

Nondeductib
le input
VAT

Label
/Text
field

Nu
mbe
r

NA

NA

NA

Non-deductible input VAT cột
DU:
Từ business_plan_version.id97
→ mapping delivery_plan.id98
theo
business_plan_version_id → →
mapping
delivery_plan_other_expenses
theo delivery_plan_id →
mapping
delivery_plan_other_expenses_
detail theo
delivery_plan_other_Expenses_i
d → lấy tổng value của tất cả
bản ghi có cost_type_id = id của
Non-deductible input VAT
Non-deductible input VAT cột
Total = Tổng của (BU + Internal
+ DU)
Non-deductible input VAT cột
Internal: Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Non-deductible input VAT cột
BU: User nhập

2
7

Other
expenses

Label
/Text
field

Nu
mbe
r

NA

NA

NA

Other expenses cột DU:
Từ business_plan_version.id99
→
mapping delivery_plan.id100
theo
business_plan_version_id → →
mapping
delivery_plan_other_expenses
theo delivery_plan_id →
mapping
delivery_plan_other_expenses_
detail theo
delivery_plan_other_Expenses_i
d → lấy tổng
delivery_plan_other_expenses_
detail.value của tất cả bản ghi

97 http://business_plan_version.id
98 http://delivery_plan.id
99 http://business_plan_version.id/
100 http://delivery_plan.id/

SRS – 134

TDX Delivery – [Dashboard phase 6] PAKD

có cost_type_id = id của Other
expenses

Other expenses cột Total =
Tổng của (BU + Internal + DU)
Other expenses cột Internal:
Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Other expenses cột BU: User
nhập
2
8

Section
7: Tax
expenses

Label

Nu
mbe
r

NA

NA

NA

Tax expenses cột DU: DU
Revenues * % CIT and VAT
Tax expenses cột Total = Total
Revenues * % CIT and VAT
Tax expenses cột Internal: NA
Tax expenses cột BU: BU
Revenues * % CIT and VAT

2
9

CIT and
VAT (If
any) (%)

Text
field

Nu
mbe
r

Lấy
từ
mate
r_dat
a.sett
ing
của
bản
ghi có
type
=6→
lấy
tr
ường
value

Y

NA

CIT and VAT cột Total: User
điền %
CIT and VAT cột BU: NA
CIT and VAT cột Internal: NA
CIT and VAT cột DU: NA

Section
8: Direct
Margin

SRS – 135

TDX Delivery – [Dashboard phase 6] PAKD

3
0

Direct
Margin

Label

Nu
mbe
r

NA

NA

NA

Direct Margin cột Total = Total
revenues - Total Cost of sales Total selling expenses - Total
delivery expenses - Total Tax
expenses
Direct Margin cột BU = = BU
revenues - BU - BU selling
expenses - BU delivery expenses
- BU Tax expenses
Direct Margin cột Internal =
Internal revenues - Internal Cost
of sales - Internal selling
expenses - Internal delivery
expenses - Internal Tax expenses
Direct Margin cột DU = DU
revenues - DU - DU selling
expenses - DU delivery expenses
- DU Tax expenses

3
1

Direct
Margin
before
Incentive
s and
Project
bonus

Label

Nu
mbe
r

NA

NA

NA

Direct margin before Incentives
and Project bonus (VND) Total =
Total Incentive + total project
bonus + total direct margin
Direct margin before Incentives
and Project bonus (VND) BU =
BU Incentive + BU project bonus
+ BU direct margin
Direct margin before Incentives
and Project bonus (VND)
Internal = Internal Incentive +
Internal project bonus + Internal
direct margin
Direct margin before Incentives
and Project bonus (VND) DU =
DU Incentive + DU project bonus
+ DU direct margin

SRS – 136

TDX Delivery – [Dashboard phase 6] PAKD

3
2

Allocatio
n of pool
and
unbillalb
e

Label

Nu
mbe
r

NA

NA

NA

Allocation of pool and unbillable
Total = BU allocation of pool and
unbillable + Internal allocation
of pool and unbillable + DU
allocation of pool and unbillable
Allocation of pool and unbillable
DU = DU Direct labor cost / (DU
Billable rate norm - DU direct
labor cost)
Allocation of pool and unbillable
BU: NA
Allocation of pool and unbillable
Internal: NA

3
3

Indirect
margin

Label

Nu
mbe
r

NA

NA

NA

Indirect margin Total = Total
direct margin - Total allocation
of pool and unbillable
Indirect margin BU = BU direct
margin - BU allocation of pool
and unbillable
Indirect margin Internal =
Internal direct margin - Internal
allocation of pool and unbillable
Indirect margin DU = DU direct
margin - BU allocation of pool
and unbillable

3
4

Direct
margin
%

Label

Nu
mbe
r

NA

NA

NA

Direct margin % Total = (Total
direct margin / total revenues)
x100
Direct margin % BU = (BU direct
margin / BU revenues)x100
Direct margin % Internal =
(Internal direct margin / Internal
revenues)x100
Direct margin % DU = (DU direct
margin / BU revenues)x100

SRS – 137

TDX Delivery – [Dashboard phase 6] PAKD

3
5

Direct
margin
before
Incentive
s and
Project
bonus %

Label

Nu
mbe
r

NA

NA

NA

Direct margin before Incentives
and Project bonus % Total =
(Total direct margin before
incentives and project bonus /
Total revenues) x 100
Direct margin before Incentives
and Project bonus % BU = (BU
direct margin before incentives
and project bonus / BU
revenues) x 100
Direct margin before Incentives
and Project bonus % Internal =
(Internal direct margin before
incentives and project bonus /
Internal revenues) x 100
Direct margin before Incentives
and Project bonus % DU = (DU
direct margin before incentives
and project bonus / DU
revenues) x 100

3
6

Indirect
margin
%

Label

Nu
mbe
r

NA

NA

NA

Indirect margin % Total = (Total
indirect margin / Total revenues)
x 100
Indirect margin % BU = (BU
indirect margin / BU revenues)x
100
Indirect margin % Internal =
(Internal indirect margin /
internal revenues)x 100
Indirect margin % DU = (DU
indirect margin / DU revenues)x
100

Section
9:
Referenc
e

SRS – 138

TDX Delivery – [Dashboard phase 6] PAKD

3
7

Average
delivery
expenses

Label

Nu
mbe
r

NA

NA

NA

Average delivery expenses
Total = (Total delivery
expenses / total MM effort)x 100

Average delivery expenses BU =
(BU Cost of sales (Ratecard) / BU
MM effort)x 100
Average delivery expenses
Internal NA
Average delivery expenses DU=
(DU delivery expenses / DU MM
effort)x 100
3
8

Average
direct
labor
cost/MM

Label

Nu
mbe
r

NA

NA

NA

Total = Total direct labor cost /
total MM effort
BU = BU direct labor cost / BU
MM effort
Internal NA
DU = DU direct labor cost / DU
MM effort

3
9

BIllable
rate (%)

Label

Nu
mbe
r

NA

NA

NA

Total = Total MM bill / total MM
effort
BU = BU MM bill / BU MM effort
Internal NA
DU = DU MM bill / DU MM effort

4
0

Producti
vity

Label

Nu
mbe
r

NA

NA

NA

Total = Total Revenues from
work delivered / total MM effort
BU NA
Internal NA
DU = DU Revenues from work
delivered / DU MM effort

4
1

Efficienc
y

Label

Nu
mbe
r

NA

NA

NA

Total = Total direct margin /
total MM effort
BU NA
Internal NA
DU= DU direct margin / DU MM
effort

SRS – 139

TDX Delivery – [Dashboard phase 6] PAKD

4
2

Incentive
s rate (%)

Text
field

Nu
mbe
r

NA

NA

NA

Total NA
BU:
• Default value: bảng
mater_data.setting type = 7
→ lấy trường value
• Giá trị: Lấy từ
business_plan_version_deta
il.value của detail_section =
REFERENCE và
participate_type = SALE và
label = INCENTIVES_RATE
Internal NA
DU NA

4
3

Project
bonus/
MM

Text
field

Nu
mbe
r

NA

NA

NA

Total NA
BU: NA
Internal NA
DU
• Default value: bảng
mater_data.setting type = 8
→ lấy trường value
• Giá trị: Lấy từ
business_plan_version_deta
il.value của detail_section =
REFERENCE và
participate_type =
DELIVERY_UNIT và label
= PRODUCTION_MM_BONUS

4
4

Billable
rate
norm (%)

Text
field

Nu
mbe
r

NA

NA

NA

Total NA
BU: NA
Internal NA
DU
• Default value: bảng
mater_data.setting type = 9
→ lấy trường value
• Giá trị: Lấy từ
business_plan_version_deta
il.value của detail_section =
REFERENCE và
participate_type =
DELIVERY_UNIT và label
= BILL_RATE_NORM

SRS – 140

TDX Delivery – [Dashboard phase 6] PAKD

4.3.6.5 3.6.5. Logic lưu data từ revenue plan và delivery plan bảng
business_plan_version_detail

#

Field

Section
2: Unit
Price &
MM Bill

Descri
ption

Contr
ol
Type

Hyper
link

Dat
a
Typ
e

Defa
ult
Value

NA

NA

Edita
ble?

Manda
tory?

(Y/N/
NA)

(Y/N/
NA)

NA

NA

Rule

N
ot
e

Khi click vào, redirect user sang
tab Revenue Plan

SRS – 141

TDX Delivery – [Dashboard phase 6] PAKD

1

Unit
Price

Label

Nu
mbe
r

NA

NA

NA

Unit Price BU = Unit Price Total
= (Tổng dpm_delivery.billable
value x sale_wo_v2.unit_price
x sale_pipeline_v2.ratio) /
(Tổng billable_value)
Query bảng sale_wo_v2 theo
điều kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter
Department user chọn
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) →
query unit_fulfilling_order theo
sale_wo_v2_id tương ứng (3)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (2) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (2) >=
dpm_delivery.month và
year
Logic query dữ liệu ở trên ký
hiệu là

SRS – 142

TDX Delivery – [Dashboard phase 6] PAKD

•

→ Unit Price BU = (Tổng
dpm_delivery.billable value
x sale_wo_v2.unit_price x
sale_pipeline_v2.ratio) /
(Tổng billable_value)

Chỉ tính tổng của các data được
planning trong khoảng tháng
hiển thị trên giao diện (Tham
khảo business rule)
Logic lưu data vào
business_plan_version_detail
của Total
• detail_section =
MAN_MONTH
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = UNIT_PRICE
• row_key = UNIT_PRICE
• label_text = null
• label_select_id = null
• value = (Tổng
dpm_delivery.billable
value x
sale_wo_v2.unit_price x
sale_pipeline_v2.ratio) /
(Tổng billable_value)
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section =
MAN_MONTH
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = UNIT_PRICE
• row_key = UNIT_PRICE
• label_text = null
• label_select_id = null
• value = (Tổng
dpm_delivery.billable
value x
sale_wo_v2.unit_price x
sale_pipeline_v2.ratio) /
(Tổng billable_value)

SRS – 143

TDX Delivery – [Dashboard phase 6] PAKD

Unit Price DU = (Tổng
dpm_delivery.billable value x
unit_fulfilling_order.ratecard
x sale_pipeline_v2.ratio) /
(Tổng billable_value)
Query bảng sale_wo_v2 theo
điều kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ Ra được list (4)
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (4)
• wo_status khác Cancel
→ ra được list bản ghi (5)
Từ trường id của list (5) →
query unit_fulfilling_order
• theo sale_wo_v2_id tương
ứng của list (5)
• unit_fulfilling_order.group_i
d = giá trị user filter trên
Revenue Plan Filter
Department
→ ra được list bản ghi (6)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (5) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (5) >=
dpm_delivery.month và
year

SRS – 144

TDX Delivery – [Dashboard phase 6] PAKD

Logic query dữ liệu ở trên ký
hiệu là

•

→ Average Price =
(Tổng dpm_delivery.billable
value x
unit_fulfilling_order.ratecar
d x sale_pipeline_v2.ratio) /
(Tổng billable_value)

Logic lưu data vào
business_plan_version_detail
của DU
• detail_section =
MAN_MONTH
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = UNIT_PRICE
• row_key = UNIT_PRICE
• label_text = null
• label_select_id = null
• value = (Tổng
dpm_delivery.billable
value x
unit_fulfilling_order.ratec
ard x
sale_pipeline_v2.ratio) /
(Tổng billable_value)

SRS – 145

TDX Delivery – [Dashboard phase 6] PAKD

2

MM
Effort
(MM)

Label

Nu
mbe
r

NA

NA

NA

MM Effort (MM) của DU
Từ business_plan_version.id101
mapping delivery_plan theo
điều kiện sau

• theo business_version_id
tương ứng
• group_id = group_id tương
ứng
→ Ra list bản ghi → mapping
delivery_plan_member theo
delivery_plan_id của list trên →
mapping
delivery_plan_budget_MM theo
delivery_plan_member_id →
tính tổng trường
delivery_plan_budget_MM.value
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section =
MAN_MONTH
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = MM_PRODUCTION
• row_key =
MM_PRODUCTION
• label_text = null
• label_select_id = null
• value
=
delivery_plan_budget_MM
.value
MM Effort (MM) của Total =
Tổng MM Effort (MM) của DU
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section =
MAN_MONTH
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = MM_PRODUCTION

101 http://business_plan_version.id

SRS – 146

TDX Delivery – [Dashboard phase 6] PAKD

• row_key =
MM_PRODUCTION
• label_text = null
• label_select_id = null
• value = Tổng MM Effort
(MM) của DU

SRS – 147

TDX Delivery – [Dashboard phase 6] PAKD

3

MM Bill
(MM)

Label

Nu
mbe
r

NA

NA

NA

MM Bill (MM) của BU
Query sale_wo_v2 theo điều
kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter
Department user chọn
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) →
query unit_fulfilling_order theo
sale_wo_v2_id tương ứng (3)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (2) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (2) >=
dpm_delivery.month và year
→ Lấy
dpm_delivery.billable_value →
Tính tổng
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section =
MAN_MONTH
• participate_type = SALE
• group_id = group_id của BU

SRS – 148

TDX Delivery – [Dashboard phase 6] PAKD

• label_type = MAIN_LABEL
• label = MM_BILL
• row_key = MM_BILL
• label_text = null
• label_select_id = null
• value = MM Bill của BU

MM Bill (MM) của DU
Query sale_wo_v2 theo điều
kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ Ra được list (4)
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (4)
• wo_status khác Cancel
→ ra được list bản ghi (5)
Từ trường id của list (5) →
query unit_fulfilling_order
• theo sale_wo_v2_id tương
ứng của list (5)
• unit_fulfilling_order.group_i
d = giá trị user filter trên
Revenue Plan Filter
Department
→ ra được list bản ghi (6)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (5) <=
dpm_delivery.month và year

SRS – 149

TDX Delivery – [Dashboard phase 6] PAKD

• tháng của
sale_wo_v2.end_date của
list (5) >=
dpm_delivery.month và
year
→ Lấy
dpm_delivery.billable_value →
Tính tổng
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section =
MAN_MONTH
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = MM_BILL
• row_key = MM_BILL
• label_text = null
• label_select_id = null
• value = MM Bill của DU

SRS – 150

TDX Delivery – [Dashboard phase 6] PAKD

4

MM Bill
theo
Subservice

Label

Nu
mbe
r

NA

NA

NA

MM Bill (MM) của DU theo subservice
Query sale_wo_v2 theo điều
kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter
Department user chọn
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) →
query unit_fulfilling_order theo
sale_wo_v2_id tương ứng (3)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (2) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (2) >=
dpm_delivery.month và year
→ Lấy
dpm_delivery.billable_value →
Tính tổng
Xác định service và subservice → Query bảng
project_code_crm_informatio
n theo điều kiện

SRS – 151

TDX Delivery – [Dashboard phase 6] PAKD

• project_code =
business_plan_version.proje
ct_code
→ Ra bản ghi → lấy service và
sub_service mapping
crm_master_data_mapping
theo id → lấy trường name
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section =
MAN_MONTH
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type =
SUB_LABEL_SELECT
• label = MM_BILL
• row_key = MM_BILL_1
• label_text = null
• label_select_id =
project_code_crm_informat
ion.sub_service
• value = MM Bill của DU
MM Bill (MM) của Total theo
sub-service = Tổng MM Bill
(MM) của DU theo sub-service
Logic lưu data vào
business_plan_version_detail
của Total
• detail_section =
MAN_MONTH
• participate_type = TOTAL
• group_id = 0
• label_type =
SUB_LABEL_SELECT
• label = MM_BILL
• row_key = MM_BILL_1
• label_text = null
• label_select_id =
project_code_crm_informat
ion.sub_service
• value = MM Bill của Total

SRS – 152

TDX Delivery – [Dashboard phase 6] PAKD

5

Section
3:
Revenue
s

Label

Nu
mbe
r

NA

NA

NA

Revenues của BU = Tổng của
các giá trị thuộc section
Revenues thuộc côt BU
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section = REVENUES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = REVENUES_TOTAL
• row_key =
REVENUES_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Revenues của DU = Tổng của
các giá trị thuộc section
Revenues thuộc cột DU
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section = REVENUES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = REVENUES_TOTAL
• row_key =
REVENUES_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Revenues của Internal = Tổng
của các giá trị thuộc section
Revenues thuộc cột Internal
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section = REVENUES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = REVENUES_TOTAL
• row_key =
REVENUES_TOTAL

SRS – 153

TDX Delivery – [Dashboard phase 6] PAKD

• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

Revenues của Total = Tổng của
các giá trị thuộc section
Revenues thuộc cột Total
Logic lưu data vào
business_plan_version_detail
của TOTAL
• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = REVENUES_TOTAL
• row_key =
REVENUES_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 154

TDX Delivery – [Dashboard phase 6] PAKD

6

Revenue
s from
work
delivered
(VND)

Label

Nu
mbe
r

NA

NA

NA

Revenues from work delivered
của BU
=
business_plan_version.exchang
e_rate*business_plan_version.s
oftware_development_fee
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section = REVENUES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label =
SOFTWARE_PRODUCTION_R
EVENUES
• row_key
= SOFTWARE_PRODUCTION
_REVENUES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Revenues from work delivered
của DU
Query bảng sale_wo_v2 theo
điều kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ Ra được list (4)
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (4)
• wo_status khác Cancel
→ ra được list bản ghi (5)
Từ trường id của list (5) →
query unit_fulfilling_order

SRS – 155

TDX Delivery – [Dashboard phase 6] PAKD

• theo sale_wo_v2_id tương
ứng của list (5)
• unit_fulfilling_order.group_i
d = giá trị user filter trên
Revenue Plan Filter
Department
→ ra được list bản ghi (6)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (5) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (5) >=
dpm_delivery.month và
year
Tổng dpm_delivery.billable
value x
unit_fulfilling_order.ratecatd x
sale_pipeline_v2.ratio
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section = REVENUES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label =
SOFTWARE_PRODUCTION_R
EVENUES
• row_key
= SOFTWARE_PRODUCTION
_REVENUES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Revenues from work delivered
của Internal = Tổng Revenues
from work delivered của DU
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section = REVENUES

SRS – 156

TDX Delivery – [Dashboard phase 6] PAKD

• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label =
SOFTWARE_PRODUCTION_R
EVENUES
• row_key
= SOFTWARE_PRODUCTION
_REVENUES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Revenues from work delivered
của Total = Revenues from
work delivered của BU
+ Revenues from work
delivered của DU + Revenues
from work delivered của
Internal
Logic lưu data vào
business_plan_version_detail
của TOTAL
• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label =
SOFTWARE_PRODUCTION_R
EVENUES
• row_key
= SOFTWARE_PRODUCTION
_REVENUES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 157

TDX Delivery – [Dashboard phase 6] PAKD

7

Deductio
n

Label

Nu
mbe
r

NA

NA

NA

Deduction của BU = Revenue
Planning - (Exchange rate x total
contract price)
Revenue Planning = Tổng
dpm_delivery.billable value x
sale_wo_v2.unit_price x
sale_pipeline_v2.ratio
Query sale_wo_v2 theo điều
kiện
• mvv =
business_plan_version.proje
ct_code
• wo_type → mapping vào
bảng
crm_master_data_mapping,
trường name = Sale và
master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter
Department user chọn
→ Query bảng sale_wo_v2 theo
điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) →
query unit_fulfilling_order theo
sale_wo_v2_id tương ứng (3)
Từ (3) mapping bảng
dpm_delivery theo
unit_fulfilling_order_id và theo
điều kiện
• tháng của
sale_wo_v2.start_date của
list (2) <=
dpm_delivery.month và year
• tháng của
sale_wo_v2.end_date của
list (2) >=
dpm_delivery.month và year
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section = REVENUES

SRS – 158

TDX Delivery – [Dashboard phase 6] PAKD

• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = DEDUCTION
• row_key = DEDUCTION
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL
• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DEDUCTION
• row_key = DEDUCTION
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 159

TDX Delivery – [Dashboard phase 6] PAKD

Onsite
fee

Label

8

Nu
mbe
r

NA

NA

NA

Từ business_plan_version.id102
→ mapping
revenue_plan_group theo
business_plan_version_id →
mapping revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue_d
etail → lấy ra các bản ghi thoả
mãn điều kiện
• revenue_type_id = id của
onsite fee
→ Ra được list bản ghi
Total revenue =
revenue_plan_other_revenue_d
etail.value
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section = REVENUES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = ONSITE_FEE
• row_key = ONSITE_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section = REVENUES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = ONSITE_FEE
• row_key = ONSITE_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL

102 http://business_plan_version.id/

SRS – 160

TDX Delivery – [Dashboard phase 6] PAKD

• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = ONSITE_FEE
• row_key = ONSITE_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section = REVENUES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = ONSITE_FEE
• row_key = ONSITE_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 161

TDX Delivery – [Dashboard phase 6] PAKD

Revenue
s from
Equipme
nt,
Internet,
Server,...

Label

9

Nu
mbe
r

NA

NA

NA

Từ business_plan_version.id103
→ mapping
revenue_plan_group theo
business_plan_version_id →
mapping revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue_d
etail → lấy ra các bản ghi thoả
mãn điều kiện
• revenue_type_id = id của
Equipment Revenue
→ Ra được list bản ghi
Total revenue
= revenue_plan_other_revenue_
detail.value
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section = REVENUES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = EQUIPMENT_FEE
• row_key = EQUIPMENT_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section = REVENUES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = EQUIPMENT_FEE
• row_key = EQUIPMENT_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL

103 http://business_plan_version.id/

SRS – 162

TDX Delivery – [Dashboard phase 6] PAKD

• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = EQUIPMENT_FEE
• row_key = EQUIPMENT_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section = REVENUES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = EQUIPMENT_FEE
• row_key = EQUIPMENT_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 163

TDX Delivery – [Dashboard phase 6] PAKD

Other
revenues

Label

1
0

Nu
mbe
r

NA

NA

NA

Từ business_plan_version.id104
→ mapping
revenue_plan_group theo
business_plan_version_id →
mapping revenue_plan_other
revenues theo
revenue_plan_group_id →
mapping
revenue_plan_other_revenue_d
etail → lấy ra các bản ghi thoả
mãn điều kiện
• revenue_type_id khác id của
Onsite Fee và Equipment
Revenue
→ Ra được list bản ghi
Total revenue
= revenue_plan_other_revenue_
detail.value
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section = REVENUES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = OTHER_FEE
• row_key = OTHER_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section = REVENUES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = OTHER_FEE
• row_key = OTHER_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

104 http://business_plan_version.id

SRS – 164

TDX Delivery – [Dashboard phase 6] PAKD

Logic lưu data vào
business_plan_version_detail
của TOTAL
• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OTHER_FEE
• row_key = OTHER_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section = REVENUES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OTHER_FEE
• row_key = OTHER_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 165

TDX Delivery – [Dashboard phase 6] PAKD

1
1

Section
6:
Delivery
expenses

Label

Nu
mbe
r

NA

NA

NA

Delivery expenses của Total =
Tổng giá trị của các trường
thuộc section Delivery Expenses
thuộc cột Total
Delivery expenses của BU = Tổng
giá trị của các trường thuộc
section Delivery Expenses thuộc
cột BU
Delivery expenses của Internal =
Tổng giá trị của các trường
thuộc section Delivery Expenses
thuộc cột Internal
Delivery expenses của DU =
Tổng giá trị của các trường
thuộc section Delivery Expenses
thuộc cột DU

Logic lưu data vào
business_plan_version_detail
của BU
• detail_section
= DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label =
DELIVERY_EXPENSES_TOTA
L
• row_key
= DELIVERY_EXPENSES_TOT
AL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section
= DELIVERY_EXPENSES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label
= DELIVERY_EXPENSES_TOT
AL

SRS – 166

TDX Delivery – [Dashboard phase 6] PAKD

• row_key
= DELIVERY_EXPENSES_TOT
AL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL
• detail_section
= DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= DELIVERY_EXPENSES_TOT
AL
• row_key
= DELIVERY_EXPENSES_TOT
AL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section
= DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= DELIVERY_EXPENSES_TOT
AL
• row_key
= DELIVERY_EXPENSES_TOT
AL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 167

TDX Delivery – [Dashboard phase 6] PAKD

1
2

Direct
labor
cost

Label
/Text
field

Nu
mbe
r

NA

NA

NA

Direct labor cost cột DU
Công thức tính Direct labor cost
của 1 tháng

Direct labor cost của 1 tháng =
Labor rate của nhân sự có type =
In-house x MM effort của nhân
sự trong tháng đó
• Labor rate của nhân sự =
delivery_plan_member.gros
s_salary_vnd x
labor_rate_config.salary_in
dex (của country tương ứng
của user) +
labor_rate_config.expense_i
ndex (của country tương
ứng của user)
• MM effort = Từ
delivery_plan_member.id105
→ mapping
delivery_plan_budget_MM
theo delivery_plan_id → lấy
ra value theo month và year
tương ứng
Direct labor cost = Tổng Direct
labor cost của từng tháng thuộc
delivery plan của
Direct labor cost cột Total =
Tổng Direct labor cost (BU +
Internal + DU)
Direct labor cost cột Internal:
Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Direct labor cost cột BU: User
nhập
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section
= DELIVERY_EXPENSES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL

105 http://delivery_plan_member.id

SRS – 168

TDX Delivery – [Dashboard phase 6] PAKD

• label
= DIRECT_LABOR_COST
• row_key
= DIRECT_LABOR_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL
• detail_section
= DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= DIRECT_LABOR_COST
• row_key
= DIRECT_LABOR_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section
= DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label
= DIRECT_LABOR_COST
• row_key
= DIRECT_LABOR_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section
= DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= DIRECT_LABOR_COST
• row_key
= DIRECT_LABOR_COST

SRS – 169

TDX Delivery – [Dashboard phase 6] PAKD

• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 170

TDX Delivery – [Dashboard phase 6] PAKD

1
3

Outsourc
ing cost

Label
/Text
field

Nu
mbe
r

NA

NA

NA

Outsourcing cost cột DU
Công thức tính Outsourcing cost
của 1 tháng
Outsourcing Cost của 1 tháng =
Gross Salary (VND) của nhân sự
có type = Outsourced x MM
Effort của nhân sự trong tháng
đó
Outsourcing cost = Tổng
Outsourcing cost của từng
tháng thuộc delivery plan

Outsourcing cost cột Total =
Tổng Direct labor cost (BU +
Internal + DU)
Outsourcing cost cột Internal:
Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Outsourcing cost cột BU: User
nhập
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section
= DELIVERY_EXPENSES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label =
OUTSOURCING_COST
• row_key =
OUTSOURCING_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL
• detail_section
= DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL

SRS – 171

TDX Delivery – [Dashboard phase 6] PAKD

• label
= OUTSOURCING_COST
• row_key
= OUTSOURCING_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section
= DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label
= OUTSOURCING_COST
• row_key
= OUTSOURCING_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section
= DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= OUTSOURCING_COST
• row_key
= OUTSOURCING_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
1
4

Equipme
nt,
Internet,
Server
cost

Label
/Text
field

Nu
mbe
r

NA

NA

NA

Equipment, Internet, Server
cost cột DU
Từ business_plan_version.id106
→
mapping delivery_plan.id107
theo
business_plan_version_id → →
mapping

106 http://business_plan_version.id/
107 http://delivery_plan.id/

SRS – 172

TDX Delivery – [Dashboard phase 6] PAKD

delivery_plan_other_expenses
theo delivery_plan_id → lấy
tổng
delivery_plan_other_expenses_
detail.value của tất cả bản ghi
có cost_type_id = id của
Equipment expense

Equipment, Internet, Server
cost cột Total = Tổng của (BU +
Internal + DU)
Equipment, Internet, Server
cost cột Internal: Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Equipment, Internet, Server
cost cột BU: User nhập
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section
= DELIVERY_EXPENSES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label =
EQUIPMENT_INTERNET_SER
VER_COST
• row_key
= EQUIPMENT_INTERNET_S
ERVER_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL
• detail_section
= DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= EQUIPMENT_INTERNET_S
ERVER_COST

SRS – 173

TDX Delivery – [Dashboard phase 6] PAKD

• row_key
= EQUIPMENT_INTERNET_S
ERVER_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section
= DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label
= EQUIPMENT_INTERNET_S
ERVER_COST
• row_key
= EQUIPMENT_INTERNET_S
ERVER_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section
= DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= EQUIPMENT_INTERNET_S
ERVER_COST
• row_key
= EQUIPMENT_INTERNET_S
ERVER_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 174

TDX Delivery – [Dashboard phase 6] PAKD

1
5

Onsite
expenses
(Onsite
allowanc
e,
perdiem,
travellin
g,
accomod
ation,
etc.)

Label
/Text
field

Nu
mbe
r

NA

NA

NA

Onsite expenses cost cột DU
Từ business_plan_version.id108
→
mapping delivery_plan.id109
theo
business_plan_version_id → →
mapping
delivery_plan_other_expenses
theo delivery_plan_id → lấy
tổng
delivery_plan_other_expenses_
detail.value của tất cả bản ghi
có cost_type_id = id của Onsite
expense

Onsite expenses cost cột Total
= Tổng của (BU + Internal + DU)
Onsite expenses cost cột
Internal: Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Onsite expenses cost cột BU:
User nhập
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section
= DELIVERY_EXPENSES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label =
ONSITE_DEVELOPMENT_CO
ST
• row_key
= ONSITE_DEVELOPMENT_C
OST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL

108 http://business_plan_version.id/
109 http://delivery_plan.id/

SRS – 175

TDX Delivery – [Dashboard phase 6] PAKD

• detail_section
= DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= ONSITE_DEVELOPMENT_C
OST
• row_key
= ONSITE_DEVELOPMENT_C
OST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section
= DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label
= ONSITE_DEVELOPMENT_C
OST
• row_key
= ONSITE_DEVELOPMENT_C
OST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section
= DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= ONSITE_DEVELOPMENT_C
OST
• row_key
= ONSITE_DEVELOPMENT_C
OST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 176

TDX Delivery – [Dashboard phase 6] PAKD

1
6

Project
bonus

Label

Nu
mbe
r

NA

NA

NA

Project bonus cost cột DU
= Project bonus/MM x MM Bill
của DU

Project bonus cost cột Total =
Tổng của (BU + Internal + DU)
Project bonus cost cột
Internal: NA
Project bonus cost cột BU: NA
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section
= DELIVERY_EXPENSES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = PROJECT_BONUS
• row_key = PROJECT_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL
• detail_section
= DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = PROJECT_BONUS
• row_key = PROJECT_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
1
7

Overtime

Label
/Text
field

Nu
mbe
r

NA

NA

NA

Overtime cột DU
Từ business_plan_version.id110
→
mapping delivery_plan.id111
theo
business_plan_version_id → →
mapping
delivery_plan_other_expenses
theo delivery_plan_id → lấy

110 http://business_plan_version.id/
111 http://delivery_plan.id/

SRS – 177

TDX Delivery – [Dashboard phase 6] PAKD

tổng
delivery_plan_other_expenses_
detail.value của tất cả bản ghi
có cost_type_id = id của
Overtime

Overtime cột Total = Tổng của
(BU + Internal + DU)
Overtime cột Internal: Chỉ
quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Overtime cột BU: User nhập
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section
= DELIVERY_EXPENSES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = OVERTIME
• row_key = OVERTIME
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL
• detail_section
= DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OVERTIME
• row_key = OVERTIME
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section
= DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL

SRS – 178

TDX Delivery – [Dashboard phase 6] PAKD

• label = OVERTIME
• row_key = OVERTIME
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section
= DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OVERTIME
• row_key = OVERTIME
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 179

TDX Delivery – [Dashboard phase 6] PAKD

Nondeductib
le input
VAT

Label
/Text
field

1
8

Nu
mbe
r

NA

NA

NA

Non-deductible input VAT cột
DU: Từ
business_plan_version.id112 →
mapping delivery_plan.id113
theo
business_plan_version_id → →
mapping
delivery_plan_other_expenses
theo delivery_plan_id →
mapping
delivery_plan_other_expenses_
detail theo
delivery_plan_other_Expenses_i
d → lấy tổng value của tất cả
bản ghi có cost_type_id = id của
Non-deductible input VAT
Non-deductible input VAT cột
Total = Tổng của (BU + Internal
+ DU)
Non-deductible input VAT cột
Internal: Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Non-deductible input VAT cột
BU: NA
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section
= DELIVERY_EXPENSES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label =
NON_DEDUCTION_VAT
• row_key
= NON_DEDUCTION_VAT
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL

112 http://business_plan_version.id
113 http://delivery_plan.id

SRS – 180

TDX Delivery – [Dashboard phase 6] PAKD

• detail_section
= DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= NON_DEDUCTION_VAT
• row_key
= NON_DEDUCTION_VAT
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section
= DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label
= NON_DEDUCTION_VAT
• row_key
= NON_DEDUCTION_VAT
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section
= DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= NON_DEDUCTION_VAT
• row_key
= NON_DEDUCTION_VAT
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 181

TDX Delivery – [Dashboard phase 6] PAKD

Other
expenses

Label
/Text
field

1
9

Nu
mbe
r

NA

NA

NA

Other expenses cột DU:
Từ business_plan_version.id114
→
mapping delivery_plan.id115
theo
business_plan_version_id → →
mapping
delivery_plan_other_expenses
theo delivery_plan_id → lấy
tổng
delivery_plan_other_expenses_
detail.value của tất cả bản ghi
có cost_type_id = id của Other
expenses
Other expenses cột Total =
Tổng của (BU + Internal + DU)
Other expenses cột Internal:
Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới
được nhập
Other expenses cột BU: User
nhập
Logic lưu data vào
business_plan_version_detail
của DU
• detail_section
= DELIVERY_EXPENSES
• participate_type =
DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = OTHER_EXPENSES
• row_key
= OTHER_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của TOTAL
• detail_section
= DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0

114 http://business_plan_version.id/
115 http://delivery_plan.id/

SRS – 182

TDX Delivery – [Dashboard phase 6] PAKD

• label_type = MAIN_LABEL
• label = OTHER_EXPENSES
• row_key
= OTHER_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của BU
• detail_section
= DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = OTHER_EXPENSES
• row_key
= OTHER_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào
business_plan_version_detail
của INTERNAL
• detail_section
= DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OTHER_EXPENSES
• row_key
= OTHER_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
2
0

Section
7: Tax
expenses

Label

Nu
mbe
r

NA

NA

NA

Tax expenses cột DU: DU
Revenues * % CIT and VAT
Tax expenses cột Total = Total
Revenues * % CIT and VAT
Tax expenses cột Internal: NA
Tax expenses cột BU: BU
Revenues * % CIT and VAT

SRS – 183

TDX Delivery – [Dashboard phase 6] PAKD

2
1

CIT and
VAT (If
any) (%)

Text
field

Nu
mbe
r

Lấy
từ
mate
r_dat
a.sett
ing
của
bản
ghi có
type
=6→
lấy
tr
ường
value

Y

NA

NA

NA

CIT and VAT cột Total: User
điền %
CIT and VAT cột BU: NA
CIT and VAT cột Internal: NA
CIT and VAT cột DU: NA

Section
8: Direct
Margin
Section
9:
Referenc
e
2
2

Average
delivery
expenses

Label

Nu
mbe
r

NA

Average delivery expenses
Total = (Total delivery
expenses / total MM effort)x 100
Average delivery expenses BU =
(BU Cost of sales (Ratecard) / BU
MM effort)x 100
Average delivery expenses
Internal NA
Average delivery expenses DU=
(DU delivery expenses / DU MM
effort)x 100

2
3

Average
direct
labor
cost/MM

Label

Nu
mbe
r

NA

NA

NA

Total = Total direct labor cost /
total MM effort
BU = BU direct labor cost / BU
MM effort
Internal NA
DU = DU direct labor cost / DU
MM effort

SRS – 184

TDX Delivery – [Dashboard phase 6] PAKD

2
4

BIllable
rate (%)

Label

Nu
mbe
r

NA

NA

NA

Total = Total MM bill / total MM
effort
BU = BU MM bill / BU MM effort
Internal NA

DU = DU MM bill / DU MM effort
2
5

Producti
vity

Label

Nu
mbe
r

NA

NA

NA

Total = Total Revenues from
work delivered / total MM effort
BU NA
Internal NA
DU = DU Revenues from work
delivered / DU MM effort

2
6

Efficienc
y

Label

Nu
mbe
r

NA

NA

NA

Total = Total direct margin /
total MM effort
BU NA
Internal NA
DU= DU direct margin / DU MM
effort

2
7

Incentive
s rate (%)

Text
field

Nu
mbe
r

NA

NA

NA

Total NA
BU:
• Default value: bảng
mater_data.setting type = 7
→ lấy trường value
• Giá trị: Lấy từ
business_plan_version_deta
il.value của detail_section =
REFERENCE và
participate_type = SALE và
label = INCENTIVES_RATE
Internal NA
DU NA

SRS – 185

TDX Delivery – [Dashboard phase 6] PAKD

2
8

Project
bonus/
MM

Text
field

Nu
mbe
r

NA

NA

NA

Total NA
BU: NA
Internal NA

DU
• Default value: bảng
mater_data.setting type = 8
→ lấy trường value
• Giá trị: Lấy từ
business_plan_version_deta
il.value của detail_section =
REFERENCE và
participate_type =
DELIVERY_UNIT và label
= PRODUCTION_MM_BONUS
2
9

Billable
rate
norm (%)

Text
field

Nu
mbe
r

NA

NA

NA

Total NA
BU: NA
Internal NA
DU
• Default value: bảng
mater_data.setting type = 9
→ lấy trường value
• Giá trị: Lấy từ
business_plan_version_deta
il.value của detail_section =
REFERENCE và
participate_type =
DELIVERY_UNIT và label
= BILL_RATE_NORM

SRS – 186

TDX Delivery – [Dashboard phase 6] PAKD

4.3.7 3.7 Feature: Edit Business Plan

4.3.7.1 3.7.1 Access control
Page

Activity

Permission

Role

Description

Business
plan
detail

EDIT_BUSINESS_PLAN

EDIT_BUSINESS_PLAN

DB-ADMIN/
DB-SALE/DBFC

Khi
business_plan
_version.statu
s = Draft, user
có quyền này
sẽ có thể
input, edit,
upload các giá
trị của section
general
information,
business plan,
documents

Business
plan
detail

EDIT_BUSINESS_PLAN_ALL

EDIT_BUSINESS_PLAN_ALL

DB-FC

User có quyền
này sẽ có thể
input, edit,
upload tất cả
các trường
(tất cả các
trạng thái sau
khi submit
ngoại trừ
approved)

Business
plan
detail

EDIT_DOCUMENT_ALL

EDIT_DOCUMENT_ALL

DB-ADMIN/
DB-SALE/DBFC/DB-BOM

User có quyền
này sẽ có thể
UPLOAD,
DOWNLOAD
các document

SRS – 187

TDX Delivery – [Dashboard phase 6] PAKD

Page

Activity

Permission

Role

Description

Business
plan
detail

EDIT_DOCUMENT_DU

EDIT_DOCUMENT_DU

DB-DUL/DBBUL

Khi user vào
màn,
BE truyền
user_id vào
bảng
group_manag
er lấy ra các
group_id
tương ứng →
map vào bảng
group lấy
group_name
(3)
→ User có
quyền này sẽ
có thể
download các
document có
business_plan
+document.du
_name = (3)

Business
plan
detail

CREATE_NEW_VERSION

CREATE_NEW_VERSION

DB-SALE, DBADMIN

Hệ thống sẽ
hiển thị button
Create New
Version khi
user có quyền
này và
Business Plan
thoả mãn điều
kiện sau
• business_
plan_versi
on có
version
cao nhất
đang ở
status =
APPROVED

4.3.7.2 3.7.2. Use case description
Use case

Edit Business
Plan

Precondition
Thiết bị của user phải có kết nối internet
Business Plan Status = Draft

SRS – 188

TDX Delivery – [Dashboard phase 6] PAKD

Description

Main Flow
Post condition

User edit thông tin thành công

Sau khi edit thì sẽ lưu history chỉnh sửa

Business Rule
GUI Reference

SRS – 189

TDX Delivery – [Dashboard phase 6] PAKD

4.3.7.3 3.7.3. Graphic User Interface

SRS – 190

TDX Delivery – [Dashboard phase 6] PAKD

SRS – 191

TDX Delivery – [Dashboard phase 6] PAKD

G1

4.3.7.4 3.7.4. GUI Element

#

Field

Descri
ption

Contr
ol
Type

Data
Type

Defau
lt
Value

Edita
ble?

Manda
tory?

(Y/N/
NA)

(Y/N/
NA)

NA

NA

Rule

Not
e

Section
1:
General
Informat
ion
1

Planning
Start
Date

Datepi
cker

String

NA

Chỉ hiển thị với
project_code_crm_infor
mation.order_type khác
T&M
Khi ấn save, lưu data vào
business_plan_version.pl
anning_start_date

2

Planning
End
Date

Datepi
cker

String

NA

NA

NA

Chỉ hiển thị với
project_code_crm_infor
mation.order_type khác
T&M
Khi ấn save, lưu data vào
business_plan_version.pl
anning_end_date

Section
2:
Selling
expense
s
3

Agency
expense
s (Cột
BU)

Text
field

Num
ber

NA

Y

NA

Từ
business_plan_version.id116
→ mapping
revenue_plan_group theo
business_plan_version_id
→ mapping
revenue_plan_other
revenues theo

116 http://business_plan_version.id/

SRS – 192

TDX Delivery – [Dashboard phase 6] PAKD

revenue_plan_group_id
→ mapping
revenue_plan_other_reve
nue_detail → lấy ra các
bản ghi thoả mãn điều
kiện
• revenue_type_id = id
của Agency expenses
→ Ra được list bản ghi
Agency
expenses
=T
ổ
ng revenue_plan_other_r
evenue_detail.value
Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau
Cột Total
• detail_section =
SELLING_EXPENSES
• participate_type =
TOTAL
• group_id = 0
• label_type =
MAIN_LABEL
• label =
AGENCY_EXPENSE
• row_key =
AGENCY_EXPENSE
• label_text = null
• label_select_id = null
• value = Giá trị tương
ứng
Cột BU
• detail_section =
SELLING_EXPENSES
• participate_type =
SALE
• group_id = group_id
của BU
• label_type =
MAIN_LABEL
• label =
AGENCY_EXPENSE
• row_key =
AGENCY_EXPENSE

SRS – 193

TDX Delivery – [Dashboard phase 6] PAKD

• label_text = null
• label_select_id = null
• value = Giá trị tương
ứng
Section
3:
Delivery
expense
s
4

Direct
labor
cost (Cột
Internal)

Text
field

Num
ber

NA

Y

NA

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau
Cột Internal
• detail_section =
DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type =
MAIN_LABEL
• label =
DIRECT_LABOR_COS
T
• row_key =
DIRECT_LABOR_COS
T
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại Direct labor
cost (Cột Internal)

SRS – 194

TDX Delivery – [Dashboard phase 6] PAKD

5

Outsour
cing
cost (Cột
Internal)

Text
field

Num
ber

NA

Y

NA

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau

Cột Internal
• detail_section =
DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type =
MAIN_LABEL
• label =
OUTSOURCING_COST
• row_key
= OUTSOURCING_CO
ST
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại Outsourcing
cost (Cột Internal)
6

Equipme
nt,
Internet,
Server
cost (Cột
Internal)

Text
field

Num
ber

NA

Y

NA

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau
Cột Internal
• detail_section =
DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type =
MAIN_LABEL
• label
= EQUIPMENT_INTER
NET_SERVER_COST
• row_key
= EQUIPMENT_INTER
NET_SERVER_COST
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại Equipment,
Internet, Server
cost (Cột Internal)

SRS – 195

TDX Delivery – [Dashboard phase 6] PAKD

7

8

Onsite
expense
s (Onsite
allowan
ce,
perdiem
,
travellin
g,
accomo
dation,
etc.) (C
ột
Internal)

Text
field

Overtim
e (Cột
Internal)

Text
field

Num
ber

NA

Y

NA

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau
Cột Internal
• detail_section =
DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type =
MAIN_LABEL
• label
= ONSITE_DEVELOPM
ENT_COST
• row_key
= ONSITE_DEVELOPM
ENT_COST
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại Onsite
expenses (Onsite
allowance, perdiem,
travelling,
accomodation,
etc.) (Cột Internal)

Num
ber

NA

Y

NA

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau
Cột Internal
• detail_section =
DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type =
MAIN_LABEL
• label = OVERTIME
• row_key = OVERTIME
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập
tạiOvertime (Cột
Internal)

SRS – 196

TDX Delivery – [Dashboard phase 6] PAKD

9

Nondeductib
le input
VAT (Cột
Internal)

Text
field

Num
ber

NA

Y

NA

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau
Cột Internal
• detail_section =
DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type =
MAIN_LABEL
• label
= NON_DEDUCTION_
VAT
• row_key
= NON_DEDUCTION_
VAT
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại Nondeductible input
VAT (Cột Internal)

10

Other
expense
s (Cột
Internal)

Text
field

Num
ber

NA

Y

NA

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau
Cột Internal
• detail_section =
DELIVERY_EXPENSES
• participate_type =
INTERNAL
• group_id = 0
• label_type =
MAIN_LABEL
• label
= OTHER_EXPENSES
• row_key
= OTHER_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại Other
expenses (Cột
Internal)

SRS – 197

TDX Delivery – [Dashboard phase 6] PAKD

Section
4: Tax
expense
s

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau

Cột Total
• detail_section = TAX
• participate_type =
TOTAL
• group_id = 0
• label_type =
MAIN_LABEL
• label = TAX_TOTAL
• row_key
= TAX_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại Tax
expenses (Cột Total)
Cột BU
• detail_section = TAX
• participate_type =
SALE
• group_id = group_id
của BU
• label_type =
MAIN_LABEL
• label = TAX_TOTAL
• row_key
= TAX_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại Tax
expenses (Cột Total)
Cột DU
• detail_section = TAX
• participate_type =
DELIVERY_UNIT
• group_id = group_id
của DU
• label_type =
MAIN_LABEL
• label = TAX_TOTAL
• row_key
= TAX_TOTAL
• label_text = null
• label_select_id = null

SRS – 198

TDX Delivery – [Dashboard phase 6] PAKD

• value = Giá trị user
nhập tại Tax
expenses (Cột DU)

11

CIT and
VAT (If
any)
(%) (Cột
Total)

Text
field

Num
ber

NA

Y

NA

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau
Cột Total
• detail_section = TAX
• participate_type =
TOTAL
• group_id = 0
• label_type =
MAIN_LABEL
• label = PIC_CIT
• row_key = PIC_CIT
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại CIT and VAT
(If any) (%) (Cột Total)

Section
5:
Referenc
e

SRS – 199

TDX Delivery – [Dashboard phase 6] PAKD

12

Incentiv
es rate
(%) (Cột
BU)

Text
field

Num
ber

NA

Y

NA

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau

Cột Total
• detail_section
= REFERENCE
• participate_type
= SALE
• group_id = group_id
của BU
• label_type =
MAIN_LABEL
• label
= INCENTIVES_RATE
• row_key
= INCENTIVES_RATE
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại Incentives
rate (%) (Cột BU)
13

Project
bonus/
MM (Cột
DU)

Text
field

Num
ber

NA

Y

NA

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau
Cột DU
• detail_section
= REFERENCE
• participate_type
= DELIVERY_UNIT
• group_id = group_id
của DU
• label_type =
MAIN_LABEL
• label
= PRODUCTION_MM_
BONUS
• row_key
= PRODUCTION_MM_
BONUS
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại Project
bonus/MM (Cột DU)

SRS – 200

TDX Delivery – [Dashboard phase 6] PAKD

14

Billable
rate
norm
(%) (Cột
DU)

Text
field

Num
ber

NA

Y

NA

Khi user ấn Save, lưu giá
trị vào
business_plan_version_d
etail theo logic sau
Cột DU
• detail_section
= REFERENCE
• participate_type
= DELIVERY_UNIT
• group_id = group_id
của DU
• label_type =
MAIN_LABEL
• label
= BILL_RATE_NORM
• row_key
= BILL_RATE_NORM
• label_text = null
• label_select_id = null
• value = Giá trị user
nhập tại Billable rate
norm (%) (Cột DU)

4.3.8 3.8 Feature: Submit Business Plan
4.3.8.1 3.8.1 Access control
4.3.8.2 3.8.2 Use case description
Use case

Create new
version

Precondition
Thiết bị của user phải có kết nối internet
Business Plan Status = Draft

Description

Post condition
User edit thông tin thành công
Sau khi edit thì sẽ lưu history chỉnh sửa

SRS – 201

TDX Delivery – [Dashboard phase 6] PAKD

Main Flow

Business Rule

GUI Reference

Khi submit, hệ thống sẽ baseline data vào bảng
baselline
Business plan: Baseline vào các bảng sau

business_plan_version_detail
business_plan_version_group
business_plan_location_exchange_rate
business_plan_labor_rate
Revenue plan: Baseline vào các bảng sau
• revenue_plan_group
• revenue_plan_other_revenues
• revenue_plan_other_revenue_detail
• revenue_plan_sales_order
• revenue_plan_unit_fulfilling_order
• revenue_plan_MM_headcount
Chỉ có user có tên trong phần AM information
được quyền ấn nút Submit (Từ
business_plan_version.id117 → mapping bảng
business_plan_collaborator → query theo
member_type = AM → Chỉ list user_id query ra
được phép ấn nút submit hoặc user có
permission SUBMIT_BUSINESS_PLAN trong
POA
Khi PAKD đang trong trạng thái chờ phê duyệt,
user không thể edit PAKD, trừ khi user có
activity EDIT_BUSINESS_PLAN_ALL
Khi user ấn nút submit, hệ thống tạo bản ghi
mới trong bảng approval_process. Trường
form_data chỉ nhận thông tin của WO có status
khác cancel
từ approval_process.workflow_id → mapping
bảng workflow và bảng step để tìm các step có
trong PAKD → tạo các bản ghi trong bảng
approval_step
từ approval_process.current_step →
mapping bảng step và tìm được assign_type và
assign_value → tạo bản ghi tương ứng trong
bảng approval_person
• nếu assign_type = Reporter → tạo bản ghi
trong bảng approval_person cho AM của
PAKD

117 http://business_plan_version.id

SRS – 202

TDX Delivery – [Dashboard phase 6] PAKD

• nếu assign_type = Department → Từ
approval_process.form_data → truyền vào
api của POA → POA trả response → tạo bản
ghi trong bảng approval_person cho
manager của các DU
• nếu assign_type = Division → Từ
approval_process.form_data → truyền vào
api của POA → POA trả response → tạo bản
ghi trong bảng approval_person cho
manager của các G
• nếu assign_type = Specific user → Từ thông
tin của assign_value → tạo bản ghi trong
bảng approval_person cho assign_value
tương ứng
• nếu assign_type = All user in group → từ giá
trị của assign_value → mapping bảng
workflow_group → mapping bảng
workflow_group_member → tạo bản ghi
trong bảng approval_person cho các
member trong group

4.3.8.3 3.8.3. Graphic User Interface
4.3.8.4 3.8.4. Logic
3.8.4.1. Business Plan
Bảng business_plan_version_detail
#

Field

Section
2: Unit
Price &
MM Bill

De
sc
rip
tio
n

Co
ntr
ol
Ty
pe

Hy
per
lin
k

Da
ta
Ty
pe

NA

Defaul
t
Value

NA

Ed
it
ab
le
?

Ma
nd
at
or
y?

(Y
/
N/
N
A)

(Y/
N/
NA
)

N
A

NA

Rule

N
o
t
e

Khi click vào, redirect user sang tab Revenue
Plan

SRS – 203

TDX Delivery – [Dashboard phase 6] PAKD

1

Unit
Price

La
bel

Nu
m
be
r

NA

N
A

NA

Unit Price BU = Unit Price Total = (Tổng
dpm_delivery.billable value x
sale_wo_v2.unit_price x
sale_pipeline_v2.ratio) / (Tổng billable_value)

Query bảng sale_wo_v2 theo điều kiện
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng
crm_master_data_mapping, trường name =
Sale và master_data_name = Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter Department user
chọn
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) → query
unit_fulfilling_order theo sale_wo_v2_id tương
ứng (3)
Từ (3) mapping bảng dpm_delivery theo
unit_fulfilling_order_id và theo điều kiện
• tháng của sale_wo_v2.start_date của list (2)
<= dpm_delivery.month và year
• tháng của sale_wo_v2.end_date của list (2)
>= dpm_delivery.month và year
Logic query dữ liệu ở trên ký hiệu là
•

→ Unit Price BU = (Tổng
dpm_delivery.billable value x
sale_wo_v2.unit_price x
sale_pipeline_v2.ratio) / (Tổng
billable_value)

Chỉ tính tổng của các data được planning trong
khoảng tháng hiển thị trên giao diện (Tham
khảo business rule)
Logic lưu data vào business_plan_version_detail
của Total
• detail_section = MAN_MONTH
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = UNIT_PRICE
• row_key = UNIT_PRICE
• label_text = null
• label_select_id = null

SRS – 204

TDX Delivery – [Dashboard phase 6] PAKD

• value = (Tổng dpm_delivery.billable value
x sale_wo_v2.unit_price x
sale_pipeline_v2.ratio) / (Tổng
billable_value)

Logic lưu data vào business_plan_version_detail
của BU
• detail_section = MAN_MONTH
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = UNIT_PRICE
• row_key = UNIT_PRICE
• label_text = null
• label_select_id = null
• value = (Tổng dpm_delivery.billable value
x sale_wo_v2.unit_price x
sale_pipeline_v2.ratio) / (Tổng
billable_value)

Unit Price DU = (Tổng dpm_delivery.billable
value x unit_fulfilling_order.ratecard x
sale_pipeline_v2.ratio) / (Tổng billable_value)
Query bảng sale_wo_v2 theo điều kiện
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng
crm_master_data_mapping, trường name =
Sale và master_data_name = Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ Ra được list (4)
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (4)
• wo_status khác Cancel
→ ra được list bản ghi (5)
Từ trường id của list (5) → query
unit_fulfilling_order
• theo sale_wo_v2_id tương ứng của list (5)
• unit_fulfilling_order.group_id = giá trị user
filter trên Revenue Plan Filter Department
→ ra được list bản ghi (6)
Từ (3) mapping bảng dpm_delivery theo
unit_fulfilling_order_id và theo điều kiện
• tháng của sale_wo_v2.start_date của list (5)
<= dpm_delivery.month và year

SRS – 205

TDX Delivery – [Dashboard phase 6] PAKD

• tháng của sale_wo_v2.end_date của list (5)
>= dpm_delivery.month và year
Logic query dữ liệu ở trên ký hiệu là

•

→ Average Price = (Tổng
dpm_delivery.billable value x
unit_fulfilling_order.ratecard x
sale_pipeline_v2.ratio) / (Tổng
billable_value)

Logic lưu data vào business_plan_version_detail
của DU
• detail_section = MAN_MONTH
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = UNIT_PRICE
• row_key = UNIT_PRICE
• label_text = null
• label_select_id = null
• value = (Tổng dpm_delivery.billable value
x unit_fulfilling_order.ratecard x
sale_pipeline_v2.ratio) / (Tổng
billable_value)

SRS – 206

TDX Delivery – [Dashboard phase 6] PAKD

2

MM
Effort
(MM)

La
bel

Nu
m
be
r

NA

N
A

NA

MM Effort (MM) của DU
Từ business_plan_version.id118 mapping
delivery_plan theo điều kiện sau
• theo business_version_id tương ứng
• group_id = group_id tương ứng

→ Ra list bản ghi → mapping
delivery_plan_member theo delivery_plan_id
của list trên → mapping
delivery_plan_budget_MM theo
delivery_plan_member_id → tính tổng trường
delivery_plan_budget_MM.value
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = MAN_MONTH
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = MM_PRODUCTION
• row_key = MM_PRODUCTION
• label_text = null
• label_select_id = null
• value = delivery_plan_budget_MM.value
MM Effort (MM) của Total = Tổng MM Effort
(MM) của DU
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = MAN_MONTH
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = MM_PRODUCTION
• row_key = MM_PRODUCTION
• label_text = null
• label_select_id = null
• value = Tổng MM Effort (MM) của DU

118 http://business_plan_version.id/

SRS – 207

TDX Delivery – [Dashboard phase 6] PAKD

3

MM Bill
(MM)

La
bel

Nu
m
be
r

NA

N
A

NA

MM Bill (MM) của BU
Query sale_wo_v2 theo điều kiện
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng
crm_master_data_mapping, trường name =
Sale và master_data_name = Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter Department user
chọn
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) → query
unit_fulfilling_order theo sale_wo_v2_id tương
ứng (3)
Từ (3) mapping bảng dpm_delivery theo
unit_fulfilling_order_id và theo điều kiện
• tháng của sale_wo_v2.start_date của list (2)
<= dpm_delivery.month và year
• tháng của sale_wo_v2.end_date của list (2)
>= dpm_delivery.month và year
→ Lấy dpm_delivery.billable_value → Tính tổng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = MAN_MONTH
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = MM_BILL
• row_key = MM_BILL
• label_text = null
• label_select_id = null
• value = MM Bill của BU

MM Bill (MM) của DU
Query sale_wo_v2 theo điều kiện
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng
crm_master_data_mapping, trường name =
Sale và master_data_name = Pipeline Type
• parent_wo_key is null
• wo_status khác cancel

SRS – 208

TDX Delivery – [Dashboard phase 6] PAKD

→ Ra được list (4)
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (4)
• wo_status khác Cancel

→ ra được list bản ghi (5)
Từ trường id của list (5) → query
unit_fulfilling_order
• theo sale_wo_v2_id tương ứng của list (5)
• unit_fulfilling_order.group_id = giá trị user
filter trên Revenue Plan Filter Department
→ ra được list bản ghi (6)
Từ (3) mapping bảng dpm_delivery theo
unit_fulfilling_order_id và theo điều kiện
• tháng của sale_wo_v2.start_date của list (5)
<= dpm_delivery.month và year
• tháng của sale_wo_v2.end_date của list (5)
>= dpm_delivery.month và year
→ Lấy dpm_delivery.billable_value → Tính tổng
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = MAN_MONTH
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = MM_BILL
• row_key = MM_BILL
• label_text = null
• label_select_id = null
• value = MM Bill của DU

SRS – 209

TDX Delivery – [Dashboard phase 6] PAKD

4

MM Bill
theo
Subservice

La
bel

Nu
m
be
r

NA

N
A

NA

MM Bill (MM) của DU theo sub-service
Query sale_wo_v2 theo điều kiện
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng
crm_master_data_mapping, trường name =
Sale và master_data_name = Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter Department user
chọn
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) → query
unit_fulfilling_order theo sale_wo_v2_id tương
ứng (3)
Từ (3) mapping bảng dpm_delivery theo
unit_fulfilling_order_id và theo điều kiện
• tháng của sale_wo_v2.start_date của list (2)
<= dpm_delivery.month và year
• tháng của sale_wo_v2.end_date của list (2)
>= dpm_delivery.month và year
→ Lấy dpm_delivery.billable_value → Tính tổng
Xác định service và sub-service → Query bảng
project_code_crm_information theo điều kiện
• project_code =
business_plan_version.project_code
→ Ra bản ghi → lấy service và sub_service
mapping crm_master_data_mapping theo id →
lấy trường name
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = MAN_MONTH
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = SUB_LABEL_SELECT
• label = MM_BILL
• row_key = MM_BILL_1
• label_text = null
• label_select_id =
project_code_crm_information.sub_service
• value = MM Bill của DU

SRS – 210

TDX Delivery – [Dashboard phase 6] PAKD

MM Bill (MM) của Total theo sub-service =
Tổng MM Bill (MM) của DU theo sub-service

Logic lưu data vào business_plan_version_detail
của Total
• detail_section = MAN_MONTH
• participate_type = TOTAL
• group_id = 0
• label_type = SUB_LABEL_SELECT
• label = MM_BILL
• row_key = MM_BILL_1
• label_text = null
• label_select_id =
project_code_crm_information.sub_service
• value = MM Bill của Total

SRS – 211

TDX Delivery – [Dashboard phase 6] PAKD

5

Section
3:
Revenue
s

La
bel

Nu
m
be
r

NA

N
A

NA

Revenues của BU = Tổng của các giá trị
thuộc section Revenues thuộc côt BU
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REVENUES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = REVENUES_TOTAL
• row_key = REVENUES_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Revenues của DU = Tổng của các giá trị thuộc
section Revenues thuộc cột DU
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REVENUES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = REVENUES_TOTAL
• row_key = REVENUES_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Revenues của Internal = Tổng của các giá trị
thuộc section Revenues thuộc cột Internal
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = REVENUES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = REVENUES_TOTAL
• row_key = REVENUES_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Revenues của Total = Tổng của các giá trị
thuộc section Revenues thuộc cột Total
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0

SRS – 212

TDX Delivery – [Dashboard phase 6] PAKD

• label_type = MAIN_LABEL
• label = REVENUES_TOTAL
• row_key = REVENUES_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 213

TDX Delivery – [Dashboard phase 6] PAKD

6

Revenue
s from
work
delivered
(VND)

La
bel

Nu
m
be
r

NA

N
A

NA

Revenues from work delivered của
BU =
business_plan_version.exchange_rate*business
_plan_version.software_development_fee
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REVENUES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label =
SOFTWARE_PRODUCTION_REVENUES
• row_key
= SOFTWARE_PRODUCTION_REVENUES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Revenues from work delivered của DU
Query bảng sale_wo_v2 theo điều kiện
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng
crm_master_data_mapping, trường name =
Sale và master_data_name = Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ Ra được list (4)
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (4)
• wo_status khác Cancel
→ ra được list bản ghi (5)
Từ trường id của list (5) → query
unit_fulfilling_order
• theo sale_wo_v2_id tương ứng của list (5)
• unit_fulfilling_order.group_id = giá trị user
filter trên Revenue Plan Filter Department
→ ra được list bản ghi (6)
Từ (3) mapping bảng dpm_delivery theo
unit_fulfilling_order_id và theo điều kiện
• tháng của sale_wo_v2.start_date của list (5)
<= dpm_delivery.month và year
• tháng của sale_wo_v2.end_date của list (5)
>= dpm_delivery.month và year

SRS – 214

TDX Delivery – [Dashboard phase 6] PAKD

Tổng dpm_delivery.billable value x
unit_fulfilling_order.ratecatd x
sale_pipeline_v2.ratio

Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REVENUES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label =
SOFTWARE_PRODUCTION_REVENUES
• row_key
= SOFTWARE_PRODUCTION_REVENUES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Revenues from work delivered của Internal =
Tổng Revenues from work delivered của DU
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REVENUES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label =
SOFTWARE_PRODUCTION_REVENUES
• row_key
= SOFTWARE_PRODUCTION_REVENUES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Revenues from work delivered của Total
= Revenues from work delivered của BU
+ Revenues from work delivered của DU
+ Revenues from work delivered của Internal
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label =
SOFTWARE_PRODUCTION_REVENUES
• row_key
= SOFTWARE_PRODUCTION_REVENUES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 215

TDX Delivery – [Dashboard phase 6] PAKD

7

Deductio
n

La
bel

Nu
m
be
r

NA

N
A

NA

Deduction của BU = Revenue Planning (Exchange rate x total contract price)
Revenue Planning = Tổng dpm_delivery.billable
value x sale_wo_v2.unit_price x
sale_pipeline_v2.ratio
Query sale_wo_v2 theo điều kiện
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng
crm_master_data_mapping, trường name =
Sale và master_data_name = Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter Department user
chọn
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) → query
unit_fulfilling_order theo sale_wo_v2_id tương
ứng (3)
Từ (3) mapping bảng dpm_delivery theo
unit_fulfilling_order_id và theo điều kiện
• tháng của sale_wo_v2.start_date của list (2)
<= dpm_delivery.month và year
• tháng của sale_wo_v2.end_date của list (2)
>= dpm_delivery.month và year
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REVENUES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = DEDUCTION
• row_key = DEDUCTION
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DEDUCTION

SRS – 216

TDX Delivery – [Dashboard phase 6] PAKD

• row_key = DEDUCTION
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 217

TDX Delivery – [Dashboard phase 6] PAKD

Onsite
fee

La
bel

Nu
m
be
r

8

NA

N
A

NA

Từ business_plan_version.id119 → mapping
revenue_plan_group theo
business_plan_version_id → mapping
revenue_plan_other revenues theo
revenue_plan_group_id → mapping
revenue_plan_other_revenue_detail → lấy ra
các bản ghi thoả mãn điều kiện
• revenue_type_id = id của onsite fee
→ Ra được list bản ghi
Total revenue
= revenue_plan_other_revenue_detail.value
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REVENUES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = ONSITE_FEE
• row_key = ONSITE_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REVENUES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = ONSITE_FEE
• row_key = ONSITE_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = ONSITE_FEE
• row_key = ONSITE_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

119 http://business_plan_version.id/

SRS – 218

TDX Delivery – [Dashboard phase 6] PAKD

Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = REVENUES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = ONSITE_FEE
• row_key = ONSITE_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 219

TDX Delivery – [Dashboard phase 6] PAKD

Revenue
s from
Equipme
nt,
Internet,
Server,...

La
bel

Nu
m
be
r

9

NA

N
A

NA

Từ business_plan_version.id120 → mapping
revenue_plan_group theo
business_plan_version_id → mapping
revenue_plan_other revenues theo
revenue_plan_group_id → mapping
revenue_plan_other_revenue_detail → lấy ra
các bản ghi thoả mãn điều kiện
• revenue_type_id = id của Equipment
Revenue
→ Ra được list bản ghi
Total revenue
= revenue_plan_other_revenue_detail.value
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REVENUES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = EQUIPMENT_FEE
• row_key = EQUIPMENT_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REVENUES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = EQUIPMENT_FEE
• row_key = EQUIPMENT_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = EQUIPMENT_FEE
• row_key = EQUIPMENT_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

120 http://business_plan_version.id/

SRS – 220

TDX Delivery – [Dashboard phase 6] PAKD

Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = REVENUES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = EQUIPMENT_FEE
• row_key = EQUIPMENT_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 221

TDX Delivery – [Dashboard phase 6] PAKD

Other
revenues

La
bel

Nu
m
be
r

1
0

NA

N
A

NA

Từ business_plan_version.id121 → mapping
revenue_plan_group theo
business_plan_version_id → mapping
revenue_plan_other revenues theo
revenue_plan_group_id → mapping
revenue_plan_other_revenue_detail → lấy ra
các bản ghi thoả mãn điều kiện
• revenue_type_id khác id của Onsite Fee và
Equipment Revenue
→ Ra được list bản ghi
Total revenue
= revenue_plan_other_revenue_detail.value
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REVENUES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = OTHER_FEE
• row_key = OTHER_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REVENUES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = OTHER_FEE
• row_key = OTHER_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REVENUES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OTHER_FEE
• row_key = OTHER_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

121 http://business_plan_version.id/

SRS – 222

TDX Delivery – [Dashboard phase 6] PAKD

Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = REVENUES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OTHER_FEE
• row_key = OTHER_FEE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 223

TDX Delivery – [Dashboard phase 6] PAKD

Section:
Cost of
Sales

Cột Total = BU Cost of Sales + Internal Cost of
Sales
Cột BU = Tổng giá trị của các trường thuộc
section Cost Of Sales trong cột BU
Cột Internal = Tổng giá trị của các trường thuộc
section Cost Of Sales trong cột Internal
Cột DU = Tổng giá trị của các trường thuộc
section Cost Of Sales trong cột DU
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = COST_PRICE
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = COST_PRICE_TOTAL
• row_key = COST_PRICE_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = COST_PRICE
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = COST_PRICE_TOTAL
• row_key = COST_PRICE_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = COST_PRICE
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = COST_PRICE_TOTAL
• row_key = COST_PRICE_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = COST_PRICE
• participate_type = INTERNAL
• group_id = 0

SRS – 224

TDX Delivery – [Dashboard phase 6] PAKD

• label_type = MAIN_LABEL
• label = COST_PRICE_TOTAL
• row_key = COST_PRICE_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Cost of
sales
(Ratecar
d DU)

Cột Total: NA
Cột BU = Tổng Revenues from work delivered
của các DU
Cột Internal = Revenues from work delivered của
Internal
Cột DU: NA
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = COST_PRICE
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = COST_OF_DU_SOLD
• row_key = COST_OF_DU_SOLD
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = COST_PRICE
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = COST_OF_DU_SOLD
• row_key = COST_OF_DU_SOLD
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

Section:
Selling
expenses

SRS – 225

TDX Delivery – [Dashboard phase 6] PAKD

Incentive
s

Cột total = BU Incentives + Internal Incentives +
DU Incentives
Cột BU = BU Revenue from work delivered *
Incentives Rate

Khi lưu vào bảng business_plan_version_detail
Cột Total
• detail_section = SELLING_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = INCENTIVES
• row_key = INCENTIVES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Cột BU
• detail_section = SELLING_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = INCENTIVES
• row_key = INCENTIVES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 226

TDX Delivery – [Dashboard phase 6] PAKD

Agency
expenses

Từ business_plan_version.id122 → mapping
revenue_plan_group theo
business_plan_version_id → mapping
revenue_plan_other revenues theo
revenue_plan_group_id → mapping
revenue_plan_other_revenue_detail → lấy ra
các bản ghi thoả mãn điều kiện
• revenue_type_id = id của Agency expenses
→ Ra được list bản ghi
Agency
expenses
=T
ổng revenue_plan_other_revenue_detail.value
Cột Total
• detail_section = SELLING_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = AGENCY_EXPENSE
• row_key = AGENCY_EXPENSE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Cột BU
• detail_section = SELLING_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = AGENCY_EXPENSE
• row_key = AGENCY_EXPENSE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

122 http://business_plan_version.id/

SRS – 227

TDX Delivery – [Dashboard phase 6] PAKD

1
1

Section
6:
Delivery
expenses

La
bel

Nu
m
be
r

NA

N
A

NA

Delivery expenses của Total = Tổng giá trị của
các trường thuộc section Delivery Expenses
thuộc cột Total
Delivery expenses của BU = Tổng giá trị của các
trường thuộc section Delivery Expenses thuộc
cột BU
Delivery expenses của Internal = Tổng giá trị của
các trường thuộc section Delivery Expenses
thuộc cột Internal
Delivery expenses của DU = Tổng giá trị của các
trường thuộc section Delivery Expenses thuộc
cột DU

Logic lưu data vào business_plan_version_detail
của BU
• detail_section = DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = DELIVERY_EXPENSES_TOTAL
• row_key = DELIVERY_EXPENSES_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = DELIVERY_EXPENSES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = DELIVERY_EXPENSES_TOTAL
• row_key = DELIVERY_EXPENSES_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DELIVERY_EXPENSES_TOTAL
• row_key = DELIVERY_EXPENSES_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 228

TDX Delivery – [Dashboard phase 6] PAKD

Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = DELIVERY_EXPENSES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DELIVERY_EXPENSES_TOTAL
• row_key = DELIVERY_EXPENSES_TOTAL
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 229

TDX Delivery – [Dashboard phase 6] PAKD

1
2

Direct
labor
cost

La
bel
/
Te
xt
fiel
d

Nu
m
be
r

NA

N
A

NA

Direct labor cost cột DU
Công thức tính Direct labor cost của 1 tháng
Direct labor cost của 1 tháng = Labor rate của
nhân sự có type = In-house x MM effort của nhân
sự trong tháng đó
• Labor rate của nhân sự =
delivery_plan_member.gross_salary_vnd x
labor_rate_config.salary_index (của country
tương ứng của user) +
labor_rate_config.expense_index (của
country tương ứng của user)
• MM effort = Từ delivery_plan_member.id123
→ mapping delivery_plan_budget_MM theo
delivery_plan_id → lấy ra value theo month
và year tương ứng
Direct labor cost = Tổng Direct labor cost của
từng tháng thuộc delivery plan của
Direct labor cost cột Total = Tổng Direct labor
cost (BU + Internal + DU)
Direct labor cost cột Internal: Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới được nhập
Direct labor cost cột BU: User nhập
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = DELIVERY_EXPENSES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = DIRECT_LABOR_COST
• row_key = DIRECT_LABOR_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DIRECT_LABOR_COST
• row_key = DIRECT_LABOR_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

123 http://delivery_plan_member.id/

SRS – 230

TDX Delivery – [Dashboard phase 6] PAKD

Logic lưu data vào business_plan_version_detail
của BU
• detail_section = DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = DIRECT_LABOR_COST
• row_key = DIRECT_LABOR_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = DELIVERY_EXPENSES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DIRECT_LABOR_COST
• row_key = DIRECT_LABOR_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 231

TDX Delivery – [Dashboard phase 6] PAKD

1
3

Outsourc
ing cost

La
bel
/
Te
xt
fiel
d

Nu
m
be
r

NA

N
A

NA

Outsourcing cost cột DU
Công thức tính Outsourcing cost của 1 tháng
Outsourcing Cost của 1 tháng = Gross Salary
(VND) của nhân sự có type = Outsourced x MM
Effort của nhân sự trong tháng đó
Outsourcing cost = Tổng Outsourcing cost của
từng tháng thuộc delivery plan

Outsourcing cost cột Total = Tổng Direct labor
cost (BU + Internal + DU)
Outsourcing cost cột Internal: Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới được nhập
Outsourcing cost cột BU: User nhập
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = DELIVERY_EXPENSES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = OUTSOURCING_COST
• row_key = OUTSOURCING_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OUTSOURCING_COST
• row_key = OUTSOURCING_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = OUTSOURCING_COST
• row_key = OUTSOURCING_COST
• label_text = null
• label_select_id = null

SRS – 232

TDX Delivery – [Dashboard phase 6] PAKD

• value = Giá trị tương ứng

Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = DELIVERY_EXPENSES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OUTSOURCING_COST
• row_key = OUTSOURCING_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 233

TDX Delivery – [Dashboard phase 6] PAKD

1
4

Equipme
nt,
Internet,
Server
cost

La
bel
/
Te
xt
fiel
d

Nu
m
be
r

NA

N
A

NA

Equipment, Internet, Server cost cột DU
Từ business_plan_version.id124 →
mapping delivery_plan.id125 theo
business_plan_version_id → → mapping
delivery_plan_other_expenses theo
delivery_plan_id → mapping
delivery_plan_other_expenses_detail theo
delivery_plan_other_Expenses_id → lấy tổng
delivery_plan_other_expenses_detail.value của
tất cả bản ghi có cost_type_id = id của
Equipment expense

Equipment, Internet, Server cost cột Total =
Tổng của (BU + Internal + DU)
Equipment, Internet, Server cost cột
Internal: Chỉ quyền EDIT_BUSINESS_PLAN_ALL
mới được nhập
Equipment, Internet, Server cost cột BU: User
nhập
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = DELIVERY_EXPENSES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label =
EQUIPMENT_INTERNET_SERVER_COST
• row_key
= EQUIPMENT_INTERNET_SERVER_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= EQUIPMENT_INTERNET_SERVER_COST
• row_key
= EQUIPMENT_INTERNET_SERVER_COST
• label_text = null
• label_select_id = null

124 http://business_plan_version.id/
125 http://delivery_plan.id/

SRS – 234

TDX Delivery – [Dashboard phase 6] PAKD

• value = Giá trị tương ứng

Logic lưu data vào business_plan_version_detail
của BU
• detail_section = DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label
= EQUIPMENT_INTERNET_SERVER_COST
• row_key
= EQUIPMENT_INTERNET_SERVER_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = DELIVERY_EXPENSES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label
= EQUIPMENT_INTERNET_SERVER_COST
• row_key
= EQUIPMENT_INTERNET_SERVER_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 235

TDX Delivery – [Dashboard phase 6] PAKD

1
5

Onsite
expenses
(Onsite
allowanc
e,
perdiem,
travelling
,
accomod
ation,
etc.)

La
bel
/
Te
xt
fiel
d

Nu
m
be
r

NA

N
A

NA

Onsite expenses cost cột DU
Từ business_plan_version.id126 →
mapping delivery_plan.id127 theo
business_plan_version_id → → mapping
delivery_plan_other_expenses theo
delivery_plan_id → mapping
delivery_plan_other_expenses_detail theo
delivery_plan_other_Expenses_id → lấy tổng
delivery_plan_other_expenses_detail.value của
tất cả bản ghi có cost_type_id = id của Onsite
expense

Onsite expenses cost cột Total = Tổng của (BU
+ Internal + DU)
Onsite expenses cost cột Internal: Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới được nhập
Onsite expenses cost cột BU: User nhập
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = DELIVERY_EXPENSES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = ONSITE_DEVELOPMENT_COST
• row_key = ONSITE_DEVELOPMENT_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = ONSITE_DEVELOPMENT_COST
• row_key = ONSITE_DEVELOPMENT_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = DELIVERY_EXPENSES
• participate_type = SALE

126 http://business_plan_version.id/
127 http://delivery_plan.id/

SRS – 236

TDX Delivery – [Dashboard phase 6] PAKD

• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = ONSITE_DEVELOPMENT_COST
• row_key = ONSITE_DEVELOPMENT_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = DELIVERY_EXPENSES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = ONSITE_DEVELOPMENT_COST
• row_key = ONSITE_DEVELOPMENT_COST
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
1
6

Project
bonus

La
bel

Nu
m
be
r

NA

N
A

NA

Project bonus cost cột DU = Project bonus/MM
x MM Bill của DU
Project bonus cost cột Total = Tổng của (BU +
Internal + DU)
Project bonus cost cột Internal: NA
Project bonus cost cột BU: NA
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = DELIVERY_EXPENSES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = PROJECT_BONUS
• row_key = PROJECT_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = PROJECT_BONUS
• row_key = PROJECT_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 237

TDX Delivery – [Dashboard phase 6] PAKD

1
7

Overtime

La
bel
/
Te
xt
fiel
d

Nu
m
be
r

NA

N
A

NA

Overtime cột DU
Từ business_plan_version.id128 →
mapping delivery_plan.id129 theo
business_plan_version_id → → mapping
delivery_plan_other_expenses theo
delivery_plan_id → mapping
delivery_plan_other_expenses_detail theo
delivery_plan_other_Expenses_id → lấy tổng
delivery_plan_other_expenses_detail.value của
tất cả bản ghi có cost_type_id = id của Overtime

Overtime cột Total = Tổng của (BU + Internal +
DU)
Overtime cột Internal: Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới được nhập
Overtime cột BU: User nhập
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = DELIVERY_EXPENSES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = OVERTIME
• row_key = OVERTIME
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OVERTIME
• row_key = OVERTIME
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU

128 http://business_plan_version.id/
129 http://delivery_plan.id/

SRS – 238

TDX Delivery – [Dashboard phase 6] PAKD

• label_type = MAIN_LABEL
• label = OVERTIME
• row_key = OVERTIME
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = DELIVERY_EXPENSES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OVERTIME
• row_key = OVERTIME
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 239

TDX Delivery – [Dashboard phase 6] PAKD

1
8

Nondeductib
le input
VAT

La
bel
/
Te
xt
fiel
d

Nu
m
be
r

NA

N
A

NA

Non-deductible input VAT cột
DU:Từ business_plan_version.id130 →
mappingdelivery_plan.id131theo
business_plan_version_id → mapping
delivery_plan_other_expenses theo
delivery_plan_id → mapping
delivery_plan_other_expenses_detail theo
delivery_plan_other_Expenses_id → lấy tổng
delivery_plan_other_expenses_detail.value của
tất cả bản ghi có cost_type_id = id của Nondeductible input VAT
Non-deductible input VAT cột Total = Tổng
của (BU + Internal + DU)
Non-deductible input VAT cột Internal: Chỉ
quyền EDIT_BUSINESS_PLAN_ALL mới được
nhập
Non-deductible input VAT cột BU: NA
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = DELIVERY_EXPENSES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = NON_DEDUCTION_VAT
• row_key = NON_DEDUCTION_VAT
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = NON_DEDUCTION_VAT
• row_key = NON_DEDUCTION_VAT
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU

130 http://business_plan_version.id/
131 http://delivery_plan.id/

SRS – 240

TDX Delivery – [Dashboard phase 6] PAKD

• label_type = MAIN_LABEL
• label = NON_DEDUCTION_VAT
• row_key = NON_DEDUCTION_VAT
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = DELIVERY_EXPENSES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = NON_DEDUCTION_VAT
• row_key = NON_DEDUCTION_VAT
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 241

TDX Delivery – [Dashboard phase 6] PAKD

1
9

Other
expenses

La
bel
/
Te
xt
fiel
d

Nu
m
be
r

NA

N
A

NA

Other expenses cột DU:
Từ business_plan_version.id132 →
mapping delivery_plan.id133 theo
business_plan_version_id → → mapping
delivery_plan_other_expenses theo
delivery_plan_id → mapping
delivery_plan_other_expenses_detail theo
delivery_plan_other_Expenses_id → lấy tổng
delivery_plan_other_expenses_detail.value của
tất cả bản ghi có cost_type_id = id của Other
expenses
Other expenses cột Total = Tổng của (BU +
Internal + DU)
Other expenses cột Internal: Chỉ quyền
EDIT_BUSINESS_PLAN_ALL mới được nhập
Other expenses cột BU: User nhập
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = DELIVERY_EXPENSES
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = OTHER_EXPENSES
• row_key = OTHER_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = DELIVERY_EXPENSES
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OTHER_EXPENSES
• row_key = OTHER_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = DELIVERY_EXPENSES
• participate_type = SALE
• group_id = group_id của BU

132 http://business_plan_version.id/
133 http://delivery_plan.id/

SRS – 242

TDX Delivery – [Dashboard phase 6] PAKD

• label_type = MAIN_LABEL
• label = OTHER_EXPENSES
• row_key = OTHER_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = DELIVERY_EXPENSES
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = OTHER_EXPENSES
• row_key = OTHER_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
2
0

Section
7: Tax
expenses

La
bel

Nu
m
be
r

NA

N
A

NA

Tax expenses cột DU: DU Revenues * % CIT and
VAT
Tax expenses cột Total = Total Revenues * %
CIT and VAT
Tax expenses cột Internal: NA
Tax expenses cột BU: BU Revenues * % CIT and
VAT

2
1

CIT and
VAT (If
any) (%)

Te
xt
fiel
d

Nu
m
be
r

Lấy từ
mater
_data.
setting
của
bản
ghi có
type =
6→
lấy
tr
ường
value

Y

NA

CIT and VAT cột Total: User điền %
CIT and VAT cột BU: NA
CIT and VAT cột Internal: NA
CIT and VAT cột DU: NA

Section
8: Direct
Margin

SRS – 243

TDX Delivery – [Dashboard phase 6] PAKD

Direct
Margin

Cột Total = Total Revenues - Total Cost of sales Total Selling expenses - Total Delivery expenses Total tax expenses

Cột BU = BU Revenue - BU Cost of sales - BU
Selling expense - BU Delivery expenses - BU Tax
expenses
Cột Internal = Internal revenues - Internal Cost of
sales - Internal selling expenses - Internal
delivery expenses - Internal tax expenses
Cột DU = DU Revenues - DU Cost of sales - DU
selling expenses - DU Delivey expense - DU Tax
expenses
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = MARGIN
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN
• row_key = DIRECT_MARGIN
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = MARGIN
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN
• row_key = DIRECT_MARGIN
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = MARGIN
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN
• row_key = DIRECT_MARGIN
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 244

TDX Delivery – [Dashboard phase 6] PAKD

Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = MARGIN
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN
• row_key = DIRECT_MARGIN
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 245

TDX Delivery – [Dashboard phase 6] PAKD

Direct
Margin
before
Incentive
s and
Project
bonus

Cột Total = Total Incentives + Total Project
Bonus + Total Direct Margin
Cột BU = BU Incentives + BU Project Bonus + BU
Direct Margin
Cột Internal = Internal Incentives + Internal
Project Bonus + Internal Direct Margin
Cột DU = DU Incentives + DU Project Bonus + DU
Direct margin
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = MARGIN
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_BONUS
• row_key = DIRECT_MARGIN_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = MARGIN
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_BONUS
• row_key = DIRECT_MARGIN_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = MARGIN
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_BONUS
• row_key = DIRECT_MARGIN_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = MARGIN
• participate_type = INTERNAL
• group_id = 0

SRS – 246

TDX Delivery – [Dashboard phase 6] PAKD

• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_BONUS
• row_key = DIRECT_MARGIN_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Allocatio
n of pool
and
unbillabl
e

Cột Total = BU Allocation of pool and unbillable +
Internal Allocation of pool and unbillable +
DU Allocation of pool and unbillable
Cột DU = (DU Direct labor cost / DU Billable rate
norms) - DU Direct labor cost

SRS – 247

TDX Delivery – [Dashboard phase 6] PAKD

Indirect
margin

Cột Total = Total Direct Margin - Total allocation
of pool and unbillable

Cột BU = BU Direct Margin - BU allocation of pool
and unbillable
Cột Internal = Internal Direct Margin - Internal
allocation of pool and unbillable
Cột DU = DU Direct Margin - DU allocation of pool
and unbillable
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = MARGIN
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = INDIRECT_MARGIN
• row_key = INDIRECT_MARGIN
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = MARGIN
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = INDIRECT_MARGIN
• row_key = INDIRECT_MARGIN
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = MARGIN
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = INDIRECT_MARGIN
• row_key = INDIRECT_MARGIN
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = MARGIN
• participate_type = INTERNAL
• group_id = 0

SRS – 248

TDX Delivery – [Dashboard phase 6] PAKD

• label_type = MAIN_LABEL
• label = INDIRECT_MARGIN
• row_key = INDIRECT_MARGIN
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 249

TDX Delivery – [Dashboard phase 6] PAKD

Direct
margin
%

Cột Total = (Total Direct Margin / Total Revenues)
x 100
Cột BU = (BU Direct Margin / BU Revenues) x 100

Cột Internal = (Internal Direct Margin / Internal
Revenues) x 100
Cột DU = (DU Direct Margin / DU Revenues) x 100
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = MARGIN
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_RATE
• row_key = DIRECT_MARGIN_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = MARGIN
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_RATE
• row_key = DIRECT_MARGIN_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = MARGIN
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_RATE
• row_key = DIRECT_MARGIN_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = MARGIN
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_RATE

SRS – 250

TDX Delivery – [Dashboard phase 6] PAKD

• row_key = DIRECT_MARGIN_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 251

TDX Delivery – [Dashboard phase 6] PAKD

Direct
margin
before
Incentive
s and
Project
bonus %

Cột Total = (Total Direct margin before
Incentives and Project bonus / Total Revenues) x
100
Cột BU = (BU Direct margin before Incentives and
Project bonus / BU Revenues) x 100
Cột Internal = (Internal Direct margin before
Incentives and Project bonus / Internal
Revenues) x 100
Cột DU = (DU Direct margin before Incentives
and Project bonus / DU Revenues) x 100
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = MARGIN
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_BONUS_RATE
• row_key = DIRECT_MARGIN_BONUS_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = MARGIN
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_BONUS_RATE
• row_key = DIRECT_MARGIN_BONUS_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = MARGIN
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_BONUS_RATE
• row_key = DIRECT_MARGIN_BONUS_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = MARGIN

SRS – 252

TDX Delivery – [Dashboard phase 6] PAKD

• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DIRECT_MARGIN_BONUS_RATE
• row_key = DIRECT_MARGIN_BONUS_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 253

TDX Delivery – [Dashboard phase 6] PAKD

Indirect
margin
%

Cột Total = (Total Indirect margin / Total
Revenues) x 100
Cột BU = (BU Indirect margin / BU Revenues) x
100
Cột Internal = (Internal Indirect margin / Internal
Revenues) x 100
Cột DU = (DU Indirect margin/ DU Revenues) x
100
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = MARGIN
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = INDIRECT_MARGIN_RATE
• row_key = INDIRECT_MARGIN_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = MARGIN
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = INDIRECT_MARGIN_RATE
• row_key = INDIRECT_MARGIN_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = MARGIN
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = INDIRECT_MARGIN_RATE
• row_key = INDIRECT_MARGIN_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = MARGIN
• participate_type = INTERNAL
• group_id = 0

SRS – 254

TDX Delivery – [Dashboard phase 6] PAKD

• label_type = MAIN_LABEL
• label = INDIRECT_MARGIN_RATE
• row_key = INDIRECT_MARGIN_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

Section
9:
Referenc
e

SRS – 255

TDX Delivery – [Dashboard phase 6] PAKD

2
2

Average
delivery
expenses

La
bel

Nu
m
be
r

NA

N
A

NA

Average delivery expenses Total = (Total delivery
expenses / total MM effort)x 100
Average delivery expenses BU = (BU Cost of sales
(Ratecard) / BU MM effort)x 100

Average delivery expenses Internal NA
Average delivery expenses DU= (DU delivery
expenses / DU MM effort) x 100
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REFERENCE
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = DELIVERY_AVERAGE_EXPENSES
• row_key = DELIVERY_AVERAGE_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REFERENCE
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = DELIVERY_AVERAGE_EXPENSES
• row_key = DELIVERY_AVERAGE_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REFERENCE
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = DELIVERY_AVERAGE_EXPENSES
• row_key = DELIVERY_AVERAGE_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = REFERENCE
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL

SRS – 256

TDX Delivery – [Dashboard phase 6] PAKD

• label = DELIVERY_AVERAGE_EXPENSES
• row_key = DELIVERY_AVERAGE_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 257

TDX Delivery – [Dashboard phase 6] PAKD

2
3

Average
direct
labor
cost/MM

La
bel

Nu
m
be
r

NA

N
A

NA

Total = Total direct labor cost / total MM effort
BU = BU direct labor cost / BU MM effort
Internal NA

DU = DU direct labor cost / DU MM effort
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REFERENCE
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = SALARY_AVERAGE_EXPENSES
• row_key = SALARY_AVERAGE_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REFERENCE
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = SALARY_AVERAGE_EXPENSES
• row_key = SALARY_AVERAGE_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REFERENCE
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = SALARY_AVERAGE_EXPENSES
• row_key = SALARY_AVERAGE_EXPENSES
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = REFERENCE
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = SALARY_AVERAGE_EXPENSES
• row_key = SALARY_AVERAGE_EXPENSES
• label_text = null

SRS – 258

TDX Delivery – [Dashboard phase 6] PAKD

• label_select_id = null
• value = Giá trị tương ứng

SRS – 259

TDX Delivery – [Dashboard phase 6] PAKD

2
4

BIllable
rate (%)

La
bel

Nu
m
be
r

NA

N
A

NA

Total = Total MM bill / total MM effort
BU = BU MM bill / BU MM effort
Internal NA

DU = DU MM bill / DU MM effort
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REFERENCE
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = BILLABLE_RATE
• row_key = BILLABLE_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REFERENCE
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = BILLABLE_RATE
• row_key = BILLABLE_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REFERENCE
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = BILLABLE_RATE
• row_key = BILLABLE_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = REFERENCE
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = BILLABLE_RATE
• row_key = BILLABLE_RATE
• label_text = null

SRS – 260

TDX Delivery – [Dashboard phase 6] PAKD

• label_select_id = null
• value = Giá trị tương ứng

SRS – 261

TDX Delivery – [Dashboard phase 6] PAKD

2
5

Producti
vity

La
bel

Nu
m
be
r

NA

N
A

NA

Total = Total Revenues from work delivered /
total MM effort
BU NA

Internal NA
DU = DU Revenues from work delivered / DU MM
effort
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REFERENCE
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = PRODUCTIVITY
• row_key = PRODUCTIVITY
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REFERENCE
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = PRODUCTIVITY
• row_key = PRODUCTIVITY
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REFERENCE
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = PRODUCTIVITY
• row_key = PRODUCTIVITY
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = REFERENCE
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = PRODUCTIVITY

SRS – 262

TDX Delivery – [Dashboard phase 6] PAKD

• row_key = PRODUCTIVITY
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 263

TDX Delivery – [Dashboard phase 6] PAKD

2
6

Efficienc
y

La
bel

Nu
m
be
r

NA

N
A

NA

Total = Total direct margin / total MM effort
BU NA
Internal NA

DU= DU direct margin / DU MM effort
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REFERENCE
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = EFFICIENCY
• row_key = EFFICIENCY
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REFERENCE
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = EFFICIENCY
• row_key = EFFICIENCY
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REFERENCE
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = EFFICIENCY
• row_key = EFFICIENCY
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL
• detail_section = REFERENCE
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = EFFICIENCY
• row_key = EFFICIENCY
• label_text = null

SRS – 264

TDX Delivery – [Dashboard phase 6] PAKD

• label_select_id = null
• value = Giá trị tương ứng

SRS – 265

TDX Delivery – [Dashboard phase 6] PAKD

2
7

Incentive
s rate (%)

Te
xt
fiel
d

Nu
m
be
r

NA

N
A

NA

Total NA
BU:
• Default value: bảng mater_data.setting type
= 7 → lấy trường value
• Giá trị: Lấy từ
business_plan_version_detail.value của
detail_section = REFERENCE và
participate_type = SALE và label =
INCENTIVES_RATE
Internal NA
DU NA
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REFERENCE
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = INCENTIVES_RATE
• row_key = INCENTIVES_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REFERENCE
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = INCENTIVES_RATE
• row_key = INCENTIVES_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REFERENCE
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = INCENTIVES_RATE
• row_key = INCENTIVES_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL

SRS – 266

TDX Delivery – [Dashboard phase 6] PAKD

• detail_section = REFERENCE
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = INCENTIVES_RATE
• row_key = INCENTIVES_RATE
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 267

TDX Delivery – [Dashboard phase 6] PAKD

2
8

Project
bonus/
MM

Te
xt
fiel
d

Nu
m
be
r

NA

N
A

NA

Total NA
BU: NA
Internal NA

DU
• Default value: bảng mater_data.setting type
= 8 → lấy trường value
• Giá trị: Lấy từ
business_plan_version_detail.value của
detail_section = REFERENCE và
participate_type = DELIVERY_UNIT và label
= PRODUCTION_MM_BONUS
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REFERENCE
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = PRODUCTION_MM_BONUS
• row_key = PRODUCTION_MM_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REFERENCE
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = PRODUCTION_MM_BONUS
• row_key = PRODUCTION_MM_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REFERENCE
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = PRODUCTION_MM_BONUS
• row_key = PRODUCTION_MM_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL

SRS – 268

TDX Delivery – [Dashboard phase 6] PAKD

• detail_section = REFERENCE
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = PRODUCTION_MM_BONUS
• row_key = PRODUCTION_MM_BONUS
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng

SRS – 269

TDX Delivery – [Dashboard phase 6] PAKD

2
9

Billable
rate
norm (%)

Te
xt
fiel
d

Nu
m
be
r

NA

N
A

NA

Total NA
BU: NA
Internal NA

DU
• Default value: bảng mater_data.setting type
= 9 → lấy trường value
• Giá trị: Lấy từ
business_plan_version_detail.value của
detail_section = REFERENCE và
participate_type = DELIVERY_UNIT và label
= BILL_RATE_NORM
Logic lưu data vào business_plan_version_detail
của DU
• detail_section = REFERENCE
• participate_type = DELIVERY_UNIT
• group_id = group_id của DU
• label_type = MAIN_LABEL
• label = BILL_RATE_NORM
• row_key = BILL_RATE_NORM
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của TOTAL
• detail_section = REFERENCE
• participate_type = TOTAL
• group_id = 0
• label_type = MAIN_LABEL
• label = BILL_RATE_NORM
• row_key = BILL_RATE_NORM
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của BU
• detail_section = REFERENCE
• participate_type = SALE
• group_id = group_id của BU
• label_type = MAIN_LABEL
• label = BILL_RATE_NORM
• row_key = BILL_RATE_NORM
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Logic lưu data vào business_plan_version_detail
của INTERNAL

SRS – 270

TDX Delivery – [Dashboard phase 6] PAKD

• detail_section = REFERENCE
• participate_type = INTERNAL
• group_id = 0
• label_type = MAIN_LABEL
• label = BILL_RATE_NORM
• row_key = BILL_RATE_NORM
• label_text = null
• label_select_id = null
• value = Giá trị tương ứng
Bảng business_plan_version_group
Fi
el
d

Rule

id

PK
của
bảng

b
u
si
n
e
ss
_
pl
a
n
_
V
er
si
o
n
_i
d

Từ
busin
ess_pl
an_ve
rsion.i
d

SRS – 271

TDX Delivery – [Dashboard phase 6] PAKD

Fi
el
d
Rule

gr
o
u
p
_i
d
Nếu
là BU:

→
Query
bảng
sale_
wo_v
2 theo
điều
kiện:

• m
vv
=
b
us
in
es
s_
pl
a
n
_v
er
si
o
n.
pr
oj
ec
t_
co
d
e
• w
o
_t
y
p
e
→
m
a
p
pi
n
g
và

SRS – 272

TDX Delivery – [Dashboard phase 6] PAKD

Fi
el
d
Rule

o
b
ả
n
g
cr
m
_
m
as
te
r_
d
at
a
_
m
a
p
pi
n
g,
tr
ư
ờ
n
g
n
a
m
e
=
S
al
e
và
m
as
te
r_
d
at
a
_
n
a
m
e
=

SRS – 273

TDX Delivery – [Dashboard phase 6] PAKD

Fi
el
d
Rule

Pi
p
eli
n
e
T
y
p
e
• p
ar
e
nt
_
w
o
_k
ey
is
n
ul
l
• w
o
_s
ta
tu
s
k
h
ác
ca
n
ce
l

→ ra
được
list
bản
ghi l
ấy
được
list
wo_k
ey (1)

SRS – 274

TDX Delivery – [Dashboard phase 6] PAKD

Fi
el
d
Rule

→
Query
bảng
sale_
wo_v
2 theo
điều
kiện:

• p
ar
e
nt
_
w
o
_k
ey
is
in
lis
t
(1
)
• w
o
_s
ta
tu
s
k
h
ác
C
a
n
ce
l

→ ra
được
list
bản
ghi (2)

SRS – 275

TDX Delivery – [Dashboard phase 6] PAKD

Fi
el
d

Rule

Từ
tr
ường
id của
list
(2) →
query
unit_f
ulfillin
g_ord
er
theo
sale_
wo_v
2_id
→ ra
được
list (3)
Nếu
là
BU:
từ
tr
ường
du_id
của
list
(2) →
distin
ct list
→
mapp
ing
crm_
maste
r_dat
a_ma
pping
.name134
theo
id →
mapp
ing
group
s.grou
p_id
134 http://crm_master_data_mapping.name

SRS – 276

TDX Delivery – [Dashboard phase 6] PAKD

Fi
el
d

Rule

theo
group
_nam
e
Nếu
là
DU: t
ừ
tr
ường
group
_id
của
list
(3) →
distin
ct list
→
lấy
group
_id
is
_
s
al
e

Nếu
là BU:
is_sal
e=1
Nếu
là DU:
is_sal
e=0

Bảng business_plan_version_location_exchange_rate
Khi user submit, từ business_plan_version.id mapping business_plan_version_location_exchange_rate
Nếu có bản ghi → Không làm gì cả
Nếu không có bản ghi → lấy data trong bảng location_default_exchange_rate → insert vào
business_plan_version_location_exchange_rate
với business_plan_version_location_exchange_rate.business_plan_version_id tương ứng
Bảng business_plan_labor_rate
Khi user submit → lấy data trong labor_rate_config → insert vào business_plan_labor_rate với
business_plan_labor_rate.business_plan_version_id tương ứng
3.8.4.1. Revenue Plan
Bảng revenue_plan_group

SRS – 277

TDX Delivery – [Dashboard phase 6] PAKD

Khi user submit, check BU tại thời điển submit là BU nào → Kiểm tra bảng revenue_plan_group với is_sale = 1 →
Nếu có group_id khác với BU tại thời điểm submit → xoá bản ghi đó trong bảng revenue_plan_group → xoá bản
ghi revenue_plan_other_revenues theo revenue_plan_group_id tương ứng → xoá bản ghi
revenue_plan_other_revenue_detail theo revenue_plan_other_revenues_id tương ứng

Khi user submit →
Query bảng sale_wo_v2 theo điều kiện
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng crm_master_data_mapping, trường name = Sale và master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
• du_id = Revenue Plan Filter Department user chọn
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) → query unit_fulfilling_order theo sale_wo_v2_id tương ứng (3)
Từ (3) mapping bảng dpm_delivery theo unit_fulfilling_order_id và theo điều kiện
• tháng của sale_wo_v2.start_date của list (2) <= dpm_delivery.month và year
• tháng của sale_wo_v2.end_date của list (2) >= dpm_delivery.month và year
→ ra list (4)
Bảng revenue_plan_sales_order

Field

Rule

id

PK

revenue_plan_group_id

revenue_plan_group.id

position

từ list (2) → lấy sale_wo_v2.position_id

unit_price

từ list (2) → lấy sale_wo_v2.unit_price

pipeline_ratio

từ list (1) → lấy sale_wo_v2.pipeline_key → mapping bảng
sale_pipeline_v2.status → mapping pipeline_status_ratio lấy ratio

sale_wo_v2_id

từ list (2) → lấy sale_wo_v2.id của wo con

SRS – 278

TDX Delivery – [Dashboard phase 6] PAKD

Field

Rule

pipeline_key

từ list (1) → lấy sale_wo_v2.pipeline_key

Bảng revenue_plan_unit_fulfilling_order
Khi user submit → lấy giá trị trong bảng unit_fulfilling_order để baseline lại vào bảng
revenue_plan_unit_fulfilling_order
Field

Rule

id

PK

revenue_plan_sales_order_id

revenue_plan_sales_order.id

ratecard

từ list (3) → lấy unit_fulfillng_order.ratecard

department

từ list (3) → lấy unit_fulfilling_order.group_id

Bảng revenue_plan_MM_headcount
Khi user submit → lấy giá trị trong bảng dpm_delivery để baseline lại vào bảng revenue_plan_MM_headcount
Field

Rule

id

PK

revenue_plan_unit_fulfilling_order_id

revenue_plan_unit_fulfilling_order.id

month

từ list (4) → lấy dpm_delivery.month

year

từ list (4) → lấy dpm_delivery.year

monthly_headcount

từ list (4) → lấy dpm_delivery.billable_value

SRS – 279

TDX Delivery – [Dashboard phase 6] PAKD

4.3.9 3.9 Feature: Delivery Plan User Action History

4.3.9.1 3.9.1 Access control
4.3.9.2 3.9.2 Use case description
Use case

Delivery Plan User Action
History

Precondition
Thiết bị của user phải có kết nối internet
User có thể view được business plan detail và delivery plan

Description

Post condition
User xem thông tin History Business Plan thành công

Main Flow

User: Tại màn Business Plan
List, click button View Details
System: Mở màn hình
Business Plan Detail
User: Tại section business
plan, chọn tab Delivery Plan

Business Rule

GUI Reference

từ delivery_plan.id mapping
history_changes.reference_id và
history_module = DELIVERY_PLAN →
mapping history_changes_detail theo
history_changes_id để lấy thông tin
history để hiển thị

G1

System: Hiển thị Delivery Plan
User: Bấm vào section History
System: Hiển thị section
History

SRS – 280

TDX Delivery – [Dashboard phase 6] PAKD

4.3.9.3 3.9.3 Graphic User Interface

G1.

4.3.9.4 3.9.4 GUI Element
#

1

Field

Author - Time
made
changes

Description

Contr
ol
Type

Label

Data
Typ
e

Strin
g

Defau
lt
Value

NA

Editab
le?

Mandat
ory?

(Y/N/
NA)

(Y/N/
NA)

NA

NA

Rule

No
te

Format: Username
- Action - Time
VD: nbtduy - made
changes to
Delivery Plan - 14/
Jan/25 01:51 PM

SRS – 281

TDX Delivery – [Dashboard phase 6] PAKD

2

Collapse/
Expand
button

Butto
n

NA

NA

NA

NA

Button Collapse/
Expand all: FE
đóng/mở tất cả
các component
Collapse/Expand
con
Button Collapse/
Expand con: Chỉ
collapse/expand
của phần đó

3

Action Time

Label

Strin
g

NA

NA

NA

history_changes.c
reated_at

4

Author

Label

Strin
g

NA

NA

NA

history_changes.a
ctor_username

5

History Type

Label

Strin
g

NA

NA

NA

history_changes.h
istory_module

6

Entity

Label

Strin
g

NA

NA

NA

history_changes_
detail.display

7

Department

Label

Strin
g

NA

NA

NA

history_changes

8

Field

Label

Strin
g

NA

NA

NA

9

Old Value

Label

Strin
g

NA

NA

NA

history_changes_
detail.old_value_s
tring

1
0

New Value

Label

Strin
g

NA

NA

NA

history_changes_
detail.new_value_
string

4.3.10 Sample Request lưu old_value_string và new_value_string
[
{

SRS – 282

TDX Delivery – [Dashboard phase 6] PAKD

"user_id": "123",
"user_name": "nbtduy",

"resource_type": "User",
"location": "Vietnam",
"employee_type": "In-house",
"original_gross_salary": "10000000",
"gross_salary_vnd": "10000000",
"position": "SE01",
"role": "Developer",
"ITEMS": [
{
"4-2024": "1",
"5-2024": "0.75",
"6-2024": "2.34",
}
]
}
]
[
{

SRS – 283

TDX Delivery – [Dashboard phase 6] PAKD

"cost_type_id": "123",
"cost_type_name" : "nbtduy",

"cost_type_specific_name": "Vietnam",
"ITEMS": [
{
"4-2024": "1000000",
"5-2024": "1000000",
"6-2024": "1000000",
}
]
}
]

4.3.11 3.10 Feature: Revenue Plan User Action History
4.3.11.1 3.10.1 Access control
4.3.11.2 3.10.2 Use case description
Use case

Revenue Plan User Action
History

Precondition
Thiết bị của user phải có kết nối internet
User có thể view được Business plan detail và Revenue
plan

Description

Post condition
User xem thông tin History Revenue Plan thành công

SRS – 284

TDX Delivery – [Dashboard phase 6] PAKD

Main Flow

User: Tại màn Business Plan
List, click button View Details

System: Mở màn hình
Business Plan Detail
User: Tại section business
plan, chọn tab Revenue Plan

Business Rule

GUI Reference

từ revenue_plan.id135 mapping
history_changes.reference_id và
history_module = REVENUE_PLAN →
mapping history_changes_detail theo
history_changes_id để lấy thông tin
history để hiển thị

G1

System: Hiển thị Revenue Plan
User: Bấm vào section History
System: Hiển thị section
History

4.3.11.3 3.10.3 Graphic User Interface

G1.

4.3.11.4 3.10.4 GUI Element
#

Field

Descripti
on

Contr
ol
Type

Data
Type

Defaul
t
Value

Editabl
e?

Mandat
ory?

(Y/N/
NA)

(Y/N/
NA)

Rule

Note

135 http://delivery_plan.id

SRS – 285

TDX Delivery – [Dashboard phase 6] PAKD

1

Author Time made
changes

Label

String

NA

NA

NA

Format:
Username Action - Time
VD: nbtduy made changes
to Delivery
Plan - 14/Jan/
25 01:51 PM

2

Collapse/
Expand
button

Button

NA

NA

NA

NA

Button
Collapse/
Expand all: FE
đóng/mở tất
cả các
component
Collapse/
Expand con
Button
Collapse/
Expand con:
Chỉ collapse/
expand của
phần đó

3

Action Time

Label

String

NA

NA

NA

history_change
s.created_at

4

Author

Label

String

NA

NA

NA

history_change
s.actor_userna
me

5

History
Type

Label

String

NA

NA

NA

history_change
s.history_mod
ule

6

Entity

Label

String

NA

NA

NA

history_change
s_detail.displa
y

7

Department

Label

String

NA

NA

NA

history_change
s

8

Field

Label

String

NA

NA

NA

SRS – 286

TDX Delivery – [Dashboard phase 6] PAKD

9

Old Value

Label

String

NA

NA

NA

history_change
s_detail.old_va
lue_string

10

New Value

Label

String

NA

NA

NA

history_change
s_detail.new_v
alue_string

4.3.12 Sample Request lưu old_value_string và new_value_string
[
{
"revenue_type_id": "123",
"revenue_type_name" : "nbtduy",
"revenue_type_specific_name": "Vietnam",
"ITEMS": [
{
"4-2024": "1000000",
"5-2024": "1000000",
"6-2024": "1000000",
}
]
}
]

SRS – 287

TDX Delivery – [Dashboard phase 6] PAKD

4.3.13 3.11 Feature: Create New Version

4.3.13.1 3.11.1 Access control
4.3.13.2 3.11.2 Use case description
Use case

Create New Version

Precondition
Thiết bị của user phải có kết nối internet
Button Create New Version chỉ hiển thị đối với version mới
nhất đã được approve của mã vụ việc trong Business Plan

Description

Post condition
User create new version thành công

SRS – 288

TDX Delivery – [Dashboard phase 6] PAKD

Main Flow

User: Tại màn Business Plan
List, click button View Details
System: Mở màn hình
Business Plan Detail

User: Ấn Create new version
System: Tạo version mới cho
Business Plan

Business Rule

GUI Reference
G1

Khi Create New Version, data trong
Business Plan mới được tạo ra sẽ
được create default theo logic như
sau
Delivery Plan
• Section Resource Information
• Tạo data trong các bảng
delivery_plan_member,
delivery_plan_budget_MM
theo data Actual Timesheet
của mã vụ việc đó (Tham
khảo phần logic database)
• Section Other expenses
• Tạo data trong các bảng
delivery_plan_other_expen
ses,
delivery_plan_other_expen
ses_detail theo data của
version trước (Tham khảo
phần logic database)
Revenue Plan
• Section Other revenues
• Tạo data trong các bảng
revenue_plan_other_reven
ues,
revenue_plan_other_reven
ue_detail theo data của
version trước (Tham khảo
phần logic database)

Business Plan: KHÔNG tạo data theo
version cũ, mà sẽ tính toán the data
của Delivery Plan và Revenue Plan sau
khi create new version

SRS – 289

TDX Delivery – [Dashboard phase 6] PAKD

4.3.13.3 3.11.3 Graphic User Interface

SRS – 290

TDX Delivery – [Dashboard phase 6] PAKD

SRS – 291

TDX Delivery – [Dashboard phase 6] PAKD

4.3.13.4 3.11.4 GUI Element

3.11.4.1. Delivery Plan
Khi user chọn trường Resource Full Name và expand collapse trong table Resources Information, BE query theo
logic sau
Truy vấn bảng project_task_spent_time theo
• user_id = user_id được chọn
• subject = business_plan.project_code
• allocation_type = 0
Ra được list (1)
(1) → Inner join 2 bảng group_user và project_task_spent_time theo user_id với điều kiện
• project_task_spent_time.start_date >= group_user.start_date
• project_task_spent_time.start_date <= group_user.end_date or group_user.end_date is null
→ lấy được list bản ghi tương ứng (2)
TÍnh tổng số project_task_spent_time.effort_per_day trong từng tháng dựa theo
project_tast_spent_time.start_date
Tính MM Actual TS = tổng số project_task_spent_time.effort_per_day trong tháng / Số ngày công trong tháng đó

Bảng delivery_plan_member
Field

Rule

id
delive
ry_pl
an_id

từ
delive
ry_pla
n.id

user_i
d

từ
projec
t_task
_spen
t_time
.assig
nee_i
d

SRS – 292

TDX Delivery – [Dashboard phase 6] PAKD

Field
Rule

user_
name
từ
projec
t_task
_spen
t_time
.assig
nee_i
d→
mappi
ng
users
theo
user_i
d→
lấy
user_
name

SRS – 293

TDX Delivery – [Dashboard phase 6] PAKD

Field
Rule

type_i
d
từ
projec
t_task
_spen
t_time
.assig
nee_i
d→
mappi
ng
users
theo
user_i
d→
lấy
emplo
yee_ty
pe

SRS – 294

TDX Delivery – [Dashboard phase 6] PAKD

Field
Rule

• N
ếu
e
m
pl
oy
ee
_t
yp
e
=
1
→
ty
pe
_i
d
=
se
tti
ng
_c
on
fig
.id
có
se
tti
ng
_c
on
fig
.n
a
m
e
=
Inho
us
e
• N
ếu
e
m
pl
oy
ee

SRS – 295

TDX Delivery – [Dashboard phase 6] PAKD

Field

origin
al_gr
oss_s
alary
Rule

_t
yp
e
kh
ác
1
→
ty
pe
_i
d
=
se
tti
ng
_c
on
fig
.id136
có
se
tti
ng
_c
on
fig
.n
a
m
e137
=
O
ut
so
ur
ce
d

null

136 http://setting_config.id

137 http://setting_config.name

SRS – 296

TDX Delivery – [Dashboard phase 6] PAKD

Field

Rule

gross
_salar
y_vnd

=
origin
al_gro
ss_sal
ary x
Excha
nge
Rate
của
locati
on của
nhân
sự
tương
ứng

positi
on_id

NA

role_i
d

từ
projec
t_task
_spen
t_time
.user_
plan_i
d→
mappi
ng

locati
on

NA

type_
resou
rce_id

settin
g_con
fig.id138
của
type_i
d→
mappi
ng
settin
g_typ
e theo
id

138 http://setting_config.id

SRS – 297

TDX Delivery – [Dashboard phase 6] PAKD

→
settin
g_typ
e.nam
e139 =
Resou
rce
Type
Bảng delivery_plan_budget_MM
Field

R
ul
e

id
delive
ry_pl
an_id

từ
de
liv
er
y_
pl
an
.id140

mont
h

từ
pr
oj
ec
t_
ta
sk
_s
pe
nt
_ti
m
e.
as
si
gn
ee
_i
d

139 http://setting_type.name/
140 http://delivery_plan.id

SRS – 298

TDX Delivery – [Dashboard phase 6] PAKD

Field
R
ul
e

year

từ
pr
oj
ec
t_
ta
sk
_s
pe
nt
_ti
m
e.
as
si
gn
ee
_i
d
→
m
ap
pi
ng
us
er
s
th
eo
us
er
_i
d
→
l
ấy
us
er
_n
a
m
e

SRS – 299

TDX Delivery – [Dashboard phase 6] PAKD

Field

R
ul
e

value

từ
M
M
Ac
tu
al
TS
c
ủa
us
er

Khi create new version
→ Query bảng sale_wo_v2 theo điều kiện:
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng crm_master_data_mapping, trường name = Sale và master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ ra được list bản ghi lấy được list wo_key (1)
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) → query unit_fulfilling_order theo sale_wo_v2_id → ra được list (3)
• BU: từ trường du_id của list (2) → distinct list → mapping crm_master_data_mapping.name141 theo id (4)
• DU: từ trường group_id của list (3) → distinct list → mapping groups.group_name theo group_id (5)

Sau đó query business_plan_version theo điều kiện
• business_plan_version.project_code = project_code được create new version
• business_plan_version.version = version mới nhất
→ lấy ra list bản ghi business_plan_version của version trước
Query delivery_plan theo business_plan_version_id và thoả mãn điều kiện
• group_id = (5)
→ ra list (6)
Query delivery_plan_other_expenses theo điều kiện

141 http://crm_master_data_mapping.name

SRS – 300

TDX Delivery – [Dashboard phase 6] PAKD

• delivery_plan_id = delivery_plan.id142 của list (6)
→ ra list (7)
Query delivery_plan_other_expenses_detail theo điều kiện
• delivery_plan_other_expenses_id = delivery_plan_other_expenses.id143 của list (7)

→ ra list (8)
Bảng delivery_plan
Khi create new version, insert vào bảng revenue_plan_group dựa theo data của list (6)
Field

R
ul
e

id

PK

busin
ess_p
lan_v
ersion
_id

từ
bu
si
ne
ss
_p
la
n_
ve
rsi
on
.id144
c
ủa
ve
rsi
on
bu
si
ne
ss
pl
an
m
ới
đ
ư
ợc
t
ạo

142 http://revenue_plan_group.id
143 http://revenue_plan_group.id
144 http://business_plan_version.id

SRS – 301

TDX Delivery – [Dashboard phase 6] PAKD

Field

R
ul
e

group
_id

gr
ou
p_
id
=
(5)

Bảng delivery_plan_other_expenses
Khi create new version, insert vào bảng delivery_plan_other_expenses dựa theo data của list (7)
• delivery_plan_id = delivery_plan.id145 của version mới được create

Bảng delivery_plan_other_expenses_detail
Khi create new version, insert vào bảng delivery_plan_other_expenses_detail dựa theo data của list (8)
• delivery_plan_other_expenses_id = delivery_plan_other_expenses.id146 của version mới được create
3.11.4.2. Revenue Plan
Khi create new version
→ Query bảng sale_wo_v2 theo điều kiện:
• mvv = business_plan_version.project_code
• wo_type → mapping vào bảng crm_master_data_mapping, trường name = Sale và master_data_name =
Pipeline Type
• parent_wo_key is null
• wo_status khác cancel
→ ra được list bản ghi lấy được list wo_key (1)
→ Query bảng sale_wo_v2 theo điều kiện:
• parent_wo_key is in list (1)
• wo_status khác Cancel
→ ra được list bản ghi (2)
Từ trường id của list (2) → query unit_fulfilling_order theo sale_wo_v2_id → ra được list (3)
• BU: từ trường du_id của list (2) → distinct list → mapping crm_master_data_mapping.name147 theo id (4)
• DU: từ trường group_id của list (3) → distinct list → mapping groups.group_name theo group_id (5)

Sau đó query business_plan_version theo điều kiện
• business_plan_version.project_code = project_code được create new version

145 http://revenue_plan_group.id
146 http://revenue_plan_group.id
147 http://crm_master_data_mapping.name

SRS – 302

TDX Delivery – [Dashboard phase 6] PAKD

• business_plan_version.version = version mới nhất
→ lấy ra list bản ghi business_plan_version của version trước
Query revenue_plan_group theo business_plan_version_id và thoả mãn điều kiện

• group_id = (4)
• is_sale = 1
hoặc
• group_id = (5)
• is_sale = 0
→ ra list (6)
Query revenue_plan_other_revenues theo điều kiện
• revenue_plan_group_id = revenue_plan_group.id của list (6)
→ ra list (7)
Query revenue_plan_other_revenue_detail theo điều kiện
• revenue_plan_other_revenues_id = revenue_plan_other_revenues.id148 của list (7)
→ ra list (8)

Bảng revenue_plan_group
Khi create new version, insert vào bảng revenue_plan_group dựa theo data của list (6)
Field

R
ul
e

id

P
K

148 http://revenue_plan_group.id

SRS – 303

TDX Delivery – [Dashboard phase 6] PAKD

Field
R
ul
e

busin
ess_p
lan_v
ersion
_id

t
ừ
b
us
in
es
s_
pl
a
n
_v
er
si
o
n.
id
c
ủ
a
ve
rs
io
n
b
us
in
es
s
pl
a
n
m
ới
đ
ư
ợ
c
t
ạ
o

SRS – 304

TDX Delivery – [Dashboard phase 6] PAKD

Field
R
ul
e

group
_id
N
ế
u
là
B
U
→
gr
o
u
p
_i
d
=
(4
)

N
ế
u
là
D
U
→
gr
o
u
p
_i
d
=
(5
)

SRS – 305

TDX Delivery – [Dashboard phase 6] PAKD

Field

R
ul
e

is_sal
e

N
ế
u
là
B
U
→
is
_s
al
e
=
1
N
ế
u
là
D
U
→
is
_s
al
e
=
0

Bảng revenue_plan_other_revenue
Khi create new version, insert vào bảng revenue_plan_group dựa theo data của list (7)
• revenue_plan_group_id = revenue_plan_group.id của version mới được create

Bảng revenue_plan_other_revenue_detail
Khi create new version, insert vào bảng revenue_plan_other_revenue_detail dựa theo data của list (8)
• revenue_plan_other_revenues_id = revenue_plan_other_revenue.id149 của version mới được create
3.11.4.3. Business Plan
Bảng business_plan_version
Khi create new version
planning_start_date is null
planning_end_date is null

149 http://revenue_plan_group.id

SRS – 306

TDX Delivery – [Dashboard phase 6] PAKD

4.3.14 3.12 Feature: View Labor Rate Setting

4.3.14.1 3.12.1 Access control

SRS – 307

TDX Delivery – [Dashboard phase 6] PAKD

S
y
st
e
m

P
a
r
e
n
t

Page

M
e
n
u
O
r
d
e
r

U
R
L
/
S
t
a
t
e

Activity

Permission

Role

Description

d
a
s
h
b
o
ar
d

S
e
t
ti
n
g

Labor
Rate

1
3

/
s
e
t
ti
n
g
/
d
e
li
v
e
r
y
s
e
t
ti
n
g
/
l
a
b
o
rr
a
t
e
s
e
t
ti
n
g

VIEW_LABOR_RATE_SE
TTING

VIEW_LABOR_RATE_SE
TTING

DB-FCL/
DB-ADMIN/
DB-FC

User có
activity này
sẽ thấy
được page
Labor Rate
trên giao
diện

SRS – 308

TDX Delivery – [Dashboard phase 6] PAKD

4.3.14.2 3.12.2 Use case description
Use case

View labor rate config

Precondition

Thiết bị của user phải có kết nối internet
User có permission View Labor Rate Config

Description

Post condition
User xem thông tin Labor rate config thành công

Main Flow

User: Tại màn Setting, Click vào page
Labor rate setting

Business Rule

GUI Reference
G1

System: Hiển thị màn hình Labor rate
setting

4.3.14.3 3.12.3 Graphic user interface

G1

4.3.14.4 3.12.4. GUI Element
#

Field

Descriptio
n

Contr
ol
Type

Data
Type

Defaul
t
Value

Editabl
e?

Mandat
ory?

(Y/N/
NA)

(Y/N/
NA)

Rule

Note

SRS – 309

TDX Delivery – [Dashboard phase 6] PAKD

1

Location

Label

String

NA

NA

NA

labor_rate_con
fig.location

2

Salary
Index

Label

String

NA

NA

NA

labor_rate_con
fig.salary_inde
x

3

Expense
Index

Label

String

NA

NA

NA

labor_rate_con
fig.expense_in
dex

4

Updated at

Label

String

NA

NA

NA

labor_rate_con
fig.updated_at

5

Updated by

Label

String

NA

NA

NA

labor_rate_con
fig.updated_by

6

Add

Button

NA

NA

NA

NA

7

Edit

Button

NA

NA

NA

NA

8

Delete

Button

NA

NA

NA

NA

SRS – 310

TDX Delivery – [Dashboard phase 6] PAKD

4.3.15 3.13 Feature: Add Labor Rate Setting

4.3.15.1 3.13.1 Access control

SRS – 311

TDX Delivery – [Dashboard phase 6] PAKD

S
y
st
e
m

P
a
r
e
n
t

Page

M
e
n
u
O
r
d
e
r

U
R
L
/
S
t
a
t
e

Activity

Permission

Role

Description

d
a
s
h
b
o
ar
d

S
e
t
ti
n
g

Labor
Rate

1
3

/
s
e
t
ti
n
g
/
d
e
li
v
e
r
y
s
e
t
ti
n
g
/
l
a
b
o
rr
a
t
e
s
e
t
ti
n
g

EDIT_LABOR_RATE_SET
TING

EDIT_LABOR_RATE_SET
TING

DB-FCL

Button Add,
Edit, Delete
sẽ enable
nếu user có
activity này

SRS – 312

TDX Delivery – [Dashboard phase 6] PAKD

4.3.15.2 3.13.2 Use case description
Use case

Add labor rate setting

Precondition

Thiết bị của user phải có kết nối internet
User có permission View Labor Rate Setting
User có permission Edit Labor Rate Setting

Description

Post condition
User add thông tin Labor rate config thành công

Main Flow

User: Tại màn Setting, Click vào
page Labor rate setting
System: Hiển thị màn hình Labor
rate setting

Business Rule

GUI Reference

Khi Save, lưu data vào bảng
labor_rate_config

G1

User: Click chọn button Add
System: Hiển thị popup Add
User: Input thông tin và ấn Add
System: Lưu data mới, hiển thị
thông báo thành công

4.3.15.3 3.13.3 Graphic user interface

G1

SRS – 313

TDX Delivery – [Dashboard phase 6] PAKD

4.3.15.4 3.13.4. GUI Element

#

1

Field

Descripti
on

Location

Contr
ol
Type

Dropd
own

Data
Type

String

Defaul
t
Value

Blank

Editab
le?

Mandat
ory?

(Y/N/
NA)

(Y/N/
NA)

NA

NA

Rule

Not
e

Khi click vào, Lấy
ra
list
setting_config.na
me150 thoả mãn
điều kiện sau
• setting_config
.type_id →
mapping
setting_type
theo id
→
setting_type.
name151 =
Location
• setting_config
.status = 1

2

Salary
index

Numb
eric
field

Numb
er

NA

Y

Y

User được chỉ
được nhập số,
dấu "-" và dấu
thập phân "."
Phân cách hàng
nghìn, trăm, đơn
vị bằng dấu phẩy
Max length: 30 ký
tự

150 http://setting_config.name/
151 http://setting_type.name/

SRS – 314

TDX Delivery – [Dashboard phase 6] PAKD

3

Expense
index

Numb
eric
field

Numb
er

NA

Y

Y

User được chỉ
được nhập số,
dấu "-" và dấu
thập phân "."
Phân cách hàng
nghìn, trăm, đơn
vị bằng dấu phẩy
Max length: 30 ký
tự

4

Add

Button

NA

NA

NA

NA

Khi ấn Add, lưu
giá trị vào bảng
labor_rate_config

5

Cancel

Button

NA

NA

NA

NA

Khi ấn Cancel,
đóng popup,
không lưu giá trị
user nhập

4.3.16 3.14 Feature: Edit Labor Rate Setting
4.3.16.1 3.14.1 Access control

SRS – 315

TDX Delivery – [Dashboard phase 6] PAKD

S
y
st
e
m

P
a
r
e
n
t

Page

M
e
n
u
O
r
d
e
r

U
R
L
/
S
t
a
t
e

Activity

Permission

Role

Description

d
a
s
h
b
o
ar
d

S
e
t
ti
n
g

Labor
Rate

1
3

/
s
e
t
ti
n
g
/
d
e
li
v
e
r
y
s
e
t
ti
n
g
/
l
a
b
o
rr
a
t
e
s
e
t
ti
n
g

EDIT_LABOR_RATE_SET
TING

EDIT_LABOR_RATE_SET
TING

DB-FCL

Button Add,
Edit, Delete
sẽ enable
nếu user có
activity này

SRS – 316

TDX Delivery – [Dashboard phase 6] PAKD

4.3.16.2 3.14.2 Use case description
Use case

Edit labor rate config

Precondition

Thiết bị của user phải có kết nối internet
User có permission View Labor Rate Setting
User có permission Edit Labor Rate Setting

Description

Post condition
User edit thông tin Labor rate config thành công

Main Flow

User: Tại màn Setting, Click vào
page Labor rate setting
System: Hiển thị màn hình Labor
rate setting

Business Rule

GUI Reference

Khi Save, lưu data vào bảng
labor_rate_config

G1

User: Click chọn button Edit
System: Hiển thị popup Edit
User: Input thông tin và ấn Save
System: Lưu data, hiển thị thông
báo thành công

4.3.16.3 3.14.3 Graphic user interface

G1

SRS – 317

TDX Delivery – [Dashboard phase 6] PAKD

4.3.16.4 3.14.4. GUI Element

#

1

Field

Description

Location

Contr
ol
Type

Dropd
own

Data
Typ
e

Strin
g

Defau
lt
Value

Blank

Editab
le?

Mandat
ory?

(Y/N/
NA)

(Y/N/
NA)

NA

NA

Rule

No
te

Khi click vào, Lấy
ra
list
setting_config.nam
e152 thoả mãn điều
kiện sau
• setting_config.
type_id →
mapping
setting_type
theo id
→
setting_type.n
ame153 =
Location
• setting_config.
status = 1

2

Salary index

Numb
eric
field

Num
ber

NA

Y

Y

User được chỉ
được nhập số, dấu
"-" và dấu thập
phân "."
Phân cách hàng
nghìn, trăm, đơn vị
bằng dấu phẩy
Max length: 15
phần nguyên + 2
phần thập phân

152 http://setting_config.name/
153 http://setting_type.name/

SRS – 318

TDX Delivery – [Dashboard phase 6] PAKD

3

Expense
index

Numb
eric
field

Num
ber

NA

Y

Y

User được chỉ
được nhập số, dấu
"-" và dấu thập
phân "."
Phân cách hàng
nghìn, trăm, đơn vị
bằng dấu phẩy
Max length: 15
phần nguyên + 2
phần thập phân

4

Save

Butto
n

NA

NA

NA

NA

Khi ấn Add, lưu giá
trị vào bảng
labor_rate_config

5

Cancel

Butto
n

NA

NA

NA

NA

Khi ấn Cancel,
đóng popup,
không lưu giá trị
user nhập

4.3.17 3.15 Feature: Delete Labor Rate Setting
4.3.17.1 3.15.1 Access control

SRS – 319

TDX Delivery – [Dashboard phase 6] PAKD

S
y
st
e
m

P
a
r
e
n
t

Page

M
e
n
u
O
r
d
e
r

U
R
L
/
S
t
a
t
e

Activity

Permission

Role

Description

d
a
s
h
b
o
ar
d

S
e
t
ti
n
g

Labor
Rate

1
3

/
s
e
t
ti
n
g
/
d
e
li
v
e
r
y
s
e
t
ti
n
g
/
l
a
b
o
rr
a
t
e
s
e
t
ti
n
g

EDIT_LABOR_RATE_SET
TING

EDIT_LABOR_RATE_SET
TING

DB-FCL

Button Add,
Edit, Delete
sẽ enable
nếu user có
activity này

SRS – 320

TDX Delivery – [Dashboard phase 6] PAKD

4.3.17.2 3.15.2 Use case description
Use case

Delete labor rate config

Precondition

Thiết bị của user phải có kết nối internet
User có permission View Labor Rate Setting
User có permission Edit Labor Rate Setting

Description

Post condition
User Delete thông tin Labor rate config thành công

Main Flow

User: Tại màn Setting, Click vào
page Labor rate setting
System: Hiển thị màn hình Labor
rate setting

Business Rule

GUI Reference

Khi delete, xoá data trong bảng
labor_rate_config

G1

User: Click chọn button Edit
System: Hiển thị tooltip confirm
User: Ấn Yes
System: Xoá data, hiển thị thông
báo thành công

SRS – 321

TDX Delivery – [Dashboard phase 6] PAKD

4.3.17.3 3.15.3 Graphic user interface

G1

4.3.17.4 3.15.4. GUI Element
#

Field

Descripti
on

Contro
l Type

Data
Type

Defaul
t
Value

Editabl
e?

Mandat
ory?

(Y/N/
NA)

(Y/N/
NA)

1

Delete

Button

NA

NA

NA

NA

2

Yes

Confir
mation
button

NA

NA

NA

NA

3

No

Confir
mation
button

NA

NA

NA

NA

Rule

Note

Khi click vào,
đóng tooltip
confirm

SRS – 322

TDX Delivery – [Dashboard phase 6] PAKD

5 System Flow

System Flow – 323

---

<a id="28--guide-for-ams--dashboard---ot-validation--1--pptx"></a>
## 28. [Guide for AMS] Dashboard - OT Validation (1).pptx

## Slide 1

Dashboard Phase 20– Module OT Validation

May 2026

User Guide_Admin / Glead/ DUL/ PM

## Slide 2

Nội dung

02

01

Tổng quan module Module OT Validation

Hướng dẫn thao tác trên hệ thống Dashboard

03

Q&A

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE OT Validation

1.2. Tổng quan workflow module Module OT Validation

## Slide 4

1.1

MỤC ĐÍCH

Mục đích: Tự động hóa quy trình log & phê duyệt OT cho PM
Cải tiến phương thức điền Overtime Expenses trên PAKD
Thêm nghiệp vụ xác thực quỹ OT theo PAKD
Thay đổi logic lưu dữ liệu Actual Timesheet theo HRMS
Người sử dụng: 
PM allocate OT cho CBNV
CBNV log OT theo ngân sách cho phép 

## Slide 5

1.2

Quy trình hiện tại

## Slide 6

1.3

Quy trình trình sau chỉnh sửa

## Slide 7

1.4

Đối tượng người dùng & phân quyền

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được quyền thao tác toàn bộ trên hệ thống
DUL
Được quyền điền quỹ OT trong PAKD
DUL, PM
Được quyền allocate OT cho nhân sự trong dự án

## Slide 8

Quy trình điền & allocate OT theo quỹ PAKD

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

Tự động hóa quy trình phê duyệt OT cho PM

Phần phát triển mới: Bổ sung quy trình allocate OT

## Slide 9

2.1.1

Thay đổi phương thức điền OT - PAKD

Đối tượng sử dụng
DUL, preparator
Mục đích:
Cho phép DUL, preparator điền chi phí OT theo MM

## Slide 10

Công thức quy đổi chi phí OT từ MM sang VND:
Giá trị OT Cost (VND) = Trung bình cộng Employee Cost VND tại Resources Information x Số MM OT đã điền
Ví dụ: Dự án có 3 nhân sự với thông tin như ảnh
-> Đơn giá 1 MM chi phí OT = 22,666,667 VND

2.1.2

Thay đổi Allocate OT

Hiện trạng
Chỉnh sửa
Điền chi phí OT theo tiền VND
- Điền OT theo MM tại tab Head Count
- Hệ thống tự quy đổi sang tiền VND tại tab OT Cost

## Slide 11

2.1.3

Hướng dẫn truy cập

B1: Truy cập Delivery -> Business Plan -> Chọn Business Plan -> View Detail

## Slide 12

2.1.3

Hướng dẫn truy cập

B2: Truy cập Delivery Plan, chọn DU 

## Slide 13

2.1.3

Hướng dẫn truy cập

B3: Tại Delivery Plan, mở tab Overtime
Người dùng có thể switch sang tab OT Cost để xem chi phí theo tiền VND

Lưu ý: Luồng submit, phê duyệt PAKD giữ nguyên như hiện tại, không có sự thay đổi

## Slide 14

2.2.1

Validate OT

Đối tượng sử dụng
DUL, PM, PMO
Mục đích:
Cho phép DUL, PM, PMO nhận biết quỹ OT còn lại của dự án
Thực hiện allocate OT theo ngân sách PAKD

## Slide 15

2.2.2

Bảng thông tin OT plan

B1: User truy cập vào thông tin dự án
B2: View bảng thông tin WT & OT trên màn hình

## Slide 16

2.2.3

Quy định allocate OT

Rule kiểm tra 3 tầng:
Remaining hours phải lớn hơn 0
Đã điền quỹ chi phí OT cho tháng tương ứng trong PAKD mới nhất được approved
Quỹ OT tính riêng cho từng DU, chỉ có thể dùng để allocate nhân sự thuộc DU đó
-> Nếu thiếu bất kỳ điều kiện nào, hệ thống không cho phép allocate OT

## Slide 17

2.2.4

Quy định allocate OT

Công thức tính Current Allocated:
1. Nếu là ngày quá khứ (Tính từ ngày hiện tại -1) -> Lấy Actual Timesheet, gồm cả status Pending lẫn Approved
2. Nếu là ngày hiện tại – tương lai - > Lấy Project Resources
Cộng 1 và 2 lại ra Current allocated
Q&A: Nếu nhân sự chuyển DU thì tháng đó tính current allocate vào DU nào?
-> Hệ thống tách thời gian chính xác theo History Department và phân bổ allocate đúng theo từng DU mà nhân sự làm việc

## Slide 18

2.3.1

Tự động hóa quy trình phê duyệt OT

Đối tượng sử dụng
PM, DUL
User
Mục đích:
Tự động hóa step phê duyệt request OT với đối tượng PM

## Slide 19

2.3.3

Cơ chế tự động hóa quy trình OT

## Slide 20

2.3.4

So sánh thay đổi cơ chế Actual Timesheet

Item
Hiện trạng
Thay đổi
Dữ liệu Timesheet
- Đồng bộ từ Project Resources
Đồng bộ dữ liệu từ HRMS
Logic approve Timesheet
- Approve cả WT & OT
Chỉ approve WT
Edit bản ghi allocate OT Project Resources
- Edit không giới hạn
- Nếu Actual Timesheet đã approve, chỉ cho phép edit giảm tối thiểu bằng dữ liệu Actual Timesheet
Remaining hours
- Chưa có
- Tính chính xác theo Actual Timesheet

Lưu ý:
Thay đổi chỉ áp dụng cho allocate OT, không ảnh hưởng đến allocate WT hiện tại
Remaining hours tính toán chính xác theo OT log tại HRMS. Trường hợp PM allocate nhưng member không log hết/checkin-out thiếu giờ, PM có thể dùng số giờ dư để allocate tiếp cho nhân sự khác 

## Slide 21

2.3.5

Ảnh hưởng đến member log OT HRMS

Hiện trạng
Chỉnh sửa
Member được phép log request OT không giới hạn
Member chỉ được phép log request OT theo ngân sách tối đa PM đã allocate tại Dashboard

## Slide 22

2.3.6

Cơ chế ghi đè Actual Timesheet

Cơ chế ghi đè Actual Timesheet:
HRMS gửi số tổng OT request khớp với 4 thông tin (ldap, ot date, project id, mvv) sang Dashboard. Mỗi lần số tổng thay đổi (Ticket OT bị cancel, xuất hiện OT pay,..) đều trigger gọi lại Dashboard
Dashboard nhận được thông tin, xử lý như sau:
Tìm tất cả bản ghi Actual Timesheet khớp với thông tin trên. Fill số vào các bản ghi sao cho total value Actual timesheet = số HRMS gửi sang
Bản ghi Actual Timesheet được fill theo điều kiện: 
+ Không được vượt giới hạn value của bản ghi Project Resources tương ứng
+ Nếu chạm giới hạn, tìm đến bản ghi Actual Timesheet tiếp theo chưa fill đầy để fill tiếp

## Slide 23

2.3.7

Bảng mã API ghi đè Actual Timesheet (API save-ot)

Msg
Mô tả
Xử lý
OT request exceeds budget limit
Request OT vượt Project Resource
Member báo PM allocate thêm
Your OT request would exceed Remaining hours of MVV & DU. Please contact PM to review OT plan
Request OT vượt budget còn lại của DU
PM báo PIC của DU làm lại PAKD để thêm budget
Each value in projectCodeList must exist at least 1 record in Project Resources
Hệ thống không tìm thấy bản ghi tương ứng của Project Resources tại Actual Timesheet
Workaround: Xóa allocate đi allocate lại
[Param] Invalid/Not Found
Hệ thống không tìm thấy bản ghi nào khớp với thông tin Request
L2 check
No OT plan found for this group id null
Lỗi data group id của user = null
Insert lại group_id cho nhân sự trong DB

## Slide 24

2.3.8

Bảng mã Project Resources 

Msg
Mô tả
Xử lý
You cannot reduce OT plan to less than  Actual timesheet hours.
Bản ghi bên Actual Timesheet đã approve, không được giảm Project Resource xuống thấp hơn Actual Timesheet
An OT request has already been logged on HRMS
Bản ghi bên Actual Timesheet đã approve và value > 0. Không được phép xóa Project Resources
PM có thể báo member cancel hết request OT của MVV trong ngày đó -> Sau đó mới được xóa allocation

## Slide 25

2.3.9

Bảng mã Actual Timesheet

Msg
Mô tả
Xử lý
N/A
Không approve được OT
Rule mới, user không được approve OT, hệ thống tự xử lý
N/A
Không edit được OT
Rule mới, user không được edit OT

## Slide 26

03

Q&A

## Slide 27

---

<a id="29--guide--dashboard---ot-validation-pptx"></a>
## 29. [Guide] Dashboard - OT Validation.pptx

## Slide 1

Dashboard Phase 20– Module OT Validation

May 2026

User Guide_Admin / Glead/ DUL/ PM

## Slide 2

Nội dung

02

01

Tổng quan module Module OT Validation

Hướng dẫn thao tác trên hệ thống Dashboard

03

Q&A

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE OT Validation

1.2. Tổng quan workflow module Module OT Validation

## Slide 4

1.1

MỤC ĐÍCH

Mục đích: Tự động hóa quy trình log & phê duyệt OT cho PM
Cải tiến phương thức điền Overtime Expenses trên PAKD
Thêm nghiệp vụ xác thực quỹ OT theo PAKD
Thay đổi logic lưu dữ liệu Actual Timesheet theo HRMS
Người sử dụng: 
PM allocate OT cho CBNV
CBNV log OT theo ngân sách cho phép 

## Slide 5

1.2

Quy trình hiện tại

## Slide 6

1.3

Quy trình trình sau chỉnh sửa

## Slide 7

1.4

Đối tượng người dùng & phân quyền

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được quyền thao tác toàn bộ trên hệ thống
DUL
Được quyền điền quỹ OT trong PAKD
DUL, PM
Được quyền allocate OT cho nhân sự trong dự án

## Slide 8

Quy trình điền & allocate OT theo quỹ PAKD

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

Tự động hóa quy trình phê duyệt OT cho PM

Phần phát triển mới: Bổ sung quy trình allocate OT

## Slide 9

2.1.1

Thay đổi phương thức điền OT - PAKD

Đối tượng sử dụng
DUL, preparator
Mục đích:
Cho phép DUL, preparator điền chi phí OT theo MM

## Slide 10

Công thức quy đổi chi phí OT từ MM sang VND:
Giá trị OT Cost (VND) = Trung bình cộng Employee Cost VND tại Resources Information x Số MM OT đã điền
Ví dụ: Dự án có 3 nhân sự với thông tin như ảnh
-> Đơn giá 1 MM chi phí OT = 22,666,667 VND

2.1.2

Thay đổi Allocate OT

Hiện trạng
Chỉnh sửa
Điền chi phí OT theo tiền VND
- Điền OT theo MM tại tab Head Count
- Hệ thống tự quy đổi sang tiền VND tại tab OT Cost

## Slide 11

2.1.3

Hướng dẫn truy cập

B1: Truy cập Delivery -> Business Plan -> Chọn Business Plan -> View Detail

## Slide 12

2.1.3

Hướng dẫn truy cập

B2: Truy cập Delivery Plan, chọn DU 

## Slide 13

2.1.3

Hướng dẫn truy cập

B3: Tại Delivery Plan, mở tab Overtime
Người dùng có thể switch sang tab OT Cost để xem chi phí theo tiền VND

Lưu ý: Luồng submit, phê duyệt PAKD giữ nguyên như hiện tại, không có sự thay đổi

## Slide 14

2.2.1

Validate OT

Đối tượng sử dụng
DUL, PM, PMO
Mục đích:
Cho phép DUL, PM, PMO nhận biết quỹ OT còn lại của dự án
Thực hiện allocate OT theo ngân sách PAKD

## Slide 15

2.2.2

Bảng thông tin OT plan

B1: User truy cập vào thông tin dự án
B2: View bảng thông tin WT & OT trên màn hình

## Slide 16

2.2.3

Quy định allocate OT

Rule kiểm tra 3 tầng:
Remaining hours phải lớn hơn 0
Đã điền quỹ chi phí OT cho tháng tương ứng trong PAKD mới nhất được approved
Quỹ OT tính riêng cho từng DU, chỉ có thể dùng để allocate nhân sự thuộc DU đó
-> Nếu thiếu bất kỳ điều kiện nào, hệ thống không cho phép allocate OT

## Slide 17

2.3.1

Tự động hóa quy trình phê duyệt OT

Đối tượng sử dụng
PM, DUL
User
Mục đích:
Tự động hóa step phê duyệt request OT với đối tượng PM

## Slide 18

2.3.2

Thay đổi quy trình phê duyệt OT cho PM

Hiện trạng
Chỉnh sửa
PM allocate trên Project Resources 
PM phê duyệt trên Actual Timesheet & HRMS
PM chỉ thực hiện allocate trên Project Resources

## Slide 19

2.3.3

Cơ chế tự động hóa quy trình OT

## Slide 20

2.3.4

So sánh thay đổi cơ chế Actual Timesheet

Item
Hiện trạng
Thay đổi
Dữ liệu Timesheet
- Đồng bộ từ Project Resources
Đồng bộ dữ liệu từ HRMS
Logic approve Timesheet
- Approve cả WT & OT
Chỉ approve WT
Edit bản ghi allocate OT Project Resources
- Edit không giới hạn
- Nếu Actual Timesheet đã approve, chỉ cho phép edit giảm tối thiểu bằng dữ liệu Actual Timesheet
Remaining hours
- Chưa có
- Tính chính xác theo Actual Timesheet

Lưu ý:
Thay đổi chỉ áp dụng cho allocate OT, không ảnh hưởng đến allocate WT hiện tại
Remaining hours tính toán chính xác theo OT log tại HRMS. Trường hợp PM allocate nhưng member không log hết/checkin-out thiếu giờ, PM có thể dùng số giờ dư để allocate tiếp cho nhân sự khác 

## Slide 21

2.3.2

Ảnh hưởng đến member log OT HRMS

Hiện trạng
Chỉnh sửa
Member được phép log request OT không giới hạn
Member chỉ được phép log request OT theo ngân sách tối đa PM đã allocate tại Dashboard

## Slide 22

03

Q&A

## Slide 23

---

<a id="30--user-guide--p19--số-hóa-cm-plan--1--1-pptx"></a>
## 30. [User Guide] P19_ Số Hóa CM Plan (1) 1.pptx

## Slide 1

Dashboard – Số Hóa CM Plan

May 2026

User Guide: Admin / PM / IT

## Slide 2

Nội dung

04

02

01

Mục tiêu & Phạm vi dự án

Hướng dẫn thao tác trên hệ thống Dashboard

Q&A

03

Cải thiện các mô-đun khác (nếu có)

## Slide 3

🞜Chuyển đổi số CM Plan: Thay thế file Excel truyền thống bằng hệ thống Dashboard tập trung.

01

Mục Tiêu & Phạm Vi Dự Án

🞜Quản lý CIs: Kiểm soát hiệu quả các thành phần cấu hình các liên kết giữa các loại kết nối trong dự án 

🞜Thiết lập quan hệ: Tạo mối liên kết giữa các CIs để theo dõi các cấu hình liên kết của dự án 

🞜Thiết lập quan hệ: Tạo mối liên kết giữa các CIs để theo dõi các cấu hình liên kết của dự án 

## Slide 4

1.1

Đối tượng sử dụng

Vai Trò
Quyền Hạn Chính
Admin
Toàn quyền quản trị hệ thống và quản lý các master data.
IT
Phê duyệt/Từ chối yêu cầu (Approve/Reject) trên Dashboard (màn hình CIs Relationship Request).
PM
Quản lý các CIs, tạo CIs Relationship, gửi request & tạo ticket CM Plan bên hệ thống Jira CRM

## Slide 5

1.2

Tổng quan workflow số hóa CM Plan (Cũ)

* Ghi chú: Nếu IT Approve/Reject tại bước 4 ,trên ticket Jira, tất cả các relationship (connection) tại dashboard  thuộc ticket được tạo trên Jira sẽ chuyển trạng thái từ Pending -> Approve/Reject

PM Tạo/Update file excel CM Plan  

PM tạo CM plan trên Jira (SUB ticket)

Các phòng ban liên quan review và approve CM Plan và tạo ticket SC tới IT

IT cập nhật CM plan đã được phê duyệt

Hoàn tất cấu hình theo ticket đã được tạo trên Jira

Thao tác của PM

Hệ thống tự động

Thao tác của IT

OLD

## Slide 6

1.2

Tổng quan workflow số hóa CM Plan (Mới)

* Ghi chú: Nếu IT Approve/Reject tại bước 4 ,trên ticket Jira, tất cả các relationship (connection) tại dashboard  thuộc ticket được tạo trên Jira sẽ chuyển trạng thái từ Pending -> Approve/Reject

Khởi tạo CI & Relationship

Tạo Request to Jira (Send)

Tạo Parent Ticket trên Jira

IT Duyệt trên Dashboard (hoặc Jira)

Hoàn tất cấu hình theo ticket đã được tạo trên Jira

Thao tác của PM

Hệ thống tự động

Thao tác của IT

New

## Slide 7

1. Dashboard: CIs Overview

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

2. Quản lý danh sách (CIs)

4. Quản lý quan hệ Relationships (CIs Relationship)

5. Tạo nhiều Relationships (Add Bulk)

Giao diện tổng quan và thao tác trên hệ thống

3. Quy trình tạo mới CIs (Create New CI)

6. Quản lý CIs request (CIs Relationship Request)

7. Tạo Request to Jira

8. IT Phê duyệt ticket CM Plan

## Slide 8

2.1

Dashboard: CIs Overview

Đối tượng sử dụng
Admin
PM
IT
Mục đích:
Giao diện tổng quan cho phép PM theo dõi các thông tin cơ bản của hệ thống CM Plan trong dự án

* Lưu ý: User có thể click trực tiếp vào các con số để chuyển hướng đến màn hình chi tiết tương ứng.

15

Total CIs

15

Total CIs Relationship

15

Total CIs Relationship (Active)

15

Total CIs Requested (Rejected)

## Slide 9

2.1.1

Dashboard: CIs Overview (UI)

## Slide 10

2.2

2. Quản lý danh sách CIs

Đối tượng sử dụng
Admin
PM
IT
Mục đích:
Tìm kiếm nâng cao: Lọc theo tên CI, loại CI hoặc người cập nhật.
Xoá CI: Chỉ cho phép xoá nếu CI chưa có relationship active hoặc đang gửi sang Jira (tham khảo Master Data, sheet mapping)
Chỉnh sửa: Cập nhật thông tin CIs (nếu có)

## Slide 11

2.2.1

2. Quản lý danh sách CIs (UI)

## Slide 12

2.3

Quy trình tạo mới CI

Đối tượng sử dụng
Admin
PM
Mục đích:
Cho phép PM tạo mới CIs
Lưu ý: hệ thống sẽ tự động kiểm tra tính trùng lặp dựa trên Primary Key và Key Information. Nếu CI đó đã tồn tại thì PM sẽ không thể tạo được CI mới

Chọn CI Type

Nhập Attributes

Add Relationship

Tạo (Create)

## Slide 13

2.3.2

Tạo mới CI (UI)

## Slide 14

2.3.2

Tạo mới CI (UI)

## Slide 15

2.4

Quản Lý Quan Hệ (CIs Relationship)

Đối tượng sử dụng
Admin
PM
IT
Mục đích:
Các loại tìm kiếm nâng cao: Lọc theo tên CI, loại CI, trạng thái (status), loại access
Quản lý các thông tin của các thành phần cấu hình kết nối với nhau (vd: Member Connects to Server).

## Slide 16

2.4.2

Quản Lý Quan Hệ CIs Relationship (UI)

## Slide 17

2.5

Tạo nhiều Relationships (CIs Relationship/Add Bulk)

Đối tượng sử dụng
Admin
PM
Mục đích:
Cho phép PM tạo nhiều relationship (connection) giữa các CIs cùng một lúc
PM có thể preview và biết số lượng relationship sẽ được tạo 

Chọn CI Type

Select Source CIs và Destination CIs

Add Relationship

Kiểm tra và tạo nhiều relationship (Connection)

## Slide 18

2.5.1

Tạo nhiều Relationships (CIs Relationship/Add Bulk UI)

## Slide 19

2.5.1

Tạo nhiều Relationships (CIs Relationship/Add Bulk UI)

B1: Tại màn CIs Relationship, PM click “Add bulk Relationship
B2: Chọn Source CI Type & Destination CI
B3: Chọn một hoặc nhiều Source CIs và Destination CIs
B4: Chọn loại quan hệ Relationship và nhập thời gian apply - start date 
B5: Review
B6: Click Add để tạo relationship

## Slide 20

2.6

Quản Lý CI Request (CIs Request)

Đối tượng sử dụng
Admin
PM
IT
Mục đích:
Đối với PM: là nơi PM  gửi yêu cầu sang Jira và quản lý yêu cầu kết nối relationship giữa các CIs
Tạo và gửi yêu cầu (Request to Jira)
Đối với Role IT (Người phê duyệt): là nơi IT tiếp nhận và ra quyết định phê duyệt các yêu cầu cấu hình.
 Xử lý và phản hồi phê duyệt bằng cách phê duyệt ticket Task trên Jira hoặc truy cập hệ thống Dashboard phê duyệt theo từng relationship

## Slide 21

2.7

PM Tạo Request to Jira (CIs Request) 

## Slide 22

2.7.1

PM Tạo Request to Jira (CIs Request) 

B1: PM click button Request To Jira
B2: Chọn Issue Type sẽ được tạo bên Jira 
B3: Chọn Access Type (Allow/Deny)
B4: Chọn Relationship đã thiết lập (trạng thái Draft)
B5: Tải lên ảnh chứng minh (nếu có)
B6: Click Send to Jira để tạo ticket CM plan (có thể mất vài phút)

## Slide 23

2.8.1

IT Phê Duyệt Ticket CM Plan

B1: IT truy cập hệ thống Jira
B2: Truy cập ticket CM Plan của dự án
B3: Review các thông tin CM Plan
B4: Đổi trạng thái ticket CM Plan sang Done
B5: Hệ thống Dashboard đổi trạng thái approval status của relationship từ Pending -> Approved

## Slide 24

2.8.1

IT Phê Duyệt Relationship Đơn

B1: Tại ticket CM Plan, click Dashboard URL để truy cập hệ thống Dashboard
B2: Tại màn CIs Request, IT chọn 1 hoặc nhiều relationship để phê duyệt (Approve/Reject)
B3: Hệ thống Dashboard đổi trạng thái relationship từ Pending -> Approved hoặc Rejected

## Slide 25

03

Q&A

## Slide 26

---

<a id="31--user-guide--p21--số-hóa-cm-plan--1--pptx"></a>
## 31. [User Guide] P21_ Số Hóa CM Plan (1).pptx

## Slide 1

Dashboard – Số Hóa CM Plan

July 2026

User Guide: Admin / PM / IT

## Slide 2

Nội dung

04

02

01

Mục tiêu & Phạm vi dự án

Hướng dẫn thao tác trên hệ thống Dashboard

Q&A

03

Cải thiện các mô-đun khác (nếu có)

## Slide 3

🞜 Quản trị CMPLAN phạm vi công ty:  Cho phép Admin/ IT quản trị toàn bộ thông tin CMPLAN của tất cả dự án.

01

Mục Tiêu & Phạm Vi Dự Án

🞜 Quản lý Group(CIs): Tính năng gom nhóm cho CIs

🞜 Số hóa sheet Baseline và Working Enviroment: Tạo mới và quản lý mối liên kết giữa các CIs

🞜 Các luồng tự động tạo request: 
 + Khi user đến ngày remove khỏi dự án
 + Khi start date, end date của dự án được cập nhật
 + Ngắt kết nối của user khi member nghỉ việc
🞜 Enhance một số tính năng khác: Create bulk CI, Sửa nhiều relationship

## Slide 4

1.1

Đối tượng sử dụng

Vai Trò
Quyền Hạn Chính
Admin
Toàn quyền quản trị hệ thống và quản lý các master data.
PM
Quản lý các CIs, Group CIs, tạo CIs Relationship, gửi request phê duyệt tại hệ thống Dashboard ...
IT
Phê duyệt/Từ chối yêu cầu (Approve/Reject) trên Dashboard Hoặc Jira
SEPG, QA
Chỉ có quyền view các module của CMPLAN và thực hiện audit nếu cần thiết

## Slide 5

1.2

Tổng quan workflow số hóa CM Plan hiện tại

* Ghi chú: Nếu IT Approve/Reject tại bước 4 ,trên ticket Jira, tất cả các relationship (connection) tại dashboard  thuộc ticket được tạo trên Jira sẽ chuyển trạng thái từ Pending -> Approve/Reject

Khởi tạo CI & Relationship

Tạo Request to Jira (Send)

Tạo Parent Ticket trên Jira

IT kiểm tra và phê duyệt yêu cầu kết nối trên Jira

Hoàn tất cấu hình theo ticket đã được tạo trên Jira

Thao tác của PM

Hệ thống tự động

Thao tác của IT

New

## Slide 6

1.3

Tổng quan workflow số hóa CM Plan (Mới)

New

Khó khăn
Cải tiến
Mất thời gian khi tạo khi dự án có nhiều CI 
Cho phép thêm, sửa, xóa nhiều CI đồng thời trên cùng 1 màn hình với giao diện quen thuộc các sheet file excel
Khi tạo quan hệ khó khăn trong việc tìm CI muốn kết nối
Tạo nhóm cho CI, dễ dàng tạo quan hệ khi số lượng CI tăng lên
Mất thời gian khi chỉnh sửa expired date của relationship
+ Cho phép edit nhiều relationship
+ Tự động tạo request để gia hạn expired date của relationship khi end date của dự án được gia hạn
Phải tạo nhiều CI member với dự án nhiều thành viên
Tự động tạo CI member ngay khi add user vào dự án

Đối với PM

## Slide 7

1.4

Tổng quan workflow số hóa CM Plan (Mới)

New

Đối với IT

Khó khăn
Cải tiến
Khó quản lý trong việc kiểm soát kết nối với toàn bộ dự án
Thêm màn hình quản trị thông tin CMPlan cho tất cả dự án, có thể xuất excel
Khó khăn trong việc hiển thị nội dung file Excel thông tin đính kèm ticket 
Gom nhóm thông tin trong file excel cho các request gửi sang Jira
Lặp lại thao tác với các relationship đã được duyệt và chỉ gia hạn
Khi ticket chỉ gồm request gia hạn, hệ thống tự động duyệt, tự xuất file
Sang ticket Jira
Màn hình đang nhiều thông tin khó quan sát
Đơn giản hóa giao diện

## Slide 8

 1. CM Plan

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

2. Quản lý danh sách (CI Inventory)

5. Quản lý quan hệ Relationships (CI Relationship)

Giao diện tổng quan và thao tác trên hệ thống

3. Tạo mới CIs (Edit Bulk CI)

6. Quản lý Request (Configuration Request)

7. Quản lý workflow phê duyệt ticket CM Plan

4. Quản lý group CI (Group Management)

## Slide 9

 8. Số hóa sheet Baseline

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

9. Số hóa sheet Working Enviroment

3. Quản lý Request (Global Config Request)

Giao diện tổng quan và thao tác trên hệ thống

1. Quản lý CIs ( Global CI Inventory)  

2. Quản lý Relationships (Global CI Relationship)

Phần quản trị cho admin / IT

12. Luồng tự động remove relationship khi remove user

14. Tự động gia hạn relationship khi thời gian dự án thay đổi

## Slide 10

2.1

Dashboard: Configuration Management

Đối tượng sử dụng
Admin
PM
IT, SEPG, QA
Mục đích:
Giao diện tổng quan cho phép User theo dõi các thông tin cơ bản của hệ thống CM Plan trong dự án

* Lưu ý: User có thể click trực tiếp vào các con số để chuyển hướng đến màn hình chi tiết tương ứng.

7

Total CIs

8

Total CIs Relationship

9

Total CIs Relationship (Active)

10

Total CIs Requested (Rejected)

2

 Relationship expired in next 7 days

## Slide 11

2.1.1

Dashboard: Configuration Management

## Slide 12

2.1.2

Dashboard: Configuration Management

Menu Configuration Management cập nhật/ thay đổi

Hiện tại

Thay đổi

## Slide 13

2.1.2

Dashboard: Configuration Management

Nhấn vào link ở con số tương ứng để chuyển tới màn tương ứng

## Slide 14

2.2

Quản lý danh sách CIs

Đối tượng sử dụng
Admin: Có toàn quyền trên màn
PM: Có toàn quyền trên màn
IT, SEPG, QA: Chỉ có quyền view danh sách, export
Mục đích:
Tìm kiếm nâng cao: Lọc theo tên CI, loại CI, người cập nhật, group CI.
Xoá CI: Chỉ cho phép xoá nếu CI chưa có relationship active hoặc đang gửi sang Jira (tham khảo Master Data, sheet mapping)
Chỉnh sửa: Cập nhật thông tin CIs (nếu có)
Xem thông tin chi tiết của CI
Xuất báo cáo: Xuất thông tin dưới dạng file Excel

## Slide 15

2.2.1

Quản lý danh sách CIs (UI)

## Slide 16

2.2.2

Chi tiết CI

Trên màn danh sách CI, chọn button View Detail tương ứng với CI muốn xem

## Slide 17

2.3

Quy trình bulk create/ edit CI

Đối tượng sử dụng
Admin
PM
Mục đích:
Cho phép User tạo mới, duplicate, cập nhật, xóa cùng lúc nhiều CIs, hỗ trợ PM giảm thời gian thao tác trên từng CI
Lưu ý: hệ thống sẽ tự động kiểm tra tính trùng lặp dựa trên Primary Key và Key Information. Nếu CI đó đã tồn tại thì PM sẽ không thể tạo được CI mới

Chọn CI Type

Nhập Attributes

Thêm, sửa xóa dòng

Save {number} CIs

## Slide 18

2.3.1

Edit Bulk CIs

Trên giao diện chọn icon edit

## Slide 19

2.3.1

Edit Bulk CIs

+ Có ghi chú với mỗi CI Type thì các thuộc tính không được giống nhau hoàn toàn giữa các CI
+ Có thể duplicate CI để nhanh chóng tạo
+ Trên màn này bạn có thể thêm sửa xóa CIs
+ Khi sửa CI thì ô thuộc tính sẽ hiển thị nền xanh, giúp người dùng phân biệt dễ hơn
+ Mặc định checkbox "Create another CI Type" được tick để user có thể tạo liên tiếp  các CI Type khác nhau mà không bị chuyển sang màn list CI Inventory

## Slide 20

2.3.1

Edit Bulk CIs

Một số CI Type đặc biệt
CI Type member
+ Được sinh ra tự động khi add member vào dự án trong màn Project Member
+ Không cho phép xóa CI member
+ Giá trị của cột Role thuộc CI Type member mặc định sẽ trống, không lấy giá trị role khi add member, PM có thể bổ sung tại màn Edit bulk CI
2.    CI Type Computer
 TH1: Khi tạo CI computer, nếu chọn Operation System ngoài 2 giá trị: Windows (trắng), Mac (trắng)
+ Hê thống tự động tạo relationship và request tương ứng với CI member qua giá trị Ldap, trạng thái của relationship, request được duyệt tự động
+ Hệ thống tự động tạo relatitonship  và request tương ứng  với CI type CMC Default Software, trạng thái của relationship, request được duyệt tự động
+ Do có tạo relationship tự động và ở trạng thái được duyệt nên không thể sửa một số thuộc tính, không cho remove
 TH2: Khi tạo CI computer, nếu chọn Operation System là 1 trong 2 giá trị: Windows (trắng), Mac (trắng)
+ Hê thống tự động tạo relationship và request tương ứng với CI member qua giá trị Ldap, trạng thái của relationship, request chờ đẩy sang bên Jira
+ Request đang chờ duyệt nên có thể xóa hoặc sửa thuộc tính, riêng thuộc tính Operation System là không cho sửa
 

## Slide 21

2.3.2

Luồng request máy trắng

B1: Truy cập màn Edit Bulk CI
B2: Chọn CI Type Computer
B3: Điền thông tin các thuộc tính theo mẫu sau
+ IP Address: Request máy trắng
+ Ldap: Chọn member cần cấp máy trắng
+ Computer Name: Request máy trắng
+ Operation System: Windows (trắng) hoặc Mac (trắng)
B4: Hệ thống tự động tạo relationship request cho member request máy trắng
B5: Ở màn hình Configuration Request, PM chọn request máy trắng đã tự động tạo -> Chọn Request to Jira 

## Slide 22

2.3.3

Export CIs

## Slide 23

2.4

Quản lý group CI (Group Management)

Đối tượng sử dụng
Admin: Có toàn quyền trên trang
PM: Có toàn quyền trên trang
IT, SEPG, QA: Chỉ xem được danh sách và chi tiết group
Mục đích:
Hỗ trợ PM gom nhóm các CI theo dạng group để dễ dàng tạo mối liên kết giữa các group, giảm thời gian tìm kiếm CI đơn lẻ, cụ thể bao gồm các tính năng sau
Danh sách group: Danh sách group, Bộ lọc theo tên group, Người chỉnh sửa
Chi tiết Group: Danh sách Cis của Group, lọc theo CI và CI Type
Tạo mới group: Tạo mới group, add Cis thuộc nhiều CI type vào group

## Slide 24

2.4.1

Danh sách group CIs

## Slide 25

2.4.2

Chi tiết group CI

Tại màn danh sách nhóm CI, ấn button View Detail tương ứng với group cần xem chi tiết

## Slide 26

2.4.3

Edit group CI

Tại màn chi tiết group nhấn button edit để thực hiện sửa thông tin group

## Slide 27

2.4.4

Edit group CI

+ Sửa tên group và mô tả
+ Chọn danh sách CI trong mục Available CIs và chọn button mũi tên phải để thêm CI cho group 
+ Nếu bỏ chọn CI cho group thì chọn CI trong mục Selected CIs 
và chọn button mũi tên trái
+ Ấn button create để lưu thông tin

## Slide 28

2.4.5

Tạo mới Group CI

Tại màn hình danh sách nhấn button Add Group CI để vào màn hình tạo nhóm cho CI

## Slide 29

2.4.6

Tạo mới Group CI

B1: Nhận tên group và mô tả
B2: Chọn CI type để hiển thị danh sách CI bên mục Available CIs
B3: Chọn danh sách CI trong mục Available CIs và chọn mũi tên phải để lựa chọn CI cho group 
B4: Nếu bỏ chọn CI cho group thì chọn CI trong mục Selected CIs và chọn mũi tên trái
B5: Ấn button create để lưu thông tin

Checkbox : "Create another after this" để sau khi  ấn Create thì màn hình này không chuyển sang màn list

## Slide 30

2.5

Quản Lý Quan Hệ (CIs Relationship)

Đối tượng sử dụng
Admin: Toàn quyền trên màn hình
PM: Toàn quyền trên màn hình
IT, SEPG, QA: Chỉ có quyền xem danh sách, export, cấu hình hiển thị cột
Mục đích:
Cho phép PM chỉnh sửa, xoá nhiều relationship cùng thời điểm, giảm thời gian thao tác trên từng relationship
Các tính năng chính
Các loại tìm kiếm nâng cao: Lọc theo Relationship Key, tên CI nguồn/đích, loại CI nguồn/đích, trạng thái (status), thời gian hết hạn , cấu hình ẩn/hiển thị cột
Chi tiết relationship
Export danh sách relationship
Tạo nhiều relationship: chọn CI theo CI type, group CI
Chỉnh sửa và xoá nhiều CIs Relationships cùng lúc

## Slide 31

2.5.1

Quản Lý Quan Hệ CIs Relationship

Mặc định màn list sẽ không hiển thi các relationship mặc định là quan hệ với CI Type CMC Default Software , chuyển mode ở Default Relationship để hiển thị tất cả relationship

## Slide 32

2.5.2

Cấu hình ẩn/hiện cột danh sách CI Relationship

Chọn icon bánh răng trên màn list CI Relationship

## Slide 33

2.5.3

Chi tiết CI Relationship (UI)

Trên màn danh sách relationship,chọn button View Detail tương ứng với relationship cần xem

## Slide 34

2.5.4

Export màn CI Relationship

Chọn icon export để xuất file

## Slide 35

2.5.5

Tạo nhiều Relationship (CI Relationship/Add Bulk)

Đối tượng sử dụng
Admin
PM
Mục đích:
Cho phép PM tạo nhiều relationship (connection) giữa các CIs cùng một lúc
PM có thể preview và biết số lượng relationship sẽ được tạo 

Chọn CI Type

Select Source CIs và Destination CIs

Add Relationship

Kiểm tra và tạo nhiều relationship (Connection)

## Slide 36

2.5.5

Tạo nhiều Relationships (CIs Relationship/Add Bulk UI)

Trên màn danh sách relationship, chọn icon dấu cộng để tạo mới 

## Slide 37

2.5.5

Tạo nhiều Relationships (CI Relationship/Add Bulk UI)

B1: Tại màn CI Relationship, PM click “Add bulk Relationship( Icon Plus)
B2: Có thể chọn group hoặc không, sau đó chọn Source CI Type & Destination CI Type
B3: Chọn một hoặc nhiều Source CIs 
và Destination CIs
B4: Nhập thời gian apply - start date 
B5: Review
B6: Click Apply để tạo relationship

Note: Phần ràng buộc quan hệ giữa các relationship user có thể xem khi ấn vào icon ? cạnh Source CI hoặc Destination CI

## Slide 38

2.5.6

Chỉnh sửa nhiều Relationships

B1: Tại màn CI Relationship, PM đảm bảo đang chọn mode Edit
B2: Check vào checkbox các relationship cần sửa hoặc select all 
B3: Chọn icon Pen
B4: Nhập thời gian apply - start date 
B5: Click Save để thay đổi ngày tháng áp dụng relationship

## Slide 39

2.5.7

Xóa nhiều CI Relationship

B1: Tại màn CI Relationship, PM đảm bảo đang chọn mode Delete
B2: Check vào checkbox các relationship cần sửa hoặc select all 
B3: Chọn icon Thùng rác
B4: Nhập thời gian apply - start date 
B5: Xác nhận và Click Yes để xóa Relationship

## Slide 40

2.6

Quản Lý Configuration Request

Đối tượng sử dụng
Admin: Có thể xem danh sách, xem chi tiết,  export, Request ticket sang Jira 
PM: Có thể xem danh sách, xem chi tiết,  export, Request ticket sang Jira 
IT:  Có thể xem danh sách, xem chi tiết,  export, Approve ticket, reject ticket, Export to Jira
SEPG, QA: Có thể xem danh sách, xem chi tiết,  export
Mục đích:
Đối với PM: là nơi PM  gửi yêu cầu sang Jira và quản lý yêu cầu kết nối relationship giữa các Cis
Các loại tìm kiếm nâng cao: Lọc theo Relationship Key, tên CI nguồn/đích, loại CI nguồn/đích, trạng thái (status), thời gian hết hạn , cấu hình ẩn/hiển thị cột
Chi tiết configuration request
Export configuration request
Tạo và gửi yêu cầu (Request to Jira)
Đối với Role IT (Người phê duyệt): là nơi IT tiếp nhận và ra quyết định phê duyệt các yêu cầu cấu hình.
 Xử lý và phản hồi phê duyệt bằng cách phê duyệt ticket Task trên Jira hoặc truy cập hệ thống Dashboard phê duyệt theo từng relationship

## Slide 41

2.6.1

Quản lý danh sách Configuration Request

Mặc định màn list sẽ không hiển thi các request là các quan hệ với CI Type CMC Default Software , chuyển mode ở Default Request để hiển thị tất cả request

## Slide 42

2.6.2

Export danh sách Configuration Request

## Slide 43

2.6.3

Chi tiết Configuration Request

## Slide 44

2.6.4

PM Tạo Request to Jira (CIs Request) 

Chọn button Request to Jira để gửi yêu cầu sang bên Jira

## Slide 45

2.6.4

PM Tạo Request to Jira (CIs Request) 

B1: PM click button Request To Jira
B2: Chọn Issue Type sẽ được tạo bên Jira 
B3: Chọn Access Type (Allow/Deny)
B4: Chọn group hoặc search theo tên CI để lọc relationship tương ứng, chọn Relationship cần thiết lập (trạng thái Draft)
B5: Tải lên ảnh chứng minh
B6: Click Send to Jira để tạo ticket CM plan (có thể mất vài phút)

## Slide 46

2.6.5

IT Phê Duyệt Ticket CM Plan

B1: IT truy cập hệ thống Jira, xem ticket được assign
B2: Truy cập ticket CM Plan của dự án, nhấn vào Dashboard URL
B3: Review các thông tin trên Dashboard
B4: Đổi trạng thái ticket CM Plan sang Rejected hoặc Closed
B5: Hệ thống Dashboard đổi trạng thái approval status của relationship từ Pending -> Rejected hoặc Approved

## Slide 47

2.6.5

IT Phê Duyệt Ticket CM Plan

B1: IT truy cập hệ thống Jira, xem ticket được assign
B2: Truy cập ticket CM Plan của dự án, nhấn vào Dashboard URL
B3: Review các thông tin trên Dashboard chọn approve hoặc reject
B4: Sau khi phê duyệt xong, nhấn Export to Jira để xuất file excel 
B5: Ticket Jira được đổi sang trạng thái Approved thể hiện là IT đang thực hiện nội dung yêu cầu, có gắn kèm file excel

## Slide 48

2.6.5

IT Phê Duyệt Ticket CM Plan

Tương ứng bước 3 ở luồng xử lý

## Slide 49

2.6.5

IT Phê Duyệt Ticket CM Plan

Tương ứng bước 4 ở luồng xử lý

## Slide 50

2.6.5

IT Phê Duyệt Ticket CM Plan

Tương ứng bước 5 ở luồng xử lý

## Slide 51

2.6.5

Luồng duyệt tự động cho các yêu cầu gia hạn

-  Xử lý khi relationship thay đổi thời gian apply date, expired date, hệ thống tự động tạo request cập nhật thời gian tương ứng
   Nếu trong ticket đẩy đi PM chọn các request đều là gia hạn thì hệ thống 
	+ Thêm từ "extend" vào tiêu đề ticket Jira
	+ Tự động approve tất cả các request trong ticket Jira này
	+ Tự động đẩy file excel sang Jira
	+ Chuyển status của ticket Jira sang Approved
   Nếu trong ticket đẩy đi các request mà PM chọn tồn tại request mới thì hệ thống xử lý theo luồng cũ

## Slide 52

2.7

Quản Lý Baseline

Đối tượng sử dụng
Admin: Toàn quyền trên màn hình
PM: Toàn quyền trên màn hình
IT, SEPG, QA: Chỉ có quyền xem danh sách và màn hình list
Mục đích: Số hóa sheet Baseline, lưu thông tin cần baseline của dự án
Mặc định tạo các bản ghi khi mở dự án
Tạo dữ liệu mặc định: Áp dụng cho dự án đang chạy sau khi golive phase 21 muốn tạo nhanh
Các loại tìm kiếm nâng cao: Lọc theo Item Name, Category, Owner, Path, Baseline Criteria
Export danh sách Item
Thêm, sửa, xóa item

## Slide 53

2.7.1

Tạo dữ liệu Baseline mặc định

## Slide 54

2.7.2

Danh sách Baseline

## Slide 55

2.7.3

Export Baseline

## Slide 56

2.7.4

Tạo mới Baseline

Dữ liệu đồng bộ với màn CI Inventory, với CI type là Document

## Slide 57

2.7.5

Chỉnh sửa Baseline

Dữ liệu đồng bộ với màn CI Inventory, với CI type là Document

## Slide 58

2.7.6

Xóa Baseline

Dữ liệu đồng bộ với màn CI Inventory, với CI type là Document

## Slide 59

2.8

Quản Lý Working Environment

Đối tượng sử dụng
Admin: Toàn quyền trên màn hình
PM: Toàn quyền trên màn hình
IT, SEPG, QA: Chỉ có quyền xem danh sách và export
Mục đích: Số hóa sheet Working Enviroment, quản trị môi trường làm việc của member trong dự án
Tab Default Software: Mặc định tạo các bản ghi khi mở dự án
Các loại tìm kiếm nâng cao: Lọc theo Resource, Category, Description, Status , Managed by… 
Export danh sách Item
Thêm, sửa, xóa item
Tab Project Enviroment: 
   Các loại tìm kiếm nâng cao: Lọc theo Resource, Category, Description, Status , Managed by… 
Export danh sách Item
Thêm, sửa, xóa item

## Slide 60

2.8.1

Quản lý danh sách Default Software

Dữ liệu đồng bộ với màn CI Inventory, với CI type là CMC Default Software

## Slide 61

2.8.2

Export danh sách Working Enviroment

## Slide 62

2.8.3

Create Default Software

## Slide 63

2.8.4

Chỉnh sửa Default Software

## Slide 64

2.8.5

Xóa Default Software

## Slide 65

2.8.6

Quản lý danh sách Project Environment

Hiện thị thông tin phần mềm hoặc đường dẫn môi trường dùng trong dự án

## Slide 66

2.8.7

Tạo mới Project Environment

## Slide 67

2.8.8

Chỉnh sửa Project Environment

## Slide 68

2.8.9

Xóa Project Environment

## Slide 69

2.9

Quản Lý CI toàn bộ dự án

Đối tượng sử dụng
Admin, IT, SEPG, QA: Toàn quyền sử dụng trên màn hình
Mục đích: 
Các loại tìm kiếm nâng cao: Lọc theo thời gian, Project Name, Status, CI name
Chi tiết CI: 

## Slide 70

2.9.1

Danh sách CI của toàn bộ dự án

## Slide 71

2.9.2

Chi tiết CI của toàn bộ dự án

## Slide 72

2.10

Quản Lý CI Relationship toàn bộ dự án

Đối tượng sử dụng
Admin, IT, SEPG, QA: Toàn quyền sử dụng trên màn hình
Mục đích: 
Các loại tìm kiếm nâng cao: Lọc theo thời gian, Project Name, Status, Source CI, Destination CI
Export Relationship
Chi tiết Relatioship 

## Slide 73

2.10.1

Danh sách Relationship

## Slide 74

2.10.2

Export Relationship

## Slide 75

2.10.3

Chi tiết Relationship

## Slide 76

2.10.4

Quản Lý Config Request toàn bộ dự án

  Đối tượng sử dụng
Admin, IT, SEPG, QA: Toàn quyền sử dụng trên màn hình
Mục đích: 
Các loại tìm kiếm nâng cao: Lọc theo thời gian, Project Name, Status, Source CI, Destination CI …
Chi tiết Config Request

## Slide 77

2.10.5

Danh sách Config Request

## Slide 78

2.10.6

Chi tiết Config Request

## Slide 79

2.10.7

Các job xử lý tự động

-  Xử lý khi relationship thay đổi thời gian apply date, expired date, hệ thống tự động tạo request cập nhật thời gian tương ứng
   Nếu trong ticket đẩy đi PM chọn các request đều là gia hạn thì hệ thống 
	+ Thêm từ "extend" vào tiêu đề ticket Jira
	+ Tự động approve tất cả các request trong ticket Jira này
	+ Tự động đẩy file excel sang Jira
	+ Chuyển status của ticket Jira sang Approved
   Nếu trong ticket đẩy đi các request mà PM chọn tồn tại request mới thì hệ thống xử lý theo luồng cũ

## Slide 80

2.10.7.1

Các job xử lý tự động

Xử lý khi relationship thay đổi thời gian apply date, expired date, hệ thống tự động tạo request cậpnhật thời gian tương ứng
TH1: Thay đổi trực tiếp tại màn CI Relationship

## Slide 81

2.10.7.2

Các job xử lý tự động

Xử lý khi relationship thay đổi thời gian apply date, expired date, hệ thống tự động tạo request cậpnhật thời gian tương ứng
TH2: Thay đổi thời gian của dự án

## Slide 82

2.10.8

Các job xử lý tự động

-  Xử lý khi member được đánh dấu là remove trong dự án, tới ngày đánh dấu là remove thì hệ thống sẽ:
+ Gửi mail cảnh báo tới current PM trước 3 ngày so với ngày đánh dấu remove
+ Tự động tạo ticket xóa connection
+ Tự động approve tất cả các request trong ticket Jira
+ Tự động đẩy file excel sang ticket Jira
+ Chuyển status của ticket Jira sang Approved

## Slide 83

2.10.9

Các job xử lý tự động

Xử lý khi member nghỉ việc
       + Tự động tạo ticket xóa connection
+ Tự động approve tất cả các request trong ticket Jira
+ Tự động đẩy file excel sang ticket Jira
+ Chuyển status của ticket Jira sang Approved

## Slide 84

2.10.10

Các job xử lý tự động

- Khi relationship chuẩn bị hết hạn
+ Gửi mail cảnh báo tới current PM trước 3 ngày 

## Slide 85

03

Q&A

## Slide 86

---

<a id="32--user-guide--p9--business-plan-enhancement-pptx"></a>
## 32. [User Guide] P9_ Business Plan Enhancement.pptx

## Slide 1

Dashboard – Module Business Plan Details & New Workflow

December 2025

User Guide_Admin / BOM/ FC / DUL / GL / Sale

## Slide 2

Nội dung

04

02

01

Tổng quan module điền Business Plan (PAKD) mới

Hướng dẫn thao tác trên hệ thống Dashboard

Q&A

03

Cải thiện các mô-đun khác trong phase 16

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE ĐÁNH GIÁ NHÂN SỰ THEO TIÊU CHÍ

1.2. Tổng quan workflow module Đánh Giá Nhân Sự Theo Tiêu Chí

## Slide 4

1.1

MỤC ĐÍCH

Thay vì xử lý luồng workflow theo từng mã Onsite & Offshore, hệ thống sẽ gộp việc điền và xử lý quy trình phê duyệt (workflow) của cả hai mã MVV lên trên cùng một màn hình Business Plan Details. Điều này giúp các phòng ban có cái nhìn tổng thể về tài chính và dữ liệu dự án dựa trên Business Plan (PAKD) của cả Onsite và Offshore qua các chiều view mới
Phát triển việc đồng bộ giữa CRM & Dashboard dựa trên Mã vụ việc (MVV) của cả Onsite và Offshore được link với nhau trên CRM.
Cho phép nộp (submit/reject) đồng thời cả hai mã MVV/PAKD Onsite & Offshore trên cùng một luồng phê duyệt duy nhất.

## Slide 5

1.2

Tổng quan workflow Tạm ứng thưởng sản xuất

## Slide 6

1.3

Đối tượng người dùng & phân quyền

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được cấp quyền thao tác toàn bộ Business Plan Details
BOM
BOM được quyền view toàn bộ hệ thống
FC
FC được quyền view và edit toàn bộ Business Plan Details
GL
GL được quyền view Business Plan Details
DUL
DUL được quyền view và edit Business Plan Details
Sale
Sale được quyền view và edit Business Plan Details

## Slide 7

1. Tạo hai mã MVV được linked với nhau trên CRM

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

2. Điền thông tin Business Plan Details (PAKD) 

4. Submit/Reject luồng phê duyệt

5. Tạo version mới

Tạo Business Plan Details (Onsite & Offshore Linked)

3. Sử dụng các chiều view Business Plan mới

## Slide 8

2.1.1

Tạo MVV Onsite & Offshore  

Đối tượng sử dụng
FC
Sale
DUL
Mục đích:
Cho phép User tạo Project Code (MVV) Onsite và Project Code (MVV) Offshore để gắn với nhau và đồng bộ về Billing Plan & Business Plan List 

## Slide 9

2.1.2

Tạo MVV Onsite & Offshore  

B1: User truy cập vào CRM JIRA, tạo 2 Project Code Onsite & Offshore như workflow cũ
Lưu ý: CRM sẽ có các trường mới như sau:
1. MVV Type: 

2. MVV link:

3. DU PIC*: cả 2 mã đều thuộc chung DU thì mới có thể link với nhau   

## Slide 10

2.1.3

Tạo MVV Onsite & Offshore  

MVV A

MVV B

## Slide 11

2.2.1

Điền thông tin liên quan tới Business Plan bên Dashboard

B2: Sau khi tạo sub-wo, gắn link đổi trạng thái = Active cho cả Onsite & Offshore. Về lại Dashboard, màn Billing Plan điền thông tin sub-wo cho từng MVV

## Slide 12

2.2.2

Điền thông tin liên quan tới Business Plan bên Dashboard

B3: Truy cập Business Plan, click View Details để bắt đầu điền PAKD cho Onsite hoặc Offshore 

## Slide 13

2.2.3

Điền thông tin liên quan tới Business Plan bên Dashboard

B4: Tại mục General Information, sử dụng filter để điền thông tin liên quan cho từng mã Onsite & Offshore. Thông tin sẽ được sử dụng riêng cho từng mã để thực hiện tính toán

## Slide 14

2.3.1

Điền thông tin và sử dụng các chiều view mới

B5: Tại mục Business Plan, tab Delivery plan, sẽ có 2 sub-tab Onsite & Offshore giúp User có thể điền PAKD trên từng mã
*Filter All: 
Cho phép User xem thông tin tổng hợp Delivery Plan của Onsite hoặc Offshore nếu có nhiều hơn 1 DU  

Lưu ý: Với DU Onsite sẽ chỉ hiển thị tab Onsite để fill PAKD cho DU đó. Tương tự với Offshore 

## Slide 15

2.3.2

Điền thông tin liên quan tới Business Plan bên Dashboard

B6: Tại mục Business Plan, tab Revenue plan, sẽ có 2 sub-tab Onsite & Offshore giúp User có thể điền PAKD trên từng mã 

Lưu ý: Với DU Onsite sẽ chỉ hiển thị tab Onsite để fill PAKD cho DU đó. Tương tự với Offshore 

## Slide 16

2.3.3

Điền thông tin liên quan tới Business Plan bên Dashboard

B7: Tại mục Business Plan, tab Business Plan, sẽ có 4 sub-tab Total / OB / Onsite / Offshore giúp User có thể xem và điền PAKD trên từng mã 
Mục đích:
Total: Cung cấp số liệu toàn diện về sức khỏe của Onsite & Offshore qua
OB: Theo dõi các số liệu liên quan tới Onsite Branch
Onsite: Theo dõi các số liệu liên quan giữa Sale & Onsite
Offshore: Theo dõi các số liệu liên quan tới Sale & Offshore

## Slide 17

2.3.3

Điền thông tin liên quan tới Business Plan bên Dashboard

Business Plan Offshore: Chỉ hiển thị các thông số Sale Offshore & DU offshore

## Slide 18

2.3.3

Điền thông tin liên quan tới Business Plan bên Dashboard

Business Plan Onsite: Chỉ hiển thị các thông số Sale Onsite & DU Onsite

## Slide 19

2.3.4

Điền thông tin liên quan tới Business Plan bên Dashboard

Business Plan OB (Onsite Branch): Hiển thị các thông số của Onsite Branch được tính từ thông số của tất cả Sale & DU đang tham gia

## Slide 20

2.3.5

Điền thông tin liên quan tới Business Plan bên Dashboard

Business Plan Total: Hiển thị các thông số của Business Plan Total được tính từ thông số của tất cả Sale & DU đang tham gia

## Slide 21

2.4.1

Submit Workflow

B8: Click Submit để đưa cả hai mã Onsite & Offshore vào luồng phê duyệt tới các phòng ban (giống luồng workflow cũ).
*Với các cấp cao hơn, chỉ cần approve 1 lần sẽ approve cho cả hai mã Onsite & Offshore

## Slide 22

2.4.2

Reject Workflow

B8.1: Click Reject, chỉ cần 1 trong 2 mã bị reject tại bước bất kì. Luồng workflow bị hủy và đưa cả 2 mã về trạng thái Draft để điền lại PAKD.

## Slide 23

2.5

Tạo new version

B9: Sau khi Workflow được approve, nếu có mong muốn thay đổi PAKD mới, User có thể click “Create New Version”. 
*Việc tạo version mới sẽ đồng thời nâng version của cả 2 mã MVV.

## Slide 24

3.1

Cải thiện các chức năng khác 

Thêm màn hình cài đặt Score by Role
Đối tượng sử dụng
Admin
Mục đích:
Hiện tại: Điểm gốc (Base Score) của các đầu điểm (N/A, L1, L2, ..) được đánh giá tại màn hình đánh giá tiêu chí member đang được set up (hard-code) trong Database
Cải tiến: Cho phép User có thể tùy chỉnh Score, Base Score, Definition trực tiếp trên giao diện của mục Settings, mục Performance & Rank Settings

## Slide 25

3.2

Cải thiện các chức năng khác (Score by Role) 

## Slide 26

3.3

Performance & Rank Settings và Rank Settings

B1: User đăng nhập màn hình Settings, mục Performance & Rank Settings, chỉnh sửa các điểm theo mong muốn.
Lưu ý: 
1. Chỉnh sửa (Xóa/Inactive) Score sẽ hủy bỏ thiết lập của Score đó tại cột List Score trong màn Rank Settings của Role tương ứng.
2. User sẽ không thể xóa Score nếu score đó là score cuối cùng tồn tại trong cột List Score ở màn hình.
3. Nếu có bất kì sự thay đổi Base Score, hãy thông báo với Admin/HR để cài đặt lại List Score trong Rank Setting
 

## Slide 27

3.3.1

Rank Setting

B2. User truy cập Rank Settings để cài đặt
 

## Slide 28

3.3.2

Cài đặt Rank Setting

B3. User truy cập màn hình đánh giá tiêu chí để đánh giá theo tiêu chí & base score và định nghĩa mới

## Slide 29

3.2

Cải thiện các chức năng khác 

2. Phân loại Tag Onsite & Offshore
Đối tượng sử dụng
Admin/BOM/FC/DUL/BUL/SALE
Mục đích:
Hiện tại: Các mã MVV đang không được đánh tag để nhận biết Onsite hay Offshore
Cải tiến: Sau khi cải tiến business plan mới, tại màn hình Business Plan (List), các mã MVV sẽ được gắn tag Onsite hoặc Offshore

## Slide 30

3.2.1

Cải thiện các chức năng khác 

B1. User truy cập màn hình Business Plan (List)

## Slide 31

3.3

Cải thiện các chức năng khác 

2. Export 
Đối tượng sử dụng
Admin
Mục đích:
Hiện tại: Export chỉ một mã MVV
Cải tiến: Export (General Information) thông tin cả hai mã MVV, Export (Business Plan Details) thông tin total của tất cả Sale & DU onsite & DU offshore

## Slide 32

3.3.1

Cải thiện các chức năng khác 

B1. User click Export (General Information)

## Slide 33

3.3.2

Cải thiện các chức năng khác 

B1.1. User click Export (Business Plan Details)

## Slide 34

03

Q&A

## Slide 35

---

<a id="33--user-guide--phase-16-ranking-member--2--pptx"></a>
## 33. [User Guide] Phase 16 Ranking Member (2).pptx

## Slide 1

Dashboard – Module Đánh Giá Tiêu Chí Nhân Sự

December 2025

User Guide_Admin / BOM / HR / DUL / PM

## Slide 2

Nội dung

04

02

01

Tổng quan module đánh giá nhân sự theo tiêu chí 

Hướng dẫn thao tác trên hệ thống Dashboard

Q&A

03

Cải thiện các mô-đun khác trong phase 16

## Slide 3

1.1. Mục đích

01

TỔNG QUAN MODULE ĐÁNH GIÁ NHÂN SỰ THEO TIÊU CHÍ

1.2. Tổng quan workflow module Đánh Giá Nhân Sự Theo Tiêu Chí

## Slide 4

1.1

MỤC ĐÍCH

Hệ thống Dashboard, chức năng đánh giá Rank & Performance là công cụ nhằm đánh giá năng lực và năng suất của nhân sự trong dự án theo role trong tháng nhằm giúp CMC quy đổi Level của nhân sự theo role bằng điểm Rank, và xác định năng suất của nhân sự bằng điểm Performance
Hỗ trợ PM: Có thể xem và đánh giá (Rank & Performance) với nhân sự thuộc DU và các nhân sự DU khác tham gia dự án do PM quản lý.
Hỗ trợ DUL: Có thể xem và đánh giá (Rank & Performance) với nhân sự thuộc DU và các nhân sự DU khác đang tham gia dự án của DU đó.
Hỗ trợ team HR, DUL, PM đánh giá và quản lý trình độ và năng lực của nhân sự

## Slide 5

1.2

Tổng quan workflow Đánh Giá Rank Score cho nhân sự

## Slide 6

1.3

Đối tượng người dùng & phân quyền

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được quyền thao tác toàn bộ trên hệ thống và sử dụng luồng đánh giá
BOM
BOM được quyền view toàn bộ hệ thống, xem tiêu chí đánh giá, và xem điểm đánh giá
PM/ DUL
PM đánh giá Rank & Performance cho nhân sự trong dự án (default Project – Member)
DUL đánh giá  Rank & Performance cho nhân sự trong dự án (default Member – Project)
HR
Cài đặt các tiêu chí đánh giá dựa trên vai trò (role) của member trong dự án 
View điểm đánh giá Rank
FC
Cho phép view điểm đánh giá Rank & Performance và Export file Excel

## Slide 7

Thêm mới / Xoá bỏ/ Chặn các tiêu chí được đánh giá

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

Xuất file report Excel Performance & Bonus kèm điểm Rank (phase 16)

Đánh giá Rank & Performance cho nhân sự trong dự án và lưu lịch sử

Phần phát triển mới: Đánh giá Rank cho Nhân Sự

## Slide 8

2.1.1

Cài đặt Rank (Rank Settings)

Đối tượng sử dụng
ADMIN
HR
Mục đích:
Cho phép HR hoặc ADMIN có quyền tạo mới, cập nhật và sửa xoá các tiêu chí đánh giá cho nhân sự

## Slide 9

2.1.2

Cài đặt các tiêu chí theo Role

B1: User truy cập vào hệ thống Dashboard vào tab Setting> Rank Setting
B2: Bấm "Edit“ tại role mong muốn được cài đặt

## Slide 10

2.1.3

Cài đặt các tiêu chí đánh giá chi tiết

B1: User truy cập màn Field Settings​
B2: User cài đặt các tiêu chí đánh giá và click Save hoặc click Cancel để từ chối lưu giá trị

## Slide 11

2.2

Rank cho nhân sự trong dự án (Project – Member)

Đối tượng sử dụng
PM (default view: Project – Member)
DUL
ADMIN
Mục đích:
Cho phép PM xem và đánh giá Rank & Perfromance cho nhân sự thuộc dự án của PM và các nhân sự DU khác tham gia dự án mà PM đó quản lý, trừ PM & QA
Cho phép DUL xem và đánh giá Rank & Performance cho tất cả các nhân sự thuộc DU và các nhân sự DU khác tham gia dự án DU đó
ADMIN là người có toàn quyền với hệ thống, có thể xem và đánh giá Rank & Performance cho nhân sự trong dự án ở bất kỳ role nào

## Slide 12

2.2.1

Rank cho nhân sự trong dự án (Project – Member)

(Chiều view Project- Member)
B1: User truy cập vào hệ thống Dashboard tab Delivery > Assessment> Tab Project - Member​
B2: Bấm "Rank” tại Project mong muốn đánh giá

## Slide 13

2.2.2

Rank cho nhân sự trong dự án (Project – Member)

(Chiều view Project- Member)
B3: User truy cập màn View Ranking, chọn các role mong muốn để đánh giá và click Rank hoặc Cancel/X để thoát khỏi màn hình View Ranking

Note: PM không thể đánh giá cho role PM & QA

Rank Score = Sum (Score x Weight)​
Với các role chưa được tạo tiêu chí, Rank Score = N/A và sẽ không hiện popup Ranking Role để đánh giá

## Slide 14

2.2.3

Rank cho nhân sự trong dự án (Project – Member)

(Chiều view Project- Member)
B4: User select điểm Score* và click Save để lưu giá trị Rank và đánh giá Rank role tiếp theo hoặc bấm Cancel để từ chối lưu giá trị đã đánh giá. 

User có thể bấm Back để quay lại rate mới Role trước đó

Note: Nếu Rateable = No, User không thể đánh giá tiêu chí đó của member manually và sẽ mặc địch là N/A (ở phase 16)

## Slide 15

2.2.4

Rank cho nhân sự trong dự án (Project – Member)

(Chiều view Project- Member)
B5: User quay về màn hình View Rank và hiển thị điểm vừa rate cho member đó

Note: Nếu Rateable = No, User không thể đánh giá tiêu chí đó của member manually và sẽ mặc địch là N/A (ở phase 16)

## Slide 16

2.2.5

Rank cho nhân sự trong dự án (Project – Member)

(Chiều view Project- Member)
B6: User click View Details để hiển thị điểm của nhân sự được đánh giá và click Close để đóng popup

Note: Với các nhân sự chưa có điểm sẽ không hiển thị View Details

## Slide 17

2.3

Rank cho nhân sự trong dự án (Member – Project)

Đối tượng sử dụng
DUL (default view: Member – Project)
PM
ADMIN
Mục đích:
Cho phép DUL xem và đánh giá Rank Score cho nhân sự thuộc DU và các nhân sự DU khác đang tham gia dự án của DU mình
Cho phép PM xem và đánh giá Rank Score cho nhân sự thuộc dự án của PM và các nhân sự DU khác tham gia dự án
ADMIN là người có toàn quyền với hệ thống, có thể đánh giá Ranking cho nhân sự trong dự án ở bất kỳ role nào

## Slide 18

2.3.1

Rank cho nhân sự trong dự án (Member – Project)

(Chiều view Member- Project)
B1: User truy cập vào hệ thống Dashboard tab Delivery > Assessment > Tab Member - Project​
B2: Bấm "Rank"

## Slide 19

2.3.2

Rank cho nhân sự trong dự án (Member – Project)

(Chiều view Member- Project)
B3: User truy cập màn View Ranking, chọn các role mong muốn để đánh giá và click Rank hoặc Cancel/X để thoát khỏi màn hình View Ranking

Note: Nếu Rateable = No, User không thể đánh giá tiêu chí đó của member manually và sẽ mặc địch là N/A (ở phase 16)

Rank Score = Sum (Score x Weight)​
Với các role chưa được tạo tiêu chí, Rank Score = N/A và sẽ không hiện popup Ranking Role để đánh giá

## Slide 20

2.3.3

Rank cho nhân sự trong dự án (Member – Project)

(Chiều view Member - Project)
B4: User select điểm Score* và click Save để lưu giá trị Rank và đánh giá Rank role tiếp theo hoặc bấm Cancel để từ chối lưu giá trị đã đánh giá. 

User có thể bấm Back để quay lại rate mới Role trước đó

Note: Nếu Rateable = No, User không thể đánh giá tiêu chí đó của member manually và sẽ mặc địch là N/A (ở phase 16)

## Slide 21

2.3.4

Rank cho nhân sự trong dự án (Project – Member)

(Chiều view Member – Project)
B5: User quay về màn hình View Rank và hiển thị điểm vừa rate cho member đó

Note: Nếu Rateable = No, User không thể đánh giá tiêu chí đó của member manually và sẽ mặc địch là N/A (ở phase 16)

## Slide 22

2.3.5

Rank cho nhân sự trong dự án (Member - Project)

(Chiều view Member - Project)
B6: User click View Details để hiển thị điểm của nhân sự được đánh giá và click Close để đóng popup

Note: Với các nhân sự chưa có điểm sẽ không hiển thị View Details

## Slide 23

2.4

History

Mục Đích: 
- Cho phép người dùng xem lịch sử thay đổi luồng đánh giá

## Slide 24

3

Cải thiện các mô-đun khác trong phase 16

## Slide 25

3.1

Performance History & Phân Quyền

Đối tượng sử dụng
HR
ADMIN
PM/DUL
Member
Mục đích:
Cho phép các đối tượng trên sử dụng được phép View lịch sử của hai trường được thay đổi (Rank Score & Performance & Criteria)
Update thêm:
Chỉnh sửa phân quyền (View by Member, View All).
Những tài khoản là member với quyền View by Member thì chỉ có thể xem lịch sử thay đổi theo member
Những tài khoản cấp cao hơn gán quyền View All thì mới có quyền xem lịch sử thay đổi của tất cả các member
  

## Slide 26

3.1.1

Module Phân Quyền Button History

Đối tượng sử dụng

Mục đích:
Phân quyền view lịch sử chỉnh sửa điểm Rank & Performance

View by Member 
VIEW_HISTORY_PERFORMANCE_BY_MEMBER 
DB - Member 
Member chỉ xem được lịch sử thay đổi điểm cá nhân của nhưng dự án đang làm 
View All 
VIEW_HISTORY_PERFORMANCE_ALL 
DB – ADMIN 
DB - BOM 
DB – HR 
DB – PM 
DB - DUL 
được xem hết lịch sửa chỉnh sửa điểm của tất cả các member trong history  

## Slide 27

3.1.2

Module Performance History

B1: User truy cập vào hệ thống Dashboard tab Delivery > Performance & Bonus
B2: Click button History​​

## Slide 28

3.1.3

Module Performance History

B2: User view màn hình performance history và xem được điểm Rank Score và Criteria (nếu có thay đổi)​ dựa trên phần phân quyền đã được update

## Slide 29

3.2

Tổng hợp phần chỉnh sửa cron-job gửi mail đối với Rank Score

Mục đích: 
- Đảm bảo DUL và PM nhận được mail và đánh giá nhân sự trong tháng đúng hạn
- Đảm bảo nhân sự có nhận thức về điểm Rank và liên hệ tới PM/DUL của mình để được đánh giá

Đối tượng sử dụng
HR
PM/DUL

​

Hiện tại: 

Gửi mail thông báo nhắc nhở PM/DUL đánh giá performance hằng tháng 

 

Thay đổi: 
Gửi mail DUL, PM 3h ngày 22 hàng tháng: sửa thể count thêm số lượng chưa đánh giá Rank & Performance
Gửi mail hàng tháng nhân viên 23h ngày 28 hàng tháng: sửa thêm thông tin rank & perfomance
Gửi mail hàng tuần cho nhân viên 9h thứ bảy hàng tuần: sửa thêm thông tin rank và performance
Job update rank = 0 cho nhân viên chưa được đánh giá vào 1h ngày 29 hàng tháng: thêm mới chức năng
Job update performance score default cho nhân viên chưa được đánh giá vào 21h ngày 28 hàng tháng: sửa chức năng cũ

## Slide 30

3.3

Module Performance Export

Đối tượng sử dụng
HR / FC
Admin
Mục Đích:
Cho phép User tải file excel bằng cách click vào button Export tại màn hình Performance & Bonus. File excel hiển thị điểm đánh giá Performance & Rank

## Slide 31

3.4

Module Performance

Đối tượng sử dụng
Sử dụng với các đối tượng sử dụng luồng đánh giá Rank
Mục đích:
Cho phép User đánh giá điểm Rank và Performance trên cùng một luồng đánh giá 
  

## Slide 32

3.4.1

Rank Performance

B1: User bắt đầu đánh giá theo luồng đánh giá Rank. 
B2: User Đánh giá Rank và Peformance trên cùng màn hình Rank Role
  

## Slide 33

3.5

Cải thiện màu sắc và hiển thị của số Actual & Planning theo timeline quá khứ và hiện tại

Đối tượng sử dụng
PM (default view: Project – Member)
DUL
ADMIN
Mục đích: 
- Thêm màu sắc cho số Actual Future nhằm hiển thị trực quan cho các filter Sales Volume, Revenue, Profit and Man Power
  

## Slide 34

3.5

Cải thiện màu sắc và hiển thị của số Actual & Planning theo timeline quá khứ và hiện tại

B1: User vào màn hình Dashboard/Global Overview
B2: User sử dụng filter để xem số liệu Actual Future

## Slide 35

03

Q&A

## Slide 36

---

<a id="34--user-guide--phase-17-milestone---workload-chart--1--pptx"></a>
## 34. [User Guide] Phase 17 Milestone & Workload chart (1).pptx

## Slide 1

Dashboard – Module Phát Triển Biểu Đồ Timeline

Jan 2026

User Guide_Admin / BOM / DUD / PM

## Slide 2

Nội dung

04

02

01

Tổng quan module phát triển biểu đồ timeline dự án

Hướng dẫn thao tác và view trên hệ thống Dashboard

Q&A

03

Cải thiện các module khác trong phase 17

## Slide 3

1.1. Mục đích

01

Tổng quan module phát triển biểu đồ timeline dự án 

1.2. Tổng quan module phát triển biểu đồ timeline dự án 

## Slide 4

1.1

MỤC ĐÍCH

Phase 17, đội ngũ sẽ tập trung vào việc phát triển biểu đồ nhằm nâng cao khả năng theo dõi tiến độ bằng biểu đồ WaterFall & Agile và quản lý nguồn lực trong dự án. Mục tiêu chính của giai đoạn này là cung cấp một giao diện trực quan, hỗ trợ quản lý dự án hiệu quả thông qua các tính năng phát triển.
Với biểu đồ Milestone: giúp các cấp quản lý dễ dàng nắm được trạng thái với các hạng mục Milestones được đổi chiếu với các ticket Delivery trên JIRA nhằm nhận diện các milestones có nguy cơ trễ cần điều chỉnh plan của dự án nếu cần thiết.
Với biểu đồ Workload: giúp các cấp quản lý dễ dàng nắm được trạng thái của việc phân bổ nguồn lực và so sánh giữa business plan và resouce đã được allocate vào dự án nhằm tối ưu chi phí
Cả hai biểu đồ nâng cao sự trực quan của từng dự án thông qua khả năng theo dõi tiến độ và quản lý nguồn lực nhằm tối ưu hóa kế hoạch và vận hành.

## Slide 5

1.2

Tổng quan workflow đánh giá hiệu quả thực hiện công việc

## Slide 6

1.3

Đối tượng người dùng & phân quyền

Đối tượng
Nhiệm vụ chính
Admin
Admin của hệ thống, được quyền thao tác toàn bộ trên hệ thống và view biểu đồ dự án
BOM
BOM được quyền view dự án và biết đồ dự án
PM/ DUD
View biểu đồ và đánh giá tình hình dự án

## Slide 7

View biểu đồ Milestone (ticket Delivery)

02

HƯỚNG DẪN THAO TÁC TRÊN HỆ THỐNG

Hiển thị cột cho phép member của dự án sử dụng AI

View biểu đồ Workload (Head Count/Workload)

Phần phát triển mới: Phát triểu biểu đồ Milestone & Workload

Hiển thị số liệu AI (AI index / AI supporting)

## Slide 8

2.1.1

View biểu đồ Milestone

Đối tượng sử dụng
ADMIN
PM/DUL
BOM
Mục đích:
Cho phép User view biểu đồ Milestone nhằm mục đích review các milestone (ticket Delivery) của dự án

## Slide 9

2.1.2

View biểu đồ Milestone

B1: User truy cập vào hệ thống Dashboard vào tab Delivery/Project List
B2: Chọn dự án mong muốn và dự án đó đã có ít nhất 1 ticket delivery đã được tạo trên JIRA

## Slide 10

2.1.3

View biểu đồ Milestone

B1: User click Project Overview​
B2: Tại Project Chart, chọn tab Milestone

Note: hiện tại hệ thống dashboard sẽ đồng bộ theo cronjob vào 4h sáng mỗi ngày chứ không hiển thị live. Do đó cần sử dụng postman để call API sync sau khi tạo ticket Delivery của dự án

## Slide 11

2.2

View biểu đồ Workload (HC-HeadCount)

Đối tượng sử dụng
ADMIN
BOM
PM/DUD
Mục đích:
Với biểu đồ Workload: giúp các cấp quản lý dễ dàng nắm được trạng thái của việc phân bổ nguồn lực và so sánh giữa business plan và resouce đã được allocate vào dự án
Tại mode Headcount: Cho phép user so sánh số người đã được allocate thực tế (Actual) với số người đã lên plan ở trong business plan theo Headcount (số lượng người). Timeline theo từng tháng

## Slide 12

2.2.1

View biểu đồ Workload (HC-HeadCount)

B1: User truy cập vào hệ thống Dashboard vào tab Delivery/Project List
B2: Chọn dự án mong muốn và đã có business plan được phê duyệt

## Slide 13

2.2.2

View biểu đồ Workload (HC-HeadCount)

B1: User click Project Overview​
B2: Tại Project Chart, chọn tab Workload, mode HC (headcount)

Note: User có thể sử dụng filter cho từng MVV nếu dự án đó có nhiều hơn 1 MVV

## Slide 14

2.2.3

View biểu đồ Workload (MM)

Đối tượng sử dụng
ADMIN
BOM
PM/DUD
Mục đích:
Với biểu đồ Workload: giúp các cấp quản lý dễ dàng nắm được trạng thái của việc phân bổ nguồn lực và so sánh giữa business plan và resouce đã được allocate vào dự án
Tại mode MM: Cho phép user so sánh số người đã được allocate thực tế (Actual) với số người đã lên plan ở trong business plan theo Man-Month. Timeline theo từng tháng

## Slide 15

2.2.4

View biểu đồ Workload (MM-ManMonth)

B1: User truy cập vào hệ thống Dashboard vào tab Delivery/Project List
B2: Chọn dự án mong muốn và đã có business plan được phê duyệt

## Slide 16

2.2.5

View biểu đồ Workload (MM-ManMonth)

B1: User click Project Overview​
B2: Tại Project Chart, chọn tab Workload, mode HC (headcount)

Note: User có thể sử dụng filter cho từng MVV nếu dự án đó có nhiều hơn 1 MVV

## Slide 17

2.3

Hiển thị số liệu AI (AI index / AI supporting)

Đối tượng sử dụng
ADMIN
BOM
PM/DUD
Mục đích:
AI index: Được sử dụng để đo lường mức độ đóng góp của AI vào việc tạo ra dòng code trong một tháng.
AI supporting: Được sử dụng để đo mức độ hỗ trợ thực tế của AI thông qua số dòng code được chấp nhận trong dự án

## Slide 18

2.3.1

Hiển thị số liệu AI (AI index / AI supporting)

B1: User click dự án bất kì. Chọn Project Overview
B2: View AI index in months & AI supporting in months

Note: User có thể sử dụng tab Accumulated để hiện số tổng của cả dự án

## Slide 19

2.3.1

Hiển thị số liệu AI (AI index / AI supporting)

Sync button: Sync dữ liệu AI trực tiếp, thay vì sync theo từng Git id
B1: User vào màn hình Project Information, click button Sync

Note: User có thể sử button Sync để update số liệu AI trực tiếp lấy về từ C-CodeX

## Slide 20

2.4

Phát triển tính năng cho phép member sử dụng C-CodeX trong dự án & ghi lại lịch sử trong log

Đối tượng sử dụng
ADMIN
BOM 
PM/DUD
Mục đích:
Cho phép PM/DUL bật/tắt quyền sử dụng C‑CodeX giúp đảm bảo AI được sử dụng đúng phạm vi, đúng đối tượng và phù hợp với yêu cầu của từng dự án.
Tối ưu hóa chi phí sử dụng AI của dự án.
Ghi lại lịch sử thay đổi trong member log history, nhằm lưu lại vết thay đổi

## Slide 21

2.4.1

Ghi lại lịch sử thay đổi trong Member Log History

B1: User click vào Member Log History
B2: Kiểm tra lịch sử thay đổi trường Use C-Code nếu có thay đổi

## Slide 22

2.4.2

Ghi lại lịch sử thay đổi trong Member Log History

B3: User click Record mới nhất để hiển thị lịch sử của việc enable/disable C-CodeX cho member trong dự án

## Slide 23

2.5

Thêm tính năng hiển thị remove Date cho chức năng current PM

Đối tượng sử dụng
ADMIN 
PM/DUD
Mục đích:
Trong trường hợp một dự án có nhiều PM, khi người dùng chọn một PM làm Current PM, hệ thống sẽ tự động đánh dấu current PM trước đó với trạng thái Removed. Đồng thời, hiển thị thêm một cột Removed Date để ghi nhận thời điểm PM bị loại khỏi vai trò chính
Đồng thời ghi lại lịch sử thay đổi current PM

## Slide 24

2.5.1

Chuyển current PM hiện tại sang Current PM mới

B1: User click vào Project Member
B2: Với các member có role PM trong dự án, chọn current PM mới thành công

## Slide 25

2.5.3

Ghi lại lịch sử thay đổi trong Member Log History

B3: Hệ thống ghi nhận current PM mới và current PM bị removed và hiển thị Removed Date

Note: Nếu current PM này trước đó sử dụng C-CodeX thì cũng sẽ bị disable cột Use Code-X

## Slide 26

3

Enhance các module khác

## Slide 27

3.1

Cải thiện tính năng Export điểm rank score và điểm tương ứng

Hiện trạng:
Ở phase 16, button Export cho phép người dùng xuất file excel với các trường thông tin tại màn hình Assessment nhưng không thể hiện điểm Score được quy đổi từ điểm L
Cải tiến:
Chỉnh sửa format file excel, chia các role theo từng sheet và hiển thị tiêu chí và điểm score tương ứng với điểm L
  

## Slide 28

3.2

Cải thiện tính năng Export Dashboard/Delivery/AvailableResource (thêm cột Other Skills)

Hiện trạng:
Button Export tại màn hình Available Resource chỉ hiển thị main skill
Cải tiến:
Lưu ý: hiện tại hệ thống Dashboard đã lấy trường other skills từ SAP và lưu trong bảng skill_user_detail. Trong bảng này có cột Skill và Skill_type. Cột other skills trong file excel sẽ chỉ lấy skill và có skill_type = skill để hiển thị tại cột other skills
User xuất file excel “Resource_Allocation_01012026_31012026” và có cột Other Skill lấy từ trường Skill của hệ thống Skillset

## Slide 29

3.3

Điều chỉnh rule của allocation 

Hiện trạng:
PM được phép allocate nhân sự theo tháng, từ ngày 1 đến hết tháng. 
Sau ngày 28, hệ thống không cho phép allocate nữa. 
Thực tế có trường hợp PM chỉ có plan allocate đến ngày 25 hoặc hết tháng.
Cải tiến:
Lock backdate: ON : 
Nếu ngày hiện tại > 28 (tức là từ ngày 29 trở đi): 
→ Chỉ cho phép allocate từ ngày 26 của tháng hiện tại đến tương lai. 
Nếu ngày hiện tại trong khoảng 1–28: 
→ Cho phép allocate từ ngày 26 của tháng trước đến tương lai như bình thường
Lock backdate: OFF:
→ User có thể allocate resouce quá khứ lẫn tương lai mà không cần quan tâm hôm nay là ngày bao nhiêu 
  

## Slide 30

03

Q&A

## Slide 31

---
