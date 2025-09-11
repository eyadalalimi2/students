package com.eyadalalimi.students.ui.activity.auth;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityVerifyEmailBinding;
import com.eyadalalimi.students.model.User;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AuthRepository;
import com.eyadalalimi.students.response.MessageResponse;
import com.eyadalalimi.students.ui.activity.home.HomeActivity;

public class VerifyEmailActivity extends BaseActivity {

    private ActivityVerifyEmailBinding binding;
    private AuthRepository authRepo;
    private String emailForResend;
    private CountDownTimer resendTimer;
    private static final long RESEND_COOLDOWN_MS = 60_000L; // 60 ثانية

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepo = new AuthRepository(this);
        setupToolbar(binding.toolbar, "تفعيل البريد الإلكتروني", true);

        setLoading(true);
        authRepo.me(new ApiCallback<User>() {
            @Override public void onSuccess(User me) {
                setLoading(false);
                if (me != null) {
                    emailForResend = me.email;
                    if (binding.tvEmail != null) binding.tvEmail.setText(me.email != null ? me.email : "");
                    if (isVerified(me)) {
                        routeAfterVerification(me);
                    }
                }
            }
            @Override public void onError(String message) {
                setLoading(false);
                toast("تعذر جلب الحساب: " + message);
            }
        });

        binding.btnOpenEmail.setOnClickListener(v -> openEmailApp());
        binding.btnIHaveVerified.setOnClickListener(v -> checkVerified());
        binding.btnResend.setOnClickListener(v -> resendLink());
    }

    private void openEmailApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:")));
            } catch (Exception ex) {
                toast("تعذر فتح تطبيق البريد");
            }
        }
    }

    private void checkVerified() {
        setLoading(true);
        authRepo.me(new ApiCallback<User>() {
            @Override public void onSuccess(User me) {
                setLoading(false);
                if (me != null && isVerified(me)) {
                    routeAfterVerification(me);
                } else {
                    toast("لم نرصد التفعيل بعد. افتح رابط التفعيل في بريدك ثم حاول مرة أخرى.");
                }
            }
            @Override public void onError(String message) {
                setLoading(false);
                toast(message);
            }
        });
    }

    private void resendLink() {
        if (emailForResend == null || emailForResend.isEmpty()) {
            toast("لا يمكن إعادة الإرسال بدون بريد");
            return;
        }
        setLoading(true);
        authRepo.resendEmail(emailForResend, new ApiCallback<MessageResponse>() {
            @Override public void onSuccess(MessageResponse data) {
                setLoading(false);
                toast("تم إرسال رابط التفعيل مجددًا");
                startResendCooldown();
            }
            @Override public void onError(String message) {
                setLoading(false);
                toast(message);
            }
        });
    }

    private void startResendCooldown() {
        binding.btnResend.setEnabled(false);
        if (resendTimer != null) resendTimer.cancel();
        resendTimer = new CountDownTimer(RESEND_COOLDOWN_MS, 1000) {
            @Override public void onTick(long ms) {
                long s = ms / 1000;
                binding.btnResend.setText("إعادة الإرسال (" + s + "ث)");
            }
            @Override public void onFinish() {
                binding.btnResend.setEnabled(true);
                binding.btnResend.setText("إعادة إرسال الرابط");
            }
        }.start();
    }

    private boolean isVerified(User u) {
        return u.email_verified_at != null && !u.email_verified_at.trim().isEmpty();
    }

    private boolean isActivated(User u) {
        return u.has_active_subscription != null && u.has_active_subscription;
    }

    private void routeAfterVerification(User me) {
        if (!isActivated(me)) {
            startActivity(new Intent(this, ActivationActivity.class));
        } else {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }

    private void setLoading(boolean b) {
        binding.progress.setVisibility(b ? View.VISIBLE : View.GONE);
        binding.btnResend.setEnabled(!b);
        binding.btnIHaveVerified.setEnabled(!b);
        binding.btnOpenEmail.setEnabled(!b);
    }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }

    @Override
    protected void onDestroy() {
        if (resendTimer != null) resendTimer.cancel();
        super.onDestroy();
    }
}
