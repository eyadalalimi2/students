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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.databinding.ActivityProfileBinding;
import com.eyadalalimi.students.databinding.DialogChangePasswordBinding;
import com.eyadalalimi.students.model.College;
import com.eyadalalimi.students.model.Country;
import com.eyadalalimi.students.model.Major;
import com.eyadalalimi.students.model.University;
import com.eyadalalimi.students.model.User;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.CatalogRepository;
import com.eyadalalimi.students.repo.ProfileRepository;
import com.eyadalalimi.students.response.MessageResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private ProfileRepository profileRepo;
    private CatalogRepository catalogRepo;
    private int loadingOps = 0;
    @Nullable private User currentUser;
    private String[] levels;

    // مصادر القوائم
    private final List<Country> countries = new ArrayList<>();
    private final List<University> universities = new ArrayList<>();
    private final List<College> colleges = new ArrayList<>();
    private final List<Major> majors = new ArrayList<>();

    // المختار حالياً
    @Nullable private Long selectedCountryId = null;
    @Nullable private Long selectedUniversityId = null;
    @Nullable private Long selectedCollegeId = null;
    @Nullable private Long selectedMajorId = null;

    // محولات السبنرز
    private ArrayAdapter<String> countryAdapter;
    private ArrayAdapter<String> uniAdapter;
    private ArrayAdapter<String> collegeAdapter;
    private ArrayAdapter<String> majorAdapter;

    // Photo Picker
    private final ActivityResultLauncher<PickVisualMediaRequest> pickPhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri == null) return;
                setLoading(true);
                profileRepo.uploadPhoto(this, uri, new ApiCallback<User>() {
                    @Override public void onSuccess(User data) {
                        // حدّث الصورة فورًا مع كسر الكاش
                        loadProfilePhoto(data != null ? data.profile_photo_path : null);
                        // اجلب البروفايل لتحديث بقية الحقول فورًا
                        profileRepo.getProfile(new ApiCallback<User>() {
                            @Override public void onSuccess(User u) {
                                setLoading(false);
                                currentUser = u;
                                bindUser(u);
                            }
                            @Override public void onError(String msg) {
                                setLoading(false);
                                Toast.makeText(ProfileActivity.this, "تم تحديث الصورة", Toast.LENGTH_SHORT).show();
                            }
                        });
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
        catalogRepo  = new CatalogRepository(this);

        setupGenderSpinner();
        setupLevelSpinner();
        setupNameSpinners();
        setupClicks();

        loadData();
    }

    // ---------- تهيئة واجهة المستخدم ----------

    private void setupGenderSpinner() {
        String[] genders = new String[]{"male", "female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        binding.spGender.setAdapter(adapter);
    }
    private void setupLevelSpinner() {
        levels = getResources().getStringArray(R.array.levels_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                levels
        );
        binding.spLevel.setAdapter(adapter);
    }

    private void setupNameSpinners() {
        countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        uniAdapter     = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        collegeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        majorAdapter   = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());

        binding.spCountry.setAdapter(countryAdapter);
        binding.spUniversity.setAdapter(uniAdapter);
        binding.spCollege.setAdapter(collegeAdapter);
        binding.spMajor.setAdapter(majorAdapter);

        binding.spCountry.setOnItemSelectedListener(new SimpleItemSelectedListener(pos -> {
            if (pos < 0 || pos >= countries.size()) return;
            selectedCountryId = countries.get(pos).id;
            // إن توفّر Endpoint لجامعات حسب الدولة يمكن تطبيقه هنا
        }));

        binding.spUniversity.setOnItemSelectedListener(new SimpleItemSelectedListener(pos -> {
            if (pos < 0 || pos >= universities.size()) return;
            University u = universities.get(pos);
            selectedUniversityId = u.id;
            selectedCollegeId = null;
            selectedMajorId   = null;
            loadColleges(u.id, currentUser != null ? currentUser.college_id : null);
        }));

        binding.spCollege.setOnItemSelectedListener(new SimpleItemSelectedListener(pos -> {
            if (pos < 0 || pos >= colleges.size()) return;
            College c = colleges.get(pos);
            selectedCollegeId = c.id;
            selectedMajorId = null;
            loadMajors(c.id, currentUser != null ? currentUser.major_id : null);
        }));

        binding.spMajor.setOnItemSelectedListener(new SimpleItemSelectedListener(pos -> {
            if (pos < 0 || pos >= majors.size()) return;
            Major m = majors.get(pos);
            selectedMajorId = m.id;
        }));

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

    // ---------- البيانات ----------

    private void loadData() {
        setLoading(true); // Start: getProfile
        profileRepo.getProfile(new ApiCallback<User>() {
            @Override public void onSuccess(User data) {
                currentUser = data;
                bindUser(data);
                // حمّل القوائم بالأسماء مع اختيار مسبق
                loadCountries(data != null ? data.country_id : null);
                loadUniversities(data != null ? data.university_id : null);
                setLoading(false); // Finish: getProfile
            }
            @Override public void onError(String msg) {
                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                // حتى لو فشل /me، حمّل القوائم لتمكين التعديل
                loadCountries(null);
                loadUniversities(null);
                setLoading(false); // Finish: getProfile (with error)
            }
        });
    }

    private void bindUser(@Nullable User u) {
        if (u == null) return;

        binding.etStudentNumber.setText(u.student_number);
        binding.etName.setText(u.name);
        binding.etEmail.setText(u.email);
        binding.etPhone.setText(u.phone);
        if (u.level != null && u.level > 0 && levels != null && u.level <= levels.length) {
            binding.spLevel.setSelection(u.level - 1);
        } else {
            binding.spLevel.setSelection(0); // أو اتركها كما هي
        }

        if (!TextUtils.isEmpty(u.gender)) {
            binding.spGender.setSelection("female".equalsIgnoreCase(u.gender) ? 1 : 0);
        } else {
            binding.spGender.setSelection(0);
        }

        selectedCountryId    = u.country_id;
        selectedUniversityId = u.university_id;
        selectedCollegeId    = u.college_id;
        selectedMajorId      = u.major_id;

        loadProfilePhoto(u.profile_photo_path);
    }

    // ---------- تحميل القوائم ----------

    private void loadCountries(@Nullable Long preselectId) {
        setLoading(true); // Start: loadCountries
        binding.spCountry.setEnabled(false);
        countryAdapter.clear();
        countries.clear();

        catalogRepo.countries(new CatalogRepository.ApiCallback<List<Country>>() {
            @Override public void onSuccess(List<Country> list) {
                countries.addAll(list != null ? list : new ArrayList<>());
                for (Country c : countries) countryAdapter.add(c.name != null ? c.name : ("الدولة #" + c.id));
                countryAdapter.notifyDataSetChanged();
                binding.spCountry.setEnabled(true);

                if (preselectId != null) {
                    int idx = indexOfCountry(preselectId);
                    if (idx >= 0) binding.spCountry.setSelection(idx);
                }
                if (selectedCountryId == null && !countries.isEmpty()) {
                    int pos = Math.max(0, binding.spCountry.getSelectedItemPosition());
                    selectedCountryId = countries.get(pos).id;
                }
                setLoading(false); // Finish: loadCountries
            }
            @Override public void onError(String msg) {
                binding.spCountry.setEnabled(true);
                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                setLoading(false); // Finish: loadCountries (with error)
            }
        });
    }

    private void loadUniversities(@Nullable Long preselectId) {
        setLoading(true); // Start: loadUniversities
        binding.spUniversity.setEnabled(false);
        uniAdapter.clear();
        universities.clear();

        catalogRepo.universities(new CatalogRepository.ApiCallback<List<University>>() {
            @Override public void onSuccess(List<University> list) {
                universities.addAll(list != null ? list : new ArrayList<>());
                for (University u : universities) uniAdapter.add(u.name != null ? u.name : ("جامعة #" + u.id));
                uniAdapter.notifyDataSetChanged();
                binding.spUniversity.setEnabled(true);

                if (preselectId != null) {
                    int idx = indexOfUni(preselectId);
                    if (idx >= 0) binding.spUniversity.setSelection(idx);
                }
                if (selectedUniversityId == null && !universities.isEmpty()) {
                    int pos = Math.max(0, binding.spUniversity.getSelectedItemPosition());
                    selectedUniversityId = universities.get(pos).id;
                }
                if (selectedUniversityId != null) {
                    loadColleges(selectedUniversityId, selectedCollegeId);
                }
                setLoading(false); // Finish: loadUniversities
            }
            @Override public void onError(String msg) {
                binding.spUniversity.setEnabled(true);
                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                setLoading(false); // Finish: loadUniversities (with error)
            }
        });
    }

    private void loadColleges(long universityId, @Nullable Long preselectCollegeId) {
        setLoading(true); // Start: loadColleges
        binding.spCollege.setEnabled(false);
        collegeAdapter.clear();
        colleges.clear();

        catalogRepo.colleges(universityId, new CatalogRepository.ApiCallback<List<College>>() {
            @Override public void onSuccess(List<College> list) {
                colleges.addAll(list != null ? list : new ArrayList<>());
                for (College c : colleges) collegeAdapter.add(c.name != null ? c.name : ("كلية #" + c.id));
                collegeAdapter.notifyDataSetChanged();
                binding.spCollege.setEnabled(true);

                if (preselectCollegeId != null) {
                    int idx = indexOfCollege(preselectCollegeId);
                    if (idx >= 0) binding.spCollege.setSelection(idx);
                }
                if (selectedCollegeId == null && !colleges.isEmpty()) {
                    int pos = Math.max(0, binding.spCollege.getSelectedItemPosition());
                    selectedCollegeId = colleges.get(pos).id;
                }

                if (selectedCollegeId != null) {
                    loadMajors(selectedCollegeId, selectedMajorId);
                } else {
                    majors.clear();
                    majorAdapter.clear();
                    majorAdapter.notifyDataSetChanged();
                }
                setLoading(false); // Finish: loadColleges
            }
            @Override public void onError(String msg) {
                binding.spCollege.setEnabled(true);
                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                setLoading(false); // Finish: loadColleges (with error)
            }
        });
    }

    private void loadMajors(long collegeId, @Nullable Long preselectMajorId) {
        setLoading(true); // Start: loadMajors
        binding.spMajor.setEnabled(false);
        majorAdapter.clear();
        majors.clear();

        catalogRepo.majors(collegeId, new CatalogRepository.ApiCallback<List<Major>>() {
            @Override public void onSuccess(List<Major> list) {
                majors.addAll(list != null ? list : new ArrayList<>());
                for (Major m : majors) majorAdapter.add(m.name != null ? m.name : ("تخصص #" + m.id));
                majorAdapter.notifyDataSetChanged();
                binding.spMajor.setEnabled(true);

                if (preselectMajorId != null) {
                    int idx = indexOfMajor(preselectMajorId);
                    if (idx >= 0) binding.spMajor.setSelection(idx);
                }
                if (selectedMajorId == null && !majors.isEmpty()) {
                    int pos = Math.max(0, binding.spMajor.getSelectedItemPosition());
                    selectedMajorId = majors.get(pos).id;
                }
                setLoading(false); // Finish: loadMajors
            }
            @Override public void onError(String msg) {
                binding.spMajor.setEnabled(true);
                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                setLoading(false); // Finish: loadMajors (with error)
            }
        });
    }

    private int indexOfCountry(long id) { for (int i=0;i<countries.size();i++) if (countries.get(i).id==id) return i; return -1; }
    private int indexOfUni(long id)     { for (int i=0;i<universities.size();i++) if (universities.get(i).id==id) return i; return -1; }
    private int indexOfCollege(long id) { for (int i=0;i<colleges.size();i++) if (colleges.get(i).id==id) return i; return -1; }
    private int indexOfMajor(long id)   { for (int i=0;i<majors.size();i++) if (majors.get(i).id==id) return i; return -1; }

    // ---------- الحفظ ----------

    private void saveProfile() {
        Map<String, Object> body = new HashMap<>();

        putIfNotEmpty(body, "name",  binding.etName.getText().toString().trim());
        putIfNotEmpty(body, "email", binding.etEmail.getText().toString().trim());
        putIfNotEmpty(body, "phone", binding.etPhone.getText().toString().trim());

        if (selectedCountryId    != null) body.put("country_id",    selectedCountryId);
        if (selectedUniversityId != null) body.put("university_id", selectedUniversityId);
        if (selectedCollegeId    != null) body.put("college_id",    selectedCollegeId);
        if (selectedMajorId      != null) body.put("major_id",      selectedMajorId);

        int levelIdx = binding.spLevel.getSelectedItemPosition(); // 0-based
        if (levelIdx >= 0) {
            body.put("level", levelIdx + 1);
        }

        String gender = (String) binding.spGender.getSelectedItem();
        if (!TextUtils.isEmpty(gender)) body.put("gender", gender);

        if (body.isEmpty()) {
            Toast.makeText(this, "لا توجد تغييرات", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true); // Start: updateProfile
        profileRepo.updateProfile(body, new ApiCallback<User>() {
            @Override public void onSuccess(User data) {
                setLoading(false); // Finish: updateProfile
                // تحديث فوري داخل الصفحة
                loadData();
                Toast.makeText(ProfileActivity.this, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();
            }
            @Override public void onError(String msg) {
                setLoading(false); // Finish: updateProfile (with error)
                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------- تغيير كلمة السر ----------

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
            profileRepo.changePassword(cur, np, cp, new ApiCallback<MessageResponse>() {
                @Override public void onSuccess(MessageResponse data) {
                    setLoading(false);
                    Toast.makeText(ProfileActivity.this,
                            data != null && data.message != null ? data.message : "تم تغيير كلمة السر",
                            Toast.LENGTH_LONG).show();
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

    // ---------- أدوات مساعدة ----------

    private void setLoading(boolean show) {
        if (show) {
            loadingOps++;
        } else {
            if (loadingOps > 0) loadingOps--;
        }
        boolean display = loadingOps > 0;
        binding.progress.setVisibility(display ? View.VISIBLE : View.GONE);
        binding.container.setAlpha(display ? 0.5f : 1f);
        binding.container.setEnabled(!display);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingOps = 0;
        if (binding != null) {
            binding.progress.setVisibility(View.GONE);
            binding.container.setAlpha(1f);
            binding.container.setEnabled(true);
        }
    }

    private void putIfNotEmpty(Map<String, Object> map, String key, String val) {
        if (!TextUtils.isEmpty(val)) map.put(key, val);
    }

    private void putIfInt(String text, String key, Map<String, Object> map) {
        if (!TextUtils.isEmpty(text)) {
            try { map.put(key, Integer.parseInt(text)); } catch (Exception ignored) {}
        }
    }

    private void loadProfilePhoto(String path) {
        String abs = ApiClient.toAbsoluteFileUrl(path);
        if (abs == null) {
            binding.ivPhoto.setImageResource(R.mipmap.ic_launcher_round);
            return;
        }
        // كسر الكاش لضمان ظهور الصورة الجديدة مباشرة
        String url = abs + (abs.contains("?") ? "&" : "?") + "t=" + System.currentTimeMillis();

        String token = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);
        if (!TextUtils.isEmpty(token)) {
            GlideUrl glideUrl = new GlideUrl(url,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build());
            Glide.with(this)
                    .load(glideUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(binding.ivPhoto);
        } else {
            Glide.with(this)
                    .load(url)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(binding.ivPhoto);
        }
    }

    private String parseError(ResponseBody err) {
        try {
            String body = err != null ? err.string() : null;
            if (body == null || body.trim().isEmpty()) return "خطأ غير معروف";

            JsonElement el = JsonParser.parseString(body);
            if (el.isJsonObject()) {
                JsonObject o = el.getAsJsonObject();
                if (o.has("message") && !o.get("message").isJsonNull())
                    return o.get("message").getAsString();
                if (o.has("status") && !o.get("status").isJsonNull()) {
                    String s = o.get("status").getAsString();
                    if (!s.isEmpty()) return s;
                }
                if (o.has("errors") && o.get("errors").isJsonObject()) {
                    JsonObject errs = o.getAsJsonObject("errors");
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, JsonElement> e : errs.entrySet()) {
                        JsonElement v = e.getValue();
                        if (v.isJsonArray()) {
                            for (JsonElement m : v.getAsJsonArray()) {
                                if (sb.length() > 0) sb.append('\n');
                                sb.append(m.getAsString());
                            }
                        } else if (v.isJsonPrimitive()) {
                            if (sb.length() > 0) sb.append('\n');
                            sb.append(v.getAsString());
                        }
                    }
                    if (sb.length() > 0) return sb.toString();
                }
                if (o.has("error") && o.get("error").isJsonObject()) {
                    JsonObject e = o.getAsJsonObject("error");
                    if (e.has("message") && !e.get("message").isJsonNull())
                        return e.get("message").getAsString();
                }
            }
            String plain = body.replaceAll("<[^>]*>", "").trim();
            if (!plain.isEmpty()) return plain;
            return "خطأ غير معروف";
        } catch (Exception e) {
            return "تعذّر قراءة الخطأ";
        }
    }

    // مستمع مختصر للـ Spinners
    private static class SimpleItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        interface OnSelect { void onSelected(int position); }
        private final OnSelect cb;
        SimpleItemSelectedListener(OnSelect cb) { this.cb = cb; }
        @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
            if (cb != null) cb.onSelected(position);
        }
        @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
    }
}
