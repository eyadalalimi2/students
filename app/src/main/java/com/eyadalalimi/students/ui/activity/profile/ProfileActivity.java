package com.eyadalalimi.students.ui.activity.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.databinding.ActivityProfileBinding;
import com.eyadalalimi.students.databinding.DialogChangePasswordBinding;
import com.eyadalalimi.students.model.User;
import com.eyadalalimi.students.repo.ProfileRepository;
import com.eyadalalimi.students.response.MessageResponse;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private ProfileRepository profileRepo;
    @Nullable private User currentUser;

    // Photo Picker
    private final ActivityResultLauncher<PickVisualMediaRequest> pickPhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri == null) return;
                setLoading(true);
                profileRepo.uploadPhoto(this, uri, new ProfileRepository.ApiCallback<User>() {
                    @Override public void onSuccess(User data) {
                        setLoading(false);
                        currentUser = data;
                        loadProfilePhoto(data != null ? data.profile_photo_path : null);
                        Toast.makeText(ProfileActivity.this, "تم تحديث الصورة", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onError(String msg) {
                        setLoading(false);
                        Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profileRepo = new ProfileRepository(this);

        setupGenderSpinner();
        setupClicks();

        loadData();
    }

    private void setupGenderSpinner() {
        String[] genders = new String[]{"male", "female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        binding.spGender.setAdapter(adapter);
    }

    private void setupClicks() {
        binding.btnChangePhoto.setOnClickListener(v ->
                pickPhotoLauncher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build())
        );

        binding.btnSave.setOnClickListener(v -> saveProfile());

        binding.btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void loadData() {
        setLoading(true);
        profileRepo.getProfile(new ProfileRepository.ApiCallback<User>() {
            @Override public void onSuccess(User data) {
                setLoading(false);
                currentUser = data;
                bindUser(data);
            }
            @Override public void onError(String msg) {
                setLoading(false);
                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindUser(@Nullable User u) {
        if (u == null) return;
        binding.etStudentNumber.setText(u.student_number);
        binding.etName.setText(u.name);
        binding.etEmail.setText(u.email);
        binding.etPhone.setText(u.phone);
        binding.etCountryId.setText(u.country_id != null ? String.valueOf(u.country_id) : "");
        binding.etUniversityId.setText(u.university_id != null ? String.valueOf(u.university_id) : "");
        binding.etCollegeId.setText(u.college_id != null ? String.valueOf(u.college_id) : "");
        binding.etMajorId.setText(u.major_id != null ? String.valueOf(u.major_id) : "");
        binding.etLevel.setText(u.level != null ? String.valueOf(u.level) : "");

        if (!TextUtils.isEmpty(u.gender)) {
            if ("female".equalsIgnoreCase(u.gender)) binding.spGender.setSelection(1);
            else binding.spGender.setSelection(0);
        } else {
            binding.spGender.setSelection(0);
        }

        loadProfilePhoto(u.profile_photo_path);
    }

    private void saveProfile() {
        Map<String, Object> body = new HashMap<>();

        putIfNotEmpty(body, "name", binding.etName.getText().toString().trim());
        putIfNotEmpty(body, "email", binding.etEmail.getText().toString().trim());
        putIfNotEmpty(body, "phone", binding.etPhone.getText().toString().trim());

        putIfLong(binding.etCountryId.getText().toString().trim(), "country_id", body);
        putIfLong(binding.etUniversityId.getText().toString().trim(), "university_id", body);
        putIfLong(binding.etCollegeId.getText().toString().trim(), "college_id", body);
        putIfLong(binding.etMajorId.getText().toString().trim(), "major_id", body);
        putIfInt(binding.etLevel.getText().toString().trim(), "level", body);

        String gender = (String) binding.spGender.getSelectedItem();
        if (!TextUtils.isEmpty(gender)) body.put("gender", gender);

        if (body.isEmpty()) {
            Toast.makeText(this, "لا توجد تغييرات", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        profileRepo.updateProfile(body, new ProfileRepository.ApiCallback<User>() {
            @Override public void onSuccess(User data) {
                setLoading(false);
                currentUser = data;
                bindUser(data);
                Toast.makeText(ProfileActivity.this, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();
            }
            @Override public void onError(String msg) {
                setLoading(false);
                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showChangePasswordDialog() {
        DialogChangePasswordBinding db = DialogChangePasswordBinding.inflate(LayoutInflater.from(this));
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setView(db.getRoot())
                .create();

        db.btnCancel.setOnClickListener(v -> dlg.dismiss());
        db.btnSave.setOnClickListener(v -> {
            String cur = db.etCurrent.getText().toString().trim();
            String np  = db.etNew.getText().toString().trim();
            String cp  = db.etConfirm.getText().toString().trim();

            if (cur.isEmpty() || np.isEmpty() || cp.isEmpty()) {
                Toast.makeText(this, "الرجاء تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }
            setLoading(true);
            profileRepo.changePassword(cur, np, cp, new ProfileRepository.ApiCallback<MessageResponse>() {
                @Override public void onSuccess(MessageResponse data) {
                    setLoading(false);
                    Toast.makeText(ProfileActivity.this, data != null && data.message != null ? data.message : "تم تغيير كلمة السر", Toast.LENGTH_LONG).show();
                    dlg.dismiss();
                }
                @Override public void onError(String msg) {
                    setLoading(false);
                    Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            });
        });

        dlg.show();
    }

    // -------- Helpers --------
    private void setLoading(boolean show) {
        binding.progress.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.container.setAlpha(show ? 0.5f : 1f);
        binding.container.setEnabled(!show);
    }

    private void putIfNotEmpty(Map<String, Object> map, String key, String val) {
        if (!TextUtils.isEmpty(val)) map.put(key, val);
    }

    private void putIfLong(String text, String key, Map<String, Object> map) {
        if (!TextUtils.isEmpty(text)) {
            try { map.put(key, Long.parseLong(text)); } catch (Exception ignored) {}
        }
    }

    private void putIfInt(String text, String key, Map<String, Object> map) {
        if (!TextUtils.isEmpty(text)) {
            try { map.put(key, Integer.parseInt(text)); } catch (Exception ignored) {}
        }
    }

    private String buildImageUrl(String path) {
        if (path == null || path.trim().isEmpty()) return null;
        if (path.startsWith("http")) return path;
        String base = ApiClient.getBaseUrl();
        String p = path.startsWith("/") ? path.substring(1) : path;
        return base + "storage/" + p;
    }

    private void loadProfilePhoto(String path) {
        String url = buildImageUrl(path);
        if (url == null) {
            binding.ivPhoto.setImageResource(R.mipmap.ic_launcher_round);
            return;
        }
        String token = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);
        if (!TextUtils.isEmpty(token)) {
            GlideUrl glideUrl = new GlideUrl(url,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build());
            Glide.with(this).load(glideUrl)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(binding.ivPhoto);
        } else {
            Glide.with(this).load(url)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(binding.ivPhoto);
        }
    }
}
