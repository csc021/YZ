package com.tf.sc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Constants;
import com.tf.sc.common.Result;
import com.tf.sc.dto.request.ParcelBatchPrintRequest;
import com.tf.sc.dto.request.ParcelBatchSelfPickupRequest;
import com.tf.sc.dto.request.ParcelInboundRequest;
import com.tf.sc.dto.request.ParcelOutboundRequest;
import com.tf.sc.dto.request.ParcelQueryRequest;
import com.tf.sc.dto.request.ParcelSelfPickupRequest;
import com.tf.sc.dto.response.ParcelPrintResponse;
import com.tf.sc.dto.response.RangeStatsResponse;
import com.tf.sc.dto.response.TodayStatsResponse;
import com.tf.sc.entity.Parcel;
import com.tf.sc.entity.User;
import com.tf.sc.service.ParcelService;
import com.tf.sc.service.SmsCodeService;
import com.tf.sc.service.UserService;
import com.tf.sc.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequireRole({"1", "2"})
@RestController
@RequestMapping("/parcel")
public class ParcelController {

    @Autowired
    private ParcelService parcelService;

    @Autowired
    private UserService userService;

    @Autowired
    private SmsCodeService smsCodeService;

    @GetMapping("/page")
    public Result<Page<Parcel>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                     @RequestParam(defaultValue = "10") Long pageSize) {
        return Result.success(parcelService.page(new Page<>(pageNum, pageSize)));
    }

    @GetMapping("/list")
    public Result<List<Parcel>> list() {
        return Result.success(parcelService.list());
    }

    @RequireRole({"0", "1", "2"})
    @GetMapping({"/{id}", "/{id}/detail"})
    public Result<Parcel> getById(@PathVariable Long id) {
        Parcel parcel = parcelService.getById(id);
        if (parcel == null) {
            return Result.error("包裹不存在");
        }
        if (isUserRole() && !canUserAccess(parcel)) {
            return Result.error(403, "Forbidden");
        }
        return Result.success(parcel);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Parcel parcel) {
        return Result.success(parcelService.save(parcel));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Parcel parcel) {
        return Result.success(parcelService.updateById(parcel));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(parcelService.removeById(id));
    }

    @PostMapping("/inbound")
    public Result<Parcel> inbound(@RequestBody ParcelInboundRequest request) {
        if (request.getOperatorId() == null) {
            request.setOperatorId(currentUserId());
        }
        return Result.success(parcelService.inbound(request));
    }

    @PostMapping("/outbound")
    public Result<Boolean> outbound(@RequestBody ParcelOutboundRequest request) {
        if (request.getOutboundBy() == null) {
            request.setOutboundBy(currentUserId());
        }
        boolean success = parcelService.outbound(request);
        return success ? Result.success(true) : Result.error("出库失败，用户尚未提交取件申请或包裹已处理");
    }

    @PostMapping("/outbound/by-code")
    public Result<Boolean> outboundByCode(@RequestParam String pickupCode) {
        boolean success = parcelService.outboundByCode(pickupCode, currentUserId());
        return success ? Result.success(true) : Result.error("出库失败，用户尚未提交取件申请或包裹已处理");
    }

    @PostMapping("/outbound/by-tracking")
    public Result<Boolean> outboundByTracking(@RequestParam String trackingNo) {
        boolean success = parcelService.outboundByTracking(trackingNo, currentUserId());
        return success ? Result.success(true) : Result.error("出库失败，用户尚未提交取件申请或包裹已处理");
    }

    @PostMapping("/outbound/by-phone")
    public Result<Boolean> outboundByPhone(@RequestParam String recipientPhone) {
        boolean success = parcelService.outboundByPhone(recipientPhone, currentUserId());
        return success ? Result.success(true) : Result.error("出库失败，用户尚未提交取件申请或包裹已处理");
    }

    @RequireRole({"0", "1", "2"})
    @PostMapping("/query")
    public Result<Page<Parcel>> query(@RequestBody ParcelQueryRequest request) {
        if (isUserRole()) {
            User user = currentUser();
            if (user == null) {
                return Result.error(401, "Unauthorized");
            }
            request.setRecipientPhone(user.getPhone());
        }
        return Result.success(parcelService.query(request));
    }

    @RequireRole({"0"})
    @PostMapping("/query/public")
    public Result<Page<Parcel>> publicQuery(@RequestBody ParcelQueryRequest request) {
        User user = currentUser();
        if (user == null) {
            return Result.error(401, "Unauthorized");
        }
        if (request.getRecipientPhone() == null) {
            request.setRecipientPhone(user.getPhone());
        }
        if (!user.getPhone().equals(request.getRecipientPhone())) {
            return Result.error(403, "Forbidden");
        }
        return Result.success(parcelService.query(request));
    }

    @RequireRole({"0", "1", "2"})
    @GetMapping("/recipient/{phone}")
    public Result<Page<Parcel>> recipientParcels(@PathVariable String phone,
                                                 @RequestParam(defaultValue = "1") Long pageNum,
                                                 @RequestParam(defaultValue = "10") Long pageSize) {
        if (isUserRole()) {
            User current = currentUser();
            if (current == null || !phone.equals(current.getPhone())) {
                return Result.error(403, "Forbidden");
            }
        }
        ParcelQueryRequest request = new ParcelQueryRequest();
        request.setRecipientPhone(phone);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        return Result.success(parcelService.query(request));
    }

    @RequireRole({"0"})
    @PostMapping("/{id}/request-pickup")
    public Result<Boolean> requestPickup(@PathVariable Long id,
                                         @RequestBody(required = false) ParcelSelfPickupRequest request) {
        if (request == null || request.getVerificationCode() == null
                || request.getVerificationCode().trim().isEmpty()) {
            return Result.error("请输入验证码");
        }
        User user = currentUser();
        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return Result.error(401, "Unauthorized");
        }
        if (!smsCodeService.verifySmsCode(user.getEmail(), 4, request.getVerificationCode().trim())) {
            return Result.error("验证码错误或已过期");
        }
        boolean success = parcelService.selfPickup(id, currentUserId());
        return success ? Result.success(true) : Result.error("取件申请提交失败，包裹可能已取件或收件人不匹配");
    }

    @RequireRole({"0"})
    @PostMapping("/{id}/self-pickup")
    public Result<Boolean> selfPickup(@PathVariable Long id, @RequestBody(required = false) ParcelSelfPickupRequest request) {
        if (request == null || request.getVerificationCode() == null || request.getVerificationCode().trim().isEmpty()) {
            return Result.error("Verification code is required");
        }
        User user = currentUser();
        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return Result.error(401, "Unauthorized");
        }
        if (!smsCodeService.verifySmsCode(user.getEmail(), 4, request.getVerificationCode().trim())) {
            return Result.error("验证码错误或已过期");
        }
        boolean success = parcelService.selfPickup(id, currentUserId());
        return success ? Result.success(true) : Result.error("取件申请提交失败，包裹可能已取件或收件人不匹配");
    }

    @RequireRole({"0"})
    @PostMapping("/self-pickup/by-tracking")
    public Result<Boolean> selfPickupByTracking(@RequestBody(required = false) ParcelSelfPickupRequest request) {
        if (request == null || request.getTrackingNo() == null || request.getTrackingNo().trim().isEmpty()) {
            return Result.error("Tracking number is required");
        }
        if (request.getVerificationCode() == null || request.getVerificationCode().trim().isEmpty()) {
            return Result.error("Verification code is required");
        }
        User user = currentUser();
        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return Result.error(401, "Unauthorized");
        }
        if (!smsCodeService.verifySmsCode(user.getEmail(), 4, request.getVerificationCode().trim())) {
            return Result.error("验证码错误或已过期");
        }
        boolean success = parcelService.selfPickupByTracking(request.getTrackingNo().trim(), currentUserId());
        return success ? Result.success(true) : Result.error("取件申请提交失败，包裹可能已取件或收件人不匹配");
    }

    @PostMapping("/approve-pickup/by-tracking")
    public Result<Boolean> approvePickupByTracking(@RequestParam String trackingNo) {
        Long operatorId = currentUserId();
        if (operatorId == null) {
            return Result.error(401, "Unauthorized");
        }
        boolean success = parcelService.approvePickupByTracking(trackingNo.trim(), operatorId);
        return success ? Result.success(true) : Result.error("批准失败，包裹尚未提交取件申请或已处理");
    }

    @RequireRole({"0"})
    @PostMapping("/batch/self-pickup")
    public Result<Integer> batchRequestPickup(@RequestBody(required = false) ParcelBatchSelfPickupRequest request) {
        if (request == null || request.getParcelIds() == null || request.getParcelIds().isEmpty()) {
            return Result.error("请选择包裹");
        }
        if (request.getVerificationCode() == null || request.getVerificationCode().trim().isEmpty()) {
            return Result.error("Verification code is required");
        }
        User user = currentUser();
        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return Result.error(401, "Unauthorized");
        }
        if (!smsCodeService.verifySmsCode(user.getEmail(), 4, request.getVerificationCode().trim())) {
            return Result.error("验证码错误或已过期");
        }
        int successCount = parcelService.batchRequestPickup(request.getParcelIds(), currentUserId());
        return successCount > 0
                ? Result.success(successCount)
                : Result.error("取件申请提交失败，包裹可能已取件或收件人不匹配");
    }

    @PostMapping("/batch/print")
    public Result<List<ParcelPrintResponse>> batchPrint(@RequestBody ParcelBatchPrintRequest request) {
        return Result.success(parcelService.batchPrint(request.getTrackingNos(), request.getParcelIds()));
    }

    @GetMapping("/check/tracking")
    public Result<Boolean> checkTrackingNo(@RequestParam String trackingNo) {
        return Result.success(parcelService.checkTrackingNoExists(trackingNo));
    }

    @GetMapping("/stats/today")
    public Result<TodayStatsResponse> todayStats(@RequestParam(required = false) Long stationId) {
        return Result.success(parcelService.todayStats(stationId));
    }

    @GetMapping("/stats/range")
    public Result<RangeStatsResponse> rangeStats(@RequestParam(required = false) Long stationId,
                                                 @RequestParam(required = false) String startTime,
                                                 @RequestParam(required = false) String endTime) {
        return Result.success(parcelService.rangeStats(stationId, startTime, endTime));
    }

    private boolean canUserAccess(Parcel parcel) {
        User user = currentUser();
        return user != null && parcel.getRecipientPhone() != null && parcel.getRecipientPhone().equals(user.getPhone());
    }

    private User currentUser() {
        Long userId = currentUserId();
        return userId == null ? null : userService.findById(userId);
    }

    private Long currentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        HttpServletRequest request = attributes.getRequest();
        Object value = request.getAttribute("userId");
        if (value instanceof Long) return (Long) value;
        String header = request.getHeader(Constants.AUTH_HEADER);
        if (header == null || !header.startsWith(Constants.JWT_PREFIX)) return null;
        try {
            return Long.valueOf(JwtUtil.parseSubject(header.substring(Constants.JWT_PREFIX.length())));
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private boolean isUserRole() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return false;
        Object value = attributes.getRequest().getAttribute("role");
        return Integer.valueOf(Constants.ROLE_USER).equals(value);
    }
}
